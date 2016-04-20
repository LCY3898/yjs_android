package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * {"obj":"course","act":"sportUpdate","course_id":"o14470511889593510627",
 * "pid":"o14431923958534181118","distance":10,
 * "calorie":2,"client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqSportUpdate extends CourseReqBase {
    private String act="sportUpdate";
    private String course_id=DataStorageUtils.getCourseId();
    private double distance=0;
    private double speed=0;
    private double calorie=0;
    private double speed_per_min=0;
    private double old_distance=0; //过去那个时间点的距离
    private double old_calorie=0;    //过去那个时间点的卡路里
    private double resist=0;    //resistance


    public ReqSportUpdate(double distance, double speed, double calorie,
                          double speed_per_min,double resist,double old_distance, double old_calorie) {
        this.distance = distance;
        this.speed = speed;
        this.calorie = calorie;
        this.speed_per_min = speed_per_min;
        this.pid=DataStorageUtils.getPid();
        this.old_calorie=old_calorie;
        this.old_distance=old_distance;
        this.resist = resist;
    }
}
