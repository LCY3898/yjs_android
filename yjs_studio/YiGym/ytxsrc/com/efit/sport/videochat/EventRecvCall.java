package com.efit.sport.videochat;

import android.content.Intent;

import com.yuntongxun.ecsdk.ECDevice;

/**
 * Created by Administrator on 2015/10/15 0015.
 */
public class EventRecvCall {

    public String mCallId;
    public String mCallNumber;
    public String []infos;
    public String nickname = "无名";
    public String fid = "";

    public EventRecvCall(Intent intent) {
        mCallId = intent.getStringExtra(ECDevice.CALLID);
        mCallNumber = intent.getStringExtra(ECDevice.CALLER);
        infos = intent.getExtras().getStringArray(ECDevice.REMOTE);
        if(infos != null && infos.length > 0) {
            for(int i=0;i<infos.length;i++) {
                if(infos[i].startsWith("nickname")) {
                    String[]nickFields = infos[i].split("=");
                    if(nickFields != null && nickFields.length > 1) {
                        nickname = nickFields[1];
                    }
                } else if(infos[i].startsWith("tel")) {
                    String[]fidFields = infos[i].split("=");
                    if(fidFields != null && fidFields.length > 1) {
                        fid = fidFields[1];
                    }
                }
            }
        }
    }



}
