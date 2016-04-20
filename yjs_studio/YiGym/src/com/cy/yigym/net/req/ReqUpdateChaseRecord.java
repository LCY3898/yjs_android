package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 客户端用户选择退出时调用接口，从进入界面到退出界面时间内跑过的里程,时间,消耗卡路里
 * Created by ejianshen on 15/7/28.
 * {"obj":"chaseRecord","act":"updateChaseRecord","pid":"o14370561365609710216",
 * "_id":"o14376346652598230838","apart_distance":"6666","p_time":"111","p_distance":"222","p_calorie":"333",
 * "client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqUpdateChaseRecord extends ChaseReqBase {
    private String act="updateChaseRecord";
    private String _id="";
    private int apart_distance=0;
    private int p_time=0;
    private int p_distance=0;
    private int p_calorie=0;
    private int my_time=0;
    private int my_distance=0;
    private int my_calorie=0;

    public ReqUpdateChaseRecord(String _id,int p_time,
                                int p_distance,int p_calorie,
                                int my_time,int my_distance,int my_calorie){
        this._id=_id;
        this.p_time=p_time;
        this.p_distance=p_distance;
        this.p_calorie=p_calorie;
        this.my_time=my_time;
        this.my_distance=my_distance;
        this.my_calorie=my_calorie;
    }

}
