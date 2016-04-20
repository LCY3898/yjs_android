package com.cy.yigym.ble;

import android.util.Log;

import com.cy.yigym.event.EventBleConnect;
import com.efit.sport.pad.serial.Protocol;
import com.efit.sport.pad.serial.SerialPortUtil;
import com.efit.sport.pad.serial.SerialProtocol;

import de.greenrobot.event.EventBus;

/**
 * author: tangtt
 * <p>
 * create at 2016/1/17
 * </p>
 * <p>
 * 串口数据收集
 * </p>
 */
public class SerialConnect {

    private static SerialConnect sInstance;

    public static SerialConnect instance() {
        if (sInstance == null) {
            synchronized (SerialConnect.class) {
                if (sInstance == null)
                    sInstance = new SerialConnect();
            }
        }
        return sInstance;
    }

    public static void release() {
        if (sInstance != null) {
            sInstance.fini();
            sInstance = null;
        }
        SerialPortUtil.getInstance().closeSerialPort();
    }

    private SerialConnect() {
        init();
    }


    private boolean isRiding = false;

    private void init() {
        SerialPortUtil.getInstance().setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {
            @Override
            public void onDataReceive(Protocol.BikeRunStatus runStatus) {
                resist = runStatus.resist;
                calcDistance(runStatus.oneCycleInMills);
            }
        });

        SerialPortUtil.getInstance().setOnTonicDataListener(new SerialPortUtil.OnTonicDataListener() {
            @Override
            public void onDataReceived(SerialProtocol.FrameData frameData) {
                //Log.i("SerialConnect","serial frameData type:" + frameData.type + " value:" + frameData.value );
                if(frameData.type == SerialProtocol.FrameData.TYPE_POWER) {
                    power  = frameData.value / 10;
                } else if(frameData.type == SerialProtocol.FrameData.TYPE_RESIST) {
                    resist = frameData.value;
                } else if(frameData.type == SerialProtocol.FrameData.TYPE_RPM) {
                    tonicCalcDistance(frameData.value);
                }
            }
        });

    }


    public void startRide() {
        distance = 0;
        calorie = 0;
        lastReportTime = 0;
        isRiding = true;
    }

    public void pause() {
        isRiding = false;
    }

    public void stopRide() {
        isRiding = false;
    }

    public void resume() {
        isRiding = true;
    }

    public void fini() {

    }


    public boolean isRiding() {
        return isRiding;
    }

    private boolean isConnected = true;

    public boolean isConnected() {
        return isConnected;
    }

    private long lastReportTime = 0;
    private double distance = 0;
    private double calorie = 0;
    private double roundPerMin; //rpm 每分钟转数
    private double speedPerHour = 0;
    private int resist = 0;
    private int power = 0;

    private double radius = 0.18;//单位(米) 原为0.2


    private double getPerimeter() {
        return radius * Math.PI * 2;
    }


    private void tonicCalcDistance(int rpm) {
        long now = System.currentTimeMillis();
        if (rpm == 0) { //如果时间为0，表示没有转速，因此要重置reportTime
            lastReportTime = now;
            speedPerHour = 0;
            roundPerMin = 0;
            isRiding = false;
            return;
        } else {
            isRiding = true;
        }

        long timeInMills = 14450 / rpm;

        if (lastReportTime == 0) {
            lastReportTime = now;
            return;
        }

        long millsDiff = now - lastReportTime;//millsDiff即为deltaT，两次上报数据的时间间隔

        distance += millsDiff * getPerimeter() / timeInMills;

        //计算卡路里
        double p;
        if (resist < 5) {
            p = millsDiff * 290.0 / timeInMills / 100;
        } else {
            p = 0.7 * millsDiff * (315.0 + 16 * resist) / timeInMills / 100;
        }
        //累加每一小段的卡路里
        calorie += p * 0.00717;

        speedPerHour = 1000 * getPerimeter() * 3.6 / timeInMills;
        roundPerMin = rpm / 4.12;//一分钟转数  //4.12 round per one pedal cycle
        lastReportTime = now;
    }

    /**
     * 距离，卡路里的计算
     * 阻力 < 5时，P = 290 / T （T为转一圈所花的时间)
     * 阻力 > 5时，P = (315 + 16* resist) / T （T为转一圈所花的时间)
     * w = p1*deltaT1 + p2*deltaT2 + ...+ pn*deltaTn
     * cal = w * 0.00717
     *
     * @param timeInMills 转动一圈所发的时间（毫秒）
     */
    private void calcDistance(int timeInMills) {
        long now = System.currentTimeMillis();
        if (timeInMills == 0) { //如果时间为0，表示没有转速，因此要重置reportTime
            lastReportTime = now;
            speedPerHour = 0;
            roundPerMin = 0;
            isRiding = false;
            return;
        } else {
            isRiding = true;
        }

        if (lastReportTime == 0) {
            lastReportTime = now;
            return;
        }

        long millsDiff = now - lastReportTime;//millsDiff即为deltaT，两次上报数据的时间间隔

        distance += millsDiff * getPerimeter() / timeInMills;

        //计算卡路里
        double p;
        if (resist < 5) {
            p = millsDiff * 290.0 / timeInMills / 100;
        } else {
            p = 0.7 * millsDiff * (315.0 + 16 * resist) / timeInMills / 100;
        }
        //累加每一小段的卡路里
        calorie += p * 0.00717;

        speedPerHour = 1000 * getPerimeter() * 3.6 / timeInMills;
        roundPerMin = 14550 / timeInMills;//一分钟转数  //4.12 round per one pedal cycle
        lastReportTime = now;
    }


    public double getDistance() {
        return distance;
    }

    /**
     * @return speedrate in rpm
     */
    public double getRoundPerMin() {
        return roundPerMin;
    }


    /**
     * @return speedrate in km/h
     */
    public double getSpeedPerHour() {
        return speedPerHour;
    }


    /**
     * @return resistance in percent. eg: 64%
     */
    public int getResist() {
        return resist;
    }


    public double getCalorie() {
        return calorie;
    }

}
