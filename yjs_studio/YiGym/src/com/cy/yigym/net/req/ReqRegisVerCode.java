package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-9
 * </p>
 * <p>
 * </p>
 */
public class ReqRegisVerCode extends PersonReqBase {
    private String act = "getRegisterVerifyCode";
    private String phone = "";  //手机号码
    private String forResetPasswd = "";//标志是否是找回密码，0表示验证码为注册时使用，1表示找回密码使用

    public ReqRegisVerCode(String phone, String forResetPasswd) {
        super();
        this.phone = phone;
        this.forResetPasswd = forResetPasswd;
    }
}
