package com.efit.sport.videochat;

import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.nim.NimHelper;
import com.netease.nimlib.sdk.auth.LoginInfo;

/**
 * author: tangtt
 * <p>
 * 2015/11/28
 * </p>
 * <p>
 * <p/>
 * </p>
 */
public class VideoChatHelper {

    /**
     * 云信必须在application中的onCreate中初始化
     */
    public static void onAppCreate() {
        NimHelper.init();
    }
    public static void init() {
        LoginInfo loginInfo = NimHelper.getLoginInfo();
        String nickname = DataStorageUtils.getUserNickName();
        NimHelper.instance().login(loginInfo.getAccount(), loginInfo.getToken());
    }

    public static void logout() {
        NimHelper.instance().logout();
    }

    public static void setIncomingSoundEnabled(boolean enabled) {

    }

    public static void onNetConnected() {

    }


    public static void onNetDisconnect() {

    }

}
