package com.efit.sport.nim.ui;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;

import com.cy.widgetlibrary.WidgetUtils;
import com.efit.sport.videochat.LiveVideoView;
import com.hhtech.base.AppUtils;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatTimeOutEvent;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOnlineAckEvent;

/**
 * 视频控制器
 * @author tangtt
 */
public class AVChatImpl implements AVChatStateObserver,AVChatUI.AVChatListener {
    // constant
    private static final String TAG = "AVChatImpl";

    // data
    private AVChatUI avChatUI; // 音视频总管理器
    private AVChatData avChatData; // config for connect video server
    private int state; // calltype 音频或视频
    private String receiverId; // 对方的account

    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    private boolean isCallEstablished = false; // 电话是否接通
    private static boolean needFinish = false; // 若来电或去电未接通时，点击home。另外一方挂断通话。从最近任务列表恢复，则finish
    private boolean hasOnpause = false; // 是否暂停音视频
    private LiveVideoView liveVideoView;
    private boolean registObserver = false;


    public void setCapturePreview(SurfaceView preview) {
        avChatUI.setCapturePreview(preview);
    }

    public void init(Activity aty,LiveVideoView liveVideoView) {
        this.liveVideoView = liveVideoView;
        avChatUI = new AVChatUI(aty,this);
        avChatUI.initiation();

        registerNetCallObserver(true);
        isCallEstablished = false;
        registObserver = true;
    }



    public void fini() {
        registObserver = false;
        AVChatProfile.getInstance().setAVChatting(false);
        registerNetCallObserver(false);
    }

    public void makeCall(final String account) {
        if(!registObserver) {
            registObserver = true;
            registerNetCallObserver(true);
        }
        mIsInComingCall = false;
        receiverId = account;
        state = AVChatType.VIDEO.getValue();
        isCallEstablished = false;
        avChatUI.outGoingCalling(account, AVChatType.VIDEO);
    }


    public void receiveCall(AVChatData avChatData) {
        if(!registObserver) {
            registObserver = true;
            registerNetCallObserver(true);
        }
        mIsInComingCall = true;
        this.avChatData = avChatData;
        state = avChatData.getChatType().getValue();
        isCallEstablished = false;
        avChatUI.inComingCalling(avChatData);
    }


    public void answerCall() {
        mIsInComingCall = true;
        avChatUI.onReceive();
    }

    public void refuseCall() {
        avChatUI.rejectInComingCall();
    }

    public void hangupCall() {
        avChatUI.onHangUp();
    }

    public void pause() {
        AVChatManager.getInstance().pauseVideo(); // 暂停视频聊天（用于在视频聊天过程中，APP退到后台时必须调用）
        hasOnpause = true;
    }

    public void resume() {
        if (hasOnpause) {
            avChatUI.resumeVideo();
            hasOnpause = false;
        }
    }


    public boolean isInComingCall() {
        return mIsInComingCall;
    }


    /**
     * just for test
     */
    public void mute() {
        //AVChatManager.getInstance().setMute(true);
    }


