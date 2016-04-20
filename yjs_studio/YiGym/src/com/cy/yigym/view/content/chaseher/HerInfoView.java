package com.cy.yigym.view.content.chaseher;

import java.text.DecimalFormat;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-9-26
 * </p>
 * <p>
 * 追她-ta的信息
 * </p>
 */
public class HerInfoView extends BaseView {
	@BindView
	public RelativeLayout rlBg;
	@BindView
	public ImageView imgHead, imgSex;
	@BindView
	public TextView txtUserName, txtDistance;
	private DecimalFormat df = new DecimalFormat("######0.00");

	public HerInfoView(Context context) {
		super(context);
	}

	@Override
	protected void initView() {
		int size = WidgetUtils.getScreenWidth() - WidgetUtils.dpToPx(80);
		RelativeLayout.LayoutParams lp = (LayoutParams) imgHead
				.getLayoutParams();
		lp.width = size;
		lp.height = size;
		imgHead.setLayoutParams(lp);
		RelativeLayout.LayoutParams lp2 = (LayoutParams) rlBg.getLayoutParams();
		lp2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		rlBg.setLayoutParams(lp2);
	}

	public void setNickName(String nickName) {
		txtUserName.setText(nickName);
	}

	public void setDistance(String dis) {
		String distance = "0";
		// 如果相聚距离大于1000则显示km否则显示m
		try {
			int m = (int) Double.parseDouble(dis);
			if (m < 1000) {
				distance = m + "m";
			} else {
				distance = df.format(m / 1000.0) + "km";
			}
		} catch (Exception e) {
			e.printStackTrace();
			distance = "0m";
		}
		txtDistance.setText(distance);

	}

	public void setSex(String sex) {
		imgSex.setBackgroundResource(sex.equals("男") ? R.drawable.bg_male
				: R.drawable.bg_female);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.view_her_info;
	}

}
