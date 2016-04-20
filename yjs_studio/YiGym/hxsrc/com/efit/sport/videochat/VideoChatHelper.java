package com.efit.sport.videochat;

import com.cy.yigym.utils.DataStorageUtils;
import com.efit.hxmob.impl.EaseContext;

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

    public static void onAppCreate() {

    }

    //private final static long lastLoginTime = System.currentTimeMillis();
    public static void init() {
        String userName = DataStorageUtils.getPid();
        String pwd = "efit123456";
        String nickname = DataStorageUtils.getUserNickName();
        EaseContext.getInstance().init();
        EaseContext.getInstance().loginOrReg(userName, pwd, nickname);
    }

    public static void logout() {
        EaseContext.getInstance().logout();
    }

    public static void setIncomingSoundEnabled(boolean enabled) {

    }

    public static void onNetConnected() {

    }


    public static void onNetDisconnect() {

    }

}
