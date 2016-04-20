package com.cy.yigym.view.content.chaseher;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Caiyuan Huang
 * <p>
 * 2015-9-26
 * </p>
 * <p>
 * 防nice翻页控件
 * </p>
 */
public class FlipPager extends FrameLayout {
	private ViewDragHelper dragHelper;
	private PointF movePoint, downPoint, upPoint;// 触摸点
	private final int MAX_ROTATION = 25;// 最大旋转角度
	private Adapter adapter;
	private OnFlipPageChangeListener onPageChangeListener;
	private int curPosition = 0;// 当前的item的索引
	private int dragPosition = curPosition;// 用于记录拖动翻页的索引
	// 判断onViewReleased方法是否有调用，避免有时拖动图片卡住，没有还原的问题
	private boolean isOnViewReleasedCall = true;

	public static interface OnFlipPageChangeListener extends
			OnPageChangeListener {
		public void onNonePage();
	}

	/**
	 * 设置页面改变监听器
	 * 
	 * @param l
	 */
	public void setOnPageChangeListener(OnFlipPageChangeListener l) {
		this.onPageChangeListener = l;
	}

	/**
	 * 设置适配器
	 * 
	 * @param adapter
	 */
	public void setAdapter(Adapter adapter) {
		this.adapter = adapter;
		setCurrentItem(0);
	}

	public FlipPager(Context context) {
		super(context);
		init(getContext());
	}

	public FlipPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(getContext());
	}

	private void init(Context context) {
		dragHelper = ViewDragHelper.create(this, dragHelperCallback);
	}

	private ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// 最上面一个才允许拖动
			boolean isTopView = (getTopChildView() == child);
			isOnViewReleasedCall = false;
			return isTopView;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			return left;
		};

		public int clampViewPositionVertical(View child, int top, int dy) {
			return top;
		};

		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (releasedChild == getTopChildView()) {
				realseView(false);
			}
		};

	};

	/**
	 * 添加子控件
	 */
	private void addChildView(int position, int zOrder) {
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View view = adapter.getView(position, null, this);
		addView(view, zOrder);
		view.setLayoutParams(lp);

	}

	/**
	 * 设置当前的item
	 * 
	 * @param position
	 */
	public void setCurrentItem(int position) {
		if (position < 0 || position > adapter.getCount() - 1)
			return;
		removeAllViews();
		if (position + 1 >= 0 && position + 1 <= adapter.getCount() - 1) {
			addChildView(position + 1, 0);
			addChildView(position, 1);
		} else {
			addChildView(position, 0);
		}
		curPosition = position;
		dragPosition = curPosition;
		if (onPageChangeListener != null)
			onPageChangeListener.onPageSelected(curPosition);
	}

	/**
	 * 释放控件
	 */
	private void realseView(boolean isOutOfCallOnViewReleased) {
		isOnViewReleasedCall = true;
		if (!isFlip()) {
			reset(isOutOfCallOnViewReleased);
		} else {
			flip();
		}
	}

	/**
	 * 是否翻页
	 * 
	 * @return
	 */
	private boolean isFlip() {
		if (downPoint == null || upPoint == null)
			return false;
		float dx = Math.abs(upPoint.x - downPoint.x);
		if (dx >= getWidth() / 3.0)
			return true;
		return false;
	}

	/**
	 * 是否需要旋转，防止点击时还没有移动就旋转了
	 * 
	 * @return
	 */
	private boolean isRotate() {
		if (downPoint == null || movePoint == null)
			return false;
		float dx = Math.abs(movePoint.x - downPoint.x);
		if (dx >= dpToPx(10))
			return true;
		return false;
	}

	/**
	 * 获取最上面的可拖拉控件
	 * 
	 * @return
	 */
	private View getTopChildView() {
		if (getChildCount() == 0)
			return null;
		return getChildAt(getChildCount() - 1);
	}

	/**
	 * 重置位置和角度
	 */
	private void reset(boolean isOutOfCallOnViewReleased) {
		// 因为page都是填充满整个父控件的，所以其初始位置为(0,0)
		if (!isOutOfCallOnViewReleased) {
			dragHelper.settleCapturedViewAt(0, 0);
			invalidate();
		} else {
			AnimatorSet set = new AnimatorSet();
			set.playTogether(ObjectAnimator.ofFloat(getTopChildView(),
					"translationX", 0), ObjectAnimator.ofFloat(
					getTopChildView(), "translationY", 0));
			set.setDuration(500).start();
		}
		ViewHelper.setRotation(getTopChildView(), 0);
	}

	/**
	 * 翻页
	 */
	private void flip() {
		try {
			dragPosition++;
			if (dragPosition > adapter.getCount() - 1) {
				if (onPageChangeListener != null)
					onPageChangeListener.onNonePage();
			}
			dragPosition = dragPosition > adapter.getCount() - 1 ? adapter
					.getCount() - 1 : dragPosition;
			setCurrentItem(dragPosition);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void computeScroll() {
		// 此处做反弹回滚效果
		if (dragHelper.continueSettling(true)) {
			invalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (adapter == null) {
			throw new RuntimeException("请调用setAdapter设置适配器");
		}
		if (getTopChildView() == null)
			return super.onInterceptTouchEvent(ev);
		return dragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		if (getTopChildView() == null)
			return true;
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downPoint = new PointF(ev.getX(), ev.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			movePoint = new PointF(ev.getX(), ev.getY());
			if (isRotate()) {
				float angle = calculateRotationAngle(movePoint, new PointF(
						(float) (getWidth() / 2.0), 0));
				// 角度矫正，避免旋转的过份厉害
				angle = angle > MAX_ROTATION ? MAX_ROTATION : angle;
				angle = ev.getX() < getWidth() / 2.0 ? angle : -angle;
				ViewHelper.setRotation(getTopChildView(), angle);
			}
			break;
		case MotionEvent.ACTION_UP:
			upPoint = new PointF(ev.getX(), ev.getY());
			if (!isOnViewReleasedCall) {
				realseView(true);
			}
			break;
		}
		dragHelper.processTouchEvent(ev);
		return true;
	}

	/**
	 * 计算旋转角度
	 * 
	 * @param touchPoint
	 *            触摸点
	 * @param topMidPoint
	 *            本控件的最上边且最中间的点
	 * @return
	 */
	private float calculateRotationAngle(PointF touchPoint, PointF topMidPoint) {
		float tan = Math.abs(touchPoint.x - topMidPoint.x)
				/ (touchPoint.y - topMidPoint.y);
		float angle = (float) ((float) Math.atan(tan) * 180.0 / Math.PI);
		return angle;
	}

	/**
	 * 将dp转换成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	private int dpToPx(float dp) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

}
