package com.cy.yigym.event;


/**
 * tangtt
 * 网络发生变化事件
 */
public class EventNetStateChange {
    public boolean isConnected;
    public EventNetStateChange(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
