package com.cy.yigym.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class PackageUtils {
	/**
	 * 获取app版本名称
	 * 
	 * @param context
	 * @return 获取失败返回""，否则返回版本名称
	 */
	public static String getVersionName() {
		String versionName = "";
		if (AppUtils.getAppContext() == null)
			return versionName;
		try {
			PackageManager pm = AppUtils.getAppContext()
					.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(AppUtils
					.getAppContext().getPackageName(), 0);
			versionName = pi.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		} catch (Exception e) {
		}
		return versionName;
	}

	/**
	 * 获取app版本号
	 * 
	 * @param context
	 * @return 获取失败返回-1，否则返回版本号
	 */
	public static int getVersionCode() {
		int versionCode = -1;
		if (AppUtils.getAppContext() == null)
			return versionCode;
		try {
			PackageManager pm = AppUtils.getAppContext()
					.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(AppUtils
					.getAppContext().getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
		}
		return versionCode;
	}

	/**
	 * 返回程序包名
	 * 
	 * @param context
	 * @return 获取失败返回""，否则返回程序包名
	 */
	public static String getPackageName() {
		if (AppUtils.getAppContext() == null)
			return "";
		return AppUtils.getAppContext().getPackageName();
	}

	/**
	 * 
	 * 检查包名所在的程序是否有某项权限
	 * 
	 * @param context
	 * @param permName
	 *            权限名称
	 * @param pkgName
	 *            程序所在的包名
	 * @return
	 */
	public static boolean checkPermission(String permName, String pkgName) {
		PackageManager pm = AppUtils.getAppContext()
				.getPackageManager();
		try {
			boolean isHave = (PackageManager.PERMISSION_GRANTED == pm
					.checkPermission(permName, pkgName));
			return isHave;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 检查当前应用是否有某项权限
	 * 
	 * @param permName
	 * @return
	 */
	public static boolean checkPermission(String permName) {
		return checkPermission(permName, AppUtils.getAppContext()
				.getPackageName());
	}
}
