package com.cy.yigym.fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.videolan.vlc.ui.ClubModeVideoActivity;
import org.videolan.vlc.ui.VlcVideoActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.wbs.NetworkUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.aty.AtyChaseMain;
import com.cy.yigym.aty.AtyChiefCoach;
import com.cy.yigym.aty.AtyLiveAdvance;
import com.cy.yigym.aty.AtyPreCourse;
import com.cy.yigym.aty.AtyTraces;
import com.cy.yigym.entity.LiveVideoEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqChiefCoachProfile;
import com.cy.yigym.net.req.ReqGetLastCourse;
import com.cy.yigym.net.req.ReqHotCourse;
import com.cy.yigym.net.req.ReqJoinLiveCast;
import com.cy.yigym.net.rsp.RspChiefCoachProfile;
import com.cy.yigym.net.rsp.RspGetLastCourse;
import com.cy.yigym.net.rsp.RspHotCourse;
import com.cy.yigym.net.rsp.RspJoinLiveCast;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;
import com.cy.yigym.utils.StartThirdPartyAppUtils;
import com.cy.yigym.view.content.DlgLiveNoStart;
import com.cy.yigym.view.content.live.ItemHotCourse;
import com.efit.sport.R;
import com.efit.sport.livecast.AtyLiveCast;
import com.efit.sport.livecast.LiveCastHelper;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.hhtech.pulltorefresh.PullToRefreshScrollView;
import com.hhtech.utils.UITimer;

/**
 * Created by eijianshen on 15/9/14.
 */
public class FragmentOnAir extends BaseFragment implements View.OnClickListener {

	@BindView
	private ImageView courseImage; // 课程简介图片
	@BindView
	private TextView tvCourseTitle;// 课程标题
	@BindView
	private TextView tvCourseTitle2;// 课程标题
	@BindView
	private TextView tvCoach;// 教练
	@BindView
	private TextView tvCourseJoinNum;// 参加直播人数
	@BindView
	private Button btnEnterLive; // 点击按钮进入直播
	@BindView
	private LinearLayout rlNoStartPrompt;// 包含倒计时textView的RelativeLayout
	@BindView
	private TextView tvCountDown;//
	@BindView
	private FrameLayout flCourseMsg; // 包含课程简介图片、开始图标以及倒计时的FrameLayout
	@BindView
	private View rlStartPrompt;// 直播开始、进入直播的提示
	@BindView
	private TextView tvCoursePreShow;// 预告课程
	@BindView
	private TextView tvOldCourse; // 往期课程
	@BindView
	private TextView tvMyFootprint; // 我的足迹
	private boolean isLiveStart = false;
	@BindView
	private TextView tvBeginTime; // 直播开始时间

	@BindView
	private TextView tvCastingHint; // 正在直播提示

