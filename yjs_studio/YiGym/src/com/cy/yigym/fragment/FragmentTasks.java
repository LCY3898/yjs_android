package com.cy.yigym.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.base.FragmentViewPagerAdapter;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomViewPager;
import com.cy.yigym.aty.AtyCreatTask;
import com.efit.sport.R;
import com.efit.sport.livecast.AtyLiveCast;

import java.util.ArrayList;

/**
 * Created by ejianshen on 15/7/16.
 */
public class FragmentTasks extends BaseFragment {
	@BindView
	private CustomTitleView TaskTitle;
	private Context context;
	private LayoutInflater inflater;
	@BindView
	private View rlLiveCast;
	@BindView
	private View rlCourse;
	private ArrayList<Fragment> fragmentList;
	private FragmentViewPagerAdapter fragmentViewPagerAdapter;
	// viewpage
	@BindView
	private CustomViewPager view_page_task;
	@BindView
	private Button btLiveCast;
	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_live;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		TaskTitle.setTitle("直播");
		TaskTitle.setTxtRightIcon(R.drawable.icon_check);
		TaskTitle.setTxtRightClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AtyCreatTask.class));
			}
		});
		btLiveCast.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AtyLiveCast.class));
			}
		});
	}

	@Override
	protected void initData(Bundle savedInstanceState) {

	}
}
