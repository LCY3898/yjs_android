package com.efit.sport.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.cy.wbs.NotifyListener;
import com.cy.widgetlibrary.base.ActivityManager;
import com.cy.yigym.aty.AtyMessages;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.efit.sport.persist.bean.SystemNotice;
import com.google.gson.Gson;
import com.hhtech.base.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by tangtt on 2015/8/26 0026.
 */
public class NotifyEventHandler implements NotifyListener {
    private long []vibrate =  new long[] {0,300,500,700};

    private final static String TAG = NotifyEventHandler.class.getSimpleName();

    private static Map<String,Class<? extends BaseNotifyEvent>> noticeMap
            = new HashMap<String,Class<? extends BaseNotifyEvent>>();
    static {
        noticeMap.put("chase",ChaseNotifyEvent.class);
    }

    @Override
    public void onNotifyEvent(String noticeType,
                              String noticeData, String obj, String act) {
        if(noticeType != null) {
            Class<?> cls = noticeMap.get(noticeType);
            try {
                BaseNotifyEvent event = (BaseNotifyEvent) new Gson().fromJson(noticeData, cls);
                //EventBus.getDefault().post(event);
                if (event != null) {
                    buildNotifyEvent(event);
                    DataStorageUtils.setShowMsgIndicator(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"error noticeType:" + noticeType + " content:" + noticeData);
            }

        } else {
            Log.e(TAG, "error noticeType == null");
        }
    }


    private void buildNotifyEvent(BaseNotifyEvent event) {
        Context ctx = AppUtils.getAppContext();
        if(ActivityManager.getInstance().isAppInFront()) {
            Vibrator vibrator = (Vibrator) ctx.getSystemService(ctx.VIBRATOR_SERVICE);
            vibrator.vibrate(vibrate,-1);
        } else {
            NotificationManager nm = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification n = new Notification(R.drawable.ic_launcher, "系统消息", System.currentTimeMillis());
            n.flags = Notification.FLAG_AUTO_CANCEL;
            n.defaults = Notification.DEFAULT_SOUND;
            Intent i = new Intent(ctx, AtyMessages.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent contentIntent = PendingIntent.getActivity(
                    ctx,
                    R.string.app_name,
                    i,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            n.setLatestEventInfo(
                    ctx,
                    getEventTitle(event),
                    getEventContent(event),
                    contentIntent);
            nm.notify(R.string.app_name, n);
        }
    }

    private String getEventTitle(BaseNotifyEvent event) {
        if(event instanceof ChaseNotifyEvent) {
            return  ((ChaseNotifyEvent) event).title + " 向你发了条消息";
        }
        return "系统消息";
    }

    private String getEventContent(BaseNotifyEvent event) {
        if(event instanceof ChaseNotifyEvent) {
            return  ((ChaseNotifyEvent) event).msg;
        }
        return "";
    }
}
