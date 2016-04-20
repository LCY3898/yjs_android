package com.cy.widgetlibrary.content;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-2
 * </p>
 * <p>
 * 全部展开的ListView
 * </p>
 */
public class CustomAllExpendListView extends ListView {
	public CustomAllExpendListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomAllExpendListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
