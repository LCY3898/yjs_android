package com.cy.yigym.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2014-10-26下午10:17:54
 * </p>
 * <p>
 * 手机设备工具类
 * </p>
 */
public class DeviceUtils {
	private static DeviceUtils mInstance = null;
	private static Context mContext;

	public static DeviceUtils getInstance(Context context) {
		mContext = context;
		if (mInstance == null) {
			mInstance = new DeviceUtils();
		}
		return mInstance;
	}

	/**
	 * 获取屏幕宽度
	 * <p>
	 * 2014-10-26下午10:21:07
	 * </p>
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		return mContext.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * <p>
	 * 2014-10-26下午10:21:59
	 * </p>
	 * 
	 * @return
	 */
	public int getScreenHeight() {
		return mContext.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * SD卡是否可用
	 * <p>
	 * 2014-10-28下午9:30:11
	 * </p>
	 * 
	 * @return
	 */
	public boolean isSDCardAvailable() {
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 拨打电话号码，需要权限&lt;uses-permission
	 * android:name="android.permission.CALL_PHONE"></uses-permission>
	 * 
	 * @param phoneNumber
	 *            电话号码
	 * @param isMobilePhone
	 *            是否是移动电话，true会进行正则表达式进行验证,false则不进行验证
	 */
	public void callPhone(String phoneNumber, boolean isMobilePhone) {
		if (TextUtils.isEmpty(phoneNumber))
			return;
		if (isMobilePhone
				&& RegularExpressionUtils.isMatcher(
						RegularExpressionUtils.PHONE, phoneNumber) == false)
			return;
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phoneNumber));
		mContext.startActivity(intent);

	}

	/**
	 * 从sd卡里面安装apk文件
	 * 
	 * @param apkPath
	 */
	public void installApkFromSDCard(String apkPath) {
		installApk(Uri.fromFile(new File(apkPath)));
	}

	/**
	 * 安装apk文件
	 * 
	 * @param apkUri
	 */
	public void installApk(Uri apkUri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}
