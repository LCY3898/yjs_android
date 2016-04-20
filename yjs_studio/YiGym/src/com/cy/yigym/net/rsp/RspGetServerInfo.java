package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.io.Serializable;

public class RspGetServerInfo extends RspBase {


//	public Data data=new Data();
//
//
//	public static class Data implements Serializable {
//
//		public ServerInfo server_info = new ServerInfo();
//	}

	public ServerInfo server_info = new ServerInfo();



	public static class ServerInfo {
		public String upload_to = "";
		public String download_path = "";
		public String mp4_link_prefix = "";
		public String proj = "";
	}



}
