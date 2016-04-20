package com.cy.yigym.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.ChaseListAdapter;
import com.cy.yigym.adapter.ChaseListFinishAdapter;
import com.cy.yigym.aty.AtyChaseHer;
import com.cy.yigym.entity.ChaseIntentEntity;
import com.cy.yigym.entity.ChaseListEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetChaseRecord;
import com.cy.yigym.net.req.ReqGetChaseRecords;
import com.cy.yigym.net.rsp.RspGetChaseRecord;
import com.cy.yigym.net.rsp.RspGetChaseRecords;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.CustomHeightLimitListView;
import com.efit.sport.R;
import com.hhtech.utils.DimenUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/7/29.
 */
public class FragmentChaseHistory extends BaseFragment implements AdapterView.OnItemClickListener {
    @BindView
    private View unfinishLayout;

    @BindView
    private View doneLayout;


    private ChaseListEntity chaseListEntity;
    private ChaseListEntity chaseListFinishEntity;
    @BindView
    private CustomHeightLimitListView lvDoing;
    @BindView
    private ListView lvDone;
    private List<ChaseListEntity> chaseListEntities =new ArrayList<ChaseListEntity>();
    private List<ChaseListEntity> chaseListFinishEntities =new ArrayList<ChaseListEntity>();
    private ChaseListAdapter chaseListAdapter;
    private ChaseListFinishAdapter chaseListFinishAdapter;
    DecimalFormat df= new DecimalFormat("######0.00");

    private boolean isUpdating = false;

    @BindView
    private View emptyView;

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.frag_chase_history;
    }
    @Override
    protected void initView(View contentView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lvDoing.setMaxHeight(DimenUtils.dpToPx(240));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initData(Bundle savedInstanceState){

        YJSNet.send(new ReqGetChaseRecords(), LOG_TAG, new YJSNet.OnRespondCallBack<RspGetChaseRecords>() {
            @Override
            public void onSuccess(RspGetChaseRecords data) {
                RspGetChaseRecords.Data record = data.data;
                //判断是否有纪录
                if (record.crs_sList.size() == 0 && record.crs_uList.size() == 0) {
                    //WidgetUtils.showToast("兄台，你还没有纪录哦！快去追ta吧");
                    unfinishLayout.setVisibility(View.GONE);
                    doneLayout.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    //未完成的纪录
                    Log.d(LOG_TAG, "size" + record.crs_uList.size());
                    for (int i = 0; i < record.crs_uList.size(); i++) {
                        chaseListEntity = new ChaseListEntity(record.crs_uList.get(i).profile_fid,
                                record.crs_uList.get(i).anotherName, record.crs_uList.get(i).apart_distance);
                        chaseListEntity.set_id(record.crs_uList.get(i)._id);
                        chaseListEntities.add(chaseListEntity);
                    }
                    chaseListAdapter = new ChaseListAdapter(mActivity, chaseListEntities);
                    lvDoing.setAdapter(chaseListAdapter);
                    if (record.crs_uList.size() > 0) {
                        //WidgetUtils.showToast("兄台，你有未完成的纪录！快去追ta吧");
                    } else {
                        unfinishLayout.setVisibility(View.GONE);
                    }
                    //已经完成的纪录
                    if(record.crs_sList.size() == 0) {
                        doneLayout.setVisibility(View.GONE);
                    }
                    for (int j = 0; j < record.crs_sList.size(); j++) {
                        chaseListFinishEntity = new ChaseListEntity(record.crs_sList.get(j).profile_fid,
                                record.crs_sList.get(j).anotherName, record.crs_sList.get(j).apart_distance);
                        chaseListFinishEntity.set_id(record.crs_sList.get(j)._id);
                        chaseListFinishEntities.add(chaseListFinishEntity);
                    }
                    chaseListFinishAdapter = new ChaseListFinishAdapter(mActivity, chaseListFinishEntities);
                    lvDone.setAdapter(chaseListFinishAdapter);
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                WidgetUtils.showToast("读取历史纪录失败" + errorMsg);
            }
        });
        lvDoing.setOnItemClickListener(this);
        lvDone.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isUpdating) {
            return;
        }
        isUpdating = true;

        switch (parent.getId()){
            case R.id.lvDoing:
                chaseListEntity=(ChaseListEntity)chaseListAdapter.getItem(position);
                notFinishListClick();
                break;
            case R.id.lvDone:
                chaseListFinishEntity=(ChaseListEntity)chaseListFinishAdapter.getItem(position);
                finishListClick();
                break;
        }
    }
    //点击未完成记录的列表
    private void notFinishListClick(){
        Log.d(LOG_TAG, "pid"+ chaseListEntity.get_id());
        YJSNet.send(new ReqGetChaseRecord(chaseListEntity.get_id()), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspGetChaseRecord>() {
                    @Override
                    public void onSuccess(RspGetChaseRecord data) {
                        Log.d(LOG_TAG, "id" + data.data.chaseRecord._id);
                        Intent intent = new Intent(mActivity, AtyChaseHer.class);
                        DataStorageUtils.setOtherPid(data.data.chaseRecord.receiver_id);
                        ChaseIntentEntity chaseList = new ChaseIntentEntity(chaseListEntity.getNickname(),data,true);
                        intent.putExtra("flag", 0);
                        intent.putExtra(ChaseIntentEntity.INTENT_KEY, chaseList);
                        startActivity(intent);
                        isUpdating = false;
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        Log.d(LOG_TAG, "错误" + errorMsg);
                        isUpdating = false;
                    }
                });
    }
    //点击已完成记录列表
    private void finishListClick(){

        YJSNet.send(new ReqGetChaseRecord(chaseListFinishEntity.get_id()), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspGetChaseRecord>() {
                    @Override
                    public void onSuccess(RspGetChaseRecord data) {
                        Log.d(LOG_TAG, "id" + data.data.chaseRecord._id);
                        Intent intent=new Intent(mActivity,AtyChaseHer.class);
                        DataStorageUtils.setOtherPid(data.data.chaseRecord.receiver_id);
                        ChaseIntentEntity chaseList=new ChaseIntentEntity(chaseListFinishEntity.getNickname(),data,false);
                        intent.putExtra("flag",0);
                        intent.putExtra(ChaseIntentEntity.INTENT_KEY,chaseList);
                        startActivity(intent);
                        isUpdating = false;
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        Log.d(LOG_TAG,"错误"+errorMsg);
                        isUpdating = false;
                    }
                });
    }


}
