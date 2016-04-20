package com.efit.http.abs;

/**
 * 云端返回的数据基类
 * 所有继承的类必须有一个空的构造函数(没有定义构造函数，则系统会默认为空构造函数)
 * */
public abstract class RspMsgBase {

    /**
     * 数据解析完成后用户可自行进行处理
     *            
     */
	public void onPostRsp(String rawData) {
		
	}
	
	public static class EmptyRspMsg extends RspMsgBase {
		
	}
	
}