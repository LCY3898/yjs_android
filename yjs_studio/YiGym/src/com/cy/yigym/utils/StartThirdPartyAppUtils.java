package com.cy.yigym.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-22
 * </p>
 * <p>
 * 启动第三方应用工具类
 * </p>
 */
public class StartThirdPartyAppUtils {
	/**
	 * 
	 * 打开网络设置界面
	 * 
	 * @param context
	 */
	public static void openNetworkSetting(Context context) {
		try {
			Intent intent = null;
			// 判断手机系统的版本 即API大于10 就是3.0或以上版本
			if (android.os.Build.VERSION.SDK_INT > 10) {
				intent = new Intent(
						android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			} else {
				intent = new Intent();
				ComponentName component = new ComponentName(
						"com.android.settings",
						"com.android.settings.WirelessSettings");
				intent.setComponent(component);
				intent.setAction("android.intent.action.VIEW");
			}
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 启动到app详情界面
	 * 
	 * @param appPkg
	 *            App的包名
	 * @param marketPkg
	 *            应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
	 */
	public static void goToAppDetailInMarket(String appPkg, String marketPkg) {
		try {
			if (TextUtils.isEmpty(appPkg))
				return;
			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (!TextUtils.isEmpty(marketPkg))
				intent.setPackage(marketPkg);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			AppUtils.getAppContext().startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
