package com.cy.widgetlibrary.content;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.R;
import com.cy.widgetlibrary.WidgetUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2014-12-1上午11:57:29
 * </p>
 * <p>
 * 自定义头部控件
 * </p>
 */
public class CustomTitleView extends LinearLayout {
	private ImageButton btnLeft = null, btnRight = null, btnRight1 = null;
	private ImageView imgCenter = null;
	private TextView txtLeft, txtRight, txtCenter;
	private LinearLayout linLeft, linRight, linCenterContainer;
	private RelativeLayout rlRoot;

	private TextView txtMsgIndicator;

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2014-12-22上午11:19:27
	 * </p>
	 * <p>
	 * 控件添加的方向
	 * </p>
	 */
	public enum ViewAddDirection {
		left, right
	}

	/**
	 * 向左部区域添加控件
	 * 
	 * @param view
	 *            添加的控件
	 * @param direction
	 *            添加的方向,{@link ViewAddDirection#left}表示添加的总是在最左边,
	 *            {@link ViewAddDirection#right}表示添加的总是在最右边
	 * @param l
	 *            点击监听事件，若不设置，请置null
	 */
	public void addLeftView(View view, ViewAddDirection direction,
			OnClickListener l) {
		switch (direction) {
		case left:
			linLeft.addView(view, 0);
			break;
		case right:
			linLeft.addView(view);
			break;
		}
		if (l != null)
			view.setOnClickListener(l);
	}

	/**
	 * 向右部区域添加控件
	 * 
	 * @param view
	 *            添加的控件
	 * @param direction
	 *            添加的方向,{@link ViewAddDirection#left}表示添加的总是在最左边,
	 *            {@link ViewAddDirection#right}表示添加的总是在最右边
	 * @param l
	 *            点击监听事件，若不设置，请置null
	 */
	public void addRightView(View view, ViewAddDirection direction,
			OnClickListener l) {
		switch (direction) {
		case left:
			linRight.addView(view, 0);
			break;
		case right:
			linRight.addView(view);
			break;
		}
		if (l != null)
			view.setOnClickListener(l);
	}

	/**
	 * 设置左边的TextView的左边的图标
	 * 
	 * @param iconId
	 */
	public void setTxtLeftIcon(int iconId) {
		Drawable arrowLeft = getResources().getDrawable(iconId);
		arrowLeft.setBounds(0, 0, arrowLeft.getMinimumWidth(),
				arrowLeft.getMinimumHeight());
		getTxtLeft().setCompoundDrawables(arrowLeft, null, null, null);
	}

	/**
	 * 设置右边的TextView的右边的图标
	 * 
	 * @param iconId
	 */
	public void setTxtRightIcon(int iconId) {
		Drawable arrowLeft = getResources().getDrawable(iconId);
		arrowLeft.setBounds(0, 0, arrowLeft.getMinimumWidth(),
				arrowLeft.getMinimumHeight());
		getTxtRight().setText("");
		getTxtRight().setCompoundDrawables(null, null, arrowLeft, null);
	}

	/**
	 * 设置左边的TextView的文字
	 * 
	 * @param text
	 */
	public void setTxtLeftText(String text) {
		getTxtLeft().setText(text);
	}

	/**
	 * 设置右边的TextView的文字
	 * 
	 * @param text
	 */
	public void setTxtRightText(String text) {
		getTxtRight().setText(text);
	}

	/**
	 * 设置左边的TextView的监听事件
	 * 
	 * @param l
	 */
	public void setTxtLeftClickListener(OnClickListener l) {
		getTxtLeft().setOnClickListener(l);
	}

	/**
	 * 设置右边的TextView的监听事件
	 * 
	 * @param l
	 */
	public void setTxtRightClickListener(OnClickListener l) {
		getTxtRight().setOnClickListener(l);
	}

	/**
	 * 
	 * @return
	 */
	public void setTitleViewBackGroundColor(int color) {
		getRlRoot().setBackgroundColor(color);
	}

	public TextView getTxtLeft() {
		txtLeft.setVisibility(View.VISIBLE);
		return txtLeft;
	}

	public TextView getTxtRight() {
		txtRight.setVisibility(View.VISIBLE);
		return txtRight;
	}

	public TextView getTxtCenter() {
		txtCenter.setVisibility(View.VISIBLE);
		return txtCenter;
	}

	public ImageButton getBtnLeft() {
		btnLeft.setVisibility(View.VISIBLE);
		return btnLeft;
	}

	public ImageButton getBtnRight() {
		btnRight.setVisibility(View.VISIBLE);
		return btnRight;
	}

	public ImageButton getBtnRight1() {
		btnRight1.setVisibility(View.VISIBLE);
		return btnRight1;
	}

	public ImageView getImgCenter() {
		imgCenter.setVisibility(View.VISIBLE);
		return imgCenter;
	}

	public RelativeLayout getRlRoot() {
		return rlRoot;
	}

	public void setRlRoot(RelativeLayout rlRoot) {
		this.rlRoot = rlRoot;
	}

	public CustomTitleView(Context context) {
		super(context);
		initView(context);
	}

	public CustomTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_title, this, true);
		txtLeft = (TextView) findViewById(R.id.view_title_txtLeft);
		txtRight = (TextView) findViewById(R.id.view_title_txtRight);
		txtCenter = (TextView) findViewById(R.id.view_title_txtCenter);
		btnLeft = (ImageButton) findViewById(R.id.view_title_btnLeft);
		imgCenter = (ImageView) findViewById(R.id.view_title_imgCenter);
		btnRight = (ImageButton) findViewById(R.id.view_title_btnRight);
		btnRight1 = (ImageButton) findViewById(R.id.view_title_btnRight1);
		linLeft = (LinearLayout) findViewById(R.id.view_title_linLeft);
		linRight = (LinearLayout) findViewById(R.id.view_title_linRight);
		linCenterContainer = (LinearLayout) findViewById(R.id.linCenterContainer);
		rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
		txtMsgIndicator = (TextView) findViewById(R.id.view_title_msgIndicator);
	}

	/**
	 * 设置中间的控件
	 * 
	 * @param centerView
	 */
	public void setCenterView(View centerView, int marginLeft, int marginRight) {
		if (centerView == null)
			return;
		linCenterContainer.addView(centerView);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.LEFT_OF, linLeft.getId());
		lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		lp.setMargins(WidgetUtils.dpToPx(marginLeft), 0,
				WidgetUtils.dpToPx(marginRight), 0);
		linCenterContainer.setLayoutParams(lp);
	}

	/**
	 * 设置标题
	 * 
	 * @param titile
	 */
	public void setTitle(String titile) {
		getTxtCenter().setText(titile);
	}

	public void showMsgIndicator() {
		txtMsgIndicator.setVisibility(View.VISIBLE);
	}

	public void hideMsgIndicarot() {
		txtMsgIndicator.setVisibility(View.GONE);
	}

	public void setTxtMsgIndicatorTxt(String txt) {
		txtMsgIndicator.setText(txt);
	}

	public void setBackgroundColor(int color) {
		rlRoot.setBackgroundColor(color);
	}
}
