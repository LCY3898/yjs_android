package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/21.
 */
public class RspGetCrazeRank extends RspBase{

    public Data data=new Data();

    public class Data{
        public int rank=0;
        public List<ArrayRank> arrayRank=new ArrayList<ArrayRank>();
    }
    public class ArrayRank{
       public double distance=0;
       public String nick_name="";
       public String pid="";
       public String profile_fid="";
       public int rank_list=1;
    }
}
