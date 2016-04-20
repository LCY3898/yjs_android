package com.cy.yigym.aty;

import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.wbs.RspBase;
import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.content.DlgLoading;
import com.cy.yigym.fragment.FragmentServicPolicy;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.req.ReqAdd;
import com.cy.yigym.net.req.ReqRegis;
import com.cy.yigym.net.req.ReqRegisVerCode;
import com.cy.yigym.net.rsp.RspRegis;
import com.cy.yigym.net.rsp.RspRegisVerCode;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.RegularExpressionUtils;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-8
 * </p>
 * <p>
 * 注册界面
 * </p>
 */
public class AtyRegis extends BaseFragmentActivity implements OnClickListener {
	@BindView
	private Button btnVercode, btnRegis;
	@BindView
	private CustomTitleView vTitle;
	@BindView
	private EditText editPhone, editVerCode, editPsw;
	@BindView
	private ImageView btnDelete;
	@BindView
	private Button btn_toLogin;
	@BindView
	private CheckBox chbAgree;
	private UITimer verCodeTimer;
	private long getCodeTimeMills = 0;
	private final int COUNT_DOWN_SECONDS = 60;
	private final int CODE_VALIDITY_PERIOD = 30 * 60;
	private int countDown = COUNT_DOWN_SECONDS;
	private StateListDrawable bgCode;
	private boolean isGettingCode = false;
	private DlgLoading dlgLoading;
	@BindView
	private TextView tvTermOfService;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_regis;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView() {
		verCodeTimer = new UITimer();
		bgCode = (StateListDrawable) getResources().getDrawable(
				R.drawable.selector_btn_blue);
		btnVercode.setOnClickListener(this);
		btnRegis.setOnClickListener(this);
		vTitle.setTitle("注册");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
		vTitle.setTxtLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		editPhone.addTextChangedListener(textWatcher);
		btnDelete.setOnClickListener(this);
		btn_toLogin.setOnClickListener(this);
		tvTermOfService.setOnClickListener(this);

		// editPhone.setBackground(ColorTheme.getEditorDrawable());
		// editPsw.setBackground(ColorTheme.getEditorDrawable());
		// editVerCode.setBackground(ColorTheme.getEditorDrawable());
		// btn_toLogin.setBackgroundDrawable(ColorTheme.getRegisBtnSelector());
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
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
	protected void initData() {
		dlgLoading = new DlgLoading(mContext);
	}

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
				dlgLoading.show("正在注册中，请稍候...");
				YJSNet.regis(new ReqRegis(phone, psw, code), LOG_TAG,
						new OnRespondCallBack<RspRegis>() {

							@Override
							public void onSuccess(RspRegis data) {
								dlgLoading.dismiss();
								WidgetUtils.showToast("注册成功，请完善个人信息");
								DataStorageUtils.setPid(data.data.pid);
								startActivity(AtyPersonalInfo.class);
								// Add();
								finish();
							}

							@Override
							public void onFailure(String errorMsg) {
								dlgLoading.dismiss();
								WidgetUtils.showToast("注册失败," + errorMsg);
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
			YJSNet.getRegisVerCode(new ReqRegisVerCode(phone, "0"), LOG_TAG,
					new OnRespondCallBack<RspRegisVerCode>() {
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
		case R.id.btn_toLogin:
			finish();
			break;
		case R.id.tvTermOfService:
			closeKeyboard();
			addFragment(new FragmentServicPolicy(),
					FragmentServicPolicy.class.getSimpleName(), true);
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

	private boolean isPwdLegal(String pwd) {
		char[] ch = pwd.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] > 0x7f) {
				return false;
			}
		}
		return true;
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

		if (!isPwdLegal(psw)) {
			WidgetUtils.showToast("密码不能含有中文或表情符号");
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
		if (!chbAgree.isChecked()) {
			WidgetUtils.showToast("您还没有同意《E健身协议》");
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
		verCodeTimer.cancel();
	}

	/**
	 * 将自己添加进排行榜
	 */
	private void Add() {
		YJSNet.send(new ReqAdd(), LOG_TAG, new OnRespondCallBack<RspBase>() {
			@Override
			public void onSuccess(RspBase data) {

			}

			@Override
			public void onFailure(String errorMsg) {
				WidgetUtils.showToast("新用户添加排行榜失败" + errorMsg);
			}
		});
	}

	@Override
	public int getContentAreaId() {
		return R.id.flFragmentContainer;
	}
}
