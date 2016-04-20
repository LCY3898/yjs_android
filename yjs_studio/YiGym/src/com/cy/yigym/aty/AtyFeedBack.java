package com.cy.yigym.aty;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.content.DlgLoading;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqFeedBack;
import com.cy.yigym.net.rsp.RspFeedBack;
import com.efit.sport.R;

//意见反馈
public class AtyFeedBack extends BaseFragmentActivity {
	private final int MAX_LENGTH = 200;
	// 标题
	@BindView
	private CustomTitleView vTitle;
	// 反馈意见内容
	@BindView
	private EditText et_customer_view;
	// 联系方式
	@BindView
	private EditText et_contact;
	@BindView
	private TextView tvLengthDesc;
	private DlgLoading dlgLoading;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_feed_back;
	}

	@Override
	protected void initView() {
		vTitle.setTitle("意见反馈");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.header_back);
		vTitle.setTxtRightIcon(R.drawable.icon_finish);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		dlgLoading = new DlgLoading(mContext);
		vTitle.setTxtRightClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeKeyboard();
				dlgLoading.show("正在提交中,请稍候...");
				String personal_idea = et_customer_view.getText().toString()
						.trim();
				String contact = et_contact.getText().toString().trim();
				YJSNet.feedBack(new ReqFeedBack(personal_idea, contact),
						LOG_TAG, new YJSNet.OnRespondCallBack<RspFeedBack>() {
							@Override
							public void onSuccess(RspFeedBack data) {
								dlgLoading.dismiss();
								WidgetUtils.showToast("" + data.msg);
								finish();
							}

							@Override
							public void onFailure(String errorMsg) {
								dlgLoading.dismiss();
								WidgetUtils.showToast("" + errorMsg);
							}
						});

			}
		});
	}

	@Override
	protected void initData() {
		editTextListener();
	}

	private void editTextListener() {
		et_customer_view.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String content = s.toString();
				if (TextUtils.isEmpty(content))
					return;
				tvLengthDesc.setText(String.format("%d/%d", content.length(),
						MAX_LENGTH));
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}

}
