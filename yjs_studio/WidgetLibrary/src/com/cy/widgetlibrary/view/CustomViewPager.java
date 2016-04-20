package com.cy.widgetlibrary.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Caiyuan Huang
 * <p>
 * 2015-3-18
 * </p>
 * <p>
 * 自定义ViewPager，可以控制是否允许其滑动
 * </p>
 */
public class CustomViewPager extends ViewPager {
	private boolean slideEnable = true;

	public CustomViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {

		return slideEnable ? super.onTouchEvent(arg0) : false;

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return slideEnable ? super.onInterceptTouchEvent(arg0) : false;
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSlideEnable(boolean slideEnable) {
		this.slideEnable = slideEnable;
	}

}
