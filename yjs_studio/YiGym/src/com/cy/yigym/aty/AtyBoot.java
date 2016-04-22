package com.cy.yigym.aty;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.service.WSService;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.ServiceUtils;
import com.efit.sport.R;
import com.efit.sport.mgr.LoginMgr;
import com.hhtech.base.AppUtils;
import com.sport.efit.constant.LoginChannel;
import com.umeng.analytics.MobclickAgent;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-8
 * </p>
 * <p>
 * 登录界面
 * </p>
 */
public class AtyBoot extends BaseFragmentActivity {
	private Handler mainHandler = new Handler(Looper.getMainLooper());

	@Override
	protected boolean isBindViewByAnnotation() {
		return false;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_boot;
	}

	@Override
	protected void initView() {
		MobclickAgent.updateOnlineConfig(mContext);
	}

	private Runnable delayShow = new Runnable() {
		@Override
		public void run() {
			if (DataStorageUtils.isLogin()) {
				if (DataStorageUtils.isUserInfoComplete()) {
					startActivity(AtyMain2.class);
				} else {
					startActivity(AtyPersonalInfo.class);
				}
			} else {

			}
			finish();
		}
	};

	@Override
	protected void initData() {
		if (!ServiceUtils.isServiceRunning(mActivity, WSService.class)) {
			startService(new Intent(mActivity, WSService.class));
		}
		if (!DataStorageUtils.isHasGuide()) {
			mainHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					startActivity(AtyFirstGuide.class);
					finish();
				}
			}, 2000);
			return;
		}
		if (!DataStorageUtils.isLogin()
				|| DataStorageUtils.getNetEaseAccount() == null) {
			startActivity(AtyLogin.class);
			finish();
			return;
		}

		// 骆总要求 新版本不支持微信和微博登录
		if (DataStorageUtils.getLoginChannel() != LoginChannel.CHANNEL_NORMAL) {
			startActivity(AtyLogin.class);
			finish();
			return;
		}

		// 服务器要求90s内必须ping 它，否则会把session关闭
		LoginMgr.loginBackground();

		BleConnect.instance().getBleMgr().forceEnable();

		AppUtils.runOnUIDelayed(500, delayShow);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
