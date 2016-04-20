package com.cy.yigym.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.ScoreAdapter;
import com.cy.yigym.entity.ScoreEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqShowScore;
import com.cy.yigym.net.rsp.RspShowScore;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ejianshen on 15/8/22.
 */
public class FragmentScore extends BaseFragment {

    private ScoreEntity scoreEntity;
    private List<ScoreEntity> scoreEntities=new ArrayList<ScoreEntity>();
    private ScoreAdapter scoreAdapter;
    @BindView
    private ListView lvScore;
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_score;
    }

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected void initView(View contentView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getScore();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    /**
     * 获取积分
     */
    private void getScore(){
        YJSNet.send(new ReqShowScore(), LOG_TAG, new YJSNet.OnRespondCallBack<RspShowScore>() {
            @Override
            public void onSuccess(RspShowScore data) {

                RspShowScore.Data score=data.data;
                Log.d(LOG_TAG,"d"+score.total_score);
                DataStorageUtils.setTotalScore(score.total_score);
                if(!score.scoreInfo.accomplishInfo.score.equals("0")){
                    Date date=new Date(Long.parseLong(score.scoreInfo.accomplishInfo.time)*1000);
                    String time=format.format(date);
                    scoreEntity=new ScoreEntity("完善用户信息",time,score.scoreInfo.accomplishInfo.score);
                    scoreEntities.add(scoreEntity);
                }
                if(!score.scoreInfo.register.score.equals("0")){
                    Date date=new Date(Long.parseLong(score.scoreInfo.register.time)*1000);
                    String time=format.format(date);
                    scoreEntity=new ScoreEntity("注册用户",time,"+"+score.scoreInfo.register.score);
                    scoreEntities.add(scoreEntity);
                }
                if(!score.scoreInfo.chase.score.equals("0")){
                    Date date=new Date(Long.parseLong(score.scoreInfo.chase.time)*1000);
                    String time=format.format(date);
                    scoreEntity=new ScoreEntity("完成追Ta骑行",time,"+"+score.scoreInfo.chase.score);
                    scoreEntities.add(scoreEntity);
                }
                if(!score.scoreInfo.achieveTarget.score.equals("0")){
                    Date date=new Date(Long.parseLong(score.scoreInfo.achieveTarget.time)*1000);
                    String time=format.format(date);
                    scoreEntity=new ScoreEntity("完成运动目标",time,"+"+score.scoreInfo.achieveTarget.score);
                    scoreEntities.add(scoreEntity);
                }
                if(!score.scoreInfo.share.score.equals("0")){
                    Date date=new Date(Long.parseLong(score.scoreInfo.share.time)*1000);
                    String time=format.format(date);
                    scoreEntity=new ScoreEntity("分享至第三方",time,"+"+score.scoreInfo.share.score);
                    scoreEntities.add(scoreEntity);
                }

                scoreAdapter=new ScoreAdapter(getActivity(),scoreEntities);
                lvScore.setAdapter(scoreAdapter);
            }

            @Override
            public void onFailure(String errorMsg) {
                WidgetUtils.showToast("获取积分失败" + errorMsg);
            }
        });
    }

}