    /**
     * 注册监听
     *
     * @param register
     */
    private void registerNetCallObserver(boolean register) {
        AVChatManager.getInstance().observeAVChatState(this, register);
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
        AVChatManager.getInstance().observeControlNotification(callControlObserver, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
        AVChatManager.getInstance().observeOnlineAckNotification(onlineAckObserver, register);
        AVChatManager.getInstance().observeTimeoutNotification(timeoutObserver, register);
        AVChatManager.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
    }


    private void showToast(final String text) {
        AppUtils.runOnUI(new Runnable() {
            @Override
            public void run() {
                WidgetUtils.showToast(text);
            }
        });
    }
    /**
     * 注册/注销网络通话被叫方的响应（接听、拒绝、忙）
     */
    Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>() {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo) {
            if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
                showToast("对方正忙，请稍后再拨");
                liveVideoView.onDisconnected("");
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                avChatUI.closeSessions(AVChatExitCode.REJECT);
                showToast("对方拒绝了您的请求");
                liveVideoView.onDisconnected("");
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                if (ackInfo.isDeviceReady()) {
                    avChatUI.isCallEstablish.set(true);
                    avChatUI.canSwitchCamera = true;
                } else {
                    // 设备初始化失败
                    avChatUI.closeSessions(AVChatExitCode.OPEN_DEVICE_ERROR);
                    showToast("设备初始化失败");
                    liveVideoView.onDisconnected("");
                }
            }
        }
    };

    Observer<AVChatTimeOutEvent> timeoutObserver = new Observer<AVChatTimeOutEvent>() {
        @Override
        public void onEvent(AVChatTimeOutEvent event) {
            if (event == AVChatTimeOutEvent.NET_BROKEN_TIMEOUT) {
                avChatUI.closeSessions(AVChatExitCode.NET_ERROR);
            } else {
                avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
            }
            /*AppUtils.runOnUI(new Runnable() {
                @Override
                public void run() {
                    WidgetUtils.showToast("对方未响应");
                }
            });*/
            liveVideoView.onDisconnected("");
            // 来电超时，自己未接听
            if (event == AVChatTimeOutEvent.INCOMING_TIMEOUT) {
            }
        }
    };

    Observer<Integer> autoHangUpForLocalPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
        }
    };

    /**
     * 注册/注销网络通话控制消息（音视频模式切换通知）
     */
    Observer<AVChatControlEvent> callControlObserver = new Observer<AVChatControlEvent>() {
        @Override
        public void onEvent(AVChatControlEvent netCallControlNotification) {
            handleCallControl(netCallControlNotification);
        }
    };

    /**
     * 注册/注销网络通话对方挂断的通知
     */
    Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>() {
        @Override
        public void onEvent(AVChatCommonEvent avChatHangUpInfo) {
            liveVideoView.onDisconnected(avChatHangUpInfo.getAccount());
            avChatUI.closeSessions(AVChatExitCode.HANGUP);
        }
    };

    /**
     * 注册/注销同时在线的其他端对主叫方的响应
     */
    Observer<AVChatOnlineAckEvent> onlineAckObserver = new Observer<AVChatOnlineAckEvent>() {
        @Override
        public void onEvent(AVChatOnlineAckEvent ackInfo) {
            if (ackInfo.getClientType() != ClientType.Android) {
                String client = null;
                switch (ackInfo.getClientType()) {
                    case ClientType.Web:
                        client = "Web";
                        break;
                    case ClientType.Windows:
                        client = "Windows";
                        break;
                    default:
                        break;
                }
                if (client != null) {
                    String option = ackInfo.getEvent() == AVChatEventType.CALLEE_ONLINE_CLIENT_ACK_AGREE ? "接听！" : "拒绝！";
                    //TODO show toast 通话被其他端接听
                }
                avChatUI.closeSessions(-1);
            }
        }
    };



    /**
     * ************************ AVChatStateObserver ****************************
     */

    @Override
    public void onConnectedServer(int res) {
        handleWithConnectServerResult(res);
    }

    @Override
    public void onUserJoin(String account) {
        Log.d(TAG, "onUserJoin");
        avChatUI.setVideoAccount(account);
        liveVideoView.onUserJoin(account);
        mute();
    }

    @Override
    public void onUserLeave(String account, int event) {
        liveVideoView.onDisconnected(account);
    }

    @Override
    public void onProtocolIncompatible(int status) {

    }

    @Override
    public void onDisconnectServer() {

    }

    @Override
    public void onNetworkStatusChange(int value) {

    }

    @Override
    public void onCallEstablished() {
        mute();
        Log.d(TAG, "onCallEstablished");
        liveVideoView.onConnected();
        if (avChatUI.getTimeBase() == 0)
            avChatUI.setTimeBase(SystemClock.elapsedRealtime());

        if (state == AVChatType.AUDIO.getValue()) {
            avChatUI.onCallStateChange(CallStateEnum.AUDIO);
        } else {
            avChatUI.initSurfaceView(avChatUI.getVideoAccount());
            avChatUI.onCallStateChange(CallStateEnum.VIDEO);
        }
        isCallEstablished = true;
    }

    public boolean isCallEstablished() {
        return isCallEstablished;
    }

    public void setCallEstablished(boolean established) {
        this.isCallEstablished = established;
    }

    @Override
    public void onOpenDeviceError(int code) {

    }

    /****************************** 连接建立处理 ********************/

    /**
     * 处理连接服务器的返回值
     *
     * @param auth_result
     */
    protected void handleWithConnectServerResult(int auth_result) {
        if (auth_result == 200) {
            Log.d(TAG, "onConnectServer success");
        } else if (auth_result == 101) { // 连接超时
            avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
            liveVideoView.onDisconnected("");
        } else if (auth_result == 401) { // 验证失败
            avChatUI.closeSessions(AVChatExitCode.CONFIG_ERROR);
            liveVideoView.onDisconnected("");
        } else if (auth_result == 417) { // 无效的channelId
            avChatUI.closeSessions(AVChatExitCode.INVALIDE_CHANNELID);
            liveVideoView.onDisconnected("");
        } else { // 连接服务器错误，直接退出
            avChatUI.closeSessions(AVChatExitCode.CONFIG_ERROR);
            liveVideoView.onDisconnected("");
        }
    }

    /**************************** 处理音视频切换 *********************************/

    /**
     * 处理音视频切换请求
     *
     * @param notification
     */
    private void handleCallControl(AVChatControlEvent notification) {
        switch (notification.getControlCommand()) {
            case SWITCH_AUDIO_TO_VIDEO:
                avChatUI.incomingAudioToVideo();
                break;
            case SWITCH_AUDIO_TO_VIDEO_AGREE:
                onAudioToVideo();
                break;
            case SWITCH_AUDIO_TO_VIDEO_REJECT:
                avChatUI.onCallStateChange(CallStateEnum.AUDIO);
                //TODO: audio to video rejected
                break;
            case SWITCH_VIDEO_TO_AUDIO:
                onVideoToAudio();
                break;
            case NOTIFY_VIDEO_OFF:
                avChatUI.peerVideoOff();
                break;
            case NOTIFY_VIDEO_ON:
                avChatUI.peerVideoOn();
                break;
            default:
                break;
        }
    }

    /**
     * 音频切换为视频
     */
    private void onAudioToVideo() {
        avChatUI.onAudioToVideo();
        avChatUI.initSurfaceView(avChatUI.getVideoAccount());
    }

    /**
     * 视频切换为音频
     */
    private void onVideoToAudio() {
        avChatUI.onCallStateChange(CallStateEnum.AUDIO);
        avChatUI.onVideoToAudio();
    }


    @Override
    public void onCallStateChanged(ChatState chatState) {
    }


    public enum ChatState {
        IDLE("idle"),
        RINGING("ringing"),
        ANSWERING("answering"),
        PAUSING("pausing"),
        CONNECTING("connecting"),
        CONNECTED("conntected"),
        ACCEPTED("accepted"),
        REJECTED("rejected"),
        HANGUP("hangup"),
        CALL_FAIL("callfail"),
        DISCONNECTED("disconnected");


        private final String state;

        private ChatState(String state) {
            this.state = state;
        }

        public String toString() {
            return this.state;
        }
    }
}

