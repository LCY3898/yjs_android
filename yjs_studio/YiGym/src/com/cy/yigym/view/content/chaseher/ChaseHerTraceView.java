package com.cy.yigym.view.content.chaseher;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.cy.widgetlibrary.WidgetUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-10
 * </p>
 * <p>
 * 追她-控制轨迹的控件
 * </p>
 */
public class ChaseHerTraceView extends View {
	private int width = 0, height = 0;
	private Point orginPoint = new Point(0, 0);// 原点坐标
	private Point trlOriginPoint = new Point();// 平移后的坐标原点
	private Paint paint;
	private int ovalA = WidgetUtils.dpToPx(150),
			ovalB = WidgetUtils.dpToPx(40);
	private int ovalA2 = WidgetUtils.dpToPx(180), ovalB2 = WidgetUtils
			.dpToPx(55);
	private OnPositionChangeListener onHeadPositionChangeListener;
	private double maxDistance = 1;// 最大距离
	private Point myPosition = new Point(0, 0), herPosition = new Point(0, 0);// 我和她当前的位置的x坐标
	private int positionMargin = WidgetUtils.dpToPx(25);// 头像重叠时，两个头像圆心之间的距离
	private boolean isShowReferenceLine = false;// 是否显示参考线，用于测试效果
	private int moonWidth = 0, moonHegiht = 0;// 月亮的大小
	private RectF ovalRect = null;// 椭圆的外切矩形
	private RectF ovalRect2 = null;// 最外边的轨迹
	private ArrayList<OnSizeMeasureCompleteListener> listener = new ArrayList<ChaseHerTraceView.OnSizeMeasureCompleteListener>();
	private boolean isSizeMeasureComplete = false;// 控件大小是否测量完成
	private boolean isFullTrace = false;// 是否绘制完整的椭圆轨迹，轨迹底片为true

	public ChaseHerTraceView(Context context) {
		super(context);
		init();
	}

	public ChaseHerTraceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 设置是否绘制完整轨迹，用于底片轨迹绘制
	 * 
	 * @param isFull
	 */
	public void setIsFullTrace(boolean isFull) {
		this.isFullTrace = isFull;
		postInvalidate();
	}

	private void init() {
		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		width = getWidth();
		height = getHeight();
		trlOriginPoint.set(width / 2, height / 2);
		if (!isSizeMeasureComplete) {
			for (int i = 0; i < listener.size(); i++) {
				listener.get(i).onSizeMeasureComplete();
			}
			listener.clear();
			isSizeMeasureComplete = true;
		}

		if (ovalRect == null)
			return;
		canvas.translate(trlOriginPoint.x, trlOriginPoint.y);
		if (isShowReferenceLine) {
			paint.setStyle(Style.STROKE);
			paint.setColor(0xff0199c2);
			paint.setStrokeWidth(WidgetUtils.dpToPx(10));
			if (isFullTrace)
				canvas.drawArc(ovalRect, 200, 160, false, paint);
			else
				canvas.drawArc(ovalRect, 0, 200, false, paint);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(WidgetUtils.dpToPx(5));
			paint.setColor(0xff9cd0ea);
			if (isFullTrace)
				canvas.drawArc(ovalRect2, 200, 160, false, paint);
			else
				canvas.drawArc(ovalRect2, 0, 200, false, paint);
			// paint.setColor(Color.RED);
			// canvas.drawPoint(myPosition.x, myPosition.y, paint);
			// canvas.drawPoint(herPosition.x, herPosition.y, paint);
		}

	}

	/**
	 * 控件大小测量完成
	 */
	private interface OnSizeMeasureCompleteListener {
		void onSizeMeasureComplete();
	}

	/**
	 * 在控件大小测量完成之后运行
	 * 
	 * @param l
	 */
	private void runAfterSizeMeasureComplete(OnSizeMeasureCompleteListener l) {
		listener.add(l);
	}

	/**
	 * 是否显示轨迹参考线
	 * 
	 * @param isShow
	 */
	public void isShowTraceLine(boolean isShow) {
		this.isShowReferenceLine = isShow;
		postInvalidate();

	}

