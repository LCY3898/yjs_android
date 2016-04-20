package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.google.gson.annotations.SerializedName;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-10
 * </p>
 * <p>
 * {"obj":"person","act":"register","login_name":"18559160624","login_passwd":
 * "1","code_num":"340","debug":1}
 * </p>
 */
public class ReqRegis extends PersonReqBase {
	private String act = "register";
	private String phone = "";    //手机号码
	private String login_passwd = ""; //密码
	private String verify_code = ""; //短信验证码


	public ReqRegis(String login_name, String login_passwd, String code_num) {
		super();
		this.phone = login_name;
		this.login_passwd = login_passwd;
		this.verify_code = code_num;
	}

}
