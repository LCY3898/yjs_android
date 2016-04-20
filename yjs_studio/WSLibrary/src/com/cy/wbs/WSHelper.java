package com.cy.wbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.cy.wbs.event.EventCourseNotify;
import com.cy.wbs.event.EventReceiveIMMsg;
import com.cy.wbs.event.EventUnlogin;
import com.google.gson.Gson;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;

import de.greenrobot.event.EventBus;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-13
 * </p>
 * <p>
 * 类Http调用方式的WebSocket请求类
 * </p>
 */
public class WSHelper {
	private final String LOG_TAG = WSHelper.class.getSimpleName();
	private Handler mainHandler = new Handler(Looper.getMainLooper());
	private static WSHelper mInstance = null;
	private WebSocket mSocket = null;
	private Map<String, CallBackObject> callbacks;
	private UITimer timer;
	private final int POLLING_TIME_OUT_PERIOD_MILLIS = 10000;// 轮询超时间隔毫秒数
	private final int TIME_OUT_MILLIS = 15000;// 超时毫秒数
	private final int PING_TIME_OUT = 80*1000;// 超时毫秒数
	private final int PING_INTERVAL = 25*1000;// ping时间间隔

	long lastRecvMills = 0;
	long lastPingMills = 0;
	boolean isConnected = false;

	private NotifyListener mNotifyListener;

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-7-8
	 * </p>
	 * <p>
	 * 响应原文
	 * </p>
	 */
	public static abstract class OnRespondCbk {
		/**
		 * 请求成功回调
		 * 
		 * @param requestUrl
		 *            请求的地址
		 * @param respondResult
		 *            响应的结果字符串
		 * @param mObject
		 *            响应的结果字符串解析出来的对象
		 */
		public abstract void onSuccess(String requestUrl, String respondResult,
				RspBase mObject);

		/**
		 * 
		 * 请求失败回调接口
		 * 
		 * @param errorMsg
		 *            失败类型
		 */
		public abstract void onFailure(String errorMsg);

		/**
		 * 回调服务端响应原文
		 * 
		 * @param respondResult
		 */
		public void onResult(String respondResult) {
		};
	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-7-8
	 * </p>
	 * <p>
	 * 回调对象，内部关联请求使用
	 * </p>
	 */
	private class CallBackObject {
		public OnRespondCbk cbk;// 响应回调
		public String timestamp = "";// 请求时间戳
		public long reqStartTimeMillis = 0;// 发送请求时的毫秒数,用于超时判断
		public Class<? extends Object> rspClass;// 响应数据对象类
		public String requesterTag = "";// 请求者标记，用来退出回调

		CallBackObject(OnRespondCbk cbk, String timestamp,
				Class<? extends Object> rspClass, long reqStartTimeMillis,
				String requesterTag) {
			this.cbk = cbk;
			this.timestamp = timestamp;
			this.rspClass = rspClass;
			this.reqStartTimeMillis = reqStartTimeMillis;
			this.requesterTag = requesterTag;
		}

	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-7-8
	 * </p>
	 * <p>
	 * 网络错误类型
	 * </p>
	 */
	public static enum NetErrorType {

		REQUEST_PARAMS_EMPTY_ERROR("网络请求参数为空"), NETWORK_NOT_AVAILABLE_ERROR(
				"网络不可用"), NO_DATA_ERROR("服务端没有返回数据"), JASON_PARSE_ERROR(
				"数据解析错误"), OTHER_ERROR("其它错误"), CONNECT_ERROR("连接服务端失败"), CONNECT_TIMEOUT(
				"连接超时");

		private String value;

		NetErrorType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public static WSHelper getInstance() {
		if (mInstance == null) {
			synchronized (WSHelper.class) {
				if (mInstance == null)
					mInstance = new WSHelper();
			}
		}
		return mInstance;
	}

	public void setNotifyListener(NotifyListener l) {
		this.mNotifyListener = l;
	}

