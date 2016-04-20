package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/7/25.
 */
public class RspGetRecentTarget extends RspBase {

    public Data data=new Data();

    public static class Data{
        public String _id="";
        public String creatOn="";
        public String isSystem="";
        public String pid="";
        public String size="";
        public String status="";
        public String type="";
        public String ut="";
        public String xtype="";
    }

}
