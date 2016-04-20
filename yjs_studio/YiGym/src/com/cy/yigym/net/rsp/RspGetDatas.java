package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/7/28.
 */
public class RspGetDatas extends RspBase {
    public Data data=new Data();

    public static class Data{
        public double get_calorie=0;
        public double get_distance=0;
        public double get_time=0;
        public double apart_distance=0;
    }
}
