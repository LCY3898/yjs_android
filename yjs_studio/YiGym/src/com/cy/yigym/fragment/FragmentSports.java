package com.cy.yigym.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.wbs.RspBase;
import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.aty.AtyTargetSetting;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.ble.sport.BaseSportStrategy;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventBleConnect;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqAchieveTarget;
import com.cy.yigym.net.req.ReqDropTarget;
import com.cy.yigym.net.req.ReqGetRecentTarget;
import com.cy.yigym.net.rsp.RspGetRecentTarget;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.ScreenAdapter;
import com.cy.yigym.utils.StringUtils;
import com.cy.yigym.view.CustomCircleProgress;
import com.cy.yigym.view.CustomCircleProgress.OnCenterViewClickListener;
import com.cy.yigym.view.content.DlgShareSport;
import com.efit.sport.R;
import com.hhtech.utils.DimenUtils;
import com.sport.efit.constant.SportType;
import com.zbar.lib.CaptureCodeActivity;
import com.zbar.lib.CaptureConstant;
import com.zxing.qrcode.encode.QRCodeEncoder;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author yyh
 * 
 */
public class FragmentSports extends BaseFragment implements OnClickListener {

	@BindView
	private CustomCircleProgress vProgress;
	private int mTotalProgress;
	private int mCurrentProgress;
	private Activity context;
	@BindView
	private CustomTitleView sportsTitle;
	@BindView
	private RelativeLayout rlSetTarget; // 点击设定目标布局
	@BindView
	private RelativeLayout rlShowTarget; // 设定完目标的布局，未设定目标时不可见
	@BindView
	private LinearLayout li_amount_of_exercise; // 圆形bar下半部
	@BindView
	private TextView currQuantity;
	@BindView
	private TextView remainQuantity;
	@BindView
	private Button btn_start;
	@BindView
	private Button btn_pause;
	@BindView
	private Button btn_finish;
	@BindView
	private Button btn_continue;
	@BindView
	private LinearLayout li_buttongroup;
	@BindView
	private TextView tvDistance;
	@BindView
	private TextView tvMins;
	@BindView
	private TextView tvCalorie;
	@BindView
	private ImageView imgRealScene;

	@BindView
	private View bgProgress;

	private DlgShareSport dlgShareSport;
	private final static int STATE_IDLE = 0;
	private final static int STATE_RUNNING = 1;
	private final static int STATE_PAUSE = 2;
	private final static int STATE_CANCEL = 3;
	private final static int STATE_FINISHED = 5;
	private int currState = STATE_IDLE;

	private SportType sportType = SportType.fromTime(0);

	private UITimer uiTimer;

