package com.cy.yigym.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.cy.ble.BLEManager;
import com.cy.yigym.event.EventBleConnect;
import com.hhtech.utils.LogToFile;

import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * author: tangtt
 * <p>
 * create at 2015/7/20
 * </p>
 * <p>
 * ble连接检测和数据收集
 * </p>
 */
public class BleConnect {

    //数据格式：0X51+0X1B+数据长度+数据

	/*	蓝牙从机发送数据包：
	0X511B0400000BB8
	0X51表示数据头
	0X1B表示数据头
	04表示数据长度
	0000 预留，以后用
	0BB8表示车轮转一圈的时间T
	0BB8是十六进制，转换成十进制3000。
	车轮转一圈的时间t=T乘以0.001=3(s)
	假设车轮周长设为D=1.8m
	V=D/t=0.6m/s*/

    //数传功能 UUID: 0X1002
    //特征 UUID 是 0X1002
    //IBeacon UUID 数据格式是 AAF1+16 位 IBeacon UUID
    //IBeacon Major与Minor数据格式是AAF2+ 1AFF4C000215 （ 6字节固定数据） +2字节 （Mayjor)+2
    //字节（Minor)+1 字节（功率）+1 字节（电量）


/*	模块服务 UUID：0X1000
	特征 UUID1:0X1001 作用：主要用于手机设置模块的(RTC、PWM、IO、蓝牙连接间隔、
	蓝牙广播间隔）等信息设置
	特征 UUID2:0X1002 作用：主要用于用户数据传输（就是数传功能通道）*/

	/*IBeacon Major与Minor数据格式是AAF2+ 1AFF4C000215 （ 6字节固定数据） +2字节 （Mayjor)+2
	字节（Minor)+1 字节（功率）+1 字节（电量）*/

	//private final static String NOTIFY_UUID = "1002";
	private final static String NOTIFY_UUID = "fff4";
	public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
	public final static UUID UUID_HEART_RATE_MEASUREMENT =
			UUID.fromString(HEART_RATE_MEASUREMENT);

	private static BleConnect sInstance;

	private Handler mHandler = new Handler(Looper.getMainLooper());
	public static BleConnect instance() {
		if(sInstance == null) {
			synchronized (BleConnect.class) {
				if(sInstance == null)
					sInstance = new BleConnect();
			}
		}
		return sInstance;
	}

	public static void release (){
		if(sInstance != null) {
			sInstance.fini();
			sInstance = null;
		}
	}

	private BleConnect() {
		init();
	}


	BLEManager bleMgr;

	private boolean isRiding = false;
	private void init() {
		bleMgr = new BLEManager();
	}


	public BLEManager getBleMgr() {
		return bleMgr;
	}

	public void startRide() {
		distance = 0;
		calorie = 0;
		lastReportTime = 0;
		isRiding = true;
	}

	public void pause() {
		isRiding = false;
	}

	public void stopRide() {
		isRiding = false;
	}

	public void resume() {
		isRiding = true;
	}

	public void fini() {

	}

	private String bleAddress;

	private boolean isConnected = false;

	private boolean isConnecting = false;

	public boolean isBleConnected() {
		return isConnected;
	}

	public String getConnectBleAddr() {
		if(isConnected) {
			return bleAddress;
		}
		return null;
	}

	public void resetConnect() {
		bleMgr.closeGatt();
		isConnected = false;
	}

	public void connectBle(String bleAddress) {
		if(isConnecting) {
			return;
		}
		if(TextUtils.isEmpty(bleAddress)) {
			return;
		}

		if(!bleAddress.equals(this.bleAddress)) {
			isConnected = false;
		}
		this.bleAddress = bleAddress;
		bleMgr.setOnBLEDeviceScanCallBack(scanCbk);
		bleMgr.scanDevice(6 * 1000);
		mHandler.removeCallbacks(connectTimeout);
		mHandler.postDelayed(connectTimeout, 20 * 1000);
		LogToFile.i("BleConnect", "connectBle :" + bleAddress);
	}

	private Runnable connectTimeout = new Runnable() {
		@Override
		public void run() {
			EventBus.getDefault().post(new EventBleConnect(false,false));
		}
	};

	Runnable connectRunnable = new Runnable() {
		@Override
		public void run() {
			isConnecting = false;
		}
	};


	public static boolean isBleAddressEqual(String bleAddress,String addr) {
		if(bleAddress == null) {
			return  false;
		}

		if(addr.equalsIgnoreCase(bleAddress)) {
			return true;
		}
		if(!bleAddress.contains(":")) {
			addr = addr.replace(":","");
			return addr.equalsIgnoreCase(bleAddress);
		}
		return  false;
	}

	BLEManager.OnBLEDeviceScanCallBack scanCbk = new BLEManager.OnBLEDeviceScanCallBack() {
		@Override
		public void onBLEDeviceScan(BLEManager.BLEDevice device) {

			String addr = device.device.getAddress();
			LogToFile.i("BleConnect", "start bleAddress:" + bleAddress + "&addr:" + addr);

			if(isBleAddressEqual(bleAddress,addr)) {
				if(isConnected || isConnecting) {
					return;
				}
				bleMgr.stopScanDevice();
				isConnecting = true;
				bleMgr.connectGatt(addr, connectGattCbk);
				mHandler.removeCallbacks(connectRunnable);
				mHandler.postDelayed(connectRunnable,5*1000);
			}

			//IBeaconClass.IBeacon ibeacon = IBeaconClass.fromScanData(device.device,device.rssi,device.scanRecord);

		}
	};

