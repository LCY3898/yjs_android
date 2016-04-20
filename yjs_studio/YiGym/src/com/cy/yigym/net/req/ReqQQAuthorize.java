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
public class ReqQQAuthorize extends ReqAuthorize {
	public String appid = "";

	public ReqQQAuthorize(String usid, String access_token, String ctype,String appid,
						  ClientInfo client_info) {
		super(usid,access_token,ctype,client_info);
		this.appid = appid;
	}

}
