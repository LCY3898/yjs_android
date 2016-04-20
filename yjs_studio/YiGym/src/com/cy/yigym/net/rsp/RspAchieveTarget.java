package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 *  完成目标
 * Created by ejianshen on 15/7/25.
 */
public class RspAchieveTarget extends RspBase {

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

