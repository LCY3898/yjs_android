package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by ejianshen on 15/8/6.
 * {"obj":"person","act":"getNearOnline","pid":"o14374571158265709877","page_num":0,"sex":"男","maxDistance":0.5}
 */
public class ReqGetNearOnline extends PersonReqBase {
    private String act="getNearOnline";
    private String page_num="0";
    private String sex="";
    private String maxDistance="1";

    public ReqGetNearOnline(String sex,String maxDistance,String page_num){
        this.sex=sex;
        this.maxDistance=maxDistance;
        this.page_num=page_num;

    }
}
