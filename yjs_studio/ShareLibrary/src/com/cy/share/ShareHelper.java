package com.cy.share;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;

import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-14
 * </p>
 * <p>
 * 分享工具类</br> 1.支持分享到微信。</br> 2.支持分享到微信朋友圈。</br> 3.支持分享到qq。</br>
 * 4.支持分享到qq空间。</br> 5.支持新浪微博登录。</br> 6.支持微信登录。</br>
 * </p>
 * <p>
 * 继承文档，详见{@link http://dev.umeng.com/social/android/detail-share#3}
 * </p>
 */
public class ShareHelper {
	private static ShareHelper mInstance = null;
	private final String SHARE_SERVICE_NAME = "com.umeng.share";
	private final String LOGIN_SERVICE_NAME = "com.umeng.login";
	private UMSocialService loginService;
	private UMSocialService shareService;
	private Context context;
	private Map<String, Platform> platforms = new HashMap<String, ShareHelper.Platform>();

	synchronized public static ShareHelper getInstance() {
		if (mInstance == null)
			mInstance = new ShareHelper();
		return mInstance;
	}

	private ShareHelper() {
		context = ShareLibInitializer.getAppContext();
		initShare();

	}

	private void initShare() {
		shareService = UMServiceFactory.getUMSocialService(SHARE_SERVICE_NAME);
		loginService = UMServiceFactory.getUMSocialService(LOGIN_SERVICE_NAME);
		boolean isWeixinOk = isOk(ShareConfig.WEIXIN_APP_ID,
				ShareConfig.WEIXIN_APP_SECRET);
		boolean isQqAndQzoneOk = isOk(ShareConfig.QQ_AND_QZONE_APP_ID,
				ShareConfig.QQ_AND_QZONE_APP_KEY);
		boolean isSinaWeiboOk = isOk(ShareConfig.SINA_APP_ID,
				ShareConfig.SINA_APP_KEY);
		if (isWeixinOk) {
			// 支持分享到微信
			UMWXHandler wxHandler = new UMWXHandler(context,
					ShareConfig.WEIXIN_APP_ID, ShareConfig.WEIXIN_APP_SECRET);
			wxHandler.addToSocialSDK();
			// 支持分享到微信朋友圈
			UMWXHandler wxCircleHandler = new UMWXHandler(context,
					ShareConfig.WEIXIN_APP_ID, ShareConfig.WEIXIN_APP_SECRET);
			wxCircleHandler.setToCircle(true);
			wxCircleHandler.addToSocialSDK();
		}
		if (isSinaWeiboOk) {
			//loginService.getConfig().setSinaCallbackUrl("http://sns.whalecloud.com/sina2/callback");
			SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
			loginService.getConfig().setSsoHandler(sinaSsoHandler);
			shareService.getConfig().setSsoHandler(sinaSsoHandler);
		}

		platforms.put(Platform.KEY_WEIXIN, new Platform(isWeixinOk,
				Platform.KEY_WEIXIN));
		platforms.put(Platform.KEY_WEIXIN_CIRCLE, new Platform(isWeixinOk,
				Platform.KEY_WEIXIN_CIRCLE));
		platforms.put(Platform.KEY_QQ, new Platform(isQqAndQzoneOk,
				Platform.KEY_QQ));
		platforms.put(Platform.KEY_QZONE, new Platform(isQqAndQzoneOk,
				Platform.KEY_QZONE));
		platforms.put(Platform.KEY_SINA_WEIBO, new Platform(isSinaWeiboOk,
				Platform.KEY_SINA_WEIBO));
	}

	private boolean isOk(String... values) {
		if (values == null || values.length == 0)
			return false;
		boolean isOk = true;
		for (int i = 0; i < values.length; i++) {
			if (TextUtils.isEmpty(values[i])) {
				isOk = false;
				break;
			}
		}
		return isOk;
	}

	private class Platform {
		public static final String KEY_WEIXIN = "weixin";
		public static final String KEY_WEIXIN1 = KEY_WEIXIN;
		public static final String KEY_WEIXIN_CIRCLE = "weixin_circle";
		public static final String KEY_QQ = "qq";
		public static final String KEY_QZONE = "qzone";
		public static final String KEY_SINA_WEIBO = "sina_weibo";
		public boolean isOk = false;// 初始化工作是否ok
		public SHARE_MEDIA value;// um对应的平台值

		Platform(boolean isOk, String platformKey) {
				this.isOk = isOk;
				if (platformKey.equals(KEY_WEIXIN)) {
					this.value = SHARE_MEDIA.WEIXIN;
				}
				if (platformKey.equals(KEY_WEIXIN_CIRCLE)) {
					this.value = SHARE_MEDIA.WEIXIN_CIRCLE;
				}
				if (platformKey.equals(KEY_QQ)) {
					this.value = SHARE_MEDIA.QQ;
				}
			if (platformKey.equals(KEY_QZONE)) {
				this.value = SHARE_MEDIA.QZONE;
			}
			if (platformKey.equals(KEY_SINA_WEIBO)) {
				this.value = SHARE_MEDIA.SINA;
			}
		}
	}


	private Platform getPlatform(SHARE_MEDIA shareMedia) {
		Platform platform = null;
		switch (shareMedia) {
			case WEIXIN:
				platform = platforms.get(Platform.KEY_WEIXIN);
				break;

			case WEIXIN_CIRCLE:
				platform = platforms.get(Platform.KEY_WEIXIN_CIRCLE);
				break;

			case SINA:
				platform = platforms.get(Platform.KEY_SINA_WEIBO);
				break;

			case QQ:
				platform = platforms.get(Platform.KEY_QQ);
				break;

			case QZONE:
				platform = platforms.get(Platform.KEY_QZONE);
				break;
		}
		return platform;
	}


	public UMSocialService getLoginService() {
		return loginService;
	}

	public UMSocialService getShareService() {
		return shareService;
	}

	public void realse() {
		if (shareService != null)
			shareService = null;
		if (mInstance != null)
			mInstance = null;
	}

	public static interface OnShareCallBack {
		void onSuccess();

		void onFailure(String errMsg);
	}

	/**
	 * 分享至微信
	 * 
	 * @param title
	 * @param content
	 * @param imgUrl
	 * @param url
	 * @param cbk
	 */
	public void shareToWeixin(String title, String content, String imgUrl,
			String url, OnShareCallBack cbk) {
		if (cbk == null)
			return;
		share(Platform.KEY_WEIXIN, title, content, imgUrl, url, cbk);

	}

	/**
	 * 分享至微信朋友圈
	 * 
	 * @param title
	 * @param content
	 * @param imgUrl
	 * @param url
	 * @param cbk
	 */
	public void shareToWeixinCircle(String title, String content,
			String imgUrl, String url, OnShareCallBack cbk) {
		if (cbk == null)
			return;
		share(Platform.KEY_WEIXIN_CIRCLE, title, content, imgUrl, url, cbk);
	}

	/**
	 * 分享至新浪微博<br>
	 * 新浪微博分享需要activity作为context
	 * @param title
	 * @param content
	 * @param imgUrl
	 * @param url
	 * @param cbk
	 */
	public void shareToSinaWeibo(Context ctx,String title, String content, String imgUrl,
			String url, OnShareCallBack cbk) {
		if (cbk == null)
			return;
		share(ctx,Platform.KEY_SINA_WEIBO, title, content, imgUrl, url, cbk);
	}

	/**
	 * 分享至QQ
	 * 
	 * @param title
	 * @param content
	 * @param imgUrl
	 * @param url
	 * @param cbk
	 */
	public void shareToQq(Activity activity, String title, String content,
			String imgUrl, String url, OnShareCallBack cbk) {
		if (cbk == null)
			return;
		if (activity == null) {
			cbk.onFailure("acitivty is null");
		}
		if (platforms.get(Platform.KEY_QQ).isOk) {
			UMQQSsoHandler qqHandler = new UMQQSsoHandler(activity,
					ShareConfig.QQ_AND_QZONE_APP_ID,
					ShareConfig.QQ_AND_QZONE_APP_KEY);
			qqHandler.addToSocialSDK();
		}
		share(Platform.KEY_QQ, title, content, imgUrl, url, cbk);
	}

	/**
	 * 分享至QQ空间
	 * 
	 * @param title
	 * @param content
	 * @param imgUrl
	 * @param url
	 * @param cbk
	 */
	public void shareToQZone(Activity activity, String title, String content,
			String imgUrl, String url, OnShareCallBack cbk) {
		if (cbk == null)
			return;
		if (activity == null) {
			cbk.onFailure("acitivty is null");
		}
		if (platforms.get(Platform.KEY_QZONE).isOk) {
			QZoneSsoHandler qzoneHandler = new QZoneSsoHandler(activity,
					ShareConfig.QQ_AND_QZONE_APP_ID,
					ShareConfig.QQ_AND_QZONE_APP_KEY);
			qzoneHandler.addToSocialSDK();
		}
		share(Platform.KEY_QZONE, title, content, imgUrl, url, cbk);
	}

	/**
	 * 登录到新浪微博
	 * 
	 * @param context
	 *            不能为applicationContext
	 * @param cbk
	 */
	public void loginToSinaWeibo(Context context, OnLoginCallBack cbk) {
		if (cbk == null)
			return;
		Platform platform = platforms.get(Platform.KEY_SINA_WEIBO);
		login(context, platform, cbk);
	}

	/**
	 * 登录到微信
	 * 
	 * @param context
	 *            不能为applicationContext
	 * @param cbk
	 */
	public void loginToWeixin(Context context, OnLoginCallBack cbk) {
		if (cbk == null)
			return;
		Platform platform = platforms.get(Platform.KEY_WEIXIN);
		login(context, platform, cbk);
	}

	/**
	 * 登录到微信
	 *
	 * @param context
	 *            不能为applicationContext
	 * @param cbk
	 */
	public void loginToQQ(Activity context, OnLoginCallBack cbk) {
		if (cbk == null)
			return;

		if (platforms.get(Platform.KEY_QQ).isOk) {
			UMQQSsoHandler qqHandler = new UMQQSsoHandler(context,
					ShareConfig.QQ_AND_QZONE_APP_ID,
					ShareConfig.QQ_AND_QZONE_APP_KEY);
			qqHandler.addToSocialSDK();
		}
		Platform platform = platforms.get(Platform.KEY_QQ);
		login(context, platform, cbk);
	}

	/**
	 * 清除新浪微博登录授权信息
	 */
	public void logoutToSinaWeibo() {
		Platform platform = platforms.get(Platform.KEY_SINA_WEIBO);
		logout(platform);
	}

	/**
	 * 清除微信登录授权信息
	 */
	public void logoutToWeixin() {
		Platform platform = platforms.get(Platform.KEY_WEIXIN);
		logout(platform);
	}

	/**
	 * 创建分享内容
	 * 
	 * @param platform
	 * @param title
	 * @param content
	 * @param imgUrl
	 * @param url
	 * @return
	 */
	private BaseShareContent createBaseShareContent(final SHARE_MEDIA platform,
			String title, String content, String imgUrl, String url) {
		BaseShareContent shareContent = new BaseShareContent() {

			@Override
			public SHARE_MEDIA getTargetPlatform() {
				return platform;
			}
		};
		if (isOk(title))
			shareContent.setTitle(title);
		if (isOk(content))
			shareContent.setShareContent(content);
		if (isOk(imgUrl))
			shareContent.setShareImage(new UMImage(context, imgUrl));
		if (isOk(url))
			shareContent.setTargetUrl(url);
		return shareContent;
	}


	private void share(String platformKey, String title, String content,
					   String imgUrl, String url, OnShareCallBack cbk) {
		share(null,platformKey,title,content,imgUrl,url,cbk);
	}

	/**
	 * 分享
	 * 
	 * @param platformKey
	 * @param title
	 * @param content
	 * @param imgUrl
	 * @param url
	 * @param cbk
	 */
	private void share(Context ctx,String platformKey, String title, String content,
			String imgUrl, String url, OnShareCallBack cbk) {
		if (cbk == null)
			return;
		if (TextUtils.isEmpty(platformKey)) {
			cbk.onFailure("platformKey is null");
			return;
		}

		Platform platform = platforms.get(platformKey);
		if (platform == null) {
			cbk.onFailure("platform is not define");
			return;
		}
		BaseShareContent shareContent = createBaseShareContent(platform.value,
				title, content, imgUrl, url);
		if (shareContent == null)
			cbk.onFailure("platform is not define");
		share(ctx,platform, shareContent, cbk);
	}


	public void share(Context ctx,final SHARE_MEDIA shareMedia, String title, String content,
					   Bitmap bmp, String url, OnShareCallBack cbk) {
		if (cbk == null)
			return;


		BaseShareContent shareContent = new BaseShareContent() {

			@Override
			public SHARE_MEDIA getTargetPlatform() {
				return shareMedia;
			}
		};
		if (isOk(title))
			shareContent.setTitle(title);
		if (isOk(content))
			shareContent.setShareContent(content);
		if (bmp != null) {
			shareContent.setShareImage(new UMImage(context, bmp));
		}

		if (isOk(url))
			shareContent.setTargetUrl(url);

		if (shareContent == null)
			cbk.onFailure("platform is not define");
		share(ctx,getPlatform(shareMedia), shareContent, cbk);
	}




	/**
	 * 分享
	 * 
	 * @param platform
	 *            分享平台 
	 * @param content
	 *            分享内容
	 * @param cbk
	 *            分享回调
	 */
	private void share(Context ctx,Platform platform, UMediaObject content,
			final OnShareCallBack cbk) {
		if(ctx == null) {
			ctx = context;
		}
		if (cbk == null)
			return;
		if (platform == null) {
			cbk.onFailure("分享平台不能为空");
			return;
		}
		if (!platform.isOk) {
			cbk.onFailure("平台的key等信息不完整，请在ShareConfig中添加");
			return;
		}
		if (content == null) {
			cbk.onFailure("分享内容不能为空");
			return;
		}

		if (shareService == null) {
			cbk.onFailure("shareService is null");
			return;
		}
		shareService.setShareMedia(content);
		//分享到微博时，context必须为activity
		shareService.postShare(ctx, platform.value, new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int stCode,
								   SocializeEntity entity) {
				if (stCode == 200) {
					cbk.onSuccess();
				} else {
					cbk.onFailure("分享失败");
				}
			}
		});
	}

	public static interface OnLoginCallBack {
		/**
		 * @param bundle
		 *            Bundle[{access_token=
		 *            OezXcEiiBSKSxW0eoylIeAYaW5_zAU3AHJOL0FvTNtzQx7HU1ow52DgPmqXzqkH_EumQHmjfDFlj_D3BMjbTHh
		 *            -
		 * 
		 *            oWI95MpNwlId3gCVK5bsbgJKrmIvvETgAGqqHXKDkXZZ_qOpG_9bbOhF9V28XRQ
		 *            ,
		 * 
		 *            refresh_token=
		 *            OezXcEiiBSKSxW0eoylIeAYaW5_zAU3AHJOL0FvTNtzQx7HU1ow52DgPmqXzqkH_XEsFnejE6FL9rZa1UF3s
		 *            -
		 * 
		 *            MSUcYxzwATFyjFQjKjj1iBeug63ql3pZuinG-
		 *            XI5d3Cuaxz8GBcrd5h9cfd2Vn8sA,
		 *            openid=oxG4AwRlbEsrW7zpzfjfVBzBOd5w, expires_in=7200,
		 * 
		 *            refresh_token_expires=604800,
		 *            unionid=oJYVuwqwkFFHBXVsqD8sThKVFe84,
		 *            uid=oxG4AwRlbEsrW7zpzfjfVBzBOd5w,
		 * 
		 *            scope=snsapi_userinfo}]
		 * @param info
		 *            {sex=1, nickname=MONEY888,
		 *            unionid=oJYVuwqwkFFHBXVsqD8sThKVFe84, province=福建,
		 *            openid=oxG4AwRlbEsrW7zpzfjfVBzBOd5w,
		 * 
		 *            language=zh_CN,
		 * 
		 *            headimgurl=http://wx.qlogo.cn/mmopen/
		 *            Q3auHgzwzM7icWxwsgDmZBgerorscuXVoyVVxufFuyHcubQ4gg1E1t21cz1j0PqZjGc1L7fWXhHS4kUEvU0Dts
		 * 
		 *            w/0, country=中国, city=龙岩}
		 */
		void onSuccess(Bundle bundle, Map<String, Object> info);

		void onCancel();

		void onFailure(String errMsg);
	}

	private void login(Context context, final Platform platform,
			final OnLoginCallBack cbk) {
		if (cbk == null)
			return;
		if (platform == null) {
			cbk.onFailure("登录平台不能为空");
			return;
		}
		if (!platform.isOk) {
			cbk.onFailure("平台的key等信息不完整，请在ShareConfig中添加");
			return;
		}
		if (!platform.value.equals(SHARE_MEDIA.SINA)
				&& !platform.value.equals(SHARE_MEDIA.WEIXIN)
				&& !platform.value.equals(SHARE_MEDIA.QQ)) {
			cbk.onFailure("暂不支持除新浪微博、微信、QQ以外的平台登录");
			return;
		}
		if (loginService == null) {
			cbk.onFailure("loginService is null");
			return;
		}
		loginService.doOauthVerify(context, platform.value,
				new UMAuthListener() {

					@Override
					public void onStart(SHARE_MEDIA arg0) {

					}

					@Override
					public void onError(SocializeException exception,
							SHARE_MEDIA arg1) {
						String msg = "登录失败";
						if (exception != null && exception.getCause() != null)
							msg = exception.getCause().toString();
						cbk.onFailure(msg);
					}

					@Override
					public void onComplete(Bundle bundle, SHARE_MEDIA arg1) {

						getPlatformInfo(bundle, platform, cbk);
					}

					@Override
					public void onCancel(SHARE_MEDIA arg0) {
						Log.i("sina","sina login cancel");
						cbk.onCancel();
					}
				});

	}

	/**
	 * 获取accesstoken及用户资料
	 * 
	 * @param cbk
	 */
	private void getPlatformInfo(final Bundle bundle, Platform platform,
			final OnLoginCallBack cbk) {
		loginService.getPlatformInfo(context, platform.value,
				new UMDataListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onComplete(int status, Map<String, Object> info) {
						if (status == 200) {
							if (info == null || info.size() == 0) {
								cbk.onFailure("登录失败");
								return;
							}
							cbk.onSuccess(bundle, info);
						} else {
							cbk.onFailure("登录失败");
						}
					}
				});
	}

	/**
	 * 注销登录用于清空已经获取的accesstoken信息
	 * 
	 * @param platform
	 */
	private void logout(Platform platform) {
		if (platform == null)
			return;
		if (!platform.value.equals(SHARE_MEDIA.SINA)
				&& !platform.value.equals(SHARE_MEDIA.WEIXIN)
				&& !platform.value.equals(SHARE_MEDIA.QQ))
			return;
		if (loginService == null)
			return;
		loginService.deleteOauth(context, platform.value,
				new SocializeClientListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onComplete(int arg0, SocializeEntity arg1) {
						// TODO Auto-generated method stub

					}
				});
	}
}