	private BLEManager.onGattConnectCallBack connectGattCbk = new BLEManager.onGattConnectCallBack() {
		@Override
		public void onConnected() {
			isConnecting = false;
			isConnected = true;
			EventBus.getDefault().post(new EventBleConnect(true,true));
			mHandler.removeCallbacks(connectTimeout);
		}

		@Override
		public void onDisConnected() {
			isConnecting = false;
			isConnected = false;
			EventBus.getDefault().post(new EventBleConnect(false,false));
		}

		@Override
		public void onServicesDiscovered(List<BluetoothGattService> services) {
			setNotifyCbk(services);
		}
	};

	private BluetoothGattCharacteristic notifyGattCharacteristic;

	public void setNotifyCbk(List<BluetoothGattService>gattServices) {

		for (BluetoothGattService gattService : gattServices) {

			List<BluetoothGattCharacteristic> gattCharacteristics =
					gattService.getCharacteristics();
			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				String uuid = gattCharacteristic.getUuid().toString();

				if (NOTIFY_UUID.equalsIgnoreCase(ParseUtils.getSimpleUUID(uuid)) &&
						(gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
					bleMgr.setCharacteristicNotification(gattCharacteristic,notificationCbk,true);
					notifyGattCharacteristic = gattCharacteristic;
				}
			}
		}
	}

	private BLEManager.OnCharacteristicChangedCallBack  notificationCbk = new BLEManager.OnCharacteristicChangedCallBack() {
		@Override
		public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
			if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
				int flag = characteristic.getProperties();
				int format = -1;
				if ((flag & 0x01) != 0) {
					format = BluetoothGattCharacteristic.FORMAT_UINT16;
				} else {
					format = BluetoothGattCharacteristic.FORMAT_UINT8;
				}
				final int heartRate = characteristic.getIntValue(format, 1);

			} else {
				String uuid = characteristic.getUuid().toString();
				if (NOTIFY_UUID.equals(ParseUtils.getSimpleUUID(uuid))) {
					dispatchTimeNotify(characteristic);
				}
			}
		}
	};

	private long lastReportTime = 0;
	private double distance = 0;
	private double calorie = 0;
	private double roundPerMin; //rpm 每分钟转数
	private double speedPerHour = 0;
	private int resist = 0;

	private double radius = 0.18;//单位(米) 原为0.2


	private double getPerimeter() {
		return radius * Math.PI * 2;
	}


	/**
	 * 距离，卡路里的计算
	 * 阻力 < 5时，P = 290 / T （T为转一圈所花的时间)
	 * 阻力 > 5时，P = (315 + 16* resist) / T （T为转一圈所花的时间)
	 * w = p1*deltaT1 + p2*deltaT2 + ...+ pn*deltaTn
	 * cal = w * 0.00717
	 * @param timeInMills
	 * 		转动一圈所发的时间（毫秒）
	 */
	private void calcDistance(int timeInMills) {
		long now = System.currentTimeMillis();
		if(timeInMills == 0) { //如果时间为0，表示没有转速，因此要重置reportTime
			lastReportTime = now;
			speedPerHour = 0;
			roundPerMin = 0;
			EventBus.getDefault().post(new EventBleConnect(true,false));
			return;
		} else {
			EventBus.getDefault().post(new EventBleConnect(true,true));
		}

		if(lastReportTime == 0) {
			lastReportTime = now;
			return;
		}

		long millsDiff = now - lastReportTime;//millsDiff即为deltaT

		distance += millsDiff* getPerimeter() / timeInMills ;

		//计算卡路里
		double p;
		if(resist < 5) {
			p = millsDiff * 290.0 / timeInMills / 100;
		} else {
			p = 0.7 * millsDiff * (315.0 + 16* resist) / timeInMills / 100;
		}
		//累加每一小段的卡路里
		calorie += p * 0.00717;

		speedPerHour = 1000 * getPerimeter() * 3.6 / timeInMills;
		roundPerMin = 14550  / timeInMills;//一分钟转数  //4.12 round per one pedal cycle
		lastReportTime = now;
}



    private void dispatchTimeNotify(BluetoothGattCharacteristic characteristic) {
		final byte[] data = characteristic.getValue();
		if (data != null && data.length >= 5) {
			resist = data[4];
		}
		if (data != null && data.length >= 7) {
			int time = (((data[5] & 0xff) << 8) + (data[6] & 0xff)) * 10;
			calcDistance(time);
			Log.d("ble", "ble resist:" + resist + " timeInMills:" + time);
		}
	}

	public double getDistance() {
		return distance;
	}

	/**
	 * @return speedrate in rpm
	 */
	public double getRoundPerMin() {
		return roundPerMin;
	}


	/**
	 * @return speedrate in km/h
	 */
	public double getSpeedPerHour() {
		return speedPerHour;
	}


	/**
	 * @return resistance in percent. eg: 64%
	 */
	public int getResist() {
		return resist;
	}


	public double getCalorie() {
		return calorie;
	}

}
