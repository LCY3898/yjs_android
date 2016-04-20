package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-25
 * </p>
 * <p>
 * IM聊天发送请求
 * {"act":"private_send","chat_content":"[em]f001[/em]","chat_xtype":"text"
 * ,"client_info"
 * :{"clientType":"webapp","userId":""},"debug":"1","obj":"chat","receiver_id"
 * :"o14370552253036420345"
 * ,"sender_id":"o14370593611595270633","echo":{"timestamp":"1440514409234"}}
 * </p>
 */
public class ReqPrivateSend extends ReqBase {
	public String obj = "chat";
	public String act = "private_send";
	public String sender_id = DataStorageUtils.getPid();
	public String receiver_id = "";
	public String chat_content = "";
	public String chat_xtype = "";
	public String debug = "1";
	public ClientInfo client_info = new ClientInfo();

	public static class ClientInfo {
		public String clientType = "webapp";
		public String userId = "";
	}

	public ReqPrivateSend(String receiver_id, String chat_content,
			String chat_xtype) {
		super();
		this.receiver_id = receiver_id;
		this.chat_content = chat_content;
		this.chat_xtype = chat_xtype;
	}

}
