package com.cy.yigym.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Caiyuan Huang
 * <p>
 * 2015-10-28
 * </p>
 * <p>
 * 震动工具类
 * </p>
 */
public class VibratorUtils {

	/**
	 * 震动
	 * 
	 * @param context
	 */
	public static void vibrate(Context context) {
		if (context == null)
			return;
		try {
			Vibrator vibrator = (Vibrator) context
					.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(new long[] { 0, 300, 500, 700 }, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
