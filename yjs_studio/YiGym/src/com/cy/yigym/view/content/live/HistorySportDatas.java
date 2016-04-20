package com.cy.yigym.view.content.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.fragment.live.FragVideoBase;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqReviewSportUpdate;
import com.efit.sport.R;
import com.efit.sport.chase.ChaseTaSelfData;
import com.efit.sport.chase.FaceBikeData;
import com.efit.sport.chase.SerialSportData;

/**
 * author: tangtt
 * <p>
 * create at 2015/11/23
 * </p>
 * <p>
 * 往期视频里的运动数据上传
 * </p>
 */
public class HistorySportDatas extends BaseView {

    @BindView
    private TextView tvDistance,tvSpeed,tvCalorie,tvRate,tvRes;
    private String LOG_TAG;

    //private FaceBikeData sportData;
    private ChaseTaSelfData sportData;
    public HistorySportDatas(Context context) {
        super(context);
    }

    public HistorySportDatas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View findViewById(String viewName) {
        return super.findViewById(viewName);
    }

    public final static int UPLOAD_INTERVAL = 4; // in seconds;
    private int lastTime;//in seconds;
    private int lastDistance;//in meter;
    private int lastCalorie;

    private long lastTimestamp = 0;

    private FragVideoBase.PlayInfoRetriver retriver;
    @Override
    protected void initView() {
    }


    public void setPlayInfoRetriver(FragVideoBase.PlayInfoRetriver retriver) {
        this.retriver = retriver;
    }

    public void initHistoryData(int time,int distance,int calorie) {
        this.lastTime = time;
        this.lastDistance = distance;
        this.lastCalorie = calorie;

        if(sportData != null) {
            sportData.fini();
        }

        sportData = new ChaseTaSelfData(lastDistance,lastTime,lastCalorie);
        //sportData = new SerialSportData(lastDistance,lastTime,lastCalorie);
        sportData.setDataUpdateListener(new ChaseTaSelfData.OnDataChangeListener() {
            @Override
            public void onDataChange() {

                int distance = sportData.getTotalDistance();
                int speedPerHour = sportData.getSpeedPerHour();
                int calorie = sportData.getTotalCalorie();
                int speedRate = sportData.getRoundPerMin();
                int resist = sportData.getResist();


                updateView(distance,speedPerHour,calorie,speedRate,resist);
                long now = System.currentTimeMillis();
                if(now - lastTimestamp >= 4000) {
                    lastTimestamp = now;

                    //TODO: add resistance
                    upLoadSportData(distance, calorie,speedPerHour,speedRate,resist, lastDistance, lastCalorie);
                    lastCalorie = calorie;
                    lastDistance = distance;
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.view_sports_data;
    }

    /**
     * 实时显示自己的运动数据
     */

    private void updateView(int distance,int speedPerHour,int calorie,int speedRate,int resist) {
        tvDistance.setText(String.format("%.2f",distance/1000.0));
        tvSpeed.setText(String.format("%.2f",speedPerHour/1.0));
        tvCalorie.setText(calorie+ "");
        tvRate.setText(String.format("%d",speedRate));
        tvRes.setText(String.format("%d",resist));
    }

    public void setLogTag(String LOG_TAG){
        this.LOG_TAG=LOG_TAG;
    }

    /**
     * 上传自己的运动数据
     */
    private void upLoadSportData(final int distance, final int calorie
            , final int speedPerHour,final int speedRate, final int resist, final int oldDistance, final int oldCalorie) {
        if(retriver == null || !retriver.isJoinSucceed()) {
            return;
        }

        YJSNet.send(new ReqReviewSportUpdate(retriver.getCourseId(),distance, calorie, speedPerHour,speedRate,resist,retriver.getPlayTime(),
                        UPLOAD_INTERVAL,oldDistance,oldCalorie), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspBase>() {
                    @Override
                    public void onSuccess(RspBase data) {

                    }

                    @Override
                    public void onFailure(String errorMsg) {
                    }
                });
    }

    public void fini() {
        if(sportData != null) {
            sportData.fini();
        }
        retriver = null;
    }
}
