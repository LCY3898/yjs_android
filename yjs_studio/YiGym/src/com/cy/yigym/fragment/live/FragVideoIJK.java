package com.cy.yigym.fragment.live;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.widget.VideoView;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.cy.widgetlibrary.WidgetUtils;
import com.efit.sport.R;
import com.efit.sport.livecast.DensityUtil;
import com.efit.sport.livecast.LightnessController;
import com.efit.sport.livecast.VolumnController;

/**
 * Caiyuan Huang
 * <p>
 * 2015-10-17
 * </p>
 * <p>
 * IJK方案直播视频界面
 * </p>
 */
public class FragVideoIJK extends FragVideoBase {
	private VideoView mVideo;
	private OnDelayCallbacl startPlayDelay;
	// 音频管理器
	private AudioManager mAudioManager;
	// 声音调节Toast
	private VolumnController volumnController;
	// 屏幕宽高
	private float width;
	private float height;
	// 原始屏幕亮度
	private int orginalLight;
	// 触摸控制
	private float mLastMotionX;
	private float mLastMotionY;
	private int startX;
	private int startY;

	private int threshold = WidgetUtils.dpToPx(18);
	private boolean isClick = true;

	@Override
	public void startPlay(final String videoUri) {
		if (TextUtils.isEmpty(videoUri))
			return;
		if (mVideo == null) {
			startPlayDelay = new OnDelayCallbacl() {
				@Override
				public void onDelay() {
					startPlayInternal(videoUri);
				}
			};
		} else
			startPlayInternal(videoUri);

	}

	/**
	 * 播放的真正实现
	 * 
	 * @param videoUri
	 */
	private void startPlayInternal(String videoUri) {
		mVideo.setVideoLayout(VideoView.VIDEO_LAYOUT_ZOOM);
		mVideo.setVideoPath(videoUri);
		mVideo.requestFocus();
		mVideo.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(IMediaPlayer mp) {
				if (onVideoProgressListener != null)
					onVideoProgressListener.onStartPlay();

			}
		});
		mVideo.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(IMediaPlayer iMediaPlayer) {
				mVideo.stopPlayback();
				if (onVideoProgressListener != null)
					onVideoProgressListener.onCompletePlay();
			}
		});
		mVideo.setOnTouchListener(mTouchListener);
		mVideo.setOnInfoListener(mInfoListner);
	}

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (onTouchListener != null)
				onTouchListener.onTouch(v, event);
			final float x = event.getX();
			final float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = x;
				mLastMotionY = y;
				startX = (int) x;
				startY = (int) y;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = x - mLastMotionX;
				float deltaY = y - mLastMotionY;
				float absDeltaX = Math.abs(deltaX);
				float absDeltaY = Math.abs(deltaY);
				// 声音调节标识
				boolean isAdjustAudio = false;
				if (absDeltaX > threshold && absDeltaY > threshold) {
					if (absDeltaX < absDeltaY) {
						isAdjustAudio = true;
					} else {
						isAdjustAudio = false;
					}
				} else if (absDeltaX < threshold && absDeltaY > threshold) {
					isAdjustAudio = true;
				} else if (absDeltaX > threshold && absDeltaY < threshold) {
					isAdjustAudio = false;
				} else {
					return true;
				}
				if (isAdjustAudio) {
					if (x < mVideo.getWidth() / 2) {
						if (deltaY > 0) {
							lightDown(absDeltaY);
						} else if (deltaY < 0) {
							lightUp(absDeltaY);
						}
					} else {
						if (deltaY > 0) {
							volumeDown(absDeltaY);
						} else if (deltaY < 0) {
							volumeUp(absDeltaY);
						}
					}

				} else {
				}
				mLastMotionX = x;
				mLastMotionY = y;
				break;
			case MotionEvent.ACTION_UP:
				if (Math.abs(x - startX) > threshold
						|| Math.abs(y - startY) > threshold) {
					isClick = false;
				}
				mLastMotionX = 0;
				mLastMotionY = 0;
				startX = (int) 0;
				if (isClick) {
					// showOrHide();
				}
				isClick = true;
				break;

			default:
				break;
			}
			return true;
		}

	};

	@Override
	public void stopPlay() {
		mVideo.stopPlayback();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_video_ijk;
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return false;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mVideo = findViewById(R.id.vVideo);
		mVideo.setOnPreparedListener(mPreparedListener);
		mVideo.setOnErrorListener(mErrorListener);
	}

	private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {

		@Override
		public void onPrepared(IMediaPlayer arg0) {
			Log.d("tttt", "Success____arg0" + arg0);
		}
	};

	private IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {

		@Override
		public boolean onError(IMediaPlayer player, int what, int extra) {
			Log.i("VideoDy", "on error,what:" + what + " extra:" + extra);
			if (playerListener == null) {
				return false;
			}
			if (extra == IMediaPlayer.MEDIA_ERROR_IO) {
				playerListener.onNetworkError(extra);
			} else if (extra == IMediaPlayer.MEDIA_ERROR_TIMED_OUT
					|| extra == IMediaPlayer.MEDIA_ERROR_UNSUPPORTED
					|| extra == IMediaPlayer.MEDIA_ERROR_SERVER_DIED) {
				playerListener.onServerError();
			} else if (extra == IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
				playerListener.onUriAddrError();
			}
			return true;
		}
	};

	private IMediaPlayer.OnInfoListener mInfoListner = new IMediaPlayer.OnInfoListener() {
		@Override
		public boolean onInfo(IMediaPlayer mp, int what, int extra) {
			if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
				playerListener.onBuffering(true);
			} else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
				playerListener.onBuffering(false);
			}
			return true;
		}
	};

	@Override
	protected void initData(Bundle savedInstanceState) {
		orginalLight = LightnessController.getLightness(mActivity);
		mAudioManager = (AudioManager) mActivity
				.getSystemService(Context.AUDIO_SERVICE);
		volumnController = new VolumnController(mActivity);
		if (startPlayDelay != null)
			startPlayDelay.onDelay();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			height = DensityUtil.getWidthInPx(mActivity);
			width = DensityUtil.getHeightInPx(mActivity);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			width = DensityUtil.getWidthInPx(mActivity);
			height = DensityUtil.getHeightInPx(mActivity);
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onPause() {
		super.onPause();
		LightnessController.setLightness(mActivity, orginalLight);
	}

	/**
	 * 降低音量
	 * 
	 * @param delatY
	 */
	private void volumeDown(float delatY) {
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int down = (int) (delatY / height * max * 3);
		int volume = Math.max(current - down, 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		int transformatVolume = volume * 100 / max;
		volumnController.show(transformatVolume);
	}

	/**
	 * 提高音量
	 * 
	 * @param delatY
	 */
	private void volumeUp(float delatY) {
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int up = (int) ((delatY / height) * max * 3);
		int volume = Math.min(current + up, max);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		int transformatVolume = volume * 100 / max;
		volumnController.show(transformatVolume);
	}

	/**
	 * 降低亮度
	 * 
	 * @param delatY
	 */
	private void lightDown(float delatY) {
		int down = (int) (delatY / height * 255 * 3);
		int transformatLight = LightnessController.getLightness(mActivity)
				- down;
		LightnessController.setLightness(mActivity, transformatLight);
	}

	/**
	 * 提高亮度
	 * 
	 * @param delatY
	 */
	private void lightUp(float delatY) {
		int up = (int) (delatY / height * 255 * 3);
		int transformatLight = LightnessController.getLightness(mActivity) + up;
		LightnessController.setLightness(mActivity, transformatLight);
	}

}
