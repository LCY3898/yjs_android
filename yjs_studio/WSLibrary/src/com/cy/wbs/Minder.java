package com.cy.wbs;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

import org.json.JSONObject;


public class Minder extends Service {
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Runnable minderRunnable = null;
    private final static long MAX_RESPONSE_TIME = 15;//in secs

    private long lastPingMills = 0;

    private final IBinder mBinder = new LocalBinder();

    public void onCreate() {

        minderRunnable = new Runnable() {
            public void run() {
                acquireWakeLock();

                if (WSHelper.getInstance().isConnected) {
                    long timeNow = System.currentTimeMillis();

                    int pingInterval = 30;

                    if ((WSHelper.getInstance().lastRecvMills < lastPingMills) && (timeNow - lastPingMills > 1000 * MAX_RESPONSE_TIME)) {
                        //connect is out,try to reconnect
                    } else if ((pingInterval > 0) && (timeNow - WSHelper.getInstance().lastRecvMills > pingInterval)) {
                        ping();
                    }
                }
                handler.postDelayed(this, 1000 * MAX_RESPONSE_TIME);
            }
        };
        handler.postDelayed(minderRunnable, 1000 * MAX_RESPONSE_TIME);
    }

    private PowerManager.WakeLock wakelock;
    private boolean isWakelockAcquired = false;

    private void ping() {
        JSONObject jo = null;
        try {
            jo = new JSONObject();
            jo.put("io", "i");
            jo.put("obj", "server");
            jo.put("act", "pina");
        } catch (Exception e) {
            e.printStackTrace();
        }
        lastPingMills = System.currentTimeMillis();
        WSHelper.getInstance().send(jo.toString(), String.valueOf(System.currentTimeMillis()));
    }

    public void acquireWakeLock() {
        if (wakelock == null) {
            PowerManager pm = (PowerManager) getSystemService("power");
            wakelock = pm.newWakeLock(1, "com.efit.sport");
        }

        if ((wakelock != null) && (!isWakelockAcquired)) {
            wakelock.acquire();
            isWakelockAcquired = true;
        }
    }

    public void releaseWakeLock() {
        if (wakelock != null)
            wakelock.release();
        isWakelockAcquired = false;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        return 1;
    }

    public void onDestroy() {
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        Minder getService() {
            return Minder.this;
        }
    }
}