package com.efit.sport.nim.ui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.cy.widgetlibrary.WidgetUtils;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.VideoChatParam;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 音视频管理器, 音视频相关功能管理
 * Created by hzxuwen on 2015/4/23.
 */
public class AVChatUI implements AVChatUIListener {
    // constant
    private static final String TAG = "AVChatUI";

    // data
    private Context context;
    private AVChatData avChatData;
    private final AVChatListener aVChatListener;
    private String receiverId;
    //private AVChatSurface avChatSurface;
    private VideoChatParam videoParam; // 视频采集参数
    private String videoAccount; // 发送视频请求，onUserJoin回调的user account

    private CallStateEnum callingState = CallStateEnum.INVALID;

    private long timeBase = 0;

    // state
    public boolean canSwitchCamera = false;
    private boolean isClosedCamera = false;
    public AtomicBoolean isCallEstablish = new AtomicBoolean(false);
    private SurfaceView mCapturePreview;

    public interface AVChatListener {
        void onCallStateChanged(AVChatImpl.ChatState state);
    }

    public AVChatUI(Activity activity,AVChatListener listener) {
        this.context = activity;
        this.aVChatListener = listener;
    }

    public void setCapturePreview(SurfaceView preview) {
        mCapturePreview = preview;
    }

    /**
     * ******************************初始化******************************
     */

    /**
     * 初始化，包含初始化音频管理器， 视频管理器和视频界面绘制管理器。
     *
     * @return boolean
     */
    public boolean initiation() {
        AVChatProfile.getInstance().setAVChatting(true);
        //avChatSurface = new AVChatSurface(context, this);

        return true;
    }

    /**
     * ******************************拨打和接听***************************
     */

    /**
     * 来电
     */
    public void inComingCalling(AVChatData avChatData) {
        this.avChatData = avChatData;
        receiverId = avChatData.getAccount();
        if (avChatData.getChatType() == AVChatType.AUDIO) {
            onCallStateChange(CallStateEnum.INCOMING_AUDIO_CALLING);
        } else {
            onCallStateChange(CallStateEnum.INCOMING_VIDEO_CALLING);
        }
    }

