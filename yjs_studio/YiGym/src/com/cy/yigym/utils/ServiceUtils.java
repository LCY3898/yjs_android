package com.cy.yigym.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

/**
 * Caiyuan Huang
 * <p>
 * 2014-11-11下午3:17:30
 * </p>
 * <p>
 * Service工具类
 * </p>
 */
public class ServiceUtils {

	/**
	 * 判断服务是否正在运行
	 * 
	 * @param mContext
	 * @param serClass
	 *            服务所在的类
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext,
			Class<? extends Service> serClass) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(300);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					serClass.getName()) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
