package com.cy.yigym.ble;

import android.bluetooth.BluetoothGattCharacteristic;

public class ParseUtils {
	public static String getPropetyString(BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        StringBuilder sb = new StringBuilder();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
        	sb.append("read ");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
        	sb.append("write ");
        }
        
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
        	sb.append("notify");
        }
        
        return sb.toString();
	}
	
	public static String getSimpleUUID(String uuid) {
		if(uuid != null && uuid.length() > 8) {
			return uuid.substring(4,8);
		}
		return uuid;
	}
	
	public static boolean isReadable(BluetoothGattCharacteristic characteristic) {
		int charaProp = characteristic.getProperties();
		return (charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0;
	}
	
	public static boolean isWriteable(BluetoothGattCharacteristic characteristic) {
		int charaProp = characteristic.getProperties();
		return (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
	}
	
	public static boolean isNotifyable(BluetoothGattCharacteristic characteristic) {
		int charaProp = characteristic.getProperties();
		return (charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0;
	}
}