    /**
     * 拨打音视频
     */
    public void outGoingCalling(String account, AVChatType callTypeEnum) {
        this.receiverId = account;
        VideoChatParam videoParam = null;
        if (callTypeEnum == AVChatType.AUDIO) {
            onCallStateChange(CallStateEnum.OUTGOING_AUDIO_CALLING);
        } else {
            onCallStateChange(CallStateEnum.OUTGOING_VIDEO_CALLING);
            if (videoParam == null) {
                videoParam = new VideoChatParam(mCapturePreview,
                        ((Activity) context).getWindowManager().getDefaultDisplay().getRotation());
            }
        }
        /**
         * 发起通话
         * account 对方帐号
         * callTypeEnum 通话类型：语音、视频
         * videoParam 发起视频通话时传入，发起音频通话传null
         * AVChatCallback 回调函数，返回AVChatInfo
         */
        AVChatManager.getInstance().call(account, callTypeEnum, videoParam, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData data) {
                Log.d(TAG, "success");
                avChatData = data;
            }

            @Override
            public void onFailed(int code) {
                Log.d(TAG, "failed code->" + code);
                closeSessions(-1);
                aVChatListener.onCallStateChanged(AVChatImpl.ChatState.CALL_FAIL);
            }

            @Override
            public void onException(Throwable exception) {
                Log.d(TAG, "start call onException->" + exception);
            }
        });
    }

    /**
     * 状态改变
     *
     * @param stateEnum
     */
    public void onCallStateChange(CallStateEnum stateEnum) {
        callingState = stateEnum;
        //avChatSurface.onCallStateChange(stateEnum);
    }

    /**
     * 挂断
     *
     * @param type 音视频类型
     */
    private void hangUp(final int type) {
        if (type == AVChatExitCode.HANGUP || type == AVChatExitCode.PEER_NO_RESPONSE || type == AVChatExitCode.CANCEL) {
            AVChatManager.getInstance().hangUp(new AVChatCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "hangup onSuccess");
                    aVChatListener.onCallStateChanged(AVChatImpl.ChatState.HANGUP);
                }

                @Override
                public void onFailed(int code) {
                    Log.d(TAG, "hangup onFailed->" + code);
                }

                @Override
                public void onException(Throwable exception) {
                    Log.d(TAG, "hangup onException->" + exception);
                }
            });
        }
        closeSessions(type);
    }

    /**
     * 关闭本地音视频各项功能
     *
     * @param exitCode 音视频类型
     */
    public void closeSessions(int exitCode) {
        //not  user  hang up active  and warning tone is playing,so wait its end
        Log.i(TAG, "close session -> " + AVChatExitCode.getExitString(exitCode));
        showQuitToast(exitCode);
        isCallEstablish.set(false);
        canSwitchCamera = false;
        isClosedCamera = false;
        aVChatListener.onCallStateChanged(AVChatImpl.ChatState.DISCONNECTED);
    }

    /**
     * 给出结束的提醒
     *
     * @param code
     */
    public void showQuitToast(int code) {
        switch (code) {
            case AVChatExitCode.NET_CHANGE: // 网络切换
            case AVChatExitCode.NET_ERROR: // 网络异常
            case AVChatExitCode.CONFIG_ERROR: // 服务器返回数据错误
                WidgetUtils.showToast("网络异常");
                break;
            case AVChatExitCode.PEER_HANGUP:
            case AVChatExitCode.HANGUP:
                if (isCallEstablish.get()) {
                    //TODO: 通话已结束

                }
                break;
            case AVChatExitCode.PEER_BUSY:
                break;
            case AVChatExitCode.PROTOCOL_INCOMPATIBLE_PEER_LOWER:
                break;
            case AVChatExitCode.PROTOCOL_INCOMPATIBLE_SELF_LOWER:
                break;
            case AVChatExitCode.INVALIDE_CHANNELID:
                break;
            case AVChatExitCode.LOCAL_CALL_BUSY:
                break;
            default:
                break;
        }
    }

    /**
     * ******************************* 接听与拒绝操作 *********************************
     */

    /**
     * 拒绝来电
     */
    public void rejectInComingCall() {
        /**
         * 接收方拒绝通话
         * AVChatCallback 回调函数
         */
        AVChatManager.getInstance().hangUp(new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "reject sucess");
            }

            @Override
            public void onFailed(int code) {
                Log.d(TAG, "reject sucess->" + code);
            }

            @Override
            public void onException(Throwable exception) {
                Log.d(TAG, "reject sucess");
            }
        });
        closeSessions(AVChatExitCode.REJECT);
    }



    /**
     * 接听来电
     */
    private void receiveInComingCall() {
        //接听，告知服务器，以便通知其他端
        VideoChatParam videoParam = null;

        if (callingState == CallStateEnum.INCOMING_AUDIO_CALLING) {
            onCallStateChange(CallStateEnum.AUDIO_CONNECTING);
        } else {
            onCallStateChange(CallStateEnum.VIDEO_CONNECTING);
            videoParam = new VideoChatParam(mCapturePreview,
                    ((Activity) context).getWindowManager().getDefaultDisplay().getRotation());
        }

        /**
         * 接收方接听电话
         * videoParam 接听视频通话时传入，接听音频通话传null
         * AVChatCallback 回调函数。成功则连接建立，不成功则关闭activity。
         */
        AVChatManager.getInstance().accept(videoParam, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void v) {
                isCallEstablish.set(true);
                canSwitchCamera = true;
            }

            @Override
            public void onFailed(int code) {
                if (code == -1) {
                    Toast.makeText(context, "本地音视频启动失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "建立连接失败", Toast.LENGTH_SHORT).show();
                }
                closeSessions(AVChatExitCode.CANCEL);
            }

            @Override
            public void onException(Throwable exception) {
                Log.d(TAG, "accept exception->" + exception);
            }
        });
    }

    /*************************** AVChatUIListener ******************************/

    /**
     * 点击挂断或取消
     */
    @Override
    public void onHangUp() {
        if (isCallEstablish.get()) {
            hangUp(AVChatExitCode.HANGUP);
        } else {
            hangUp(AVChatExitCode.CANCEL);
        }
    }

    /**
     * 拒绝操作，根据当前状态来选择合适的操作
     */
    @Override
    public void onRefuse() {
        switch (callingState) {
            case INCOMING_AUDIO_CALLING:
            case AUDIO_CONNECTING:
                rejectInComingCall();
                break;
            case INCOMING_AUDIO_TO_VIDEO:
                break;
            case INCOMING_VIDEO_CALLING:
            case VIDEO_CONNECTING: // 连接中点击拒绝
                rejectInComingCall();
                break;
            default:
                break;
        }
    }

    /**
     * 开启操作，根据当前状态来选择合适的操作
     */
    @Override
    public void onReceive() {
        switch (callingState) {
            case INCOMING_AUDIO_CALLING:
                receiveInComingCall();
                onCallStateChange(CallStateEnum.AUDIO_CONNECTING);
                break;
            case AUDIO_CONNECTING: // 连接中，继续点击开启 无反应
                break;
            case INCOMING_VIDEO_CALLING:
                receiveInComingCall();
                onCallStateChange(CallStateEnum.VIDEO_CONNECTING);
                break;
            case VIDEO_CONNECTING: // 连接中，继续点击开启 无反应
                break;
            case INCOMING_AUDIO_TO_VIDEO:
                receiveAudioToVideo();
            default:
                break;
        }
    }

    @Override
    public void toggleMute() {
        if (!isCallEstablish.get()) { // 连接未建立，在这里记录静音状态
            return;
        } else { // 连接已经建立
            if (!AVChatManager.getInstance().isMute()) { // isMute是否处于静音状态
                // 关闭音频
                AVChatManager.getInstance().setMute(true);
            } else {
                // 打开音频
                AVChatManager.getInstance().setMute(false);
            }
        }
    }

    @Override
    public void toggleSpeaker() {
        AVChatManager.getInstance().setSpeaker(!AVChatManager.getInstance().speakerEnabled()); // 设置扬声器是否开启
    }

    @Override
    public void videoSwitchAudio() {
        /**
         * 请求视频切换到音频
         */
        AVChatManager.getInstance().requestSwitchToAudio(new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // 界面布局切换。
                onCallStateChange(CallStateEnum.AUDIO);
                onVideoToAudio();
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void audioSwitchVideo() {
        onCallStateChange(CallStateEnum.OUTGOING_AUDIO_TO_VIDEO);
        videoParam = new VideoChatParam(mCapturePreview,
                ((Activity) context).getWindowManager().getDefaultDisplay().getRotation());
        /**
         * 请求音频切换到视频
         */
        AVChatManager.getInstance().requestSwitchToVideo(videoParam, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "requestSwitchToVideo onSuccess");
            }

            @Override
            public void onFailed(int code) {
                Log.d(TAG, "requestSwitchToVideo onFailed" + code);
            }

            @Override
            public void onException(Throwable exception) {
                Log.d(TAG, "requestSwitchToVideo onException" + exception);
            }
        });
    }

    @Override
    public void switchCamera() {
        AVChatManager.getInstance().toggleCamera(); // 切换摄像头（主要用于前置和后置摄像头切换）
    }

    @Override
    public void closeCamera() {
        if (!isClosedCamera) {
            // 关闭摄像头
            AVChatManager.getInstance().toggleLocalVideo(false, null);
            isClosedCamera = true;
            //avChatSurface.localVideoOff();
        } else {
            // 打开摄像头
            AVChatManager.getInstance().toggleLocalVideo(true, null);
            isClosedCamera = false;
            //avChatSurface.localVideoOn();
        }

    }

    /**
     * 音频切换为视频的请求
     */
    public void incomingAudioToVideo() {
        onCallStateChange(CallStateEnum.INCOMING_AUDIO_TO_VIDEO);
        if (videoParam == null) {
            videoParam = new VideoChatParam(mCapturePreview,
                    ((Activity) context).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    /**
     * 同意音频切换为视频
     */
    private void receiveAudioToVideo() {
        AVChatManager.getInstance().ackSwitchToVideo(true, videoParam, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onAudioToVideo();
                initSurfaceView(videoAccount);
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        }); // 音频切换到视频请求的回应. true为同意，false为拒绝

    }

    /**
     * 初始化大小图像
     *
     * @param largeAccount 对方的帐号
     */
    public void initSurfaceView(String largeAccount) {
        //avChatSurface.initLargeSurfaceView(largeAccount);
        //avChatSurface.initSmallSurfaceView(DataStorageUtils.getPid());
    }

    /**
     * 音频切换为视频
     */
    public void onAudioToVideo() {
        onCallStateChange(CallStateEnum.VIDEO);
        if (!AVChatManager.getInstance().isVideoSend()) { // 是否在发送视频 即摄像头是否开启
            AVChatManager.getInstance().toggleLocalVideo(true, null);
            //avChatSurface.localVideoOn();
            isClosedCamera = false;
        }
    }

    /**
     * 视频切换为音频
     */
    public void onVideoToAudio() {
        // 判断是否静音，扬声器是否开启，对界面相应控件进行显隐处理。
    }

    public void peerVideoOff() {
        //avChatSurface.peerVideoOff();
    }

    public void peerVideoOn() {
        //avChatSurface.peerVideoOn();
    }

    /**
     * // 恢复视频聊天（用于视频聊天退到后台后，从后台恢复时调用）
     */
    public void resumeVideo() {
        AVChatManager.getInstance().resumeVideo(true);
    }

    public boolean canSwitchCamera() {
        return canSwitchCamera;
    }

    public CallStateEnum getCallingState() {
        return callingState;
    }

    public String getVideoAccount() {
        return videoAccount;
    }

    public void setVideoAccount(String videoAccount) {
        this.videoAccount = videoAccount;
    }

    public String getAccount() {
        if (receiverId != null)
            return receiverId;
        return null;
    }

    public long getTimeBase() {
        return timeBase;
    }

    public void setTimeBase(long timeBase) {
        this.timeBase = timeBase;
    }
}
