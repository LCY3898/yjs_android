package com.cy.wbs;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Caiyuan Huang
 * <p>
 * 2014-11-20下午3:22:18
 * </p>
 * <p>
 * 网络工具类
 * </p>
 */
public class NetworkUtils {

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return true表示网络连接，false表示无网络连接
	 */
	public static boolean isNetworkAvailable() {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) WSLibInitializer
					.getAppContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				return false;
			} else {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断网络是否处于漫游状态
	 * 
	 * @param context
	 * @return true表示网络处于漫游状态，false表示网络处于非漫游状态
	 */
	public static boolean isNetworkRoaming() {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) WSLibInitializer
					.getAppContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null
						&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
					TelephonyManager tm = (TelephonyManager) WSLibInitializer
							.getAppContext().getSystemService(
									Context.TELEPHONY_SERVICE);
					if (tm != null && tm.isNetworkRoaming()) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断Mobile网络是否可用
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean isMobileDataEnable() {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) WSLibInitializer
					.getAppContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				return connectivity.getNetworkInfo(
						ConnectivityManager.TYPE_MOBILE)
						.isConnectedOrConnecting();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断WiFi网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiDataEnable() {
		ConnectivityManager connectivity = (ConnectivityManager) WSLibInitializer
				.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			return connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnectedOrConnecting();
		}
		return false;
	}

	public static void registerNetworkChangeListener(Context context,
			NetworkChangeReceiver networkChangeReceiver) {
		IntentFilter filter = new IntentFilter();
		// filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(networkChangeReceiver, filter);
	}

	public static void unRegisterNetworkChangeListener(Context context,
			NetworkChangeReceiver networkChangeReceiver) {
		context.unregisterReceiver(networkChangeReceiver);
	}

	/**
	 * ping命令回调接口
	 */
	public static interface PingCallBack {
		/**
		 * @param isPingOk
		 *            true,ping通了,false,ping失败了
		 */
		void onPingCallBack(boolean isPingOk);
	}

	/**
	 * ping命令
	 * 
	 * @return
	 */
	public static void ping(final PingCallBack pingCallBack) {
		if (pingCallBack == null)
			return;
		pingCallBack.onPingCallBack(true);
	}
}
