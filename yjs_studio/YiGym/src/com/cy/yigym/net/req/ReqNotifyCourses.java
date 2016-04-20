package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 *
 * Created by ejianshen on 15/10/19.
 * 用户查看直播预告：
 * 输入：
 pid    //个人id
 flag   //天数标识  0、1、2、3
 {"obj":"course","act":"notifyCourses","pid":"o14449953485305719375","flag":"2","client_info":{"clientType":"webapp","userId":null},"debug":1}

 */
public class ReqNotifyCourses extends CourseReqBase {
    private String act="notifyCourses";
    private int flag=0;

    public ReqNotifyCourses(int flag) {
        this.flag = flag;
        this.pid= DataStorageUtils.getPid();
    }
}
