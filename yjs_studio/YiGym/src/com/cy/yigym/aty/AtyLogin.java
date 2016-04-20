package com.cy.yigym.aty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.share.ShareConfig;
import com.cy.share.ShareHelper;
import com.cy.share.ShareHelper.OnLoginCallBack;
import com.cy.wbs.WSHelper;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.DlgLoading;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.req.ReqAuthorize;
import com.cy.yigym.net.req.ReqAuthorize.ClientInfo;
import com.cy.yigym.net.req.ReqLogin;
import com.cy.yigym.net.req.ReqQQAuthorize;
import com.cy.yigym.net.rsp.RspAuthorize;
import com.cy.yigym.net.rsp.RspLogin;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.RegularExpressionUtils;
import com.efit.sport.R;
import com.efit.sport.notify.NotifyEventHandler;
import com.sport.efit.constant.LoginChannel;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.Map;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-8
 * </p>
 * <p>
 * 登录界面
 * </p>
 */
public class AtyLogin extends BaseFragmentActivity implements OnClickListener {
	@BindView
	private Button btnLogin, btnRegis;
	@BindView
	private EditText editPhone, editPsw;
	@BindView
	private TextView txtForgetPsw;

	@BindView
	private ImageView imgSina, imgWeixin, imgQQ;

	private DlgLoading dlgLoading;

	@BindView
	private View contentView;

