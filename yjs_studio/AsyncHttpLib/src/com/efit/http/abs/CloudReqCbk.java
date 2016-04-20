package com.efit.http.abs;

/**
 * app与云端通信时采用异步调用方式，每一个请求的结果都会回调CloudReqCbk
 * 返回类型为基础类型，需要强制转化为具体的返回类型
 * 进行云端请求时，如果成功返回(retCode == 0 并且数据格式正确)，则回调 onSuccess
 * 如果返回失败或者数据格式不正确，又或者出现异常都会回调 onFailure
 **/
public abstract class CloudReqCbk {
	/**
	 * 云端成功返回(retCode == 0 并且 数据格式正确)
	 **/
	public abstract void onSuccess(RspMsgBase rspData);
	
	
	/**
	 * 云端返回失败或者出现异常(无效的服务器域名，网络异常，未登录等)
	 **/
	public abstract void onFailure(int code,String errMsg);
	
	/**
	 * 使用线程池来进行http请求，如果同一时间或者很短一段时间内有多个请求，则会使用先进先出队列缓冲
	 * 当轮到本次http请求时，就执行此回调
	 **/
	public void onStart() {
		
	}

}