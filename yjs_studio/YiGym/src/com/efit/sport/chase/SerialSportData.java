package com.efit.sport.chase;

import com.cy.wbs.UITimer;
import com.cy.yigym.ble.sport.SerialSportStrategy;
import com.cy.yigym.entity.ChaseIntentEntity;
import com.sport.efit.constant.SportType;

/**
 * author: tangtt
 * <p>
 * create at 2015/10/23
 * </p>
 * <p>
 *  串口收集个人运动数据
 * </p>
 */
public class SerialSportData extends FaceBikeData {

    private SerialSportStrategy sportStrategy;

    private boolean isConnected = false;

    /**
     * @param isFinished
     *      追ta是否完成，true：完成
     * @param isSender
     *       本人是否发起人,true:本人即为发起人
     * @param entity
     */
    public SerialSportData(boolean isFinished, boolean isSender, ChaseIntentEntity entity) {
        if(isSender) {
            startDistance = (int)entity.sendDistance;
            startTimes = (int)entity.sendTime;
            startCalorie = (int)entity.sendCalorie;
        } else {
            startDistance = (int)entity.receiverDistance;
            startTimes = (int)entity.receiverTime;
            startCalorie = (int)entity.receiverCalorie;
        }
        totalDistance = startDistance;
        totalTimes = startTimes;
        totalCalorie = startCalorie;
        distanceInMeters = (int)entity.realApartDistance;
        //已完成的数据不需要再跑
        if(!isFinished) {
            init();
        }
        //test();
    }

    /**
     * 个人历史数据。 骑行间断后，继续骑行时使用
     * @param sendDistance
     * @param sendTime
     * @param sendCalorie
     */
    public SerialSportData(double sendDistance, double sendTime, double sendCalorie) {
        startDistance = (int)sendDistance;
        startTimes = (int)sendTime;
        startCalorie = (int)sendCalorie;

        totalDistance = startDistance;
        totalTimes = startTimes;
        totalCalorie = startCalorie;
        distanceInMeters = 10000000; //big enough number

        init();
        //test();
    }

    protected void init() {
        initSportStatics();
    }

    @Override
    public void fini() {
        if(sportStrategy != null) {
            sportStrategy.fini();
        }

        if(testTimer != null) {
            testTimer.cancel();
        }
    }

    UITimer testTimer = null;
    private void test() {
        testTimer = new UITimer();
        testTimer.schedule(new Runnable() {
            int start = 0;

            @Override
            public void run() {
                start++;
                transformMotionParam(start, start, start);
                if (mListner != null) {
                    mListner.onDataChange();
                }
            }
        }, 500);
    }


    private void initSportStatics() {
        sportStrategy = SerialSportStrategy.create(SportType
                .fromDistance(distanceInMeters));
        sportStrategy
                .setFinishDecider(new SerialSportStrategy.DistanceFinishDecider() {
                    @Override
                    public boolean isFinish(int totalDistance,
                                            int currRunDistance) {
                        return false;
                    }
                });
        // self data
        sportStrategy.start();
        sportStrategy.setListener(new SerialSportStrategy.MotionParamChangeListener() {
            @Override
            public void onTargetFinish(int secs, int distanceInMeter,
                                       int kcal) {
                transformMotionParam(secs, distanceInMeter, kcal);
            }

            @Override
            public void onTargetCancel(int secs, int distanceInMeter,
                                       int kcal) {

            }

            @Override
            public void onMotionParam(int secs, int distanceInMeter,
                                      int kcal) {
                transformMotionParam(secs, distanceInMeter, kcal);
                roundPerMin = sportStrategy.getRoundPerMin();
                speedPerHour = sportStrategy.getSpeedPerHour();
                resist = sportStrategy.getResist();
                if (mListner != null) {
                    mListner.onDataChange();
                }
            }
        });

    }

    private void transformMotionParam(int secs, int distanceInMeter,
                                      int kcal) {

        thisTimeDistance = distanceInMeter;
        thisTimeTimes = secs;
        thisTimeCalorie = kcal;

        totalDistance = startDistance + thisTimeDistance;
        totalTimes = startTimes + thisTimeTimes;
        totalCalorie = startCalorie + thisTimeCalorie;
    }
}
