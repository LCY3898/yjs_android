package com.hhtech.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.hhtech.base.AppUtils;

/**
 * @author tangtt
 * {{@link #DimenUtils()} intends for providing dimension utility
 * 进行长度单位dp,sp,px的相互转化 
 * */
public class DimenUtils {
	private final static Resources sResource; 
	
	static {
		sResource = AppUtils.getAppContext().getResources();
	}

	
	public static int getDisplayWidth() {
		DisplayMetrics metrics = sResource.getDisplayMetrics();
		return metrics.widthPixels;
	}
	
	public static int getDisplayHeight() {
		DisplayMetrics metrics = sResource.getDisplayMetrics();
		return metrics.heightPixels;		
	}
	
	public static int dpToPx(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				sResource.getDisplayMetrics());
	}
	
	/**
	 * 
	 * 将PX转换成Dp
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int pxToDip(float px) {
		return (int) (px / sResource.getDisplayMetrics().density + 0.5f);
	}

	/**
	 * 将dp转换成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dpToPx(float dp) {
		final float scale = sResource.getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * 
	 * 将PX转换为SP
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int pxToSp(float px) {
		return (int) (px / sResource.getDisplayMetrics().scaledDensity + 0.5f);
	}

	/**
	 * 
	 * 将SP转换为PX
	 * 
	 * @param context
	 * @param sp
	 * @return
	 */
	public static int spToPx(float sp) {
		return (int) (sp* sResource.getDisplayMetrics().scaledDensity + 0.5f);
	}
}
