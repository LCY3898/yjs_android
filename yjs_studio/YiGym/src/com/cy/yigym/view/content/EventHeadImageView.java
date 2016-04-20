package com.cy.yigym.view.content;

import android.content.Context;
import android.util.AttributeSet;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.event.EventUpdateHead;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;

import de.greenrobot.event.EventBus;

/**
 * Caiyuan Huang
 * <p>
 * 2015-4-8
 * </p>
 * <p>
 * 能接受头像变更通知并自动更新头像的圆形图片控件
 * </p>
 */
public class EventHeadImageView extends CustomCircleImageView {
	public EventHeadImageView(Context context) {
		super(context);
		init(context);
	}

	public EventHeadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.setScaleType(ScaleType.CENTER_CROP);
		register();
		// 加载默认头像
		HeaderHelper.loadSelf(this,0);
	}

	/**
	 * 注册刷新头像事件
	 */
	@SuppressWarnings("deprecation")
	public void register() {
		EventBus.getDefault().register(this, "onEventUpdateHead",
				EventUpdateHead.class);
	}

	/**
	 * 通知所有EventHeadImageView刷新头像
	 * 
	 * @param headUrl
	 */
	public void notifyUpdateHead(String headUrl) {
		EventBus.getDefault().post(new EventUpdateHead(headUrl));
	}

	/**
	 * 在销毁时反注册更新头像事件，防止内存泄漏
	 */
	public void unRegister() {
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 更新头像
	 * 
	 * @param updateHead
	 */
	public void onEventUpdateHead(EventUpdateHead updateHead) {
		ImageLoaderUtils.getInstance().loadImage(updateHead.getHeadUrl(), this);
	}

}
