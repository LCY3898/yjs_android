package com.efit.sport.p2p;

import android.view.SurfaceView;

import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;


public class YtxHelper {

    public static enum Cameratation  {
        Rotate_Auto(0),
        Rotate_0(1),
        Rotate_90(2),
        Rotate_180(3),
        Rotate_270(4);

        private int mValue;

        private Cameratation(int value) {
            this.mValue = value;
        }

        public int getId() {
            return 0;
        }

        public int getValue() {
            return this.mValue;
        }
    }
    /*public final static String ACCOUNT_SID = "8a48b5515018a0f401504172f61c4093";
    public final static String AUTH_TOKEN = "4553677eeedb4e53824dadecb55501ad";*/

    public final static String APP_ID = "aaf98f8950189e9b015056e9df4827bf";
    public final static String APP_TOKEN = "32eb9a1059887f00d835fa094369ad05";

    //from canghui
    //public final static String APP_ID = "8a48b5515018a0f401505b2728677c79";
    //public final static String APP_TOKEN = "8f0acedc4de6cb82f3202ca018b337db";



    //容联云demo的appId和appkey
    /*public final static String APP_ID = "20150314000000110000000000000010";
    public final static String APP_TOKEN = "17E24E5AFDB6D0C1EF32F3533494502B";*/



/*    public void loginToCloud(String pid) {
        ECInitParams params = ECInitParams.createParams();
        params.setUserid(pid);
        params.setAppKey(APP_ID);
        params.setToken(APP_TOKEN);
        // 设置登陆验证模式（是否验证密码）NORMAL_AUTH-自定义方式
        params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
        // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
        // 3 LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO）
        params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);

        // 设置登陆状态回调
        params.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
            public void onConnect() {
                // 兼容4.0，5.0可不必处理
            }

            @Override
            public void onDisconnect(ECError error) {
                // 兼容4.0，5.0可不必处理
            }

            @Override
            public void onConnectState(ECDevice.ECConnectState state, ECError error) {
                if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
                    if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                        //账号异地登陆
                    } else {
                        //连接状态失败
                    }
                    return;
                } else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                    // 登陆成功
                }
            }
        });

        // 判断注册参数是否正确
        if (params.validate()) {
            ECDevice.login(params);
        }
    }*/

    public static void setVoipCbk(VoipCallback.VoipCbk cbk) {
        ECDevice.getECVoIPCallManager().setOnVoIPCallListener(new VoipCallback(cbk));
    }




    /**
     * 如果视频呼叫，则在接受呼叫之前，需要先设置视频通话显示的view
     * @param remote
     * @param local
     */
    public static void setVideView(SurfaceView remote,SurfaceView local) {
        ECDevice.getECVoIPSetupManager().setVideoView(remote, local);
    }

    public static String makeVideoCall(String account) {
        //说明：callId，可能是参数错误引起。否则返回是一串数字，是当前通话的标识。
        String callId = ECDevice.getECVoIPCallManager().makeCall(ECVoIPCallManager.CallType.VIDEO, account);
        return callId;
    }


    /**
     * 接受呼叫
     */
    public static void acceptCall(String callId) {

        ECDevice.getECVoIPCallManager().acceptCall(callId);
    }

    /**
     * 拒绝呼叫
     */
    public static void rejectCall(String callId,int reason) {
        ECDevice.getECVoIPCallManager().rejectCall(callId, reason);
    }

    public static void releaseCall(String callId) {
        ECDevice.getECVoIPCallManager().releaseCall(callId);
    }

    /**
     * fps 摄像头码率，
     * rotate 摄像头旋转的度数，默认为0，参数范围有（0、90、180、270），
     * force 是否强制初始化摄像头,当cameraIndex和当前正在显示的摄像头一样仍然会重新初始化摄像头
    */
    public static void selectCamera(int cameraIndex,int capabilityIndex,
                             int fps,Cameratation rotate ,boolean force) {


        ECVoIPSetupManager.Rotate devRotate = ECVoIPSetupManager.Rotate.ROTATE_AUTO;
        if(rotate == Cameratation.Rotate_0) {
            devRotate = ECVoIPSetupManager.Rotate.ROTATE_0;
        } else if(rotate == Cameratation.Rotate_90) {
            devRotate = ECVoIPSetupManager.Rotate.ROTATE_90;
        } else if(rotate == Cameratation.Rotate_180){
            devRotate = ECVoIPSetupManager.Rotate.ROTATE_180;
        } else if(rotate == Cameratation.Rotate_270){
            devRotate = ECVoIPSetupManager.Rotate.ROTATE_270;
        }
        ECDevice.getECVoIPSetupManager().selectCamera(cameraIndex,
                capabilityIndex, fps, devRotate, force);

    }

