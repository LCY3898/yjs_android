package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by xiaoshu on 15/12/10.
 */
public class ReqExitLive extends ReqBase {
    private String obj = "person";
    private String act = "exitLive";
    private String course_id = "";

    public ReqExitLive(String course_id) {
        this.pid = DataStorageUtils.getPid();
        this.course_id = course_id;
    }
}
