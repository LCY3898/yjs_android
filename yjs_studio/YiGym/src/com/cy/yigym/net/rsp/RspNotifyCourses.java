package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/10/19.
 */
public class RspNotifyCourses extends RspBase {

    public Data data=new Data();
    public class Data{
        public List<CourseList> course_list=new ArrayList<CourseList>();
    }
    public class CourseList{
        public String _id= "o14434296030828480720";
        public long begin_time=0;
        public double calorie= 501;
        public String coach_id="o14431676610128390789";
        public String coach_name="陈杰";
        public String course_fid= "f14434295732507400512001";
        public String course_info="陈教头之人鱼线 1-1";
        public String course_name="陈教头之人鱼线 1-1";
        public long course_time=12000;
        public String course_type="中级课程";
        public int current_num=1;
        public long end_time=1445393520;
        public int isSubscribed= -1;                   //预约与否标志，-1为未预约，1为预约
        public String type="course";
    }
}
