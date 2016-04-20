package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;
import com.google.gson.annotations.SerializedName;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-29
 * </p>
 * <p>
 * 第三方登录响应
 * </p>
 * { "sess": "o14381832972145369052", "io": "o", "obj": "person", "act":
 * "authorize", "code": "SUCCEED", "data": { "server_info": { "upload_to": null
 * }, "user_info": { "_id": "o14381832972123808860", "account_id":
 * "o14381832972114479541", "display_name":
 * "weixin:oxG4AwRlbEsrW7zpzfjfVBzBOd5w", "et": 1438183297, "location":
 * "CNFujianLongyan", "nick_name": null, "profile_fid":
 * "http://wx.qlogo.cn/mmopen/Q3auHgzwzM7icWxwsgDmZBgerorscuXVoyVVxufFuyHcubQ4gg1E1t21cz1j0PqZjGc1L7fWXhHS4kUEvU0Dtsw/0"
 * , "security": 10, "server": "default", "sex": "男", "status": "active",
 * "type": "person", "ut": 1438183297 } }, "echo": { "timestamp":
 * "1438183295294" }, "msg": "微信第三方登录成功-第一次" }
 */
public class RspAuthorize extends RspBase {

	public Data data=new Data();

	public static class Data{
		public UserInfo user_info=new UserInfo();
		public RspLogin.ServerInfo server_info = new RspLogin.ServerInfo();
	}

	public boolean isAccomplish() {
		if(data == null || data.user_info == null) {
			return false;
		}
		return "1".equals(data.user_info.isAccomplish);
	}

	public static class UserInfo {
		public String _id = "";
		public String account_id = "";
		public String display_name = "";
		public String et = "";
		public String location = "";
		public String nick_name = "";
		public String profile_fid = "";
		public String security = "";
		public String server = "";
		public String sex = "";
		public String status = "";
		public String type = "";
		public String ut = "";
		public String isRegister="";
		public String isAccomplish = "0";
		@SerializedName("SubAccount")
		public RspLogin.SubAccount subAccount;
		@SerializedName("neteaseIMAcc")
		public RspLogin.NetEaseAccount neteaseIMAcc;
	}

}
