package com.cy.yigym.fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.aty.AtyGrowthTravel;
import com.cy.yigym.aty.AtyScore;
import com.cy.yigym.aty.AtySportRecord;
import com.cy.yigym.aty.AtySportTarget;
import com.cy.yigym.aty.AtyUserInfo;
import com.cy.yigym.aty.PopupWindows;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventUpdateUserInfo;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.req.ReqGetExerciseTarget;
import com.cy.yigym.net.req.ReqGetTotalExercise;
import com.cy.yigym.net.req.ReqGetUserInfo;
import com.cy.yigym.net.req.ReqGrowthTravel;
import com.cy.yigym.net.rsp.RspGetExerciseTarget;
import com.cy.yigym.net.rsp.RspGetExerciseTarget.DataEntity;
import com.cy.yigym.net.rsp.RspGetTotalExercise;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.net.rsp.RspGrowthTravel;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.StringUtils;
import com.cy.yigym.view.ThumbTextSeekbar;
import com.cy.yigym.view.content.DlgAllSportDataShare;
import com.cy.yigym.view.content.EventHeadImageView;
import com.efit.sport.R;
import com.hhtech.utils.DimenUtils;
import com.sport.efit.theme.ColorTheme;
import com.zxing.qrcode.encode.QRCodeEncoder;

import de.greenrobot.event.EventBus;

/**
 * Created by eijianshen on 15/7/15.
 */