	/**
	 * 设置月亮的大小，只有设置了月亮的大小，才能知道轨迹有多大
	 * 
	 * @param width
	 * @param height
	 */
	public void setMoonSize(int width, int height) {
		this.moonWidth = width;
		this.moonHegiht = height;
		ovalA = (int) (moonWidth / 2.0) + WidgetUtils.dpToPx(10);
		float top = orginPoint.y - ovalB;
		float left = orginPoint.x - ovalA;
		float right = orginPoint.x + ovalA;
		float bottom = orginPoint.y + ovalB;

		ovalA2 = (int) (moonWidth / 2.0) + WidgetUtils.dpToPx(30);
		float top2 = orginPoint.y - ovalB2;
		float left2 = orginPoint.x - ovalA2;
		float right2 = orginPoint.x + ovalA2;
		float bottom2 = orginPoint.y + ovalB2;
		ovalRect = new RectF(left, top, right, bottom);
		ovalRect2 = new RectF(left2, top2, right2, bottom2);
		postInvalidate();
	}

	/**
	 * 获取椭圆外切矩形
	 * 
	 * @return
	 */
	public RectF getOvalRect() {
		return ovalRect;
	}

	/**
	 * 设置最大距离
	 * 
	 * @param maxDistance
	 */
	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	/**
	 * 获取设置的最大距离
	 * 
	 * @return
	 */
	public double getMaxDistance() {
		return maxDistance;
	}

	/**
	 * 设置当前距离
	 * 
	 * @param curDistance
	 */
	public void setCurDistance(double curDistance) {
		if (this.maxDistance == 0) {
			this.maxDistance = 1;
			curDistance = 1;
		}
		if (ovalRect == null)
			return;
		float x = 0;
		float minX = (float) (positionMargin / 2.0);
		x = (float) (ovalA * curDistance / maxDistance);
		x = x < minX ? minX : x;
		this.myPosition.set((int) (orginPoint.x - x),
				(int) getOvalY(orginPoint.x - x));
		this.herPosition.set((int) (orginPoint.x + x),
				(int) getOvalY(orginPoint.x + x));
		this.postInvalidate();
		if (isSizeMeasureComplete) {
			setRealPosition();
		} else {
			runAfterSizeMeasureComplete(new OnSizeMeasureCompleteListener() {

				@Override
				public void onSizeMeasureComplete() {
					setRealPosition();
				}
			});
		}
	}

	/**
	 * 获取旋转坐标系后相对于控件左上角为原点的真实坐标
	 */
	private void setRealPosition() {
		if (onHeadPositionChangeListener != null) {
			onHeadPositionChangeListener
					.onMyPositionChange(getRealPoint(myPosition));
			onHeadPositionChangeListener
					.onHerPositionChange(getRealPoint(herPosition));
		}
	}

	/**
	 * 获取以控件的(0,0)点位原点的坐标系中的相对坐标
	 * 
	 * @param point
	 * @return
	 */
	private Point getRealPoint(Point point) {
		if (trlOriginPoint == null || point == null)
			return null;
		return new Point(point.x + trlOriginPoint.x, point.y + trlOriginPoint.y);
	}

	/**
	 * 位置改变监听接口
	 */
	public interface OnPositionChangeListener {
		void onMyPositionChange(Point position);

		void onHerPositionChange(Point position);
	}

	/**
	 * 设置头像位置改变监听事件
	 * 
	 * @param l
	 */
	public void setOnPositionChangeListener(OnPositionChangeListener l) {
		this.onHeadPositionChangeListener = l;
	}

	/**
	 * 获取椭圆的y坐标
	 * 
	 * @param x
	 *            椭圆的x坐标
	 * @return
	 */
	private float getOvalY(float x) {
		float yy = Math.abs((float) (ovalB * ovalB - ovalB * ovalB * 1.0
				/ (ovalA * ovalA) * x * x));
		float y = (float) Math.sqrt(yy);
		return y;
	}

}
