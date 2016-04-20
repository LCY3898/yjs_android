package com.sport.efit.constant;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by tangtt on 15/7/25.
 */
public class SportType implements Serializable {

    private SportType(int type, int target) {
        this.type = type;
        switch (type) {
            case TYPE_DISTANCE:
                distanceInMeter = target;
                break;

            case TYPE_TIME:
                secs = target;
                break;

            case TYPE_CALORIE:
                calorie = target;
                break;
        }
        this.target = target;
    }

    public static SportType from(int type,int target) {
        return new SportType(type,target);
    }

    public static SportType fromDistance(int meters) {
        SportType sportType = new SportType(TYPE_DISTANCE,meters);
        return sportType;
    }

    public static SportType fromTime(int secs) {
        SportType sportType = new SportType(TYPE_TIME,secs);
        return sportType;
    }

    public static SportType fromCalorie(int calorie) {
        SportType sportType = new SportType(TYPE_CALORIE,calorie);
        return sportType;
    }

    public int type;
    public int target;

    public int distanceInMeter;
    public int calorie;
    public int secs;

    public final static String INTENT_KEY = "sport_type";


    public final static int TYPE_INVALID = -1;
    public final static int TYPE_TIME = 0;
    public final static int TYPE_DISTANCE = 1;
    public final static int TYPE_CALORIE = 2;


    public final static int MAX_PROGRESS = 1000;

    public final static int MAX_TASKVALUE=10000;
    /**
     * 计算运动量
     * */
    public final int getSportQuantity() {
        int targetQuantity = 0;
        switch (type) {
            case SportType.TYPE_CALORIE:
                targetQuantity = calorie;// cal
                break;
            case SportType.TYPE_TIME:
                targetQuantity = (secs / 60) * 60; //secs(round to minute)
                break;

            case SportType.TYPE_DISTANCE:
                targetQuantity = distanceInMeter;//in meter
                break;
            default:
                break;
        }
        return targetQuantity;
    }

    public final String formatSportQuantity() {
        int targetQuantity = getSportQuantity();
        String quantity = "";
        switch (type) {
            case SportType.TYPE_CALORIE:
                quantity = String.format("%d cal", targetQuantity);
                break;
            case SportType.TYPE_TIME:
                quantity = String.format("%d min", targetQuantity / 60);
                break;

            case SportType.TYPE_DISTANCE:
                quantity = String.format("%.2f km", (double)targetQuantity / 1000);
            default:
                break;
        }
        return  quantity;
    }

    public final static String getAccomplished(SportType sportType,int secs,int distance,int calorie) {
         String quantity = "";
        switch (sportType.type) {
            case SportType.TYPE_CALORIE:
                quantity = String.format("%d", calorie);
                break;
            case SportType.TYPE_TIME:
                if(secs <= 60 ) {
                    quantity = String.format("%d", secs);
                } else {
                    quantity = String.format("%d:%d:%d",secs/3600, secs / 60,secs % 60);
                }

                break;

            case SportType.TYPE_DISTANCE:
                if(distance < 1000) {
                    quantity = String.format("%d", distance);
                } else {
                    quantity = String.format("%.2f", (double)distance / 1000);
                }
            default:
                break;
        }

        return  quantity;
    }

    public final static String getRemain(SportType sportType,int secs,int distance,int calorie) {
        String quantity = "";
        int target = sportType.getSportQuantity();
        switch (sportType.type) {
            case SportType.TYPE_CALORIE:
                quantity = String.format("剩余%d kcal", target - calorie);
                break;
            case SportType.TYPE_TIME:
                int remainSec = target - secs;
                if(remainSec <= 60 ) {
                    quantity = String.format("剩余%d sec", remainSec);
                } else {
                    quantity = String.format("剩余%d min", remainSec / 60);
                }
                break;

            case SportType.TYPE_DISTANCE:
                int remainDistance = target - distance;
                if(remainDistance < 1000) {
                    quantity = String.format("%d m", remainDistance);
                } else {
                    quantity = String.format("剩余%.2f km", (double)remainDistance / 1000);
                }
            default:
                break;
        }

        return  quantity;
    }


    public final static int getAccomplishPercent(SportType sportType,int secs,int distance,int calorie) {
        int target = sportType.getSportQuantity();
        int percent = 0;
        switch (sportType.type) {
            case SportType.TYPE_CALORIE:
                percent = calorie * 100 / target;
                break;
            case SportType.TYPE_TIME:
                percent = secs * 100 /target;
                break;

            case SportType.TYPE_DISTANCE:
                percent = distance * 100 / target;
            default:
                break;
        }
        return  percent;
    }

}
