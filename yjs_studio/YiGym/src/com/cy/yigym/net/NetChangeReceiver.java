package com.cy.yigym.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.cy.yigym.event.EventNetStateChange;

import de.greenrobot.event.EventBus;

/**
 * Caiyuan Huang
 * <p>2015-5-5</p>
 * <p>网络状态改变广播接收器</p>
 */
public class NetChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent == null)
			return;
		String action = intent.getAction();
		if (TextUtils.isEmpty(action))
			return;
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isConnected()) {
					EventBus.getDefault().post(new EventNetStateChange(true));
				} else {
					EventBus.getDefault().post(new EventNetStateChange(false));
				}
			} else {
				EventBus.getDefault().post(new EventNetStateChange(false));
			}
		}

	}

}
