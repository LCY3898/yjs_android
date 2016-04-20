package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-29
 * </p>
 * <p>
 * 运动目标请求
 * </p>
 */
public class ReqGetExerciseTarget extends ReqBase {
	public String obj = "person";
	public String act = "getExerciseTarget";
	public ClientInfoEntity client_info;
	public int debug = 1;

	public ReqGetExerciseTarget() {
		pid = DataStorageUtils.getPid();
	}

	public static class ClientInfoEntity {
		public String clientType = "webapp";
		public Object userId;
	}
}
