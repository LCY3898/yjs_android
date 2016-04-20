package com.cy.share;

import android.content.Context;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-14
 * </p>
 * <p>
 * share lib入口
 * </p>
 */
public class ShareLibInitializer {
	private static Context mContext;

	/**
	 * 初始化
	 * 
	 * @param context
	 *            全局上下文
	 * @param isOpenLog
	 *            是否开启log
	 */
	public static void init(Context context, boolean isOpenLog) {
		mContext = context;
		com.umeng.socialize.utils.Log.LOG = isOpenLog;
	}

	synchronized public static Context getAppContext() {
		if (mContext == null) {
			throw new RuntimeException(
					"请在主工程的Application的onCreate方法里面调用本类的init方法进行初始化");
		}
		return mContext;
	}
}
