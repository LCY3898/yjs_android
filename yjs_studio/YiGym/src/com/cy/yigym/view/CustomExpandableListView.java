package com.cy.yigym.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-28
 * </p>
 * <p>
 * 自定义ExpandableListView,用于展示成长旅程的时间轴
 * </p>
 */
public class CustomExpandableListView extends ExpandableListView {
	public CustomExpandableListView(Context context) {
		super(context);
		init();
	}

	public CustomExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// 去除箭头
		setGroupIndicator(null);
		// 禁止收缩
		setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		super.setAdapter(adapter);
		// 初始时默认全部展开
		try {
			if (adapter != null) {
				for (int i = 0; i < adapter.getGroupCount(); i++) {
					expandGroup(i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 嵌套在ScrollView中时全部展开
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
