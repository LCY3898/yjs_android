package com.cy.wbs;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-23
 * </p>
 * <p>
 * 测试的服务端地址
 * </p>
 */
public class TestNetUrlImpl implements INet {

	@Override
	public String getBaseUrl() {
		return "ws://121.40.16.113:51717/yjs";
	}

}
