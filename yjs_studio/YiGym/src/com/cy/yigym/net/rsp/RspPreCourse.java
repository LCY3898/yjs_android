package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoshu on 15/10/10.
 * <p>
 * <p>
 * _id: "o14434251417337679862", begin_time: 1443770563, //开始时间 calorie: 5800,
 * //预估消耗卡路里 coach_id: "o14434172184801750183", //教练id coach_name: "阿汤哥", //教练名字
 * course_fid: "f14434250460615739822001", //课程封面图片fid course_info:
 * "汤姆·克鲁斯（Tom Cruise）", //课程简介 course_name: "阿汤哥带你飞1-1", //课程名称 course_time:
 * 1800, //课程时长 course_type: "bottom", //课程类型 end_time: 1443770563, //结束时间
 */
public class RspPreCourse extends RspBase {

	public Data data = new Data();

	public class Data {
		public int number = 0;
		public int page_num = 0;
		public String[] coach_list = new String[] {};
		public ArrayList<PreCourse> course_list = new ArrayList<PreCourse>();
	}

	public class PreCourse {
		public String _id = "";
		public long begin_time = 0;
		public int calorie = 0;
		public String coach_id = "";
		public String coach_name = "";
		public String course_fid = "";
		public String course_info = "";
		public String course_name = "";
		public int course_time = 0; //课程时长
		public String course_type = "";
		public long end_time = 0;
		public int watch_num = 0;
		public String video_link = "";
		public int current_num=0;

	}
}
