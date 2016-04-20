package com.cy.yigym.fragment.live;

import android.view.View.OnTouchListener;

import com.cy.widgetlibrary.base.BaseFragment;
import com.efit.sport.livecast.VideoPlayerListener;

/**
 * Caiyuan Huang
 * <p>
 * 2015-10-17
 * </p>
 * <p>
 * 视频界面基类
 * </p>
 */
public abstract class FragVideoBase extends BaseFragment {
	protected OnTouchListener onTouchListener;
	protected OnVideoProgressListener onVideoProgressListener;

	protected VideoPlayerListener playerListener;

	/**
	 * 开始播放Video
	 * 
	 * @param videoUri
	 */
	public abstract void startPlay(String videoUri);

	/**
	 * 停止播放Video
	 */
	public abstract void stopPlay();

	/**
	 * 延迟操作接口
	 */
	public interface OnDelayCallbacl {
		void onDelay();
	}

	/**
	 * 设置触摸事件监听
	 * 
	 * @param l
	 */
	public void setOnTouchListener(OnTouchListener l) {
		this.onTouchListener = l;
	}

	/**
	 * 设置视频进度监听
	 * 
	 * @param l
	 */
	public void setOnVideoProgressListener(OnVideoProgressListener l) {
		this.onVideoProgressListener = l;
	}

	/**
	 * 设置播放异常监听
	 *
	 * @param l
	 */
	public void setOnCastEventListener(VideoPlayerListener l) {
		this.playerListener = l;
	}
	/**
	 * 视频进度监听接口
	 */
	public interface OnVideoProgressListener {
		/**
		 * 视频开始播放
		 */
		void onStartPlay();

		/**
		 * 视频完成播放
		 */
		void onCompletePlay();
	}

	public interface PlayInfoRetriver {
		int getPlayTime();//in seconds
		int getDuration();//in seconds
		String getCourseId();
		boolean isJoinSucceed();
	}
}
