package com.efit.sport.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.efit.sport.videochat.EventRecvCall;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/10/15 0015.
 */
public class VideoCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new EventRecvCall(intent));
        Log.i("SDKCoreHelper", "isconnect: has incoming call");
    }
}
