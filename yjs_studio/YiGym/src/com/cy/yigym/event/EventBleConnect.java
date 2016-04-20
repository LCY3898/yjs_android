package com.cy.yigym.event;

/**
 * Created by tangtt on 2015/7/25 0025.
 */
public class EventBleConnect {
    public EventBleConnect(boolean isConnect,boolean isRunning) {
        this.isConnect = isConnect;
        this.isWheelRunning = isRunning;
    }
    public boolean isConnect;
    public boolean isWheelRunning;
}
