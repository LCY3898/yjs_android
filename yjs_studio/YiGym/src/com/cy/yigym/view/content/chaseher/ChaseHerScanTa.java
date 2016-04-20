package com.cy.yigym.view.content.chaseher;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.fragment.FragmentChaseHer;
import com.cy.yigym.view.content.EventHeadImageView;
import com.efit.sport.R;
import com.hhtech.base.AppUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-15
 * </p>
 * <p>
 * 追她-扫描附近的Ta
 * </p>
 */
public class ChaseHerScanTa extends BaseView {
	@BindView
	private ImageView imgScan;
	@BindView
	private EventHeadImageView imgHead;
	@BindView
	private TextView txtContent;
	private Animation animScan;
	private ImageView imageScan;
	private boolean isFirstShow = true;
	public static final String TEXT_SCAN_TA = "正在搜索附近的Ta......";
	public static final String TEXT_NOT_FIND_TA = "附近暂无好友,请重新调整搜索范围......";

	public ChaseHerScanTa(Context context) {
		super(context);
	}

	public ChaseHerScanTa(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initView() {
		animScan = AnimationUtils.loadAnimation(mContext, R.anim.anim_scan_ta_new);
		setScanTaText();
		imageScan = (ImageView) findViewById(R.id.imgScan);
		imageScan.setVisibility(GONE);

		delayScanAnim = new Runnable() {
			@Override
			public void run() {
				if (isFirstShow) {
					isFirstShow = false;
					imageScan.setVisibility(VISIBLE);
					startScanAnim();
				}
			}
		};

		// FIXME: tangtt 2015/11/21 临时解决追他扫描界面第一次进入时动画抖动问题
		//在initView时界面还没有绘制完成，需要隔一段时间开始动画，否则动画马上启动，则
		//绘制完成时，动画已经走过了一段时间，导致扫描控件启动时剧烈抖动
		//dispatchDraw中完成绘制，此处是为了即使dispatchDraw失效仍然确保动画执行
		AppUtils.runOnUIDelayed(600, delayScanAnim);
		//startScanAnim();
	}

	private Runnable delayScanAnim;

	@Override
	protected int getContentViewId() {
		return R.layout.view_chase_her_scan_ta;
	}

	/**
	 * 显示
	 */
	public void show() {
		this.setVisibility(View.VISIBLE);
	}

	public void dismiss() {
		this.setVisibility(View.GONE);
	}

	/**
	 * 设置内容
	 * 
	 * @param text
	 */
	public void setText(String text) {
		txtContent.setText(text);
	}

	/**
	 * 开始扫描动画
	 */
	public void startScanAnim() {
		imgScan.clearAnimation();
		imgScan.startAnimation(animScan);
	}

	/**
	 * 停止扫描动画
	 */
	public void stopScanAnim() {
		imgScan.clearAnimation();
	}

	/**
	 * 设置正在搜索附近的ta提示文字
	 */
	public void setScanTaText() {
		setText(TEXT_SCAN_TA);
	}

	/**
	 * 设置没有搜索到好友时的提示文字
	 */
	public void setNotFinddTaText() {
		setText(TEXT_NOT_FIND_TA);
	}

	/**
	 * 释放资源
	 */
	public void realse() {
		imgHead.unRegister();
		stopScanAnim();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// FIXME: tangtt 2015/11/21 临时解决追他扫描界面第一次进入时动画抖动问题
		//如果是百度地图第一次加载，则需要等待更长时间，因为百度加载时间较长，会造成动画卡顿
		if(isFirstShow) {
			if(FragmentChaseHer.firstStartSearch) {
				AppUtils.runOnUIDelayed(150, delayScanAnim);
			} else {
				AppUtils.runOnUIDelayed(40, delayScanAnim);
			}
		}
	}
}