	private WSHelper() {
		callbacks = new HashMap<String, WSHelper.CallBackObject>();
		timer = new UITimer();
		// 轮询判断超时的回调
		timer.schedule(new Runnable() {

			@Override
			public void run() {

				ArrayList<String> rmKeys = new ArrayList<String>();
				for (Map.Entry<String, CallBackObject> entry : callbacks
						.entrySet()) {
					if (System.currentTimeMillis()
							- entry.getValue().reqStartTimeMillis >= TIME_OUT_MILLIS) {
						rmKeys.add(entry.getKey());
					}
				}
				// 删除超时的回调
				for (int i = 0; i < rmKeys.size(); i++) {
					callbacks.get(rmKeys.get(i)).cbk
							.onFailure(NetErrorType.CONNECT_TIMEOUT.value);
					callbacks.remove(rmKeys.get(i));
				}
				pingIfNeed();
			}
		}, 0, POLLING_TIME_OUT_PERIOD_MILLIS);
	}

	private void pingIfNeed() {
		if(!NetworkUtils.isNetworkAvailable()) {
			isConnected = false;
			mSocket = null;
			return;
		}
		if(!isConnected) {
			return;
		}

		long now = System.currentTimeMillis();
		//接收时间离当前时间已超过超时时间
		if(now - lastRecvMills > PING_TIME_OUT) {
			isConnected = false;
			mSocket = null;
			connect(new WebSocketConnectCallback() {
				@Override
				public void onCompleted(Exception ex, WebSocket webSocket) {
					onConnectCompleted(webSocket);
					lastRecvMills = System.currentTimeMillis();
				}
			});
			return;
		}
		if(now - lastPingMills < PING_INTERVAL) {
			return;
		}

		try {
			SharedPreferences sp = WSLibInitializer.getAppContext()
					.getSharedPreferences(
							WSLibInitializer.getAppContext().getPackageName(),
							Context.MODE_PRIVATE);

			String sess = sp.getString("session", "");
			if(TextUtils.isEmpty(sess)) {
				return;
			}
			JSONObject jo = new JSONObject();
			jo.put("io", "i");
			jo.put("obj", "server");
			jo.put("act", "pina");
			jo.put("sess", sess);
			lastPingMills = System.currentTimeMillis();
			send(jo.toString(), String.valueOf(System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 连接服务端
	 * 
	 * @param callback
	 */
	private void connect(WebSocketConnectCallback callback) {
		if (mSocket != null)
			return;
		AsyncHttpClient.getDefaultInstance().websocket(NetUrl.url.getBaseUrl(),
				"myprotocol", callback);
	}

	/**
	 * 连接完成
	 * 
	 * @param socket
	 */
	private void onConnectCompleted(WebSocket socket) {
		if (socket == null)
			return;
		mSocket = socket;
		mSocket.setStringCallback(new StringCallback() {

			@Override
			public void onStringAvailable(String s) {
				onRspResultAcailable(s);
			}
		});
		mSocket.setClosedCallback(new CompletedCallback() {

			@Override
			public void onCompleted(Exception ex) {
				mSocket = null;
				isConnected = false;
			}
		});
		isConnected = true;
	}

	/**
	 * 数据到达
	 * 
	 * @param result
	 */
	private void onRspResultAcailable(String rsp) {
		lastRecvMills = System.currentTimeMillis();
		LogUtils.i(LOG_TAG, "onRspResultAcailable=" + rsp);
		// TODO: 临时解决数据头有int数字问题
		int start = rsp.indexOf('{');
		int end = rsp.lastIndexOf('}');
		final String result = rsp.substring(start, end + 1);


		if (handleNotify(result)) {
			return;
		}
		//tangtt comment out,提高效率,所有消息只在handleNotify中处理
		/*if (handleReceiveMsg(result)) {
			return;
		}
		if (handleCourseNotity(result)) {
			return;
		}*/
		if(!result.contains("timestamp")) {
			return;
		}
		String timestamp = getReqTimestampFormRsp(result);
		final CallBackObject callBackObject = callbacks.get(timestamp);
		if (callBackObject == null)
			return;
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				handleRspResult(callBackObject, result);
			}
		});

	}

	private boolean handleReceiveMsg(String content) {
		try {
			JSONObject json = new JSONObject(content);
			String act = json.getString("act");
			if (act.equals("get_private_chat")) {
				EventBus.getDefault().post(new EventReceiveIMMsg(content));
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 处理课程通知
	 * 
	 * @param content
	 * @return
	 */
	private boolean handleCourseNotity(String content) {
		try {
			JSONObject json = new JSONObject(content);
			String act = json.getString("act");
			if (act.equals("sendNotifies")) {
				EventBus.getDefault().post(new EventCourseNotify(content));
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void test(String data) {
		onRspResultAcailable(data);
	}

	private boolean handleNotify(String content) {
		try {
			JSONObject json = new JSONObject(content);

			try {
				String act = json.getString("act");
				if (act.equals("get_private_chat")) {//handleReceiveMsg
					EventBus.getDefault().post(new EventReceiveIMMsg(content));
					return true;
				} else if (act.equals("sendNotifies")) {//handleCourseNotity
					//courseNotify
					EventBus.getDefault().post(new EventCourseNotify(content));
					return true;
				} else if(act.equals("logout")) {
					int force = json.optInt("force");
					if(force == 1) {
						EventBus.getDefault().post(new EventUnlogin(EventUnlogin.UnloginReason.KICK_OFF));
						return true;
					} else if(!content.contains("timestamp")) {
						EventBus.getDefault().post(new EventUnlogin(EventUnlogin.UnloginReason.SESSION_TIMEOUT));
						return true;
					}
				}
			} catch (Exception e) {

			}
			String notice = json.getString("notice");
			if ("true".equals(notice)) {
				String noticeType = json.getString("notice_type");
				String obj = json.optString("obj");
				String act = json.getString("act");
				JSONObject noticeData = json.optJSONObject("data");
				dispatchNotifyEvent(noticeType, noticeData, obj, act);
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	private void dispatchNotifyEvent(String noticeType, JSONObject noticeData,
			String obj, String act) {
		if (mNotifyListener != null) {
			String noticeContent;
			if (noticeData == null) {
				noticeContent = "{}";
			} else {
				noticeContent = noticeData.toString();
			}
			mNotifyListener.onNotifyEvent(noticeType, noticeContent, obj, act);
		}
	}

	/**
	 * 处理响应结果
	 * 
	 * @param cbk
	 * @param rsp
	 */
	private void handleRspResult(CallBackObject callBackObject, String rsp) {
		if (TextUtils.isEmpty(rsp)) {
			callBackObject.cbk.onFailure(NetErrorType.NO_DATA_ERROR.value);
			callbacks.remove(callBackObject.timestamp);
			return;
		}
		try {
			// Gson会自动处理null，如果字段值为null，则对象中的字段也会置为null
			/*
			 * GsonBuilder builder = new GsonBuilder();
			 * builder.registerTypeAdapter(String.class, new StringConverter());
			 * Gson gson = builder.create();
			 */

			Object mObject = new Gson().fromJson(rsp, callBackObject.rspClass);
			if(RspBase.USER_NOT_EXIST.equals(((RspBase)mObject).code)) {
				callBackObject.cbk.onFailure(NetErrorType.OTHER_ERROR.value);
				callbacks.remove(callBackObject.timestamp);
				EventBus.getDefault().post(new EventUnlogin(EventUnlogin.UnloginReason.USER_NOT_EXIST));
				return;
			}
			if (mObject != null) {
				callBackObject.cbk.onSuccess(NetUrl.url.getBaseUrl(), rsp,
						(RspBase) mObject);
				callbacks.remove(callBackObject.timestamp);
			} else {
				callBackObject.cbk.onFailure(NetErrorType.NO_DATA_ERROR.value);
				callbacks.remove(callBackObject.timestamp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			callBackObject.cbk.onFailure(NetErrorType.JASON_PARSE_ERROR.value);
			callbacks.remove(callBackObject.timestamp);
		}

	}

	/**
	 * 从响应结果中返回请求时间戳
	 * 
	 * @param rsp
	 */
	private String getReqTimestampFormRsp(String rsp) {

		try {

			JSONObject json = new JSONObject(rsp);
			JSONObject echo = json.getJSONObject("echo");
			String timestamp = echo.getString("timestamp");
			return timestamp;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	public void initConnect() {
		connect(new WebSocketConnectCallback() {
			@Override
			public void onCompleted(Exception error, WebSocket webSocket) {
				if (error != null || webSocket == null) {
					return;
				}
				onConnectCompleted(webSocket);
			}
		});
	}

	/**
	 * 发送请求
	 * 
	 * @param content
	 * @param timestamp
	 */
	void send(final String content, final String timestamp) {
		if (mSocket == null||!mSocket.isOpen()) {
			connect(new WebSocketConnectCallback() {
				@Override
				public void onCompleted(Exception error, WebSocket webSocket) {
					if (error != null || webSocket == null) {
						final CallBackObject callBackObject = callbacks
								.get(timestamp);
						if (callBackObject != null) {
							mainHandler.post(new Runnable() {

								@Override
								public void run() {
									callBackObject.cbk
											.onFailure(NetErrorType.CONNECT_ERROR.value);
								}
							});
						}
						return;
					}
					onConnectCompleted(webSocket);
					LogUtils.i(LOG_TAG, "send=" + content + " timestamp="
							+ timestamp);
					mSocket.send(content + "\n");
				}
			});
		} else {
			LogUtils.i(LOG_TAG, "send=" + content + " timestamp=" + timestamp);
			mSocket.send(content + "\n");
		}
	}

	/**
	 * 发送请求
	 * 
	 * @param reqObject
	 *            请求数据对象
	 * @param rspClass
	 *            返回数据类型
	 * @param timestamp
	 *            请求时间戳，用于表示某次具体的请求
	 * @param requesterTag
	 *            请求者标记，用于退出时取消回调
	 * @param cbk
	 *            请求回调
	 */
	public void send(final ReqBase reqObject,
			final Class<? extends Object> rspClass, String requesterTag,
			final OnRespondCbk cbk) {
		String timestamp = System.currentTimeMillis() + "";
		reqObject.echo.timestamp = timestamp;
		boolean isLegal = handleBeforeRequest(reqObject, rspClass, timestamp,
				cbk);
		if (!isLegal)
			return;
		try {
			CallBackObject callBackObject = new CallBackObject(cbk, timestamp,
					rspClass, System.currentTimeMillis(), requesterTag);
			callbacks.put(timestamp, callBackObject);
			String reqJson = new Gson().toJson(reqObject);
			send(reqJson, timestamp);
		} catch (Exception e) {
			e.printStackTrace();
			if (callbacks.containsKey(timestamp)) {
				callbacks.remove(timestamp);
			}
		}
	}

	/**
	 * 处理请求前的条件
	 * 
	 * @param reqUrl
	 * @param params
	 * @param rspClass
	 * @param cbk
	 * @return true表示满足请求的条件，false表示请求条件不满足
	 */
	private boolean handleBeforeRequest(Object params,
			Class<? extends Object> rspClass, String timestamp,
			final OnRespondCbk cbk) {
		if (cbk == null)
			return false;
		if (TextUtils.isEmpty(timestamp) || params == null || rspClass == null) {
			cbk.onFailure(NetErrorType.REQUEST_PARAMS_EMPTY_ERROR.value);
			return false;
		}
		if (NetworkUtils.isNetworkAvailable() == false) {
			cbk.onFailure(NetErrorType.NETWORK_NOT_AVAILABLE_ERROR.value);
			return false;
		}
		return true;
	}

	/**
	 * 释放
	 */
	public void realse() {
		if (mSocket != null) {
			mSocket.close();
			mSocket = null;
		}
		mInstance = null;
		callbacks = null;
	}

	/**
	 * 取消回调监听
	 * 
	 * @param requesterTag
	 *            请求者标记,同
	 *            {@link #send(Object, Class, String, String, OnRespondCbk)
	 *            }
	 *            方法里面的参数
	 */
	public void removeRespondCbks(String requesterTag) {
		if (TextUtils.isEmpty(requesterTag))
			return;
		ArrayList<String> rmKeys = new ArrayList<String>();
		for (Map.Entry<String, CallBackObject> entry : callbacks.entrySet()) {
			if (entry.getValue().requesterTag.equals(requesterTag)) {
				rmKeys.add(entry.getKey());
			}
		}
		// 删除相关回调
		for (int i = 0; i < rmKeys.size(); i++) {
			callbacks.remove(rmKeys.get(i));
		}
	}
}
