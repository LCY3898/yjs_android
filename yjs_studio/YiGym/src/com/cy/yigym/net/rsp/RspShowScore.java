package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/8/24.
 */
public class RspShowScore extends RspBase {

    public Data data=new Data();
    public class Data{
        public String total_score="";
        public ScoreInfo scoreInfo=new ScoreInfo();
    }
    public class ScoreInfo{
        private String _id="";
        public Score accomplishInfo=new Score();
        public Score achieveTarget=new Score();
        public Score chase=new Score();
        public Score register=new Score();
        public Score share=new Score();
    }
    public class Score{
        public String time="";
        public String score="";
    }
}
