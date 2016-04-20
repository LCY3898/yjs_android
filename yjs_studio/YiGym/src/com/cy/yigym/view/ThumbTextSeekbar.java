package com.cy.yigym.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-25
 * </p>
 * <p>
 * 文字跟随的Seekbar
 * </p>
 */
public class ThumbTextSeekbar extends RelativeLayout {
	// public ThumbTextView tvThumb;
	public TextView tvCur;
	public SeekBar seekBar;
	public TextView tvTotal;
	private OnSeekBarChangeListener onSeekBarChangeListener;

	public ThumbTextSeekbar(Context context) {
		super(context);
		init();
	}

	public ThumbTextSeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(
				R.layout.view_thumb_text_seekbar, this);
		tvTotal = (TextView) findViewById(R.id.tvTotal);
		// tvThumb = (ThumbTextView) findViewById(R.id.tvThumb);
		tvCur = (TextView) findViewById(R.id.tvCur);
		seekBar = (SeekBar) findViewById(R.id.sbProgress);
		seekBar.setEnabled(false);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (onSeekBarChangeListener != null)
					onSeekBarChangeListener.onStopTrackingTouch(seekBar);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				if (onSeekBarChangeListener != null)
					onSeekBarChangeListener.onStartTrackingTouch(seekBar);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (onSeekBarChangeListener != null)
					onSeekBarChangeListener.onProgressChanged(seekBar,
							progress, fromUser);
				//tvThumb.attachToSeekBar(seekBar);
			}
		});
	}

	/**
	 * 设置进度监听
	 * 
	 * @param l
	 */
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
		this.onSeekBarChangeListener = l;
	}

	// /**
	// * 设置Thumb的文字
	// *
	// * @param text
	// */
	// public void setThumbText(String text) {
	// tvThumb.setText(text);
	// }

	/**
	 * 设置进度
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		// 首次设置值且值为0时,也触发onProgressChanged回调
		if (progress == seekBar.getProgress() && progress == 0) {
			seekBar.setProgress(1);
			seekBar.setProgress(0);
		} else {
			seekBar.setProgress(progress);
		}
	}
}
