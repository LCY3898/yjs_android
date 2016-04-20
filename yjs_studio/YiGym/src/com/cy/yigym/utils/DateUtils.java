package com.cy.yigym.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Caiyuan Huang
 * <p>
 * 2014-10-28下午9:59:52
 * </p>
 * <p>
 * 日期工具类
 * </p>
 */
public class DateUtils {

	/**
	 * 获取当前时间
	 * <p>
	 * 2014-10-28下午10:01:40
	 * </p>
	 * 
	 * @param mDateFormat
	 *            日期格式化字符串(eg:yyyyMMddhhmmss，12小时制为hh，24小时制为HH)
	 * @return
	 */
	public static String getCurDateTime(SimpleDateFormat mDateFormat) {
		String date = mDateFormat.format(getCurDate());
		return date;
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static Date getCurDate() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 
	 * 获取当前年份
	 * 
	 * @return
	 */
	public static int getCurYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * 
	 * 获取当前月份
	 * 
	 * @return
	 */
	public static int getCurMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 
	 * 获取当前是几号
	 * 
	 * @return
	 */
	public static int getCurDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
}
