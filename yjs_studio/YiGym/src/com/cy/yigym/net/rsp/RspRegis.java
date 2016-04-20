package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-10
 * </p>
 * <p>
 * { sess: "o14363287439230020046", io: "o", obj: "person", act: "register",
 * server_info: { upload_to: null }, user_info: { _id: "o14363287439197471141",
 * account_id: "o14363287439186570644", display_name: "未填写", et: 1436328743,
 * security: 10, server: "default", status: "active", type: "person", ut:
 * 1436328743 } }
 * 
 * 
 * </p>
 */
public class RspRegis extends RspBase{
	public ServerInfo server_info = new ServerInfo();
	public UserInfo user_info = new UserInfo();
	public Data data=new Data();
	public class Data{
		public String pid="";  //用户ID
	}

	public static class ServerInfo {
		public String upload_to = "";
	}

	public static class UserInfo {
		public String _id = "";  //用户ID
		public String account_id = ""; //帐户纪录ID
		public String display_name = "";//手机号码
		public String et = "";//记录入库时间
		public String security = "";//
		public String server = "";//
		public String status = "";//标记这条记录的状态
		public String type = "";//集合名称
		public String ut = "";//记录更新时间
	}

}
