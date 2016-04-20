package com.efit.sport.videochat;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.yigym.Task.AppTaskManager;
import com.cy.yigym.entity.LiveRankEntity;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventNetStateChange;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetUserInfo;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;
import com.efit.sport.R;
import com.efit.sport.nim.NimHelper;
import com.efit.sport.nim.ui.AVChatImpl;
import com.efit.sport.utils.DateTimeUtis;
import com.hhtech.base.AppUtils;
import com.netease.nimlib.sdk.avchat.AVChatManager;

import de.greenrobot.event.EventBus;


/**
 * @author tangtt
 */
public class LiveVideoView extends BaseView implements View.OnClickListener{

    private String profileFid;
    private String remoteAccount;
    private String selfVoipAccount;
    private String otherNickname;
    private boolean isCallSender;
    private int videoStartTime = 0;
    private boolean isCallHanger = false;

    private boolean isChatActive = false;
    private boolean isCallTimeout = false;

    private UITimer videoTimer;

    private AVChatImpl videoHelper;
    private TextView titleTxt;
    private RelativeLayout llTitle;
    private ImageView ivImageView;
    private Button btnCancel;
    private Button btnAccept;
    private LinearLayout rlIncomingConfirm;
    private Button btnStop;
    private RelativeLayout rlReleaseCall;

    private SurfaceView mCapturePreview;
    private LinearLayout smallSizePreviewLayout;
    private LinearLayout largeSizePreviewLayout;
    private SurfaceView smallSizeSurfaceView;

    public LiveVideoView(Context context) {
        super(context);
    }

    public LiveVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void initView() {


        selfVoipAccount = DataStorageUtils.getNetEaseAccount().accid;
        findViews();
        videoHelper = new AVChatImpl();
        videoHelper.init((Activity) getContext(), this);
        videoHelper.setCapturePreview(mCapturePreview);
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        mNetChangeListener = new NetChangeListener();
        EventBus.getDefault().register(mNetChangeListener);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.nim_live_video;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAccept:
                videoHelper.answerCall();
                isChatActive = true;
                videoStartTime = (int) (System.currentTimeMillis() / 1000);
                startUpdateTime();
                acceptLiveVideo();
                initPreview(selfVoipAccount, remoteAccount);
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
        remoteAccount = NimHelper.personToUid(entity.voipAccount);
        videoStartTime = 0;
        isCallSender = true;
        acceptLiveVideo();
        llTitle.setVisibility(VISIBLE);
        titleTxt.setText("正在呼叫" + entity.getNickname() + "..");
        HeaderHelper.load(entity.profileFid, ivImageView);
        profileFid = entity.profileFid;
        otherNickname = entity.getNickname();

        videoHelper.makeCall(remoteAccount);


        AppUtils.runOnUIDelayed(20*1000,videoCallTimeoutCheck);
        initPreview(selfVoipAccount, remoteAccount);
    }

    public void onRecvCall(final EventRecvCall event) {
        //TODO: incoming sound
        remoteAccount = event.avChatData.getAccount();
        videoStartTime = 0;
        isCallSender = false;
        titleTxt.setText("有人" + "向您发起了视频通话");
        profileFid = "";
        otherNickname = "";

        AppTaskManager.execute(new GetUserInfoByAccid(remoteAccount, new OnFinishGet() {
            @Override
            public void onFinish(RspGetUserInfo data) {
                titleTxt.setText(data.data.personInfo.nick_name + "向您发起了视频通话");
                HeaderHelper.load(data.data.personInfo.profile_fid, ivImageView);
                LiveVideoView.this.setVisibility(VISIBLE);
                //videoHelper.receiveCall(event.avChatData);
            }

            @Override
            public void onError(String errorMsg) {
//                LiveVideoView.this.setVisibility(GONE); //获取不到昵称和头像 还要不要显示呼叫窗口
            }
        }));

        videoHelper.receiveCall(event.avChatData);

    }

    private void acceptLiveVideo(){
        isCallHanger = false;
        rlReleaseCall.setVisibility(VISIBLE);
        rlIncomingConfirm.setVisibility(GONE);
        ivImageView.setVisibility(GONE);
        /*localView.setVisibility(VISIBLE);
        remoteView.setVisibility(VISIBLE);*/

        //localView.setZOrderOnTop(true);
    }


    private void cancelLiveVideo() {
        rlReleaseCall.setVisibility(GONE);
        rlIncomingConfirm.setVisibility(VISIBLE);
        llTitle.setVisibility(VISIBLE);
        ivImageView.setVisibility(VISIBLE);
        /*remoteView.setVisibility(INVISIBLE);
        localView.setVisibility(INVISIBLE);*/
        setVisibility(GONE);
        isChatActive = false;
        largeSizePreviewLayout.removeAllViews();
        smallSizePreviewLayout.removeAllViews();
        videoHelper.fini();
    }


    public void onConnected() {
        AppUtils.runOnUI(new Runnable() {
            @Override
            public void run() {
                onCallAnswer();
                initSmallSurfaceView(selfVoipAccount);
            }
        });
    }

