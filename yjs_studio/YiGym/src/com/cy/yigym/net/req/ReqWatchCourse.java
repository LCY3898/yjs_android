package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by ejianshen on 15/11/16.
 * pid：用户id
 * course_id：课程id
 * {"obj":"course","act":"watchCourse","pid":"o14458513848364729881"
 * ,"course_id":"o14466224794646210670","client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqWatchCourse extends CourseReqBase {
    private String act = "watchCourse";
    private String course_id = "";

    public ReqWatchCourse(String course_id) {
        this.course_id = course_id;
        this.pid = DataStorageUtils.getPid();
    }

}
