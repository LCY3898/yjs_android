package com.cy.yigym.event;

import com.cy.yigym.db.entity.IMChatMsg;
import com.cy.yigym.net.rsp.RspGetPrivateChat;

public class EventIMMsgReceived {
	public IMChatMsg msg;

	public EventIMMsgReceived(IMChatMsg msg) {
		super();
		this.msg = msg;
	}

}
