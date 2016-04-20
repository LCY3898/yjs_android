package com.cy.yigym.view.content.im;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.efit.sport.R;

public class ItemIMChatSentBase extends LinearLayout {
	public TextView txtTime;
	public CustomCircleImageView imgAvatar;
	public RelativeLayout rlContainer;
	public ImageView imgSentFail;
	public ProgressBar prgLoad;
	protected LayoutInflater layoutInflater;

	public ItemIMChatSentBase(Context context) {
		super(context);
		init(context);
	}

	public ItemIMChatSentBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		layoutInflater = LayoutInflater.from(getContext());
		LayoutInflater.from(getContext()).inflate(
				R.layout.item_im_chat_sent_base, this, true);
		txtTime = (TextView) findViewById(R.id.txtTime);
		imgAvatar = (CustomCircleImageView) findViewById(R.id.imgAvatar);
		rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
		imgSentFail = (ImageView) findViewById(R.id.imgSentFail);
		prgLoad = (ProgressBar) findViewById(R.id.prgLoad);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		setGravity(Gravity.CENTER_HORIZONTAL);
		setPadding(0, WidgetUtils.dpToPx(13), 0, 0);
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(lp);
	}

	/**
	 * 添加内容区控件
	 * 
	 * @param contentView
	 */
	public void setContentView(View contentView) {
		rlContainer.removeAllViews();
		rlContainer.addView(contentView);
	}

}
