package com.cy.yigym.net.req;

import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.utils.DataStorageUtils;

/**
 *   完成目标
 * Created by ejianshen on 15/7/25.
 * {"obj":"person","act":"achieveTarget","pid":"o14365168245796160697","tid":"o14367627217695050239",
 * "time":"20","distance":"4000","calorie":"5678","client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqAchieveTarget extends PersonReqBase {

    public String act="achieveTarget";
    public String time="";
    public String distance="";
    public String calorie="";
    public String tid = "";

    public ReqAchieveTarget(String taskId,String time,String distance,String calorie){
        this.tid = taskId;
        this.time=time;
        this.distance=distance;
        this.calorie=calorie;
    }
}
