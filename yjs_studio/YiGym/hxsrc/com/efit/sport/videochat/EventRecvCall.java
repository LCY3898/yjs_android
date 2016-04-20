package com.efit.sport.videochat;

/**
 * Created by Administrator on 2015/10/15 0015.
 */
public class EventRecvCall {
    public String mCallNumber;
    public String nickname = "无名";
    public String fid = "";

    public EventRecvCall(String from) {
        mCallNumber = from;
    }
}
