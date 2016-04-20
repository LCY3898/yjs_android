package com.efit.sport.videochat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.yigym.entity.LiveRankEntity;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventNetStateChange;
import com.cy.yigym.utils.HeaderHelper;
import com.easemob.chat.EMCallStateChangeListener;
import com.efit.hxmob.impl.VideoHelperImpl;
import com.efit.sport.R;
import com.efit.sport.utils.DateTimeUtis;
import com.hhtech.base.AppUtils;

import de.greenrobot.event.EventBus;


/**
 * @author tangtt
 */
public class LiveVideoView extends BaseView implements View.OnClickListener{

    private String profileFid;
    private String otherNickname;
    private boolean isCallSender;
    private int videoStartTime = 0;
    private boolean isCallHanger = false;

    private boolean isChatActive = false;
    private boolean isCallTimeout = false;

    private UITimer videoTimer;

    private VideoHelperImpl videoHelper;
    private TextView titleTxt;
    private RelativeLayout llTitle;
    private ImageView ivImageView;
    private SurfaceView remoteView;
    private SurfaceView localView;
    private Button btnCancel;
    private Button btnAccept;
    private LinearLayout rlIncomingConfirm;
    private Button btnStop;
    private RelativeLayout rlReleaseCall;

    public LiveVideoView(Context context) {
        super(context);
    }

