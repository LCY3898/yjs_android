package com.cy.yigym.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-11
 * </p>
 * <p>
 * 自定义可以旋转的FrameLayout
 * </p>
 */
public class CustomRotateFrameLayout extends FrameLayout {
	private int rotateAngle = -25;

	public CustomRotateFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomRotateFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.rotate(rotateAngle, getMeasuredWidth() / 2,
				getMeasuredHeight() / 2);
		super.dispatchDraw(canvas);
	}

	/**
	 * 设置旋转角度
	 * 
	 * @param angle
	 */
	public void setRotateAngle(int angle) {
		this.rotateAngle = angle;
		postInvalidate();
	}

}