public class FragmentUserInfo extends BaseFragment implements
		View.OnClickListener {
	// 个人信息区域
	@BindView
	private LinearLayout ll_base_info;
	// 进入设置界面
	@BindView
	private ImageView iv_to_setting;
	@BindView
	private EventHeadImageView iv_header;
	@BindView
	private View mMainView, rlTarget, rlGrowth;
	@BindView
	private ImageView ivBack;
	@BindView
	private View llScore;
	@BindView
	private static TextView tv_nickname, tv_signature, tvScore;
	@BindView
	private TextView tvDistanceS, tvTimeS, tvCalorieS;
	private FragmentUserInfo mInstance;
	private RspGetUserInfo userInfo;
	static DecimalFormat df = new DecimalFormat("0");
	static DecimalFormat df1 = new DecimalFormat("0.00");
	private PopupWindows popupWindows;
	private String tvTotalDistance, tvTime, tvCalorie;
	private DlgAllSportDataShare dlgAllSportDataShare;
	@BindView
	private FrameLayout flHeadOutCircle;
	@BindView
	private FrameLayout flHeadInCircle;
	@BindView
	private ThumbTextSeekbar sbCal;
	private DecimalFormat df00 = new DecimalFormat("#0.00");
	private final String FORMAT_KM = "%.1fkm";
	private final String FORMAT_CAL = "%dcal";

	@BindView
	private TextView recentCoard;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_user_info0;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		ll_base_info.setOnClickListener(this);
		iv_to_setting.setOnClickListener(this);
		iv_header.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		llScore.setOnClickListener(this);
		rlTarget.setOnClickListener(this);
		rlGrowth.setOnClickListener(this);
		flHeadOutCircle.setBackgroundDrawable(BgDrawableUtils.creShape(
				0xff545a66, 360));
		flHeadInCircle.setBackgroundDrawable(BgDrawableUtils.creShape(
				0xff6a707e, 360));

	}

	// 更新用户信息后，在这个界面数据作相应改变
	private final static class UserInfoUpdateListener implements
			BusEventListener.MainThreadListener<EventUpdateUserInfo> {
		@Override
		public void onEventMainThread(EventUpdateUserInfo obj) {
			RspGetUserInfo.PersonInfo info = CurrentUser.instance()
					.getPersonInfo();
			if (obj.getHeight() != null && !obj.getHeight().isEmpty()) {
				setHeight(obj.getHeight());
				double h = Integer.parseInt(obj.getHeight());
				double w = Integer.parseInt(info.weight);
				double bmi = w / ((h / 100) * (h / 100));
				setBMI(bmi);
			}
			if (obj.getWeight() != null && !obj.getWeight().isEmpty()) {
				setWeight(obj.getWeight());
				double h = Integer.parseInt(info.height);
				double w = Integer.parseInt(obj.getWeight());
				double bmi = w / ((h / 100) * (h / 100));
				setBMI(bmi);
			}
			if (obj.getNickname() != null && !obj.getNickname().isEmpty()) {
				tv_nickname.setText(obj.getNickname());
			}
			if (obj.getSign() != null && !obj.getSign().isEmpty()) {
				tv_signature.setText(obj.getSign());
			}

		}
	}

	UserInfoUpdateListener infoUpdateListener = new UserInfoUpdateListener();

	@Override
	protected void initData(Bundle savedInstanceState) {
		mInstance = this;
		getUserInfo();
		getTotalExercise();
		getRecentCoard();
		EventBus.getDefault().register(infoUpdateListener);
	}

	/**
	 * 从网络上获取用户信息
	 */
	private void getUserInfo() {
		YJSNet.getUserInfo(new ReqGetUserInfo(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGetUserInfo>() {
					@Override
					public void onSuccess(RspGetUserInfo data) {
						// DataStorageUtils.setUserInfo(data);
						CurrentUser.instance()
								.setUserInfo(data.data.personInfo);
						userInfo = data;
						initViewData(userInfo);
					}

					@Override
					public void onFailure(String errorMsg) {
						WidgetUtils.showToast(" 获取用户信息失败" + errorMsg);
					}
				});
	}

	/**
	 * 获取运动数据
	 */
	private void getTotalExercise() {
		YJSNet.send(new ReqGetTotalExercise(), LOG_TAG,
				new OnRespondCallBack<RspGetTotalExercise>() {
					@Override
					public void onSuccess(RspGetTotalExercise data) {
						if (data.data != null) {
							String.valueOf(data.data.total_exercise);
							tvTotalDistance = String.valueOf(df1
									.format(data.data.total_distance / 1000));
							tvTime = String.valueOf(df
									.format(data.data.total_time / 60));
							tvCalorie = String.valueOf(df
									.format(data.data.total_calorie));
							tvDistanceS.setText(tvTotalDistance + "km");
							tvTimeS.setText(tvTime + "min");
							tvCalorieS.setText(tvCalorie + "cal");

						}
					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
		YJSNet.getExerciseTarget(new ReqGetExerciseTarget(), LOG_TAG,
				new OnRespondCallBack<RspGetExerciseTarget>() {

					@Override
					public void onSuccess(RspGetExerciseTarget data) {
						DataEntity d = data.data;
						if (d != null)
							showContent(sbCal, FORMAT_CAL,
									d.current_total_calorie,
									d.current_target_total_calorie);

					}

					@Override
					public void onFailure(String errorMsg) {
						WidgetUtils.showToast(errorMsg);
					}
				});
	}

	/**
	 * 初始化控件的数据
	 *
	 * @param user
	 */
	private void initViewData(RspGetUserInfo userInfo) {
		if (userInfo == null)
			return;
		RspGetUserInfo.PersonInfo info = userInfo.data.personInfo;
		tv_nickname.setText(info.nick_name);
		tv_signature.setText(info.signature);
		tvScore.setText("积分"
				+ (TextUtils.isEmpty(info.score) ? "0" : info.score));
		setHeight(info.height);
		setWeight(info.weight);
		double h = Integer.parseInt(info.height);
		double w = Integer.parseInt(info.weight);
		double bmi = w / ((h / 100) * (h / 100));
		setBMI(bmi);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(info.profile_fid),
				iv_header);
	}

	/**
	 * 设置身高内容
	 *
	 * @param height
	 */
	public static void setHeight(String height) {
		SpannableStringBuilder content = StringUtils.creSpanString(
				new String[] { "身高", "\n" + height, "m" },
				new int[] { R.color.text_color,
						ColorTheme.getColor(R.color.data_color),
						ColorTheme.getColor(R.color.data_color), }, new int[] {
						12, 14, 12 });
		// tv_height.setText(content);
	}

	/**
	 * 设置体重内容
	 *
	 * @param weight
	 */
	private static void setWeight(String weight) {
		SpannableStringBuilder content = StringUtils.creSpanString(
				new String[] { "体重", "\n" + weight, "kg" }, new int[] {
						R.color.whites, ColorTheme.getColor(R.color.whites),
						ColorTheme.getColor(R.color.whites), }, new int[] { 12,
						14, 12 });
		// tv_weight.setText(content);
	}

	/**
	 * 设置BMI
	 *
	 * @param bmi
	 */
	private static void setBMI(double bmi) {
		SpannableStringBuilder content = StringUtils.creSpanString(
				new String[] { "BMI", "" + "\n" + df.format(bmi) },
				new int[] { R.color.text_color,
						ColorTheme.getColor(R.color.data_color) }, new int[] {
						12, 14 });
		// tv_bmi.setText(content);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_to_setting:
			startActivity(new Intent(getActivity(), AtyUserInfo.class));
			break;
        case R.id.iv_header:
            startActivity(new Intent(getActivity(), AtyUserInfo.class));
            break;
		case R.id.ivBack:
			finish();
			break;
		case R.id.llScore:
			startActivity(new Intent(getActivity(), AtyScore.class));
			break;
		case R.id.rlTarget:
			startActivity(new Intent(getActivity(), AtySportTarget.class));
			break;
		case R.id.ll_base_info:
			startActivity(new Intent(getActivity(), AtySportRecord.class));
			break;
		case R.id.rlGrowth:
			startActivity(new Intent(getActivity(), AtyGrowthTravel.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(final int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
		EventBus.getDefault().unregister(infoUpdateListener);
		iv_header.unRegister();
	}

	/**
	 * 分享弹窗
	 *
	 * @param content
	 * @param view
	 */
	private void setShareData(String content, View view) {
		String title = "E健身健康生活";
		String imgUrl = "http://img0w.pconline.com.cn/pconline/1309/11/3464151_10.jpg";
		String url = "http://mp.weixin.qq.com/s?__biz=MzI2MDAxODY3Mg==&mid=218040101&idx=1&sn=2396374941dda10297d0b61326c8456e#rd";

		dlgAllSportDataShare = new DlgAllSportDataShare(mActivity);
		// 显示窗口
		dlgAllSportDataShare.showAtLocation(
				getActivity().findViewById(R.id.rlUserInfo), Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
		dlgAllSportDataShare.getAllDataShare().setShareData(title, content,
				null, null);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		dlgAllSportDataShare.getAllDataShare().setViewToShare(view);
	}

	/**
	 * 动态加载布局dlg_share_sport，用来截取分享到第三方的图片
	 *
	 * @param text
	 * @param dis
	 * @param cal
	 * @param time
	 */
	private View shareImage(String dis, String cal, String time) {

		View view = LayoutInflater.from(mActivity).inflate(
				R.layout.view_all_sport_data_to_share, null);

		// 获取当然用户头像
		CustomCircleImageView myselfHead = (CustomCircleImageView) view
				.findViewById(R.id.myselfHead);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(DataStorageUtils
						.getCurUserProfileFid()), myselfHead);

		TextView myNickName = (TextView) view.findViewById(R.id.myNickName); // 当然用户昵称
		myNickName.setText(CurrentUser.instance().getNickname());
		TextView tvSportDis = (TextView) view.findViewById(R.id.tvSportDis);// 运动总距离
		tvSportDis.setText(dis);
		TextView tvSportCal = (TextView) view.findViewById(R.id.tvSportCal);// 运动消耗总卡路里
		tvSportCal.setText(cal);
		TextView tvSportMin = (TextView) view.findViewById(R.id.tvSportMin);// 运动总耗时
		tvSportMin.setText(time);

		view.setDrawingCacheEnabled(true);

		String url = "http://www.pgyer.com/jlQm";
		ImageView iv = (ImageView) view.findViewById(R.id.download_qrcode);
		iv.setImageBitmap(QRCodeEncoder.encodeAsBitmap(url,
				DimenUtils.dpToPx(100), DimenUtils.dpToPx(100)));
		return view;
	}


	private void showContent(final ThumbTextSeekbar sb, final String format,
			int progress, int max) {
		progress = progress < 0 ? 0 : progress;
		showContentInternal(sb, format, progress, max);

	}

	/**
	 * 显示内容
	 *
	 * @param sb
	 * @param format
	 * @param progress
	 * @param max
	 */
	private void showContentInternal(final ThumbTextSeekbar sb,
			final String format, final int progress, int max) {
		if (format.equals(FORMAT_CAL)) {
			sb.tvTotal.setText((String.format(format, (int) max)));
		} else if (format.equals(FORMAT_KM)) {
			sb.tvTotal.setText(String.format(format, max / 1000.0));
		}

		sb.seekBar.setMax(max);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int p,
					boolean fromUser) {
				if (format.equals(FORMAT_CAL)) {
					//sb.setThumbText(String.format(format, progress));
					sb.tvCur.setText(progress+"/");
				} else if (format.equals(FORMAT_KM)) {
					//sb.setThumbText(String.format(format, progress / 1000.0));
					sb.tvCur.setText(String.format("%.1f/", progress / 1000.0));
				}
			}
		});
		sb.setProgress(progress);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUserInfo();
	}

	private void updateUserInfo() {
		if (DataStorageUtils.isUserInfoChange()) {
			tv_nickname.setText(DataStorageUtils.getUserNickName());
			tv_signature.setText(CurrentUser.instance().getUserSign());
			ImageLoaderUtils.getInstance().loadImage(
					DataStorageUtils.getHeadDownloadUrl(CurrentUser.instance()
							.getPid()), iv_header);
		}
	}

	private void getRecentCoard(){
		YJSNet.send(new ReqGrowthTravel(), LOG_TAG, new OnRespondCallBack<RspGrowthTravel>() {
			@Override
			public void onSuccess(RspGrowthTravel data) {
				if(data.data.medal_list==null||data.data.medal_list.size()==0){
					return;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				String date = sdf.format(new Date(data.data.medal_list.get(0).finished_time*1000));
				recentCoard.setText("最近的记录："+date+data.data.medal_list.get(0).medal_meaning);
			}

			@Override
			public void onFailure(String errorMsg) {

			}
		});
	}


}
