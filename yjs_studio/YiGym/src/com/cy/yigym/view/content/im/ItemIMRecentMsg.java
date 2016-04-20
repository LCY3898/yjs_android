package com.cy.yigym.view.content.im;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.view.content.emoji.EmojiTextView;
import com.efit.sport.R;

public class ItemIMRecentMsg extends BaseView {
	@BindView
	public ImageView imgAvatar;
	@BindView
	public TextView txtNickName, txtChatTime;
	@BindView
	public EmojiTextView txtMsg;

	public ItemIMRecentMsg(Context context) {
		super(context);
	}

	public ItemIMRecentMsg(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initView() {
		int dp8 = WidgetUtils.dpToPx(8);
		setPadding(dp8, dp8, dp8, dp8);
		setBackgroundColor(Color.WHITE);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.item_im_recent_msg;
	}

}
