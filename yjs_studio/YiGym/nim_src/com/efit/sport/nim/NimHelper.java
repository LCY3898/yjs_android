package com.efit.sport.nim;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.cy.yigym.net.rsp.RspLogin.NetEaseAccount;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.efit.sport.videochat.EventRecvCall;
import com.hhtech.base.AppUtils;
import com.hhtech.utils.SystemUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatRingerConfig;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import de.greenrobot.event.EventBus;


public class NimHelper {

    private static final int RESULT_ALREAY_REGISTER = 602;

    private static NimHelper sInstance;

    private final static UserInfoProvider infoProvider;

    static {
        infoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                UserInfo user = new UserInfo() {
                    @Override
                    public String getAccount() {
                        return personToUid(DataStorageUtils.getPid());
                    }

                    @Override
                    public String getName() {
                        return DataStorageUtils.getUserNickName();
                    }

                    @Override
                    public String getAvatar() {
                        return DataStorageUtils.getCurUserProfileFid();
                    }
                };
                return user;
            }


            @Override
            public int getDefaultIconResId() {
                return R.drawable.ic_launcher;
            }

            @Override
            public Bitmap getTeamIcon(String teamId) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String
                    sessionId, SessionTypeEnum sessionType) {
                return DataStorageUtils.getUserNickName();
            }
        };
    }

    public static NimHelper instance() {
        if(sInstance != null) {
            return sInstance;
        }
        synchronized (NimHelper.class) {
            if(sInstance == null) {
                sInstance = new NimHelper();
            }
        }
        return sInstance;
    }

    public static void init() {
        NIMClient.init(AppUtils.getAppContext(),getLoginInfo(),getOptions());
        boolean isInMainProcess = true;
        try {
            isInMainProcess = SystemUtil.inMainProcess();
        } catch(Exception e) {}

        if (isInMainProcess) {
            // 初始化消息提醒
            NIMClient.toggleNotification(false);
            // 注册网络通话来电
            enableAVChat();
        }
    }

    public static LoginInfo getLoginInfo() {
        NetEaseAccount netEaseAccount = DataStorageUtils.getNetEaseAccount();
        if(netEaseAccount == null) {
            return new LoginInfo("", "");
        }
        String account = personToUid(netEaseAccount.accid);
        String token = netEaseAccount.token;
        return new LoginInfo(account, token);
    }

    private static SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();

        // 配置保存图片，文件，log等数据的目录
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + AppUtils.getAppContext().getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "efitsport";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;


        // 用户信息提供者
        options.userInfoProvider = infoProvider;

        return options;
    }


    /**
     * 音视频通话配置与监听
     */
    private static void enableAVChat() {
        setupAVChat();
        registerAVChatIncomingCallObserver(true);
    }

    private static void setupAVChat() {
        AVChatRingerConfig config = new AVChatRingerConfig();
        config.res_connecting = R.raw.avchat_connecting;
        config.res_no_response = R.raw.avchat_no_response;
        config.res_peer_busy = R.raw.avchat_peer_busy;
        config.res_peer_reject = R.raw.avchat_peer_reject;
        config.res_ring = R.raw.avchat_ring;
        AVChatManager.getInstance().setRingerConfig(config); // 设置铃声配置
    }

    private static void registerAVChatIncomingCallObserver(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>() {
            @Override
            public void onEvent(AVChatData data) {
                //TODO:
                EventBus.getDefault().post(new EventRecvCall(data));
            }
        }, register);
    }






    private boolean hasLogin = false;

    public void login(final String account,final String pwd) {
        AbortableFuture<LoginInfo>  loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, pwd));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                Log.i("NimHelper","nim login success");
            }

            @Override
            public void onFailed(int code) {
                if (code == 302 || code == 404) {//账号和密码错误

                }
                Log.i("NimHelper","nim login fail, code:" + code );
            }

            @Override
            public void onException(Throwable exception) {
                Log.i("NimHelper","nim login exception, code:",exception );
            }
        });
    }

    public void logout() {
        NIMClient.getService(AuthService.class).logout();
    }


    public void register(final String userName,final String pwd,String nickName ) {
        ContactHttpClient.getInstance().register(userName, nickName, pwd, new ContactHttpClient.ContactHttpCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                login(userName, pwd);
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                if (code == RESULT_ALREAY_REGISTER) {
                    login(userName, pwd);
                }
            }
        });
    }

    public boolean isLogin() {
        return hasLogin;
    }


    public static String personToUid(String account) {
        if(account == null) {
            return "";
        }
        return account;
    }
}
