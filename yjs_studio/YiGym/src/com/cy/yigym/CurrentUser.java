package com.cy.yigym;

import android.content.Context;
import android.content.SharedPreferences;

import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CurrentUser {

	private final static String KEY_USER_INFO = "key_user_info";


	@SerializedName("userid")
	private String userId;

	@SerializedName("user_name")
	private String userName;

	@SerializedName("password")
	private String passwd;

	@SerializedName("is_verified")
	private boolean isVerified;

	private static CurrentUser sUser = null;

	@SerializedName("person_info")
	RspGetUserInfo.PersonInfo personInfo;


	private static SharedPreferences getUserConfig() {
		return AppUtils.getAppContext().getSharedPreferences("user_cfg", Context.MODE_PRIVATE);
	}

	synchronized public static CurrentUser instance() {
		if(sUser == null) {
			String json = getUserConfig().getString(KEY_USER_INFO,"");
			if("".equals(json)) {
				sUser = new CurrentUser();
				sUser.personInfo = new RspGetUserInfo.PersonInfo();
			} else {
				Gson gson = new Gson();
				sUser = gson.fromJson(json, CurrentUser.class);
			}
		}
		return sUser;
	}

	public static  void release() {
		sUser = null;
	}


	public void clearUser() {
		sUser = null;
		getUserConfig().edit().putString(KEY_USER_INFO, "").commit();
	}

	/**
	 * 设置用户id
	 * */
	public void setPid(String uid) {
		this.userId = uid;
		onUserInfoChange();
	}
	public void setUserNickname(String nickname){
		personInfo.nick_name=nickname;
		onUserInfoChange();
	}
	/**
	 * 设置性别
	 * @param sex
	 */
	public void setSex(String sex){
		personInfo.sex=sex;
		onUserInfoChange();
	}
	/**
	 * 设置用户身高
	 * @param height
	 */
	public void setUserHeight(String height) {
		personInfo.height=height;
		onUserInfoChange();
	}

	/**
	 * 设置用户体重
	 * @param weight
	 */
	public void setUserWeight(String weight){
		personInfo.weight=weight;
		onUserInfoChange();
	}

	/**
	 * 设置生日
	 * @param birth
	 */
	public void setUserBirth(String birth){
		personInfo.birth=birth;
		onUserInfoChange();
	}


	/**
	 * 设置用户签名
	 * @param signature
	 */
	public void setUserSign(String signature){
		personInfo.signature=signature;
		onUserInfoChange();
	}

	/**
	 * 设置职业
	 * @param job
	 */
	public void setUserJob(String job){
		personInfo.job=job;
		onUserInfoChange();
	}

	/**
	 *  设置所在地
	 * @param location
	 */
	public void setUserLocation(String location){
		personInfo.location=location;
		onUserInfoChange();
	}

	/**
	 * 设置用户信息
	 * @param personInfo
	 */
	public void setUserInfo(RspGetUserInfo.PersonInfo personInfo){
		this.personInfo=personInfo;
		onUserInfoChange();
	}

	public void setUserName(String userName) {
		this.userName = userName;
		onUserInfoChange();
	}

	/**
	 * 暂时使用明文密码，以后需要改成md5加密，或者使用token验证的形式
	 * */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
		onUserInfoChange();
	}

	/**
	 * 设置是否登录成功过，登录成功置为true,失败置为false
	 * 用户退出登录也需置为false
	 * */
	public void setVerified(boolean verified) {
		this.isVerified = verified;
		onUserInfoChange();
	}


	public String getNickname() {
		if(personInfo != null) {
			return personInfo.nick_name;
		}
		return "";
	}

	public String getUserSign() {
		if(personInfo != null) {
			return personInfo.signature;
		}
		return "";
	}

	public String getUserJob() {
		if(personInfo != null) {
			return personInfo.job;
		}
		return "";
	}



	/**
	 * 获取积分
	 * @return
	 */
	public int getScore(){
		if(personInfo!=null){
			try {
				return Integer.parseInt(personInfo.score);
			}catch (Exception e) {

			}
		}
		return 0;
	}

	/**
	 * 获取用户信息
	 * @return
	 */
	public RspGetUserInfo.PersonInfo getPersonInfo(){
		return this.personInfo;
	}
	/**
	 * 获取用户id
	 * */
	public String getPid() {
		return this.userId;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * 暂时使用明文密码，以后需要改成md5加密，或者使用token验证的形式
	 * */
	public String getPasswd() {
		return passwd;
	}


	/**
	 * 判断是否登录成功过，登录成功过则
	 * 可直接使用当前用户名和密码
	 * */
	public boolean isVerified() {
		return isVerified;
	}

	private void onUserInfoChange() {
		Gson gson = new Gson();
		String json = gson.toJson(this);
		getUserConfig().edit().putString(KEY_USER_INFO, json).commit();
	}

}
