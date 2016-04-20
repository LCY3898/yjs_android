package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/10/12.
 */
public class RspGetLastCourse extends RspBase {
    public Data data = new Data();

    public class Data {
        public LastCourse lastCourse = new LastCourse();
        public long server_current_time = 0; //服务器上的当前时间戳
        public String live_broadcast_addr = "";
    }

    public class LastCourse {
        public String _id = "";
        public long begin_time = 0;
        public String calorie = "";
        public String coach_id = "";
        public String coach_name = "";
        public String course_fid = "";
        public String course_info = "";
        public String course_name = "";
        public int course_time = 0;
        public String course_type = "";
        public int current_num = 0;
        public String watch_num = "";
        public long end_time = 0;
        public String type = "";
        public String coach_fid = "";
    }
}
