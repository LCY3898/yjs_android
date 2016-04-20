package com.cy.yigym.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-16
 * </p>
 * <p>
 * 自定义全部展开的Gridview
 * </p>
 */
public class CustomExpandGridView extends GridView {
	public CustomExpandGridView(Context context) {
		super(context);
	}

	public CustomExpandGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
