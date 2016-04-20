package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/11/16.
 */
public class RspGetWatchingHistory extends RspBase {
	public Data data = new Data();

	public class Data {
		public List<VideoListReturn> video_list_return = new ArrayList<VideoListReturn>();
	}

	public class VideoListReturn {
		public String coach_name = "";
		public String course_fid = "";
		public String course_name = "";
		public String course_id = "";
		public long ut = 0;
		public String video_link = "";
	}
}