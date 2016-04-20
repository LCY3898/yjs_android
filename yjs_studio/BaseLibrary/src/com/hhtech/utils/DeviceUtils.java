package com.hhtech.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hhtech.base.AppUtils;

public class DeviceUtils {
	/** 
	 * regular methods to install apk, 
	 * */
	public static void installApk(String apkPath) {
		installApk(Uri.fromFile(new File(apkPath)));
	}
	
	public static void installApk(Uri apkUri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		AppUtils.getAppContext().startActivity(intent);
	}

	
	/** 
	 * silent install apk for rooted device
	 * */
	public static boolean installApkRooted(File file, Context context) {
		boolean result = false;
		Process process = null;
		OutputStream out = null;
		InputStream in = null;
		String state = null;
		try {
			// request for root
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
	
			// write install to process
			out.write(("pm install -r " + file + "\n").getBytes());
	
			// start install
			in = process.getInputStream();
			int len = 0;
			byte[] bs = new byte[256];
			while (-1 != (len = in.read(bs))) {
				state = new String(bs, 0, len);
				if (state.equals("Success\n")) {
					result = true;
					Log.e("success", state);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	
	}
		

    /**
     * @return imei
     */
    public static String getLocaldeviceStrId() {
    	TelephonyManager telephonyManager = (TelephonyManager)AppUtils.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
		String str = telephonyManager.getDeviceId();
		return str;
    }
	
	
	/**
	 * 获取SD卡路径
	 * @return
	 */
	public static String getSdDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}
	
	
	/**
	 * SD卡是否可用	
	 * @return
	 */
	public static boolean isSDCardAvailable() {
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
}
