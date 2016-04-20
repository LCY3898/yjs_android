package com.cy.yigym.view.content.emoji;

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.cy.yigym.view.content.emoji.EmojiCover.GroupEntity;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-5
 * </p>
 * <p>
 * 支持表情功能的Edittext
 * </p>
 */
public class EmojiEdittext extends EditText {
	private int emojiWidthDp = EmojiConstance.DEFAULT_EMOJI_ICON_SIZE_DP,
			emojiHeightDp = EmojiConstance.DEFAULT_EMOJI_ICON_SIZE_DP;

	public EmojiEdittext(Context context) {
		super(context);
	}

	public EmojiEdittext(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

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

	/**
	 * 输入文字,在光标处插入文字
	 * 
	 * @param text
	 */
	public void inputText(CharSequence text) {
		SpannableString content = EmojiCover.decode(getContext(),
				text.toString(), emojiWidthDp, emojiHeightDp);
		Editable edit = getEditableText();
		int selectionStart = getSelectionStart();
		if (selectionStart < 0 || selectionStart >= edit.length()) {
			edit.append(content);
		} else {
			edit.insert(selectionStart, content);
		}
	}

	/**
	 * 回格
	 */
	public void backspace() {
		String content = getText().toString();
		if (TextUtils.isEmpty(content))
			return;
		Editable e = getEditableText();
		int oldSelection = getSelectionEnd();
		if (oldSelection == 0)
			return;
		String beforeSelectionStr = content.substring(0, oldSelection);
		ArrayList<ArrayList<GroupEntity>> groups = EmojiCover.getGroups(
				EmojiCover.EXPRESS_EMOJI, beforeSelectionStr);
		boolean isLastEmoji = false;
		GroupEntity lastEmoji = null;
		if (groups.size() != 0) {
			ArrayList<GroupEntity> emojis = groups.get(groups.size() - 1);
			if (emojis.size() != 0) {
				lastEmoji = emojis.get(emojis.size() - 1);
				isLastEmoji = lastEmoji.end == beforeSelectionStr.length() ? true
						: false;
			}
		}
		int newSelection = 0;
		if (isLastEmoji) {
			newSelection = lastEmoji.start;
			e.delete(lastEmoji.start, lastEmoji.end);
		} else {
			newSelection = oldSelection - 1;
			e.delete(oldSelection - 1, oldSelection);
		}
		setSelection(newSelection);
	}
}
