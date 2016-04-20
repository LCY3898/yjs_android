package com.hhtech.location;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hhtech.base.AppUtils;


/**
 * 百度地图定位功能<br>
 * 示例代码如下:<br>
 *
 * //设置定位监听器
 * 	LocationUtils.instance().setListener(new LocationUtils.OnLocationListener() {
		@Override
		public void onLocation(double longitude, double latitude) {
		Log.i("onLocation", "longitude:" + longitude + "latitude:" + latitude);
		}
	});

 	//请求3000ms(3s)更新一次位置
 	LocationUtils.instance().requestCyclicLocation(3000);

    //停止定位
	 LocationUtils.instance().stopLocation();
 */
public class LocationUtils {

	public static abstract class OnLocationListener {

		/**
		 * @param longitude 经度
		 * @param latitude  纬度
		 */
		public abstract void onLocation(double longitude,double latitude);

		public void onDetailLocation(BDLocation location) {
			String address = location.getAddrStr();

		}
	}


	synchronized public static LocationUtils instance() {
		if (sInstance == null) {
			create(AppUtils.getAppContext());
		}
		return sInstance;
	}

	public static void release() {
		if(sInstance != null) {
			sInstance.fini();
		}
		sInstance = null;
	}

	/**
	 * 设置定位监听器
	 * @param l
	 */
	public void setListener(OnLocationListener l) {
		mListener = l;
	}


	/**
	 * 单次请求
	 * */
	public void requestSingleLocation() {
		mLocator.requestSingleLocation();
	}

	/**
	 * 周期性请求
	 *
	 * @param intervalInMills
	 * 		请求周期，单位为毫秒
	 * */
	public void requestCyclicLocation(int intervalInMills) {
		mLocator.requestCyclicLocation(intervalInMills);
	}


	/**
	 * 停止定位请求
	 */
	public void stopLocation() {
		mLocator.stopLocation();
	}


	/**
	 * 在程序前台后调用
	 */
	public void fini() {
		mInited = false;
		if (mLocator != null) {
			mLocator.fini();
			mLocator = null;
		}
	}



	private static LocationUtils sInstance;

	private Locator mLocator;

	private interface Locator {
		void requestSingleLocation();

		void requestCyclicLocation(int mills);

		void stopLocation();

		void fini();
	}


	private boolean mInited = false;

	private Handler mHandler = new Handler(Looper.getMainLooper());


	private static Context sContext = null;

	private LocationUtils() {

	}


	private static void create(Context context) {
		sContext = context;
		sInstance = new LocationUtils();
		sInstance.init();
	}


	private void init() {
		if (mInited) {
			return;
		}
		mInited = true;
		mLocator = new BaiduMapLocator();
	}



	private OnLocationListener mListener;

	/************************ baidu map location manager *********************/

	private class BaiduMapLocator implements Locator {

		public LocationClient mLocationClient;
		public MyLocationListener mMyLocationListener;

		private boolean inited = false;

		private void initLocation(int timeInMills) {
			mLocationClient = new LocationClient(sContext);
			mMyLocationListener = new MyLocationListener();
			mLocationClient.registerLocationListener(mMyLocationListener);

			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true); // 是否打开GPS
			option.setCoorType("gcj02"); // 设置返回值的坐标类型。
			option.setProdName("yjs"); // 设置产品线名称
			int span = timeInMills;// * 60 * 60;//1000
			option.setScanSpan(span); // 设置定时定位的时间间隔。单位毫秒
			option.setIsNeedAddress(true);//是否需要地址
			mLocationClient.setLocOption(option);
		}

		@Override
		public void requestSingleLocation() {
			stopLocation();
			initLocation(1000 * 60 * 60);
			mLocationClient.getLocOption().setScanSpan(1000 * 60 * 60);
			mHandler.removeCallbacks(mStarterCheck);

			if (!mLocationClient.isStarted()) {
				mLocationClient.start();
				mHandler.postDelayed(mStarterCheck, 500);
			} else {
				mLocationClient.requestLocation();
			}

		}

		@Override
		public void fini() {
			mLocationClient.stop();
			mLocationClient.unRegisterLocationListener(mMyLocationListener);
			mHandler.removeCallbacks(mStarterCheck);
		}

		private Runnable mStarterCheck = new Runnable() {

			@Override
			public void run() {
				if (mLocationClient.isStarted()) {
					mLocationClient.requestLocation();
				} else {
					mHandler.postDelayed(mStarterCheck, 500);
				}
			}
		};

		public class MyLocationListener implements BDLocationListener {

			@Override
			public void onReceiveLocation(BDLocation location) {

				if (mListener != null) {
					if (mListener != null) {
						mListener.onLocation(location.getLatitude(),
								location.getLongitude());
						mListener.onDetailLocation(location);
					}

				}
				// Receive Location
				StringBuffer sb = new StringBuffer(100);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append(" latitude : ");
				sb.append(location.getLatitude());
				sb.append(" lontitude : ");
				sb.append(location.getLongitude());
				String addr = location.getAddrStr();
				Log.i("address","addr:" + addr);
			}
		}

		@Override
		public void requestCyclicLocation(int mills) {
			initLocation(mills);
			// stopLocation();
			// mHandler.removeCallbacks(mStarterCheck);
			mLocationClient.getLocOption().setLocationNotify(true);
			mLocationClient.getLocOption().setScanSpan(mills);

			if (!mLocationClient.isStarted()) {
				mLocationClient.start();
				mHandler.postDelayed(mStarterCheck, 500);
			} else {
				mLocationClient.requestLocation();
			}
		}

		@Override
		public void stopLocation() {
			mHandler.removeCallbacks(mStarterCheck);
			if (mLocationClient != null) {
				mLocationClient.stop();
				mLocationClient = null;
			}
		}

	}

}