    public void onUserJoin(final String account) {
        AppUtils.runOnUI(new Runnable() {
            @Override
            public void run() {
                initLargeSurfaceView(account);
            }
        });
    }

    /*EMCallStateChangeListener callStateListener = new EMCallStateChangeListener() {

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
    };*/

    public void onDisconnected(String account) {
        videoHelper.hangupCall();
        AppUtils.removeRunOnUI(videoCallTimeoutCheck);
        cancelLiveVideo();
        if(!videoHelper.isCallEstablished()) {
            return;
        }
        videoHelper.setCallEstablished(false);
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
            cancelLiveVideo();
            videoHelper.hangupCall();
        }
    };


    private BusEventListener.MainThreadListener mNetChangeListener;

    private void findViews() {

        titleTxt = (TextView) findViewById(R.id.titleTxt);
        llTitle = (RelativeLayout) findViewById(R.id.llTitle);
        ivImageView = (ImageView) findViewById(R.id.ivImageView);
        /*remoteView = (SurfaceView) findViewById(R.id.vvLive);
        localView = (SurfaceView) findViewById(R.id.localView);*/
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        rlIncomingConfirm = (LinearLayout) findViewById(R.id.rlIncomingConfirm);
        btnStop = (Button) findViewById(R.id.btnStop);
        rlReleaseCall = (RelativeLayout) findViewById(R.id.rlReleaseCall);

        mCapturePreview = (SurfaceView) findViewById(R.id.capture_preview);
        mCapturePreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        smallSizePreviewLayout = (LinearLayout) findViewById(R.id.small_size_preview);
        largeSizePreviewLayout = (LinearLayout) findViewById(R.id.large_size_preview);
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
    }


    private void initPreview(String selfAccount,String otherAccount) {
        initLargeSurfaceView(otherAccount);
        initSmallSurfaceView(selfAccount);
    }

    /**
     * 大图像surfaceview 初始化
     * @param account 显示视频的用户id
     */
    public void initLargeSurfaceView(String account){
        /**
         * 获取视频SurfaceView，加入到自己的布局中，用于呈现视频图像
         * account 要显示视频的用户帐号
         */
        SurfaceView surfaceView = AVChatManager.getInstance().getSurfaceRender(account);

        if (surfaceView != null) {
            addIntoLargeSizePreviewLayout(surfaceView);
        }
    }

    /**
     * 小图像surfaceview 初始化
     * @param account
     * @return
     */
    public void initSmallSurfaceView(String account){
        /**
         * 获取视频SurfaceView，加入到自己的布局中，用于呈现视频图像
         * account 要显示视频的用户帐号
         */
        SurfaceView surfaceView = AVChatManager.getInstance().getSurfaceRender(account);
        if (surfaceView != null) {
            smallSizeSurfaceView = surfaceView;
            addIntoSmallSizePreviewLayout();
        }
    }

    /**
     * 添加surfaceview到largeSizePreviewLayout
     * @param surfaceView
     */
    private void addIntoLargeSizePreviewLayout(SurfaceView surfaceView) {
        largeSizePreviewLayout.removeAllViews();
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);

        }
        largeSizePreviewLayout.addView(surfaceView);
        //surfaceView.setZOrderOnTop(true);
        surfaceView.setZOrderMediaOverlay(true);
        largeSizePreviewLayout.setVisibility(VISIBLE);
    }

    /**
     * 添加surfaceview到smallSizePreviewLayout
     */
    private void addIntoSmallSizePreviewLayout() {
        smallSizePreviewLayout.removeAllViews();
        if (smallSizeSurfaceView.getParent() != null) {
            ((ViewGroup)smallSizeSurfaceView.getParent()).removeView(smallSizeSurfaceView);
        }
        smallSizePreviewLayout.addView(smallSizeSurfaceView);
        smallSizeSurfaceView.setZOrderMediaOverlay(true);
        smallSizePreviewLayout.setVisibility(View.VISIBLE);
    }

    private class GetUserInfoByAccid implements Runnable {
        private OnFinishGet callBack;
        private String pid;

        public GetUserInfoByAccid(String accid, OnFinishGet callBack) {
            this.pid = accid;
            this.callBack = callBack;
        }

        @Override
        public void run() {
            // 从网络获取，也可以在getRank那预先保存后这里就可以从本地读取
            ReqGetUserInfo req = new ReqGetUserInfo();
            req.pid = pid;
            YJSNet.getUserInfo(req, "xxxxx",
                    new YJSNet.OnRespondCallBack<RspGetUserInfo>() {

                        @Override
                        public void onSuccess(RspGetUserInfo data) {
                            if (callBack != null) {
                                callBack.onFinish(data);
                            }
                        }

                        @Override
                        public void onFailure(String errorMsg) {
                            if (callBack != null) {
                                callBack.onError(errorMsg);
                            }
                        }
                    });

        }
    }

    public interface OnFinishGet {
        void onFinish(RspGetUserInfo data);
        void onError(String errorMsg);
    }

}
