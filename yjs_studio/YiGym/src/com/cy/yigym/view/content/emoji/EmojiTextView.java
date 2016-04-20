package com.cy.yigym.view.content.emoji;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-5
 * </p>
 * <p>
 * 支持表情功能的TextView
 * </p>
 */
public class EmojiTextView extends TextView {
	public EmojiTextView(Context context) {
		super(context);
	}

	public EmojiTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private int emojiWidthDp = EmojiConstance.DEFAULT_EMOJI_ICON_SIZE_DP,
			emojiHeightDp = EmojiConstance.DEFAULT_EMOJI_ICON_SIZE_DP;

	/**
	 * 设置表情图标大小,单位为dp
	 * 
	 * @param widthDp
	 * @param heightDp
	 */
	public void setEmojiSize(int widthDp, int heightDp) {
		this.emojiWidthDp = widthDp;
		this.emojiHeightDp = heightDp;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		SpannableString content = EmojiCover.decode(getContext(),
				text.toString(), emojiWidthDp, emojiHeightDp);
		super.setText(content, type);
	}
}
