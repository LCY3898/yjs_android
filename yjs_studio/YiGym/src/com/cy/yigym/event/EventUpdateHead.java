package com.cy.yigym.event;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-21
 * </p>
 * <p>
 * 更新头像事件
 * </p>
 */
public class EventUpdateHead {
	private String headUrl = "";

	public EventUpdateHead(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getHeadUrl() {
		return headUrl;
	}
}
