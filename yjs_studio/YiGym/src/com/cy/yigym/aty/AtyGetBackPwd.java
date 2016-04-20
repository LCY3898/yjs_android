package com.cy.yigym.aty;

import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.content.DlgLoading;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqRegisVerCode;
import com.cy.yigym.net.req.ReqResetPassword;
import com.cy.yigym.net.rsp.RspRegisVerCode;
import com.cy.yigym.net.rsp.RspResetPassword;
import com.cy.yigym.utils.RegularExpressionUtils;
import com.efit.sport.R;

//重置密码
public class AtyGetBackPwd extends BaseFragmentActivity implements
		View.OnClickListener {

	// 手机号
	@BindView
	private EditText editPhone;
	@BindView
	private ImageView btnDelete;
	// 验证码
	@BindView
	private EditText editVerCode;
	// 新密码
	@BindView
	private EditText editPsw;
	// 获取验证码按钮
	@BindView
	private Button btnVercode;
	// 确认重新设置密码按钮
	@BindView
	private Button btnRegis;
	// 标题
	@BindView
	private CustomTitleView vTitle;
	private DlgLoading dlgLoading;
	private UITimer verCodeTimer;
	private long getCodeTimeMills = 0;
	private final int COUNT_DOWN_SECONDS = 60;
	private final int CODE_VALIDITY_PERIOD = 30 * 60;
	private int countDown = COUNT_DOWN_SECONDS;
	private StateListDrawable bgCode;
	private boolean isGettingCode = false;
	@BindView
	private LinearLayout linAgreen;
	@BindView
	private Button btn_toLogin;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_regis;
	}

	@Override
	protected void initView() {
		verCodeTimer=new UITimer();
		vTitle.setTitle("重置密码");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		editPhone.addTextChangedListener(textWatcher);
		btnRegis.setText("确定");
		linAgreen.setVisibility(View.GONE);
		btn_toLogin.setVisibility(View.GONE);
	}

	@Override
	protected void initData() {
		btnVercode.setOnClickListener(this);
		btnRegis.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		dlgLoading = new DlgLoading(mContext);
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			editPhone.setSelected(false);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {
			String phone = arg0.toString();
			btnDelete.setVisibility(TextUtils.isEmpty(phone) ? View.GONE
					: View.VISIBLE);
		}
	};

	@Override
	public void onClick(View v) {
		String phone = editPhone.getText().toString();
		String code = editVerCode.getText().toString();
		String psw = editPsw.getText().toString();
		switch (v.getId()) {
		case R.id.btnDelete:
			editPhone.getText().clear();
			break;
		case R.id.btnRegis:
			closeKeyboard();
			if (isLegal(phone, code, psw)) {
				dlgLoading.show("正在重置密码，请稍候...");
				YJSNet.send(new ReqResetPassword(phone, code, psw), LOG_TAG,
						new YJSNet.OnRespondCallBack<RspResetPassword>() {

							@Override
							public void onSuccess(RspResetPassword data) {
								dlgLoading.dismiss();
								WidgetUtils.showToast("成功，请登录");
								finish();
							}

							@Override
							public void onFailure(String errorMsg) {
								dlgLoading.dismiss();
								WidgetUtils.showToast("重置密码失败," + errorMsg);
							}
						});
			}
			break;
		case R.id.btnVercode:
			if (!RegularExpressionUtils.isMatcher(RegularExpressionUtils.PHONE,
					phone)) {
				editPhone.setSelected(true);
				WidgetUtils.showToast("请输入正确的手机号码");
				return;
			}
			editPhone.setSelected(false);
			YJSNet.send(new ReqRegisVerCode(phone, "1"), LOG_TAG,
					new YJSNet.OnRespondCallBack<RspRegisVerCode>() {

						@Override
						public void onSuccess(RspRegisVerCode data) {
							changeVerCodeBtnStatus();
						}

						@Override
						public void onFailure(String errorMsg) {
							WidgetUtils.showToast(String.format(
									"获取验证码失败，%s，请重试!", errorMsg));
						}
					});
			break;

		}
	}

	private void changeVerCodeBtnStatus() {
		getCodeTimeMills = System.currentTimeMillis();
		verCodeTimer = new UITimer();
		verCodeTimer.scheduleTimes(new Runnable() {

			@Override
			public void run() {
				btnVercode.setText(String.format("重新获取(%s)", countDown));
				countDown--;
				if (countDown == 0) {
					btnVercode.setText("重新获取");
					countDown = COUNT_DOWN_SECONDS;
					btnVercode.setBackgroundDrawable(bgCode);
					btnVercode.setEnabled(true);
					isGettingCode = false;
				} else {
					btnVercode.setBackgroundColor(Color.GRAY);
					btnVercode.setEnabled(false);
					isGettingCode = true;
				}

			}
		}, 1000, COUNT_DOWN_SECONDS);
	}

	private boolean isCodeValid() {
		long t = System.currentTimeMillis() - getCodeTimeMills;
		t = (long) (t / 1000.0);
		return t > CODE_VALIDITY_PERIOD ? false : true;
	}

	private boolean isLegal(String phone, String code, String psw) {
		editPhone.setSelected(true);
		if (!RegularExpressionUtils.isMatcher(RegularExpressionUtils.PHONE,
				phone)) {
			WidgetUtils.showToast("请输入正确的手机号码");
			return false;
		}
		editPhone.setSelected(false);
		editVerCode.setSelected(true);
		if (TextUtils.isEmpty(code)) {
			WidgetUtils.showToast("请输入验证码");
			return false;
		}
		if (!isCodeValid()) {
			WidgetUtils.showToast("验证码无效，请重新获取");
			return false;
		}
		editVerCode.setSelected(false);
		editPsw.setSelected(true);
		// if (isGettingCode) {
		// WidgetUtils.showToast("验证码正在获取中，请稍后重新填入");
		// return false;
		// }
		if (TextUtils.isEmpty(psw)) {
			WidgetUtils.showToast("请输入密码");
			return false;
		}
		if (psw.length() < 6) {
			WidgetUtils.showToast("密码不足6位，请重新输入");
			return false;
		}
		if (psw.length() > 30) {
			WidgetUtils.showToast("密码超出30位，请重新输入");
			return false;
		}
		editPsw.setSelected(false);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		verCodeTimer.cancel();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}

}
