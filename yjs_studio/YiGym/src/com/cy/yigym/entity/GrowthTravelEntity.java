package com.cy.yigym.entity;

/**
 * Created by ejianshen on 15/11/13.
 */
public class GrowthTravelEntity {
    private String time;
    private String item;
    private String photoUrl;

    public GrowthTravelEntity(String time, String item, String photoUrl) {
        this.time = time;
        this.item = item;
        this.photoUrl = photoUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
