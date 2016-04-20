package com.danikula.videocache.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

/**
 * Caiyuan Huang
 * <p>
 * 2015-9-18
 * </p>
 * <p>
 * 响应头部数据关联工具类，用于无网络时能够播放缓存视频
 * </p>
 */
public class RspHeaderUtils {
	private static SharedPreferencesUtils sb = new RspHeaderUtils().new SharedPreferencesUtils();

	private class SharedPreferencesUtils {
		public SharedPreferences.Editor mEditor;
		public SharedPreferences mSharedPreferences;

		private SharedPreferencesUtils() {
			mSharedPreferences = VideoCacheInitializer.getAppContext()
					.getSharedPreferences(
							VideoCacheInitializer.getAppContext()
									.getPackageName(), Context.MODE_PRIVATE);
			mEditor = mSharedPreferences.edit();
		}

		public void saveString(String key, String value) {

			mEditor.putString(key, value).commit();
		}

		public String getString(String key, String defValue) {
			return mSharedPreferences.getString(key, defValue);
		}

		/**
		 * 保存对象，object所在的类必须实现{@link Serializable}接口
		 * 
		 * @param key
		 * @param object
		 */
		public void saveObject(String key, Object object) {
			if ((object instanceof Serializable) == false) {
				throw new RuntimeException("请将object所在的类实现Serializable接口");
			}
			ByteArrayOutputStream baos = null;
			ObjectOutputStream oos = null;
			try {
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				String strBase64 = new String(Base64.encode(baos.toByteArray(),
						Base64.DEFAULT));
				saveString(key, strBase64);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					oos.close();
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/**
		 * 获取存储的object
		 * 
		 * @param key
		 * @param defValue
		 * @return 获取成功则返回存储的object，否则返回默认值
		 */
		public Object getObject(String key, Object defValue) {
			Object object = null;
			String strBase64 = getString(key, "");
			if (TextUtils.isEmpty(strBase64)) {
				return defValue;
			}
			byte[] base64 = Base64.decode(strBase64.getBytes(), Base64.DEFAULT);
			ByteArrayInputStream bais = null;
			ObjectInputStream bis = null;
			try {
				bais = new ByteArrayInputStream(base64);
				bis = new ObjectInputStream(bais);
				object = bis.readObject();
				return object;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					bis.close();
					bais.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return defValue;
		}
	}

	/**
	 * 获取响应头部映射
	 * 
	 * @param map
	 */
	public static void setRspHeaderMap(RspHeaderMap map) {
		sb.saveObject("video_cache_rsp_hearder", map);
	}

	/**
	 * 获取响应头部映射
	 * 
	 * @return
	 */
	public static RspHeaderMap getRspHeaderMap() {
		return (RspHeaderMap) sb.getObject("video_cache_rsp_hearder",
				new RspHeaderMap());
	}

	/**
	 * 响应头部数据
	 */
	public static class RspHeaderData implements Serializable {
		public String mime = "";// 媒体类型
		public int length = 0;// 视频总长度

		public RspHeaderData(String mime, int length) {
			super();
			this.mime = mime;
			this.length = length;
		}

	}

	/**
	 * url与响应头部的映射
	 */
	public static class RspHeaderMap implements Serializable {
		public Map<String, RspHeaderData> map = new HashMap<String, RspHeaderData>();
	}
}
