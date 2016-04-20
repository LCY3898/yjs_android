package com.hhtech.umeng.update;

import android.content.Context;
import android.widget.Toast;

import com.cy.widgetlibrary.content.DlgLoading;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * <p>
 * 友盟更新工具类
 * </p>
 */
public class UmengUpdateUtils {
	/**
	 * 
	 * 自动检查更新<br>
	 * 
	 * @param context
	 *            必须是Activity 作为context
	 */
	public static void update(Context context) {
		// 停止配置检查
		UmengUpdateAgent.setUpdateCheckConfig(false);
		// 在3G、Wifi的场景下都提示更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(context);
	}

	/**
	 * 检测更新,用户手动点击更新图标时使用
	 */
	public static void checkUpdate(final Context context,
			final OnUpdateCallBack cbk) {
		final DlgLoading dlgLoading = new DlgLoading(context);
		dlgLoading.getTxtContent().setText("正在检测更新，请稍后");
		dlgLoading.show();
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				dlgLoading.dismiss();
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(context, updateInfo);
					if (cbk != null)
						cbk.onUpdate(true);
					break;
				case UpdateStatus.No: // has no update
					showToast("已经是最新版本了", context);
					if (cbk != null)
						cbk.onUpdate(false);
					break;
				case UpdateStatus.NoneWifi: // none wifi
					showToast("没有wifi连接， 只在wifi下更新", context);
					break;
				case UpdateStatus.Timeout: // time out
					showToast("连接服务器超时", context);
					break;
				}
			}
		});
		UmengUpdateAgent.update(context);
	}

	/**
	 * 更新结果回调
	 */
	public static abstract class OnUpdateCallBack {
		public abstract void onUpdate(boolean hasUpdate);
	}

	public static void showToast(String txt, Context context) {
		Toast.makeText(context, txt, Toast.LENGTH_LONG).show();

	}

}
