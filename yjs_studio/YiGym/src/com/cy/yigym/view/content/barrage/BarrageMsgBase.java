package com.cy.yigym.view.content.barrage;

import android.os.Handler;
import android.os.Looper;

public class BarrageMsgBase implements Cloneable {
	private long lifeCycleMillis = 5000;// 生命周期
	private String tag;// 标记
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private OnDeathListener onDeathListener;

	/**
	 * 当元素被添加到队列时,该方法被执行,且开始自己的生命周期
	 */
	public void attach() {
		mHandler.postDelayed(runnable, lifeCycleMillis);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			death(true);
		}
	};

	/**
	 * 元素死亡监听器
	 */
	public static interface OnDeathListener {
		/**
		 * 当元素到达指定的生命周期时,该方法被执行
		 */
		void onDeath();
	}

	/**
	 * 设置死亡监听器
	 * 
	 * @param l
	 */
	public void setOnDeathListener(OnDeathListener l) {
		this.onDeathListener = l;
	}

	/**
	 * 元素死亡
	 * 
	 * @param isNotify
	 *            是否通知队列,若为true则会回调OnDeathListener.onDeath()方法
	 */
	public void death(boolean isNotifyToQueue) {
		if (isNotifyToQueue && onDeathListener != null)
			onDeathListener.onDeath();
		mHandler.removeCallbacks(runnable);
		mHandler = null;
	}

	/**
	 * 设置生命周期毫秒数
	 * 
	 * @param lifeCycleMillis
	 */
	public void setLifeCycleMillis(long lifeCycleMillis) {
		if (lifeCycleMillis <= 0)
			return;
		this.lifeCycleMillis = lifeCycleMillis;
	}

	/**
	 * 获取标签
	 * 
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 设置标签
	 * 
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	protected BarrageMsgBase clone() {
		try {
			return (BarrageMsgBase) super.clone();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
