package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 将自己的经纬度上传到服务器
 * Created by ejianshen on 15/8/4.
 * longitude经度   latitude纬度  pid 用户id  name 你的昵称
 */
public class ReqTransmitLocation extends PersonReqBase {
    private String act="transmitLocation";
    private String name="";
    private double longitude=0;
    private double latitude=0;
    private String sex="";
    public ReqTransmitLocation(String name,String sex,double longitude,double latitude){
        this.name=name;
        this.sex=sex;
        this.longitude=longitude;
        this.latitude=latitude;
    }
}
