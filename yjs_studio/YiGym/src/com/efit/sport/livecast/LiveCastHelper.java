package com.efit.sport.livecast;

import android.content.Intent;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/11/16 0016.
 */
public class LiveCastHelper {

    public static void saveVideoInfo(Intent intent,VideoInfo info) {
        intent.putExtra("video_info",info);
    }

    public static VideoInfo getVideoInfo(Intent intent) {
        VideoInfo videoInfo = (VideoInfo) intent.getSerializableExtra("video_info");
        return videoInfo;
    }

    public static VideoInfo genVideoInfo(String courseId,String coachName,String courseName,
                                         int duration,String videoUrl,String avatarFid) {
        VideoInfo info = new VideoInfo();
        info.courseId = courseId;
        info.coachName = coachName;
        info.courseName = courseName;
        info.duration = duration;
        info.videoUrl = videoUrl;
        info.avatarFid = avatarFid;
        return info;
    }

    public final static class VideoInfo implements Serializable {
        public String coachName;
        public String courseName;
        public int duration;
        public String videoUrl;
        public String avatarFid;
        public String courseId;
    }
}
