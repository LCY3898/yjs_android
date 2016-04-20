package com.cy.yigym.test;

import com.cy.wbs.UITimer;
import com.cy.yigym.CurrentUser;

import java.util.Random;

/**
 * ģ�ⱻ׷�Ķ���
 * Created by tangtt on 2015/8/6.
 */
public class ChaserObjectEmulator {

    public interface ReceiverEventListener {
        void onArrival();
        void onUpdateData(int secs,int distanceInMeter,int calorie);
        void onDisppear();
    }

    private ReceiverEventListener listener;
    UITimer uiTimer = new UITimer();

    private boolean firstStart = true;
    private void init() {

    }

    public void setListener(ReceiverEventListener l) {
        listener = l;
    }

    public void start() {
        firstStart = true;
        uiTimer.schedule(testRunnable,getDelaySecs(),1000*10);
    }

    public void fini() {
        uiTimer.cancel();
    }

    private int getDelaySecs() {
        Random random = new Random();
        int secs = random.nextInt(60);
        return 15 + secs;
    }


    private Runnable testRunnable = new Runnable() {
        @Override
        public void run() {
            if(firstStart) {
                firstStart = false;
                listener.onArrival();

                startSecs = System.currentTimeMillis() /1000;
            } else {
                calcData();
                listener.onUpdateData(secs,distance,calorie);
            }
        }
    };


    private long startSecs;

    private int secs = 0;
    private int distance = 0;
    private int calorie = 0;

    private void calcData() {
        secs = (int) (System.currentTimeMillis() / 1000 - startSecs);
        distance += new Random().nextInt(20);
        calcCalorie();
    }


    private double getMinuteSpeed() {
        return distance * 60/ secs;
    }

    private void calcCalorie() {
        int weight = 60;
        try {
            weight = Integer.parseInt(CurrentUser.instance().getPersonInfo().weight);
            if(weight == 0) {
                weight = 60;
            }
        }catch (Exception e) {
            weight = 60;
        }
        double hour = (double)(secs ) / 3600;
        double speed = getMinuteSpeed() / 400;
        calorie = (int) (weight * hour * speed * 30);
    }
}
