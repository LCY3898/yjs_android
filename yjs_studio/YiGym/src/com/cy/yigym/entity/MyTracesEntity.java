package com.cy.yigym.entity;

/**
 * Created by ejianshen on 15/11/9.
 */
public class MyTracesEntity {
    private String courseName;
    private String caochName;
    private String liveWhere;
    private String time;
    private String videoUrl;
    public String videoAvatarId;
    public String courseId;

    public MyTracesEntity(String courseId,String courseName, String caochName,String fid,String liveWhere, String time) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.caochName = caochName;
        this.liveWhere = liveWhere;
        this.time = time;
        this.videoAvatarId = fid;
    }

    /**
     * @return 绝对地址
      */
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCaochName() {
        return caochName;
    }

    public void setCaochName(String caochName) {
        this.caochName = caochName;
    }

    public String getLiveWhere() {
        return liveWhere;
    }

    public void setLiveWhere(String liveWhere) {
        this.liveWhere = liveWhere;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
