package com.efit.sport.pad.serial;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * author: tangtt
 * <p>
 * 2015/12/21
 * </p>
 * <p>
 *   串口协议
 * <p/>
 * </p>
 */
public class Protocol {
    public static byte[]HEAD = {(byte)0xb4,0x4b};
    public static byte[]TAIL = {(byte)0xf0,(byte)0xf0};

    public static byte[]RESERVED = {0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1};

    public final static int CHECKSUM_LEN = 1;

    /**
     * app向硬件发送握手信号
     */
    public static byte CMD_HANDSHAKE = 5;

    /**
     * 硬件向app回复握手信号
     */
    public static byte RSP_HANDSHAKE = 10;

    /**
     *硬件向app发送握手信号
     */
    public static byte SENSOR_TO_APP_HANDSHAKE = 15;

    /**
     * app回复握手信号
     */
    public static byte HANDSHAKE_ACK = 20;


    /**
     * 硬件上报行车数据
     */
    public static byte REPORT_DATA = 25;
    /**
     * app回应行车数据上报
     */
    public static byte ACK_REPORT = 30;

    public static class SerialRecvData {
        public BikeRunStatus status;
        public byte[]ack; //握手信号
        public int cmd;
        public int errCode;
    }


    public static class BikeRunStatus {
        public int oneCycleInMills; // 车轮转动一圈的时间
        public int resist;            //阻力，范围0～0x3ff
        public int powerConsume;     //功率
    }

    public static class SensorErrCode {
        private static final int ERR_NONE = 0;
    }


    public static byte[] getHandshake() {
        return getFrame(CMD_HANDSHAKE,null);
    }

    /**
     * app 回复握手信号
     * @return
     */
    public static byte[] getHandshakeAck() {
        return getFrame(HANDSHAKE_ACK,null);
    }


    public static byte[] getAckFrame(byte []recvData) {
        return getFrame(ACK_REPORT,recvData);
    }

    public static byte[] getFrame(byte cmd,byte[] data) {
        ByteBuffer buf = ByteBuffer.allocate(100);//big enough

        buf.put(HEAD);
        byte dataLen;
        if(data == null || data.length == 0) {
            dataLen = (byte) (1+RESERVED.length);
        } else {
            dataLen = (byte) (1 + data.length + RESERVED.length);
        }
        buf.put(dataLen);
        buf.put(cmd);

        if(data != null && data.length > 0) {
            buf.put(data);
        }
        buf.put(RESERVED);
        buf.put(calcChecksum(dataLen,cmd,data,RESERVED));
        buf.put(TAIL);
        int pos = buf.position();
        byte []frame = new byte[pos];
        buf.rewind();
        buf.get(frame);
        return frame;
    }


    private static byte[] calcChecksum(byte dataLen,byte cmd,byte[]data,byte[]reserved) {
        byte []checksum = new byte[CHECKSUM_LEN];
        checksum[0] = (byte) (dataLen^cmd);
        for(int i=0;i<data.length;i++) {
            checksum[0] = (byte) (checksum[0] ^ data[i]);
        }
        for(int i=0;i<RESERVED.length;i++) {
            checksum[0] = (byte) (checksum[0] ^ reserved[i]);
        }
        return checksum;
    }


    private static byte[] calcChecksum(byte dataLen,byte[]data) {
        byte []checksum = new byte[CHECKSUM_LEN];
        checksum[0] = (byte) (dataLen ^ ((~dataLen) & 0xff));
        for(int i=0;i<data.length;i++) {
            checksum[0] = (byte) (checksum[0] ^ data[i]);
        }
        return checksum;
    }


    public static SerialRecvData parseFrame(byte[] data) {
        if(data == null || data.length == 0) {
            return null;
        }
        SerialRecvData recvData = new SerialRecvData();
        recvData.cmd = data[0];
        if(data[0] == RSP_HANDSHAKE) {
            return recvData;
        } else if(data[0] == SENSOR_TO_APP_HANDSHAKE) {
            recvData.ack = getHandshakeAck();
            return recvData;
        } else if(data[0] == REPORT_DATA) {
            byte errCode = data[1];
            if(errCode == SensorErrCode.ERR_NONE) {
                byte[]realData = new byte[data.length - 2];
                System.arraycopy(data, 2, realData, 0, realData.length);
                recvData.status = paresReportFrame(realData);
                recvData.ack = getAckFrame(realData);
                return recvData;
            } else {
                recvData.errCode = errCode;
                return recvData;
            }
        }
        return null;
    }