	private Handler handler = new Handler(Looper.getMainLooper());
	private String taskId = "";

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_sports;
	}

	private final static int REQ_CODE_QRCODE = 1000;

	private final static int SPORT_TARGET_RESULT = 0;

	private BaseSportStrategy sportStrategy=new BaseSportStrategy(sportType);

	@Override
	protected void initView(View contentView, final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		rlShowTarget.setVisibility(View.GONE);
		rlSetTarget.setVisibility(View.VISIBLE);
		sportsTitle.setTitle("挑战");
		sportsTitle.setTxtLeftText("       ");
		sportsTitle.setTxtLeftIcon(R.drawable.header_scan);
		sportsTitle.setTxtLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//WidgetUtils.showToast("二维码扫描");
				Intent intent = new Intent(getActivity(),
						CaptureCodeActivity.class);
				startActivityForResult(intent, REQ_CODE_QRCODE);

			}
		});
		imgRealScene.setOnClickListener(this);
		dlgShareSport = new DlgShareSport(mActivity);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		EventBus.getDefault().register(bleConnectListener);
		uiTimer = new UITimer();
		viewCreated();
		getRecentTarget();
		vProgress.setProgress(0);
		vProgress.setOnCenterViewClickListener(new OnCenterViewClickListener() {

			@Override
			public void onCenterViewClick(float x, float y) {
				if (currState == STATE_RUNNING || currState == STATE_PAUSE) {
					WidgetUtils.showToast("当前正在骑行，无法设置运动目标");
					return;
				}
				Intent intent = new Intent(getActivity(),
						AtyTargetSetting.class);
				startActivityForResult(intent, SPORT_TARGET_RESULT);
			}
		});

		showUnconfigIfNeed();

		btn_start.setOnClickListener(mBtnClickListener);
		btn_pause.setOnClickListener(mBtnClickListener);
		btn_finish.setOnClickListener(mBtnClickListener);
		btn_continue.setOnClickListener(mBtnClickListener);

		if(ScreenAdapter.hasVirtualKey()) {
			ViewGroup.LayoutParams params=bgProgress.getLayoutParams();
		}
	}


	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(bleConnectListener);
		uiTimer.cancel();
		uiTimer = null;
		super.onDestroy();
	}

	private View.OnClickListener mBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			if (view == btn_start) {
				startRiding();
			} else if (view == btn_pause) {
				pauseRiding();
			} else if (view == btn_finish) {
				cancelRiding();
			} else if (view == btn_continue) {
				continueRiding();
			}
		}
	};

	private void startRiding() {

		String bleAddr = DataStorageUtils.getBleAddress();
		if (TextUtils.isEmpty(bleAddr)) {
			showUnconfigPop();
			return;
		}

		currState = STATE_RUNNING;
		btn_start.setVisibility(View.GONE);
		btn_pause.setVisibility(View.VISIBLE);


		uiTimer.schedule(bleConnectionCheckTask, 0, 3 * 1000);
		sportStrategy.start();
		if(!BleConnect.instance().isBleConnected()) {
			sportStrategy.pause();
		}
	}


	private void pauseRiding() {
		currState = STATE_PAUSE;
		btn_pause.setVisibility(View.GONE);
		li_buttongroup.setVisibility(View.VISIBLE);
		sportStrategy.pause();
	}

	private void cancelRiding() {
		DlgTextMsg dlg = new DlgTextMsg(mActivity,
				new DlgTextMsg.ConfirmDialogListener() {
					@Override
					public void onCancel() {
						currState = STATE_CANCEL;
						li_buttongroup.setVisibility(View.GONE);
						toggleTargetState(false);
						vProgress.setProgress(0, false);
						sportStrategy.cancel();
					}

					@Override
					public void onOk() {
						currState = STATE_RUNNING;
						li_buttongroup.setVisibility(View.GONE);
						btn_pause.setVisibility(View.VISIBLE);

						sportStrategy.resume();
					}

					@Override
					public void onCenter() {
					}
				});
		dlg.setBtnString("结束", "继续");
		dlg.show("标题", "亲，要不要再坚持一会儿，坚持就是胜利！");
	}

	private void continueRiding() {
		currState = STATE_RUNNING;
		li_buttongroup.setVisibility(View.GONE);
		btn_pause.setVisibility(View.VISIBLE);

		sportStrategy.resume();
	}


	private BaseSportStrategy.MotionParamChangeListener listener = new BaseSportStrategy.MotionParamChangeListener() {
		@Override
		public void onTargetFinish(int secs, int distanceInMeter, int kcal) {
			currState = STATE_FINISHED;
			toggleTargetState(false);
			achieveTarget(String.valueOf(secs),
					String.valueOf(distanceInMeter), String.valueOf(kcal));
			li_buttongroup.setVisibility(View.GONE);
			sportStrategy.fini();

			final String finishContent = String.format("骑行%.1f公里,时长%d分钟,消耗能量%d卡路里", distanceInMeter / 1000.0, secs / 60, kcal);
			final String finishShareText = String.format("消耗热量%dcal,\n打败全国20％的用户", kcal);

			final SpannableStringBuilder sbFinishDis= StringUtils.creSpanString(new String[]{
					String.format("%.1f", (distanceInMeter / 1000.0)), "km"}
					, new int[]{0xff47bfd1, 0xff47bfd1}, new int[]{30, 17});
			final SpannableStringBuilder sbFinishTime= StringUtils.creSpanString(new String[]{
					String.format("%.1f", (secs / 60.0)), "min"}
					, new int[]{0xff47bfd1,0xff47bfd1}, new int[]{30, 17});
			final SpannableStringBuilder sbFinishCal= StringUtils.creSpanString(new String[]{
					String.format("%d",kcal), "cal"}
					, new int[]{0xff47bfd1,0xff47bfd1}, new int[]{30, 17});

			final String finishDistanceText = String.format("%.1f", (distanceInMeter / 1000.0));
			final String finishSecsText=String.format("%.1f", (secs / 60.0));
			final String finishKcalText=String.format("%d",kcal);
			final View viewFinishToShare=shareImage(finishShareText, finishDistanceText, finishKcalText, finishSecsText);
			DlgTextMsg dlg = new DlgTextMsg(mActivity,
					new DlgTextMsg.ConfirmOkListener() {
						@Override
						public void onCancel() {
						}

						@Override
						public void onOk() {
							setShareData(finishContent,"恭喜"+finishShareText
									,sbFinishDis,sbFinishTime,sbFinishCal,viewFinishToShare);
						}

						@Override
						public void onCenter() {
						}
					});
			dlg.setBtnString("确定","分享");
			dlg.showCancel(true);
			dlg.show("恭喜您，完成目标！\n" + finishShareText);
		}

		@Override
		public void onTargetCancel(int secs, int distanceInMeter, int kcal) {
			if(secs != 0 && distanceInMeter != 0 && kcal != 0) {
				dropTarget(String.valueOf(secs),
						String.valueOf(distanceInMeter), String.valueOf(kcal));
			}

			currQuantity.setText(SportType.getAccomplished(sportType, secs,
					distanceInMeter, kcal));
			//remainQuantity.setText(SportType.getRemain(sportType, secs,
				//	distanceInMeter, kcal));
			int percent = SportType.getAccomplishPercent(sportType, secs,
					distanceInMeter, kcal);
			vProgress.setProgress(percent, false);
			final String content = String.format("骑行%.1f公里,时长%d分钟,消耗能量%d卡路里",distanceInMeter / 1000.0,secs / 60,kcal);
			final String shareText = String.format("消耗热量%dcal,\n打败全国20％的用户", kcal);

			final SpannableStringBuilder sbDis= StringUtils.creSpanString(new String[]{
					String.format("%.1f", (distanceInMeter / 1000.0)), "km"}
					, new int[]{0xff47bfd1, 0xff47bfd1}, new int[]{30, 17});
			final SpannableStringBuilder sbTime= StringUtils.creSpanString(new String[]{
					String.format("%.1f", (secs / 60.0)), "min"}
					, new int[]{0xff47bfd1, 0xff47bfd1}, new int[]{30, 17});
			final SpannableStringBuilder sbCal= StringUtils.creSpanString(new String[]{
					String.format("%d", kcal), "cal"}
					, new int[]{0xff47bfd1, 0xff47bfd1}, new int[]{30, 17});
			final String distanceText = String.format("%.1f", (distanceInMeter / 1000.0));
			final String secsText=String.format("%.1f", (secs / 60.0));
			final String kcalText=String.format("%d", kcal);
			final View viewToShare=shareImage(shareText,distanceText,kcalText,secsText);

			DlgTextMsg dlg = new DlgTextMsg(mActivity,
					new DlgTextMsg.ConfirmOkListener() {
						@Override
						public void onOk() {
							//WidgetUtils.showToast(((TextView)viewToShare.findViewById(R.id.tvSportMin)).getText().toString());
							setShareData(content, "恭喜" + shareText, sbDis, sbTime, sbCal,shareImage(shareText,distanceText,kcalText,secsText));
						}
					});
			dlg.setBtnString("确定","分享");
			dlg.showCancel(true);
			dlg.show("恭喜," + shareText);

		}

		@Override
		public void onMotionParam(int secs, int distanceInMeter, int kcal) {
			currQuantity.setText(SportType.getAccomplished(sportType, secs,
					distanceInMeter, kcal));
//			remainQuantity.setText(SportType.getRemain(sportType, secs,
//					distanceInMeter, kcal));
			int percent = SportType.getAccomplishPercent(sportType, secs,
					distanceInMeter, kcal);
			vProgress.setProgress(percent, false);

			tvDistance.setText(String
					.format("%.2f", distanceInMeter / 1000.0));
			tvMins.setText(String.format("%d:%d:%d",secs/3600, secs / 60,secs % 60));
			tvCalorie.setText(String.format("%d", kcal));
		}
	};

	private void showUnconfigPop() {
		DlgTextMsg dlg = new DlgTextMsg(mActivity,
				new DlgTextMsg.ConfirmDialogListener() {
					@Override
					public void onCancel() {

					}
					@Override
					public void onOk() {

					}

					@Override
					public void onCenter() {
						Intent intent = new Intent(getActivity(),
								CaptureCodeActivity.class);
						startActivityForResult(intent, REQ_CODE_QRCODE);
					}
				});
		dlg.setCenterbtnString("去扫描");
		dlg.show("提示", "请扫描二维码连接数据");

	}


	private void showUnconfigIfNeed() {
		String bleAddr = DataStorageUtils.getBleAddress();
		if (TextUtils.isEmpty(bleAddr)) {
			showUnconfigPop();

		}
	}

	private void ensureConnect(boolean showUncofig) {
		String bleAddr = DataStorageUtils.getBleAddress();
		if (TextUtils.isEmpty(bleAddr)) {
			if(showUncofig) {
				showUnconfigPop();
			}
			return;
		}

		// 如果是同一个设备，并且已经连接上，则不需要再连接
		if (!BleConnect.instance().isBleConnected()
				|| bleAddr.equals(BleConnect.instance().getConnectBleAddr())) {
			BleConnect.instance().connectBle(bleAddr);
		}
	}

	private void toggleTargetState(boolean isSet) {
		if (isSet) {
			rlSetTarget.setVisibility(View.GONE);
			rlShowTarget.setVisibility(View.VISIBLE);
			btn_start.setVisibility(View.VISIBLE);
			btn_pause.setVisibility(View.GONE);
			currQuantity.setText(String.valueOf(0));
			remainQuantity.setText("/ " + sportType.formatSportQuantity());
			li_amount_of_exercise.setVisibility(View.VISIBLE);
			vProgress.setProgress(0, false);
			sportStrategy = BaseSportStrategy.create(sportType);
			sportStrategy.setListener(listener);
		} else {
			rlSetTarget.setVisibility(View.VISIBLE);
			rlShowTarget.setVisibility(View.GONE);
			li_amount_of_exercise.setVisibility(View.GONE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_CODE_QRCODE) {
			if (resultCode == Activity.RESULT_OK) {
				String qrcodeStr = data
						.getStringExtra(CaptureConstant.QRCODE_RESULT);
				boolean isFamilyUser = data.getBooleanExtra(CaptureConstant.QRCODE_IS_FAMILY_USER,true);
				//if(isFamilyUser) {
					DataStorageUtils.setBleAddress(qrcodeStr);
				//}

				Log.d(LOG_TAG, qrcodeStr + "蓝牙地址");
				WidgetUtils.showToast("扫描成功！");
				ensureConnect(true);
			}
		}
		if (requestCode == SPORT_TARGET_RESULT) {
			if (resultCode == Activity.RESULT_OK) {
				sportType = (SportType) data
						.getSerializableExtra(SportType.INTENT_KEY);

				taskId = data.getStringExtra("task_id");
				toggleTargetState(true);
			} else {
				WidgetUtils.showToast("设定目标失败");
			}
		}
	}

	/**
	 * 获取当前目标
	 */
	private void getRecentTarget() {
		YJSNet.send(new ReqGetRecentTarget(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGetRecentTarget>() {
					@Override
					public void onSuccess(RspGetRecentTarget data) {

					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	/**
	 * 完成目标
	 * 
	 * @param time
	 * @param distance
	 * @param calorie
	 */
	private void achieveTarget(String time, String distance, String calorie) {
		YJSNet.send(new ReqAchieveTarget(taskId,time, distance, calorie), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspBase>() {
					@Override
					public void onSuccess(RspBase data) {
						// TODO 弹出分享数据的窗口
					}

					@Override
					public void onFailure(String errorMsg) {

						WidgetUtils.showToast("发生错误" + errorMsg);
					}
				});
	}

	/**
	 * 放弃目标
	 * 
	 * @param time
	 * @param distance
	 * @param calorie
	 */
	private void dropTarget(String time, String distance, String calorie) {

		YJSNet.send(new ReqDropTarget(taskId,time, distance, calorie), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspBase>() {
					@Override
					public void onSuccess(RspBase data) {
						// TODO 弹出分享数据的窗口
					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	public void viewCreated() {
		Activity activity = context;
		mTotalProgress = 1000; // 总共100%
		mCurrentProgress = 0; // 当前位置

		//tangtaotao_20151019  comment these code to fix memory leak
		/*new Thread(new Runnable() { // 新开一个线程，从0到100%隔0.1秒逐渐增加

					@Override
					public void run() {
						while (mCurrentProgress < mTotalProgress) {
							mCurrentProgress += 1;
							try {
								Thread.sleep(200);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}).start();*/
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgRealScene:
			//setShareData("","",100+"",200+"",300+"");
			//dlgShareSport.show();
			break;
		}
	}



	private CustomCircleImageView shareHead;  //用户自己的头像
	private TextView tvSharehead; //  用户的昵称
	private TextView tvSportDis; //  用户运动的距离
	private TextView tvSportCal; //  用户运动消耗的卡路里
	private TextView tvSportMin; //  用户运动所用的时长
	private TextView tvRuningshare; //用户运动提示语句
	private View view;

	/**
	 * 动态加载布局dlg_share_sport，用来截取分享到第三方的图片
	 * @param text
	 * @param dis
	 * @param cal
	 * @param time
	 */
	private View shareImage(String text,String dis,String cal,String time){
		view =LayoutInflater.from(mActivity).inflate(R.layout.dlg_share_sport,null);
		shareHead=(CustomCircleImageView)view.findViewById(R.id.shareHead);
		ImageLoaderUtils.getInstance().loadImage(DataStorageUtils
				.getHeadDownloadUrl(DataStorageUtils.getCurUserProfileFid()),shareHead);
		tvSharehead=(TextView)view.findViewById(R.id.tvSharehead);
		tvSharehead.setText(CurrentUser.instance().getNickname());
		tvSportDis=(TextView)view.findViewById(R.id.tvSportDis);
		tvSportDis.setText(dis);
		tvSportCal=(TextView)view.findViewById(R.id.tvSportCal);
		tvSportCal.setText(cal);
		tvSportMin=(TextView)view.findViewById(R.id.tvSportMin);
		tvSportMin.setText(time);
		tvRuningshare=(TextView)view.findViewById(R.id.tvRuningshare);
		tvRuningshare.setText(text);

		view.setDrawingCacheEnabled(true);

		String url = "http://www.pgyer.com/jlQm";
		ImageView iv = (ImageView) view.findViewById(R.id.download_qrcode);
		iv.setImageBitmap(QRCodeEncoder.encodeAsBitmap(url, DimenUtils.dpToPx(100),DimenUtils.dpToPx(100)));

		return view;
	}


	/**
	 * 设置分享数据
	 */
	private void setShareData(String content,String text,SpannableStringBuilder distance
			,SpannableStringBuilder secs,SpannableStringBuilder cal,View view) {
		String title = "E健身健康生活";
		String imgUrl = "http://img0w.pconline.com.cn/pconline/1309/11/3464151_10.jpg";
		String url = "http://mp.weixin.qq.com/s?__biz=MzI2MDAxODY3Mg==&mid=218040101&idx=1&sn=2396374941dda10297d0b61326c8456e#rd";
		dlgShareSport.getSvSportData().setShareData(title, content, null, null);
		dlgShareSport.getSvSportData().setViewToShare(view);  //截取要分享到第三方的图片
		dlgShareSport.getTvSportShareContent().setText(text);
		dlgShareSport.getTvShareSportDis().setText(distance);
		dlgShareSport.getTvShareSportTime().setText(secs);
		dlgShareSport.getTvShareSportCal().setText(cal);
		dlgShareSport.show();
	}


	private Runnable bleConnectionCheckTask = new Runnable() {
		@Override
		public void run() {
			if(currState == STATE_RUNNING) {
				if(!BleConnect.instance().isBleConnected()) {
					ensureConnect(false);
					sportStrategy.pause();
				} else {
					//sportStrategy.resume();
				}
			}
		}
	};


	private DlgTextMsg dlgConnectDlg;
	private BusEventListener.MainThreadListener<EventBleConnect> bleConnectListener =
			new BusEventListener.MainThreadListener<EventBleConnect>() {
				@Override
				public void onEventMainThread(EventBleConnect event) {
					if (event.isConnect) {
						if (dlgConnectDlg != null) {
							dlgConnectDlg.dismiss();
						}
						if(currState == STATE_RUNNING) {
							if(event.isWheelRunning) {
								sportStrategy.resume();
							} else {
								sportStrategy.pause();
							}

						}
						//WidgetUtils.showToast("设备已重新连接");
					} else {
						if (currState == STATE_RUNNING) {
							sportStrategy.pause();
							WidgetUtils.showToast("设备连接已断开");
						}
					}
				}
	};


}
