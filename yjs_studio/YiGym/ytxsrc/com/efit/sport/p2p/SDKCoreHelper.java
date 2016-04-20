package com.efit.sport.p2p;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cy.yigym.CurrentUser;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.net.rsp.RspLogin;
import com.cy.yigym.utils.DataStorageUtils;
import com.hhtech.base.AppUtils;
import com.yuntongxun.ecsdk.ECDeskManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.VoIPCallUserInfo;

import de.greenrobot.event.EventBus;


/**
 * 容联云通信SDK 辅助类
 */
public class SDKCoreHelper implements ECDevice.InitListener , ECDevice.OnECDeviceConnectListener,ECDevice.OnLogoutListener {

    public static final String TAG = "SDKCoreHelper";
    public static final String ACTION_LOGOUT = "com.yuntongxun.ECDemo_logout";
    public static final String ACTION_SDK_CONNECT = "com.yuntongxun.Intent_Action_SDK_CONNECT";
    public static final String ACTION_KICK_OFF = "com.yuntongxun.Intent_ACTION_KICK_OFF";
    private static SDKCoreHelper sInstance;
    private Context mContext;
    private ECDevice.ECConnectState mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
    private ECInitParams mInitParams;
    private ECInitParams.LoginMode mMode = ECInitParams.LoginMode.FORCE_LOGIN;
    /**初始化错误*/
    public static final int ERROR_CODE_INIT = -3;

    public static final int WHAT_SHOW_PROGRESS = 0x101A;
	public static final int WHAT_CLOSE_PROGRESS = 0x101B;
    private boolean mKickOff = false;

    public static SoftUpdate mSoftUpdate;

    private boolean isInited = false;


    private SDKCoreHelper() {
        initOptions();
    }

    private void initOptions() {

    }



    public static SDKCoreHelper getInstance() {
        if (sInstance == null) {
            synchronized (SDKCoreHelper.class) {
                if(sInstance == null) {
                    sInstance = new SDKCoreHelper();
                }
            }
        }
        return sInstance;
    }

    public static boolean isKickOff() {
        return getInstance().mKickOff;
    }


    public static String personToUid(String pid) {
        if(pid.charAt(0)>= '0' && pid.charAt(0) <= '9') {
            return pid;
        }
        return pid.substring(1);
    }

    public static void init() {
        init(DataStorageUtils.getPid(), ECInitParams.LoginMode.FORCE_LOGIN);
    }

    private static void init(String pid,ECInitParams.LoginMode mode) {
        if(TextUtils.isEmpty(pid)) {
            Log.i("SDKCoreHelper","isconnect empty userId");
            return;
        }
        getInstance().isInited = true;
        getInstance().mKickOff = false;
        getInstance().mMode = mode;
        getInstance().mContext = AppUtils.getAppContext();
        // 判断SDK是否已经初始化，没有初始化则先初始化SDK
        if(!ECDevice.isInitialized()) {
            getInstance().mConnect = ECDevice.ECConnectState.CONNECTING;
            // ECSDK.setNotifyOptions(getInstance().mOptions);
            ECDevice.initial(AppUtils.getAppContext(), getInstance());
            postConnectNotify();
            return ;
        }
        // 已经初始化成功，直接进行注册
        getInstance().onInitialized();
    }

    public static void setSoftUpdate(String version , int mode) {
        getInstance().mSoftUpdate = new SoftUpdate(version , mode);
    }

