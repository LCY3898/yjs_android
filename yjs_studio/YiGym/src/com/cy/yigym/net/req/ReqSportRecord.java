package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;

/**
 * Created by xiaoshu on 15/11/17.
 */
public class ReqSportRecord extends ReqBase {
    public String  obj="precord";
    public String  act="getPrecord";

    public String date="";

    public ReqSportRecord(String pid,String date){
        this.pid=pid;
        this.date=date;
    }
    public ReqSportRecord(){
    }
}
