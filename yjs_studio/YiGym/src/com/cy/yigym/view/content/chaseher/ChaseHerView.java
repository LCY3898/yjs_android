package com.cy.yigym.view.content.chaseher;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.yigym.view.CustomRotateFrameLayout;
import com.cy.yigym.view.content.chaseher.ChaseHerTraceView.OnPositionChangeListener;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-10
 * </p>
 * <p>
 * 追她控件
 * </p>
 */
public class ChaseHerView extends FrameLayout implements
		OnPositionChangeListener {
	private ChaseHerHeadView vMyHead, vHerHead, vFinalHerHead, vFinalMyHead;
	private LinearLayout linFinalHead;
	private ImageView imgMoonBg, imgMoon;
	private TextView txtSurpDistance;
	private ChaseHerTraceView vChaseHerTrace, vTrace;// vTrace是底部的轨迹
	private CustomRotateFrameLayout frmContent;
	private Handler mainHandler = new Handler(Looper.getMainLooper());
	private double maxDistance = 0;
	private double curDistance = 0;
	private ArrayList<OnTraceCompleteListener> listeners = new ArrayList<ChaseHerView.OnTraceCompleteListener>();
	private boolean isTraceComplete = false;// 轨迹是否计算完成
	private OnGlobalLayoutListener listener = null;
	private int headWidth = WidgetUtils.dpToPx(60);// 头像宽度
	private int headHeight = WidgetUtils.dpToPx(60);// 头像高度
	private int rotateAnge = -25;
	private ImageView imgStar;

	public ChaseHerView(Context context) {
		super(context);
		init();
	}

	public ChaseHerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_chase_her,
				this, true);
		imgStar = (ImageView) findViewById(R.id.imgStar);
		txtSurpDistance = (TextView) findViewById(R.id.txtSurpDistance);
		frmContent = (CustomRotateFrameLayout) findViewById(R.id.frmContent);
		imgMoon = (ImageView) findViewById(R.id.imgMoon);
		imgMoonBg = (ImageView) findViewById(R.id.imgMoonBg);
		vMyHead = (ChaseHerHeadView) findViewById(R.id.vMyHead);
		vHerHead = (ChaseHerHeadView) findViewById(R.id.vHerHead);
		vFinalHerHead = (ChaseHerHeadView) findViewById(R.id.vFinalHerHead);
		vFinalMyHead = (ChaseHerHeadView) findViewById(R.id.vFinalMyHead);
		vTrace = (ChaseHerTraceView) findViewById(R.id.vTrace);
		vChaseHerTrace = (ChaseHerTraceView) findViewById(R.id.vChaseHerTrace);
		linFinalHead = (LinearLayout) findViewById(R.id.linFinalHead);
		// 没有设值，不要出来
		setHeadViewsGone();
		// 设置头像背景
		vHerHead.setHeadBg(R.drawable.icon_tx_pink);
		vMyHead.setHeadBg(R.drawable.icon_tx_yellow);
		imgMoon.setBackgroundResource(R.drawable.icon_moon);
		vChaseHerTrace.setOnPositionChangeListener(ChaseHerView.this);
		final ViewTreeObserver vto = this.getViewTreeObserver();
		listener = new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {

				int moonHeight = imgMoon.getMeasuredHeight();
				int moonWidth = imgMoon.getMeasuredWidth();
				vChaseHerTrace.setMoonSize(moonWidth, moonHeight);
				vTrace.setMoonSize(moonWidth, moonHeight);
				vTrace.setIsFullTrace(true);
				// txtSurpDistance.setPathRectF(vTrace.getOvalRect());
				isTraceComplete = true;
				for (int i = 0; i < listeners.size(); i++) {
					listeners.get(i).onComplete();
				}
				listeners.clear();
				imgMoon.getViewTreeObserver().removeGlobalOnLayoutListener(
						listener);
			}
		};
		vto.addOnGlobalLayoutListener(listener);
	}

	/**
	 * 设置旋转角度
	 * 
	 * @param angle
	 */
	public void setRotateAngle(int angle) {
		this.rotateAnge = angle;
		frmContent.setRotateAngle(rotateAnge);
	}

	/**
	 * 轨迹计算完成监听接口
	 */
	private interface OnTraceCompleteListener {
		void onComplete();
	}

	/**
	 * 设置头像位置
	 * 
	 * @param imgHead
	 * @param position
	 */
	private void setHeadPosition(ChaseHerHeadView imgHead, Point position) {
		if (imgHead == null || position == null)
			return;
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imgHead
				.getLayoutParams();
		lp.leftMargin = (int) (position.x - headWidth / 2.0);
		lp.topMargin = position.y - headHeight;
		imgHead.setLayoutParams(lp);
		imgHead.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置最大距离
	 * 
	 * @param maxDistance
	 */
	public void setMaxDistance(double maxDistance) {
		if (maxDistance == 0)
			txtSurpDistance.setVisibility(View.GONE);
		vChaseHerTrace.setMaxDistance(maxDistance);
		this.maxDistance = maxDistance;
	}

	/**
	 * 设置当前距离
	 * 
	 * @param curDistance
	 *            当前距离
	 * @param surpTextFormat
	 *            剩余距离的文字格式化字符串
	 * @param dfNum
	 */
	private void setCurDistanceInternal(final double curDistance) {
		final double distance = maxDistance - curDistance;
		if (isTraceComplete)
			vChaseHerTrace.setCurDistance(distance);
		else {
			runAfterTraceComplete(new OnTraceCompleteListener() {

				@Override
				public void onComplete() {
					vChaseHerTrace.setCurDistance(distance);
				}
			});
		}
	}

	/**
	 * 设置当前距离
	 * 
	 * @param curDistance
	 *            当前距离
	 * @param surpTextFormat
	 *            剩余距离的格式化字符串
	 */
	public void setCurDistance(double curDistance) {
		this.curDistance = curDistance;
		if (this.maxDistance <= this.curDistance) {
			txtSurpDistance.setVisibility(View.GONE);
			setHeadViewsGone();
			return;
		}
		setCurDistanceInternal(curDistance);
	}

	/**
	 * 获取剩余距离
	 * 
	 * @return
	 */
	public double getSurpDistance() {
		return maxDistance - curDistance;
	}

	/**
	 * 设置剩余距离的文字内容
	 * 
	 * @param text
	 */
	public void setSurpDistanceText(String text) {
		if (curDistance >= maxDistance) {
			setSurpDistanceViewGone();
			return;
		}
		txtSurpDistance.setText(text);
		txtSurpDistance.setVisibility(View.VISIBLE);
		// @tangtt 解决使用setText无法更新文字显示的问题
		txtSurpDistance.invalidate();
	}

	/**
	 * 设置头像地址
	 * 
	 * @param myFid
	 * @param herFid
	 */
	public void setHeadUrl(String myFid, String herFid) {
		vMyHead.setHead(myFid);
		vHerHead.setHead(herFid);
		vFinalHerHead.setHead(herFid);
		vFinalMyHead.setHead(myFid);
	}

	/**
	 * 设置月球的资源id
	 * 
	 * @param id
	 */
	public void setMoonDrawableId(int id) {
		imgMoon.setBackgroundResource(id);
	}

	/**
	 * 设置月亮背景的资源id
	 * 
	 * @param id
	 */
	public void setMoonBgDrawableId(int id) {
		imgMoonBg.setBackgroundResource(id);
	}

	@Override
	public void onMyPositionChange(Point position) {
		setHeadPosition(vMyHead, position);

	}

	@Override
	public void onHerPositionChange(Point position) {
		setHeadPosition(vHerHead, position);

	}

	/**
	 * 在轨迹计算完成之后运行
	 * 
	 * @param l
	 */
	private void runAfterTraceComplete(OnTraceCompleteListener l) {
		listeners.add(l);
	}

	/**
	 * 设置头像控件为隐藏状态
	 */
	public void setHeadViewsGone() {
		vHerHead.setVisibility(View.GONE);
		vMyHead.setVisibility(View.GONE);
	}

	/**
	 * 设置星球图片
	 * 
	 * @param bgId
	 */
	public void setStarBg(int bgId) {
		imgStar.setImageResource(bgId);
	}

	/**
	 * 设置剩余距离控件为隐藏状态
	 */
	public void setSurpDistanceViewGone() {
		txtSurpDistance.setVisibility(View.GONE);
	}

	/**
	 * 设置追到ta之后的状态
	 */
	public void setChaseHerSuccessStatus() {
		setHeadViewsGone();
		linFinalHead.setVisibility(View.VISIBLE);
		setSurpDistanceViewGone();
		setStarBg(R.drawable.icon_star_final);

	}
}
