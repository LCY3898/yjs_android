package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 更新用户纪录（隔一段时间上传自己的运动数据）
 * Created by ejianshen on 15/7/28.
 * {"obj":"chaseRecord","act":"update","pid":"o14370561365609710216","_id":"o14376346652598230838","my_time":"111",
 * "my_distance":"222","my_calorie":"333","client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqUpdateRecord extends ChaseReqBase{
    public String act="update";
    //_id为这条纪录的id，创建纪录时可获取到
    public String _id="";
    public int my_time=0;
    public int my_distance=0;
    public int my_calorie=0;

    public ReqUpdateRecord(String _id,int my_time,int my_distance,int my_calorie){
        this.my_time=my_time;
        this.my_distance=my_distance;
        this.my_calorie=my_calorie;
        this._id=_id;

    }
}