    @Override
    public void onInitialized() {
        //passwordAuth();
        normalAuth();
        CameraHelper.instance();//初始化摄像头参数
        if(mInitParams == null || !mInitParams.validate()) {
            Intent intent = new Intent(ACTION_SDK_CONNECT);
            intent.putExtra("error", -1);
            mContext.sendBroadcast(intent);
            return ;
        }

        // 设置接收VoIP来电事件通知Intent
        // 呼入界面activity、开发者需修改该类
        Intent intent = new Intent(getInstance().mContext, VideoCallReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getInstance().mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mInitParams.setPendingIntent(pendingIntent);

        // 设置SDK注册结果回调通知，当第一次初始化注册成功或者失败会通过该引用回调
        // 通知应用SDK注册状态
        // 当网络断开导致SDK断开连接或者重连成功也会通过该设置回调
        mInitParams.setOnDeviceConnectListener(this);
        ECDevice.login(mInitParams);
    }

    private void passwordAuth() {
        RspLogin.SubAccount subAccount = DataStorageUtils.getVoipObj();
        if(subAccount == null) {
            return;
        }
        if (mInitParams == null || mInitParams.getInitParams() == null || mInitParams.getInitParams().isEmpty()){
            mInitParams = ECInitParams.createParams();
        }
        mInitParams.reset();

        // 如：VoIP账号/手机号码/..
        mInitParams.setUserid(subAccount.voipAccount);
        mInitParams.setPwd(subAccount.voipPwd);
        // appkey
        mInitParams.setAppKey(YtxHelper.APP_ID);
        mInitParams.setToken(YtxHelper.APP_TOKEN);
        // 设置登陆验证模式（是否验证密码）NORMAL_AUTH-自定义方式
        mInitParams.setAuthType(ECInitParams.LoginAuthType.PASSWORD_AUTH);
        mInitParams.setMode(getInstance().mMode);
    }

    private void normalAuth() {
        RspLogin.SubAccount subAccount = DataStorageUtils.getVoipObj();
        if(subAccount == null) {
            return;
        }
        if (mInitParams == null || mInitParams.getInitParams() == null || mInitParams.getInitParams().isEmpty()){
            mInitParams = ECInitParams.createParams();
        }
        mInitParams.reset();

        // 如：VoIP账号/手机号码/..
        mInitParams.setUserid(personToUid(DataStorageUtils.getPid()));
        //mInitParams.setUserid(subAccount.voipAccount);
        // appkey
        mInitParams.setAppKey(YtxHelper.APP_ID);
        mInitParams.setToken(YtxHelper.APP_TOKEN);
        // 设置登陆验证模式（是否验证密码）NORMAL_AUTH-自定义方式
        mInitParams.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        mInitParams.setMode(getInstance().mMode);
    }

    @Override
    public void onConnect() {
        // Deprecated
    }

    @Override
    public void onDisconnect(ECError error) {
        // SDK与云通讯平台断开连接
        // Deprecated
    }

    @Override
    public void onConnectState(ECDevice.ECConnectState state, ECError error) {
        if(state == ECDevice.ECConnectState.CONNECT_FAILED && error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
            mKickOff = true;
            Log.i("","isconnect: kick off " + error.errorMsg);
            // 失败，账号异地登陆
            Intent intent = new Intent(ACTION_KICK_OFF);
            intent.putExtra("kickoffText" , error.errorMsg);
            mContext.sendBroadcast(intent);
            return;
            //TODO: show kickv off
        }
        getInstance().mConnect = state;
        Intent intent = new Intent(ACTION_SDK_CONNECT);
        intent.putExtra("error", error.errorCode);
        mContext.sendBroadcast(intent);
        postConnectNotify();

        if(state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
            ECDevice.getECVoIPSetupManager().enableLoudSpeaker(true);
            setUserInfo();
            setRing();
        }
    }


    //设置是否启用来去电铃声播放
    private void setRing() {
        // 获取一个VoIP设置接口对象
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        // 设置VoIP呼叫是否播放回铃音
        setupManager.setOutgoingSoundEnabled(true);
        // 设置VoIP呼叫是否播放呼叫失败提示音
        setupManager.setDisconnectSoundEnabled(false);
    }

   /* private int enableSpeaker() {
        try{
            AudioManager audioManager = (AudioManager) mContext.getSystemService  (Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if(!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);

                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }*/


    private void setUserInfo() {
        String nickname = CurrentUser.instance().getNickname();
        String fid = DataStorageUtils.getCurUserProfileFid();
        if(TextUtils.isEmpty(nickname)) {
            nickname = "无名";
        }
        if(TextUtils.isEmpty(fid)) {
            fid = "0";
        }
        VoIPCallUserInfo info = new VoIPCallUserInfo(fid,nickname);
        ECDevice.getECVoIPSetupManager().setVoIPCallUserInfo(info);

        regLiveCastReceiver();

        YtxHelper.setIncomingSoundEnabled(false);
    }

    /**
     * 当前SDK注册状态
     * @return
     */
    public static ECDevice.ECConnectState getConnectState() {
        return getInstance().mConnect;
    }

    @Override
    public void onLogout() {
        getInstance().mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
        if(mInitParams != null && mInitParams.getInitParams() != null) {
            mInitParams.getInitParams().clear();
        }
        mInitParams = null;
        mKickOff = false;
        isInited = false;
    }

    @Override
    public void onError(Exception exception) {
        Intent intent = new Intent(ACTION_SDK_CONNECT);
        intent.putExtra("error", ERROR_CODE_INIT);
        mContext.sendBroadcast(intent);
        ECDevice.unInitial();
    }

    /**
     * 状态通知
     */
    private static void postConnectNotify() {
        //TODO: post connect state
        ECDevice.ECConnectState state = getConnectState();
        Log.i("SDKCoreHelper", "isconnect:" + state.toString());
    }

    public static void logout() {
        ECDevice.logout(getInstance());
        getInstance().unRegLiveCastReceiver();
    }

    public static void release() {
        getInstance().mKickOff = false;
    }


    public static ECDeskManager getECDeskManager() {
        return ECDevice.getECDeskManager();
    }

    /**
     * VoIP呼叫接口
     * @return
     */
    public static ECVoIPCallManager getVoIPCallManager() {
        return ECDevice.getECVoIPCallManager();
    }

    public static ECVoIPSetupManager getVoIPSetManager() {
        return ECDevice.getECVoIPSetupManager();
    }


    public static class SoftUpdate  {
        public String version;
        public int mode;

        public SoftUpdate(String version , int mode) {
            this.version = version;
            this.mode = mode;
        }
    }

    /**
     *
     * @return返回底层so库 是否支持voip及会议功能
     * true 表示支持 false表示不支持
     * 请在sdk初始化完成之后调用
     */
    public boolean isSupportMedia(){
    	return ECDevice.isSupportMedia();
    }

    public static boolean hasFullSize(String inStr) {
		if (inStr.getBytes().length != inStr.length()) {
			return true;
		}
		return false;
	}


    /**
     * 如果不在直播界面，对方呼入
     */
    private void regLiveCastReceiver() {
        //EventBus.getDefault().register(incomingCallListener);
    }

    private void unRegLiveCastReceiver() {
        //EventBus.getDefault().unregister(incomingCallListener);
    }
}


