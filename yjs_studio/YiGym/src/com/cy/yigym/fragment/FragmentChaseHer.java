package com.cy.yigym.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.aty.AtyChaseHer;
import com.cy.yigym.aty.AtyChaseHistory;
import com.cy.yigym.entity.ChaseIntentEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqCreateRecord;
import com.cy.yigym.net.req.ReqGetNearOnline;
import com.cy.yigym.net.req.ReqTransmitLocation;
import com.cy.yigym.net.rsp.RspCreateRecord;
import com.cy.yigym.net.rsp.RspGetNearOnline;
import com.cy.yigym.net.rsp.RspGetNearOnline.ArrayLocation;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;
import com.cy.yigym.utils.StringUtils;
import com.cy.yigym.view.content.DlgScreen;
import com.cy.yigym.view.content.chaseher.ChaseHerScanTa;
import com.cy.yigym.view.content.chaseher.FlipPager;
import com.cy.yigym.view.content.chaseher.FlipPager.OnFlipPageChangeListener;
import com.cy.yigym.view.content.chaseher.HerInfoView;
import com.efit.sport.R;
import com.hhtech.base.AppUtils;
import com.hhtech.location.LocationUtils;

/**
 * Created by ejianshen on 15/7/16.
 */
public class FragmentChaseHer extends BaseFragment implements
		View.OnClickListener, OnFlipPageChangeListener {
	// 标题
	@BindView
	private CustomTitleView altertitle;

	@BindView
	private CustomCircleImageView headcircleview;
	@BindView
	private FlipPager vFlipPager;
	@BindView
	private ChaseHerScanTa vScanTa; // 搜索界面布局
	@BindView
	private RelativeLayout llsearchres; // 搜索结果界面布局
	@BindView

	private TextView tv_distance;

	@BindView
	private Button btnChaseHer;
	@BindView
	private Button btnPre, btnNext;



	@BindView
	private ImageView imgScan;
	@BindView
	private ScrollView srcSearchRes;
	private DlgScreen dlgScreen;
	private Context context;
	// 被追用户的pid
	private String pid;
	// 相距的距离
	private double apartDistance;
	private Handler mainHandler = new Handler(Looper.getMainLooper());
	private DecimalFormat df = new DecimalFormat("######0.00");
	private int totalPageNum = 1;
	private int curPage = 1;
	private int curIndex = 0;
	private int pageSize = 0;
	private ArrayList<ArrayLocation> users = new ArrayList<RspGetNearOnline.ArrayLocation>();
	private ArrayLocation curSelectUser = null;// 当前选择的被追用户
	private String condiSex = "女", condiDistance = "50";// 搜索条件
	private final String NONE_NEXT_USER_WARNING = "已经到底了，没有更多用户了！";
	private final String NONE_PRE_USER_WARNING = "已经到顶了，没有更多用户了！";
	private boolean isFirstScan = true;// 是否是第一次进入的时候扫描附近的ta
	private UserInfoAdapter adapter;


	/**
	 * 指明应用是否第一次启动百度地图
	 * 第一次启动搜索时，百度地图启动需要一定时间（启动一个新的进程）
	 * 后面启动则较快
	 */
	public static boolean firstStartSearch = true;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_chase_her;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {

		context = this.getActivity();
		altertitle.setTitle("追TA");
		altertitle.setTxtLeftText("历史纪录");
		altertitle.setTxtLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AtyChaseHistory.class));
			}
		});
		altertitle.setTxtRightIcon(R.drawable.icon_screen);
		altertitle.setTxtRightClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgScreen.show();
			}
		});
		btnChaseHer.setOnClickListener(this);
		btnPre.setOnClickListener(this);
		btnNext.setOnClickListener(this);

		StateListDrawable bgBtnPre = BgDrawableUtils.crePressSelector(
				R.drawable.cat_last_b, R.drawable.cat_last_b_on);
		StateListDrawable bgBtnNext = BgDrawableUtils.crePressSelector(
				R.drawable.cat_next_b, R.drawable.cat_next_b_on);
		btnPre.setBackgroundDrawable(bgBtnPre);
		btnNext.setBackgroundDrawable(bgBtnNext);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		adapter = new UserInfoAdapter(mActivity, users);
		vFlipPager.setAdapter(adapter);
		vFlipPager.setOnPageChangeListener(this);
		// 定位，将自己所在地的经纬度上传服务器

		dlgScreen = new DlgScreen(context);
		// 如果搜索成功，则显示llsearchres布局,否则不显示llsearch布局
		dlgScreen.getBtnSure().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetWhenReFilter();
				condiSex = dlgScreen.getSex();
				condiDistance = dlgScreen.getDistance() + "";
				getNearOnlineUsersDelayed(condiSex, condiDistance, curPage - 1
						+ "", 3000);
				dlgScreen.dismiss();
			}
		});

		startSearch();
	}


	private void startSearch() {
		LocationUtils.instance().setListener(
				new LocationUtils.OnLocationListener() {
					@Override
					public void onLocation(double longitude, double latitude) {
						String nickname = CurrentUser.instance()
								.getPersonInfo().nick_name;
						String sex = CurrentUser.instance().getPersonInfo().sex;
						YJSNet.send(new ReqTransmitLocation(nickname, sex,
										latitude, longitude), LOG_TAG,
								new YJSNet.OnRespondCallBack<RspBase>() {
									@Override
									public void onSuccess(RspBase data) {
										// 正在搜索附近的人
										if (isFirstScan) {
											isFirstScan = false;
											// 延迟三秒，展示雷达扫描效果之后再开始真正的搜索
											getNearOnlineUsersDelayed(condiSex,
													condiDistance, curPage - 1
															+ "", 2000);
										}
									}

									@Override
									public void onFailure(String errorMsg) {
										WidgetUtils
												.showToast("上传失败" + errorMsg);
									}
								});

					}
				});

		LocationUtils.instance().requestCyclicLocation(200000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocationUtils.instance().stopLocation();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnChaseHer:
			selectChaseTarget();
			// TODO IM测试
			// AtyIM.luanchIM(mActivity, pid);
			break;
		case R.id.btnPre:
			getPreUser();
			break;
		case R.id.btnNext:
			getNextUser();
			break;
		}
	}

	private boolean isCreatingRecord = false;

	// 用户追Ta目标
	private void selectChaseTarget() {
		if (DataStorageUtils.getPid().equals(pid)) {
			WidgetUtils.showToast("追自己？不可以哦！");
		} else {
			if (isCreatingRecord) {
				return;
			}
			isCreatingRecord = true;
			YJSNet.send(new ReqCreateRecord(pid, (int) apartDistance), LOG_TAG,
					new YJSNet.OnRespondCallBack<RspCreateRecord>() {
						@Override
						public void onSuccess(RspCreateRecord data) {
							Intent intent = new Intent(getActivity(),
									AtyChaseHer.class);
							ChaseIntentEntity entity = new ChaseIntentEntity(
									curSelectUser.nick_name, data);
							intent.putExtra("flag", 1);

							if (data.data.isSender == 1) {
								DataStorageUtils
										.setOtherPid(data.data.cr_ref.receiver_id);

							} else {
								DataStorageUtils
										.setOtherPid(data.data.cr_ref.sender_id);
							}
							intent.putExtra(ChaseIntentEntity.INTENT_KEY,
									entity);
							startActivity(intent);
							mainHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									isCreatingRecord = false;
								}
							}, 1000);

						}

						@Override
						public void onFailure(String errorMsg) {
							isCreatingRecord = false;
							Log.i("errorMsg",errorMsg);
						}
					});
		}
	}

	/**
	 * 获取总页数
	 * 
	 * @param data
	 * @return
	 */
	private int getTotalPageNum(RspGetNearOnline data) {
		int totalPageNum = 1;
		try {
			totalPageNum = (int) Math.ceil(Double.parseDouble(data.data.total)
					/ data.data.arrayLocation.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalPageNum;
	}

	/**
	 * 展示当前选择用户的信息
	 */
	private void showCurSelectUserInfo() {
		if (curSelectUser == null)
			return;
		pid = curSelectUser.pid;
		DataStorageUtils.setOtherPid(pid);
		String distance = "0m";
		// 如果相聚距离大于1000则显示km否则显示m
		try {
			int m = (int) Double.parseDouble(curSelectUser.dis);
			apartDistance = m;
			if (m < 1000) {
				distance = m + "m";
			} else {

				distance = df.format(m / 1000.0) + "km";
			}
		} catch (Exception e) {
			e.printStackTrace();
			apartDistance = 0;
			distance = "0m";
		}

		SpannableStringBuilder sb = StringUtils.creSpanString(
				new String[] { "你和Ta只差", distance, ",追上Ta!" },
				new int[] { 0xff555555, 0xff05bbe2, 0xff555555, },
				new int[] { mResources.getInteger(R.integer.sp18),
						mResources.getInteger(R.integer.sp24),
						mResources.getInteger(R.integer.sp18) });
		tv_distance.setText(sb);
	}

	/**
	 * 获取下一位用户
	 */
	private void getNextUser() {
		curIndex++;
		if (curIndex < users.size()) {
			curSelectUser = users.get(curIndex);
			vFlipPager.setCurrentItem(curIndex);
		} else if (curIndex == users.size() - 1 && curPage < totalPageNum) {
			curPage++;
			getNearOnlineUsers(condiSex, condiDistance, curPage - 1 + "", true);
		} else {
			curIndex--;
			WidgetUtils.showToast(NONE_NEXT_USER_WARNING);
		}
	}

	/**
	 * 获取前一位用户
	 */
	private void getPreUser() {
		curIndex--;
		if (curIndex >= 0) {
			curSelectUser = users.get(curIndex);
			vFlipPager.setCurrentItem(curIndex);
		} else {
			curIndex++;
			WidgetUtils.showToast(NONE_PRE_USER_WARNING);
		}
	}

	/**
	 * 当重新筛选的时候重置
	 */
	private void resetWhenReFilter() {
		curIndex = 0;
		curPage = 1;
		totalPageNum = 1;
		users.clear();
		curSelectUser = null;
		condiSex = "";
		condiDistance = "0";
		switchViewStatus(false, ChaseHerScanTa.TEXT_SCAN_TA);
	}

	/**
	 * 延迟搜索附近的用户
	 * 
	 * @param sex
	 * @param distance
	 * @param pageNum
	 * @param delayMillis
	 */
	private void getNearOnlineUsersDelayed(final String sex,
			final String distance, final String pageNum, long delayMillis) {
		mainHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				getNearOnlineUsers(sex, distance, pageNum, true);
			}
		}, delayMillis);
	}

	/**
	 * 获取附近在线用户
	 * 
	 * @param sex
	 * @param distance
	 * @param page_num
	 */
	private void getNearOnlineUsers(String sex, String distance,
			String pageNum, final boolean isSetItem) {
		YJSNet.send(new ReqGetNearOnline(sex, distance, pageNum), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGetNearOnline>() {
					@Override
					public void onSuccess(RspGetNearOnline data) {
						// 没有搜索到附近的ta
						if (data.data.arrayLocation.size() == 0
								&& users.size() == 0) {
							switchViewStatus(false,
									ChaseHerScanTa.TEXT_NOT_FIND_TA);
							return;
						}
						switchViewStatus(true, "");
						users.addAll(data.data.arrayLocation);
						if (totalPageNum == 1) {
							totalPageNum = getTotalPageNum(data);
							pageSize = data.data.arrayLocation.size();
						}
						if (curIndex < users.size() && isSetItem) {
							curSelectUser = users.get(curIndex);
							vFlipPager.setCurrentItem(curIndex);
							showCurSelectUserInfo();
						}

					}

					@Override
					public void onFailure(String errorMsg) {
						switchViewStatus(false, errorMsg);
					}
				});
	}

	/**
	 * 切换控件状态
	 * 
	 * @param isFindTa
	 *            是否有找到附近的ta
	 * @param text
	 *            没有找到ta时的原因提示
	 */
	public void switchViewStatus(boolean isFindTa, String text) {
		if (isFindTa) {
			vScanTa.stopScanAnim();
			vScanTa.dismiss();
			llsearchres.setVisibility(View.VISIBLE);
		} else {
			vScanTa.startScanAnim();
			vScanTa.show();
			vScanTa.setText(text);
			llsearchres.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int index) {
		curIndex = index;
		if (pageSize == 0) {
			curPage = 1;
		} else {
			int p = (int) Math.ceil(index / (pageSize * 1.0));
			curPage = p == 0 ? 1 : p;
		}
		curSelectUser = users.get(index);
		showCurSelectUserInfo();
		// 预加载后面的数据
		if ((curIndex + 1) == users.size() - 1 && curPage < totalPageNum) {
			curPage++;
			getNearOnlineUsers(condiSex, condiDistance, curPage - 1 + "", false);
		}
	}

	@Override
	public void onNonePage() {
		WidgetUtils.showToast(NONE_NEXT_USER_WARNING);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ImageLoaderUtils.getInstance().cleanCache();
		vScanTa.realse();
		firstStartSearch = false;
	}

	/**
	 * 展示用户信息的适配器
	 */
	private class UserInfoAdapter extends AdapterBase<ArrayLocation> {

		public UserInfoAdapter(Context context, List<ArrayLocation> list) {
			super(context, list);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected View getItemView(int position, View convertView,
				ViewGroup parent, ArrayLocation entity) {
			HerInfoView item = new HerInfoView(mContext);
			HeaderHelper.load(entity.profile_fid, item.imgHead, 0);
			item.setDistance(entity.dis);
			item.setSex(entity.sex);
			item.setNickName(entity.nick_name);
			GradientDrawable bg = BgDrawableUtils.creShape(0xffffffff, 10, 1,
					0xffd6d6d6, null);
			item.rlBg.setBackgroundDrawable(bg);
			return item;
		}

	}

}
