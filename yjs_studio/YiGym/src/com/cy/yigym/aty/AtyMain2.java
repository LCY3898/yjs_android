package com.cy.yigym.aty;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.share.ShareHelper;
import com.cy.wbs.event.EventUnlogin;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.ActivityManager;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.Task.AppTaskManager;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventIMMsgReceived;
import com.cy.yigym.event.EventNetStateChange;
import com.cy.yigym.fragment.FragmentClubContent;
import com.cy.yigym.fragment.FragmentMainContent;
import com.cy.yigym.net.NetChangeReceiver;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetServerInfo;
import com.cy.yigym.net.req.ReqGetUserInfo;
import com.cy.yigym.net.rsp.RspGetServerInfo;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.utils.AppModeHelper;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.EventHeadImageView;
import com.efit.sport.R;
import com.efit.sport.notify.ChaseNotifyEvent;
import com.efit.sport.videochat.VideoChatHelper;
import com.hhtech.base.AppUtils;
import com.hhtech.umeng.update.UmengUpdateUtils;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.zbar.lib.CaptureCodeActivity;
import com.zbar.lib.CaptureConstant;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-4
 * </p>
 * <p>
 * 主界面第二版
 * </p>
 */
public class AtyMain2 extends BaseFragmentActivity implements OnClickListener {
    @BindView
    private DrawerLayout vDrawer;
    @BindView
    private LinearLayout llHead, llMsg, llTa, llSetting;
    @BindView
    private EventHeadImageView ivHead;

    @BindView
    private TextView tvNickname;

    @BindView
    private TextView drawMsgIndicator;

