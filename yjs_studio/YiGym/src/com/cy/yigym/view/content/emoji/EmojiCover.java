package com.cy.yigym.view.content.emoji;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-5
 * </p>
 * <p>
 * 表情转换工具类
 * </p>
 */
public class EmojiCover {
	public static final String EXPRESS_EMOJI = "(\\[\\S{1,3}\\])";

	/**
	 * 解码
	 * 
	 * @param context
	 * @param resContent
	 * @param emojiWidthDp
	 *            图标宽度,单位为dp
	 * @param emojiHeightDp
	 *            图标高度,单位为dp
	 * @return
	 */
	public static SpannableString decode(Context context, String resContent,
			int emojiWidthDp, int emojiHeightDp) {
		if (TextUtils.isEmpty(resContent))
			return null;
		SpannableString content = new SpannableString(resContent);
		ArrayList<ArrayList<GroupEntity>> keys = getGroups(EXPRESS_EMOJI,
				resContent);
		for (int i = 0; i < keys.size(); i++) {
			ArrayList<GroupEntity> eKeys = keys.get(i);
			for (int j = 0; j < eKeys.size(); j++) {
				GroupEntity eKey = eKeys.get(j);
				Integer drawableId = EmojiHashMap.get(eKey.content);
				if (drawableId == null)
					continue;
				content.setSpan(new EmojiSpan(context, drawableId,
						emojiWidthDp, emojiHeightDp), eKey.start, eKey.end,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return content;
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
