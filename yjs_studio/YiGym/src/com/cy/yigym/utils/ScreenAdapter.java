package com.cy.yigym.utils;

import com.hhtech.utils.DimenUtils;

/**
 * Created by tangtt on 15/8/19.
 */
public class ScreenAdapter {

    public final static int STATUSBAR_HEIGHT  = DimenUtils.dpToPx(55);

    public static boolean hasVirtualKey() {
        int contentViewH = DataStorageUtils.getContentViewHeight();
        int displayH = DimenUtils.getDisplayHeight();
        if(displayH - contentViewH  > STATUSBAR_HEIGHT) {
            return true;
        }
        return false;
    }

}
