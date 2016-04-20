package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-10
 * </p>
 * <p>
 * {"login_passwd":"123456","obj":"person","sess":"","act":"login","verbose":"1"
 * ,"io":"i","login_name":"hcy01"}
 * </p>
 */
public class ReqLogin extends PersonReqBase {

	private String login_passwd = ""; //登录密码
	private String act = "login";
	private String login_name = ""; //用户名

	public ReqLogin(String login_passwd, String login_name) {
		super();
		this.login_passwd = login_passwd;
		this.login_name = login_name;
	}

	public String getUsername() {
		return login_name;
	}

	public String getPasswd() {
		return login_passwd;
	}

}
