package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-21
 * </p>
 * <p>
 * 获取上传地址接口响应类型
 * </p>
 */
public class RspGetUpload extends RspBase {
	public Data data = new Data();

	public static class Data {
		public String result = "";
	}
}
