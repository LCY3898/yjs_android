package com.cy.yigym.entity;

import java.io.Serializable;

/**
 * Created by ejianshen on 15/8/14.
 */
public class MeetSuccessData implements Serializable {
    public final static String INTENT_KEY = "sport_data";

    public MeetSuccessData(int distance, int timeInSecs,int calorie
            , String nickname,String myId,String herId
            ,int otherDistance,int otherTimeInSecs,int otherCalorie) {
        this.distance = distance;
        this.calorie = calorie;
        this.timeInSecs = timeInSecs;
        this.nickname = (nickname == null ? "TA":nickname);
        this.myId=myId;
        this.herId=herId;
        this.otherDistance=otherDistance;
        this.otherTimeInSecs=otherTimeInSecs;
        this.otherCalorie=otherCalorie;

    }

    public int distance;//kilo
    public int calorie;
    public int timeInSecs;
    public String nickname;
    public String myId;
    public String herId;
    public int otherDistance;
    public int otherTimeInSecs;
    public int otherCalorie;
}
