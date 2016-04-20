package com.cy.yigym.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Caiyuan Huang
 * <p>
 * 2015-3-28
 * </p>
 * <p>
 * 自定义圆环进度条
 * </p>
 */
public class CustomCircleProgress extends ViewGroup {
	// 画圆环的画笔
	private Paint mPaint;
	// 圆环背景颜色
	private int ringBgColor;
	// 圆环颜色
	private Integer mRingColor;
	// 中间园的颜色
	private int centerCircleColor;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	// 圆弧所在的椭圆的外面包围的矩形
	private RectF oval;
	// 控件的宽高
	private int mWidth, mHeight;
	private Context context;
	private boolean isFirstDraw = true;
	private OnCenterViewClickListener mOnCenterViewClickListener;

	public CustomCircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// 一定要加这句话，否则onDraw方法不会调用
		setWillNotDraw(false);
	}

	private void initAttrs(Context context) {
		this.context = context;
		if (mStrokeWidth == 0)
			mStrokeWidth = dpToPx(context, 15);
		ringBgColor = Color.parseColor("#ff171717");
		centerCircleColor = 0xff353535;
		if (mRingColor == null)
			mRingColor = Color.parseColor("#ff33a7dd"); //tangtaotao change from ffde5f22
		mRingRadius = mWidth / 4 - mStrokeWidth / 12;
		mXCenter = mWidth / 2;
		mYCenter = mHeight / 2;
		oval = new RectF();
		oval.left = (mXCenter - mRingRadius);
		oval.top = (mYCenter - mRingRadius);
		oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
		oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
	}

	public void setCircleWidth(int dp) {
		mStrokeWidth = dpToPx(context, dp);
	}

	private void initVariable() {

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mRingColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(mStrokeWidth);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isFirstDraw) {
			initAttrs(context);
			initVariable();
			isFirstDraw = false;

		}
		//tangtaotao comment out
		// 绘制中间的圆
		//mPaint.setColor(centerCircleColor);
		//mPaint.setStyle(Paint.Style.FILL);
		//canvas.drawCircle(mXCenter, mYCenter, mRingRadius, mPaint);
		mPaint.setStyle(Paint.Style.STROKE);
		if (mProgress > 100) {
			mProgress = 100;
		}
		if (mProgress >= 0) {
			//tangtaotao comment out
			//mPaint.setColor(ringBgColor);
			//canvas.drawArc(oval, 0, 360, false, mPaint);
			mPaint.setColor(mRingColor);
			/*
			 * drawArc方法介绍:</br>\n
			 * oval:表示圆弧所在的椭圆，是这个矩形的内切椭圆，若矩形为正方形，则椭圆会变成圆</br>
			 * startAngle:绘制圆弧开始的角度，以水平向右为0度，顺时针递增</br>
			 * sweepAngle:表示圆弧要扫过的角度大小</br>
			 * useCenter:如果为true则会变成扇形,false为圆弧</br>
			 */
			if (mProgress > 0)
				// 水杯方式两端平等上升
				// canvas.drawArc(oval, 90 - (int) ((float) mProgress
				// / mTotalProgress * 360.0 / 2.0),
				// ((float) mProgress / mTotalProgress) * 360, false,
				// mRingPaint);
				// 从顶端顺时针方式开始
				canvas.drawArc(oval, -90,
						((float) mProgress / mTotalProgress) * 360, false,
						mPaint);
		}
	}

	private ProgressRunable progressRunable;

	/**
	 * 设置进度
	 * 
	 * @param progress
	 *            进度在[0,100]之间
	 */
	public void setProgress(int progress) {
		if (progressRunable == null) {
			progressRunable = new ProgressRunable(progress);
		} else {
			progressRunable.stop();
			progressRunable = new ProgressRunable(progress);
		}
		new Thread(progressRunable).start();
	}

	/**
	 * 设置进度
	 * 
	 * @param progress
	 *            进度在[0,100]之间
	 * @param isAnim
	 *            是否要动画
	 */
	public void setProgress(int progress, boolean isAnim) {
		if (isAnim)
			setProgress(progress);
		else {
			mProgress = progress;
			postInvalidate();
		}
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthSpecMode == MeasureSpec.EXACTLY
				|| widthSpecMode == MeasureSpec.AT_MOST) {
			mWidth = widthSpecSize;
		} else {
			mWidth = dpToPx(context, 100);
		}
		if (heightSpecMode == MeasureSpec.AT_MOST
				|| heightSpecMode == MeasureSpec.UNSPECIFIED) {
			mHeight = dpToPx(context, 100);
		} else {
			mHeight = heightSpecSize;
		}
		setMeasuredDimension(mWidth, mHeight);
	}

	class ProgressRunable implements Runnable {
		private int progress;
		private boolean isStop = false;

		public ProgressRunable(int progress) {
			this.progress = progress;
		}

		@Override
		public void run() {
			int count = mProgress;
			//mProgress = count;
			while (count < progress && !isStop) {
				count++;
				mProgress = count;
				postInvalidate();
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			postInvalidate();
		}

		public void stop() {
			isStop = true;
		}

	}

	/**
	 * 将dp转换成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dpToPx(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * 设置圆环颜色
	 * 
	 * @param color
	 */
	public void setRingColor(int color) {
		this.mRingColor = color;
	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-3-28
	 * </p>
	 * <p>
	 * 圆圈内部控件点击监听事件
	 * </p>
	 */
	public interface OnCenterViewClickListener {
		void onCenterViewClick(float x, float y);
	}

	/**
	 * 设置圆圈内部控件点击监听事件
	 * 
	 * @param l
	 */
	public void setOnCenterViewClickListener(OnCenterViewClickListener l) {
		this.mOnCenterViewClickListener = l;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();
			if ((x - mXCenter) * (x - mXCenter) + (y - mYCenter)
					* (y - mYCenter) <= mRingRadius * mRingRadius) {
				if (mOnCenterViewClickListener != null)
					mOnCenterViewClickListener.onCenterViewClick(x, y);
			}
		}
		return super.onTouchEvent(event);
	}
}
