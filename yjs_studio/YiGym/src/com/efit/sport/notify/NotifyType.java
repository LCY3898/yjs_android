package com.efit.sport.notify;

/**
 * Created by tangtt on 15/8/14.
 */
public enum NotifyType {
    CHASE("chase"),      //追她
    TASK("task"),        //任务
    BARRAGE("barrage"),  //弹幕
    CHAT("chat");



    private NotifyType(String name) {
        this.typeName = name;
    }

    public String getTypeName() {
        return typeName;
    }
    private String typeName;
}
