package com.cy.yigym.net;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.cy.wbs.ReqBase;
import com.cy.wbs.RspBase;
import com.cy.wbs.WSHelper;
import com.cy.wbs.WSHelper.OnRespondCbk;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.net.req.ReqAccomplishInfo;
import com.cy.yigym.net.req.ReqAddTarget;
import com.cy.yigym.net.req.ReqAuthorize;
import com.cy.yigym.net.req.ReqFeedBack;
import com.cy.yigym.net.req.ReqGetAboutYjs;
import com.cy.yigym.net.req.ReqGetExerciseTarget;
import com.cy.yigym.net.req.ReqGetPevent;
import com.cy.yigym.net.req.ReqGetRecentTarget;
import com.cy.yigym.net.req.ReqGetUpload;
import com.cy.yigym.net.req.ReqGetUserInfo;
import com.cy.yigym.net.req.ReqLogOut;
import com.cy.yigym.net.req.ReqLogin;
import com.cy.yigym.net.req.ReqPrivateSend;
import com.cy.yigym.net.req.ReqRegis;
import com.cy.yigym.net.req.ReqRegisVerCode;
import com.cy.yigym.net.req.ReqUpdatePersonInfo;
import com.cy.yigym.net.rsp.RspAccomplishInfo;
import com.cy.yigym.net.rsp.RspAddTarget;
import com.cy.yigym.net.rsp.RspAuthorize;
import com.cy.yigym.net.rsp.RspFeedBack;
import com.cy.yigym.net.rsp.RspGetAboutYjs;
import com.cy.yigym.net.rsp.RspGetExerciseTarget;
import com.cy.yigym.net.rsp.RspGetPevent;
import com.cy.yigym.net.rsp.RspGetUpload;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.net.rsp.RspLogOut;
import com.cy.yigym.net.rsp.RspLogin;
import com.cy.yigym.net.rsp.RspRegis;
import com.cy.yigym.net.rsp.RspRegisVerCode;
import com.cy.yigym.net.rsp.RspUpdatePersonInfo;
import com.cy.yigym.net.rsp.RspUploadPhoto;
import com.cy.yigym.utils.DataStorageUtils;
import com.google.gson.Gson;
import com.hhtech.base.ProjConfig;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.StringCallback;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.MultipartFormDataBody;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-10
 * </p>
 * <p>
 * 服务端接口通信类
 * </p>
 */
public class YJSNet {
	private static Handler uiThreadHandler = new Handler(Looper.getMainLooper());

	/**
	 * 移除响应监听
	 * 
	 * @param requesterTag
	 */
	public static void removeRspCallBacks(String requesterTag) {
		if (TextUtils.isEmpty(requesterTag))
			return;
		WSHelper.getInstance().removeRespondCbks(requesterTag);
	}

