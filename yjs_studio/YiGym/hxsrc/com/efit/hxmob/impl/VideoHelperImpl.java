/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.efit.hxmob.impl;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.cy.widgetlibrary.WidgetUtils;
import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMVideoCallHelper;
import com.easemob.exceptions.EMServiceNotReadyException;
import com.efit.hxmob.applib.controller.HXSDKHelper;
import com.efit.hxmob.utils.CameraHelper;
import com.hhtech.base.AppUtils;

public class VideoHelperImpl extends VideoCallHepler {

    private SurfaceView localSurface;
    private SurfaceHolder localSurfaceHolder;
    private SurfaceView oppositeSurface;
    private SurfaceHolder oppositeSurfaceHolder;

    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private int streamID;
    private boolean endCallTriggerByMe = false;

    private EMVideoCallHelper callHelper;

    private CameraHelper cameraHelper;

    private boolean isCallActive = false;


    public static void addWindowFlags(Window window) {
        window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public void init(SurfaceView local,SurfaceView remote) {
        localSurface = local;
        oppositeSurface = remote;
        if(localSurface != null) {
            //localSurface.setZOrderMediaOverlay(true);
            //localSurface.setZOrderOnTop(true);
            localSurfaceHolder = localSurface.getHolder();
            localSurfaceHolder.addCallback(new LocalCallback());
        }

        // 获取callHelper,cameraHelper
        callHelper = EMVideoCallHelper.getInstance();
        cameraHelper = new CameraHelper(callHelper, localSurfaceHolder);

        // 显示对方图像的surfaceview
        oppositeSurfaceHolder = oppositeSurface.getHolder();
        // 设置显示对方图像的surfaceview
        callHelper.setSurfaceView(oppositeSurface);

        oppositeSurface.setZOrderMediaOverlay(true);
        oppositeSurface.setZOrderOnTop(true);
        oppositeSurfaceHolder.addCallback(new OppositeCallback());
        callHelper.setVideoOrientation(EMVideoCallHelper.EMVideoOrientation.EMLandscape);
    }

    public void makeCall(String userName) {
        HXSDKHelper.getInstance().isVideoCalling = true;
        this.username = userName;
        isInComingCall = false;
        soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
        outgoing = soundPool.load(getOutgoingRing(), 1);

        AppUtils.runOnUIDelayed(new Runnable() {
            public void run() {
                streamID = playMakeCallSounds();
            }
        }, 300);
    }

    private AssetFileDescriptor getOutgoingRing() {
        try {
            return AppUtils.getAssets().openFd("outgoing.ogg");
        } catch (Exception e) {
            return null;
        }
    }


    public void recvCall() {
        HXSDKHelper.getInstance().isVideoCalling = true;
        isInComingCall = true;
        localSurface.setVisibility(View.INVISIBLE);
        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(true);
        ringtone = RingtoneManager.getRingtone(AppUtils.getAppContext(), ringUri);
        ringtone.play();
    }


    public boolean isAnswered() {
        return isAnswered;
    }

    /**
     * 本地SurfaceHolder callback
     *
     */
    class LocalCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            cameraHelper.startCapture();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    /**
     * 对方SurfaceHolder callback
     */
    class OppositeCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            callHelper.onWindowResize(width, height, format);
            if (!cameraHelper.isStarted()) {
                if (!isInComingCall) {
                    try {
                        // 拨打视频通话
                        EMChatManager.getInstance().makeVideoCall(username);
                        // 通知cameraHelper可以写入数据
                        cameraHelper.setStartFlag(true);
                    } catch (EMServiceNotReadyException e) {
                        Toast.makeText(AppUtils.getAppContext(), "尚未连接服务端" ,Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

    }

    public void onCallAccepted() {
        AppUtils.runOnUI(new Runnable() {
            @Override
            public void run() {
                try {
                    if (soundPool != null)
                        soundPool.stop(streamID);
                } catch (Exception e) {
                }
                openSpeakerOn();
                callingState = CallingState.NORMAL;
            }
        });

    }


    public void onCallDisconnected( final EMCallStateChangeListener.CallError fError) {
        if (fError == EMCallStateChangeListener.CallError.REJECTED) {
            callingState = CallingState.BEREFUESD;
            WidgetUtils.showToast("对方拒绝了您的请求");
        } else if (fError == EMCallStateChangeListener.CallError.ERROR_TRANSPORT) {
            WidgetUtils.showToast("请求失败，网络传输异常");
        } else if (fError == EMCallStateChangeListener.CallError.ERROR_INAVAILABLE) {
            callingState = CallingState.OFFLINE;
            WidgetUtils.showToast("对方不在线");
        } else if (fError == EMCallStateChangeListener.CallError.ERROR_BUSY) {
            callingState = CallingState.BUSY;
            WidgetUtils.showToast("对方正忙");
        } else if (fError == EMCallStateChangeListener.CallError.ERROR_NORESPONSE) {
            callingState = CallingState.NORESPONSE;
            WidgetUtils.showToast("无人应答");
        } else {
            if (isAnswered) {
                callingState = CallingState.NORMAL;
                if (endCallTriggerByMe) {
                } else {
                }
            } else {
                if (isInComingCall) {
                    callingState = CallingState.UNANSWERED;
                    WidgetUtils.showToast("对方挂断了视频");
                } else {
                    if (callingState != CallingState.NORMAL) {
                        callingState = CallingState.CANCED;
                    } else {
                    }
                    WidgetUtils.showToast("对方未接听");
                }
            }
        }
        postDelayedCloseMsg();
    }

    private void postDelayedCloseMsg() {
         /*AppUtils.runOnUIDelayed(new Runnable() {
            @Override
            public void run() {
               Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(800);
                rootContainer.startAnimation(animation);
            }
        }, 200);*/
    }

    /**
     * 设置通话状态监听
     */
    public void addCallStateListener(EMCallStateChangeListener callStateListener) {
        EMChatManager.getInstance().addVoiceCallStateChangeListener(callStateListener);
    }


    /**
     * 设置通话状态监听
     */
    public void removeCallStateListener(EMCallStateChangeListener callStateListener) {
        EMChatManager.getInstance().removeCallStateChangeListener(callStateListener);
    }
    public void refuseCall() {
        if (ringtone != null)
            ringtone.stop();
        try {
            EMChatManager.getInstance().rejectCall();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        callingState = CallingState.REFUESD;
    }

    public void answerCall() {
        if (ringtone != null)
            ringtone.stop();
        if (isInComingCall) {
            try {
                EMChatManager.getInstance().answerCall();
                cameraHelper.setStartFlag(true);
                openSpeakerOn();
                isAnswered = true;
                isHandsfreeState = true;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        localSurface.setVisibility(View.VISIBLE);
        isCallActive = true;
    }

    // 挂断电话
    public void hangupCall() {
        if (soundPool != null)
            soundPool.stop(streamID);
        endCallTriggerByMe = true;
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void toggleMute(boolean mute) {
        isMuteState = mute;
        audioManager.setMicrophoneMute(mute);
    }


    public void toggleHandfree(boolean handFree) {
        isHandsfreeState = handFree;
        if (handFree) {
            openSpeakerOn();
        } else {
            // 关闭免提
            closeSpeakerOn();
        }
    }


    public void fini() {
        super.fini();
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e){}
        HXSDKHelper.getInstance().isVideoCalling = false;
        try {
            callHelper.setSurfaceView(null);
            cameraHelper.stopCapture();
            oppositeSurface = null;
            cameraHelper = null;
        } catch (Exception e) {
        }
    }
}
