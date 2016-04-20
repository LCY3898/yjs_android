package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by xiaoshu on 15/11/11.
 */
public class ReqChiefCoach extends ChiefCoachReqBase {
    public String act="dayLead";
    public ReqChiefCoach(){
        this.pid= DataStorageUtils.getPid();
    }
}
