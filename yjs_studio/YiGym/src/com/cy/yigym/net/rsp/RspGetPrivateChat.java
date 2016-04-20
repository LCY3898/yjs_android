package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-25
 * </p>
 * <p>
 * IM消息接收回调
 * </p>
 * { "io": "o", "obj": "chat", "act": "get_private_chat", "chat_id":
 * "o14405172358404839038", "chat_xtype": "text", "code": "SUCCEED", "content":
 * "[em]f001[/em]", "durable_at": 0, "echo": { "timestamp": "1440514409234" },
 * "send_time": 1440517235, "sender_id": "o14370593611595270633" }
 */
public class RspGetPrivateChat{
	public String io="";
	public String obj="";
	public String act="";
	public String chat_id="";
	public String chat_xtype="";
	public String content="";
	public String durable_at="";
	public String send_time="";
	public String sender_id="";

}
