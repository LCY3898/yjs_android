package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * tangtt
 * <p>
 * 2015-8-27
 * </p>
 * <p>
 * {"obj":"inbox","act":"load","pid":"o14374571158265709877",
 * "time":1440559717,"load_size":"3","client_info":clientType":"webapp","userId":null},"debug":1}
 * </p>
 */
public class ReqLoadMsg extends InboxReqBase {
    public String act = "load";
    public int time;
    public int load_size = 20;

    public ReqLoadMsg(int timeInSecs, int loadSize) {
        this.pid=DataStorageUtils.getPid();
        this.time = timeInSecs;
        this.load_size = loadSize;
    }

    public ReqLoadMsg(int timeInSecs) {
        this.time = timeInSecs;
        this.pid=DataStorageUtils.getPid();
    }
}
