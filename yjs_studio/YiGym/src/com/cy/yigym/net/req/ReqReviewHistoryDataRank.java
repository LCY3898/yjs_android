package com.cy.yigym.net.req;

/**
 * Created by ejianshen on 15/11/9. 往期课程排名
 * pid             //用户id
 course_id       //课程id
 time_stamp      //相对时间点                 单位秒
 interval_time   //每次上传数据的时间间隔     单位秒

 输入：{"obj":"course","act":"reviewHistoryDataRank","course_id":"o14470444509698419570",
 "pid":"o14448292223981139659","interval_time":5,
 "time_stamp":30,"client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqReviewHistoryDataRank extends CourseReqBase {
    private String act="reviewHistoryDataRank";
    private String course_id;
    private long interval_time=0;
    private long time_stamp=0;

    public ReqReviewHistoryDataRank(String course_id,long interval_time, long time_stamp) {
        this.course_id = course_id;
        this.interval_time = interval_time;
        this.time_stamp = time_stamp;
    }
}
