package com.cy.yigym.logic.im;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-17
 * </p>
 * <p>
 * im聊天记录时间工具类
 * </p>
 */
@SuppressLint("SimpleDateFormat")
public class IMTimeUtils {
	/**
	 * 获取时间描述
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String getTimeDescFromTimeMillis(long timeMillis) {
		try {
			Date today = new Date(System.currentTimeMillis());
			Date otherDay = new Date(timeMillis);
			SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
			SimpleDateFormat mm = new SimpleDateFormat("MM");
			SimpleDateFormat dd = new SimpleDateFormat("dd");
			String year0 = yyyy.format(otherDay);
			String year1 = yyyy.format(today);
			String month0 = mm.format(otherDay);
			String month1 = mm.format(today);
			if (!year0.equals(year1) || !month0.equals(month1)) {
				return longToString(timeMillis, "yyyy-MM-dd HH:mm");
			}
			int gapDays = Integer.parseInt(dd.format(today))
					- Integer.parseInt(dd.format(otherDay));
			switch (gapDays) {
			case 0:
				// 今天
				return longToString(timeMillis, "HH:mm");
			case 1:
				// 昨天
				return "昨天 " + longToString(timeMillis, "HH:mm");
				// case 2:
				// // 前天
				// return "前天 " + longToString(timeMillis, "HH时mm分");
			default:
				return longToString(timeMillis, "yyyy-MM-dd HH:mm");

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * getTimeDescFromTimeMillis
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String getTimeDescFromTimeMillis(String timeMillis) {

		try {
			long time = 0;
			// Unix时间戳转换
			if (timeMillis.length() == 10)
				time = Long.parseLong(timeMillis) * 1000;
			else
				time = Long.parseLong(timeMillis);
			return getTimeDescFromTimeMillis(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 当前的消息距离上条消息是否大于两分钟的时间间隔
	 * 
	 * @param preTimeMillis
	 * @param curTimeMillis
	 * @return
	 */
	public static boolean isMoreThanPreMsgTimeTwoMinutes(long preTimeMillis,
			long curTimeMillis) {
		// Unix时间戳转换
		if ((preTimeMillis + "").length() == 10)
			preTimeMillis = preTimeMillis * 1000;
		if ((curTimeMillis + "").length() == 10)
			curTimeMillis = curTimeMillis * 1000;
		long gap = curTimeMillis - preTimeMillis;
		return gap > 120000;
	}

	/**
	 * 当前的消息距离上条消息是否大于两分钟的时间间隔
	 * 
	 * @param preTimeMillis
	 * @param curTimeMillis
	 * @return
	 */
	public static boolean isMoreThanPreMsgTimeTwoMinutes(String preTimeMillis,
			String curTimeMillis) {
		try {
			long pre = Long.parseLong(preTimeMillis);
			long cur = Long.parseLong(curTimeMillis);
			return isMoreThanPreMsgTimeTwoMinutes(pre, cur);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * long型转String型
	 * 
	 * @param timeMillis
	 * @param dateFormat
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String longToString(long timeMillis, String dateFormat) {
		try {
			String time = "";
			Date date = new Date(timeMillis);
			time = new SimpleDateFormat(dateFormat).format(date);
			return time;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
