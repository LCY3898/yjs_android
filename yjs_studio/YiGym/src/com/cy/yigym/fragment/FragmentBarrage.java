package com.cy.yigym.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.view.content.barrage.BarrageView;
import com.cy.yigym.view.content.barrage.BarrageView.BarrageMsg;
import com.cy.yigym.view.content.im.IMSendBoard;
import com.cy.yigym.view.content.im.IMSendBoard.OnKeyBoardOpCallBack;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-4
 * </p>
 * <p>
 * 弹幕
 * </p>
 */
public class FragmentBarrage extends BaseFragment {
	@BindView
	private BarrageView vBarrage;
	@BindView
	private IMSendBoard vSendBoard;
	@BindView
	private CustomTitleView vTitle;

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_barrage;
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		vTitle.setTitle("弹幕");
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		vSendBoard.setOnSendClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String msg = vSendBoard.getText();
				if (TextUtils.isEmpty(msg))
					return;
				vBarrage.putMsg(new BarrageMsg(
						BarrageMsg.BarrageMsgType.NEW_MSG_NOTIFY,
						"hcy",
						"http://c.hiphotos.baidu.com/image/h%3D200/sign=52127e1b553d269731d30f5d65f9b24f/0dd7912397dda1445fca2f63b6b7d0a20df48624.jpg",
						msg));
			}
		});
		vSendBoard.setOnKeyBoardOpCallBack(new OnKeyBoardOpCallBack() {

			@Override
			public void onShowKeyBoard() {
				mActivity.showKeyboard();
			}

			@Override
			public void onCloseKeyBoard() {
				mActivity.closeKeyboard();
			}
		});
	}

}
