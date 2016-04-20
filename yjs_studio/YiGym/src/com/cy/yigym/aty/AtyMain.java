package com.cy.yigym.aty;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.cy.share.ShareHelper;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.base.FragmentViewPagerAdapter;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.widgetlibrary.view.CustomViewPager;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventNaviToFrag;
import com.cy.yigym.fragment.FragmentChaseHer;
import com.cy.yigym.fragment.FragmentLive;
import com.cy.yigym.fragment.FragmentSports;
import com.cy.yigym.fragment.FragmentUserInfo;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.efit.sport.videochat.VideoChatHelper;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-16
 * </p>
 * <p>
 * 主界面
 * </p>
 */
public class AtyMain extends BaseFragmentActivity implements
        View.OnClickListener {

    @BindView
    private View sports;
    @BindView
    private View chase;
    @BindView
    private View notify;
    @BindView
    private View aboutme;
    // 当前按钮id
    private int currentId = R.id.sports;
    // viewpage
    @BindView
    private CustomViewPager view_page;
    private FragmentUserInfo fmUserInfo = new FragmentUserInfo();
    private FragmentChaseHer fmChaseHer = new FragmentChaseHer();
    private FragmentLive fmLive = new FragmentLive();
    private FragmentSports fmSports = new FragmentSports();
    private long exitTime;
    // 页面列表
    private ArrayList<Fragment> fragmentList;
    private FragmentViewPagerAdapter fragmentViewPagerAdapter;

    @Override
    protected boolean isBindViewByAnnotation() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_main;
    }

    @Override
    public int getContentAreaId() {
        return R.id.main_view;
    }

    @SuppressWarnings("serial")
    @Override
    protected void initView() {
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(fmSports);
        fragmentList.add(fmLive);
        fragmentList.add(fmChaseHer);
        fragmentList.add(fmUserInfo);
        sports.setOnClickListener(this);
        chase.setOnClickListener(this);
        notify.setOnClickListener(this);
        aboutme.setOnClickListener(this);
        fragmentViewPagerAdapter = new FragmentViewPagerAdapter(
                super.getSupportFragmentManager(), fragmentList);
        view_page.setAdapter(fragmentViewPagerAdapter);
        view_page.setCurrentItem(0);
        view_page.setOffscreenPageLimit(4);
        view_page.setSlideEnable(false);
    }


    private void initBle() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (DataStorageUtils.isBleFirstOp()) {
                    //TODO: show pop
                    DlgTextMsg dlgTextMsg = new DlgTextMsg(mContext, new DlgTextMsg.ConfirmDialogListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onOk() {

                        }

                        @Override
                        public void onCenter() {
                            BleConnect.instance().getBleMgr().forceEnable();
                            DataStorageUtils.setIsBleFirstOp(false);
                        }
                    });
                    dlgTextMsg.setIvBike();
                    dlgTextMsg.setCenterbtnString("打开蓝牙");
                    dlgTextMsg.show("", "使用产品，完成以下操作");
                }

                if (!DataStorageUtils.isBleFirstOp()) {
                    BleConnect.instance().getBleMgr().forceEnable();
                }
            }
        }, 1000);
    }

    private void finiBle() {
        BleConnect.instance().getBleMgr().disable();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(naviListener);
        initBle();
        currentId = R.id.sports;
        sports.setSelected(true);
        // // 获取图片上传地址
        /*YJSNet.getUpload(LOG_TAG, new OnRespondCallBack<RspGetUpload>() {

            @Override
            public void onSuccess(RspGetUpload data) {
                String url = data.data.result;
                DataStorageUtils.setUploadUrl(url);
                DataStorageUtils.setDownloadUrl(url.replace("upload",
                        "download"));

            }

            @Override
            public void onFailure(String errorMsg) {
                // TODO Auto-generated method stub

            }
        });*/
    }

    @Override
    public void onClick(View view) {
        hadleNewFragment(view.getId());
    }

    protected void hadleNewFragment(int newId) {
        if (newId == currentId)
            return;
        resetBtn();
        currentId = newId;
        switch (newId) {
            case R.id.sports:
                sports.setSelected(true);
                view_page.setCurrentItem(0);
                break;
            case R.id.notify:
                notify.setSelected(true);
                view_page.setCurrentItem(1);
                break;
            case R.id.chase:
                chase.setSelected(true);
                view_page.setCurrentItem(2);
                break;
            case R.id.aboutme:
                aboutme.setSelected(true);
                view_page.setCurrentItem(3);
                break;

        }

    }

    private void resetBtn() {
        sports.setSelected(false);
        chase.setSelected(false);
        notify.setSelected(false);
        aboutme.setSelected(false);
    }

    private BusEventListener.MainThreadListener naviListener = new BusEventListener.MainThreadListener<EventNaviToFrag>() {
        @Override
        public void onEventMainThread(EventNaviToFrag event) {
            String fragName = event.fragCls;
            if(fragName == null) {
                return;
            }
            if(fragName.equals(fmSports.getClass().getName())) {
                sports.performClick();
            } else if(fragName.equals(fmLive.getClass().getName())) {
                notify.performClick();
            } else if(fragName.equals(fmChaseHer.getClass().getName())) {
                chase.performClick();
            } else if(fragName.equals(fmUserInfo.getClass().getName())) {
                aboutme.performClick();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(naviListener);
        finiBle();
        YJSNet.removeRspCallBacks(LOG_TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 根据requestCode获取对应的SsoHandler
        UMSocialService service = ShareHelper.getInstance().getShareService();
        if (service != null) {
            UMSsoHandler ssoHandler = service.getConfig().getSsoHandler(requestCode);
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void exit(){
        if(System.currentTimeMillis()-exitTime>2000){
            Toast.makeText(this, "再次点击退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }else{
            //System.exit(0);
            finish();
            //SDKCoreHelper.logout();
            VideoChatHelper.logout();
        }
    }
}
