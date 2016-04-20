package com.cy.yigym.net.req;

/**
 * Created by xiaoshu on 15/10/10.
 */
public class ReqPreCourse extends PreCourseReqBase {
    public String act = "search";

    public String course_type;
    public String coach_name;
    public int calorie;
    public int course_time;
    public int page_num;
    public int page_size;
    public String status;

    public ReqPreCourse(String course_type, String coach_name, int calorie, int course_time) {
        this.course_type = course_type;
        this.coach_name = coach_name;
        this.calorie = calorie;
        this.course_time = course_time;
    }

    public ReqPreCourse(String course_type, String coach_name, int calorie, int course_time,
                        int page_num, int page_size,String status) {
        this.course_type = course_type;
        this.coach_name = coach_name;
        this.calorie = calorie;
        this.course_time = course_time;
        this.page_num = page_num;
        this.page_size = page_size;
        this.status=status;
    }
}
