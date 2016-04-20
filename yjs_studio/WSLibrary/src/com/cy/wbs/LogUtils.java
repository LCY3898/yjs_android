package com.cy.wbs;

import android.util.Log;

public class LogUtils {
	
	public static void i(String tag, String msg) {
		if (WSLibInitializer.IS_DEBUG) {
			Log.i(tag, msg);
		}
	}
}
