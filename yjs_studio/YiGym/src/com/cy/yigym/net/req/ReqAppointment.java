package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 用户预约
 courseId        //预约课程id
 pid             //用户id
 isAppointment   //是否预约，1表示预约，0表示取消预约
 * Created by ejianshen on 15/10/19.
 * {"obj":"person","act":"appointment","courseId":"o14434164595177071094","pid":"o14449953485305719375","isAppointment":"1"}
 */
public class ReqAppointment extends PersonReqBase {
    private String act="appointment";
    private String courseId="";
    private int isAppointment=0;

    public ReqAppointment(String courseId, int isAppointment) {
        this.courseId = courseId;
        this.isAppointment = isAppointment;
        this.pid= DataStorageUtils.getPid();
    }
}
