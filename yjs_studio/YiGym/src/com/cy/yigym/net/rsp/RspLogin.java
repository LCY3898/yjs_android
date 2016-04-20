package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;
import com.google.gson.annotations.SerializedName;

public class RspLogin extends RspBase {


	public Data data=new Data();
	public boolean isAccomplish() {
		if(data == null || data.user_info == null) {
			return false;
		}
		return "1".equals(data.user_info.isAccomplish);
	}


	public static class Data {
		public double delta_time=0;
		public ServerInfo server_info = new ServerInfo();
		public UserInfo user_info = new UserInfo();

	}

	public static class UserInfo {
		@SerializedName("SubAccount")
		public SubAccount subAccount =new SubAccount();  //容联云账号信息，暂未使用
		@SerializedName("neteaseIMAcc")
		public NetEaseAccount neteaseIMAcc=new NetEaseAccount();//网易云信账号信息，用于点对点视频
		public String _id = "";//用户ID
		public String phone = "";//电话号码
		public String security = "";//
		public String isAccomplish= "0";//
		public String type = "";//
		public String nick_name="";//昵称
		public String profile_fid="";//用户头像ID
	}


	public static class ServerInfo {
		public String upload_to = ""; //图片上传地址
		public String download_path;//图片下载地址
		public String mp4_link_prefix;//视频播放地址
	}
	public static class SubAccount{
		public String dateCreated="";
		public String subAccountSid="";
		public String subToken="";
		public String voipAccount="";
		public String voipPwd="";
	}

	public static class NetEaseAccount{
		public String accid=""; //用户accid码，即用户ID
		public String name="";//用户昵称
		public String token="";
	}

}
