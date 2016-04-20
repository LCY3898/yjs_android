package com.cy.yigym.aty;

import java.text.DecimalFormat;

import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.wbs.RspBase;
import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.entity.ChaseIntentEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetDatas;
import com.cy.yigym.net.req.ReqUpdateChaseRecord;
import com.cy.yigym.net.req.ReqUpdateRecord;
import com.cy.yigym.net.rsp.RspGetDatas;
import com.cy.yigym.net.rsp.RspUpdateRecprd;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;
import com.cy.yigym.utils.StringUtils;
import com.cy.yigym.view.content.DlgmeetSucc;
import com.cy.yigym.view.content.chaseher.ChaseHerView;
import com.efit.sport.R;
import com.efit.sport.chase.ChaseTaSelfData;
import com.hhtech.utils.DimenUtils;
import com.zxing.qrcode.encode.QRCodeEncoder;

/**
 * 追她页面 Created by ejianshen on 15/7/28.
 */
public class AtyChaseHer extends BaseFragmentActivity {

	@BindView
	private CustomTitleView vTitle;
	// 发起者的运动距离
	@BindView
	private TextView tvSendDistance;
	// 发起者的运动时间
	@BindView
	private TextView tvSendTime;
	// 发起者的运动卡路里
	@BindView
	private TextView tvSendCalorie;
	@BindView
	private TextView tvReceiverDistance;
	@BindView
	private TextView tvReceiverTime;
	@BindView
	private TextView Tvchaseremind;
	@BindView
	private TextView tvReceiverCalorie, btnChatTa;
	@BindView
	private ChaseHerView vChaseHer;

	@BindView
	private CustomCircleImageView myID, herID;

	private DlgmeetSucc dlgmeetSucc;

	private int locationDistance = 0;// 实际相距的物理距离
	private UITimer getReceiverTimer = new UITimer();
	private UITimer updateSelfTimer = new UITimer();

	/**
	 * 运动的实时数据
	 */
	private double distance = 0;
	private double time = 0;
	private double calorie = 0;

	// 两者实际相距的距离（追完上次后剩余的距离）
	private double apartDistance;
	// 纪录的id
	private String recordId;
	private ChaseIntentEntity chaseIntentEntity;
	private int receiverDistance; // in meter
	private String receiverNickname;
	DecimalFormat df = new DecimalFormat("######0.00");

	private int otherDistance;
	private int otherTime;
	private int otherCalorie;

	private ChaseTaSelfData selfData;

	private boolean isSelfSender = true;

