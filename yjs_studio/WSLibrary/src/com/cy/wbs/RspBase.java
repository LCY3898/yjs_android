package com.cy.wbs;


/**
 * Caiyuan Huang
 * <p>
 * 2015-7-13
 * </p>
 * <p>
 * 接口响应数据基类
 * </p>
 */
public class RspBase {
	public String msg=""; //服务端返回的具体信息 例："msg":"成功获取用户信息"
	public String sess="";//session字段
	public String io="";//output，表示响应
	public String obj="";//要操纵的数据集合
	public String act="";//接口名称
	public String code="-1";//返回的code码，1表示SUCCEED，接口调用成功；0表示ERROR，调用失败
	public String uerr = "";//用户错误
	public String derr = "";//开发者错误
	public String ustr = "";//
	public static transient final String OK_CODE="SUCCEED";  // 标志接口调用成功
	public static transient final String USER_NOT_EXIST ="NOT_EXIST_PHONE";//标志该账户未注册

	public boolean isOk() {
		return OK_CODE.equals(code);
	}
}
