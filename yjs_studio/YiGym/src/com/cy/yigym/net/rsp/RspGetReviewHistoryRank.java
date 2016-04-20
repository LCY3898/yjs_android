package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;


/**
 * @author tangtt
 */
public class RspGetReviewHistoryRank extends RspBase{
    public Data data=new Data();
    public class Data{
        public List<RankItem> values=new ArrayList<RankItem>();
        public MyRef myRank=new MyRef();
    }

    public class MyRef{
        public double value=0;  //卡路里值
        public double distance=0;
        public int myrank;
        public String nick_name= "";
        public String pid="";
        public String profile_fid="";
        public double speed= 0;
        public double speed_per_min= 0;
        public String voipAccount;
        public double resist=0;
    }
    public class RankItem{
        public double value=121; //卡路里值
        public double distance=222;
        public int myrank;
        public String nick_name="";
        public String pid="";
        public String profile_fid="";
        public double speed=0;
        public double speed_per_min=0;
        public String voipAccount;
        public double resist=0;
    }
}
