package com.efit.sport.notify;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tangtt on 15/8/14.
 */
public class ChaseNotifyEvent extends BaseNotifyEvent {

    public String chase_id; // message id(消息号)
    public String msg;

    public String fid;
    public String title;

    public int notice_time;//notice create time
    public String id; //record id
}
