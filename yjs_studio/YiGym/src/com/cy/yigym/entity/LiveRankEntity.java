package com.cy.yigym.entity;

/**
 * Created by ejianshen on 15/9/15.
 */
public class LiveRankEntity {
    private String rank;
    private String nickname;
    private String calorie;
    private String distance;
    private String speed;
    private int rate;
    private int resist;
    public String pid; //person id
    public String profileFid; //person avatar id
    public String voipAccount;
    private String fid;

    private String login_mode = "";

    public LiveRankEntity(String voipAccount, String pid, String profileFid, String rank,
                          String nickname, String calorie, String speed) {
        this.voipAccount = voipAccount;
        this.pid = pid;
        this.profileFid = profileFid;
        this.rank = rank;
        this.nickname = nickname;
        this.calorie = calorie;
        this.speed = speed;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String distance) {
        this.calorie = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getRpm() {
        return String.valueOf(rate);
    }

    public void setRpm(int rate) {
        this.rate = rate;
    }

    public void setResist(int resist) {
        this.resist = resist;
    }

    public String getResist() {
        return String.valueOf(resist);
    }

    public void setLoginMode (String login_mode) {
        this.login_mode = login_mode;
    }
    public String getLoginMode() {
        return this.login_mode;
    }
}
