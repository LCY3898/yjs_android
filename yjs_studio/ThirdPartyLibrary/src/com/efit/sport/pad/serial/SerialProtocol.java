package com.efit.sport.pad.serial;

import android.util.Log;

/**
 * Created by xiaoshu on 16/1/27.
 * <p>
 * 串口协议
 * by 台湾厂家
 */
public class SerialProtocol {

    public static class FrameData {
        public final static int TYPE_RPM = 1;
        public final static int TYPE_POWER = 2;
        public final static int TYPE_RESIST = 3;
        public final static int TYPE_NACK = 0;
        public final static int TYPE_UNKNOW = -1;


        public final static int INVALID_VALUE = -1;

        public int value = INVALID_VALUE;
        public int type = TYPE_NACK;
        public int frameSize;

        public static int getType(int requestCode) {
            if (requestCode == REQUEST_FOR_RPM) {
                return TYPE_RPM;
            } else if (requestCode == REQUEST_FOR_INSTANTANEOUS_POWER) {
                return TYPE_POWER;
            } else if (requestCode == REQUEST_FOR_RESISTENCE) {
                return TYPE_RESIST;
            }
            return TYPE_UNKNOW;
        }


        public boolean hasValidData() {
            return ((type != TYPE_NACK) && (type != TYPE_UNKNOW));
        }
    }

    /**
     * 串口数据位，8bit
     */
    public static int DATA_BITS = 8;
    /**
     * 停止位，1bit
     */
    public static int STOP_BITS = 1;
    /**
     * 波特率，19200bps
     */
    public static int BAUD_RATE = 19200;

    /**
     * 奇偶校验位
     */
    public static int PARITY_BIT;

    /**
     * 流量控制
     */
    public static int FLOW_CONTROL;

    /**
     * console发送命令给sensor board时的讯息开头
     */
    public static byte REQUEST_START_CODE = (byte) 0xF5;

    /**
     * sensor board回复信息给console时的讯息开头
     */
    public static byte RESPONSE_START_CODE = (byte) 0xF1;

    /**
     * 当sensor board接收到不合法命令的时候回复信息给console时的讯息开头
     */
    public static byte RSP_NAK_CODE = (byte) 0xF3;

    /**
     * console向sensor board请求转速的命令
     */
    public static byte REQUEST_FOR_RPM = 0x41;

    /**
     * console向sensor board请求瞬时功率的命令
     * <p>
     * instantaneous power
     */
    public static byte REQUEST_FOR_INSTANTANEOUS_POWER = 0x44;

    /**
     * console向sensor board请求阻力的命令
     */
    public static byte REQUEST_FOR_RESISTENCE = 0x49;

    /**
     * console和sensor board通信得数据帧尾
     */
    public static byte DATA_END_CODE = (byte) 0xF6;

    /**
     * sensor board 返回RPM数据帧的固定长度
     */
    public int RPM_DATA_LENGTH = 8;
    /**
     * sensor board 返回瞬时功率数据帧的固定长度
     */
    public int POWER_DATA_LENGTH = 10;
    /**
     * sensor board 返回阻力数据帧的固定长度
     */
    public int RESIST_DATA_LENGTH = 8;

    public static class ErrorCode {
        /**
         * 结尾字元异常
         */
        public static int END_CODE_ERROR = 0x01;

        /**
         * 开头字元异常
         */
        public static int START_CODE_ERROR = 0x02;
        /**
         * 数据位长度字元异常
         */
        public static int OVERRUN_CODE_ERROR = 0x03;
        /**
         * CRC校验码字元异常
         */
        public static int CRC_CODE_ERROR = 0x04;
        /**
         * 数据字元超出范围异常
         */
        public static int DATA_CODE_ERROR = 0x05;
        /**
         * 指令码无定义
         */
        public static int INSTRUCTION_CODE_ERROR = 0x06;
    }


    public static class CyclicGetData {
        public int index =0;

