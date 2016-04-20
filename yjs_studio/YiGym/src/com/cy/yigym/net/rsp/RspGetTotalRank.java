package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取运动排名
 * Created by ejianshen on 15/8/3.
 */
public class RspGetTotalRank extends RspBase {
    public Data data=new Data();

    public class Data{
        public List<Pers> pers=new ArrayList<Pers>();
        public Person person=new Person();
    }
    public class Pers{
        public String pId="";
        public String profile_fid="";
        public double total_calorie=0;
        public double total_distance=0;
        public double total_time=0;
    }
    public class Person{
        public String _id="";
        public String birth="";
        public String display_name="";
        public String height="";
        public String job="";
        public String location="";
        public String phone="";
        public String profile_fid="";
        public String security="";
        public String sex="";
        public String signature="";
        public String total_distance="";
        public String type="";
        public String weight="";
    }
}