package com.cy.yigym.net.req;

import com.cy.yigym.utils.AppModeHelper;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by ejianshen on 15/10/12.
 * course_id：课程id
 * nick_name：个人昵称
 * profile_fid：头像id
 * voipAccount 容联账号
 * <p/>
 * {"obj":"course","act":"join","course_id":"o14434164595177071094","pid":"o14434944009058489799",
 * "nick_name":"小淑","profile_fid":"f14434030204265060424001","client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqJoinLiveCast extends CourseReqBase {
    public String act = "broadcastJoin";
    public String course_id = "";
    //private String voipAccount="";
    public String nick_name = DataStorageUtils.getUserNickName();
    public String profile_fid = DataStorageUtils.getCurUserProfileFid();
    private long start_time = 0;
    public String accid = "";
    public String token = "";

    public String login_mode = ""; // 20151211 add

    public ReqJoinLiveCast(String course_id, String accid, String token, long start_time) {
        this.course_id = course_id;
        this.pid = DataStorageUtils.getPid();
        this.start_time = start_time;
        this.accid = accid;
        this.token = token;

        this.login_mode = AppModeHelper.getCurrentMode();
    }

}