    public static BikeRunStatus paresReportFrame(byte[] data) {
        BikeRunStatus status = new BikeRunStatus();
        status.oneCycleInMills = (((data[0] & 0xff) << 8) + (data[1] & 0xff)) * 10;
        status.resist =100*(((data[2] & 0xff) << 8) + (data[3] & 0xff)) /0x3ff;
        status.powerConsume = (((data[4] & 0xff) << 8) + (data[5] & 0xff));

        return status;
    }


    public static class SerialData {
        public byte[] data;
        public int offset;
    }

    public static boolean readByte(SerialData serialData, byte[] out) {
        int len = out.length;
        int offset = serialData.offset;
        if(offset + len > serialData.data.length) {
            return false;
        }
        for(int i=offset; i < offset + len; i++) {
            out[i - offset] = serialData.data[i];
        }
        serialData.offset += len;
        return true;
    }


    public static class FrameOutPut {
        public byte []realData;
        public int totalLen;
    }
    public static FrameOutPut parseSerialData(byte[]rawDatas, int len) {
        try {
            SerialData serialData = new SerialData();
            byte[]raw = new byte[len];
            System.arraycopy(rawDatas,0,raw,0,len);
            serialData.data = raw;
            serialData.offset = 0;
            // First, read header
            byte []head = new byte[HEAD.length];
            if(!readByte(serialData, head)) {
                return null;
            }

            if(!bytesEqual(head, HEAD)) {
                return null;
            }


            byte []lens = new byte[2];
            if(!readByte(serialData, lens)) {
                return null;
            }

            int dataLen = lens[0] & 0xff;
            int lenComplement = lens[1] & 0xff;

            if(dataLen + lenComplement != 0xff) {
                return null;
            }



            byte []data = new byte[dataLen];

            if(!readByte(serialData, data)) {
                return null;
            }


            byte[] checksum = new byte[CHECKSUM_LEN];
            if(!readByte(serialData, checksum)) {
                return null;
            }

            byte []calcChecksum = calcChecksum((byte)dataLen,data);
            if(!bytesEqual(checksum,calcChecksum)) {
                return null;
            }

            byte[] tail = new byte[TAIL.length];
            if(!readByte(serialData, tail)) {
                return null;
            }
            FrameOutPut frameOutPut = new FrameOutPut();
            frameOutPut.realData = data;
            frameOutPut.totalLen = data.length + HEAD.length + 2 + CHECKSUM_LEN + TAIL.length;
            return frameOutPut;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] readFrame(InputStream is) {
        try {
            // First, read header
            byte []head = new byte[HEAD.length];
            Log.i("Protocol", "head " + Arrays.toString(head));
            if(!read(is, head)) {
                return null;
            }

            if(!bytesEqual(head, HEAD)) {
               return null;
            }

            int len = is.read();
            if(len < 0) {
                return null;
            }

            //长度的反码
            int lenComplement = is.read();
            if(len + lenComplement != 0xff) {
                return null;
            }

            byte []data = new byte[len];
            if(!read(is,data)) {
                return null;
            }

            byte[] checksum = new byte[CHECKSUM_LEN];
            if(!read(is,checksum)) {
                return null;
            }

            byte []calcChecksum = calcChecksum((byte)len,data);
            if(!bytesEqual(checksum,calcChecksum)) {
                return null;
            }

            byte[] tail = new byte[TAIL.length];
            if(!read(is,tail)) {
                return null;
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param is
     * @param out
     * @return
     * @throws IOException
     */
    private static boolean read(InputStream is,byte[] out) throws IOException {
        int offset = 0;
        int remaining = out.length;
        do {
            //防止粘包和分包
            int countRead = is.read(out, offset, remaining);
            if (countRead < 0) {
                return false;
            }
            offset += countRead;
            remaining -= countRead;
        } while (remaining > 0);
        return true;
    }

    public static boolean bytesEqual(byte []lhs,byte[]rhs) {
        if(lhs == null && rhs == null) {
            return true;
        }
        if(lhs == null || rhs == null) {
            return false;
        }
        if(lhs.length != rhs.length) {
            return false;
        }
        for(int i=0;i<lhs.length;i++) {
            if(lhs[i] != rhs[i]) {
                return false;
            }
        }
        return true;
    }
}
