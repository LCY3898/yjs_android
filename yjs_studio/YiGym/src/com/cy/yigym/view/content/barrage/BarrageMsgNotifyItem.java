package com.cy.yigym.view.content.barrage;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-3
 * </p>
 * <p>
 * 聊天弹幕之消息提醒条目控件
 * </p>
 */
public class BarrageMsgNotifyItem extends BaseView {
	@BindView
	private CustomCircleImageView imgHead;
	@BindView
	private TextView txtNickName, txtMsg;

	public BarrageMsgNotifyItem(Context context) {
		super(context);
	}

	public BarrageMsgNotifyItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initView() {
		GradientDrawable bg = BgDrawableUtils.creShape(0xffab47bc, 5);
		this.setBackground(bg);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.item_im_barrage_msg_notify;
	}

	/**
	 * 设置头像
	 * 
	 * @param headUrl
	 */
	public void setHead(String headUrl) {
		ImageLoaderUtils.getInstance().loadImage(headUrl, imgHead);
	}

	/**
	 * 设置昵称
	 * 
	 * @param nickName
	 */
	public void setNickName(String nickName) {
		txtNickName.setText(nickName);
	}

	/**
	 * 设置内容
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		txtMsg.setText(content);
	}

}
