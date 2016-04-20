package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-15
 * </p>
 * <p>
 * 用户修改个人资料
 * </p>
 */
public class ReqUpdatePersonInfo extends PersonReqBase {
	public String act = "updatePersonInfo";
	public String nick_name = "";
	public String profile_fid = "";
	public String sex = "";
	public String height = "";
	public String weight = "";
	public String birth = "";
	public String job = "";
	public String signature = "";
	public String location = "";

	public ReqUpdatePersonInfo(String display_name, String profile_fid,
			String sex, String height, String weight, String dob, String job,
			String signature, String location, String debug) {
		super();
		this.nick_name = display_name;
		this.profile_fid = profile_fid;
		this.sex = sex;
		this.height = height;
		this.weight = weight;
		this.birth = dob;
		this.job = job;
		this.signature = signature;
		this.location = location;
	}

}
