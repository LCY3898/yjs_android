package com.efit.sport.videochat;

import com.efit.sport.p2p.SDKCoreHelper;
import com.efit.sport.p2p.YtxHelper;

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
    public static void init() {
        SDKCoreHelper.getInstance().init();
    }

    public static void logout() {
        SDKCoreHelper.getInstance().logout();
    }

    public static void setIncomingSoundEnabled(boolean enalbe) {
        YtxHelper.setIncomingSoundEnabled(true);
    }


    public static void onNetConnected() {

    }


    public static void onNetDisconnect() {

    }
}
