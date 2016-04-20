package com.cy.yigym.service;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.cy.wbs.event.EventCourseNotify;
import com.cy.wbs.event.EventReceiveIMMsg;
import com.cy.widgetlibrary.base.ActivityManager;
import com.cy.yigym.aty.AtyMain;
import com.cy.yigym.aty.AtyMain2;
import com.cy.yigym.db.dao.IMChatMsgDao;
import com.cy.yigym.db.dao.IMRecentMsgDao;
import com.cy.yigym.db.entity.IMChatMsg;
import com.cy.yigym.db.entity.IMRecentMsg;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventIMMsgReceived;
import com.cy.yigym.logic.im.MsgCoverUtils;
import com.cy.yigym.net.rsp.RspGetPrivateChat;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.NotificationUtils;
import com.cy.yigym.utils.VibratorUtils;
import com.efit.sport.R;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-25
 * </p>
 * <p>
 * Websock相关的业务分发辅助服务
 * </p>
 */
public class WSService extends Service {
	private Context mContext;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mContext = this;
		EventBus.getDefault().register(imMsgListener);
		EventBus.getDefault().register(courseNotifyListener);
		return START_STICKY;
	}

	private BusEventListener.MainThreadListener<EventReceiveIMMsg> imMsgListener = new BusEventListener.MainThreadListener<EventReceiveIMMsg>() {
		@Override
		public void onEventMainThread(EventReceiveIMMsg event) {
			RspGetPrivateChat msg;
			try {
				msg = new Gson().fromJson(event.imMsg, RspGetPrivateChat.class);
				IMChatMsg chatMsg = MsgCoverUtils
						.rspGetPrivateChat2IMChatMsg(msg);
				// 优先保存到本地,防止不在界面时依然可以保存信息
				IMChatMsgDao.getInstance().insert(chatMsg);
				IMRecentMsgDao.getInstance().update(
						new IMRecentMsg(chatMsg.msgSender, chatMsg.msgTime));
				EventBus.getDefault().post(new EventIMMsgReceived(chatMsg));
				DataStorageUtils.setShowMsgIndicator(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private BusEventListener.MainThreadListener<EventCourseNotify> courseNotifyListener = new BusEventListener.MainThreadListener<EventCourseNotify>() {
		@Override
		public void onEventMainThread(EventCourseNotify event) {
			Log.i("WSService", "--------------------------EventCourseNotify:" + event.courseNotify);
			String respondResult = event.courseNotify;
			if (TextUtils.isEmpty(respondResult))
				return;
			try {
				JSONObject json = new JSONObject(respondResult);
				String content = json.getString("msg_content");
				if (TextUtils.isEmpty(content))
					return;
				if (ActivityManager.getInstance().isAppInFront()
						|| TextUtils.isEmpty(DataStorageUtils.getPid())) {
					// 在前台时并且用户没有登录过，则震动,点击通知不跳转
					NotificationUtils.notity(mContext, R.drawable.ic_launcher,
							getResources().getString(R.string.app_name),
							content, null, null);
					VibratorUtils.vibrate(mContext);
				} else {
					// 在后台震动，点击通知跳到主页
					NotificationUtils.notity(mContext, R.drawable.ic_launcher,
							getResources().getString(R.string.app_name),
							content, null, AtyMain2.class);
				}
				DataStorageUtils.setShowMsgIndicator(true);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(imMsgListener);
		EventBus.getDefault().unregister(courseNotifyListener);
	};

}
