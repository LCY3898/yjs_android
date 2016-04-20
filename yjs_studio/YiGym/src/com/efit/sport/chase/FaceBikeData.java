package com.efit.sport.chase;

/**
 * author: tangtt
 * <p>
 * create at 2016/1/15
 * </p>
 * <p>
 *  个人运动数据
 * </p>
 */
public abstract class FaceBikeData {
    public int startDistance;
    public int startTimes;
    public int startCalorie;
    public int totalDistance;
    public int totalTimes;
    public int totalCalorie;
    public int thisTimeDistance;
    public int thisTimeTimes;
    public int thisTimeCalorie;
    protected int distanceInMeters;
    protected int roundPerMin = 0;
    protected int speedPerHour = 0;
    protected int resist  = 0;
    protected OnDataChangeListener mListner;

    public FaceBikeData() {
    }

    public void setDataUpdateListener(OnDataChangeListener l) {
        this.mListner = l;
    }


    public void fini() {

    }



    public int getTotalDistance() {
        return totalDistance;
    }

    public int getSpeedPerHour() {//km/h
        return speedPerHour;
    }

    public int getRoundPerMin() {//rpm (round per minute)
        return roundPerMin;
    }

    public int getResist() {//rpm (round per minute)
        return resist;
    }

    public int getTotalTime() {
        return totalTimes;
    }

    public int getTotalCalorie() {
        return totalCalorie;
    }

    public int getThisTimeDistance() {
        return thisTimeDistance;
    }

    public int getThisTimeTimes() {
        return thisTimeTimes;
    }

    public int getThisTimeCalorie() {
        return thisTimeCalorie;
    }

    public interface OnDataChangeListener {
        void onDataChange();
    }
}
