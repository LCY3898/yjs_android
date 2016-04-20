package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-9
 * </p>
 * <p>
 * {"sess":"","io":"o","obj":"person","act":"getRegisterVerifyCode","derr":
 * "[boot.pm: 188]> [boot.pm: 128]> [_lib.pl: 3673]> [_lib.pl: 1886]> [Error.pm: 413]> [Error.pm: 414]> [_lib.pl: 1880]> [app.pl: 84]> [_lib.pl: 1109]: invalid _id when writing"
 * ,"code":0,"data":"092341431185451317","echo":"{timestamp:1436456501560}",
 * "msg":"验证码发送成功","verify_code":"5485"}
 * </p>
 */
public class RspRegisVerCode extends RspBase{
	public VerifyCode data;

	public static class VerifyCode {
		public String verify_code = "";//验证码
	}
}
