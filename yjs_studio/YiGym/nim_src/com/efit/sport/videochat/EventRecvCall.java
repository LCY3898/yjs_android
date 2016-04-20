package com.efit.sport.videochat;

import com.netease.nimlib.sdk.avchat.model.AVChatData;

/**
 * Created by Administrator on 2015/10/15 0015.
 */
public class EventRecvCall {
    public AVChatData avChatData;

    public EventRecvCall(AVChatData avChatData) {
        this.avChatData = avChatData;
    }
}
