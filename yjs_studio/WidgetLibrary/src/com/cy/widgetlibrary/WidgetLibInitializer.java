package com.cy.widgetlibrary;

import android.content.Context;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-27
 * </p>
 * <p>
 * Widget lib初始化入口类
 * </p>
 */
public class WidgetLibInitializer {
	private static Context sContext;

	public static void init(Context context) {
		sContext = context;
	}

	synchronized public static Context getAppContext() {
		return sContext;
	}

}
