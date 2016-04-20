package com.cy.yigym.view.content.live;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.entity.LiveVideoSportData;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqSportUpdate;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.efit.sport.chase.ChaseTaSelfData;

/**
 * Created by ejianshen on 15/9/14.
 */
public class ClubModeLiveSportDatas extends BaseView {

    @BindView
    private TextView tvDistance, tvSpeed, tvCalorie, tvRate,tvRes;
    private String LOG_TAG;

    private ChaseTaSelfData sportData;
    private long lastTimestamp = 0;

    public ClubModeLiveSportDatas(Context context) {
        super(context);
    }

    public ClubModeLiveSportDatas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View findViewById(String viewName) {
        return super.findViewById(viewName);
    }

    private final static int UPLOAD_INTERVAL = 4; // in seconds;
    private int count = 0;
    private int oldDistance;//过去的距离
    private int oldCalorie;//过去的卡路里

    private int distance;
    private int calorie;
    private int seconds;

    private LiveVideoSportData perisistData;

    private ClubLiveRankView clubLiveRankView;
    @Override
    protected void initView() {
        perisistData = new LiveVideoSportData();
    }


    public void setSportDataListener(ClubLiveRankView clubLiveRankView) {
        this.clubLiveRankView = clubLiveRankView;
    }


    public void initData(int startDistance,int startTime,int startCalorie) {
        perisistData.courseId = DataStorageUtils.getCourseId();
        distance = startDistance;
        seconds = startTime;
        calorie = startCalorie;
        oldDistance = startDistance;
        oldCalorie = startCalorie;
        if(sportData != null) {
            sportData.fini();
        }
        sportData = new ChaseTaSelfData(startDistance, startTime, startCalorie);
        sportData.setDataUpdateListener(new ChaseTaSelfData.OnDataChangeListener() {
            @Override
            public void onDataChange() {
                distance = sportData.getTotalDistance();
                calorie = sportData.getTotalCalorie();
                seconds = sportData.getTotalTime();
                int speedPerHour = sportData.getSpeedPerHour();
                int speedRate = sportData.getRoundPerMin();
                int resist = sportData.getResist();

                //updateView(distance,speedPerHour,calorie,speedRate);

                updateView(distance, speedPerHour, calorie, speedRate,resist);
                long now = System.currentTimeMillis();
                if(now - lastTimestamp >= 4000) {
                    Log.i("ClubMode","sport update");
                    lastTimestamp = now;
                    //TODO: add resistance
                    upLoadSportData(distance, speedPerHour, calorie,
                            speedRate, resist, oldDistance, oldCalorie);
                    oldDistance = distance;
                    oldCalorie = calorie;
                    getSportData(perisistData.courseId);
                    DataStorageUtils.setLiveVideoData(perisistData);
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.view_club_sports_data;
    }

    /**
     * 实时显示自己的运动数据
     */

    private void updateView(int distance, int speedPerHour, int calorie, int speedRate,int resist) {

        tvDistance.setText(String.format("%.2f", distance / 1000.0));
        tvSpeed.setText(String.format("%.2f", speedPerHour / 1.0));
        tvCalorie.setText(calorie + "");
        tvRate.setText(String.format("%d", speedRate));
        tvRes.setText(String.format("%d", resist));
        if(clubLiveRankView != null) {
            clubLiveRankView.updateSelfData(calorie,resist,speedRate);
        }

    }

    public void setLogTag(String LOG_TAG) {
        this.LOG_TAG = LOG_TAG;
    }

    /**
     * 上传自己的运动数据
     */
    private void upLoadSportData(final int distance, final int speedPerHour, final int calorie
            ,final int speedRate, final int resist, final int oldDistance, final int oldCalorie) {
        YJSNet.send(new ReqSportUpdate(distance, speedPerHour, calorie, speedRate, resist, oldDistance, oldCalorie), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspBase>() {
                    @Override
                    public void onSuccess(RspBase data) {

                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        Log.d("tttt", "errorMsg=======" + errorMsg);
                    }
                });
    }

    public void fini() {
        sportData.fini();
    }


    public LiveVideoSportData getSportData(String courseId) {
        perisistData.courseId = courseId;
        perisistData.calorie = calorie;
        perisistData.distance = distance;
        perisistData.seconds = seconds;
        return perisistData;
    }

    //TextView tvDistance, tvSpeed, tvCalorie, tvRate,tvRes;
    public String getDistance() {
        return tvDistance.getText().toString();
    }

    public String getSpeed() {
        return tvSpeed.getText().toString();
    }

    public String getCalorie() {
        return tvCalorie.getText().toString();
    }

    public String getRpm() {
        return tvRate.getText().toString();
    }

    public String getRes() {
        return tvRes.getText().toString();
    }
}
