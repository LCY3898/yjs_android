package com.cy.yigym.aty;

import android.view.View;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.efit.sport.R;

public class AtyPreCourse extends BaseFragmentActivity {
	CustomTitleView vTitle;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_pre_course;
	}

	@Override
	protected void initView() {
		vTitle = (CustomTitleView) findViewById(R.id.vTitle);
		vTitle.setTitle("往期课程");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	protected void initData() {

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