    private NetChangeReceiver netChangeReceiver;
    private final static int REQ_CODE_QRCODE = 1000;

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_main2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppModeHelper.isInClubMode()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        //AtyDataStorageUtils.setShowMsgIndicator(true);
        //结束当前activity之前的所有activity
        ActivityManager.getInstance().finishAtyBeforeThis();
    }

    @Override
    protected void initData() {
        getUserInfo();
        VideoChatHelper.init();
        if (AppModeHelper.isInClubMode()) {
            addFragment(R.id.flContent, new FragmentClubContent());
           // vDrawer.setVisibility(View.GONE);
           hideDrawer();
        } else {
            addFragment(R.id.flContent, new FragmentMainContent());
        }
        llHead.setOnClickListener(this);
        llMsg.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        llTa.setOnClickListener(this);
        initBle();

        tvNickname.setText(DataStorageUtils.getUserNickName());

        netChangeReceiver = new NetChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netChangeReceiver, filter);

        EventBus.getDefault().register(mNetChangeListener);

        EventBus.getDefault().register(msgPrivate);

        EventBus.getDefault().register(msgNotify);
        EventBus.getDefault().register(unLoginListener);

        //vDrawer.setDrawerListener(onDrawerListener);


        syncServerInfo();

        showUnconfigIfNeed();

        UmengUpdateUtils.update(mContext); //检查新版本
    }


    private void syncServerInfo() {
        AppTaskManager.execute(syncServerInfoTask);
    }

    private Runnable syncServerInfoTask = new Runnable() {
        @Override
        public void run() {
            String syncDate = DataStorageUtils.getServerInfoSyncDate();
            final String currDate = getDate();
            if (TextUtils.equals(currDate, syncDate)) {
                return;
            }
            YJSNet.send(new ReqGetServerInfo(), LOG_TAG,
                    new YJSNet.OnRespondCallBack<RspGetServerInfo>() {

                        @Override
                        public void onSuccess(RspGetServerInfo data) {
                            DataStorageUtils.setServerInfoSyncDate(currDate);
                            DataStorageUtils.setDownloadUrl(data.server_info.download_path);
                            DataStorageUtils.setUploadUrl(data.server_info.upload_to);
                            DataStorageUtils.setMp4LinkPrefix(data.server_info.mp4_link_prefix);
                        }

                        @Override
                        public void onFailure(String errorMsg) {
                            DataStorageUtils.setServerInfoSyncDate("");
                        }
                    });
        }
    };

    private String getDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        return year + "-" + month + "-" + day;
    }


    @Override
    public void onResume() {
        super.onResume();
        processMsgIndicator();
        updateUserInfo();
    }

    private void processMsgIndicator() {
        // 有消息时是否显示小红点

        if (DataStorageUtils.isShowMsgIndicator()) {
            msgIndicatorShow();
        } else {
            msgIndicatorHide();
        }
    }

    private void msgIndicatorShow() {
        drawMsgIndicator.setVisibility(View.VISIBLE);
    }

    private void msgIndicatorHide() {
        drawMsgIndicator.setVisibility(View.GONE);
    }

    private BusEventListener.MainThreadListener<ChaseNotifyEvent> msgNotify =
            new BusEventListener.MainThreadListener<ChaseNotifyEvent>() {
                @Override
                public void onEventMainThread(ChaseNotifyEvent event) {
                    DataStorageUtils.setShowMsgIndicator(true);
                    msgIndicatorShow();
                }
            };

    private boolean isInLoginScreen = false;

    private BusEventListener.MainThreadListener unLoginListener =
            new BusEventListener.MainThreadListener<EventUnlogin>() {
                @Override
                public void onEventMainThread(EventUnlogin event) {
                    DataStorageUtils.setLogin(false);
                    DataStorageUtils.setSession("");
                    if (event.reason == EventUnlogin.UnloginReason.USER_NOT_EXIST) {
                        CurrentUser.instance().clearUser();
                        WidgetUtils.showToast("用户未登录");
                    } else if (event.reason == EventUnlogin.UnloginReason.KICK_OFF) {
                        WidgetUtils.showToast("用户在其他地方登录，请您确认账号密码是否泄露");
                    } else if (event.reason == EventUnlogin.UnloginReason.SESSION_TIMEOUT) {
                        WidgetUtils.showToast("您离开太久了，请重新登录");
                    }
                    if (isInLoginScreen ||
                            ActivityManager.getInstance().isActivityVisible(AtyLogin.class)) {
                        return;
                    }
                    try {
                        isInLoginScreen = true;
                        AppUtils.runOnUIDelayed(2000, new Runnable() {
                            @Override
                            public void run() {
                                isInLoginScreen = false;
                            }
                        });
                        BaseFragmentActivity aty = ActivityManager.getInstance().getTopAty();
                        ActivityManager.getInstance().exitApplication();
                        if(aty != null) {
                            AtyLogin.relogin(aty);
                        }

                    } catch (Exception e) {
                    }

                }
            };

    private BusEventListener.MainThreadListener<EventIMMsgReceived> msgPrivate = new BusEventListener.MainThreadListener<EventIMMsgReceived>() {
        @Override
        public void onEventMainThread(EventIMMsgReceived event) {
            DataStorageUtils.setShowMsgIndicator(true);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finiBle();
        unregisterReceiver(netChangeReceiver);
        EventBus.getDefault().unregister(mNetChangeListener);
        EventBus.getDefault().unregister(msgPrivate);
        EventBus.getDefault().unregister(msgNotify);
        EventBus.getDefault().unregister(unLoginListener);
    }

    @Override
    public void onClick(View v) {
        //hideDrawer();
        //delayHideDrawer();
        switch (v.getId()) {
            case R.id.llHead:
                startActivity(AtyMyself.class);
                hideDrawer();
                break;

            case R.id.llMsg:
                DataStorageUtils.setShowMsgIndicator(false);
                startActivity(AtyMessages.class);
                hideDrawer();
                break;

            case R.id.llTa:
                startActivity(AtyChaseMain.class);
                hideDrawer();
                break;

            case R.id.llSetting:
                startActivity(AtySetting.class);
                hideDrawer();
                break;
        }
    }

    private void delayHideDrawer() {
        AppUtils.runOnUIDelayed(1000, hideRunnable);
    }

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hideDrawer();
        }
    };

    public void showDrawer() {
        vDrawer.openDrawer(Gravity.LEFT);
    }

    public void hideDrawer() {
        vDrawer.closeDrawers();
    }

    private void initBle() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (DataStorageUtils.isBleFirstOp()) {
                    /*
                    // TODO: show pop
                    DlgTextMsg dlgTextMsg = new DlgTextMsg(mContext,
                            new DlgTextMsg.ConfirmDialogListener() {
                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onOk() {

                                }

                                @Override
                                public void onCenter() {
                                    BleConnect.instance().getBleMgr()
                                            .forceEnable();
                                    DataStorageUtils.setIsBleFirstOp(false);
                                }
                            });
                    dlgTextMsg.setIvBike();
                    dlgTextMsg.setCenterbtnString("打开蓝牙");
                    dlgTextMsg.show("", "使用产品，完成以下操作"); */
                    if (!BleConnect.instance().getBleMgr().isEnabled()) {
                        BleConnect.instance().getBleMgr().forceEnable();  // 放到atyBoot中去

                    }
                    DataStorageUtils.setIsBleFirstOp(false);
                }

                if (!DataStorageUtils.isBleFirstOp()) {
                    if (!BleConnect.instance().getBleMgr().isEnabled()) {
                        BleConnect.instance().getBleMgr().forceEnable();
                    }
                }
            }
        }, 1000);
    }

    private void finiBle() {
        BleConnect.instance().getBleMgr().disable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 根据requestCode获取对应的SsoHandler
        UMSocialService service = ShareHelper.getInstance().getShareService();
        if (service != null) {
            UMSsoHandler ssoHandler = service.getConfig().getSsoHandler(
                    requestCode);
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }


        if (requestCode == REQ_CODE_QRCODE) {
            if (resultCode == Activity.RESULT_OK) {
                String qrcodeStr = data
                        .getStringExtra(CaptureConstant.QRCODE_RESULT);
                boolean isFamilyUser = data.getBooleanExtra(CaptureConstant.QRCODE_IS_FAMILY_USER, true);
                //if(isFamilyUser) {
                DataStorageUtils.setBleAddress(qrcodeStr);
                //}

                Log.d(LOG_TAG, qrcodeStr + "蓝牙地址");
                WidgetUtils.showToast("扫描成功！");
                ensureConnect(true);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long exitTime;

    private void exit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            WidgetUtils.showToast("再次点击退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            //结束所有的activity
            ActivityManager.getInstance().exitApplication();
            VideoChatHelper.logout();
        }
    }


    private BusEventListener.MainThreadListener mNetChangeListener = new BusEventListener.MainThreadListener<EventNetStateChange>() {
        @Override
        public void onEventMainThread(EventNetStateChange event) {
            if (event.isConnected) {
                VideoChatHelper.onNetConnected();
            } else {
                VideoChatHelper.onNetDisconnect();
            }
        }
    };


    public DrawerLayout.DrawerListener onDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            View mContent = vDrawer.getChildAt(0);
            View mMenu = drawerView;
            float scale = 1 - slideOffset;
            float leftScale = 1 - 0.3f * scale;
            float rightScale = 0.8f + scale * 0.2f;
            ViewHelper.setScaleX(mMenu, leftScale);
            ViewHelper.setScaleY(mMenu, leftScale);
            ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
            ViewHelper.setTranslationX(mContent,
                    mMenu.getMeasuredWidth() * (1 - scale));
            ViewHelper.setPivotX(mContent, 0);
            ViewHelper.setPivotY(mContent,
                    mContent.getMeasuredHeight() / 2);
            mContent.invalidate();
            ViewHelper.setScaleX(mContent, rightScale);
            ViewHelper.setScaleY(mContent, rightScale);
        }

        @Override
        public void onDrawerOpened(View drawerView) {

        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };


    /**
     * 从网络上获取用户信息
     */
    private void getUserInfo() {
        YJSNet.getUserInfo(new ReqGetUserInfo(), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspGetUserInfo>() {
                    @Override
                    public void onSuccess(RspGetUserInfo data) {
                        //DataStorageUtils.setUserInfo(data);
                        //保存用户信息
                        CurrentUser.instance().setUserInfo(data.data.personInfo);
                        setHeadAvatar(data.data.personInfo.profile_fid);
                        //保存网易云信账号
                        DataStorageUtils.setNetEaseAccount(data.data.personInfo.neteaseIMAcc);
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        WidgetUtils.showToast(" 获取用户信息失败" + errorMsg);
                    }
                });
    }

    private void setHeadAvatar(String fid) {
        ImageLoaderUtils.getInstance().loadImage(DataStorageUtils.getHeadDownloadUrl(fid), ivHead);
    }

    private void updateUserInfo() {
        if (DataStorageUtils.isUserInfoChange()) {
            tvNickname.setText(DataStorageUtils.getUserNickName());
            DataStorageUtils.setUserInfoChange(false);
        }
    }


    private void showUnconfigIfNeed() {
        String bleAddr = DataStorageUtils.getBleAddress();
        if (TextUtils.isEmpty(bleAddr)) {
            showUnconfigPop();

        }
    }

    private void ensureConnect(boolean showUncofig) {
        String bleAddr = DataStorageUtils.getBleAddress();
        if (TextUtils.isEmpty(bleAddr)) {
            if (showUncofig) {
                showUnconfigPop();
            }
            return;
        }

        // 如果是同一个设备，并且已经连接上，则不需要再连接
        if (!BleConnect.instance().isBleConnected()
                || bleAddr.equals(BleConnect.instance().getConnectBleAddr())) {
            BleConnect.instance().connectBle(bleAddr);
        }
    }

    /**
     * 扫描二维码提示弹窗
     */
    private void showUnconfigPop() {
        if (AppModeHelper.isInClubMode()) {
            return; // 健身模式由工作人员配置
        }
        DlgTextMsg dlg = new DlgTextMsg(mActivity,
                new DlgTextMsg.ConfirmDialogListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOk() {

                    }

                    @Override
                    public void onCenter() {
                        Intent intent = new Intent(mActivity,
                                CaptureCodeActivity.class);
                        startActivityForResult(intent, REQ_CODE_QRCODE);
                    }
                });
        dlg.setCenterbtnString("去扫描");
        dlg.show("提示", "请扫描二维码连接数据");

    }

}
