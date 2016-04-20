package com.hhtech.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

/**
 * @author tangtt
 * global context access
 *
 */
public class AppUtils {

	private static Context sContext;
	private static Thread mUiThread;
	
	private static Handler sHandler = new Handler(Looper.getMainLooper());
	
	/**
	 * 在Application的onCreate方法中调用
	 * @param context
	 */
	public static void init(Context context) {
		sContext = context;
		mUiThread = Thread.currentThread();
	}

	public static boolean isMainThread() {
		return Looper.getMainLooper() == Looper.myLooper();
	}
	
	public static Context getAppContext() {
		return sContext;
	}

	public static AssetManager getAssets() {
		return sContext.getAssets();
	}
	
	public static Resources getResource() {
		return sContext.getResources();
	}
	
	public static boolean isUIThread() {
		return Thread.currentThread() == mUiThread;
	}
	
	public static void runOnUI(Runnable r) {
		sHandler.post(r);
	}

	public static void runOnUIDelayed(long delayMills,Runnable r) {
		sHandler.postDelayed(r, delayMills);
	}

	public static void runOnUIDelayed(Runnable r,long delayMills) {
		sHandler.postDelayed(r,delayMills);
	}

	public static void removeRunOnUI(Runnable r) {
		if(r == null) {
			sHandler.removeCallbacksAndMessages(null);
		} else {
			sHandler.removeCallbacks(r);
		}
	}
}
