package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 *  放弃目标
 * Created by ejianshen on 15/7/25.
 */
public class RspDropTarget extends RspBase {

    public Data data=new Data();
    public static class Data{
        public String percent="";
        public Result result=new Result();
    }
    public static class Result{
        public String calorie="";
        public String distance="";
        public String time="";
    }
}
