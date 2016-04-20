/**
 * @author Raghav Sood
 * @version 1
 * @date 26 January, 2013
 */
package com.cy.yigym.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cy.widgetlibrary.WidgetUtils;
import com.efit.sport.R;
import com.hhtech.utils.DimenUtils;

/**
 * 可尝试seekArc或者ProgressWheel The Class CircularSeekBar.
 */
public class BookmarkView extends View {
	private int width = 0;
	private int height = 0;
	private Bitmap bookmarkBmp;

	private int MARGIN = DimenUtils.dpToPx(8);

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public BookmarkView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initDrawable();
	}

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public BookmarkView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDrawable();
	}

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 */
	public BookmarkView(Context context) {
		super(context);
		initDrawable();
	}

	/**
	 * Inits the drawable.
	 */
	public void initDrawable() {
		bookmarkBmp = BitmapFactory.decodeResource(getContext().getResources(),
				R.drawable.icon_bookmark);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		width = getWidth(); // Get View Width
		height = getHeight();// Get View Height
		if (width == 0) {
			width = widthMeasureSpec & MEASURED_SIZE_MASK;
			height = heightMeasureSpec & MEASURED_SIZE_MASK;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int height = getHeight();
		int iconH = bookmarkBmp.getHeight();
		int iconW = bookmarkBmp.getWidth();
		int unitH = iconH + MARGIN;
		int num = (height + iconH - MARGIN) / unitH;
		for (int i = 0; i < num; i++) {
			int top = i * unitH + MARGIN;
			if (i == num - 1) {
				int bottom = Math.min(top + iconH, height - MARGIN);
				Rect dest = new Rect(0, top, iconW, bottom);
				Rect src = new Rect(0, top, iconW, top + iconH);
				canvas.save();
				canvas.clipRect(dest);
				canvas.drawBitmap(bookmarkBmp, 0, top, null);
				canvas.restore();
				// canvas.drawBitmap(bookmarkBmp, r);
			} else {
				canvas.drawBitmap(bookmarkBmp, 0, i * unitH + MARGIN, null);
			}
		}

		super.onDraw(canvas);
	}

	public void drawMarkerAtProgress(Canvas canvas) {
		canvas.drawBitmap(bookmarkBmp, 0, 0, null);
	}

	/**设置两个点之间的间隔
	 * @param marginDp
	 */
	public void setMargin(int marginDp) {
		this.MARGIN = WidgetUtils.dpToPx(marginDp);
	}

}
