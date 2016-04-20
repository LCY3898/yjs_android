package com.cy.yigym.entity;

import com.cy.yigym.net.rsp.RspLoadMsg;

/**
 * Created by eijianshen on 15/8/6.
 */
public class NotifyListItem {
    public String headerFid;
    public String sysTime;
    public String userName;
    public String content;
    public boolean isSelected = false;

    public RspLoadMsg.Messages.MessageListEntity event;
}
