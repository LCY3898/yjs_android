package com.cy.yigym.ble.sport;

import com.cy.wbs.UITimer;
import com.cy.yigym.ble.SerialConnect;
import com.sport.efit.constant.SportType;

/**
 * Created by tangtt on 15/7/25.
 */
public class SerialSportStrategy {

    private int type = SportType.TYPE_TIME;
    protected long resumeMills;

    protected long accumulateMills = 0;

    protected double distanceInMeter = 0;

    protected double calorie = 0;


    private UITimer timer = new UITimer();

    SportType sportType;
    private boolean isPause = false;

    public final static int UPDATE_INTERVAL = 150;


    /**
     * 用户没有在骑行时，可能是在调整阻力，这时仍要更新数据
     */
    private boolean fetchResistOnly = false;


    public static SerialSportStrategy create(SportType sportType) {
        SerialSportStrategy strategy = new SerialSportStrategy(sportType);
        return strategy;
    }

    public SerialSportStrategy(SportType type) {
        sportType = type;
    }

    protected MotionParamChangeListener mListener;
    protected DistanceFinishDecider mDecider;

    public interface MotionParamChangeListener {
        void onTargetFinish(int secs, int distanceInMeter, int kcal);

        void onTargetCancel(int secs, int distanceInMeter, int kcal);

        void onMotionParam(int secs, int distanceInMeter, int kcal);
    }

    public interface DistanceFinishDecider {
        boolean isFinish(int totalDistance, int currRunDistance);
    }


    public void setListener(MotionParamChangeListener listener) {
        mListener = listener;
    }


    public void setFinishDecider(DistanceFinishDecider decider) {
        this.mDecider = decider;
    }

    /**
     * in secs
     */
    public int getSportSec() {
        return (int) (accumulateMills / 1000);
    }

    /**
     * in kcal
     */
    public int getSportCalorie() {
        return (int) calorie;
    }

    /**
     * in meter
     */
    public int getSportDistance() {
        return (int) distanceInMeter;
    }


    /**
     * @return rpm（round per minute） 一分钟飞轮转数
     */
    public int getRoundPerMin() {
        return (int) SerialConnect.instance().getRoundPerMin();
    }


    /**
     * 返回阻力 in percent
     *
     * @return
     */
    public int getResist() {
        return (int) SerialConnect.instance().getResist();
    }


    /**
     * @return km/h
     */
    public int getSpeedPerHour() {
        return (int) SerialConnect.instance().getSpeedPerHour();
    }

    public void start() {
        calorie = 0;
        distanceInMeter = 0;
        resumeMills = System.currentTimeMillis();
        SerialConnect.instance().startRide();
        timer.schedule(fetchDataTask, UPDATE_INTERVAL);
        isPause = false;
        fetchResistOnly = false;
    }

    public void fini() {
        SerialConnect.instance().stopRide();
        timer.cancel();
        long now = System.currentTimeMillis();
        accumulateMills += (now - resumeMills);
    }

    public void cancel() {
        SerialConnect.instance().stopRide();
        timer.cancel();
        long now = System.currentTimeMillis();
        accumulateMills += (now - resumeMills);
        if (mListener != null) {
            mListener.onTargetCancel(getSportSec(), getSportDistance(), getSportCalorie());
        }
    }

    public void pause() {
        if (isPause) {
            return;
        }
        isPause = true;
        fetchResistOnly = true;
        //timer.cancel();
        //long now = System.currentTimeMillis();
        //accumulateMills += (now - resumeMills);
        SerialConnect.instance().pause();
    }

    public void resume() {
        if (!SerialConnect.instance().isConnected()) {
            return;
        }
        if (!isPause) {
            return;
        }
        isPause = false;
        fetchResistOnly = false;
        resumeMills = System.currentTimeMillis();
        //timer.schedule(fetchDataTask,1000);
        SerialConnect.instance().resume();
    }

    private Runnable fetchDataTask = new Runnable() {
        @Override
        public void run() {

            if(SerialConnect.instance().isRiding()) {
                resume();
            } else {
                pause();
            }
            /**
             * 用户没有在骑行时，可能是在调整阻力，这时仍要更新数据
             */
            if (fetchResistOnly) {
                if (mListener != null) {
                    mListener.onMotionParam(getSportSec(), getSportDistance(), getSportCalorie());
                }
                return;
            }
            distanceInMeter = SerialConnect.instance().getDistance();
            calcCalorie();

            long now = System.currentTimeMillis();
            accumulateMills += (now - resumeMills);
            resumeMills = now;
            if (isFinishTarget()) {
                timer.cancel();
                if (mListener != null) {
                    mListener.onTargetFinish(getSportSec(), getSportDistance(), getSportCalorie());
                }
            } else {
                if (mListener != null) {
                    mListener.onMotionParam(getSportSec(), getSportDistance(), getSportCalorie());
                }
            }
        }
    };

    private boolean isFinishTarget() {
        boolean isFinish = false;
        int sportQuantity = sportType.getSportQuantity();
        if (sportType.type == SportType.TYPE_TIME) {
            int secs = (int) ((accumulateMills / 1000));
            isFinish = (secs >= sportQuantity);
        } else if (sportType.type == SportType.TYPE_DISTANCE) {
            if (this.mDecider != null) {
                isFinish = mDecider.isFinish(sportQuantity, (int) distanceInMeter);
            } else {
                isFinish = distanceInMeter >= sportQuantity;
            }
        } else if (sportType.type == SportType.TYPE_CALORIE) {
            isFinish = calorie >= sportQuantity;
        }

        return isFinish;
    }


    private double getMinuteSpeed() {
        return distanceInMeter * 60 / (accumulateMills / 1000);
    }


    private void calcCalorie() {
        calorie = SerialConnect.instance().getCalorie();
    }


}
