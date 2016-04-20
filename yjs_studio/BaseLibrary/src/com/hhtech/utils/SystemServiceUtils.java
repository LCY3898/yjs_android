package com.hhtech.utils;

import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.hhtech.base.AppUtils;

public class SystemServiceUtils {

	public static WifiManager getWifiMgr() {
		return (WifiManager) AppUtils.getAppContext().getSystemService(Context.WIFI_SERVICE);
	}

	
	public static ConnectivityManager getConnectivityMgr() {
		return (ConnectivityManager) AppUtils
				.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	}
}
