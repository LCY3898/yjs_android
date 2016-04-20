package com.cy.yigym.aty;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.YJSNet.TargetType;
import com.cy.yigym.net.rsp.RspAddTarget;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-13
 * </p>
 * <p>
 * 服务端接口调试
 * </p>
 */
public class AtyServerTest extends BaseFragmentActivity implements
		OnClickListener {
	@BindView
	private Button btnTest;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_server_test;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		btnTest.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		YJSNet.addTarget(TargetType.TIME, "12", LOG_TAG,
				new OnRespondCallBack<RspAddTarget>() {

					@Override
					public void onSuccess(RspAddTarget data) {
						WidgetUtils.showToast("成功");

					}

					@Override
					public void onFailure(String errorMsg) {
						WidgetUtils.showToast("失败" + errorMsg);
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}
}
