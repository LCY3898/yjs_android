package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;

/**
 * Created by xiaoshu on 15/11/13.
 */
public class RspHotCourse extends RspBase {
    public ArrayList<HotCourse> data = new ArrayList<HotCourse>();

    public class HotCourse {
        public String coach_name = "";
        public String course_fid = "";
        public String course_id = "";
        public String course_name = "";
        public String video_link = "";
        public long begin_time=0;
        public long end_time=0;
        public int course_time=0;
    }
}
