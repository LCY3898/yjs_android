package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 获取任务的基本信息
 * Created by ejianshen on 15/8/21.
 */
public class ReqGetTaskBaseInfo extends TaskReqBase {
    private String act="getTaskBaseInfo";
    private String task_id="";

    public ReqGetTaskBaseInfo(String task_id){
        this.task_id=task_id;
    }

}
