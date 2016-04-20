package com.efit.sport.persist.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_video_history")
public class VideoHistory {

    @DatabaseField(generatedId = true)
    public int id;


    @DatabaseField(columnName = COLUMN_COURSE_ID)
    public String courseId;

    @DatabaseField(columnName = COLUMN_URL)
    public String videoUrl;

    @DatabaseField(columnName = "pid")
    public String pid;

    @DatabaseField(columnName = "video_fid")
    public String videoFid;//background fid

    @DatabaseField(columnName = "title")
    public String courseName;

    @DatabaseField(columnName = "coach_name")
    public String coachName;

    @DatabaseField(columnName = "calorie")
    public int calorie;

    @DatabaseField(columnName = "duration")
    public int duration;

    @DatabaseField(columnName = "begin_time")
    public long beginTime;

    @DatabaseField(columnName = "end_time")
    public long endTime;

    @DatabaseField(columnName = "last_play_time")
    public int lastPlayTime;//上次观看时间


    @DatabaseField(columnName = "last_watch_postion")
    public int lastPlayPostion;//上次观看进度，以时间(毫秒)计

    @DatabaseField(columnName = "continue_tip_status")
    public boolean continueTipStatus; // 是否弹出接续上次播放进度的提示, add 2015/11/18


    public VideoHistory() {
    }

    public final static String COLUMN_URL = "video_url";
    public final static String COLUMN_COURSE_ID = "course_id";
}
