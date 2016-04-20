package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * #暂时不做快进快退，所以不考虑
 course_id       //课程id
 pid            //用户id
 calorie        //卡路里
 distance       //距离
 time_stamp     //上传数据的相对时间点    单位秒
 interval_time  //每次上传数据的时间间隔，单位秒
 * {"obj":"course","act":"reviewSportUpdate",
 * "course_id":"o14470511889593510627"
 * ,"pid":"o14448292223981139659","distance":10,"calorie":2,
 * "time_stamp":5,"interval_time":5,
 * "client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqReviewSportUpdate extends CourseReqBase {
    private String act="reviewSportUpdate";
    private String course_id=DataStorageUtils.getCourseId();
    private double distance=0;
    private double calorie=0;
    private long time_stamp=0;
    private long interval_time=0; //in seconds
    private double old_distance=0;
    private double old_calorie=0;
    private double speed=0;
    private double speed_per_min=0;
    private double resist=0;

    public ReqReviewSportUpdate(String courseId,double distance, double calorie,double speed,double speedPerMin,double resist,
                                long time_stamp,long interval_time,int oldDistance,int oldCalorie) {
        this.course_id = courseId;
        this.distance = distance;
        this.calorie = calorie;
        this.speed = speed;
        this.speed_per_min = speedPerMin;
        this.resist = resist;
        this.time_stamp=time_stamp;
        this.interval_time=interval_time;
        this.old_calorie = oldCalorie;
        this.old_distance = oldDistance;
    }
}