	/**
	 * 释放请求单例
	 */
	public static void realse() {
		WSHelper.getInstance().realse();
	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-7-22
	 * </p>
	 * <p>
	 * 响应回调接口
	 * </p>
	 */
	public static interface OnRespondCallBack<T extends RspBase> {
		void onSuccess(T data);

		void onFailure(String errorMsg);
	}

	/**
	 * 获取注册验证码
	 * 
	 * @param phone
	 * @param requesterTag
	 * @param cbk
	 */
	public static void getRegisVerCode(ReqRegisVerCode req,
			String requesterTag, final OnRespondCallBack<RspRegisVerCode> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 注册
	 * 
	 * @param phone
	 * @param verCode
	 * @param psw
	 * @param cbk
	 */
	public static void regis(ReqRegis req, String requesterTag,
			final OnRespondCallBack<RspRegis> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 登录
	 * 
	 * @param phone
	 * @param psw
	 * @param requesterTag
	 * @param cbk
	 */
	public static void login(ReqLogin req, String requesterTag,
			final OnRespondCallBack<RspLogin> cbk) {
		final ReqLogin loginReq = req;
		send(req, requesterTag, new OnRespondCallBack<RspLogin>() {
			@Override
			public void onSuccess(RspLogin data) {
				CurrentUser.instance().setUserName(loginReq.getUsername());
				CurrentUser.instance().setPasswd(loginReq.getPasswd());
				CurrentUser.instance().setVerified(true);
				DataStorageUtils.setSession(data.sess);
				DataStorageUtils.setVoipObj(data.data.user_info.subAccount);
				DataStorageUtils
						.setDownloadUrl(data.data.server_info.download_path);
				DataStorageUtils.setUploadUrl(data.data.server_info.upload_to);
				DataStorageUtils
						.setMp4LinkPrefix(data.data.server_info.mp4_link_prefix);
				//保存网易账号信息
				DataStorageUtils.setNetEaseAccount(data.data.user_info.neteaseIMAcc);
				cbk.onSuccess(data);
			}

			@Override
			public void onFailure(String errorMsg) {
				cbk.onFailure(errorMsg);
			}
		});
	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-7-13
	 * </p>
	 * <p>
	 * 运动目标目标类型
	 * </p>
	 */
	public static enum TargetType {
		TIME("time"), DISTANCE("distance"), CALORIE("calorie");
		public String value;

		TargetType(String value) {
			this.value = value;
		}
	};

	/**
	 * 用户设置运动目标
	 * 
	 * @param type
	 * @param size
	 */
	public static void addTarget(TargetType type, String size,
			String requesterTag, final OnRespondCallBack<RspAddTarget> cbk) {
		if (type == null)
			return;
		send(new ReqAddTarget(type.value, size), requesterTag, cbk);
	}

	/**
	 * 获取最近设置的运动目标
	 * 
	 * @param requesterTag
	 * @param cbk
	 */
	public static void getRecentTarget(String requesterTag,
			final OnRespondCallBack<RspBase> cbk) {
		send(new ReqGetRecentTarget(), requesterTag, cbk);
	}

	/**
	 * 获取上传地址
	 * 
	 * @param requesterTag
	 * @param cbk
	 */
	public static void getUpload(String requesterTag,
			final OnRespondCallBack<RspGetUpload> cbk) {
		send(new ReqGetUpload(), requesterTag, cbk);
	}

	/**
	 * 退出登入
	 * 
	 * @param requesterTag
	 * @param cbk
	 */
	public static void logout(String requesterTag,
			final OnRespondCallBack<RspLogOut> cbk) {
		DataStorageUtils.setLogin(false);
		DataStorageUtils.setUserInfoComplete(false);
		send(new ReqLogOut(), requesterTag, new OnRespondCallBack<RspLogOut>() {
			@Override
			public void onSuccess(RspLogOut data) {
				CurrentUser.instance().setVerified(false);
				DataStorageUtils.setSession("");
				DataStorageUtils.setPid("");
				cbk.onSuccess(data);
			}

			@Override
			public void onFailure(String errorMsg) {
				cbk.onFailure(errorMsg);
			}
		});
	}

	/**
	 * 完善个人信息
	 * 
	 * @param display_name
	 * @param sex
	 * @param age
	 * @param height
	 * @param weight
	 * @param requesterTag
	 * @param cbk
	 */
	public static void accomplishInfo(ReqAccomplishInfo req,
			String requesterTag, final OnRespondCallBack<RspAccomplishInfo> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 更新个人资料
	 * 
	 * @param data
	 * @param requesterTag
	 * @param cbk
	 */
	public static void updatePersonInfo(ReqUpdatePersonInfo req,
			String requesterTag,
			final OnRespondCallBack<RspUpdatePersonInfo> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param data
	 * @param requesterTag
	 * @param cbk
	 */
	public static void getUserInfo(ReqGetUserInfo req, String requesterTag,
			final OnRespondCallBack<RspGetUserInfo> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 意见反馈
	 * 
	 * @param data
	 * @param requesterTag
	 * @param cbk
	 */
	public static void feedBack(ReqFeedBack req, String requesterTag,
			final OnRespondCallBack<RspFeedBack> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 关于易 jianshen
	 * 
	 * @param data
	 * @param requesterTag
	 * @param cbk
	 */
	public static void aboutYjs(ReqGetAboutYjs req, String requesterTag,
			final OnRespondCallBack<RspGetAboutYjs> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 第三方登录
	 * 
	 * @param req
	 * @param requesterTag
	 * @param cbk
	 */
	public static void authorize(ReqAuthorize req, String requesterTag,
			OnRespondCallBack<RspAuthorize> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * IM发送消息
	 * 
	 * @param req
	 * @param requesterTag
	 * @param cbk
	 */
	public static void privateSend(ReqPrivateSend req, String requesterTag,
			OnRespondCallBack<RspBase> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 获取成长记录数据
	 * 
	 * @param req
	 * @param requesterTag
	 * @param cbk
	 */
	public static void getPevent(ReqGetPevent req, String requesterTag,
			OnRespondCallBack<RspGetPevent> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 获取运动目标
	 * 
	 * @param req
	 * @param requesterTag
	 * @param cbk
	 */
	public static void getExerciseTarget(ReqGetExerciseTarget req,
			String requesterTag, OnRespondCallBack<RspGetExerciseTarget> cbk) {
		send(req, requesterTag, cbk);
	}

	/**
	 * 发送请求底层方法
	 * 
	 * @param reqObj
	 *            请求数据对象
	 * @param requesterTag
	 *            请求者标记
	 * @param l
	 *            请求响应回调
	 */
	@SuppressWarnings("unchecked")
	public static <T extends RspBase> void send(ReqBase reqObj,
			String requesterTag, final OnRespondCallBack<T> l) {
		if (l == null)
			return;
		reqObj.sess = DataStorageUtils.getSession();
		String pid = DataStorageUtils.getPid();
		reqObj.pid = TextUtils.isEmpty(reqObj.pid) ? pid : reqObj.pid;
		WSHelper.getInstance().send(reqObj, getRspClass(l), requesterTag,
				new OnRespondCbk() {

					@Override
					public void onSuccess(String requestUrl,
							String respondResult, RspBase mObject) {
						try {

							T data = (T) mObject;
							if (mObject.isOk()) {
								l.onSuccess(data);
							} else {
								l.onFailure(mObject.msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							l.onFailure(e == null || e.getMessage() == null ? ""
									: e.getMessage().toString());
						}
					}

					@Override
					public void onFailure(String errorMsg) {
						l.onFailure(errorMsg);
					}

				});
	}

	/**
	 * 获取响应类型的Class
	 * 
	 * @param cbk
	 * @return
	 */
	public static Class<?> getRspClass(OnRespondCallBack<?> cbk) {
		// OnRespondCallBack 是一个interface
		Type type = cbk.getClass().getGenericInterfaces()[0];
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> cls = (Class<?>) parameterizedType
					.getActualTypeArguments()[0];
			return cls;
		}
		return null;
	}

	/**
	 * 上传照片
	 * 
	 * @param photoPath
	 * @param cbk
	 */
	public static void uploadPhoto(String photoPath,
			final OnRespondCallBack<RspUploadPhoto> cbk) {
		if (cbk == null)
			return;
		if (TextUtils.isEmpty(photoPath)) {
			cbk.onFailure("文件地址不存在");
			return;
		}
		File file = new File(photoPath);
		if (!file.exists()) {
			cbk.onFailure("文件不存在");
			return;
		}
		AsyncHttpPost post = new AsyncHttpPost(DataStorageUtils.getUploadUrl());
		MultipartFormDataBody body = new MultipartFormDataBody();
		body.addFilePart("local_file", file);
		body.addStringPart("proj",
				ProjConfig.PRODUCT_ENV ? ProjConfig.PRODUCT_PIC_DIR
						: ProjConfig.TEST_PIC_DIR);
		post.setBody(body);
		AsyncHttpClient.getDefaultInstance().executeString(post,
				new StringCallback() {

					@Override
					public void onCompleted(final Exception e,
							AsyncHttpResponse source, final String result) {
						uiThreadHandler.post(new Runnable() {

							@Override
							public void run() {
								if (e != null) {
									cbk.onFailure("上传失败");
									return;
								}
								try {
									RspUploadPhoto data = new Gson().fromJson(
											result, RspUploadPhoto.class);
									if (data != null)
										cbk.onSuccess(data);
									else
										cbk.onFailure("上传失败");
								} catch (Exception e1) {
									e1.printStackTrace();
									cbk.onFailure("上传失败");
								}

							}
						});

					}
				});
	}

}
