package com.cy.yigym.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * tangtt
 * <p>
 * 2015-11-21
 * </p>
 * <p>
 * 自定义高度限定的ListView
 * </p>
 */
public class CustomHeightLimitListView extends ListView {

	/**
	 * listview最大高度
	 */
	private int maxListHeight = 0;

	public int getMaxListHeight() {
		return maxListHeight;
	}

	public void setMaxHeight(int height) {
		this.maxListHeight = height;
	}

	public CustomHeightLimitListView(Context context) {
		super(context);
	}

	public CustomHeightLimitListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomHeightLimitListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		if (maxListHeight > 0) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxListHeight,
					MeasureSpec.AT_MOST);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