	@BindView
	private View linWeixin;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_login;
	}

	@Override
	protected void initView() {
		btnRegis.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		txtForgetPsw.setOnClickListener(this);


		//骆总要求 新版本不支持微信和微博登录
		linWeixin.setVisibility(View.GONE);
		imgSina.setOnClickListener(this);
		imgWeixin.setOnClickListener(this);
		imgQQ.setOnClickListener(this);


		contentView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (contentView.getWidth() > 0) {
							contentView.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
							DataStorageUtils.setContentViewHeight(contentView
									.getHeight());
							Log.i("screenHeight",
									"height:" + contentView.getHeight());
						}
					}
				});

		WSHelper.getInstance().initConnect();
	}

	public static void relogin(Activity aty) {
		Intent intent = new Intent(aty,AtyLogin.class);
		intent.putExtra("force_out",true);
		aty.startActivity(intent);
	}

	@Override
	protected void initData() {
		boolean forceOut = false;
		Intent it = getIntent();
		if(it != null) {
			forceOut = it.getBooleanExtra("force_out",false);
		}
		dlgLoading = new DlgLoading(mContext);
		WSHelper.getInstance().setNotifyListener(new NotifyEventHandler());
		String phone = CurrentUser.instance().getUserName();
		String psw = CurrentUser.instance().getPasswd();
		editPhone.setText(phone);
		editPsw.setText(psw);
		if(!forceOut) {
			autoLoginIfPossible(phone, psw);
		}
	}


	private void autoLoginIfPossible(String phone,String psw) {
		if (CurrentUser.instance().isVerified()) {
			LoginChannel channel = DataStorageUtils.getLoginChannel();

			//骆总要求 新版本不支持微信和微博登录
			if(channel != LoginChannel.CHANNEL_NORMAL) {
				return;
			}

			switch (channel) {
				case CHANNEL_NORMAL: {
					if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(psw)) {
						return;
					}
					login(phone, psw);
				}
				break;
				case CHANNEL_WEIXIN:
					weixinLogin();
					break;
				case CHANNEL_QQ:
					qqLogin();
					break;
				case CHANNEL_WEIBO:
					sinaLogin();
					break;
			}

		}
	}

	private void login(String phone, String psw) {
		dlgLoading.show("正在登录中，请稍候...");
		YJSNet.login(new ReqLogin(psw, phone), LOG_TAG,
				new OnRespondCallBack<RspLogin>() {

					@Override
					public void onSuccess(RspLogin data) {
						dlgLoading.dismiss();
						WidgetUtils.showToast("登录成功");
						DataStorageUtils
								.setLoginChannel(LoginChannel.CHANNEL_NORMAL);
						DataStorageUtils.setPid(data.data.user_info._id);
						DataStorageUtils
								.setCurUserProfileFid(data.data.user_info.profile_fid);
						if (!TextUtils.isEmpty(data.data.user_info.nick_name)
								&& data.isAccomplish()) {
							DataStorageUtils
									.setUserNickName(data.data.user_info.nick_name);
							startActivity(AtyMain2.class);
							DataStorageUtils.setUserInfoComplete(true);
						} else {
							DataStorageUtils.setUserInfoComplete(false);
							startActivity(AtyPersonalInfo.class);
						}

						onLoginSuccess();
					}

					@Override
					public void onFailure(String errorMsg) {
						dlgLoading.dismiss();
						WidgetUtils.showToast("登录失败," + errorMsg);
					}
				});

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnRegis:
			startActivity(AtyRegis.class);
			break;
		case R.id.btnLogin:
			closeKeyboard();
			String phone = editPhone.getText().toString();
			String psw = editPsw.getText().toString();
			if (isLegal(phone, psw)) {
				login(phone, psw);
			}
			break;
		case R.id.txtForgetPsw:
			startActivity(AtyGetBackPwd.class);
			break;
		case R.id.imgSina:
			sinaLogin();
			break;
		case R.id.imgWeixin:
			weixinLogin();
			break;

		case R.id.imgQQ:
			qqLogin();
			break;

		}
	}

	/**
	 * 新浪微博登录
	 */
	private void sinaLogin() {
		dlgLoading.show("正在登录新浪微博,请稍候...");
		ShareHelper.getInstance().loginToSinaWeibo(mActivity,
                new OnLoginCallBack() {

                    @Override
                    public void onSuccess(Bundle bundle,
                                          Map<String, Object> info) {
                        dlgLoading.dismiss();
                        authorize(bundle, info,
                                LoginChannel.CHANNEL_WEIBO.getChannelName());
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        dlgLoading.dismiss();
                        WidgetUtils.showToast("登录失败");
                    }

                    @Override
                    public void onCancel() {
                        dlgLoading.dismiss();

                    }
                });
	}

	/**
	 * 微信登录
	 */
	private void weixinLogin() {
		dlgLoading.show("正在登录微信,请稍候...");
		ShareHelper.getInstance().loginToWeixin(mActivity,
                new OnLoginCallBack() {

                    @Override
                    public void onSuccess(Bundle bundle,
                                          Map<String, Object> info) {
                        dlgLoading.dismiss();
                        authorize(bundle, info,
                                LoginChannel.CHANNEL_WEIXIN.getChannelName());
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        dlgLoading.dismiss();
                        WidgetUtils.showToast("登录失败");
                    }

                    @Override
                    public void onCancel() {
                        dlgLoading.dismiss();
                    }
                });
	}

	/**
	 * 微信登录
	 */
	private void qqLogin() {
		dlgLoading.show("正在登录qq,请稍候...");
		ShareHelper.getInstance().loginToQQ(mActivity, new OnLoginCallBack() {

            @Override
            public void onSuccess(Bundle bundle, Map<String, Object> info) {
                dlgLoading.dismiss();
                authorize(bundle, info,
                        LoginChannel.CHANNEL_QQ.getChannelName());
            }

            @Override
            public void onFailure(String errMsg) {
                dlgLoading.dismiss();
                WidgetUtils.showToast("登录失败");
            }

            @Override
            public void onCancel() {
                dlgLoading.dismiss();
            }
        });
	}

	/**
	 * 第三方登录<br>
	 * 使用uid,accessToken进行第三方登录
	 * 
	 * @param
	 * @param
	 */
	public void authorize(Bundle bundle, Map<String, Object> info,
			final String channelName) {
		if (bundle == null || info == null || info.size() == 0) {
			WidgetUtils.showToast("获取用户信息失败!");
			return;
		}
		String accessToken = bundle.getString("access_token");
		String uid = bundle.getString("uid");
		Log.d(LOG_TAG, "access_token===" + accessToken + "uid=====" + uid);
		if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(uid)) {
			WidgetUtils.showToast("获取用户信息失败");
			return;
		}
		final ReqAuthorize req;
		if (LoginChannel.CHANNEL_QQ.getChannelName().equals(channelName)) {
			req = new ReqQQAuthorize(uid, accessToken, channelName,
					ShareConfig.QQ_AND_QZONE_APP_ID, new ClientInfo(""));
		} else {
			req = new ReqAuthorize(uid, accessToken, channelName,
					new ClientInfo(""));
		}
		YJSNet.authorize(req, LOG_TAG, new OnRespondCallBack<RspAuthorize>() {

            @Override
            public void onSuccess(RspAuthorize data) {
				//保存网易账号
				DataStorageUtils.setNetEaseAccount(data.data.user_info.neteaseIMAcc);
                DataStorageUtils.setVoipObj(data.data.user_info.subAccount);
                DataStorageUtils.setSession(data.sess);
                DataStorageUtils.setPid(data.data.user_info._id);
                DataStorageUtils
                        .setCurUserProfileFid(data.data.user_info.profile_fid);
                DataStorageUtils.setUserNickName(data.data.user_info.nick_name);
                gotoCompletePersonalInfo(data);
                DataStorageUtils
                        .setDownloadUrl(data.data.server_info.download_path);
                DataStorageUtils.setUploadUrl(data.data.server_info.upload_to);
                DataStorageUtils
                        .setMp4LinkPrefix(data.data.server_info.mp4_link_prefix);

                CurrentUser.instance().setVerified(true);
                DataStorageUtils.setLoginChannel(LoginChannel
                        .fromName(channelName));
                onLoginSuccess();
            }

            @Override
            public void onFailure(String errorMsg) {
                WidgetUtils.showToast("错误＝＝" + errorMsg);
            }
        });
	}

	/**
	 * 去完善个人信息
	 */
	public void gotoCompletePersonalInfo(RspAuthorize data) {
		RspAuthorize.Data userinfo = data.data;
		if (data.isAccomplish()) {
			startActivity(AtyMain2.class);
			DataStorageUtils.setUserInfoComplete(true);
		} else {
			DataStorageUtils.setUserInfoComplete(false);
			Intent intent = new Intent(this, AtyPersonalInfo.class);
			intent.putExtra("authorize", 1);
			intent.putExtra("nickname", userinfo.user_info.nick_name);
			intent.putExtra("sex", userinfo.user_info.sex);
			startActivity(intent);
		}

	}

	public boolean isLegal(String phone, String psw) {
		if (TextUtils.isEmpty(phone)) {
			WidgetUtils.showToast("请输入手机号码");
			return false;
		}
		if (!RegularExpressionUtils.isMatcher(RegularExpressionUtils.PHONE,
				phone)) {
			WidgetUtils.showToast("请输入正确的手机号码");
			return false;
		}
		if (TextUtils.isEmpty(psw)) {
			WidgetUtils.showToast("请输入密码");
			return false;
		}
		if (psw.length() < 6) {
			WidgetUtils.showToast("密码至少为6位，请重新输入");
			return false;
		}
		if (psw.length() > 30) {
			WidgetUtils.showToast("密码最多为30位，请重新输入");
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}

	private void onLoginSuccess() {
		finish();
		DataStorageUtils.setLogin(true);
	}

	// 新浪微博会执行onActivityResult，需要在此处回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 根据requestCode获取对应的SsoHandler
		UMSsoHandler ssoHandler = ShareHelper.getInstance().getLoginService()
				.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
