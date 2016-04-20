package com.cy.yigym.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-10-27
 * </p>
 * <p>
 * 通知栏消息工具类
 * </p>
 */
public class NotificationUtils {
	/**
	 * 通知
	 * 
	 * @param context
	 * @param iconId
	 *            通知图标的资源id
	 * @param title
	 *            通知标题
	 * @param content
	 *            通知内容文字
	 * @param extras
	 *            点击通知启动的意图的额外数据
	 * @param atyCls
	 *            点击通知想启动的界面的类
	 */
	public static void notity(Context context, int iconId, String title,
			String content, Bundle extras, Class<? extends Activity> atyCls) {
		if (iconId == 0 || TextUtils.isEmpty(title)
				|| TextUtils.isEmpty(content))
			throw new IllegalArgumentException("iconId,title,content不能有一个为空");
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder builder = new Notification.Builder(context)
				.setSmallIcon(iconId).setContentTitle(title)
				.setContentText(content).setWhen(System.currentTimeMillis());
		if (atyCls != null) {
			Intent intent = new Intent(context, atyCls);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			if (extras != null)
				intent.putExtras(extras);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			builder.setContentIntent(pendingIntent);
		}
		Notification notification = builder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND;
		notificationManager.notify(1, notification);
	}
}