        public byte[] getDataCmd() {
            byte []reqData = requestResistCommand();
            if(index == 0) {
                reqData = requestRPMCommand();
            } else if(index == 1) {
                reqData = requestResistCommand();
            } else if(index == 2) {
                reqData = requestPowerCommand();
            } else if(index == 3) {
                reqData = requestResistCommand();
            }
            index++;
            if(index > 3) {
                index = 0;
            }
            return reqData;
        }
    }


    /**
     * 返回请求RPM的命令
     *
     * @return
     */
    public static byte[] requestRPMCommand() {
        byte checkSum = (byte) getRequestCheckSum(REQUEST_START_CODE, REQUEST_FOR_RPM);
        byte[] rpmCommand = {(byte) REQUEST_START_CODE, (byte) REQUEST_FOR_RPM
                , checkSum, (byte) DATA_END_CODE};
        return rpmCommand;
    }

    /**
     * 返回请求瞬时功率的命令
     *
     * @return
     */
    public static byte[] requestPowerCommand() {
        byte checkSum = (byte) getRequestCheckSum(REQUEST_START_CODE, REQUEST_FOR_INSTANTANEOUS_POWER);
        byte[] rpmCommand = {(byte) REQUEST_START_CODE, (byte) REQUEST_FOR_INSTANTANEOUS_POWER
                , checkSum, (byte) DATA_END_CODE};
        return rpmCommand;
    }

    /**
     * 返回请求阻力的命令
     *
     * @return
     */
    public static byte[] requestResistCommand() {
        byte checkSum = getRequestCheckSum(REQUEST_START_CODE, REQUEST_FOR_RESISTENCE);
        byte[] rpmCommand = {(byte) REQUEST_START_CODE, (byte) REQUEST_FOR_RESISTENCE
                , checkSum, (byte) DATA_END_CODE};
        return rpmCommand;
    }

    /**
     * 返回请求校验码
     *
     * @param startCode
     * @param requestCode
     * @return
     */
    private static byte getRequestCheckSum(int startCode, int requestCode) {
        return (byte) (startCode + requestCode);
    }

    /**
     * 返回响应校验码
     *
     * @param params
     * @return
     */
    public static byte getResponseCheckSum(byte... params) {
        int checkSum = 0;
        for (byte i : params) {
            checkSum += i;
        }
        return (byte) checkSum;
    }


    public static boolean hasOneFrame(byte[] buf, int bufLen) {
        if (bufLen < 3) {
            return false;
        }
        if (buf[0] == RSP_NAK_CODE) {
            return bufLen >= 5;
        } else if (buf[0] == RESPONSE_START_CODE) {
            int len = buf[2];
            return bufLen >= (len + 5);
        }
        return false;
    }

    public static boolean isValidHead(byte head) {
        return (head == RSP_NAK_CODE || head == RESPONSE_START_CODE);
    }

    public static FrameData parseFrame(byte[] input) {
        FrameData frameData = new FrameData();
        if (input[0] == RSP_NAK_CODE) {
            frameData.type = FrameData.TYPE_NACK;
            frameData.frameSize = 5;
            showNoAckInfo(input[1], input[2]);
        } else if (input[0] == RESPONSE_START_CODE) {
            frameData.type = FrameData.getType(input[1]);
            int dataLen = input[2];
            frameData.frameSize = 5 + dataLen;
            if (frameData.type != FrameData.TYPE_UNKNOW
                    && dataLen >= 0) {
                int index = 3 - 1 + dataLen;
                int value = 0;
                for (int i = dataLen - 1; i >= 0; i--) {
                    value = value * 10 + (input[index--] - '0');
                }
                frameData.value = value;
            }
        }
        return frameData;
    }

    private static void showNoAckInfo(byte reqCode, byte errCode) {
        String noAckInfo = "";
        if (reqCode == REQUEST_FOR_RPM) {
            noAckInfo = "rpm";
        } else if (reqCode == REQUEST_FOR_INSTANTANEOUS_POWER) {
            noAckInfo = "power";
        } else if (reqCode == REQUEST_FOR_RESISTENCE) {
            noAckInfo = "resistance";
        }
        Log.i("SerialProtocol", "no ack " + noAckInfo + " err code:" + errCode);
    }

}
