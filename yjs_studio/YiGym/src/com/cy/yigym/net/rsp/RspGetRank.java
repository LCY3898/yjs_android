package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/21.
 */
public class RspGetRank extends RspBase{
    public Data data=new Data();
    public class Data{
        public List<Array> rank_list=new ArrayList<Array>();
        public int count=0;
        public int myrank=0;
    }
    public static class Array{
        public String nick_name="";
        public String profile_fid="";
        public double total_calorie=0;
        public double total_distance=0;
        public double total_time=0;
        public double bechased_distance=0;
        public int total_chase=0;
        public double total_chase_distance=0;

    }
}
