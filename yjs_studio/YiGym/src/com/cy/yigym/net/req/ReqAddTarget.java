package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-13
 * </p>
 * <p>
 * 用户设置运动目标
 * </p>
 */
public class ReqAddTarget extends PersonReqBase {
	private String act = "addTarget";
	private String xtype = "";
	private String size = "0";

	/**
	 * @param xtype
	 *            {eg:time、distance、calorie}这三个值
	 * @param size与
	 *            xtype相对应的单位为分钟、公里、卡
	 */
	public ReqAddTarget(String xtype, String size) {
		super();
		this.xtype = xtype;
		this.size = size;
	}

}
