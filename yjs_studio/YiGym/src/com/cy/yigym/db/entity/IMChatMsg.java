package com.cy.yigym.db.entity;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-16
 * </p>
 * <p>
 * 聊天记录信息实体
 * </p>
 */
public class IMChatMsg {
	/**
	 * 消息创建时间
	 */
	public String msgTime = "";
	/**
	 * 消息类型
	 */
	public String msgType = "";
	/**
	 * 消息状态
	 */
	public String msgStatus = "";
	/**
	 * 消息内容
	 */
	public String msgContent = "";
	/**
	 * 消息发送者
	 */
	public String msgSender = "";
	/**
	 * 消息接收者
	 */
	public String msgReceiver = "";

	public IMChatMsg() {
		super();
	}

	public IMChatMsg(String msgTime, String msgType, String msgStatus,
			String msgContent, String msgSender, String msgReceiver) {
		super();
		this.msgTime = msgTime;
		this.msgType = msgType;
		this.msgStatus = msgStatus;
		this.msgContent = msgContent;
		this.msgSender = msgSender;
		this.msgReceiver = msgReceiver;
	}

	public String getMsgTime() {
		return msgTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public String getMsgStatus() {
		return msgStatus;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public String getMsgSender() {
		return msgSender;
	}

	public String getMsgReceiver() {
		return msgReceiver;
	}

}
