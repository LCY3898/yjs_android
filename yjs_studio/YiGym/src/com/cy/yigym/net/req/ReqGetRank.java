package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 获取运动排名(排行榜)
 * pid 	//个人id
 nick_name 	//排行榜名称 charmRank/totalRank
 page_size //每页显示条数(默认3条)
 cnt  //排名结构标记(第一次不传)
 * Created by ejianshen on 15/8/3.
 * {"obj":"charts","act":"getRank","pid":"o14374634597974619865","rank_name":"totalRank","client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqGetRank extends ChartsReqBase {
    private String act="getSortRank";
    private String rankType="";
    private int pages;
    private int page_size;
    public ReqGetRank(String rank_name,int pages,int page_size){
        this.rankType=rank_name;
        this.pages=pages;
        this.page_size=page_size;
    }

}
