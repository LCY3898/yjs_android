package com.efit.sport.pad.serial;

import java.io.FileDescriptor;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * author: tangtt
 * <p>
 * 2015/12/21
 * </p>
 * <p>
 * <p/>
 * </p>
 */



public class SerialPort {

    /**奇偶校验*/
    private static final int NO_VERIFY = 1;
    private static final int EVENT_VERIFY = 2;
    private static final int ODD_VERIFY = 3;

    private static final String TAG = "SerialPort";
    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    public final static boolean BLOCK_MODE = true;


    public SerialPort(File device, int baudrate) throws SecurityException, IOException {
        mFd = open(device.getAbsolutePath(), baudrate,8,1,NO_VERIFY,false? 1:0);
        if (mFd == null) {
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    /**
     * @param path
     * @param baudrate
     * @param nBit
     *      数据位 7或 8
     * @param nStop
     *      停止位 1 或 2
     * @param verifyBit
     *      奇偶校验位 {@link #NO_VERIFY},EVENT_VERIFY,ODD_VERIFY
     * @param block
     *      是否为阻塞方式 1：阻塞， 0:非阻塞
     * @return
     */
    private native FileDescriptor open(String path, int baudrate,int nBit,int nStop,int verifyBit,int block);
    public native int close();
    public native int clearBuf();
    static {
        System.loadLibrary("serialport");
    }
}
