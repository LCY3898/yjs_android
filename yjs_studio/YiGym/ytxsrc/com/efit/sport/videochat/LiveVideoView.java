package com.efit.sport.videochat;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.entity.LiveRankEntity;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventNetStateChange;
import com.cy.yigym.utils.HeaderHelper;
import com.efit.sport.R;


import com.efit.sport.p2p.CameraHelper;
import com.efit.sport.p2p.SDKCoreHelper;
import com.efit.sport.p2p.VoipCallback;
import com.efit.sport.p2p.YtxHelper;
import com.efit.sport.p2p.internal.CallFailReason;
import com.efit.sport.utils.DateTimeUtis;
import com.hhtech.base.AppUtils;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.SdkErrorCode;

import org.webrtc.videoengine.ViERenderer;

import de.greenrobot.event.EventBus;

/**
 * Created by ejianshen on 15/9/14.
 */
public class LiveVideoView extends BaseView implements View.OnClickListener{
    @BindView
    private Button btnCancel,btnAccept,btnStop;
    @BindView
    private TextView titleTxt;
    @BindView
    private View llTitle, rlReleaseCall, rlIncomingConfirm,rlLiveVideo;
    @BindView
    private SurfaceView vvLive;

    @BindView
    private RelativeLayout localView;

    @BindView
    private ImageView ivImageView;

    private String callId;
    private String profileFid;
    private String otherNickname;
    private boolean isCallSender;
    private int videoStartTime = 0;
    private boolean isCallHanger = false;

    private boolean isChatActive = false;
    private boolean isCallTimeout = false;

    private UITimer videoTimer;
    public LiveVideoView(Context context) {
        super(context);
    }

