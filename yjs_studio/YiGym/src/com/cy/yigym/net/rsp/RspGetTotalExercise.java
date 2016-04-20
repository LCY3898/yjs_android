package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/8/20.
 */
public class RspGetTotalExercise extends RspBase {
    public Data data=new Data();
    public class Data{
        public double total_calorie=0;
        public double total_distance=0;
        public int total_exercise=0;
        public double total_time=0;
    }
}
