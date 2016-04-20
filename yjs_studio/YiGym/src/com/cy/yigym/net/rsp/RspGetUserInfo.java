package com.cy.yigym.net.rsp;

import java.io.Serializable;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/7/22.
 */
public class RspGetUserInfo extends RspBase implements Serializable {

	public Data data = new Data();

	public static class Data implements Serializable{
		public PersonInfo personInfo = new PersonInfo();

	}

	public static class PersonInfo implements Serializable{
		public String nick_name = "";
		public String profile_fid = "";
		public String sex = "";
		public String height = "";
		public String weight = "";
		public String birth = "";
		public String job = "";
		public String location = "";
		public String signature = "";
		public String score="";
		public String _id = "";
		public RspLogin.NetEaseAccount neteaseIMAcc;
	}
}