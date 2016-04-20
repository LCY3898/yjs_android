package com.cy.yigym.entity;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.yigym.net.rsp.RspLoadMsg;
import com.efit.sport.notify.ChaseNotifyEvent;

/**
 * Created by eijianshen on 15/8/6.
 */
public class EtyListItem {
    public String headerFid;
    public String sysTime;
    public String userName;
    public String content;
    public String joinNum = "";

    public RspLoadMsg.Messages.MessageListEntity event;
}
