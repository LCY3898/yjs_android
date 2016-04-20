package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoshu on 15/11/11.
 */
public class RspChiefCoach extends RspBase {

	public List<Data> data = new ArrayList<Data>();

	public class Data {
		public String coach_id = "";
		public String coach_name = "";
		public int course_number = 0;
		public String profile_fid = "";
		public ArrayList<CourseList> course_list = new ArrayList<CourseList>();

	}

	public class CourseList {
		public long begin_time = 0;
		public long end_time = 0;
		public String coach_name = "";
		public String course_fid;
		public String video_link;
		public String course_id = "";
		public String course_name = "";
		// FIXME:服务端该字段暂时没有添加，若添加了请检查是否与该字段一致
		public int isSubscribed= -1; // 预约与否标志，-1为未预约，1为预约
	}
}
