package com.cy.yigym.entity;

import com.cy.yigym.net.rsp.RspCreateRecord;
import com.cy.yigym.net.rsp.RspGetChaseRecord;

import java.io.Serializable;

/**
 * Created by ejianshen on 15/8/18.
 */
 public class ChaseIntentEntity implements Serializable {


    public ChaseIntentEntity(String nickname, RspCreateRecord createRecord) {

        this.otherNickname = nickname;
        isDoing = true;

        RspCreateRecord.CrRef record = createRecord.data.cr_ref;


        recordId = record._id;

        this.senderId = record.sender_id;
        this.receiverId = record.receiver_id;

        sendDistance = record.sender_distance;
        sendTime = record.sender_time;
        sendCalorie = record.sender_calorie;

        receiverDistance = record.receiver_distance;
        receiverCalorie = record.receiver_calorie;
        receiverTime = record.receiver_time;

        totalApartDistance = record.total_apart_distance;
        realApartDistance = record.real_apart_distance;

        my_fid = createRecord.data.my_fid;
        another_fid = createRecord.data.another_fid;
    }

    //完成会面
    public ChaseIntentEntity(String nickname,RspGetChaseRecord createRecord,boolean isDoing) {
        this.isDoing = isDoing;
        this.otherNickname = nickname;

        RspGetChaseRecord.Data crRef = createRecord.data;
        recordId =crRef.chaseRecord._id;

        this.senderId = crRef.chaseRecord.sender_id;
        this.receiverId = crRef.chaseRecord.receiver_id;

        sendDistance = crRef.chaseRecord.sender_distance;
        sendTime = crRef.chaseRecord.sender_time;
        sendCalorie = crRef.chaseRecord.sender_calorie;

        receiverDistance = crRef.chaseRecord.receiver_distance;
        receiverCalorie = crRef.chaseRecord.receiver_calorie;
        receiverTime = crRef.chaseRecord.receiver_time;


        realApartDistance = (int)crRef.chaseRecord.real_apart_distance;
        totalApartDistance =crRef.chaseRecord.total_apart_distance;
        my_fid=crRef.my_fid;
        another_fid=crRef.another_fid;
    }




    public String recordId;

    public String senderId;
    public String receiverId;

    public boolean isDoing;

    public double sendDistance;
    public double sendCalorie;
    public double sendTime;

    public double receiverDistance;
    public double receiverCalorie;
    public double receiverTime;

    public double realApartDistance;
    public double totalApartDistance;

    // 我的头像和被追人的头像
    public String my_fid;
    public String another_fid;

    public String otherNickname;

    public final static String INTENT_KEY = "chase_entity";
}
