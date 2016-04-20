package com.cy.ble;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-26
 * </p>
 * <p>
 * BLE管理器
 * </p>
 * <p>
 * 所需权限:</br>&ltuses-permission
 * android:name="android.permission.BLUETOOTH"/></br> &ltuses-permission
 * android:name="android.permission.BLUETOOTH_ADMIN"/>
 * </p>
 */
public class BLEManager {
	// 蓝牙管理器
	private BluetoothManager bluetoothManager;
	// 蓝牙适配器
	private BluetoothAdapter bluetoothAdapter;
	// GATT
	private BluetoothGatt bluetoothGatt;
	// 是否正在扫描中
	private boolean isScanning = false;
	private OnBLEDeviceScanCallBack onBLEDeviceScanCallBack;
	private onGattConnectCallBack onGattCallBack;
	private OnReadCharacteristicCallBack onReadCharacteristicCallBack;
	private OnWriteCharacteristicCallBack onWriteCharacteristicCallBack;
	private OnCharacteristicChangedCallBack onCharacteristicChangedCallBack;

	public BLEManager() {
		init();
	}

	/**
	 * BLE设备类
	 */
	public static class BLEDevice {
		public BluetoothDevice device;
		public int rssi;
		public byte[] scanRecord;

		public BLEDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
			super();
			this.device = device;
			this.rssi = rssi;
			this.scanRecord = scanRecord;
		}

	}

	/**
	 * BLE设备扫描回调
	 */
	public static interface OnBLEDeviceScanCallBack {
		void onBLEDeviceScan(BLEDevice device);
	}

	/**
	 * 设置BLE设备扫描回调
	 * 
	 * @param l
	 */
	public void setOnBLEDeviceScanCallBack(OnBLEDeviceScanCallBack l) {
		this.onBLEDeviceScanCallBack = l;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void init() {
		bluetoothManager = (BluetoothManager) BLELibInitializer.getAppContext()
				.getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
	}

	/**
	 * 手机是否支持BLE功能
	 * 
	 * @param context
	 * @return
	 */
	public boolean isSupportBLE() {
		return BLELibInitializer.getAppContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	/**
	 * 蓝牙是否开启
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		if (bluetoothAdapter == null)
			return false;
		return bluetoothAdapter.isEnabled();
	}

	/**
	 * 非强制开启蓝牙，向用户展示开启界面，由用户决定是否开启
	 * 
	 * @param activity
	 *            所在的Activity
	 * @param REQUEST_ENABLE_BT
	 *            开启蓝牙的请求码，结果在OnActivityForResult里面获取
	 * 
	 */
	public void notForceEnable(Activity activity, final int REQUEST_ENABLE_BT) {
		if (bluetoothAdapter == null || activity == null)
			return;
		if (!isEnabled()) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(intent, REQUEST_ENABLE_BT);
		}
	}

	/**
	 * 强制开启蓝牙，不通知用户
	 */
	public void forceEnable() {
		if (bluetoothAdapter == null)
			return;
		if (!isEnabled()) {
			bluetoothAdapter.enable();
		}
	}

	/**
	 * 关闭蓝牙
	 */
	public void disable() {
		if (bluetoothAdapter == null)
			return;
		if (isEnabled()) {
			bluetoothAdapter.disable();
		}
	}

	/**
	 * 扫描蓝牙
	 * 
	 * @param stopScanTimeMillis
	 *            扫描开始后隔多久后关闭扫描蓝牙功能，若为0,则不关闭扫描功能,单位为毫秒
	 */
	public void scanDevice(int stopScanTimeMillis) {
		if (isScanning || bluetoothAdapter == null)
			return;
		Handler mHandler = new Handler();
		if (stopScanTimeMillis != 0)
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					stopScanDevice();
				}
			}, stopScanTimeMillis);
		isScanning = true;
		bluetoothAdapter.startLeScan(mLeScanCallback);
	}

	/**
	 * 蓝牙扫描回调
	 */
	private LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (onBLEDeviceScanCallBack != null)
				onBLEDeviceScanCallBack.onBLEDeviceScan(new BLEDevice(device,
						rssi, scanRecord));
		}
	};

	/**
	 * 停止蓝牙扫描
	 */
	public void stopScanDevice() {
		if (isScanning == false || bluetoothAdapter == null)
			return;
		isScanning = false;
		bluetoothAdapter.stopLeScan(mLeScanCallback);
	}

	/**
	 * 连接GATT
	 * 
	 * @param remoteDevice
	 *            远程设备
	 * @param autoConnect
	 *            是否自动连接，一般设置成false
	 * @param l
	 * @return
	 */
	private void connectGatt(BluetoothDevice remoteDevice, boolean autoConnect,
			BluetoothGattCallback l) {
		if (remoteDevice == null || l == null)
			return;
		bluetoothGatt = remoteDevice.connectGatt(
				BLELibInitializer.getAppContext(), autoConnect, l);
	}

	/**
	 * GATT连接回调
	 */
	public static abstract class onGattConnectCallBack {
		/**
		 * 与远程设备连接建立时回调
		 */
		public abstract void onConnected();

		/**
		 * 与远程设备连接断开时回调
		 */
		public abstract void onDisConnected();

		/**
		 * 发现远程设备的服务时回调
		 * 
		 * @param services
		 */
		public abstract void onServicesDiscovered(
				List<BluetoothGattService> services);

	}

	/**
	 * 读取特征回调
	 */
	public static interface OnReadCharacteristicCallBack {
		/**
		 * 所连接设备的特征值被读取时回调
		 * 
		 * @param characteristic
		 */
		void onCharacteristicRead(BluetoothGattCharacteristic characteristic);
	}

	/**
	 * 写入特征回调
	 */
	public static interface OnWriteCharacteristicCallBack {
		/**
		 * 所连接设备的特征值被写入时回调
		 * 
		 * @param characteristic
		 */
		void onCharacteristicWrite(BluetoothGattCharacteristic characteristic);
	}

	/**
	 * 特征改变回调
	 */
	public static interface OnCharacteristicChangedCallBack {
		/**
		 * 所连接设备的特征值发生改变时回调
		 * 
		 * @param characteristic
		 */
		void onCharacteristicChanged(BluetoothGattCharacteristic characteristic);
	}

	public boolean isSameDevice(String remoteAddress) {
		try {
			if(bluetoothGatt != null) {
				BluetoothDevice device = bluetoothGatt.getDevice();
				return device.getAddress().equalsIgnoreCase(remoteAddress);
			}
		}catch (Exception e) {}
		return false;
	}

	/**
	 * 连接GATT
	 * 
	 * @param remoteDeviceAddress
	 *            远程设备物理地址
	 * @param autoConnect
	 *            是否自动连接，一般设置成false
	 * @param l
	 * @return
	 */
	public void connectGatt(String remoteDeviceAddress, onGattConnectCallBack l) {
		if (TextUtils.isEmpty(remoteDeviceAddress) || bluetoothAdapter == null
				|| l == null)
			return;
		onGattCallBack = l;

		// 连接已经建立，则无须再连接
		if (isSameDevice(remoteDeviceAddress)) {
			bluetoothGatt.connect();
			return;
		}
		if(bluetoothGatt != null) {
			try {
				bluetoothGatt.disconnect();
				bluetoothGatt.close();
			} catch (Exception e) {}
		}
		BluetoothDevice device = null;
		try {
			device = bluetoothAdapter.getRemoteDevice(remoteDeviceAddress);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (device == null)
			return;
		connectGatt(device, false, new BluetoothGattCallback() {
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status,
					int newState) {
				if (newState == BluetoothProfile.STATE_CONNECTED) {
					if (onGattCallBack != null)
						onGattCallBack.onConnected();
					// 连接建立,开始发现服务
					bluetoothGatt.discoverServices();
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					if (onGattCallBack != null)
						onGattCallBack.onDisConnected();
					// 连接断开
					bluetoothGatt = null;
				}

			}

			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				if (status == BluetoothGatt.GATT_SUCCESS
						&& bluetoothGatt != null) {
					List<BluetoothGattService> services = bluetoothGatt
							.getServices();
					if (onGattCallBack != null)
						onGattCallBack.onServicesDiscovered(services);
				}
			}

			@Override
			public void onCharacteristicChanged(BluetoothGatt gatt,
					BluetoothGattCharacteristic characteristic) {
				// 特征值改变回调
				if (onCharacteristicChangedCallBack != null)
					onCharacteristicChangedCallBack
							.onCharacteristicChanged(characteristic);
			}

			@Override
			public void onCharacteristicRead(BluetoothGatt gatt,
					BluetoothGattCharacteristic characteristic, int status) {
				// 特征值读取回调
				if (status == BluetoothGatt.GATT_SUCCESS
						&& onReadCharacteristicCallBack != null) {
					onReadCharacteristicCallBack
							.onCharacteristicRead(characteristic);
				}
			}

			@Override
			public void onCharacteristicWrite(BluetoothGatt gatt,
					BluetoothGattCharacteristic characteristic, int status) {
				// 特征值写入回调
				if (status == BluetoothGatt.GATT_SUCCESS
						&& onWriteCharacteristicCallBack != null) {
					onWriteCharacteristicCallBack
							.onCharacteristicWrite(characteristic);
				}
			}
		});
	}

	/**
	 * 读取特征
	 * 
	 * @param characteristic
	 *            特征
	 * @param callBack
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic,
			OnReadCharacteristicCallBack callBack) {
		if (characteristic == null || callBack == null || bluetoothGatt == null)
			return;
		this.onReadCharacteristicCallBack = callBack;
		bluetoothGatt.readCharacteristic(characteristic);

	}

	/**
	 * 写入特征
	 * 
	 * @param characteristic
	 *            特征
	 * @param callBack
	 */
	public void writeCharacteristic(BluetoothGattCharacteristic characteristic,
			OnWriteCharacteristicCallBack callBack) {
		if (characteristic == null || callBack == null || bluetoothGatt == null)
			return;
		this.onWriteCharacteristicCallBack = callBack;
		bluetoothGatt.writeCharacteristic(characteristic);
	}

	/**
	 * 设置特征改变通知
	 * 
	 * @param characteristic
	 *            特征
	 * @param callBack
	 * @param enable
	 *            是否开启
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic,
			OnCharacteristicChangedCallBack callBack, boolean enable) {
		if (characteristic == null || callBack == null || bluetoothGatt == null)
			return;
		this.onCharacteristicChangedCallBack = callBack;
		bluetoothGatt.setCharacteristicNotification(characteristic, enable);
	}

	/**
	 * 关闭GATT连接
	 */
	public void closeGatt() {
		if (bluetoothGatt == null)
			return;
		bluetoothGatt.close();
		bluetoothGatt = null;
	}
}
