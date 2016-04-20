package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-19
 * </p>
 * <p>
 * 成长记录接口
 * </p>
 */
public class ReqGetPevent extends ReqBase {
	public String obj = "pevent";
	public String act = "getPevent";
	public int debug = 1;
	public ClientInfo client_info = new ClientInfo();

	public ReqGetPevent() {
		pid = DataStorageUtils.getPid();
	}

	public static class ClientInfo {
		public String clientType = "webapp";
		public String userId = "";
	}
}
