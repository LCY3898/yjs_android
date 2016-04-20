package com.cy.yigym.aty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetTotalExercise;
import com.cy.yigym.net.req.ReqSportRecord;
import com.cy.yigym.net.rsp.RspGetTotalExercise;
import com.cy.yigym.net.rsp.RspSportRecord;
import com.cy.yigym.utils.CalendarUtils;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.StringUtils;
import com.cy.yigym.utils.SupportBarUtils;
import com.cy.yigym.view.content.DlgAllDataToShare;
import com.efit.sport.R;
import com.hhtech.utils.DimenUtils;
import com.zxing.qrcode.encode.QRCodeEncoder;

/**
 * Created by ejianshen on 15/11/11.
 */
public class AtySportRecord extends BaseFragmentActivity implements
		View.OnClickListener {
	@BindView
	private TextView tvTotalData;
	@BindView
	private TextView tvNumber;
	@BindView
	private TextView tvTotalTime;
	@BindView
	private TextView tvTotalDistance;
	@BindView
	private TextView tvTotalCalorie;
	@BindView
	private RelativeLayout rlTotalData;
	@BindView
	private ImageView ivUp;
	@BindView
	private TextView startTime;
	@BindView
	private TextView endTime;
	@BindView
	private ImageView ivNext;
	@BindView
	private TextView tvCalorie;
	@BindView
	private LinearLayout llTotalCalorie;
	@BindView
	private LinearLayout llChartCalorie;
	@BindView
	private TextView tvDistance;
	@BindView
	private LinearLayout llTotalDistance;
	@BindView
	private CustomTitleView vTitle;
	@BindView
	private LinearLayout llChartDistance;
	private SupportBarUtils mBarChartUtils;
	private SupportBarUtils mBarChartDistance;
	private List<Double> calorieData = new ArrayList<Double>();
	private List<Double> distanceData = new ArrayList<Double>();
	private String mNumber, mTime, mDistance, mCalorie;

	private Calendar calendar1, calendar2;

	private String date;// 日期拼接字符串

	private double maxCalorie = 0;
	private double maxDistance = 0;

	private int[] RENERDER_VALUE = { 0, 1, 2, 3, 4, 5, 6 }; // X坐标值

	private DecimalFormat df = new DecimalFormat("0.00");
	static DecimalFormat df1 = new DecimalFormat("0");

	private Calendar currentCalendar = new GregorianCalendar();

	private DlgAllDataToShare dlgAllDataToShare;// 总数据分享弹窗

	AllData allData = new AllData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_sport_record;
	}

	@Override
	protected void initView() {
		dlgAllDataToShare = new DlgAllDataToShare(mContext);
		vTitle.setTitle("运动纪录");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.header_back);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		vTitle.setTxtRightIcon(R.drawable.btn_medal_share);
		vTitle.setTxtRightClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setShareData(
						allData.sportNum,
						allData.totalTime,
						allData.totalDistance,
						allData.totalCalrie,
						getAllDataView(allData.sportNum, allData.totalTime,
								allData.totalDistance, allData.totalCalrie));
			}
		});
		mBarChartUtils = new SupportBarUtils(mActivity);
		mBarChartDistance = new SupportBarUtils(mActivity);

		ivUp.setOnClickListener(this);
		ivNext.setOnClickListener(this);

		// getWeekData(date);
	}

	@Override
	protected void initData() {
		getTotalExercise();
		initTimerPicker();// 初始化时间控件的值 @xiaoshu
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivUp:
			setLastWeek();
			break;
		case R.id.ivNext:
			setNextWeek();
			break;
		default:
			break;
		}
	}

	/**
	 * @xiaoshu 将时间设置为当前text显示时间所在周的上周
	 */
	private void setNextWeek() {
		currentCalendar = new GregorianCalendar();
		currentCalendar.setTime(CalendarUtils.getFirstDayOfWeek(new Date()));
		if (calendar1.compareTo(currentCalendar) >= 0) {
			ivNext.setClickable(false);
		} else {
			calendar1.add(calendar1.DAY_OF_MONTH, +7);
			calendar2.add(calendar2.DAY_OF_MONTH, +7);
			SpannableStringBuilder sb = StringUtils.creSpanString(
					new String[] { (calendar1.get(Calendar.MONTH) + 1) + "",
							"月", calendar1.get(Calendar.DAY_OF_MONTH) + "",
							"日-" }, new int[] { 0xff434d56, 0xff747d88,
							0xff434d56, 0xff747d88 }, new int[] { 17, 14, 17,
							14 });
			SpannableStringBuilder sb1 = StringUtils
					.creSpanString(
							new String[] {
									(calendar2.get(Calendar.MONTH) + 1) + "",
									"月",
									calendar2.get(Calendar.DAY_OF_MONTH) + "",
									"日" }, new int[] { 0xff434d56, 0xff747d88,
									0xff434d56, 0xff747d88 }, new int[] { 17,
									14, 17, 14 });
			startTime.setText(sb);
			endTime.setText(sb1);
			getWeekData(getDateValue(calendar1));
			if (calendar1.compareTo(currentCalendar) >= 0) {
				ivNext.setClickable(false);
			}
		}
	}

	/**
	 * @xiaoshu 将时间设置为当前text显示时间所在周的下周
	 */
	private void setLastWeek() {
		calendar2.add(calendar2.DAY_OF_MONTH, -7);
		calendar1.add(calendar1.DAY_OF_MONTH, -7);
		SpannableStringBuilder sb = StringUtils.creSpanString(
				new String[] { (calendar1.get(Calendar.MONTH) + 1) + "", "月",
						calendar1.get(Calendar.DAY_OF_MONTH) + "", "日-" },
				new int[] { 0xff434d56, 0xff747d88, 0xff434d56, 0xff747d88 },
				new int[] { 17, 14, 17, 14 });
		SpannableStringBuilder sb1 = StringUtils.creSpanString(
				new String[] { (calendar2.get(Calendar.MONTH) + 1) + "", "月",
						calendar2.get(Calendar.DAY_OF_MONTH) + "", "日" },
				new int[] { 0xff434d56, 0xff747d88, 0xff434d56, 0xff747d88 },
				new int[] { 17, 14, 17, 14 });
		startTime.setText(sb);
		endTime.setText(sb1);
		ivNext.setClickable(true);
		getWeekData(getDateValue(calendar1));
	}

	/**
	 * @xiaoshu 初始化时间选择控件的值，默认为当前时间所在周
	 */
	private void initTimerPicker() {
		Date currentDate = new Date();
		calendar1 = new GregorianCalendar();
		calendar1.setTime(CalendarUtils.getFirstDayOfWeek(currentDate));
		currentCalendar = calendar1;
		calendar2 = new GregorianCalendar();
		calendar2.setTime(CalendarUtils.getLastDayOfWeek(currentDate));
		SpannableStringBuilder sb = StringUtils.creSpanString(
				new String[] { (calendar1.get(Calendar.MONTH) + 1) + "", "月",
						calendar1.get(Calendar.DAY_OF_MONTH) + "", "日 - " },
				new int[] { 0xff434d56, 0xff747d88, 0xff434d56, 0xff747d88 },
				new int[] { 17, 14, 17, 14 });
		SpannableStringBuilder sb1 = StringUtils.creSpanString(
				new String[] { (calendar2.get(Calendar.MONTH) + 1) + "", "月",
						calendar2.get(Calendar.DAY_OF_MONTH) + "", "日" },
				new int[] { 0xff434d56, 0xff747d88, 0xff434d56, 0xff747d88 },
				new int[] { 17, 14, 17, 14 });
		startTime.setText(sb);
		endTime.setText(sb1);
		ivNext.setClickable(false);
		getWeekData(getDateValue(calendar1));
	}

	/**
	 * @param calendar
	 * @return
	 * @xiaoshu 获取控件中周的起始日期转换的string值，判断月和日是否大于10，小于10的在获取的数字前填充0
	 */
	private String getDateValue(Calendar calendar) {
		if ((calendar.get(Calendar.MONTH) + 1) < 10) {
			if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
				date = calendar.get(Calendar.YEAR) + "0"
						+ (calendar.get(Calendar.MONTH) + 1) + "0"
						+ calendar.get(Calendar.DAY_OF_MONTH);
			} else {
				date = calendar.get(Calendar.YEAR) + "0"
						+ (calendar.get(Calendar.MONTH) + 1) + ""
						+ calendar.get(Calendar.DAY_OF_MONTH);
			}
		} else {
			if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
				date = calendar.get(Calendar.YEAR) + ""
						+ (calendar.get(Calendar.MONTH) + 1) + "0"
						+ calendar.get(Calendar.DAY_OF_MONTH);
			} else {
				date = calendar.get(Calendar.YEAR) + ""
						+ (calendar.get(Calendar.MONTH) + 1) + ""
						+ calendar.get(Calendar.DAY_OF_MONTH);
			}
		}

		return date;
	}

	/**
	 * 获取个人运动总数据
	 */
	private void getTotalExercise() {
		YJSNet.send(new ReqGetTotalExercise(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGetTotalExercise>() {
					@Override
					public void onSuccess(RspGetTotalExercise data) {
						if (data.data != null) {
							mNumber = String.valueOf(data.data.total_exercise);
							mDistance = String.valueOf(df
									.format(data.data.total_distance / 1000));
							mTime = String.valueOf(df1
									.format(data.data.total_time / 60));
							mCalorie = String.valueOf(df1
									.format(data.data.total_calorie));
							tvNumber.setText(mNumber);
							tvTotalDistance.setText(mDistance + "km");
							tvTotalTime.setText(mTime + "min");
							tvTotalCalorie.setText(mCalorie + "cal");

							allData.sportNum = data.data.total_exercise;
							allData.totalCalrie = data.data.total_calorie;
							allData.totalDistance = data.data.total_distance;
							allData.totalTime = data.data.total_time;
						}
					}

					@Override
					public void onFailure(String errorMsg) {

						WidgetUtils.showToast("获取周运动数据失败" + errorMsg);
					}
				});
	}

	/**
	 * @xiaoshu 获取个人一周的运动数据
	 */
	public void getWeekData(String date) {
		calorieData.clear();
		distanceData.clear();
		maxCalorie = 0;
		maxDistance = 0;
		for (int i : RENERDER_VALUE) {
			calorieData.add(i, 0.0);
			distanceData.add(i, 0.0);
		}
		YJSNet.send(new ReqSportRecord(DataStorageUtils.getPid(), date),
				LOG_TAG, new YJSNet.OnRespondCallBack<RspSportRecord>() {
					@Override
					public void onSuccess(RspSportRecord data) {
						if (data.data.prec_list == null
								|| data.data.prec_list.size() == 0) {
							WidgetUtils.showToast("亲，您这周没有运动数据哦");
							tvCalorie.setText("0cal");
							tvDistance.setText("0km");
							// 当用户在当前周没有运动数据时候，人为将Y轴最大值置为1，避免没有运动数据时坐标图表不显示
							llChartCalorie.removeAllViews();
							llChartDistance.removeAllViews();
							mBarChartUtils = new SupportBarUtils(mActivity);
							mBarChartDistance = new SupportBarUtils(mActivity);
							llChartCalorie.addView(mBarChartUtils
									.initBarChartView(1, calorieData, 7, ""));
							llChartDistance.addView(mBarChartDistance
									.initBarChartView(1, distanceData, 7, ""));
							return;
						}
						for (RspSportRecord.PreDaySportData entity : data.data.prec_list) {
							// 一周中的卡路里的最大值
							maxCalorie = maxCalorie > entity.day_calorie ? maxCalorie
									: entity.day_calorie;
							// 一周中的距离的最大值
							maxDistance = maxDistance > entity.day_distance ? maxDistance
									: entity.day_distance;
							calorieData.set(entity.wday, entity.day_calorie);
							distanceData.set(entity.wday, Double.parseDouble(df
									.format(entity.day_distance / 1000.0)));
						}
						SpannableStringBuilder sb = StringUtils.creSpanString(
								new String[] { data.data.week_calorie + "",
										"cal" }, new int[] { 0xff434d56,
										0xff747d88 }, new int[] { 20, 14 });
						tvCalorie.setText(sb);
						SpannableStringBuilder sb1 = StringUtils.creSpanString(
								new String[] {
										df.format(data.data.week_distance / 1000.0),
										"km" }, new int[] { 0xff434d56,
										0xff747d88 }, new int[] { 20, 14 });
						tvDistance.setText(sb1);
						llChartCalorie.removeAllViews();
						llChartDistance.removeAllViews();
						mBarChartUtils = new SupportBarUtils(mActivity);
						mBarChartDistance = new SupportBarUtils(mActivity);
						llChartCalorie.addView(mBarChartUtils.initBarChartView(
								(float) maxCalorie, calorieData, 7, ""));
						llChartDistance.addView(mBarChartDistance
								.initBarChartView((float) maxDistance / 1000,
										distanceData, 7, ""));
					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	/**
	 * 获取分享到第三方的数据
	 * 
	 * @return
	 */

	private TextView tvSportNum;
	private CustomCircleImageView ccvMyHead;
	private TextView tvMyName;
	private TextView tvAllTime;
	private TextView tvAllDistance;
	private TextView tvAllCalrie;
	private RelativeLayout rlAllDataShare;
	private LinearLayout llCode;
	private ImageView ivCode;

	private View getAllDataView(int sportNum, double totalTime,
			double totalDistance, double totalCalrie) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.view_all_data_to_share, null);
		SpannableStringBuilder sb = StringUtils.creSpanString(new String[] {
				String.valueOf(sportNum), "次" }, new int[] { 0xffffffff,
				0xffffffff }, new int[] { 24, 16 });
		tvSportNum = (TextView) view.findViewById(R.id.tvSportNum);
		tvSportNum.setText(sb);
		ccvMyHead = (CustomCircleImageView) view.findViewById(R.id.ccvMyHead);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(DataStorageUtils
						.getCurUserProfileFid()), ccvMyHead);
		tvMyName = (TextView) view.findViewById(R.id.tvMyName);
		tvMyName.setText(DataStorageUtils.getUserNickName());
		tvAllTime = (TextView) view.findViewById(R.id.tvAllTime);
		tvAllTime.setText(String.valueOf(df1.format(totalTime / 60)) + "min");
		tvAllDistance = (TextView) view.findViewById(R.id.tvAllDistance);
		tvAllDistance.setText(String.valueOf(df.format(totalDistance / 1000))
				+ "km");
		tvAllCalrie = (TextView) view.findViewById(R.id.tvAllCalrie);
		tvAllCalrie.setText(String.valueOf(df1.format(totalCalrie)) + "cal");
		rlAllDataShare = (RelativeLayout) view
				.findViewById(R.id.rlAllDataShare);
		rlAllDataShare.setBackground(BgDrawableUtils.creShape(0xffffffff,
				new float[] { 5, 5, 5, 5, 0, 0, 0, 0 }));
		llCode = (LinearLayout) view.findViewById(R.id.llCode);
		llCode.setBackground(BgDrawableUtils.creShape(0xffffffff, new float[] {
				0, 0, 0, 0, 5, 5, 5, 5 }));
		String url = "http://www.pgyer.com/jlQm";
		ivCode = (ImageView) view.findViewById(R.id.ivCode);
		ivCode.setImageBitmap(QRCodeEncoder.encodeAsBitmap(url,
				DimenUtils.dpToPx(96), DimenUtils.dpToPx(96)));
		view.setDrawingCacheEnabled(true);
		return view;
	}

	/**
	 * 总数据分享
	 */
	private void setShareData(int sportNum, double totalTime,
			double totalDistance, double totalCalrie, View view) {
		String title = "e健身健康生活";
		String content = "";
		dlgAllDataToShare.getSvAllDataToShare().setShareData(title, null, null,
				null);
		dlgAllDataToShare.getSvAllDataToShare().setViewToShare(view);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(DataStorageUtils
						.getCurUserProfileFid()),
				dlgAllDataToShare.getCcvMyHead());
		SpannableStringBuilder sb = StringUtils.creSpanString(new String[] {
				String.valueOf(sportNum), "次" }, new int[] { 0xffffffff,
				0xffffffff }, new int[] { 24, 16 });
		dlgAllDataToShare.getTvSportNum().setText(sb);
		dlgAllDataToShare.getTvMyName().setText(
				DataStorageUtils.getUserNickName());
		dlgAllDataToShare.getTvAllTime().setText(
				String.valueOf(df1.format(totalTime / 60)) + "min");
		dlgAllDataToShare.getTvAllDistance().setText(
				String.valueOf(df.format(totalDistance / 1000)) + "km");
		dlgAllDataToShare.getTvAllCalrie().setText(
				String.valueOf(df1.format(totalCalrie)) + "cal");
		dlgAllDataToShare.show();

	}

	private class AllData {
		public int sportNum;
		public double totalTime;
		public double totalDistance;
		public double totalCalrie;
		// public AllData(int sportNum,double totalTime,double
		// totalDistance,double totalCalrie){
		// this.sportNum=sportNum;
		// this.totalTime=totalTime;
		// this.totalDistance=totalDistance;
		// this.totalCalrie=totalCalrie;
		// }
	}
}
