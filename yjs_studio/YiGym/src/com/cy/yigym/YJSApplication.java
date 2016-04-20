package com.cy.yigym;

import android.app.Application;

import com.cy.ble.BLELibInitializer;
import com.cy.imagelib.ImageLibInitializer;
import com.cy.share.ShareLibInitializer;
import com.cy.wbs.WSLibInitializer;
import com.cy.widgetlibrary.WidgetLibInitializer;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.utils.AppUtils;
import com.danikula.videocache.internal.VideoCacheInitializer;
import com.efit.sport.videochat.VideoChatHelper;

public class YJSApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// 注: 对于一个应用中有多个进程的情况(百度地图会增加:remote进程)，onCreate会被多次调用
		// 因此不宜进行单例或者加载库等操作初始化
		com.hhtech.base.AppUtils.init(this);
		WidgetLibInitializer.init(this);
		WSLibInitializer.init(this);
		AppUtils.init(this);
		ShareLibInitializer.init(this, true);
		ImageLibInitializer.init(this);
		BLELibInitializer.init(this);
		VideoCacheInitializer.init(this);
		BleConnect.instance();
		VideoChatHelper.onAppCreate();
	}

}