	private UITimer countDownTimer = new UITimer();
	/**
	 * 来自服务器开始时间
	 */
	private long beginTime;
	/**
	 * 来自服务器结束时间
	 */
	private long endTime;
	private long currentTime;
	/**
	 * 开始时间
	 */
	private String timeBegin;
	/**
	 * 结束时间
	 */
	private String timeNow;
	private Date bTime = new Date();
	private Date nTime = new Date();
	/**
	 * 加入直播的时间
	 */
	private long joinTime;
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
	private int currenthour, currentMin, currentSec;
	/**
	 * 课程ID
	 */
	private String courseId;
	/***/
	private String courseFid;
	private long startTime;
	private long day;
	private long hour;
	private long min;
	private long s;
	private DecimalFormat df = new DecimalFormat("00");
	private DlgLiveNoStart dlgLiveNoStart;
	private Bitmap coursebitmap;
	@BindView
	private GridView gvHotCourse;
	private int hotCourseItemWidth = 1, hotCourseItemHeight;// 热门课程列表项的宽度
	private HotCourseListAdapter adapter;
	@BindView
	private RelativeLayout rlChiefCoach;// 包含主教头像的relativeLayout
	@BindView
	private CustomCircleImageView chiefcoachHead; // 主教头像
	@BindView
	private CustomCircleImageView chiefcoachHead1; // 主教头像
	@BindView
	private CustomCircleImageView chiefcoachHead2; // 主教头像
	@BindView
	private PullToRefreshScrollView ptrRefresh;
	@BindView
	private TextView tvLiveCoach;
	/**
	 * 直播地址
	 */
	private String liveUrl = "";
	private ArrayList<RspHotCourse.HotCourse> hotCourseArrayList = new ArrayList<RspHotCourse.HotCourse>();
	private Boolean isLiveEnd = false;// 判断直播是否结束,默认为false
	private LinearLayout llNotNetWarning;
	@BindView
	private LinearLayout linJoin;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_onair;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		showNotNetTips();
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		btnEnterLive.setOnClickListener(this);
		dlgLiveNoStart = new DlgLiveNoStart(mActivity);
		// 热热身按钮点击时前往挑战主页，同时弹窗消失
		dlgLiveNoStart.getBtnWarmUp().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlgLiveNoStart.dismiss();
						// EventBus.getDefault().post(
						// new EventNaviToFrag(FragmentSports.class));
						mActivity.startActivity(AtyChaseMain.class);
					}
				});
		// 按钮点击时前往往期课程，同时弹窗消失
		dlgLiveNoStart.getBtnLookPreCourse().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlgLiveNoStart.dismiss();
						// EventBus.getDefault().post(
						// new EventNaviToFrag(FragmentPreCourse.class));
						mActivity.startActivity(AtyPreCourse.class);
					}
				});
		tvCoursePreShow.setOnClickListener(this);
		tvOldCourse.setOnClickListener(this);
		tvMyFootprint.setOnClickListener(this);
		rlChiefCoach.setOnClickListener(this);
		rlNoStartPrompt.setOnClickListener(this);
		ptrRefresh.setOnRefreshListener(onRefreshListener);
		llNotNetWarning = findViewById(R.id.llNotNetWarning);
		llNotNetWarning.setOnClickListener(this);
		showNotNetTips();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		hotCourseItemWidth = (int) (WidgetUtils.getScreenWidth() / 2.0);
		hotCourseItemHeight = (int) (hotCourseItemWidth / 3.0 * 2);
		updateHotCourseList(hotCourseArrayList);
		getChiefCoachprifile();// 获取今日主教头像
		getHotCourse();// 获取热门课程列表
		getLastCourse();// 获取直播课程信息
	}

	/**
	 * 获取热门课程列表
	 */
	public void getHotCourse() {
		YJSNet.send(new ReqHotCourse(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspHotCourse>() {
					@Override
					public void onSuccess(RspHotCourse data) {
						if (data.data == null || data.data.size() == 0) {
							return;
						}
						hotCourseArrayList.clear();
						hotCourseArrayList.addAll(data.data);
						updateHotCourseList(hotCourseArrayList);
					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	/**
	 * 获取直播课程信息
	 */
	private void getLastCourse() {
		YJSNet.send(new ReqGetLastCourse(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGetLastCourse>() {
					@Override
					public void onSuccess(RspGetLastCourse data) {
						RspGetLastCourse.Data data1 = data.data;
						if (TextUtils.isEmpty(data1.lastCourse._id)) {
							btnEnterLive.setText("暂无直播");
							btnEnterLive.setEnabled(false);
							courseImage.setImageResource(R.drawable.head);
							tvCastingHint.setVisibility(View.GONE);
						} else {
							btnEnterLive.setText("立即加入");
							btnEnterLive.setEnabled(true);
							tvCastingHint.setVisibility(View.VISIBLE);
						}
						tvCourseTitle.setText(data1.lastCourse.course_name);
						tvCourseTitle2.setText(data1.lastCourse.course_name);
						tvCoach.setText(data1.lastCourse.coach_name);
						tvCourseJoinNum.setText(data1.lastCourse.current_num
								+ "人正在参与");
						beginTime = data1.lastCourse.begin_time;
						endTime = data1.lastCourse.end_time;
						courseId = data1.lastCourse._id;
						courseFid = data1.lastCourse.course_fid;
						liveUrl = data1.live_broadcast_addr;
						DataStorageUtils.setCourseId(courseId);
						DataStorageUtils.saveLastCourse(data.data);
						// 设置已经加载过直播课程
						DataStorageUtils.setGetLastCourse(true);
						timeBegin = format.format(new Date(beginTime * 1000));
						if (beginTime * 1000 > System.currentTimeMillis()) {
							rlNoStartPrompt.setVisibility(View.VISIBLE);
							countDownTimer.cancel();
							countDownTimer.schedule(cutDownLive, 1000);
						} else {
							rlNoStartPrompt.setVisibility(View.GONE);
							isLiveStart = true;
						}
						tvBeginTime.setText(format1.format(new Date(
								beginTime * 1000)) + "");
						tvLiveCoach.setText(data1.lastCourse.coach_name);
						ImageLoaderUtils.getInstance().loadImage(
								DataStorageUtils.getHeadDownloadUrl(courseFid),
								courseImage, 0, 0, R.drawable.loading_show);
						HeaderHelper.load(data1.lastCourse.coach_fid,
								dlgLiveNoStart.getCoachHeadImage());
						dlgLiveNoStart.getCoachName().setText(
								data1.lastCourse.coach_name);
						dlgLiveNoStart.setCoachHeadImage(courseFid);
						ptrRefresh.onRefreshComplete();
						tvCourseTitle.setVisibility(TextUtils
								.isEmpty(tvCourseTitle.getText().toString()) ? View.GONE
								: View.VISIBLE);
					}

					@Override
					public void onFailure(String errorMsg) {
						courseImage.setImageResource(R.drawable.loading_show);
						tvCastingHint.setVisibility(View.GONE);
						ptrRefresh.onRefreshComplete();
					}
				});
	}

	/**
	 * 获取今日主教教练头像id
	 */
	private void getChiefCoachprifile() {
		YJSNet.send(new ReqChiefCoachProfile(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspChiefCoachProfile>() {
					@Override
					public void onSuccess(RspChiefCoachProfile data) {
						if (data.data == null
								|| data.data.profile_fid_list.length == 0) {
							return;
						}
						if (data.data.profile_fid_list.length == 1) {
							chiefcoachHead.setVisibility(View.VISIBLE);
							HeaderHelper.load(data.data.profile_fid_list[0],
									chiefcoachHead);
						} else if (data.data.profile_fid_list.length == 2) {
							chiefcoachHead.setVisibility(View.VISIBLE);
							chiefcoachHead1.setVisibility(View.VISIBLE);
							HeaderHelper.load(data.data.profile_fid_list[0],
									chiefcoachHead1);
							HeaderHelper.load(data.data.profile_fid_list[1],
									chiefcoachHead);
						} else if (data.data.profile_fid_list.length == 3) {
							chiefcoachHead.setVisibility(View.VISIBLE);
							chiefcoachHead1.setVisibility(View.VISIBLE);
							chiefcoachHead2.setVisibility(View.VISIBLE);

							HeaderHelper.load(data.data.profile_fid_list[0],
									chiefcoachHead2);
							HeaderHelper.load(data.data.profile_fid_list[1],
									chiefcoachHead1);
							HeaderHelper.load(data.data.profile_fid_list[2],
									chiefcoachHead);
						}
					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	/**
	 * 计算距离课程直播开始时间
	 */
	public Runnable cutDownLive = new Runnable() {
		@Override
		public void run() {
			currentTime = System.currentTimeMillis();
			joinTime = currentTime / 1000 - beginTime;
			timeNow = format.format(new Date(currentTime));
			try {
				bTime = format.parse(timeBegin);
				nTime = format.parse(timeNow);
			} catch (Exception e) {
				e.printStackTrace();
			}
			startTime = bTime.getTime() - nTime.getTime();
			hour = startTime / (60 * 60 * 1000);
			min = ((startTime / (60 * 1000)) - day * 24 * 60 - hour * 60);
			s = (startTime / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			if (hour > 0) {
				rlStartPrompt.setVisibility(View.GONE);
				tvCastingHint.setVisibility(View.GONE);
				rlNoStartPrompt.setVisibility(View.VISIBLE);
				tvCountDown.setText(df.format(hour) + ":" + df.format(min)
						+ ":" + df.format(s));
				dlgLiveNoStart.getCountDownTime().setText(
						df.format(hour) + ":" + df.format(min) + ":"
								+ df.format(s));
			} else if (min > 0) {
				rlStartPrompt.setVisibility(View.GONE);
				tvCastingHint.setVisibility(View.GONE);
				rlNoStartPrompt.setVisibility(View.VISIBLE);
				tvCountDown.setText(df.format(0) + ":" + df.format(min) + ":"
						+ df.format(s));
				dlgLiveNoStart.getCountDownTime().setText(
						df.format(0) + ":" + df.format(min) + ":"
								+ df.format(s));
			} else if (s > 0) {
				rlStartPrompt.setVisibility(View.GONE);
				tvCastingHint.setVisibility(View.GONE);
				rlNoStartPrompt.setVisibility(View.VISIBLE);
				tvCountDown.setText(df.format(0) + ":" + df.format(0) + ":"
						+ df.format(s));
				dlgLiveNoStart.getCountDownTime().setText(
						df.format(0) + ":" + df.format(0) + ":" + df.format(s));
			} else {
				rlNoStartPrompt.setVisibility(View.GONE);
				rlStartPrompt.setVisibility(View.VISIBLE);
				tvCastingHint.setVisibility(View.VISIBLE);
				countDownTimer.cancel();
				isLiveStart = true;
			}
			if ((currentTime / 1000) > endTime) {
				isLiveEnd = true;
			}
		}
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnEnterLive:
			// startActivity(new Intent(mActivity, VlcVideoActivity.class));
			if (isLiveStart) {
				// startActivity(new Intent(mActivity, VlcVideoActivity.class));
				JoinLive();
			}
			// else{
			// WidgetUtils.showToast("当前直播已经结束，请等待下一个直播");
			// getLastCourse();
			// }
			break;
		case R.id.tvCoursePreShow:
			mActivity.startActivity(AtyLiveAdvance.class);
			break;

		case R.id.tvOldCourse:
			mActivity.startActivity(AtyPreCourse.class);
			break;
		case R.id.tvMyFootprint:
			startActivity(new Intent(getActivity(), AtyTraces.class));
			break;
		case R.id.rlChiefCoach:
			dlgLiveNoStart.dismiss();
			startActivity(new Intent(this.getActivity(), AtyChiefCoach.class));
			break;
		case R.id.rlNoStartPrompt:
			dlgLiveNoStart.show();
			break;
		case R.id.llNotNetWarning:
			StartThirdPartyAppUtils.openNetworkSetting(mActivity);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		if (coursebitmap != null && !coursebitmap.isRecycled()) {
			coursebitmap.recycle();
			coursebitmap = null;
		}
		// tangtaotao_20151019 add to fix memory leak
		countDownTimer.cancel();
		if (scrollToHeadTimer != null) {
			scrollToHeadTimer.cancel();
		}
		super.onDestroyView();
	}

	/**
	 * 加入直播
	 */
	private void JoinLive() {
		if (courseId == null) {
			WidgetUtils.showToast("课程id不能为空");
			return;
		}
		if (TextUtils.isEmpty(liveUrl)) {
			WidgetUtils.showToast("直播地址不能为空");
			return;
		}
		if (isLiveOverTime()) {
			WidgetUtils.showToast("直播时间已过");
			getLastCourse();
			return;
		}
		YJSNet.send(
				new ReqJoinLiveCast(courseId, DataStorageUtils
						.getNetEaseAccount().accid, DataStorageUtils
						.getNetEaseAccount().token, joinTime), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspJoinLiveCast>() {
					@Override
					public void onSuccess(RspJoinLiveCast data) {
						DataStorageUtils.setCourseId(courseId);
						// 进入健身房模式
						if (DataStorageUtils.isInClubMode()) {
							Intent intent = new Intent(mActivity,
									ClubModeVideoActivity.class);
							intent.putExtra("live_course", new LiveVideoEntity(
									courseId, liveUrl, beginTime, endTime));
							startActivity(intent);
						} else {
							Intent intent = new Intent(mActivity,
									VlcVideoActivity.class);
							// liveUrl =
							// "rtmp://streaming.51efit.com:1935/live/test16";
							intent.putExtra("live_course", new LiveVideoEntity(
									courseId, liveUrl, beginTime, endTime));
							startActivity(intent);
						}
					}

					@Override
					public void onFailure(String errorMsg) {
						Toast.makeText(mActivity, "加入直播失败", Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	private boolean isLiveOverTime() {
		Date courseEndTime = new Date(endTime * 1000);
		Date currentTime = new Date(System.currentTimeMillis());
		return currentTime.after(courseEndTime);
	}

	private boolean isFirst = true;
	private UITimer scrollToHeadTimer;

	private Runnable scrollToHead = new Runnable() {
		@Override
		public void run() {
			ScrollView scroll = ptrRefresh.getScrollView();
			if (scroll != null) {
				scroll.scrollTo(0, 0);
			}
		}
	};

	/**
	 * 更新热门课程列表
	 * 
	 * @param courses
	 */
	private void updateHotCourseList(ArrayList<RspHotCourse.HotCourse> courses) {
		adapter = new HotCourseListAdapter(mActivity, courses);
		gvHotCourse.setAdapter(adapter);
		// FIXME: 2015/11/21 CustomExpandGridView Layout时会将焦点移动到gridview头部,
		// 导致scrollview下移。等gridview第一次绘制完成后，重新滚动到头部
		// gridview 绘制完成时间有不确定性，因此在2s内每100ms滚动到头部
		if (isFirst) {
			isFirst = false;
			scrollToHeadTimer = new UITimer();
			scrollToHeadTimer.schedule(scrollToHead, 100, 100, 10);
		}

	}

	private OnRefreshListener2<ScrollView> onRefreshListener = new OnRefreshListener2<ScrollView>() {

		@Override
		public void onPullDownToRefresh(
				PullToRefreshBase<ScrollView> refreshView) {
			getChiefCoachprifile();
			getHotCourse();
			getLastCourse();
			// 已经执行完onWindowFocusChanged，网络还没开启完成时，在下拉的过程中再做一次判断，好去除无网路提示
			showNotNetTips();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

		}
	};

	/**
	 * 热门课程列表适配器
	 */
	private class HotCourseListAdapter extends
			AdapterBase<RspHotCourse.HotCourse> {

		public HotCourseListAdapter(Context context,
				List<RspHotCourse.HotCourse> list) {
			super(context, list);
		}

		@Override
		protected View getItemView(int position, View convertView,
				ViewGroup parent, final RspHotCourse.HotCourse entity) {
			ItemHotCourse item = null;
			if (convertView == null) {
				item = new ItemHotCourse(mContext);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						hotCourseItemWidth, hotCourseItemHeight);
				item.ivCover.setLayoutParams(lp);
				convertView = item;
			} else {
				item = (ItemHotCourse) convertView;
			}
			ImageLoaderUtils.getInstance().loadImage(
					DataStorageUtils.getHeadDownloadUrl(entity.course_fid),
					item.ivCover, 0, 0, R.drawable.loading_show);
			item.tvName.setText(entity.coach_name);
			item.tvTitle.setText(entity.course_name);
			item.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String videoLinkUrl = DataStorageUtils
							.getMp4Url(entity.video_link);
					if (TextUtils.isEmpty(videoLinkUrl)) {
						WidgetUtils.showToast("链接为空,视频已下架");
						return;
					}
					/*
					 * if(true) { forTest(videoLinkUrl,entity.course_id);
					 * return; }
					 */
					Intent intent = new Intent(mActivity, AtyLiveCast.class);
					LiveCastHelper.saveVideoInfo(intent, LiveCastHelper
							.genVideoInfo(entity.course_id, entity.coach_name,
									entity.course_name, entity.course_time,
									videoLinkUrl, entity.course_fid));
					startActivity(intent);
				}
			});
			return convertView;
		}

	}

	private void forTest(String videolink, String courseId) {
		DataStorageUtils.setCourseId(courseId);
		this.courseId = courseId;
		this.liveUrl = videolink;
		endTime = System.currentTimeMillis() / 1000 + 60 * 5;
		JoinLive();
	}

	/**
	 * 显示没有网络时的提示
	 */
	private void showNotNetTips() {
		boolean isNetAvailable = NetworkUtils.isNetworkAvailable();
		llNotNetWarning
				.setVisibility(isNetAvailable ? View.GONE : View.VISIBLE);
	}
}
