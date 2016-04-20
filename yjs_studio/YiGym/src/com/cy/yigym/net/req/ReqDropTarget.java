package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 放弃目标
 * Created by ejianshen on 15/7/25.
 * {"obj":"person","act":"dropTarget","pid":"o14365168245796160697","tid":"o14367706492604429721","time":"10",
 * "distance":"5000","calorie":"6670","client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqDropTarget extends PersonReqBase {

    public String act="dropTarget";
    public String time="";
    public String distance="";
    public String calorie="";

    public String tid = "";

    public ReqDropTarget(String taskId,String time,String distance,String calorie){
        this.tid = taskId;
        this.time=time;
        this.distance=distance;
        this.calorie=calorie;
    }
}
