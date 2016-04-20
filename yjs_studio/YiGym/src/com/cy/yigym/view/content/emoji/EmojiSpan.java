package com.cy.yigym.view.content.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-4
 * </p>
 * <p>
 * 表情span
 * </p>
 */
public class EmojiSpan extends DynamicDrawableSpan {
	private Context context;
	private int width = 0, height = 0;// 表情图片的大小,单位为px
	private int drawableId = 0;// 表情图片资源id

	public EmojiSpan(Context context, int drawableId, int widthDp, int heightDp) {
		this.context = context;
		this.width = dpToPx(widthDp);
		this.height = dpToPx(heightDp);
		this.drawableId = drawableId;
	}

	@Override
	public Drawable getDrawable() {
		Drawable drawable = null;
		try {
			if (drawableId != 0) {
				drawable = context.getResources().getDrawable(drawableId);
				drawable.setBounds(0, 0, width, height);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return drawable;
	}

	/**
	 * 将dp转换成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	private int dpToPx(float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

}
