package com.efit.sport.livecast;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

import com.cy.yigym.utils.traffic.TrafficMonitor;

/**
 * 自动全屏的VideoView
 */
public class FullScreenVideoView extends VideoView {

	private int videoWidth;
	private int videoHeight;
	protected VideoPlayerListener errListener;
	private Context context;

	private TrafficMonitor monitor;

	public FullScreenVideoView(Context context) {
		super(context);
		this.context=context;
	}

	public FullScreenVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
	}

	public FullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(videoWidth, widthMeasureSpec);
		int height = getDefaultSize(videoHeight, heightMeasureSpec);
		if (videoWidth > 0 && videoHeight > 0) {
			if (videoWidth * height > width * videoHeight) {
				height = width * videoHeight / videoWidth;
			} else if (videoWidth * height < width * videoHeight) {
				width = height * videoWidth / videoHeight;
			}
		}
		setMeasuredDimension(width, height);

		monitor=new TrafficMonitor(context);
		monitor.setOnTrafficMonitorCallBack(new TrafficMonitor.OnTrafficMonitorCallBack() {
			@Override
			public void onMonitorUidTxBytesPeriod(long bytes) {

			}

			@Override
			public void onMonitorUidRxBytesPeriod(long bytes) {
				Log.i("downLoad", monitor.unitCover(bytes));
			}

			@Override
			public void onMonitorUidBytesPeriod(long bytes) {

			}
		});
		monitor.start(1000);
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}

}
