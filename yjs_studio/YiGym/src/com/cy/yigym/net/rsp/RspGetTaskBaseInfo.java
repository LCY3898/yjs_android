package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/8/21.
 */
public class RspGetTaskBaseInfo extends RspBase{

    public Data data=new Data();

    public class Data{
        public int state=1;
        public String task_begin="";
        public String task_description="";
        public String task_end="";
        public String task_joiner_num="";
        public String task_size="";
        public String task_title="";
        public String task_xtype="";
    }

}