	private boolean isAlreadyFinish = false;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_chase_someone;
	}

	@Override
	protected void initData() {
		if (!isAlreadyFinish) {
			updateSelfTimer.schedule(updateRecord, 2000);
			getReceiverTimer.schedule(getDatas, 2000);
		}
		initChaseHerViewSize();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView() {
		vTitle.setTitle("去追Ta");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.header_back);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		dlgmeetSucc = new DlgmeetSucc(mContext);
		Tvchaseremind.setBackground(BgDrawableUtils.creShape(0xffcdf1f9, 5));
		btnChatTa.setBackgroundDrawable(BgDrawableUtils.crePressSelector(
				BgDrawableUtils.creShape(0xff9c9c9c, 5),
				BgDrawableUtils.creShape(0xff828080, 5)));
		btnChatTa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				AtyIM.luanchIM(mActivity, DataStorageUtils.getOtherPid(),
						receiverNickname);
			}
		});
		Intent intent = getIntent();
		chaseIntentEntity = (ChaseIntentEntity) intent
				.getSerializableExtra(ChaseIntentEntity.INTENT_KEY);
		String selfPid = DataStorageUtils.getPid();
		isSelfSender = selfPid.equals(chaseIntentEntity.senderId);

		// 从追她历史列表中传过来的值
		if (intent.getIntExtra("flag", 0) == 0) {
			isAlreadyFinish = !chaseIntentEntity.isDoing;
		}

		recordId = chaseIntentEntity.recordId;
		apartDistance = chaseIntentEntity.realApartDistance;
		locationDistance = (int) chaseIntentEntity.totalApartDistance;
		receiverNickname = chaseIntentEntity.otherNickname;

		selfData = new ChaseTaSelfData(isAlreadyFinish, isSelfSender,
				chaseIntentEntity);

		selfData.setDataUpdateListener(new ChaseTaSelfData.OnDataChangeListener() {
			@Override
			public void onDataChange() {
				formatData();
			}
		});
		if (isSelfSender) {
			otherDistance = (int) chaseIntentEntity.receiverDistance;
			otherTime = (int) chaseIntentEntity.receiverTime;
			otherCalorie = (int) chaseIntentEntity.receiverCalorie;
		} else {
			otherDistance = (int) chaseIntentEntity.sendDistance;
			otherTime = (int) chaseIntentEntity.sendTime;
			otherCalorie = (int) chaseIntentEntity.sendCalorie;
		}
		HeaderHelper.load(chaseIntentEntity.my_fid, myID, 0);
		HeaderHelper.load(chaseIntentEntity.another_fid, herID, 0);
		formatData();

		if (isAlreadyFinish) {
			onChaseFini();
		}

	}

	@SuppressWarnings("deprecation")
	private void showMeetSuccess() {
		Tvchaseremind.setTextColor(0xffea647a);
		Tvchaseremind.setText("恭喜，会面成功");
		vChaseHer.setChaseHerSuccessStatus();
		btnChatTa.setBackgroundDrawable(BgDrawableUtils.crePressSelector(
				BgDrawableUtils.creShape(0xff05bbe2, 5),
				BgDrawableUtils.creShape(0xff03a3c5, 5)));
		btnChatTa.setEnabled(true);
	}

	private void onChaseFini() {
		updateThisTimeData();
		showMeetSuccess();
		updateRecord.run();
		updateSelfTimer.cancel();
		getReceiverTimer.cancel();
		selfData.fini();

		// Intent intent = new Intent(mContext, AtyMeetSucc.class);
		// intent.putExtra(
		// MeetSuccessData.INTENT_KEY,
		// new MeetSuccessData(selfData.getTotalDistance(), selfData
		// .getTotalTime(), selfData.getTotalCalorie(),
		// receiverNickname, chaseIntentEntity.my_fid,
		// chaseIntentEntity.another_fid, otherDistance,
		// otherTime, otherCalorie));
		// startActivity(intent);
		String text = "您和" + receiverNickname + "会面成功";
		String content = "哈哈，我在e健身健康生活成功追上了" + receiverNickname;
		SpannableStringBuilder myDis = StringUtils.creSpanString(new String[] {
				String.format("%.2f", selfData.getTotalDistance() / 1000.0),
				"km" }, new int[] { 0xff05bbe2, 0xff05bbe2 }, new int[] { 17,
				12 });

		SpannableStringBuilder myTime = StringUtils.creSpanString(new String[] {
				String.format("%.1f", selfData.getTotalTime() / 60.0), "min" },
				new int[] { 0xff05bbe2, 0xff05bbe2 }, new int[] { 17, 12 });

		SpannableStringBuilder myCal = StringUtils.creSpanString(new String[] {
				String.format("%d", (int) selfData.getTotalCalorie()), "cal" },
				new int[] { 0xff05bbe2, 0xff05bbe2 }, new int[] { 17, 12 });
		View view = shareImage(text,
				String.format("%.2f", selfData.getTotalDistance() / 1000.0),
				String.format("%d", (int) selfData.getTotalCalorie()),
				String.format("%.1f", selfData.getTotalTime() / 60.0));
		setShareData(content, text, myDis, myCal, myTime, view);
	}

	private void formatData() {
		int meters = selfData.getTotalDistance();
		if (meters < 1000) {
			SpannableStringBuilder sbSendDis = StringUtils.creSpanString(
					new String[] { String.format("%d", meters), "m" },
					new int[] { 0xffffffff, 0xffffffff }, new int[] { 17, 10 });
			tvSendDistance.setText(sbSendDis);
		} else {
			SpannableStringBuilder sbSendDis = StringUtils.creSpanString(
					new String[] {
							df.format(selfData.getTotalDistance() / 1000.0),
							"km" }, new int[] { 0xffffffff, 0xffffffff },
					new int[] { 17, 10 });
			tvSendDistance.setText(sbSendDis);
		}

		int secs = selfData.getTotalTime();
		if (secs > 3600) {
			SpannableStringBuilder sbSendTime = StringUtils.creSpanString(
					new String[] {
							String.format("%02d:%02d:%02d", secs / 3600,
									secs % 3600 / 60, secs % 60), "min" },
					new int[] { 0xffffffff, 0xffffffff }, new int[] { 17, 10 });
			tvSendTime.setText(sbSendTime);
		} else {
			SpannableStringBuilder sbSendTime = StringUtils.creSpanString(
					new String[] {
							String.format("%02d:%02d", secs / 60, secs % 60),
							"min" }, new int[] { 0xffffffff, 0xffffffff },
					new int[] { 17, 10 });
			tvSendTime.setText(sbSendTime);
		}

		SpannableStringBuilder sbSendCalorie = StringUtils.creSpanString(
				new String[] { String.format("%d", selfData.getTotalCalorie()),
						"cal" }, new int[] { 0xffffffff, 0xffffffff },
				new int[] { 17, 10 });
		tvSendCalorie.setText(sbSendCalorie);

		if (otherDistance < 1000) {
			SpannableStringBuilder sbReceiverDis = StringUtils.creSpanString(
					new String[] { String.format("%d", otherDistance), "m" },
					new int[] { 0xffffffff, 0xffffffff }, new int[] { 17, 10 });
			tvReceiverDistance.setText(sbReceiverDis);
		} else {
			SpannableStringBuilder sbReceiverDis = StringUtils.creSpanString(
					new String[] { df.format(otherDistance / 1000.0), "km" },
					new int[] { 0xffffffff, 0xffffffff }, new int[] { 17, 10 });
			tvReceiverDistance.setText(sbReceiverDis);
		}

		if (otherTime > 3600) {
			SpannableStringBuilder sbReceiverTime = StringUtils.creSpanString(
					new String[] {
							String.format("%02d:%02d:%02d", otherTime / 3600,
									otherTime % 3600 / 60, otherTime % 60),
							"min" }, new int[] { 0xffffffff, 0xffffffff },
					new int[] { 17, 10 });
			tvReceiverTime.setText(sbReceiverTime);
		} else {
			SpannableStringBuilder sbReceiverTime = StringUtils.creSpanString(
					new String[] {
							String.format("%02d:%02d", otherTime / 60,
									otherTime % 60), "min" }, new int[] {
							0xffffffff, 0xffffffff }, new int[] { 17, 10 });
			tvReceiverTime.setText(sbReceiverTime);
		}
		SpannableStringBuilder sbReceiverCalorie = StringUtils.creSpanString(
				new String[] { String.format("%d", otherCalorie), "cal" },
				new int[] { 0xffffffff, 0xffffffff }, new int[] { 17, 10 });
		tvReceiverCalorie.setText(sbReceiverCalorie);
		vChaseHer.setCurDistance(Math.max(locationDistance - apartDistance, 0));
		double surpDistance = vChaseHer.getSurpDistance();
		if (surpDistance < 1000) {
			vChaseHer.setSurpDistanceText(String.format("还需要%.0f米",
					Math.max(surpDistance, 0)));
		} else {
			vChaseHer.setSurpDistanceText(String.format("还需要%.2f千米",
					surpDistance / 1000));
		}
	}

	/**
	 * 初始化追她控件
	 */
	private void initChaseHerViewSize() {
		int size = WidgetUtils.getScreenWidth();
		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size,
		// size);
		// vChaseHer.setLayoutParams(lp);
		vChaseHer.setRotateAngle(-20);
		vChaseHer.setMaxDistance(locationDistance);

		if (chaseIntentEntity != null) {
			vChaseHer.setHeadUrl(chaseIntentEntity.my_fid,
					chaseIntentEntity.another_fid);
		}
		vChaseHer.setCurDistance(Math.max(locationDistance - apartDistance, 0));
		double surpDistance = vChaseHer.getSurpDistance();

		vChaseHer.setSurpDistanceText(String.format("剩余距离%.1f米",
				Math.max(surpDistance, 0)));

	}

	// 每隔一段时间将自己的运动数据上传一次到服务器
	private Runnable updateRecord = new Runnable() {
		@Override
		public void run() {
			formatData();
			YJSNet.send(new ReqUpdateRecord(recordId, selfData.getTotalTime(),
					selfData.getTotalDistance(), selfData.getTotalCalorie()),
					LOG_TAG, new YJSNet.OnRespondCallBack<RspUpdateRecprd>() {
						@Override
						public void onSuccess(RspUpdateRecprd data) {

						}

						@Override
						public void onFailure(String errorMsg) {

						}
					});
		}
	};

	// 每隔一段时间拉取一次对方运动数据
	private Runnable getDatas = new Runnable() {
		@Override
		public void run() {

			YJSNet.send(new ReqGetDatas(recordId), LOG_TAG,
					new YJSNet.OnRespondCallBack<RspGetDatas>() {
						@Override
						public void onSuccess(RspGetDatas data) {
							otherDistance = (int) data.data.get_distance;
							otherTime = (int) data.data.get_time;
							otherCalorie = (int) data.data.get_calorie;
							apartDistance = (int) data.data.apart_distance;
							formatData();
							if (data.data.apart_distance <= 0) {
								onChaseFini();
								isAlreadyFinish = true;
							}

						}

						@Override
						public void onFailure(String errorMsg) {
						}
					});
		}
	};

	private void updateThisTimeData() {
		YJSNet.send(
				new ReqUpdateChaseRecord(recordId, selfData.getThisTimeTimes(),
						selfData.getThisTimeDistance(), selfData
								.getThisTimeCalorie(), selfData.getTotalTime(),
						selfData.getTotalDistance(), selfData.getTotalCalorie()),
				LOG_TAG, new YJSNet.OnRespondCallBack<RspBase>() {
					@Override
					public void onSuccess(RspBase data) {

					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		selfData.fini();
		updateSelfTimer.cancel();
		getReceiverTimer.cancel();
		if (!isAlreadyFinish) {
			updateThisTimeData();
		}

	}

	/**
	 * 动态加载布局dlg_share_sport，用来截取分享到第三方的图片
	 * 
	 * @param text
	 * @param dis
	 * @param cal
	 * @param time
	 */
	private View shareImage(String text, String dis, String cal, String time) {
		View view = LayoutInflater.from(mActivity).inflate(
				R.layout.view_meet_data_to_share, null);

		CustomCircleImageView SendHead = (CustomCircleImageView) view
				.findViewById(R.id.SendHead);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(chaseIntentEntity.my_fid),
				SendHead);

		CustomCircleImageView receiverHead = (CustomCircleImageView) view
				.findViewById(R.id.receiverHead);
		ImageLoaderUtils
				.getInstance()
				.loadImage(
						DataStorageUtils
								.getHeadDownloadUrl(chaseIntentEntity.another_fid),
						receiverHead);

		TextView tvMeetContent = (TextView) view
				.findViewById(R.id.tvMeetContent);
		tvMeetContent.setText(text);
		TextView tvSportDis = (TextView) view.findViewById(R.id.tvSportDis);
		tvSportDis.setText(dis);
		TextView tvSportCal = (TextView) view.findViewById(R.id.tvSportCal);
		tvSportCal.setText(cal);
		TextView tvSportMin = (TextView) view.findViewById(R.id.tvSportMin);
		tvSportMin.setText(time);

		view.setDrawingCacheEnabled(true);

		String url = "http://www.pgyer.com/jlQm";
		ImageView iv = (ImageView) view.findViewById(R.id.download_qrcode);
		iv.setImageBitmap(QRCodeEncoder.encodeAsBitmap(url,
				DimenUtils.dpToPx(100), DimenUtils.dpToPx(100)));
		return view;
	}

	/**
	 * 分享弹窗
	 * 
	 * @param content
	 * @param meetPoint
	 * @param myDis
	 * @param myCal
	 * @param myTime
	 * @param view
	 */
	private void setShareData(String content, String meetPoint,
			SpannableStringBuilder myDis, SpannableStringBuilder myCal,
			SpannableStringBuilder myTime, View view) {
		String title = "分享";
		String imgUrl = "http://img0w.pconline.com.cn/pconline/1309/11/3464151_10.jpg";
		String url = "http://mp.weixin.qq.com/s?__biz=MzI2MDAxODY3Mg==&mid=218040101&idx=1&sn=2396374941dda10297d0b61326c8456e#rd";
		dlgmeetSucc.getShareMeetData().setViewToShare(view);
		dlgmeetSucc.getShareMeetData().setShareData(title, content, null, null);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(chaseIntentEntity.my_fid),
				dlgmeetSucc.getMyHead());
		ImageLoaderUtils
				.getInstance()
				.loadImage(
						DataStorageUtils
								.getHeadDownloadUrl(chaseIntentEntity.another_fid),
						dlgmeetSucc.getReceiverHead());
		dlgmeetSucc.getTvMeetPoint().setText(meetPoint);
		dlgmeetSucc.getTvMeetMyDis().setText(myDis);
		dlgmeetSucc.getTvMeetMyCal().setText(myCal);
		dlgmeetSucc.getTvMeetMyTime().setText(myTime);
		dlgmeetSucc.getLlSendMsg().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						AtyIM.luanchIM(mActivity,
								DataStorageUtils.getOtherPid(),
								receiverNickname);
					}
				});
		dlgmeetSucc.show();
	}

}
