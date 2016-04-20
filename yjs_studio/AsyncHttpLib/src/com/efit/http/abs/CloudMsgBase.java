package com.efit.http.abs;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;


/**
 * 所有云端请求的基类(abstract ND-cloud message)
 * 每一个云端请求都以此为基类
 * @author nd
 *
 */
public abstract class CloudMsgBase {
	public static char SLASH = File.separatorChar;

	private final static String TAG = CloudMsgBase.class.getSimpleName();
	private static int sSeqId = 1;


	public int reqSeqId = 0;

	public CloudMsgBase() {
	}
	
	
	/**
	 * 请求返回的对象类型
	 * @return
	 */
	public abstract Class<? extends RspMsgBase> getRspDataType();	
	
	/**
	 * 云端返回的数据是否为列表,根据协议文档由用户指定
	 * 若为列表则用户需自行解析数据
	 * @return true：返回的数据为列表,格式为: [{..},{..},..]; 
	 * 		   false:返回的数据为单个对象,格式为: {..};
	 * */
	public boolean isRspDataList() {
		return false;
	}
	

	public abstract String getUri();
	public abstract String getMethod();

	protected String getBaseUrl() {
		return "";
	}
	
	private String getFullUrl() {
		StringBuilder url = new StringBuilder(getBaseUrl());
		int lastIndex = url.length() - 1;
		if (url.charAt(lastIndex) == SLASH) {
			url.deleteCharAt(lastIndex);
		}
		String uri = getUri();
		if (!TextUtils.isEmpty(uri)) {
			if (uri.charAt(0) != SLASH) {
				url.append(SLASH);
			}
			url.append(uri);
		}
		return url.toString();
	}
	
	
	private JSONObject getSystemInfo() {
		JSONObject json = new JSONObject();
		return json;
	}
	
	private JSONObject getRequsetInfo() {
		JSONObject json = new JSONObject();

		return json;
	}
	
	private int getSeqId() {
		if(sSeqId == Integer.MAX_VALUE) {
			sSeqId = 0;			
		}
		sSeqId++;
		reqSeqId = sSeqId;
		return sSeqId;
	}
	


}
