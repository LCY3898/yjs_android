package com.cy.yigym.view.content.live;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-9
 * </p>
 * <p>
 * 热门课程列表项
 * </p>
 */
public class ItemHotCourse extends BaseView {
	@BindView
	public ImageView ivCover;
	@BindView
	public TextView tvTitle;
	@BindView
	public TextView tvName;

	public ItemHotCourse(Context context) {
		super(context);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView() {
		/*int dp1 = WidgetUtils.dpToPx(1);
		setPadding(dp1, dp1, dp1, 0);
		GradientDrawable bg = BgDrawableUtils.creShape(Color.TRANSPARENT, 2, 1,
				0x20878787, null);
		setBackgroundDrawable(bg);*/
	}

	@Override
	protected int getContentViewId() {
		return R.layout.item_hot_course;
	}

}
