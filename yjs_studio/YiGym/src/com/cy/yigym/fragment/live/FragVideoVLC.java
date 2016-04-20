package com.cy.yigym.fragment.live;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.vlc.util.VLCInstance;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-10-17
 * </p>
 * <p>
 * VLC方案播放视频界面
 * </p>
 */
@Deprecated
public class FragVideoVLC extends FragVideoBase implements OnTouchListener {
	// 视频控件相关
	private SurfaceView vSurface;
	private LibVLC libVLC;
	private SurfaceHolder surfaceHolder;
	// 视频尺寸大小相关
	private int mVideoHeight = 1;
	private int mVideoWidth = 1;
	private int mVideoVisibleHeight = 1;
	private int mVideoVisibleWidth = 1;
	private int mSarNum = 1;
	private int mSarDen = 1;
	// 视频尺寸适配模式相关
	private int curSizeMode = SURFACE_BEST_FIT;// 当前的适配模式
	private static final int SURFACE_BEST_FIT = 0;
	private static final int SURFACE_FIT_HORIZONTAL = 1;
	private static final int SURFACE_FIT_VERTICAL = 2;
	private static final int SURFACE_FILL = 3;
	private static final int SURFACE_16_9 = 4;
	private static final int SURFACE_4_3 = 5;
	private static final int SURFACE_ORIGINAL = 6;
	private OnDelayCallbacl delayPlay;

	@Override
	public void startPlay(final String videoUri) {
		if (TextUtils.isEmpty(videoUri))
			return;
		if (libVLC == null) {
			// VLC可能还没有初始化完成，延迟播放
			delayPlay = new OnDelayCallbacl() {

				@Override
				public void onDelay() {
					if (libVLC == null)
						return;
					libVLC.playMRL(videoUri);
					vSurface.setKeepScreenOn(true);
				}
			};
		} else {
			// VCL初始化完成，立即播放
			libVLC.playMRL(videoUri);
			vSurface.setKeepScreenOn(true);
		}
	}

	@Override
	public void stopPlay() {
		if (libVLC == null || vSurface == null)
			return;
		libVLC.stop();
		vSurface.setKeepScreenOn(false);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_video_vlc;
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return false;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		vSurface = findViewById(R.id.vSurface);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		try {
			libVLC = VLCInstance.getLibVlcInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (libVLC == null || vSurface == null || mActivity == null)
			return;
		vSurface.setOnTouchListener(this);
		surfaceHolder = vSurface.getHolder();
		surfaceHolder.setFormat(PixelFormat.RGBX_8888);
		surfaceHolder.addCallback(surfaceCbk);
		libVLC.eventVideoPlayerActivityCreated(true);
		EventHandler.getInstance().addHandler(handlerVLC);
		mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		if (delayPlay != null)
			delayPlay.onDelay();
	}

	private Handler handlerVLC = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg == null || msg.getData() == null)
				return;
			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaPlayerTimeChanged:
				break;
			case EventHandler.MediaPlayerPositionChanged:
				break;
			case EventHandler.MediaPlayerPlaying:
				if (onVideoProgressListener != null)
					onVideoProgressListener.onStartPlay();
				break;
			case EventHandler.MediaPlayerBuffering:
				break;
			case EventHandler.MediaPlayerLengthChanged:
				break;
			case EventHandler.MediaPlayerEndReached:
				// 播放完成
				break;
			}

		}
	};

	/**
	 * 根据当前的适配模式改变大小
	 */
	private void chageSurfaceSize() {
		if (mActivity == null || surfaceHolder == null || vSurface == null)
			return;
		// get screen size
		int dw = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		int dh = mActivity.getWindowManager().getDefaultDisplay().getHeight();
		// calculate aspect ratio
		double ar = (double) mVideoWidth / (double) mVideoHeight;
		// calculate display aspect ratio
		double dar = (double) dw / (double) dh;
		switch (curSizeMode) {
		case SURFACE_BEST_FIT:
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_FIT_HORIZONTAL:
			dh = (int) (dw / ar);
			break;
		case SURFACE_FIT_VERTICAL:
			dw = (int) (dh * ar);
			break;
		case SURFACE_FILL:
			break;
		case SURFACE_16_9:
			ar = 16.0 / 9.0;
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_4_3:
			ar = 4.0 / 3.0;
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_ORIGINAL:
			dh = mVideoHeight;
			dw = mVideoWidth;
			break;
		}
		surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
		ViewGroup.LayoutParams lp = vSurface.getLayoutParams();
		lp.width = dw;
		lp.height = dh;
		vSurface.setLayoutParams(lp);
		vSurface.invalidate();
	}

	private IVideoPlayer iVideoPlayer = new IVideoPlayer() {

		@Override
		public void setSurfaceSize(int width, int height, int visible_width,
				int visible_height, int sar_num, int sar_den) {
			mVideoHeight = height;
			mVideoWidth = width;
			mVideoVisibleHeight = visible_height;
			mVideoVisibleWidth = visible_width;
			mSarNum = sar_num;
			mSarDen = sar_den;
			chageSurfaceSize();
		}
	};
	private SurfaceHolder.Callback surfaceCbk = new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (libVLC == null)
				return;
			surfaceHolder = holder;
			libVLC.attachSurface(holder.getSurface(), iVideoPlayer);

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (libVLC == null)
				return;
			surfaceHolder = holder;
			libVLC.attachSurface(holder.getSurface(), iVideoPlayer);
			if (width > 0 && height > 0) {
				mVideoHeight = height;
				mVideoWidth = width;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (libVLC == null)
				return;
			libVLC.detachSurface();

		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (libVLC == null)
			return;
		iVideoPlayer.setSurfaceSize(mVideoWidth, mVideoHeight,
				mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (libVLC != null) {
			libVLC.stop();
			vSurface.setKeepScreenOn(false);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (libVLC == null)
			return;
		libVLC.eventVideoPlayerActivityCreated(false);
		EventHandler.getInstance().removeHandler(handlerVLC);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (onTouchListener != null)
			onTouchListener.onTouch(v, event);
		return false;
	}
}
