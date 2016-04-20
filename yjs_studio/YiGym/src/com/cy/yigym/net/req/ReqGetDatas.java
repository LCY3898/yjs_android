package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 *  从服务端拉取对方数据
 * Created by ejianshen on 15/7/28.
 * {"obj":"chaseRecord","act":"get","_id":"o14376346652598230838","pid":"o14370561365609710216",
 * "client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqGetDatas extends ChaseReqBase {
    private String act="get";
    private String _id= "";
    //对方的pid
    public ReqGetDatas(String _id){
        this._id=_id;
    }

}
