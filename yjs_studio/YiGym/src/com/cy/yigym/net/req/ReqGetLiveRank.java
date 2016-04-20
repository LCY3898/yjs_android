package com.cy.yigym.net.req;

import com.cy.yigym.utils.AppModeHelper;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by ejianshen on 15/10/12.
 * course_id：  //课程id
 * pid：个人id
 * <p/>
 * {"obj":"course","act":"getRank","course_id":"o14443672196922369003","pid":"o14444663350296070575","client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqGetLiveRank extends CourseReqBase {
    public String act = "getRank";

    public int pages = 1;
    public int numbers = 100;
    public String course_id = DataStorageUtils.getCourseId();
    public String login_mode = "";


    public ReqGetLiveRank() {
        this.pid = DataStorageUtils.getPid();
        this.login_mode = AppModeHelper.getCurrentMode();
    }

}
