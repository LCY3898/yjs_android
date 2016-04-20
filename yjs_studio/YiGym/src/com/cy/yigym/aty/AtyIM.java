package com.cy.yigym.aty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.yigym.fragment.FragmentIM;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-10
 * </p>
 * <p>
 * 聊天界面
 * </p>
 */
public class AtyIM extends BaseFragmentActivity {

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_common;
	}

	@Override
	protected void initView() {

	}

	@Override
	public int getContentAreaId() {
		return R.id.frmContent;
	}

	@Override
	protected void initData() {
		if (getIntent() != null && getIntent().getExtras() != null)
			addFragment(new FragmentIM(), getIntent().getExtras(), false);

	}

	/**
	 * 发起聊天请求
	 * 
	 * @param context
	 * @param receiverId
	 */
	public static void luanchIM(Context context, String receiverId,
			String receiverNickName) {
		if (TextUtils.isEmpty(receiverId))
			return;
		Intent intent = new Intent(context, AtyIM.class);
		Bundle bundle = new Bundle();
		bundle.putString(FragmentIM.INTENT_KEY_RECEIVER_PID, receiverId);
		bundle.putString(FragmentIM.INTENT_KEY_RECEIVER_NICK_NAME,
				receiverNickName);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

}
