package com.cy.yigym.event;

/**
 * Created by ejianshen on 15/7/25.
 */
public class EventUpdateUserInfo {
    private String nickname;
    private String height;
    private String weight;
    private String sign;

    public EventUpdateUserInfo(String content,int i){
        switch (i){
            case 0:
                this.nickname=content;
                break;
            case 1:
                this.height=content;
                break;
            case 2:
                this.weight=content;
                break;
            case 3:
                this.sign=content;
                break;
        }
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
