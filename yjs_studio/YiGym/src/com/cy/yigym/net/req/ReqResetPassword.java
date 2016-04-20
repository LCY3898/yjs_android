package com.cy.yigym.net.req;

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
public class ReqResetPassword extends PersonReqBase {

	private String login_passwd = "";
	private String act = "resetPassword";
	private String phone = "";
	private String verify_code="";

	public ReqResetPassword(String phone,String verifyCode,String login_passwd) {
		super();
		this.login_passwd = login_passwd;
		this.phone = phone;
		this.verify_code =  verifyCode;
	}

}
