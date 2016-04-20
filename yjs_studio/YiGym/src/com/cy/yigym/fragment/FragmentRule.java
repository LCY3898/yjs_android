package com.cy.yigym.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.ScoreAdapter;
import com.cy.yigym.entity.ScoreEntity;
import com.efit.sport.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/22.
 */
public class FragmentRule extends BaseFragment {


    @BindView
    private ListView lvScore;
    private ScoreEntity scoreEntity;
    private List<ScoreEntity> scoreEntities=new ArrayList<ScoreEntity>();
    private ScoreAdapter scoreAdapter;
    private String[][] datas={{"注册成功","建议完善个人资料","10"},{"完善个人信息","上传头像并完善个人信息","5"},{"完成运动目标","每天至多三次","10"},
            {"追Ta成功","每完成一次都可得分","10"},{"分享第三方","每条信息分享第三方既可得分","5"}};
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
        for(int i=0;i<datas.length;i++){
            scoreEntity=new ScoreEntity(datas[i][0],datas[i][1],datas[i][2]);
            scoreEntities.add(scoreEntity);
        }
        scoreAdapter=new ScoreAdapter(getActivity(),scoreEntities);
        lvScore.setAdapter(scoreAdapter);

    }
    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
