package com.efit.sport.chase;

import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.ble.sport.BaseSportStrategy;
import com.cy.yigym.entity.ChaseIntentEntity;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventBleConnect;
import com.cy.yigym.utils.DataStorageUtils;
import com.hhtech.utils.LogToFile;
import com.sport.efit.constant.SportType;

import de.greenrobot.event.EventBus;

/**
 * author: tangtt
 * <p>
 * create at 2015/10/23
 * </p>
 * <p>
 *  个人运动数据
 * </p>
 */
public class ChaseTaSelfData extends FaceBikeData {

    private UITimer timerCheckBle = new UITimer();
    private BaseSportStrategy sportStrategy;

    private boolean isConnected = false;

    /**
     * @param isFinished
     *      追ta是否完成，true：完成
     * @param isSender
     *       本人是否发起人,true:本人即为发起人
     * @param entity
     */
    public ChaseTaSelfData(boolean isFinished,boolean isSender, ChaseIntentEntity entity) {
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
    public ChaseTaSelfData(double sendDistance,double sendTime,double sendCalorie) {
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
        EventBus.getDefault().register(bleConnectListener);
        timerCheckBle.schedule(bleConnectionCheckTask, 2000);
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
        timerCheckBle.cancel();
        EventBus.getDefault().unregister(bleConnectListener);
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
        sportStrategy = BaseSportStrategy.create(SportType
                .fromDistance(distanceInMeters));
        sportStrategy
                .setFinishDecider(new BaseSportStrategy.DistanceFinishDecider() {
                    @Override
                    public boolean isFinish(int totalDistance,
                                            int currRunDistance) {
                        return false;
                    }
                });
        // self data
        sportStrategy.start();
        ensureConnect();
        sportStrategy.setListener(new BaseSportStrategy.MotionParamChangeListener() {
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


    private BusEventListener.MainThreadListener<EventBleConnect> bleConnectListener = new BusEventListener.MainThreadListener<EventBleConnect>() {
        @Override
        public void onEventMainThread(EventBleConnect event) {
            if (event.isConnect) {
                if (event.isWheelRunning) {
                    sportStrategy.resume();
                } else {
                    sportStrategy.pause();
                }
                if(!isConnected) {
                    LogToFile.i("BleConnect", "status: connected");
                }
            } else {
                sportStrategy.pause();
                if(isConnected) {//
                    WidgetUtils.showToast("设备连接已断开");
                }
                LogToFile.i("BleConnect", "status: connection break");
            }
            isConnected = event.isConnect;
        }
    };

    private Runnable bleConnectionCheckTask = new Runnable() {
        @Override
        public void run() {
            ensureConnect();
        }
    };

    private void ensureConnect() {
        String bleAddr = DataStorageUtils.getBleAddress();
        // 如果是同一个设备，并且已经连接上，则不需要再连接
        if (!BleConnect.instance().isBleConnected()
                || !BleConnect.isBleAddressEqual(bleAddr,BleConnect.instance().getConnectBleAddr())) {
            sportStrategy.pause();
            BleConnect.instance().connectBle(bleAddr);
            isConnected = false;
            LogToFile.i("BleConnect", "period check, not connected:bleAddr:" + bleAddr + "&&:" + BleConnect.instance().getConnectBleAddr());
        } else {
            isConnected = true;
            LogToFile.i("BleConnect", "period check, connected");
        }
    }
    

}
