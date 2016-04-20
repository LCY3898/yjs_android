package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;

/**
 * 输出：
 * {
 * sess: "",
 * io: "o",
 * obj: "person",
 * act: "getManyWatchProgress",
 * code: "SUCCEED",
 * data: [
 * {
 * beginTime: "1449417600",
 * calorie: "30",
 * continueTipStatus: "yes",
 * courseId: "o14491464991206929654",
 * courseName: "测试",
 * duration: "2",
 * endTime: "1449460800",
 * lastPlayPostion: "50000",
 * lastPlayTime: "60000",
 * videoFid: "aaadddff",
 * videoUrl: "2222"
 * },
 * {
 * beginTime: "1449417600",
 * calorie: "30",
 * continueTipStatus: "yes",
 * courseId: "o14491464991206929694",
 * courseName: "测试",
 * duration: "2",
 * endTime: "1449460800",
 * lastPlayPostion: "50000",
 * lastPlayTime: "60000",
 * videoFid: "aaadddff",
 * videoUrl: "2222"
 * },
 * {
 * beginTime: "1449417600",
 * calorie: "30",
 * continueTipStatus: "yes",
 * courseId: "o14491464991206929000",
 * courseName: "测试",
 * duration: "2",
 * endTime: "1449460800",
 * lastPlayPostion: "50000",
 * lastPlayTime: "60000",
 * videoFid: "aaadddff",
 * videoUrl: "2222"
 * }
 * ],
 * msg: "成功获取该用户所有课程的的观看进度"
 * }
 * <p/>
 * <p/>
 * </p>
 */
public class RspManyWatchProgress extends RspBase {

    public ArrayList<WatchProgressRecord> data = new ArrayList<WatchProgressRecord>();


    public class WatchProgressRecord {
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
    }
}