    public LiveVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void initView() {
        findViews();
        videoHelper = new VideoHelperImpl();
        remoteView.getHolder().setFixedSize(240, 320);

        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        mNetChangeListener = new NetChangeListener();
        EventBus.getDefault().register(mNetChangeListener);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.hx_live_video_view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAccept:
                videoHelper.answerCall();
                //YtxHelper.setAudioEcEnabled(true);
                isChatActive = true;
                videoStartTime = (int) (System.currentTimeMillis() / 1000);
                startUpdateTime();
                acceptLiveVideo();

                break;
            case R.id.btnCancel:
                videoHelper.refuseCall();
                cancelLiveVideo();
                break;
            case R.id.btnStop:
                videoHelper.hangupCall();
                cancelLiveVideo();
                isCallHanger = true;
                stopUpdateTime();
                break;
        }
    }

    public void startCall(LiveRankEntity entity) {
        videoHelper.init(localView,remoteView);
        videoStartTime = 0;
        isCallSender = true;
        acceptLiveVideo();
        llTitle.setVisibility(VISIBLE);
        titleTxt.setText("正在呼叫" + entity.getNickname() + "..");
        HeaderHelper.load(entity.profileFid, ivImageView);
        profileFid = entity.profileFid;
        otherNickname = entity.getNickname();

        videoHelper.makeCall(entity.pid);


        AppUtils.runOnUIDelayed(20*1000,videoCallTimeoutCheck);
    }

    public void onRecvCall(EventRecvCall event) {
        //TODO: incoming sound
        videoStartTime = 0;
        isCallSender = false;
        titleTxt.setText(event.nickname + "向您发起了视频通话");
        profileFid = event.fid;
        HeaderHelper.load(profileFid, ivImageView);
        otherNickname = event.nickname;
        videoHelper.init(localView, remoteView);
        videoHelper.recvCall();

    }

    private void acceptLiveVideo(){
        isCallHanger = false;
        videoHelper.addCallStateListener(callStateListener);
        rlReleaseCall.setVisibility(VISIBLE);
        rlIncomingConfirm.setVisibility(GONE);
        ivImageView.setVisibility(GONE);
        localView.setVisibility(VISIBLE);
        remoteView.setVisibility(VISIBLE);

        //localView.setZOrderOnTop(true);
    }


    private void cancelLiveVideo() {
        rlReleaseCall.setVisibility(GONE);
        rlIncomingConfirm.setVisibility(VISIBLE);
        llTitle.setVisibility(VISIBLE);
        ivImageView.setVisibility(VISIBLE);
        remoteView.setVisibility(INVISIBLE);
        localView.setVisibility(INVISIBLE);
        setVisibility(GONE);
        isChatActive = false;
        videoHelper.removeCallStateListener(callStateListener);
        videoHelper.fini();
    }


    EMCallStateChangeListener callStateListener = new EMCallStateChangeListener() {

        @Override
        public void onCallStateChanged(final EMCallStateChangeListener.CallState
        callState, final EMCallStateChangeListener.CallError error) {
            switch (callState) {
                case CONNECTING: // 正在连接对方
                    Log.i("LiveVideo", "liveVideo connecting");
                    break;

                case CONNECTED: // 双方已经建立连接
                    Log.i("LiveVideo", "liveVideo connecting");
                    break;

                case ACCEPTED: // 电话接通成功
                    AppUtils.runOnUI(new Runnable() {
                        @Override
                        public void run() {
                            videoHelper.onCallAccepted();
                            onCallAnswer();
                        }
                    });

                    break;
                case DISCONNNECTED: // 电话断了
                    Log.i("LiveVideo", "liveVideo DISCONNNECTED");
                    AppUtils.runOnUI(new Runnable() {
                        @Override
                        public void run() {
                            onCallDisconnected();
                            videoHelper.onCallDisconnected(error);
                        }
                    });

                    break;

                default:
                    break;
            }

        }
    };

    private void onCallDisconnected() {
        videoHelper.hangupCall();
        AppUtils.removeRunOnUI(videoCallTimeoutCheck);
        cancelLiveVideo();
        if(!videoHelper.isAnswered()) {
            return;
        }
        int nowSecs = (int) (System.currentTimeMillis() / 1000);
        String time = "";
        stopUpdateTime();
        if (videoStartTime != 0) {
            int interval = nowSecs - videoStartTime;
            time = interval < 60 ? (interval + "秒") : (interval / 60 + "分" + interval % 60 + "秒");
            time = "视频时长" + time;
            if (isCallHanger) {
                WidgetUtils.showToast("视频已结束," + time);
            } else {
                WidgetUtils.showToast("视频中断," + time);
            }
        } else {
            WidgetUtils.showToast("视频已结束" + time);
        }
    }

    private void onCallAnswer() {
        //TODO: 回声消除
        WidgetUtils.showToast("通话已开始");
        videoStartTime = (int) (System.currentTimeMillis() / 1000);
        startUpdateTime();
        AppUtils.removeRunOnUI(videoCallTimeoutCheck);
        isChatActive = true;
    }

    private void startUpdateTime() {
        if(videoTimer == null) {
            videoTimer = new UITimer();
        }
        videoTimer.cancel();
        videoTimer.schedule(new Runnable() {
            @Override
            public void run() {
                int nowSecs = (int) (System.currentTimeMillis() / 1000);
                int interval = nowSecs - videoStartTime;
                titleTxt.setText(DateTimeUtis.formatIntervalTime(interval));
            }
        }, 1000);
    }

    private void stopUpdateTime() {
        if(videoTimer != null) {
            videoTimer.cancel();
            videoTimer = null;
        }
    }

    public void fini() {
        videoHelper.fini();
        if(videoTimer != null) {
            videoTimer.cancel();
        }
        AppUtils.removeRunOnUI(videoCallTimeoutCheck);
        EventBus.getDefault().unregister(mNetChangeListener);
    }

    private Runnable videoCallTimeoutCheck = new Runnable() {
        @Override
        public void run() {
            WidgetUtils.showToast("视频通话未接听");
            videoHelper.hangupCall();
        }
    };


    private BusEventListener.MainThreadListener mNetChangeListener;

    private void findViews() {
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        llTitle = (RelativeLayout) findViewById(R.id.llTitle);
        ivImageView = (ImageView) findViewById(R.id.ivImageView);
        remoteView = (SurfaceView) findViewById(R.id.vvLive);
        localView = (SurfaceView) findViewById(R.id.localView);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        rlIncomingConfirm = (LinearLayout) findViewById(R.id.rlIncomingConfirm);
        btnStop = (Button) findViewById(R.id.btnStop);
        rlReleaseCall = (RelativeLayout) findViewById(R.id.rlReleaseCall);
    }

    private class NetChangeListener implements BusEventListener.MainThreadListener<EventNetStateChange> {
        @Override
        public void onEventMainThread(EventNetStateChange event) {
            if(!event.isConnected) {
                WidgetUtils.showToast("网络异常");
                if(isChatActive) {
                    cancelLiveVideo();
                    videoHelper.hangupCall();
                }
            }
        }
    };

}
