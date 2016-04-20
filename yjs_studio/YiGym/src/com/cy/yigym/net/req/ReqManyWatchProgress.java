package com.cy.yigym.net.req;


import com.cy.yigym.utils.DataStorageUtils;

/**
 {"obj":"person","act":"getManyWatchProgress","pid":"o14492128781147511006"}
 */
public class ReqManyWatchProgress extends PersonReqBase {
    public String act = "getManyWatchProgress";



    public ReqManyWatchProgress() {
        this.pid = DataStorageUtils.getPid();
        this.act = "getManyWatchProgress";
    }


}
