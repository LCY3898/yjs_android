package com.cy.yigym.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.base.FragmentViewPagerAdapter;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomViewPager;
import com.cy.yigym.aty.AtyLiveAdvance;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventNaviToFrag;
import com.efit.sport.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by eijianshen on 15/9/14.
 */
public class FragmentLive extends BaseFragment implements View.OnClickListener {

	@BindView
	private CustomTitleView liveTitle;

	@BindView
	private CustomViewPager view_page_live;

	@BindView
	private View vBarLine1;

	@BindView
	private View vBarLine2;

	@BindView
	private View rlLiveCast;

	@BindView
	private View rlCourse;

	private ArrayList<Fragment> fragmentList;
	private FragmentViewPagerAdapter fragmentViewPagerAdapter;

	@BindView
	private TextView tvLiveBar;

	@BindView
	private TextView tvPreCourseBar;

	private Fragment fragLive;
	private Fragment fragPreCourse;

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
		liveTitle.setTitle("直播");
		liveTitle.setTxtRightIcon(R.drawable.header_menu);
		liveTitle.setTxtRightClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AtyLiveAdvance.class));
			}
		});
		rlLiveCast.setOnClickListener(this);
		rlCourse.setOnClickListener(this);
		fragmentList = new ArrayList<Fragment>();
		fragLive = new FragmentOnAir();
		fragPreCourse = new FragmentPreCourse();
		fragmentList.add(fragLive);
		fragmentList.add(fragPreCourse);
		fragmentViewPagerAdapter = new FragmentViewPagerAdapter(
				super.getChildFragmentManager(), fragmentList);
		view_page_live.setAdapter(fragmentViewPagerAdapter);
		view_page_live.setCurrentItem(0);
		view_page_live.setOffscreenPageLimit(2);
		view_page_live.setSelected(true);
		view_page_live.setSlideEnable(true);
		vBarLine1.setVisibility(View.VISIBLE);
		rlLiveCast.setSelected(true);

		view_page_live
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {

					}

					@Override
					public void onPageSelected(int position) {
						switch (position) {
						case 0:
							vBarLine1.setVisibility(View.VISIBLE);
							vBarLine2.setVisibility(View.INVISIBLE);
							break;
						case 1:
							vBarLine1.setVisibility(View.INVISIBLE);
							vBarLine2.setVisibility(View.VISIBLE);
							break;
						default:
							break;
						}
						rlLiveCast.setSelected(position == 0);
						rlCourse.setSelected(position == 1);
					}

					@Override
					public void onPageScrollStateChanged(int state) {

					}
				});

	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		EventBus.getDefault().register(naviListener);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(naviListener);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlLiveCast:
			vBarLine1.setVisibility(View.VISIBLE);
			vBarLine2.setVisibility(View.GONE);
			break;
		case R.id.rlCourse:
			vBarLine2.setVisibility(View.VISIBLE);
			vBarLine1.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		rlLiveCast.setSelected(v == rlLiveCast);
		rlLiveCast.setSelected(v == rlCourse);
		view_page_live.setCurrentItem(v == rlLiveCast ? 0 : 1);
	}

	private BusEventListener.MainThreadListener naviListener = new BusEventListener.MainThreadListener<EventNaviToFrag>() {
		@Override
		public void onEventMainThread(EventNaviToFrag event) {
			String fragName = event.fragCls;
			if (fragName == null) {
				return;
			}
			if (fragName.equals(fragLive.getClass().getName())) {
				view_page_live.setCurrentItem(0);
			} else if (fragName.equals(fragPreCourse.getClass().getName())) {
				view_page_live.setCurrentItem(1);
			}
		}
	};
}
