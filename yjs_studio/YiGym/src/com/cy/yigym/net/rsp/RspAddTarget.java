package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-13
 * </p>
 * <p>
 * { "sess": "", "io": "o", "obj": "person", "act": "addTarget", "code": 1,
 * "data": { "creatOn": 1436796806, "pid": "o14366193999110519886", "size":
 * "12", "status": "开始", "ut": 1436796806, "xtype": "time" }, "echo": {
 * "timestamp": "1436796699694" }, "msg": "成功设定目标" }
 * 
 * </p>
 */
public class RspAddTarget extends RspBase {
	public Data data = new Data();

	public static class Data {
		public String creatOn = "";
		public String pid = "";
		public String size = "";
		public String status = "";
		public String ut = "";
		public String xtype = "";
		public String _id = ""; //task id
	}

}
