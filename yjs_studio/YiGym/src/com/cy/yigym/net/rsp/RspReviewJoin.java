package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/7/21.
 */
public class RspReviewJoin extends RspBase {
    public Data data = new Data();
    public class Data {
        public double distance = 0;
        public double calorie =0;
    }
}
