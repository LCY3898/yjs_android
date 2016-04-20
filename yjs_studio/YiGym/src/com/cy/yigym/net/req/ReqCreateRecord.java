package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 创建追她纪录
 * Created by ejianshen on 15/7/28.
 * {"obj":"chaseRecord","act":"createRecord","sender_id":"o14370561365609710216","receiver_id":"o14374636412452878952",
 * "client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqCreateRecord extends ChaseReqBase {
    private String act="createRecord";
    private String sender_id= DataStorageUtils.getPid();
    private String receiver_id="";
    private int apart_distance=0;

    public ReqCreateRecord(String receiver_id,int apart_distance){
        this.receiver_id=receiver_id;
        this.apart_distance=apart_distance;
    }

}
