package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by ejianshen on 15/11/16.
 * pid：用户id
 page_size：显示一页条数（可选）
 page_num：页码数（可选）
 {"obj":"course","act":"getWatchingHistory","pid":"o14458513848364729881","client_info":{"clientType":"webapp","userId":null},"debug":1}

 输出：
 number：数据库中记录数
 video_list_return：当前页返回的数组名
 coach_name：教练名称
 course_fid：课程封面id
 course_name:课程名称
 ut：最近一次观看该视频的时间
 video_link：视频链接地址
 */
public class ReqGetWatchingHistory extends CourseReqBase {
    private String act="getWatchingHistory";
    //private String pid= DataStorageUtils.getPid();
}
