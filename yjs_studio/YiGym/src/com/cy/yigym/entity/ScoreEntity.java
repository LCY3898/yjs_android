package com.cy.yigym.entity;

/**
 * Created by ejianshen on 15/8/22.
 */
public class ScoreEntity {
    private String fid;
    private String content;
    private String detail;
    private String score;

    public ScoreEntity(String content,String detail,String score){
        this.content=content;
        this.detail=detail;
        this.score=score;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
