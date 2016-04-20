package com.cy.yigym.entity;

/**
 * Created by ejianshen on 15/9/22.
 */
public class LiveAdvanceEntity {
    private String course;
    private String startTime;
    private String coach;
    private String courseTime;
    private String calorie;
    private int isSubscribed;

    private String courseId;

    private String courseFid;

    public LiveAdvanceEntity(String course, String startTime, String coach, String courseTime, String calorie) {
        this.course = course;
        this.startTime = startTime;
        this.coach = coach;
        this.courseTime = courseTime;
        this.calorie = calorie;
    }

    public int getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(int isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseFid() {
        return courseFid;
    }

    public void setCourseFid(String courseFid) {
        this.courseFid = courseFid;
    }
}
