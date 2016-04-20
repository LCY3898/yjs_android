package com.cy.yigym.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-9-8
 * </p>
 * <p>
 * 自定义根据特定路径绘制的textview
 * </p>
 */
public class CustomPathTextView extends TextView {
	private RectF rectF=new RectF(0, 0, WidgetUtils.dpToPx(100), WidgetUtils.dpToPx(40));
	private Path path;
	private Paint paint;

	public CustomPathTextView(Context context) {
		super(context);
		init(context);
	}

	public CustomPathTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setPathRectF(RectF rectF) {
		this.rectF = rectF;
		init(getContext());
		postInvalidate();
	}

	@TargetApi(11)
	private void init(Context context) {
		if (rectF == null)
			return;
//		if (Build.VERSION.SDK_INT >= 11)
//			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		paint = new Paint();
		path=new Path();
		path.addOval(rectF, Path.Direction.CCW);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (rectF == null) {
			super.onDraw(canvas);
			return;
		}
		paint = getPaint();
		paint.setColor(getCurrentTextColor());
		paint.setTextAlign(Paint.Align.RIGHT);
		canvas.translate(getWidth() / 2 - rectF.width() / 2, getHeight() / 2
				- rectF.height() / 2);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawTextOnPath(getText().toString(), path, 0, 0, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (rectF == null)
			return;
		setMeasuredDimension((int) rectF.width() * 2, (int) rectF.height() * 2);
	}
}
