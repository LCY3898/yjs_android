package com.cy.yigym.net.rsp;

import java.util.ArrayList;

import com.cy.wbs.RspBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-19
 * </p>
 * <p>
 * 成长记录接口返回数据实体
 * </p>
 */
public class RspGetPevent extends RspBase {
	public Data data = new Data();

	public static class Data {
		public long delta_sec;
		public ArrayList<Medal> medal_list = new ArrayList<RspGetPevent.Medal>();
		public int medal_type_the_chase;
		public int medal_type_the_data_accumulated;
		public int medal_type_the_day_most;
		public int medal_type_the_emotional;
		public int medal_type_the_live_broadcast;
		public int medal_type_the_training_time;
	}

	public static class Medal {
		public long finished_time;
		public String medal_fid = "";
		public String medal_id = "";
		public String medal_meaning = "";
		public String medal_name = "";
		public String medal_type = "";
		public String share_fid = "";
	}
}
