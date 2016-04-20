package com.cy.yigym.net.req;


import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.persist.bean.VideoHistory;

/**
 {"obj":"person","act":"saveWatchProgress","pid":"o14492128781147511005","courseId":"o14491464991206929000","videoFid":"aaadddff",
 "videoUrl":"2222","courseName":"测试","calorie":"30","beginTime":"1449417600","endTime":"1449460800",
 "lastPlayPostion":"50000","lastPlayTime":"60000","continueTipStatus":"yes","duration":"2"}
 */
public class ReqSaveWatchProgress extends PersonReqBase {
    public String act = "saveWatchProgress";


    public String courseId;

    public String videoUrl;

    public String videoFid;//background fid

    public String courseName;

    public String coachName;

    public int calorie;

    public int duration;

    public long beginTime;

    public long endTime;

    public int lastPlayTime;//上次观看时间


    public int lastPlayPostion;//上次观看进度，以时间(毫秒)计

    public boolean continueTipStatus; // 是否弹出接续上次播放进度的提示, add 2015/11/18

    public ReqSaveWatchProgress(VideoHistory history) {
        this.pid = DataStorageUtils.getPid();
        this.act = "saveWatchProgress";

        this.courseId = history.courseId;
        this.videoUrl = history.videoUrl;

        this.videoFid = history.videoFid;//background fid
        this.courseName = history.courseName;
        this.coachName = history.coachName;
        this.calorie = history.calorie;
        this.duration = history.duration;
        this.beginTime = history.beginTime;
        this.endTime = history.endTime;
        this.lastPlayTime = history.lastPlayTime;//上次观看时间
        this.lastPlayPostion = history.lastPlayPostion;//上次观看进度，以时间(毫秒)计
        this.continueTipStatus = history.continueTipStatus;
    }


}
