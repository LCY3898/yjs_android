package com.cy.yigym.db.entity;

public class IMRecentMsg {
	/**
	 * 对方的uid
	 */
	public String chatUid = "";
	/**
	 * 最后一次交流的时间
	 */
	public String lastChatTime = "";

	public IMRecentMsg(String chatUid, String lastChatTime) {
		super();
		this.chatUid = chatUid;
		this.lastChatTime = lastChatTime;
	}

	public IMRecentMsg() {
		super();
		// TODO Auto-generated constructor stub
	}

}
