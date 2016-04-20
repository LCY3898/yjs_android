package com.cy.yigym.aty;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.yigym.fragment.FragmentBarrage;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-4
 * </p>
 * <p>
 * 弹幕界面
 * </p>
 */
public class AtyBarrage extends BaseFragmentActivity {

	@Override
	protected boolean isBindViewByAnnotation() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_barrage;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		addFragment(new FragmentBarrage());
	}

	@Override
	public int getContentAreaId() {
		return R.id.linContent;
	}

}
