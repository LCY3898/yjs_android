package com.cy.yigym.utils.traffic;

import tv.danmaku.ijk.media.widget.VideoView;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.TrafficStats;

/**
 * Caiyuan Huang
 * <p>
 * 2016-1-20
 * </p>
 * <p>
 * 流量监控器,主要用于测试监控视频流量
 * </p>
 */
public class TrafficMonitor {
	private Context mContext;
	private int uid = -1;// context所属的程序的uid
	private MonitorThread monitorThread = null;
	private long period = 0;
	private long preRxBytesByUid = 0;
	private long preTxBytesByUid = 0;
	private OnTrafficMonitorCallBack onTrafficMonitorCallBack;

	public TrafficMonitor(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context 不能为空");
		}
		this.mContext = context;
		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(
					mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
			this.uid = applicationInfo.uid;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始监控
	 * 
	 * @param period
	 *            监控的间隔事件，单位为毫秒
	 */
	public void start(long period) {
		if (monitorThread != null && monitorThread.isRunning()) {
			return;
		}
		this.period = period;
		monitorThread = new MonitorThread();
		monitorThread.start();
	}

	/**
	 * 停止监控
	 */
	public void stop() {
		if (monitorThread == null) {
			return;
		}
		monitorThread.stopMonitor();
		monitorThread = null;
	}

	/**
	 * 监控线程
	 * 
	 */
	private final class MonitorThread extends Thread {
		private boolean isStop = false;

		public boolean isRunning() {
			return !isStop;
		}

		public void stopMonitor() {
			isStop = true;
		}

		@Override
		public void run() {
			super.run();
			while (!isStop) {
				doMonitor();
				try {
					Thread.sleep(period);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 开始监控流量
	 */
	private void doMonitor() {
		long txBytes = getUidTxBytesPeriod();
		long rxBytes = getUidRxBytesPeriod();
		if (onTrafficMonitorCallBack != null) {
			onTrafficMonitorCallBack.onMonitorUidBytesPeriod(txBytes + rxBytes);
			onTrafficMonitorCallBack.onMonitorUidTxBytesPeriod(txBytes);
			onTrafficMonitorCallBack.onMonitorUidRxBytesPeriod(rxBytes);
		}
	}

	/**
	 * 监控每个周期uid所在应用发送的字节数
	 */
	private long getUidTxBytesPeriod() {
		preTxBytesByUid = preTxBytesByUid == 0 ? TrafficStats
				.getUidTxBytes(uid) : preTxBytesByUid;
		long bytes = TrafficStats.getUidTxBytes(uid) - preTxBytesByUid;
		preTxBytesByUid=TrafficStats.getUidTxBytes(uid);
		return bytes;
	}

	/**
	 * 监控每个周期uid所在应用接收的字节数
	 */
	private long getUidRxBytesPeriod() {
		preRxBytesByUid = preRxBytesByUid == 0 ? TrafficStats
				.getUidRxBytes(uid) : preRxBytesByUid;
		long bytes = TrafficStats.getUidRxBytes(uid) - preRxBytesByUid;
		preRxBytesByUid=TrafficStats.getUidRxBytes(uid);
		return bytes;
	}

	/**
	 * 流量监听回调
	 * 
	 */
	public static interface OnTrafficMonitorCallBack {
		/**
		 * 每个周期uid对应的应用发送的字节数
		 * 
		 * @param bytes
		 */
		public void onMonitorUidTxBytesPeriod(long bytes);

		/**
		 * 每个周期uid对应的应用接收的字节数
		 * 
		 * @param bytes
		 */
		public void onMonitorUidRxBytesPeriod(long bytes);

		/**
		 * 每个周期uid对应的应用消耗的字节数
		 * 
		 * @param bytes
		 */
		public void onMonitorUidBytesPeriod(long bytes);
	}

	/**
	 * 设置流量监控监听器
	 * 
	 * @param l
	 */
	public void setOnTrafficMonitorCallBack(OnTrafficMonitorCallBack l) {
		this.onTrafficMonitorCallBack = l;
	}

	/**
	 * 单位转换
	 * 
	 * @param bytes
	 * @return
	 */
	public String unitCover(double bytes) {
		if (bytes < 1024) {
			return String.format("%.2fB", bytes);
		}
		if (bytes < 1024 * 1024) {
			return String.format("%.2fKB", bytes / 1024.0);
		}
		if (bytes < 1024 * 1024 * 1024) {
			return String.format("%.2fMB", bytes / 1024.0 / 1024.0);
		}
		return String.format("%.2fB", bytes);
	}

	/**
	 * 获取视频分辨率
	 * 
	 * @param videoView
	 * @return
	 */
	public String getVideoResolutionRatio(VideoView videoView) {
		if (videoView == null)
			return "获取异常";
		int heiht = videoView.getVideoHeight();
		int width = videoView.getVideoWidth();
		Rect rectCur = new Rect(0, 0, width, heiht);
		Rect rect640p = new Rect(0, 0, 960, 640);
		Rect rect720p = new Rect(0, 0, 1280, 720);
		Rect rect1080p = new Rect(0, 0, 1920, 1080);
		if (rect640p.contains(rectCur)) {
			return "640p";
		}
		if (rect720p.contains(rectCur)) {
			return "720p";
		}
		if (rect1080p.contains(rectCur)) {
			return "1080p";
		}
		return "未知分辨率";
	}
}
