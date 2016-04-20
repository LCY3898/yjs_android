package com.cy.yigym.entity;

/**
 * Created by ejianshen on 15/8/20.
 */
public class RankEntity {
    private double totalDistance;
    private double totalTime;
    private double totalCalorie;
    private String nickname;
    private String fid;
    private int rank;
    private int peopleNumber;

    public RankEntity(int rank,String nickname,String fid,
                      double totalDistance,double totalCalorie,double totalTime){
        this.rank=rank;
        this.nickname=nickname;
        this.fid=fid;
        this.totalDistance=totalDistance;
        this.totalCalorie=totalCalorie;
        this.totalTime=totalTime;
    }
    public RankEntity(String nickname,String fid,double totalDistance){
        this.nickname=nickname;
        this.fid=fid;
        this.totalDistance=totalDistance;
    }
    public RankEntity(String nickname,String fid,int peopleNumber,double totalDistance){
        this.nickname=nickname;
        this.fid=fid;
        this.peopleNumber=peopleNumber;
        this.totalDistance=totalDistance;
    }

    public RankEntity(double totalDistance, double totalTime, double totalCalorie, String nickname, String fid) {
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.totalCalorie = totalCalorie;
        this.nickname = nickname;
        this.fid = fid;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public double getTotalCalorie() {
        return totalCalorie;
    }

    public void setTotalCalorie(double totalCalorie) {
        this.totalCalorie = totalCalorie;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getPeopleNumber() {
        return peopleNumber;
    }

    public void setPeopleNumber(int peopleNumber) {
        this.peopleNumber = peopleNumber;
    }
}
