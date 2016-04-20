package com.cy.yigym.utils;



/**
 * Created by lijianqiang on 15/12/14.
 *
 * 健身房模式，家庭模式
 */
public class AppModeHelper {
    public static final String CLUB_MODE = "1"; // 与服务端一致
    public static final String HOME_MODE = "2";

    public static boolean isInClubMode() {
        return DataStorageUtils.isInClubMode();
    }

    public static void setInClubMode(boolean isInClub) {
        DataStorageUtils.setInClubMode(isInClub);
    }

    public static String getCurrentMode() {
        return isInClubMode() ? CLUB_MODE : HOME_MODE;
    }

}
