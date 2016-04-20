package com.cy.yigym.view.content.im;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.yigym.view.content.emoji.EmojiEdittext;
import com.cy.yigym.view.content.emoji.EmojiHashMap;
import com.cy.yigym.view.content.emoji.EmojiViewPager;
import com.cy.yigym.view.content.emoji.EmojiViewPager.EmojiEntity;
import com.cy.yigym.view.content.emoji.EmojiViewPager.OnEmojiDeleteClickListener;
import com.cy.yigym.view.content.emoji.EmojiViewPager.OnEmojiItemClickListener;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-5
 * </p>
 * <p>
 * 聊天信息发送板
 * </p>
 */
public class IMSendBoard extends RelativeLayout implements
		OnEmojiDeleteClickListener, OnEmojiItemClickListener, OnClickListener {
	private ImageButton btnEmoji;
	private Button btnSend;
	private EmojiEdittext editMsg;
	private EmojiViewPager vEmoji;
	private ArrayList<EmojiEntity> emojis = new ArrayList<EmojiViewPager.EmojiEntity>();
	private OnKeyBoardOpCallBack onKeyBoardOpCallBack;

	public IMSendBoard(Context context) {
		super(context);
		init();
	}

	public IMSendBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void initEmojis() {
		LinkedHashMap<String, Integer> maps = EmojiHashMap.getMap();
		Set<String> keys = maps.keySet();
		for (String key : keys) {
			emojis.add(new EmojiEntity(key, maps.get(key)));
		}

	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_im_send_board,
				this, true);
		initEmojis();
		btnEmoji = (ImageButton) findViewById(R.id.btnEmoji);
		btnSend = (Button) findViewById(R.id.btnSend);
		editMsg = (EmojiEdittext) findViewById(R.id.editMsg);
		vEmoji = (EmojiViewPager) findViewById(R.id.vEmoji);
		btnEmoji.setOnClickListener(this);
		editMsg.setOnClickListener(this);
		vEmoji.setEmojis(emojis);
		vEmoji.setOnEmojiDeleteClickListener(this);
		vEmoji.setOnEmojiItemClickListener(this);
		this.setBackgroundColor(0xffebecee);
		GradientDrawable msBg = BgDrawableUtils.creShape(0xffffffff, 8, 1,
				0xffccc6c6, null);
		StateListDrawable bgSend = BgDrawableUtils.crePressSelector(0xff2e88ee,
				0xff2367b3, 5);
		editMsg.setBackgroundDrawable(msBg);
		btnSend.setBackgroundDrawable(bgSend);
		vEmoji.setVisibility(View.GONE);
		editMsg.setFocusable(true);
		if (onKeyBoardOpCallBack != null)
			onKeyBoardOpCallBack.onCloseKeyBoard();
	}

	@Override
	public void onEmojiItemClick(EmojiEntity emoji) {
		editMsg.inputText(emoji.emojiKey);
	}

	@Override
	public void onEmojiDeleteClick() {
		editMsg.backspace();
	}

	/**
	 * 获取文字内容
	 * 
	 * @return
	 */
	public String getText() {
		return editMsg.getText().toString();
	}

	/**
	 * 清除文字
	 */
	public void clearText() {
		editMsg.getText().clear();
	}

	/**
	 * 设置发送点击监听事件
	 * 
	 * @param l
	 */
	public void setOnSendClickListener(OnClickListener l) {
		btnSend.setOnClickListener(l);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnEmoji:
			if (vEmoji.getVisibility() == View.VISIBLE) {
				vEmoji.setVisibility(View.GONE);
				if (onKeyBoardOpCallBack != null)
					onKeyBoardOpCallBack.onShowKeyBoard();

			} else {
				vEmoji.setVisibility(View.VISIBLE);
				if (onKeyBoardOpCallBack != null)
					onKeyBoardOpCallBack.onCloseKeyBoard();

			}
			break;
		case R.id.editMsg:
			if (vEmoji.getVisibility() == View.VISIBLE) {
				vEmoji.setVisibility(View.GONE);
			}
			break;
		}
	}

	/**
	 * 输入框操作回调
	 */
	public static interface OnKeyBoardOpCallBack {
		void onShowKeyBoard();

		void onCloseKeyBoard();
	}

	/**
	 * 设置输入框操作回调
	 * 
	 * @param cbk
	 */
	public void setOnKeyBoardOpCallBack(OnKeyBoardOpCallBack cbk) {
		this.onKeyBoardOpCallBack = cbk;
	}

}
