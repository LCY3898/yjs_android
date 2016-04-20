package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 加入往期课程接口
 *course_id      //课程id
 pid            //用户id
 nick_name      //昵称
 profile_fid    //可传可不传
 start_time     //加入课程的相对时间
 输入
 {"obj":"course","act":"reviewJoin","course_id":"o14470511889593510627",
 "pid":"o14448292223981139659","nick_name":"surfer斌",
 "start_time":0,"client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqReviewJoin extends CourseReqBase {
    private String act="reviewJoin";
    private String course_id="";
    private String nick_name=DataStorageUtils.getUserNickName();
    private String profile_fid=DataStorageUtils.getCurUserProfileFid();
    private int start_time;
    private int isStartWatch;//1表示重新开始，-1表示继续

    public ReqReviewJoin(String course_id,int start_time,boolean freshStart) {
        this.course_id = course_id;
        this.start_time = start_time;
        isStartWatch = freshStart?1:-1;
    }
}
