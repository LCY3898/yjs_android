package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by ejianshen on 15/8/10.
 */
public class ReqGetChaseRecord extends ChaseReqBase {
    private String act="getChaseRecord";
    private String _id="";
    public ReqGetChaseRecord(String _id){
        this._id=_id;
    }
}
