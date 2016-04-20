package com.cy.yigym.view.content.im;

import android.content.Context;
import android.util.AttributeSet;

import com.cy.yigym.view.content.emoji.EmojiTextView;
import com.efit.sport.R;

public class ItemIMChatSentText extends ItemIMChatSentBase {
	
	public EmojiTextView txtMsg;

	public ItemIMChatSentText(Context context) {
		super(context);
		init(context);
	}

	public ItemIMChatSentText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		txtMsg=(EmojiTextView) layoutInflater.inflate(R.layout.item_im_chat_sent_text, null);
		setContentView(txtMsg);

	}
}
