package com.cy.yigym.view.content.barrage;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.cy.yigym.view.content.barrage.BarrageMsgQueue.OnElementRemoveListener;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-6
 * </p>
 * <p>
 * 弹幕控件
 * </p>
 */
public class BarrageView extends LinearLayout implements
		OnElementRemoveListener {
	private BarrageMsgQueue queue = null;
	private int removeAnimId = R.anim.anim_fade_out;
	private int addAnimId = R.anim.anim_fade_in;
	private int marginTop = dpToPx(20), marginRight = dpToPx(10),
			marginLeft = 0, marginBottom = 0;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	public BarrageView(Context context) {
		super(context);
		init();
	}

	public BarrageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		setBackgroundColor(Color.TRANSPARENT);
		setOrientation(LinearLayout.VERTICAL);
		queue = new BarrageMsgQueue();
		queue.setOnElementRemoveListener(this);
	}

	/**
	 * 添加消息
	 * 
	 * @param msg
	 */
	public void putMsg(BarrageMsg msg) {
		try {
			addItem(msg);
			queue.put(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onForceRemove(BarrageMsgBase element) {
		BarrageMsg b = (BarrageMsg) element;
		if (getChildCount() == 0)
			return;
		View item = null;
		for (int i = 0; i < getChildCount(); i++) {
			String tag = (String) getChildAt(i).getTag();
			String eTag = ((BarrageMsg) element).getTag();
			if (tag.equals(eTag)) {
				item = getChildAt(i);
				break;
			}
		}
		item.clearAnimation();
		removeView(item);

	}

	@Override
	public void onAutoRemove(BarrageMsgBase element) {
		BarrageMsg b = (BarrageMsg) element;
		if (getChildCount() == 0)
			return;
		View view = null;
		for (int i = 0; i < getChildCount(); i++) {
			String tag = (String) getChildAt(i).getTag();
			String eTag = ((BarrageMsg) element).getTag();
			if (tag.equals(eTag)) {
				view = getChildAt(i);
				break;
			}
		}
		if (view == null)
			return;
		removeItem(element, view);
	}

	/**
	 * 添加item
	 * 
	 * @param msg
	 */
	private void addItem(BarrageMsg msg) {
		if (msg == null)
			return;
		String tag = System.currentTimeMillis() + "";
		msg.setTag(tag);
		View item = createItem(msg);
		if (item == null)
			return;
		addView(item, 0);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(marginLeft, marginTop, marginRight, marginBottom);
		item.setLayoutParams(lp);
		item.setTag(msg.getTag());
		item.setId(msg.index);
		Animation anim = AnimationUtils.loadAnimation(getContext(), addAnimId);
		try {
			item.startAnimation(anim);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成item
	 * 
	 * @param msg
	 * @param item
	 */
	private View createItem(BarrageMsg msg) {
		View item = null;
		switch (msg.type) {
		case ERROR_MSG_NOTIFY:
			break;
		case NEW_MSG_NOTIFY:
			BarrageMsgNotifyItem msgItem = new BarrageMsgNotifyItem(
					getContext());
			msgItem.setContent(msg.msg);
			msgItem.setHead(msg.headUrl);
			msgItem.setNickName(msg.nickName);
			item = msgItem;
			break;
		case NEW_USER_NOTIFY:
			break;
		}
		return item;
	}

	/**
	 * 移除item
	 * 
	 * @param item
	 */
	private void removeItem(final BarrageMsgBase element, final View item) {
		if (item != null) {
			Animation anim = AnimationUtils.loadAnimation(getContext(),
					removeAnimId);
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							item.clearAnimation();
							removeView(item);
							BarrageMsg b = (BarrageMsg) element;

						}
					});

				}
			});
			item.startAnimation(anim);
		}

	}

	/**
	 * 设置添加item时的动画资源id
	 * 
	 * @param animId
	 */
	public void setAddItemAnimId(int animId) {
		this.addAnimId = animId;
	}

	/**
	 * 设置移除item时的动画资源id
	 * 
	 * @param animId
	 */
	public void setRemoveItemAnimId(int animId) {
		this.removeAnimId = animId;
	}

	/**
	 * 设置item与上一个item之间的距离,单位为dp
	 * 
	 * @param marginTopDp
	 */
	public void setItemMarginTop(int marginLeftDp, int marginTopDp,
			int marginRightDp, int marginBottomDp) {
		this.marginTop = dpToPx(marginTopDp);
		this.marginLeft = dpToPx(marginLeftDp);
		this.marginRight = dpToPx(marginRightDp);
		this.marginBottom = dpToPx(marginBottomDp);
	}

	/**
	 * 设置允许容纳的最多的item的条数
	 * 
	 * @param maxCount
	 */
	public void setMaxItemCount(int maxCount) {
		this.queue.setMaxQueueSize(maxCount);
	}

	/**
	 * 将dp转换成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	private int dpToPx(float dp) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static class BarrageMsg extends BarrageMsgBase {
		public String nickName = "";
		public String headUrl = "";
		public String msg = "";
		public BarrageMsgType type;
		public int index = 0;

		public BarrageMsg(BarrageMsgType type, String nickName, String headUrl,
				String msg) {
			super();
			this.nickName = nickName;
			this.headUrl = headUrl;
			this.msg = msg;
			this.type = type;
		}

		public static enum BarrageMsgType {
			NEW_USER_NOTIFY, NEW_MSG_NOTIFY, ERROR_MSG_NOTIFY
		}
	}
}
