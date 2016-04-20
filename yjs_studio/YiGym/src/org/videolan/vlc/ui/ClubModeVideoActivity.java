package org.videolan.vlc.ui;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.widgetlibrary.view.AppMsg;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.entity.LiveVideoEntity;
import com.cy.yigym.entity.LiveVideoSportData;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.fragment.DlgClubInfoShow;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqClearLiveCourseData;
import com.cy.yigym.net.req.ReqExitLive;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.live.ClubLiveRankView;
import com.cy.yigym.view.content.live.ClubModeLiveSportDatas;
import com.cy.yigym.view.content.live.LiveInfoView;
import com.efit.sport.R;
import com.efit.sport.livecast.VideoPlayerListener;
import com.efit.sport.utils.DateTimeUtis;
import com.efit.sport.videochat.EventRecvCall;
import com.efit.sport.videochat.LiveVideoView;
import com.efit.sport.videochat.VideoChatHelper;
import com.hhtech.utils.NetUtils;
import com.hhtech.utils.UITimer;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class ClubModeVideoActivity extends BaseFragmentActivity {

    /**
     * 更新排名数据
     */
    private ClubLiveRankView mRankView;
    private LiveInfoView mInfoView;
    private LiveVideoView liveVideoView;
    private ClubModeLiveSportDatas sportData;
    private ProgressBar progressBar;
    private ImageView ivRePlay;
    private View rlPlayAgain;

    private double lastCalorie = 0;
    private double lastDistance = 0;

    private ImageView bgImage;

    private CustomTitleView vTitle;
    // 自动隐藏顶部和底部View的时间
    private static final int HIDE_TIME = 5000;
    private boolean isCanStartAnim = true;

    private Animation animTopHide;
    private Animation animBottomHide;

    private Animation animTopEnter;
    private Animation animBottomEnter;

    private long lastClickTime = 0;
    private LiveVideoEntity liveVideoEntity;

    private TextView tvTimeCutDown;

    private UITimer uiTimer = new UITimer();
    //tangtt 是否为健身房模式,健身房模式则不现实直播，只有数据和排名
    private boolean isInClubMode;
    private TextView tvClubTimeCutDown;


    private TextView tvClearSportData;//清除数据
    private TextView tvEndLiveCourse;//结束课程

    private long endTime;

    private Handler handler = new Handler();

    private Runnable checkAndShowTip = new Runnable() {

        public void run() {
            doCheckAndShowTip();
            handler.removeCallbacks(checkAndShowTip);
        }

    };

    private void doCheckAndShowTip() {
        BleConnect instance = BleConnect.instance();
        if (instance == null) {
            WidgetUtils.showToast("请检查蓝牙");
        }

        if (instance.isBleConnected()) {
            WidgetUtils.showToast("蓝牙已连上");
        } else {
            AppMsg.makeText(this, "蓝牙还未连接，请骑动自行车或重新扫描二维码", AppMsg.STYLE_ALERT).show();
        }
    }

    @Override
    protected boolean isBindViewByAnnotation() {
        return false;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_club_video_vlc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        handler.postDelayed(checkAndShowTip, 12 * 1000);
    }

    @Override
    protected void initView() {
        isInClubMode = DataStorageUtils.isInClubMode();

        liveVideoEntity = (LiveVideoEntity) getIntent().
                getSerializableExtra("live_course");
        vTitle = (CustomTitleView) findViewById(R.id.vTitle);
        sportData = (ClubModeLiveSportDatas) findViewById(R.id.sportData);
        liveVideoView = (LiveVideoView) findViewById(R.id.liveVideoView);
        mRankView = (ClubLiveRankView) findViewById(R.id.liveRankView);
        mInfoView = (LiveInfoView) findViewById(R.id.liveInfoView);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        rlPlayAgain = findViewById(R.id.rlPlayAgain);
        ivRePlay = (ImageView) findViewById(R.id.ivRePlay);

        mRankView.setInfoView(mInfoView, mRankView, LOG_TAG);
        mInfoView.setVideoView(liveVideoView);
        //mInfoView.setLiveRankView(mRankView);
        sportData.setLogTag(LOG_TAG);
        sportData.setSportDataListener(mRankView);
        LiveVideoSportData data = DataStorageUtils.getLiveVideoData();
        if (data != null && !TextUtils.isEmpty(data.courseId)
                && data.courseId.equals(DataStorageUtils.getCourseId())) {
            sportData.initData(data.distance, data.seconds, data.calorie);
        } else {
            sportData.initData(0, 0, 0);
        }

        vTitle.setTxtLeftText("    ");
        vTitle.setTxtLeftIcon(R.drawable.header_back);

        tvTimeCutDown = new TextView(this);
        tvTimeCutDown.setTextColor(0xffedf1f6);
        tvTimeCutDown.setTextSize(14);
        tvTimeCutDown.setText("00:00");
        vTitle.addLeftView(tvTimeCutDown, CustomTitleView.ViewAddDirection.right, null);
        vTitle.setTitle("课程直播");
        vTitle.setTxtLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        vTitle.setBackgroundColor(0xcc363d4d);
        initAnim();
        tvClubTimeCutDown = (TextView) findViewById(R.id.tvClubTimeCutDown);
//        uiTimer = new UITimer();
        uiTimer.schedule(new Runnable() {
            @Override
            public void run() {
                int nowSec = (int) (System.currentTimeMillis() / 1000);
                int secsDiff = Math.max((int) liveVideoEntity.endTime - nowSec, 0);
                tvTimeCutDown.setText(DateTimeUtis.formatTimeDuration(secsDiff));
                tvClubTimeCutDown.setText(DateTimeUtis.formatTimeDuration(secsDiff));
            }
        }, 1000);

        if (isInClubMode) {
            progressBar.setVisibility(View.GONE);
            rlPlayAgain.setVisibility(View.GONE);
            ivRePlay.setVisibility(View.GONE);
            tvClubTimeCutDown.setVisibility(View.VISIBLE);
            bgImage = (ImageView) findViewById(R.id.bgImg);
            bgImage.setImageResource(R.drawable.club_bg);
            //bgImage.setOnTouchListener(touchListener);

            try {
                LinearLayout.LayoutParams params =
                        (LinearLayout.LayoutParams) mRankView.getLayoutParams();
                params.weight = 0.5f;
                mRankView.setLayoutParams(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            uiTimer.schedule(new Runnable() {
                @Override
                public void run() {
                    int nowSec = (int) (System.currentTimeMillis() / 1000);
                    int secsDiff = Math.max((int) liveVideoEntity.endTime - nowSec, 0);
                    tvClubTimeCutDown.setText(DateTimeUtis.formatTimeDuration(secsDiff));
                    if (secsDiff == 0) {
                        showFinalRankInfo();
                        uiTimer.cancel();
                    }
                }
            }, 1000);
        }
        tvClearSportData = (TextView) findViewById(R.id.tvClearSportData);
        tvClearSportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DlgTextMsg dlgTextMsg = new DlgTextMsg(mContext, new DlgTextMsg.ConfirmDialogListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onOk() {
                        endTime = System.currentTimeMillis() / 1000 - liveVideoEntity.startTime;
                        clearLiveCourseData();
                        //数据清零
                        sportData.initData(0, 0, 0);
                    }
                });
                dlgTextMsg.setBtnString("取消", "确定");
                dlgTextMsg.show("提示", "您确定要清除当前的运动数据吗？");
            }
        });

        tvEndLiveCourse = (TextView) findViewById(R.id.tvEndLiveCourse);
        tvEndLiveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFinalRankInfo();
            }
        });
    }

    private void showFinalRankInfo() {
        String myRank = mRankView.getMyRank();
        String distance = sportData.getDistance() + "km";
        String speed = sportData.getSpeed() + "km/h";
        String res = sportData.getRes() + "%";
        String rpm = sportData.getRpm() + "rpm";
        String calorie = sportData.getCalorie() + "cal";
        DlgClubInfoShow digShow = new DlgClubInfoShow(new DlgClubInfoShow.RankInfoBean(myRank, distance, speed, res, rpm, calorie));
        digShow.setCancelable(false);
        digShow.setCallBack(new DlgClubInfoShow.ExitCallBack() {
            @Override
            public void onExit() {

                //Toast.makeText(mContext, "执行退出操作", Toast.LENGTH_SHORT).show();
                DlgTextMsg dlgTextMsg = new DlgTextMsg(mActivity, new DlgTextMsg.ConfirmDialogListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOk() {
                        sportData.initData(0, 0, 0);
                        if((System.currentTimeMillis()/1000-liveVideoEntity.endTime)<0) {
                            clearLiveCourseData();
                        }
                        finish();
                    }
                });
                dlgTextMsg.setBtnString("取消", "确定");
                dlgTextMsg.show("提示", "如果执行此操作的话您在当前直播中的数据将会被清除，您确定要退出该课程吗？");
            }
        });
        digShow.show(getFragmentManager(), "DlgClubInfoShow");
    }


    private void initAnim() {
        animTopHide = AnimationUtils.loadAnimation(this,
                R.anim.option_leave_from_top);
        animBottomHide = AnimationUtils.loadAnimation(this,
                R.anim.option_leave_from_bottom);

        animTopEnter = AnimationUtils.loadAnimation(this,
                R.anim.option_entry_from_top);
        animBottomEnter = AnimationUtils.loadAnimation(this,
                R.anim.option_entry_from_bottom);

        animTopHide.setAnimationListener(new AnimationImp() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                vTitle.setVisibility(View.GONE);
            }
        });
        animBottomHide.setAnimationListener(new AnimationImp() {
            @Override
            public void onAnimationEnd(Animation animation) {
                sportData.setVisibility(View.GONE);
                isCanStartAnim = true;
            }
        });
    }


    @Override
    protected void initData() {

        initECDev();

        if (!NetUtils.isNetConnected(mContext)) {
            WidgetUtils.showToast("网络不可用,请先开启网络");
        }
        //showOrHide();
    }


    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                long time = System.currentTimeMillis();
                if (time - lastClickTime >= 500) {
                    showOrHide();
                    lastClickTime = time;
                }
            }
            return true;
        }
    };

    private VideoPlayerListener playerListener = new VideoPlayerListener() {
        @Override
        public void onNetworkError(int code) {
            stopOnError();
        }

        @Override
        public void onServerError() {
            stopOnError();
        }

        @Override
        public void onUriAddrError() {
            stopOnError();
        }

        @Override
        public void onBuffering(boolean isBuffering) {
            progressBar.setVisibility(isBuffering ? View.VISIBLE : View.GONE);
        }
    };

    private void stopOnError() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        rlPlayAgain.setVisibility(View.VISIBLE);
        ivRePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtils.isNetConnected(mContext)) {
                    WidgetUtils.showToast("您的网络未连接，请先检查网络连接");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                rlPlayAgain.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mRankView.pause();

    }

    @Override
    public void onResume() {
        super.onResume();
        mRankView.resume();
        MobclickAgent.onEvent(this, "JoinLiveCastPage");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fini();
        LiveVideoSportData data = sportData.getSportData(DataStorageUtils
                .getCourseId());
        DataStorageUtils.setLiveVideoData(data);
        mRankView.fini();
        sportData.fini();
        liveVideoView.fini();
        if (uiTimer != null) {
            uiTimer.cancel();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        exitLive();
    }

    private BusEventListener.MainThreadListener incomingCallListener = new BusEventListener.MainThreadListener<EventRecvCall>() {
        @Override
        public void onEventMainThread(EventRecvCall event) {
            try {
                EventBus.getDefault().cancelEventDelivery(event);
            } catch (Exception e) {
            }

            liveVideoView.onRecvCall(event);
            //liveVideoView.setVisibility(View.VISIBLE);
        }
    };

    private void initECDev() {
        VideoChatHelper.setIncomingSoundEnabled(true);
        EventBus.getDefault().register(incomingCallListener);
    }

    private void fini() {
        VideoChatHelper.setIncomingSoundEnabled(false);
        EventBus.getDefault().unregister(incomingCallListener);
    }

    /**
     * 启动头部动画
     */
    private void showOrHide() {
        if (vTitle.getVisibility() == View.VISIBLE) {
            vTitle.clearAnimation();
            vTitle.startAnimation(animTopHide);
            sportData.startAnimation(animBottomHide);
        } else {
            vTitle.setVisibility(View.VISIBLE);
            vTitle.clearAnimation();
            vTitle.startAnimation(animTopEnter);

            sportData.setVisibility(View.VISIBLE);
            sportData.clearAnimation();
            sportData.startAnimation(animBottomEnter);
            //mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    }

    private Runnable hideRunnable = new Runnable() {

        @Override
        public void run() {
            showOrHide();
        }
    };

    private class AnimationImp implements AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    }

    /**
     * 退出直播
     */
    private void exitLive() {
        YJSNet.send(new ReqExitLive(DataStorageUtils.getCourseId()), LOG_TAG, new YJSNet.OnRespondCallBack<RspBase>() {
            @Override
            public void onSuccess(RspBase data) {
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }

    /**
     * 当做了清除数据操作的时候，告知服务端清除当前直播中的个人数据
     */
    private void clearLiveCourseData() {
        YJSNet.send(new ReqClearLiveCourseData(DataStorageUtils.getCourseId(), endTime), LOG_TAG, new YJSNet.OnRespondCallBack<RspBase>() {
            @Override
            public void onSuccess(RspBase data) {
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }

}
