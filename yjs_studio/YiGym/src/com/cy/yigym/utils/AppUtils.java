package com.cy.yigym.utils;


import android.content.Context;

/**
 * Caiyuan Huang
 *<p>2015-3-6</p>
 *<p>请在Application里面进行初始化</p>
 */
public class AppUtils {

	private static Context sContext;
	
	public static void init(Context context) {
		sContext = context;
	}
	
	synchronized public static Context getAppContext() {
		return sContext;
	}	
	
	/**获取字符串
	 * @param id
	 * @return
	 */
	public static String getString(int id) {
		return sContext.getString(id);
	}

}
