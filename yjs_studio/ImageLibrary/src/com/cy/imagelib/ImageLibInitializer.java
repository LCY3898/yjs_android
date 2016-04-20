package com.cy.imagelib;

import android.content.Context;
import android.content.res.Resources;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-29
 * </p>
 * <p>
 * 图片Lib初始化
 * </p>
 */
public class ImageLibInitializer {
	private static Context sContext;

	public static void init(Context context) {
		sContext = context;
	}

	synchronized public static Context getAppContext() {
		if (sContext == null)
			throw new RuntimeException("请调用init方法在Application里面进行初始化");
		return sContext;
	}

	public static Resources getResources() {
		if (sContext == null)
			throw new RuntimeException("请调用init方法在Application里面进行初始化");
		return sContext.getResources();
	}
}
