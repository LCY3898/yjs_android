package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by xiaoshu on 16/1/20.
 * <p/>
 * course_id
 * pid
 * start_time          //相对时间，从0起步，这个是按下清除数据的时候 到 固定时间直播开始的时候的间隔
 */
public class ReqClearLiveCourseData extends ReqBase {

    public String obj = "course";
    public String act = "broadcastClearData";

    public String course_id;//课程ID
    public long start_time; //相对时间，从0起步，这个是按下清除数据的时候 到 固定时间直播开始的时候的间隔

    public ReqClearLiveCourseData(String course_id, long start_time) {
        this.course_id = course_id;
        this.pid = DataStorageUtils.getPid();
        this.start_time = start_time;
    }
}
