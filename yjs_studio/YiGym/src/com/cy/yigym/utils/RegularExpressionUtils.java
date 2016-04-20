package com.cy.yigym.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-2
 * </p>
 * <p>
 * 正则表达式工具类
 * </p>
 */
public class RegularExpressionUtils {
	/**
	 * 双精度浮点型
	 */
	public static final String DOUBLE = "^[-\\+]?\\d+(\\.\\d+)?$";
	/**
	 * 手机号码
	 */
	public static final String PHONE = "^((13[0-9])|(15[^4\\D])|(18[0-9])|(17[0-9]))\\d{8}$";
	/**
	 * 邮箱地址
	 */
	public static final String EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	/**
	 * 函数，eg:函数名(参数1,参数2,参数3,...,参数n),参数和函数名不能出现,()及四则运算字符
	 */
	public static final String FUNCTION = "[^,\\(\\)\\s\\+\\-*/]+\\(([^,\\(\\)\\s\\+\\-*/]+,)*([^,\\(\\)\\s\\+\\-*/]+)+\\)";
	/**
	 * 数字
	 */
	public static final String NUMBER = "^[0-9]*$";

	/**
	 * 是否匹配
	 * 
	 * @param regularExpression
	 *            正则表达式
	 * @param resString
	 *            原文
	 * @return 若匹配则返回true,否则返回false
	 */
	public static boolean isMatcher(String regularExpression, String resString) {
		if (TextUtils.isEmpty(regularExpression)
				|| TextUtils.isEmpty(resString))
			return false;
		Pattern pattern = Pattern.compile(regularExpression);
		Matcher matcher = pattern.matcher(resString);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 获取组
	 * 
	 * @param regularExpression
	 *            正则表达式
	 * @param resString
	 *            原文
	 * @return 符合规则的组,[分组索引][对应分组索引中的所有记录]
	 */
	public static ArrayList<ArrayList<GroupEntity>> getGroups(
			String regularExpression, String resString) {
		ArrayList<ArrayList<GroupEntity>> groups = new ArrayList<ArrayList<GroupEntity>>();
		try {
			Pattern pattern = Pattern.compile(regularExpression);
			Matcher matcher = pattern.matcher(resString);
			int groupCount = matcher.groupCount();
			for (int i = 0; i < groupCount; i++) {
				ArrayList<GroupEntity> group = new ArrayList<GroupEntity>();
				groups.add(group);
			}
			while (matcher.find()) {
				// 这里循环一次,表示匹配整个正则表达式一次,然后从匹配的结果中获取分组数据
				// groups.get(0)表示符合整个正则表达式,分组捕获从1开始
				for (int i = 0; i < groupCount; i++) {
					GroupEntity entity = new GroupEntity();
					entity.content = matcher.group(i + 1);
					entity.start = matcher.start(i + 1);
					entity.end = matcher.end(i + 1);
					groups.get(i).add(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groups;
	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-8-5
	 * </p>
	 * <p>
	 * 分组实体
	 * </p>
	 */
	public static class GroupEntity {
		public String content = "";// 内容
		public int start = 0;// 在原文中的起始坐标
		public int end = 0;// 在原文中的结束坐标
	}
}
