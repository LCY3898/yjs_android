package com.cy.wbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>2015-5-5</p>
 * <p>网络状态改变广播接收器</p>
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
	private OnNetworkChangeListener mNetworkChangeListener;

	public interface OnNetworkChangeListener {
		void onConnect(NetworkInfo networkInfo);

		void onDisConnect();
	}

	public NetworkChangeReceiver(OnNetworkChangeListener l) {
		mNetworkChangeListener = l;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent == null)
			return;
		String action = intent.getAction();
		if (TextUtils.isEmpty(action))
			return;
		if (action.equals(android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isConnected()) {
					if (mNetworkChangeListener != null)
						mNetworkChangeListener.onConnect(networkInfo);
				} else {
					if (mNetworkChangeListener != null)
						mNetworkChangeListener.onDisConnect();
				}
			} else {
				if (mNetworkChangeListener != null)
					mNetworkChangeListener.onDisConnect();
			}
		}

	}

}
