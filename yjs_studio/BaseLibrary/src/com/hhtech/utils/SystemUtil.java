package com.hhtech.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hhtech.base.AppUtils;

/**
 * 系统工具箱
 */
public class SystemUtil {


    public static boolean inMainProcess() {
        String packageName = AppUtils.getAppContext().getPackageName();
        String processName = SystemUtil.getProcessName();
        Log.i("SystemUtil","SystemUtil packageName:" + packageName + " processname:"+ processName);
        return packageName.equals(processName);
    }
    /**
     * 获取当前进程名
     * @return 进程名
     */
    public static final String getProcessName() {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) AppUtils.getAppContext().getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

}
