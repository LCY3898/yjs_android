package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-29
 * </p>
 * <p>
 * 第三方登录接口
 * </p>
 */
public class ReqAuthorize extends ReqBase {
	public String obj = "person";
	public String act = "authorize";
	public String usid = "";
	public String access_token = "";
	public String ctype = "weibo";
	public ClientInfo client_info;

	public ReqAuthorize(String usid, String access_token, String ctype,
			ClientInfo client_info) {
		super();
		this.usid = usid;
		this.access_token = access_token;
		this.ctype = ctype;
		this.client_info = client_info;
	}

	public static class ClientInfo {
		public String clientType = "";
		public String userId = "";

		public ClientInfo(String userId) {
			super();
			this.userId = userId;
		}

	}
}
