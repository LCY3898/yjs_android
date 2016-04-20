package com.cy.yigym.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cy.widgetlibrary.base.ActivityManager;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.AppMsg;
import com.cy.yigym.aty.AtyMain2;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.zbar.lib.CaptureCodeActivity;
import com.zbar.lib.CaptureConstant;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-4
 * </p>
 * <p>
 * 主界面内容区
 * </p>
 */
public class FragmentMainContent extends BaseFragment {
	@BindView
	private CustomTitleView vTitle;

	private final static int REQ_CODE_QRCODE = 1000;

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_main_content;
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		vTitle.setTxtRightIcon(R.drawable.qrcode_scan);
		vTitle.setTitle("e健身");
		vTitle.setTxtRightClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						CaptureCodeActivity.class);
				startActivityForResult(intent, REQ_CODE_QRCODE);
			}
		});

		vTitle.setTxtLeftIcon(R.drawable.navi_icon);
		vTitle.setTxtLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AtyMain2 aty = (AtyMain2) getActivity();
				aty.showDrawer();
			}
		});

		//结束当前activity之前的所有activity
		ActivityManager.getInstance().finishAtyBeforeThis();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {

	}

	@Override
	public void onResume() {
		super.onResume();
		processMsgIndicator();
	}

	private void processMsgIndicator() {
		if (DataStorageUtils.isShowMsgIndicator()) {
			msgIndicatorShow();
		} else {
			msgIndicatorHide();
		}
	}

	private void msgIndicatorShow() {
		vTitle.showMsgIndicator();
	}

	private void msgIndicatorHide() {
		vTitle.hideMsgIndicarot();
	}




	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_CODE_QRCODE) {
			if (resultCode == Activity.RESULT_OK) {
				String qrcodeStr = data
						.getStringExtra(CaptureConstant.QRCODE_RESULT);
				boolean isFamilyUser = data.getBooleanExtra(CaptureConstant.QRCODE_IS_FAMILY_USER,true);
				DataStorageUtils.setBleAddress(qrcodeStr);
				AppMsg.makeText(getActivity(), "扫描成功，可以开始骑行啦" , AppMsg.STYLE_CONFIRM).show();
			}
		}
	}

}
