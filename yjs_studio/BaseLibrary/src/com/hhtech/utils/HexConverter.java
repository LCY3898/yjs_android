package com.hhtech.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.os.MemoryFile;

/**
 * 
 */

/**
 * @author tangtt
 * 各种转换 16进制数值和字符串的相互转化，byte和int相互转化等
 *
 */
public class HexConverter {

    /**
     * The digits for every supported radix.
     */
    private static final char[] DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final char[] UPPER_CASE_DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    
    /**
     * Convert char to byte<br>
     * @param c char
     * @return byte 
     */
     private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    
     
    /**
     * byte数据转化为字符串<br>
     * 例如 0xA5变为字符串 "A5"
     * @param b
     * 		要转换的byte数据
     * @param upperCase
     * 		指定String大小写类型，true为大写
     * @return
     * 		转化后的字符串数据
     */
    public static String byteToHexString(byte b, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        char[] buf = new char[2]; // We always want two digits.
        buf[0] = digits[(b >> 4) & 0xf];
        buf[1] = digits[b & 0xf];
        return new String(buf);
    }
    
    /**
     * byte数据转化为字符串<br>
     * 例如 bytes = {0x14,0xa5}， 输出为字符串 "14A5"
     * @param bytes
     * 		要转换的byte数组
     * @param upperCase
     * 		指定String大小写类型，true为大写
     * @return
     * 		转化后的字符串
     */
    public static String bytesToHexString(byte[] bytes, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        char[] buf = new char[bytes.length * 2];
        int c = 0;
        for (byte b : bytes) {
            buf[c++] = digits[(b >> 4) & 0xf];
            buf[c++] = digits[b & 0xf];
        }
        return new String(buf);
    }
    
    /**
     * Convert hex string to byte[]<br>
     * 把为字符串转化为字节数组， 例如 字符串 为"14A5" 则输出byte数组： {0x14,0xa5}
     * 如果出现非法字符如 (G-Z,g-z)则会出现异常
     * @param hexString 
     * 		the hex string
     * @return byte[]
     * 		
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexString.charAt(pos)) << 4 | charToByte(hexString.charAt(pos+1)));
        }
        return d;
    }

    
	/**
	 * 将字符串转化为16进制数据保存<br>
	 * 例如"add"对应byte 数组为{0x61,0x64,0x64},则输出为"616464"
	 * @param bin
	 * @return
	 */
	public static String bin2hex(String bin) {
	    char[] digital = "0123456789ABCDEF".toCharArray();
	    StringBuffer sb = new StringBuffer("");
	    byte[] bs = bin.getBytes();
	    int bit;
	    for (int i = 0; i < bs.length; i++) {
	        bit = (bs[i] & 0xf0) >> 4;
	        sb.append(digital[bit]);
	        bit = bs[i] & 0x0f;
	        sb.append(digital[bit]);
	    }
	    return sb.toString();
	}

	/**
	 * 将十六进制的两个字节合并为一个十进制字节<br>
	 * 例如 输入为 {0x5,0x8},则输出为{88}(即 5 * 16 + 8)
	 * @param b
	 * @return
	 */
	public static byte[] hex2byte(byte[] b) {
	    if ((b.length % 2) != 0) {
	        throw new IllegalArgumentException("length should be even number");
	    }
	    byte[] b2 = new byte[b.length / 2];
	    for (int n = 0; n < b.length; n += 2) {
	        String item = new String(b, n, 2);
	        // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个十进制字节
	        b2[n / 2] = (byte) Integer.parseInt(item, 16);
	    }
	    b = null;
	    return b2;
	}
	
	
    /**
	 * 将byte数组头4个字节转化为int，采用BIG_ENDIAN对齐
	 * 
	 * @param src
	 * @return
	 */
	public static int byteArrayToInt(byte[] src) {
		return (((src[0] & 0xff) << 24) 
				| ((src[1] & 0xff) << 16)
				| ((src[2] & 0xff) << 8) 
				| ((src[3] & 0xff) << 0));
	}

	/**
	 * 将byte数组头8个字节转化为long，采用BIG_ENDIAN对齐
	 * 
	 * @param src
	 * @return
	 */
	public static long byteArrayToLong(byte[] src) {

		int h = (((src[0] & 0xff) << 24) 
				| ((src[1] & 0xff) << 16)
				| ((src[2] & 0xff) << 8) 
				| ((src[3] & 0xff) << 0));
		
		int l = (((src[4] & 0xff) << 24) 
				| ((src[5] & 0xff) << 16)
				| ((src[6] & 0xff) << 8) 
				| ((src[7] & 0xff) << 0));
		return (((long) h) << 32L) | ((long) l) & 0xffffffffL;
	} 
      
    /**
     * 将byte数组头两个字节转化为short，采用BIG_ENDIAN对齐
     * @param src
     * @return
     */
    public static short byteArrayToShort(byte[] src) { 
    	  return (short) (((src[0] & 0xff) << 8) | (src[1] & 0xff)); //BIG_ENDIAN
    	  //return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getShort();
    }
      
      public static final byte[] intToByteArray(int value) {
    	    return new byte[] {
    	            (byte)((value >>> 24) & 0xFF),
    	            (byte)((value >>> 16) & 0xFF),
    	            (byte)((value >>> 8) & 0xFF),
    	            (byte)(value & 0xFF)};
    	}
      
      public static final byte[] intToByteArrayLittleEndian(int value) {
  	    return new byte[] {
  	    		(byte)(value & 0xFF),
  	    		(byte)((value >>> 8) & 0xFF),
  	            (byte)((value >>> 16) & 0xFF),  	            
  	            (byte)((value >>> 24) & 0xFF)
  	            };
  	}
      
      
      
