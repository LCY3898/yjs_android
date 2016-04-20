package com.cy.yigym.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.aty.AtyLogin;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.CustomAutoSildeViewPager;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-12-27
 * </p>
 * <p>
 * 首页引导页
 * </p>
 */
public class FragmentFirstGuide extends BaseFragment {
	@BindView
	private CustomAutoSildeViewPager vGuidePager;

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_first_guide;
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		vGuidePager.setDotParams(R.drawable.selector_guide_dot, 9, 10);

	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		ArrayList<View> itemViews = new ArrayList<View>();
		itemViews.add(createImageViewItem(R.drawable.gui_4));
		itemViews.add(createImageViewItem(R.drawable.gui_6));
		View item3 = mInflater.inflate(R.layout.view_guide_enter_yjs, null,
				false);
		item3.findViewById(R.id.btEnterYjs).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						DataStorageUtils.setGuideFlag(true);
						mActivity.startActivity(AtyLogin.class);
						finish();

					}
				});
		itemViews.add(item3);
		vGuidePager.setItemViews(itemViews);
		vGuidePager.setDotsVisibility(View.VISIBLE);
		vGuidePager.setDotsGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		vGuidePager.setDotsMargins(0, 0, 0, 70);
	}

	private ImageView createImageViewItem(int imgId) {
		ImageView item = new ImageView(mActivity);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		item.setScaleType(ScaleType.CENTER_CROP);
		item.setImageResource(imgId);
		item.setLayoutParams(lp);
		return item;
	}

}
