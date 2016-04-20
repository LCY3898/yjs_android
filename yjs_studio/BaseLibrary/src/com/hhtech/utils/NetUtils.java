package com.hhtech.utils;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.hhtech.base.AppUtils;

public class NetUtils {

	public final static int BUILD_VERSION_JELLYBEAN = 17;

	// 判断网络是否可用
	public static boolean isNetConnected(Context context) {
		boolean result;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	// 判断网络是否可用
	public static boolean isNetConnected() {
		return isNetConnected(AppUtils.getAppContext());
	}

	public static boolean isWifiConnected() {
		ConnectivityManager connManager = SystemServiceUtils.getConnectivityMgr();
		NetworkInfo wifiInfo = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return (wifiInfo != null && wifiInfo.isConnected());
	}

	
	
	public static String getWifiIp() {
		WifiInfo wifiInfo = SystemServiceUtils.getWifiMgr().getConnectionInfo();
		if(wifiInfo == null) {
			return null;
		}
		return intToIp(wifiInfo.getIpAddress());
	}
	
	public static String intToIp(int ipInt) {
		 StringBuilder sb = new StringBuilder();
		 sb.append(ipInt & 0xff).append('.');
		 sb.append((ipInt >> 8) & 0xff).append('.');
		 sb.append((ipInt >> 16) & 0xff).append('.');
		 sb.append((ipInt >> 24) & 0xff);
		 return sb.toString();
	 }

	public static WifiConfiguration findConfigIfExist(WifiManager wifi,
			String ssid) {
		WifiConfiguration config = null;
		List<WifiConfiguration> existingConfigs = wifi.getConfiguredNetworks();
		if (existingConfigs != null && ssid != null) {
			for (WifiConfiguration existingConfig : existingConfigs) {
				if (existingConfig == null) {
					continue;
				} else if (existingConfig.SSID == null) {
					continue;
				}
				boolean ssidSame;

				ssidSame = existingConfig.SSID.equals("\"" + ssid + "\"")
						|| existingConfig.SSID.equals(ssid);

				if (ssidSame) {
					config = existingConfig;
					break;
				}
			}
		}
		return config;
	}

	public static String getCurrentWifiSsid() {
		WifiManager wifi = (WifiManager) AppUtils.getAppContext()
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String ssid = info.getSSID();
		return removeSSIDQuotes(ssid);
	}

	
	/**
	 * 获取wifi bssid，bssid即为路由器的mac地址
	 * @return
	 */
	public static String getWifiBssid() {
		WifiManager wifi = (WifiManager) AppUtils.getAppContext()
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String bssid = info.getBSSID();
		return bssid;
	}
	
	
	public static boolean isWifiEnable() {
		WifiManager wifi = (WifiManager) AppUtils.getAppContext()
				.getSystemService(Context.WIFI_SERVICE);
		return wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
	}

	/*
	 * Filters the double Quotations occuring in Jellybean and above devices.
	 * This is only occuring in SDK 17 and above this is documented in SDK as
	 * http
	 * ://developer.android.com/reference/android/net/wifi/WifiConfiguration.
	 * html#SSID
	 * 
	 * @param connectedSSID
	 * 
	 * @return
	 */
	public static String removeSSIDQuotes(String connectedSSID) {
		int currentVersion = Build.VERSION.SDK_INT;

		if (currentVersion >= BUILD_VERSION_JELLYBEAN) {
			if (connectedSSID.startsWith("\"") && connectedSSID.endsWith("\"")) {
				connectedSSID = connectedSSID.substring(1,
						connectedSSID.length() - 1);
			}
		}
		return connectedSSID;
	}
}
