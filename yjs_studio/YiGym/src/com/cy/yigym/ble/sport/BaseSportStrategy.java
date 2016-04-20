package com.cy.yigym.ble.sport;

import com.cy.wbs.UITimer;
import com.cy.yigym.ble.BleConnect;
import com.sport.efit.constant.SportType;

/**
 * Created by tangtt on 15/7/25.
 */
public class BaseSportStrategy {

    private int type = SportType.TYPE_TIME;
    protected long resumeMills;

    protected long accumulateMills = 0;

    protected double distanceInMeter = 0;

    protected double calorie = 0;


    private UITimer timer = new UITimer();

    SportType sportType;
    private boolean isPause = false;

    public final static int UPDATE_INTERVAL = 100;


    /**
     * 用户没有在骑行时，可能是在调整阻力，这时仍要更新数据
     */
    private boolean fetchResistOnly = false;


    public static BaseSportStrategy create(SportType sportType) {
        BaseSportStrategy strategy = new BaseSportStrategy(sportType);
        return strategy;
    }

    public BaseSportStrategy(SportType type) {
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
        return (int) BleConnect.instance().getRoundPerMin();
    }


    /**
     * 返回阻力 in percent
     *
     * @return
     */
    public int getResist() {
        return (int) BleConnect.instance().getResist();
    }


    /**
     * @return km/h
     */
    public int getSpeedPerHour() {
        return (int) BleConnect.instance().getSpeedPerHour();
    }

    public void start() {
        calorie = 0;
        distanceInMeter = 0;
        resumeMills = System.currentTimeMillis();
        BleConnect.instance().startRide();
        timer.schedule(fetchDataTask, UPDATE_INTERVAL);
        isPause = false;
        fetchResistOnly = false;
    }

    public void fini() {
        BleConnect.instance().stopRide();
        timer.cancel();
        long now = System.currentTimeMillis();
        accumulateMills += (now - resumeMills);
    }

    public void cancel() {
        BleConnect.instance().stopRide();
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
        BleConnect.instance().pause();
    }

    public void resume() {
        if (!BleConnect.instance().isBleConnected()) {
            return;
        }
        if (!isPause) {
            return;
        }
        isPause = false;
        fetchResistOnly = false;
        resumeMills = System.currentTimeMillis();
        //timer.schedule(fetchDataTask,1000);
        BleConnect.instance().resume();
    }

    private Runnable fetchDataTask = new Runnable() {
        @Override
        public void run() {

            /**
             * 用户没有在骑行时，可能是在调整阻力，这时仍要更新数据
             */
            if (fetchResistOnly) {
                if (mListener != null) {
                    mListener.onMotionParam(getSportSec(), getSportDistance(), getSportCalorie());
                }
                return;
            }
            distanceInMeter = BleConnect.instance().getDistance();
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

    /*
        跑步热量（kcal）＝体重（kg）×运动时间（小时）×指数K
		指数K＝30÷速度（分钟/400米）
		例如：某人体重60公斤，长跑1小时，速度是3分钟/400米或8公里/小时，那么他跑步过程中消耗的热量＝60×1×30/3=600kcal(千卡);
	*/
    private void calcCalorie() {
        /*int weight;
        try {
            weight = Integer.parseInt(CurrentUser.instance().getPersonInfo().weight);
            if(weight == 0) {
                weight = 60;
            }
        }catch (Exception e) {
            weight = 60;
        }
        double hour = (double)(accumulateMills / 1000) / 3600;
        double speed = getMinuteSpeed() / 400;
        calorie = weight * hour * speed * 30 / 2.5;//tangtt calorie 再除2.5*/
        calorie = BleConnect.instance().getCalorie();
    }


}
