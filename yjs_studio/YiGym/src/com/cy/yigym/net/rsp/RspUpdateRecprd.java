package com.cy.yigym.net.rsp;


import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/7/28.
 */
public class RspUpdateRecprd extends RspBase {

    public Data data=new Data();

    public static class Data{
        public String _id="";
        public String apart_distance="";
        public String begin="";
        public String chaseStatus="";
        public String receiver_calorie="";
        public String receiver_distance="";
        public String receiver_id="";
        public String receiver_time="";
        public String sender_calorie="";
        public String sender_distance="";
        public String sender_id="";
        public String sender_time="";
        public String status="";
        public String type="";
        public int flag=0;
    }
}