     public static final byte[] shortToByteArray(short value) {
  	    return new byte[] {
  	            (byte)((value >>> 8) & 0xFF),
  	            (byte)(value & 0xFF)};
  	  }
      
     public static final byte[] shortToByteArray(short value,ByteOrder order) {
    	 if(order == ByteOrder.BIG_ENDIAN) {
    		 return new byte[] {
     	            (byte)((value >>> 8) & 0xFF),
     	            (byte)(value & 0xFF)}; 
    	 } else {
    		return  new byte[] {
    				(byte)(value & 0xFF),
     	            (byte)((value >>> 8) & 0xFF)
     	            };
    	 }    	    
    }
  	public static byte[] byteMerger(byte[] lhs, byte[] rhs) {
		byte[] merged = new byte[lhs.length + rhs.length];
		System.arraycopy(lhs, 0, merged, 0, lhs.length);
		System.arraycopy(rhs, 0, merged, lhs.length, rhs.length);
		return merged;
	}
  	
    /**
     * convert byte array to int
     * @param src
     * 		the input byte array
     * @param offset
     * 		start offset for byte array
     * @param order
     * 		big_endian for network sequence
     * @return
     * 		the converted int value
     */
  	public static int getInt(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (((src[offset++] & 0xff) << 24) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset  ] & 0xff) <<  0));
        } else {
            return (((src[offset++] & 0xff) <<  0) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset  ] & 0xff) << 24));
        }
    }

  	
    /**
     * convert byte array to long
     * @param src
     * 		the input byte array
     * @param offset
     * 		start offset for byte array
     * @param order
     * 		big_endian for network sequence
     * @return
     * 		the converted long value
     */
    public static long getLong(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int h = ((src[offset++] & 0xff) << 24) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset++] & 0xff) <<  0);
            int l = ((src[offset++] & 0xff) << 24) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset  ] & 0xff) <<  0);
            return (((long) h) << 32L) | ((long) l) & 0xffffffffL;
        } else {
            int l = ((src[offset++] & 0xff) <<  0) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset++] & 0xff) << 24);
            int h = ((src[offset++] & 0xff) <<  0) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset  ] & 0xff) << 24);
            return (((long) h) << 32L) | ((long) l) & 0xffffffffL;
        }
    }

    
    
    /**
     * convert byte array to short
     * @param src
     * 		the input byte array
     * @param offset
     * 		start offset for byte array
     * @param order
     * 		big_endian for network sequence
     * @return
     * 		the converted short value
     */
    public static short getShort(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (short) ((src[offset] << 8) | (src[offset + 1] & 0xff));
        } else {
            return (short) ((src[offset + 1] << 8) | (src[offset] & 0xff));
        }
    }

    /**
     * convert int to byte array
     * @param dst
     * 		output byte array
     * @param offset
     * 		start offset for byte array
     * @param value
     * 		int value to convert
     * @param order
     * 		big_endian for network sequence
     */
    public static void putInt(byte[] dst, int offset, int value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            dst[offset++] = (byte) ((value >> 24) & 0xff);
            dst[offset++] = (byte) ((value >> 16) & 0xff);
            dst[offset++] = (byte) ((value >>  8) & 0xff);
            dst[offset  ] = (byte) ((value >>  0) & 0xff);
        } else {
            dst[offset++] = (byte) ((value >>  0) & 0xff);
            dst[offset++] = (byte) ((value >>  8) & 0xff);
            dst[offset++] = (byte) ((value >> 16) & 0xff);
            dst[offset  ] = (byte) ((value >> 24) & 0xff);
        }
    }

    /**
     * convert long to byte array
     * @param dst
     * 		output byte array
     * @param offset
     * 		start offset for byte array
     * @param value
     * 		long value to convert
     * @param order
     * 		big_endian for network sequence
     */
    public static void putLong(byte[] dst, int offset, long value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int i = (int) (value >> 32);
            dst[offset++] = (byte) ((i >> 24) & 0xff);
            dst[offset++] = (byte) ((i >> 16) & 0xff);
            dst[offset++] = (byte) ((i >>  8) & 0xff);
            dst[offset++] = (byte) ((i >>  0) & 0xff);
            i = (int) value;
            dst[offset++] = (byte) ((i >> 24) & 0xff);
            dst[offset++] = (byte) ((i >> 16) & 0xff);
            dst[offset++] = (byte) ((i >>  8) & 0xff);
            dst[offset  ] = (byte) ((i >>  0) & 0xff);
        } else {
            int i = (int) value;
            dst[offset++] = (byte) ((i >>  0) & 0xff);
            dst[offset++] = (byte) ((i >>  8) & 0xff);
            dst[offset++] = (byte) ((i >> 16) & 0xff);
            dst[offset++] = (byte) ((i >> 24) & 0xff);
            i = (int) (value >> 32);
            dst[offset++] = (byte) ((i >>  0) & 0xff);
            dst[offset++] = (byte) ((i >>  8) & 0xff);
            dst[offset++] = (byte) ((i >> 16) & 0xff);
            dst[offset  ] = (byte) ((i >> 24) & 0xff);
        }
    }

    /**
     * convert short to byte array
     * @param dst
     * 		output byte array
     * @param offset
     * 		start offset for byte array
     * @param value
     * 		short value to convert
     * @param order
     * 		big_endian for network sequence
     */
    public static void putShort(byte[] dst, int offset, short value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            dst[offset++] = (byte) ((value >> 8) & 0xff);
            dst[offset  ] = (byte) ((value >> 0) & 0xff);
        } else {
            dst[offset++] = (byte) ((value >> 0) & 0xff);
            dst[offset  ] = (byte) ((value >> 8) & 0xff);
        }
    }

}
