package com.cy.yigym.entity;

import java.io.Serializable;

/**
 * tangtt
 * <p>
 * 2015-12-1
 * </p>
 * <p>
 * 直播视频内容
 * </p>
 */
public class LiveVideoEntity implements Serializable {
    public String courseId;
    public long startTime;
    public long endTime;
    public String liveUrl;

    public LiveVideoEntity(String courseId, String liveUrl,
                                        long startTime, long endTime) {
        this.courseId = courseId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.liveUrl = liveUrl;
    }
}
