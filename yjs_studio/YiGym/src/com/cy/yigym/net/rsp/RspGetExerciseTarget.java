package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-29
 * </p>
 * <p>
 * 运动目标数据
 * </p>
 */
public class RspGetExerciseTarget extends RspBase {
	public DataEntity data = new DataEntity();

	public static class DataEntity {
		public int current_day_farest_distance;
		public int current_day_most_calorie;
		public int current_target_day_farest_distance;
		public int current_target_day_most_calorie;
		public int current_target_total_calorie;
		public int current_target_total_distance;
		public int current_total_calorie;
		public int current_total_distance;
		public int next_target_day_farest_distance;
		public int next_target_day_most_calorie;
		public int next_target_total_calorie;
		public int next_target_total_distance;
	}
}
