package com.efit.sport.livecast;

import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.yigym.fragment.live.FragVideoBase;
import com.efit.sport.R;

public class AtyVideoPlayer extends BaseFragmentActivity implements
		OnClickListener, OnTouchListener {
	// 头部View
	private View mTopView;
	// private String videoUrl = "rtmp://wspub.live.hupucdn.com/prod/slk";
	// private String videoUrl = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
	// private String videoUrl = "http://streaming.51efit.com/hls/test2.m3u8";
	// private String videoUrl ="rtmp://streaming.51efit.com/live/test16";
	//private String videoUrl = "http://streaming.51efit.com/hls/test3.m3u8";
	private String videoUrl ="http://121.40.16.113:8081/cgi-bin/download.pl?fid=f14453189657512300014001";
	//private String videoUrl ="http://121.40.16.113:8081/cgi-bin/download.pl?fid=f14453163635573029518001";
	//private String videoUrl ="rtmp://121.40.16.113:1935/live/test16";
	// 自动隐藏顶部和底部View的时间
	private static final int HIDE_TITLE_DELAY_TIME = 5000;
	private boolean isExecAnim = false;
	private FragVideoBase fragVideo;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_mediaplayer_test;
	}

	@Override
	protected void initView() {
		getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
		mTopView = findViewById(R.id.top_layout);
	}

	@Override
	protected void initData() {
		fragVideo = (FragVideoBase) mFragmentManager
				.findFragmentById(R.id.fragVideo);
		fragVideo.startPlay(videoUrl);
		fragVideo.setOnTouchListener(this);
		execTipsAnimation();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_btn:
			break;
		default:
			break;
		}
	}

	/**
	 * 执行提示控件的动画
	 */
	private void execTipsAnimation() {
		if (isExecAnim)
			return;
		if (mTopView.getVisibility() == View.VISIBLE) {
			mTopView.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(mActivity,
					R.anim.option_leave_from_top);
			animation.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mTopView.setVisibility(View.GONE);
				}
			});
			mTopView.startAnimation(animation);

		} else {
			mTopView.setVisibility(View.VISIBLE);
			mTopView.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.option_entry_from_top);
			mTopView.startAnimation(animation);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					execTipsAnimation();
				}
			}, HIDE_TITLE_DELAY_TIME);
		}
	}

	private class AnimationImp implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			isExecAnim = false;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
			isExecAnim = true;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			execTipsAnimation();
			break;
		}
		return false;
	}

}
