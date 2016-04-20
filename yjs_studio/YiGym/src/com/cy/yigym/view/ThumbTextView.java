package com.cy.yigym.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-25
 * </p>
 * <p>
 * SeekBar的Thumb的文字
 * </p>
 */
public class ThumbTextView extends TextView {
	private int width = 0;
	private Runnable runnable;
	private Handler mainHandler = new Handler(Looper.getMainLooper());

	public ThumbTextView(Context context) {
		super(context);
	}

	public ThumbTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void attachToSeekBar(final SeekBar seekBar) {
		if (width == 0) {
			// 当宽度为0时，说明还没测量完成，延后处理
			runnable = new Runnable() {

				@Override
				public void run() {
					attachToSeekBarInternal(seekBar);
				}
			};
		} else {
			runnable = null;
			attachToSeekBarInternal(seekBar);
		}

	}

	public void attachToSeekBarInternal(SeekBar seekBar) {
		String content = getText().toString();
		if (TextUtils.isEmpty(content) || seekBar == null)
			return;
		float contentWidth = this.getPaint().measureText(content);
		int realWidth = width - seekBar.getPaddingLeft()
				- seekBar.getPaddingRight();
		int maxLimit = (int) (width - contentWidth - seekBar.getPaddingRight());
		int minLimit = seekBar.getPaddingLeft();
		float percent = (float) (1.0 * seekBar.getProgress() / seekBar.getMax());
		int left = minLimit + (int) (realWidth * percent - contentWidth / 2.0);
		left = left <= minLimit ? minLimit : left >= maxLimit ? maxLimit : left;
		setPadding(left, 0, 0, 0);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (width == 0)
			width = MeasureSpec.getSize(widthMeasureSpec);
		if (runnable != null) {
			mainHandler.post(runnable);
			runnable = null;
		}
	}
}
