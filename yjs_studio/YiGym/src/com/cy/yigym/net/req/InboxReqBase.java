package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;

/**
 * Money888
 * <p>
 * 2015-7-10
 * </p>
 * <p>
 * {"login_passwd":"123456","obj":"person","sess":"","act":"login","verbose":"1"
 * ,"io":"i","login_name":"hcy01"}
 * </p>
 */
public class InboxReqBase extends ReqBase {
	public String obj = "inbox";
}
