package com.cy.yigym.entity;

/**
 * Created by ejianshen on 15/8/7.
 */
public class ChaseListEntity {
    private String fid;
    private String nickname;
    private int distance;
    private String _id;
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    public ChaseListEntity(){

    }
    public ChaseListEntity(String fid, String nickname, int distance){
        this.fid=fid;
        this.nickname=nickname;
        this.distance=distance;
    }
    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }



}
