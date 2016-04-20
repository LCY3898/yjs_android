package com.cy.yigym.view.content.chaseher;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.utils.HeaderHelper;
import com.efit.sport.R;

public class ChaseHerHeadView extends BaseView {
	@BindView
	private CustomCircleImageView imgMyHead;
	@BindView
	private ImageView imgHeadBg;

	public ChaseHerHeadView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ChaseHerHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	/**设置头像背景
	 * @param resId
	 */
	public void setHeadBg(int resId) {
		imgHeadBg.setBackgroundResource(resId);
	}

	/**设置头像
	 * @param fid
	 */
	public void setHead(String fid) {
		HeaderHelper.load(fid, imgMyHead, 0);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.view_chase_her_head;
	}

}
