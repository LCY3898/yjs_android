package com.cy.yigym.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.MyTracesAdapter;
import com.cy.yigym.entity.MyTracesEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqManyWatchProgress;
import com.cy.yigym.net.rsp.RspManyWatchProgress;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.efit.sport.livecast.AtyLiveCast;
import com.efit.sport.livecast.LiveCastHelper;
import com.efit.sport.persist.bean.VideoHistDao;
import com.efit.sport.persist.bean.VideoHistory;
import com.umeng.socialize.utils.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ejianshen on 15/11/9.
 */
public class FragmentMyTraces extends BaseFragment {

    @BindView
    private ListView listView;
    @BindView
    private RelativeLayout lvEmptyTip;
    private MyTracesEntity myTracesEntity;
    private List<MyTracesEntity> myTracesEntityList = new ArrayList<MyTracesEntity>();
    private MyTracesAdapter adapter;
    private SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_my_traces;
    }

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected void initView(View contentView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView.setEmptyView(lvEmptyTip);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        //getWatchingVideoListFromLocal();
        getMyAllWatchListFromWeb();
    }

    private void getMyAllWatchListFromWeb() {
        YJSNet.send(new ReqManyWatchProgress(), LOG_TAG, new YJSNet.OnRespondCallBack<RspManyWatchProgress>() {
            @Override
            public void onSuccess(RspManyWatchProgress data) {


                for (RspManyWatchProgress.WatchProgressRecord bean : data.data) {
                    VideoHistory record = buildVideoHistory(bean);

                    String time = format1.format(new Date(1000L * record.lastPlayTime));
                    String where = formatWhere(record);
                    Log.i("where",where);
                    myTracesEntity = new MyTracesEntity(record.courseId,
                            record.courseName
                            , record.coachName
                            , record.videoFid
                            , where
                            , time);
                    myTracesEntity.setVideoUrl(record.videoUrl);
                    myTracesEntityList.add(myTracesEntity);

                    VideoHistDao.addOrUpdate(record);
                }

                if (adapter == null) {
                    adapter = new MyTracesAdapter(mActivity, myTracesEntityList);
                    listView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        myTracesEntity = myTracesEntityList.get(position);
                        //String videoLinkUrl = DataStorageUtils.getMp4Url(myTracesEntity.getVideoUrl());
                        String videoLinkUrl = myTracesEntity.getVideoUrl();
                        if (TextUtils.isEmpty(videoLinkUrl)) {
                            WidgetUtils.showToast("链接为空,视频已下架");
                            return;
                        }
                        Intent intent = new Intent(mActivity, AtyLiveCast.class);
                        LiveCastHelper.saveVideoInfo(intent, LiveCastHelper.genVideoInfo(
                                myTracesEntity.courseId,
                                myTracesEntity.getCaochName(), myTracesEntity.getCourseName(),
                                -1, videoLinkUrl, myTracesEntity.videoAvatarId));
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(String errorMsg) {
                Log.d("tttt", "ERROR" + errorMsg);
            }
        });
    }


    private VideoHistory buildVideoHistory(RspManyWatchProgress.WatchProgressRecord record) {

        VideoHistory history = new VideoHistory();
        history.pid = DataStorageUtils.getPid();

        history.courseId = record.courseId;
        history.videoUrl = record.videoUrl;
        history.videoFid = record.videoFid;
        history.courseName = record.courseName;
        history.coachName = record.coachName;
        history.calorie = record.calorie;
        history.duration = record.duration;
        history.beginTime = record.beginTime;
        history.endTime = record.endTime;
        history.lastPlayTime = record.lastPlayTime;
        history.lastPlayPostion = record.lastPlayPostion;
        history.continueTipStatus = record.continueTipStatus;

        return history;
    }


    private void getWatchingVideoListFromLocal() {

        List<VideoHistory> list = VideoHistDao.getMyAllHistory();
        for (VideoHistory record : list) {
            String time = format1.format(new Date(1000L * record.lastPlayTime));
            String where = formatWhere(record);
            myTracesEntity = new MyTracesEntity(record.courseId,
                    record.courseName
                    , record.coachName
                    , record.videoFid
                    , where
                    , time);
            myTracesEntity.setVideoUrl(record.videoUrl);
            myTracesEntityList.add(myTracesEntity);
        }
        if (adapter == null) {
            adapter = new MyTracesAdapter(mActivity, myTracesEntityList);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myTracesEntity = myTracesEntityList.get(position);
                //String videoLinkUrl = DataStorageUtils.getMp4Url(myTracesEntity.getVideoUrl());
                String videoLinkUrl = myTracesEntity.getVideoUrl();
                if (TextUtils.isEmpty(videoLinkUrl)) {
                    WidgetUtils.showToast("链接为空,视频已下架");
                    return;
                }
                Intent intent = new Intent(mActivity, AtyLiveCast.class);
                LiveCastHelper.saveVideoInfo(intent, LiveCastHelper.genVideoInfo(
                        myTracesEntity.courseId,
                        myTracesEntity.getCaochName(), myTracesEntity.getCourseName(),
                        -1, videoLinkUrl, myTracesEntity.videoAvatarId));
                startActivity(intent);
            }
        });
    }

    private String formatWhere(VideoHistory record) {
        String where = "已看完";
        if (record.duration != record.lastPlayPostion) {
            double wh = (double)record.lastPlayPostion / record.duration;
            int pInt = (int) (wh * 100);
            pInt = pInt > 0 ? pInt : 1;
            String per = String.valueOf(pInt);
            where = "观看至" + per + "%";
        }
        return where;
    }

}
