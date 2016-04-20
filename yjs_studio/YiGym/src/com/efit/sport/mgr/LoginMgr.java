package com.efit.sport.mgr;

import android.text.TextUtils;

import com.cy.wbs.WSHelper;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.req.ReqLogin;
import com.cy.yigym.net.rsp.RspLogin;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.notify.NotifyEventHandler;
import com.sport.efit.constant.LoginChannel;


/**
 * tangtt
 * <p>
 * 2015-12-12
 * </p>
 * <p>
 * 后台登录管理
 * </p>
 */
public class LoginMgr {

    private final static String LOG_TAG = "LoginMgr";

    public static void loginBackground() {
        WSHelper.getInstance().setNotifyListener(new NotifyEventHandler());
        String phone = CurrentUser.instance().getUserName();
        String psw = CurrentUser.instance().getPasswd();

        if (CurrentUser.instance().isVerified()) {
            login(phone, psw);
        }
    }


    private static void login(String phone, String psw) {
        YJSNet.login(new ReqLogin(psw, phone), LOG_TAG,
                new OnRespondCallBack<RspLogin>() {

                    @Override
                    public void onSuccess(RspLogin data) {
                        onLoginSuccess(data);
                    }

                    @Override
                    public void onFailure(String errorMsg) {

                    }
                });
    }


    private static void onLoginSuccess(RspLogin rspLogin) {
        //设置登录状态为已登录
        DataStorageUtils.setLogin(true);
        DataStorageUtils.setLoginChannel(LoginChannel.CHANNEL_NORMAL);

        RspLogin.UserInfo userInfo = rspLogin.data.user_info;

        DataStorageUtils.setPid(userInfo._id);
        DataStorageUtils.setCurUserProfileFid(userInfo.profile_fid);

        if (!TextUtils.isEmpty(userInfo.nick_name) && rspLogin.isAccomplish()) {
            DataStorageUtils.setUserNickName(userInfo.nick_name);
            DataStorageUtils.setUserInfoComplete(true);
        } else {
            DataStorageUtils.setUserInfoComplete(false);
        }
    }
}

