package com.efit.sport.pad.serial;


import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * author: tangtt
 * <p>
 * 2015/12/21
 * </p>
 * <p>
 *   串口操作类
 * <p/>
 * </p>
 */
public class SerialPortUtil {
    private String TAG = SerialPortUtil.class.getSimpleName();
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private Thread mReadThread;
    private String path = "/dev/ttyS2";
    private int baudrate = 19200;
    private static SerialPortUtil portUtil;
    private OnDataReceiveListener onDataReceiveListener = null;
    private boolean isStop = false;
    private OnTonicDataListener tonicDataListener;


    public interface OnDataReceiveListener {
        void onDataReceive(Protocol.BikeRunStatus status);
    }

    public interface OnTonicDataListener {
        void onDataReceived(SerialProtocol.FrameData frameData);
    }


    public void setOnTonicDataListener(
            OnTonicDataListener dataReceiveListener) {
        tonicDataListener = dataReceiveListener;
    }

    public void setOnDataReceiveListener(
            OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }


    public static void init() {
        getInstance();
    }


    public static SerialPortUtil getInstance() {
        if(portUtil != null) {
            return portUtil;
        }

        synchronized (SerialPortUtil.class) {
            if (null == portUtil) {
                portUtil = new SerialPortUtil();
            }
        }
        return portUtil;
    }

    private SerialPortUtil() {
        onCreate();
    }

    /**
     * 初始化串口信息
     */
    public void onCreate() {
        try {
            mSerialPort = new SerialPort(new File(path), baudrate);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            mReadThread = new ReadTonicThread();
            isStop = false;
            mReadThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送指令到串口
     *
     * @param cmd
     * @return
     */
    public boolean sendCmds(String cmd) {
        boolean result = true;
        byte[] mBuffer = (cmd).getBytes();

        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public boolean sendBuffer(byte[] buf) {
        if(buf == null) {
            return false;
        }
        boolean result = true;
        try {
            if (mOutputStream != null ) {
                mOutputStream.write(buf);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }


    private static SerialProtocol.FrameData readTonicData(InputStream inputStream,Protocol.SerialData serialData)
            throws IOException {

        byte[]buffer = serialData.data;
        int bufLen = serialData.offset;

        int size = inputStream.read(buffer, bufLen, 10);
        bufLen += size;
        if (bufLen > 1) {
            //移动到正常的头部
            if (!SerialProtocol.isValidHead(buffer[0])) {
                for (int i = 0; i < bufLen; i++) {
                    if (SerialProtocol.isValidHead(buffer[i])) {
                        System.arraycopy(buffer, i, buffer, 0, bufLen - i);
                        bufLen = bufLen - i;
                        break;
                    }
                }
            }
        }

        if(SerialProtocol.hasOneFrame(buffer,bufLen)) {
            //Log.i("serialport", "serial buffer:" + Arrays.toString(buffer));
            SerialProtocol.FrameData frameData = SerialProtocol.parseFrame(buffer);
            if (frameData != null) {
                int frameSize = frameData.frameSize;
                System.arraycopy(buffer, frameSize, buffer, 0, bufLen - frameSize);
                bufLen = bufLen - frameSize;
                serialData.offset = bufLen;
                if(frameData.hasValidData()) {
                    return frameData;
                }
            }
        }
        serialData.offset = bufLen;
        return null;
    }



    /**
     * 读取台湾厂家TONIC FITNESS TECHNOLOGY的串口
     */
    private class ReadTonicThread extends Thread {

        public static final int READ_INTERVAL = 240;

        @Override
        public void run() {
            byte[] buffer = new byte[512];
            int bufLen = 0;
            Protocol.SerialData serialBuf = new Protocol.SerialData();
            serialBuf.data = buffer;
            serialBuf.offset = bufLen;

            long lastSendTime = 0;
            SerialProtocol.CyclicGetData getDataCmd = new SerialProtocol.CyclicGetData();

            while (!isStop && !isInterrupted()) {
                try {
                    if(mInputStream.available() > 0) {
                        SerialProtocol.FrameData frameData = readTonicData(mInputStream,serialBuf);
                        if(frameData != null && tonicDataListener != null) {
                            tonicDataListener.onDataReceived(frameData);
                        }
                    }

                    long now = System.currentTimeMillis();
                    if(now - lastSendTime > READ_INTERVAL) {
                        lastSendTime = now;
                        sendBuffer(getDataCmd.getDataCmd());
                    }
                    try {
                        Thread.sleep(30);
                    }catch (InterruptedException e) {}
                }catch (Exception e) {

                }
            }
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[512];
            int bufLen = 0;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!isStop && !isInterrupted()) {
                int size = 0;
                try {
                    if (mInputStream == null)
                        return;

                    size = mInputStream.read(buffer,bufLen,10);
                    bufLen += size;
                    if(bufLen > 2) {
                        int validPos = 0;
                        for(int i=0; i< bufLen - 1;i++) {
                            if(((buffer[i] & 0xff) == 0xb4) && ((buffer[i+1] & 0xff) == 0x4b)) {
                                validPos = i;
                                break;
                            }
                        }
                        if(validPos != 0) {
                            System.arraycopy(buffer,validPos,buffer,0,bufLen - validPos);
                            bufLen = bufLen - validPos;
                        }
                    }

                    Protocol.FrameOutPut outPut = null;
                    if(bufLen > 2) {
                        outPut = Protocol.parseSerialData(buffer, bufLen);
                    }
                    if(outPut != null) {
                        System.arraycopy(buffer,outPut.totalLen,buffer,0,bufLen - outPut.totalLen);
                        bufLen = bufLen - outPut.totalLen;
                        size = outPut.realData.length;
                        Log.i("SerialPort", "data size:" + size);
                        String hexStr = "";
                        for(int i=0;i<size;i++) {
                            hexStr += ("0x" + Integer.toHexString(outPut.realData[i] & 0xff)) + " ";
                        }
                        Log.i("SerialOutput", "original hex string:" + hexStr);
                    }


                    if (outPut != null) {
                        Protocol.SerialRecvData recvData = Protocol.parseFrame(outPut.realData);
                        if(recvData == null) {
                            Log.i("SerialPortUtil","unknow recvData");
                            return;
                        }
                        if(recvData.ack != null ) {
                            //sendBuffer(recvData.ack);
                        }

                        //有效的数据

                        if (null != onDataReceiveListener && recvData.status != null) {
                            onDataReceiveListener.onDataReceive(recvData.status);
                        }
                    }
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.clearBuf();
            mSerialPort.close();
        }
    }


    public static void execSuCmd() {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes("chmod 0777 /dev/ttyS2" + "\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