    public LiveVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void initView() {
        YtxHelper.setAudioEcEnabled(true);
        vvLive.getHolder().setFixedSize(240, 320);
        YtxHelper.selectCamera(CameraHelper.instance().getDefaultCameraId(),
                CameraHelper.instance().getCameraCapbilityIndex(), 15, YtxHelper.Cameratation.Rotate_Auto, true);
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        mNetChangeListener = new NetChangeListener();
        EventBus.getDefault().register(mNetChangeListener);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.view_live_video;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAccept:
                YtxHelper.acceptCall(callId);
                ECDevice.getECVoIPSetupManager().enableLoudSpeaker(true);
                YtxHelper.setAudioEcEnabled(true);
                isChatActive = true;
                videoStartTime = (int) (System.currentTimeMillis() / 1000);
                startUpdateTime();
                acceptLiveVideo();
                YtxHelper.setVideView(vvLive, null);
                break;
            case R.id.btnCancel:
                YtxHelper.rejectCall(callId, 3);
                cancelLiveVideo();
                break;
            case R.id.btnStop:
                YtxHelper.releaseCall(callId);
                cancelLiveVideo();
                isCallHanger = true;
                stopUpdateTime();
                break;
        }
    }

    public void startCall(LiveRankEntity entity) {
        videoStartTime = 0;
        isCallSender = true;
        acceptLiveVideo();
        llTitle.setVisibility(VISIBLE);
        titleTxt.setText("正在呼叫" + entity.getNickname() + "..");
        HeaderHelper.load(entity.profileFid, ivImageView);
        profileFid = entity.profileFid;
        otherNickname = entity.getNickname();
        YtxHelper.setVideView(vvLive, null);

        callId = YtxHelper.makeVideoCall(SDKCoreHelper.personToUid(entity.pid));
        AppUtils.runOnUIDelayed(15*1000,videoCallTimeoutCheck);
    }

    public void onRecvCall(EventRecvCall event) {
        if(!YtxHelper.isIncomingSoundEnabled()) {
            YtxHelper.setIncomingSoundEnabled(true);
        }
        videoStartTime = 0;
        isCallSender = false;
        titleTxt.setText(event.nickname + "向您发起了视频通话");
        callId = event.mCallId;
        profileFid = event.fid;
        HeaderHelper.load(profileFid, ivImageView);
        otherNickname = event.nickname;
    }

    private void acceptLiveVideo(){
        isCallHanger = false;
        YtxHelper.setVoipCbk(voipCbk);
        rlReleaseCall.setVisibility(VISIBLE);
        rlIncomingConfirm.setVisibility(GONE);
        //llTitle.setVisibility(GONE);

        ivImageView.setVisibility(GONE);

        localView.setVisibility(VISIBLE);
        displayLocalView(getContext());
        vvLive.setVisibility(VISIBLE);
        vvLive.setZOrderOnTop(true);
    }


    private void cancelLiveVideo() {
        rlReleaseCall.setVisibility(GONE);
        rlIncomingConfirm.setVisibility(VISIBLE);
        llTitle.setVisibility(VISIBLE);
        ivImageView.setVisibility(VISIBLE);
        vvLive.setVisibility(GONE);
        localView.setVisibility(GONE);
        setVisibility(GONE);
        isChatActive = false;
    }

    public void displayLocalView(Context context) {
        SurfaceView surfaceView = ViERenderer.CreateLocalRenderer(context);
        surfaceView.setZOrderOnTop(true);
        localView.removeAllViews();
        localView.setBackgroundColor(getResources().getColor(
                R.color.white));
        localView.addView(surfaceView);
    }


    VoipCallback.VoipCbk voipCbk = new VoipCallback.VoipCbk() {
        @Override
        public void onVideoRatioChanged(int width, int height) {
            if(width != 0 && height != 0) {
                vvLive.getHolder().setFixedSize(width , height);
            }

        }

        @Override
        public void onCallRelease(String callId) {
            int nowSecs = (int) (System.currentTimeMillis() / 1000);
            String time = "";
            stopUpdateTime();
            if(videoStartTime != 0) {
                int interval = nowSecs - videoStartTime;
                time = interval < 60? (interval + "秒"):(interval / 60 + "分" + interval %60 + "秒");
                time = "视频时长" + time;
                if(isCallHanger) {
                    WidgetUtils.showToast("视频已结束,"+ time);
                } else {
                    WidgetUtils.showToast("视频中断,"+ time);
                }
            } else {
                WidgetUtils.showToast("视频已结束" + time);
            }

            cancelLiveVideo();
            YtxHelper.releaseCall(callId);
            AppUtils.removeRunOnUI(videoCallTimeoutCheck);
        }

        @Override
        public void onCallAnswer(String callId) {
            WidgetUtils.showToast("通话已开始");
            YtxHelper.setAudioEcEnabled(true);
            videoStartTime = (int) (System.currentTimeMillis() / 1000);
            startUpdateTime();
            AppUtils.removeRunOnUI(videoCallTimeoutCheck);
            isChatActive = true;
            ECDevice.getECVoIPSetupManager().enableLoudSpeaker(true);
        }

        @Override
        public void onCallFail(String callId,int reason) {
            WidgetUtils.showToast(CallFailReason.getCallFailReason(reason));
            cancelLiveVideo();
            if(reason != SdkErrorCode.REMOTE_CALL_DECLINED) {
                YtxHelper.releaseCall(callId);
            }
            AppUtils.removeRunOnUI(videoCallTimeoutCheck);
        }

        @Override
        public void onCallProcessing(String callId) {
            WidgetUtils.showToast("正在呼叫对方..");
        }
    };

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
        },1000);
    }

    private void stopUpdateTime() {
        if(videoTimer != null) {
            videoTimer.cancel();
            videoTimer = null;
        }
    }

    public void fini() {
        if(!TextUtils.isEmpty(callId)) {
            YtxHelper.releaseCall(callId);
        }
        if(videoTimer != null) {
            videoTimer.cancel();
        }
        YtxHelper.setVoipCbk(null);
        AppUtils.removeRunOnUI(videoCallTimeoutCheck);
        EventBus.getDefault().unregister(mNetChangeListener);
    }

    private Runnable videoCallTimeoutCheck = new Runnable() {
        @Override
        public void run() {
            WidgetUtils.showToast("视频通话未接听");
            YtxHelper.releaseCall(callId);
        }
    };


    private BusEventListener.MainThreadListener mNetChangeListener;

    private class NetChangeListener implements BusEventListener.MainThreadListener<EventNetStateChange> {
        @Override
        public void onEventMainThread(EventNetStateChange event) {
            if(!event.isConnected) {
                WidgetUtils.showToast("网络异常");
                if(isChatActive) {
                    cancelLiveVideo();
                    YtxHelper.releaseCall(callId);
                }
            }
        }
    };

}
