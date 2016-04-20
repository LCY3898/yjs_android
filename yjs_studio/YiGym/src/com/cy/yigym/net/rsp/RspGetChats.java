package com.cy.yigym.net.rsp;

import java.util.ArrayList;

import com.cy.wbs.RspBase;

public class RspGetChats extends RspBase {
	public Data data = new Data();

	public static class Data {
		public ArrayList<Chat> chatList = new ArrayList<RspGetChats.Chat>();
	}

	public static class Chat {
		public String content = "";
		public String et = "";
		public String userId = "";
		public String xtype = "";
	}
}
