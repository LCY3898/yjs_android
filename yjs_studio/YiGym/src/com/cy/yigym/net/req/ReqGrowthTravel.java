package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by xiaoshu on 15/11/19.
 */
public class ReqGrowthTravel extends ReqBase {
    public String obj = "pevent";
    public String act = "getPevent";

    public String medal_share_fid;
    public int medal_number;

    public ReqGrowthTravel() {
        this.pid = DataStorageUtils.getPid();
    }

    public ReqGrowthTravel(int medal_number,String medal_share_fid) {
        this.medal_number=medal_number;
        this.medal_share_fid=medal_share_fid;
        this.pid = DataStorageUtils.getPid();
    }
}