    public static void setIncomingSoundEnabled(boolean enabled) {
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        // 设置来电响铃
        setupManager.setIncomingSoundEnabled(enabled);
    }

    public static boolean isIncomingSoundEnabled() {
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        // 设置来电响铃
        return setupManager.isIncomingSoundEnabled();
    }


    /**
     * 设置开启回音消除模式
     * @return
     */
    public static void setAudioEcEnabled(boolean enabled) {
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();

        // 比如设置开启回音消除模式
        if (enabled) {
            setupManager.setAudioConfigEnabled(ECVoIPSetupManager.AudioType.AUDIO_EC,
                    true, ECVoIPSetupManager.AudioMode.EC_Aecm);
            setupManager.setAudioConfigEnabled(ECVoIPSetupManager.AudioType.AUDIO_NS,
                    true, ECVoIPSetupManager.AudioMode.NS_VeryHighSuppression);
            setupManager.setAudioConfigEnabled(ECVoIPSetupManager.AudioType.AUDIO_AGC,
                    true, ECVoIPSetupManager.AudioMode.AGC_FixedDigital);

        } else {
            setupManager.setAudioConfigEnabled(ECVoIPSetupManager.AudioType.AUDIO_EC,
                    false, ECVoIPSetupManager.AudioMode.EC_Conference);
        }

    }

    //设置是否启用来去电铃声播放
    public void setRing() {
        // 获取一个VoIP设置接口对象
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        // 设置来电响铃
        setupManager.setIncomingSoundEnabled(true);
        // 查询是否来电响铃
        setupManager.isIncomingSoundEnabled();
        // 设置VoIP呼叫是否播放回铃音
        setupManager.setOutgoingSoundEnabled(true);
        // 查询是否启用呼叫播放回铃音
        setupManager.isOutgoingSoundEnabled();
        // 设置VoIP呼叫是否播放呼叫失败提示音
        setupManager.setDisconnectSoundEnabled(true);
        // 查询是否启用呼叫失败提示音
        setupManager.isDisconnectSoundEnabled();
    }

    public void configAudio() {
        // 获取一个VoIP设置接口对象
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        // 比如是否启用回音消除
        setupManager.getAudioConfig(ECVoIPSetupManager.AudioType.AUDIO_EC);
        // 查询回音消除模式
        setupManager.getAudioConfigMode(ECVoIPSetupManager.AudioType.AUDIO_EC);

        // 比如设置开启回音消除模式
        setupManager.setAudioConfigEnabled(ECVoIPSetupManager.AudioType.AUDIO_EC,
                true, ECVoIPSetupManager.AudioMode.EC_Conference);
    }

    //设置视频通话码流（需要在通话前使用）
    public void setVideoBitRates(int rates) {
        // 获取一个VoIP设置接口对象
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        // 比如：将视频通话码流设置成150
        setupManager.setVideoBitRates(150);
    }


    //设置SDK支持的编解码方式，默认全部支持
    public void setVideoEncoder() {
        // 获取一个VoIP设置接口对象
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        // 比如：设置当前通话使用 G729编码传输
        setupManager.setCodecEnabled(ECVoIPSetupManager.Codec.Codec_G729, true);
        // 查询制定编解码是否支持
        setupManager.getCodecEnabled(ECVoIPSetupManager.Codec.Codec_G729);
    }


    public void makeVoiceCall(String account) {
        String mCurrentCallId = ECDevice.getECVoIPCallManager().makeCall(
                ECVoIPCallManager.CallType.VOICE, account);
        //说明：mCurrentCallId如果返回空则代表呼叫失败，可能是参数错误引起。否则返回是一串数字，是当前通话的标识。
    }
}
