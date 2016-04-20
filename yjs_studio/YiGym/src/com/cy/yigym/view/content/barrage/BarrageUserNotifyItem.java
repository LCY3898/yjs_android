package com.cy.yigym.view.content.barrage;

import com.cy.widgetlibrary.utils.BgDrawableUtils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-3
 * </p>
 * <p>
 * 聊天弹幕之用户提醒条目控件
 * </p>
 */
public class BarrageUserNotifyItem extends TextView {
	public BarrageUserNotifyItem(Context context) {
		super(context);
		init();
	}

	public BarrageUserNotifyItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		GradientDrawable bg = BgDrawableUtils.creShape(0xffab47bc, 5);
		this.setBackground(bg);
	}

	/**
	 * 设置提醒内容
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		this.setText(content);
	}

}
