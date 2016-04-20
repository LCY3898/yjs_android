package com.cy.yigym.view.content.im;

import com.cy.yigym.view.content.emoji.EmojiTextView;
import com.efit.sport.R;

import android.content.Context;
import android.util.AttributeSet;

public class ItemIMChatReceivedText extends ItemIMChatReceivedBase {
	public EmojiTextView txtMsg;

	public ItemIMChatReceivedText(Context context) {
		super(context);
		init(context);
	}

	public ItemIMChatReceivedText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		txtMsg = (EmojiTextView) layoutInflater.inflate(R.layout.item_im_chat_received_text,
				null);
		setContentView(txtMsg);

	}
}
