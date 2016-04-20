package com.cy.yigym.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.PreCourseAdapter;
import com.cy.yigym.entity.DropDownMenuEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqPreCourse;
import com.cy.yigym.net.rsp.RspPreCourse;
import com.cy.yigym.net.rsp.RspPreCourse.PreCourse;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.SpinerMenuView;
import com.efit.sport.R;
import com.efit.sport.livecast.AtyLiveCast;
import com.efit.sport.livecast.LiveCastHelper;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshListView;
import com.hhtech.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eijianshen on 15/9/15.
 */
public class FragmentPreCourse extends BaseFragment implements
		PullToRefreshBase.OnRefreshListener2<ListView>,
		AdapterView.OnItemClickListener,
		SpinerMenuView.OnSpinerMenuItemClickListener {
	@BindView
	private SpinerMenuView courseType;
	@BindView
	private SpinerMenuView courseCoach;
	@BindView
	private SpinerMenuView courseCal;
	@BindView
	private SpinerMenuView courseTime;
	@BindView
	private PullToRefreshListView preCourseListView;
	@BindView
	private RelativeLayout rlNoFind; // 当找不到符合条件的往期课程的时候该布局可见
	@BindView
	private TextView tvNoNet; // 当没有网络的时候tvNoNet可见
	@BindView
	private TextView tvNoPreCourse;// 当没有往期课程的时候tvNoPreCourse可见
	private ArrayList<PreCourse> preCourseList = new ArrayList<PreCourse>();
	private PreCourseAdapter adapter;
	private boolean isCanPullUp = true;
	// 往期课程列表请求相关
	private String type = null;
	private String coach = null;
	private int calrie = -1;
	private int duration = -1;
	private static final String COURSE_TYPE = "课程类型";
	private static final String COURSE_COACH = "教练";
	private static final String COURSE_CALRIE = "卡路里";
	private static final String COURSE_DURATION = "时间";
	private Boolean isNotInitMenu = true;// 是否是没有初始化菜单
	// 当前加载的页数
	private int pageNum = 1;
	// 每页加载的条数
	private int pageSize = 5;

	private String STATUS = "end";

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_pre_course;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		adapter = new PreCourseAdapter(mActivity, preCourseList);
		preCourseListView.setAdapter(adapter);
		preCourseListView.setOnItemClickListener(FragmentPreCourse.this);
		preCourseListView.setOnRefreshListener(this);
		preCourseListView.setMode(PullToRefreshBase.Mode.BOTH);
		fetchData();
	}

	/**
	 * 初始化下拉选择菜单的值
	 * 
	 * @param coach
	 */
	private void initSpinnerMenu(String[] coach) {
		// 课程类型
		List<String> listType = new ArrayList<String>();
		for (int i = 0; i < DropDownMenuEntity.courseType.length; i++) {
			listType.add(DropDownMenuEntity.courseType[i]);
		}
		courseType.setTvMenuText(COURSE_TYPE);
		courseType.setMenuItemData(listType);
		courseType.setOnSpinerMenuItemClickListener(this);
		// 教练，列表值从服务端获得
		List<String> listCoach = new ArrayList<String>();
		listCoach.add(DropDownMenuEntity.courseCoach[0]);
		for (int i = 0; coach != null && i < coach.length; i++) {
			listCoach.add(coach[i]);
		}
		courseCoach.setTvMenuText(COURSE_COACH);
		courseCoach.setMenuItemData(listCoach);
		courseCoach.setOnSpinerMenuItemClickListener(this);
		// 卡路里
		List<String> listCal = new ArrayList<String>();
		for (int i = 0; i < DropDownMenuEntity.courseCalrie.length; i++) {
			listCal.add(DropDownMenuEntity.courseCalrie[i]);
		}
		courseCal.setTvMenuText(COURSE_CALRIE);
		courseCal.setMenuItemData(listCal);
		courseCal.setOnSpinerMenuItemClickListener(this);
		// 时长
		List<String> listTime = new ArrayList<String>();
		for (int i = 0; i < DropDownMenuEntity.courseTime.length; i++) {
			listTime.add(DropDownMenuEntity.courseTime[i]);
		}
		courseTime.setTvMenuText(COURSE_DURATION);
		courseTime.setMenuItemData(listTime);
		courseTime.setOnSpinerMenuItemClickListener(this);
	}

	/**
	 * 获取往期课程列表数据
	 */
	private void fetchData() {
		showDataView();
		YJSNet.send(new ReqPreCourse(type, coach, calrie, duration, pageNum,
				pageSize, STATUS), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspPreCourse>() {
					@Override
					public void onSuccess(RspPreCourse data) {
						refreshListFinish();
						// 当教练数据存在并且下拉菜单没有初始化时，进行菜单初始化
						if (data.data.coach_list != null
								&& data.data.coach_list.length > 0
								&& isNotInitMenu) {
							isNotInitMenu = false;
							initSpinnerMenu(data.data.coach_list);
							if (data.data.course_list == null
									|| data.data.course_list.size() <= 0) {
								showNoPreCourse();
							}
						}
						// 没有数据的时候表示不可上拉了
						if (data.data.course_list == null
								|| data.data.course_list.size() <= 0) {
							isCanPullUp = false;
						} else {
							preCourseList.addAll(data.data.course_list);
							adapter.notifyDataSetChanged();
						}
						if (preCourseList.size() == 0) {
							showNoDataTips();
						}

					}

					@Override
					public void onFailure(String errorMsg) {
						refreshListFinish();
						WidgetUtils.showToast(errorMsg);
					}
				});
	}

	/**
	 * 获取下拉菜单控件的值
	 */
	private void getMenuValue() {
		try {
			// 获取课程类型下拉列表表头的数据，如果表头数据为‘课程类型’，type设为空，否则等于表头数据，即初级课程/中级课程/高级课程
			if (courseType.getTvMenuText().getText().equals(COURSE_TYPE)) {
				type = null;
			} else {
				type = courseType.getTvMenuText().getText().toString().trim();
			}
			// 获取教练下拉列表表头的数据，如果表头数据为‘教练’，coach设为空，否则等于表头数据，即教练名字
			if (courseCoach.getTvMenuText().getText().equals(COURSE_COACH)) {
				coach = null;
			} else {
				coach = courseCoach.getTvMenuText().getText().toString().trim();
			}
			// 获取卡路里下拉列表表头的数据，如果表头数据为‘卡路里’，calrie设为空，否则等于表头数据，即0/300/500
			if (courseCal.getTvMenuText().getText().equals(COURSE_CALRIE)) {
				calrie = -1;
			} else {
				String sp[];
				if (courseCal
						.getTvMenuText()
						.getText()
						.toString()
						.equals(DropDownMenuEntity.courseCalrie[DropDownMenuEntity.courseCalrie.length - 1])) {
					sp = courseCal.getTvMenuText().getText().toString().trim()
							.split("k");
				} else {
					sp = courseCal.getTvMenuText().getText().toString().trim()
							.split("~");
				}
				calrie = Integer.parseInt(sp[0]);
			}
			// 获取时间下拉列表表头的数据，如果表头数据为‘时间’，duration设为空，否则等于表头数据，即0/30/60
			if (courseTime.getTvMenuText().getText().equals(COURSE_DURATION)) {
				duration = -1;
			} else {
				String sp[];
				if (courseTime
						.getTvMenuText()
						.getText()
						.toString()
						.equals(DropDownMenuEntity.courseTime[DropDownMenuEntity.courseTime.length - 1])) {
					sp = courseTime.getTvMenuText().getText().toString().trim()
							.split("min");
				} else {
					sp = courseTime.getTvMenuText().getText().toString().trim()
							.split("~");
				}
				duration = Integer.parseInt(sp[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshListFinish() {
		preCourseListView.onRefreshComplete();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		reset();
		if (!NetUtils.isNetConnected(mActivity)) {
			showNoNetTips();
			return;
		}
		fetchData();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		if (!NetUtils.isNetConnected(mActivity)) {
			showNoNetTips();
			return;
		}
		if (isCanPullUp) {
			pageNum++;
			fetchData();
		} else {
			WidgetUtils.showToast("没有更多课程了");
			refreshView.postDelayed(new Runnable() {

				@Override
				public void run() {
					refreshListFinish();
				}
			}, 1000);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PreCourse preCourse = preCourseList.get(position - 1);
		String videoUrl = DataStorageUtils.getMp4Url(preCourse.video_link);
		if (TextUtils.isEmpty(videoUrl)) {
			WidgetUtils.showToast("该课程视频无法播放");
			return;
		}

		Intent intent = new Intent(mActivity, AtyLiveCast.class);
		LiveCastHelper.saveVideoInfo(intent, LiveCastHelper.genVideoInfo(
				preCourse._id, preCourse.coach_name, preCourse.course_name,
				preCourse.course_time, preCourse.video_link,
				preCourse.course_fid));
		startActivity(intent);
	}

	/**
	 * 监听四个下拉选择菜单是否变化
	 */
	@Override
	public void spinerMenuItemClicked() {
		refreshListFinish();
		getMenuValue();
		reset();
		if (!NetUtils.isNetConnected(mActivity)) {
			showNoNetTips();
		} else {
			fetchData();
		}
	}

	/**
	 * 显示没有网络的提示
	 */
	private void showNoNetTips() {
		tvNoNet.setVisibility(View.VISIBLE);
		tvNoPreCourse.setVisibility(View.GONE);
		rlNoFind.setVisibility(View.GONE);
		preCourseListView.setVisibility(View.GONE);
	}

	/**
	 * 显示没有数据的提示
	 */
	private void showNoDataTips() {
		tvNoPreCourse.setVisibility(View.GONE);
		rlNoFind.setVisibility(View.VISIBLE);
		tvNoNet.setVisibility(View.GONE);
		preCourseListView.setVisibility(View.GONE);
	}

	/**
	 * 现实没有往期课程的提示
	 */
	private void showNoPreCourse() {
		tvNoPreCourse.setVisibility(View.VISIBLE);
		rlNoFind.setVisibility(View.GONE);
		tvNoNet.setVisibility(View.GONE);
		preCourseListView.setVisibility(View.GONE);
	}

	/**
	 * 显示数据控件
	 */
	private void showDataView() {
		rlNoFind.setVisibility(View.GONE);
		tvNoPreCourse.setVisibility(View.GONE);
		tvNoNet.setVisibility(View.GONE);
		preCourseListView.setVisibility(View.VISIBLE);
	}

	/**
	 * 重置数据列表及页码等相关标记
	 */
	private void reset() {
		isCanPullUp = true;
		pageNum = 1;
		preCourseList.clear();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
