package com.cy.yigym.aty;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.fragment.FragmentServicPolicy;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetAboutYjs;
import com.cy.yigym.net.rsp.RspGetAboutYjs;
import com.cy.yigym.utils.PackageUtils;
import com.efit.sport.R;
import com.hhtech.umeng.update.UmengUpdateUtils;
import com.hhtech.umeng.update.UmengUpdateUtils.OnUpdateCallBack;

/**
 * Created by ejianshen on 15/7/22.
 */
public class AtyAboutEjs extends BaseFragmentActivity implements
		View.OnClickListener {
	// 标题
	@BindView
	private CustomTitleView vTitle;
	@BindView
	private TextView tv_version;
	@BindView
	private TextView tv_mail;
	@BindView
	private TextView tv_phone;
	@BindView
	private RelativeLayout rl_term_of_server;
	@BindView
	private RelativeLayout rl_update_version;
	@BindView
	private RelativeLayout rlUpdateTag;

	@BindView
	private TextView tvVersionCode;
	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_about_ejs;
	}


	@Override
	public int getContentAreaId() {
		return R.id.container;
	}

	@Override
	protected void initView() {
		vTitle.setTitle("关于e健身");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		rl_term_of_server.setOnClickListener(this);
		rl_update_version.setOnClickListener(this);

		tvVersionCode.setText(PackageUtils.getVersionName());
	}

	@Override
	protected void initData() {

		YJSNet.aboutYjs(new ReqGetAboutYjs(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGetAboutYjs>() {
					@Override
					public void onSuccess(RspGetAboutYjs data) {
						tv_version.setText("e健身" + data.data.version);
						tv_mail.setText(data.data.email);
						tv_phone.setText(data.data.phone);
					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_term_of_server:
			addFragment(new FragmentServicPolicy(),FragmentServicPolicy.class.getSimpleName(),true);
			break;
		case R.id.rl_update_version:
			UmengUpdateUtils.checkUpdate(this, new OnUpdateCallBack() {

				@Override
				public void onUpdate(boolean hasUpdate) {
					/*rlUpdateTag.setVisibility(hasUpdate ? View.VISIBLE
							: View.GONE);*/
				}
			});
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}
}
