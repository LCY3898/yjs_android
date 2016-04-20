package com.cy.yigym.aty;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.yigym.fragment.FragmentFirstGuide;
import com.efit.sport.R;

public class AtyFirstGuide extends BaseFragmentActivity {

	@Override
	protected boolean isBindViewByAnnotation() {
		return false;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_common;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		addFragment(new FragmentFirstGuide());
	}

	@Override
	public int getContentAreaId() {
		return R.id.frmContent;
	}

}
