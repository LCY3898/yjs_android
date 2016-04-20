package com.cy.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-26
 * </p>
 * <p>
 * BLE工具类
 * </p>
 */
public class BLEUtils {
	private final static float A = 59;
	private final static float N = 2;

	/**
	 * 根据蓝牙的信号强度计算距离
	 * <p>
	 * 计算公式:d = 10^((abs(RSSI) - A) / (10 * n))
	 * </p>
	 * <p>
	 * RSSI:为蓝牙信号强度，为负值</br>A:发射端和接收端相隔1米时的信号强度</br>n:环境衰减因子
	 * </p>
	 * 
	 * @param rssi
	 *            蓝牙信号强度
	 * @return
	 */
	public static double calculateDistanceByRssi(int rssi) {
		return Math.pow(10, ((Math.abs(rssi) - A) / (10 * N)));
	}

	private static HashMap<Integer, String> charPermissions = new HashMap();
	static {
		charPermissions.put(0, "UNKNOW");
		charPermissions
				.put(BluetoothGattCharacteristic.PERMISSION_READ, "READ");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED,
				"READ_ENCRYPTED");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM,
				"READ_ENCRYPTED_MITM");
		charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE,
				"WRITE");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED,
				"WRITE_ENCRYPTED");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM,
				"WRITE_ENCRYPTED_MITM");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED,
				"WRITE_SIGNED");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM,
				"WRITE_SIGNED_MITM");
	}

	/**
	 * 获取蓝牙特征的权限
	 * 
	 * @param characteristic
	 *            蓝牙属性
	 * @return
	 */
	public static String getCharPermission(
			BluetoothGattCharacteristic characteristic) {
		return getHashMapValue(charPermissions, characteristic.getPermissions());
	}

	private static HashMap<Integer, String> charProperties = new HashMap();
	static {

		charProperties.put(BluetoothGattCharacteristic.PROPERTY_BROADCAST,
				"BROADCAST");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS,
				"EXTENDED_PROPS");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_INDICATE,
				"INDICATE");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_NOTIFY,
				"NOTIFY");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_READ, "READ");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE,
				"SIGNED_WRITE");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE, "WRITE");
		charProperties.put(
				BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
				"WRITE_NO_RESPONSE");
	}

	/**
	 * 获取蓝牙特征的特性
	 * 
	 * @param characteristic
	 *            蓝牙属性
	 * @return
	 */
	public static String getCharPropertie(
			BluetoothGattCharacteristic characteristic) {
		return getHashMapValue(charProperties, characteristic.getProperties());
	}

	private static HashMap<Integer, String> descPermissions = new HashMap();
	static {
		descPermissions.put(0, "UNKNOW");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ, "READ");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED,
				"READ_ENCRYPTED");
		descPermissions.put(
				BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM,
				"READ_ENCRYPTED_MITM");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE, "WRITE");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED,
				"WRITE_ENCRYPTED");
		descPermissions.put(
				BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM,
				"WRITE_ENCRYPTED_MITM");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED,
				"WRITE_SIGNED");
		descPermissions.put(
				BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM,
				"WRITE_SIGNED_MITM");
	}

	public static String getDescPermission(int property) {
		return getHashMapValue(descPermissions, property);
	}

	private static String getHashMapValue(HashMap<Integer, String> hashMap,
			int number) {
		String result = hashMap.get(number);
		if (TextUtils.isEmpty(result)) {
			List<Integer> numbers = getElement(number);
			result = "";
			for (int i = 0; i < numbers.size(); i++) {
				result += hashMap.get(numbers.get(i)) + "|";
			}
		}
		return result;
	}

	/**
	 * 位运算结果的反推函数10 -> 2 | 8;
	 */
	static private List<Integer> getElement(int number) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < 32; i++) {
			int b = 1 << i;
			if ((number & b) > 0)
				result.add(b);
		}

		return result;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
}
