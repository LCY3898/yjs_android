package com.cy.yigym.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.EtyListAdapter;
import com.cy.yigym.entity.EtyListItem;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqCreateTask;
import com.cy.yigym.net.req.ReqGetTasks;
import com.cy.yigym.net.rsp.RspGetTasks;
import com.efit.sport.R;
import com.hhtech.base.AppUtils;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by eijianshen on 15/8/12.
 */
public class FragmentAllTaskList extends BaseFragment implements AdapterView.OnItemClickListener{

    @BindView
    private PullToRefreshListView lvnotify;

    private EtyListItem etyListItem;

    private ArrayList<EtyListItem> mlist;

    private EtyListAdapter etyListAdapter;
    private Context context;

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_listview;
    }

    @Override
    protected void initView(View contentView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addListviewItem();

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }


    private  void addListviewItem(){
        context=this.getActivity();
        int i=0;
        mlist=new ArrayList<EtyListItem>();
        YJSNet.send(new ReqGetTasks(), LOG_TAG, new YJSNet.OnRespondCallBack<RspBase>() {
            @Override
            public void onSuccess(RspBase data) {

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
        do{
            etyListItem=new EtyListItem();
            //etyListItem.headerFid  = R.drawable.h001;
            etyListItem.userName = "骑行到北京";
            etyListItem.content = "发起人：道哥";
            etyListItem.sysTime = "3小时23分后开始";
            etyListItem.joinNum = "50";
            mlist.add(etyListItem);
            i++;
        }while (i<10);

        etyListAdapter=new EtyListAdapter(context,mlist,false);
        //lvnotify.getRefreshableView();
        lvnotify.setAdapter(etyListAdapter);

        lvnotify.setMode(PullToRefreshBase.Mode.BOTH);
        lvnotify
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {

                        String label = DateUtils.formatDateTime(AppUtils.getAppContext(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);

                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);
                        onRefreshPullList();
                    }
                });

        // Add an end-of-list listener
        lvnotify
                .setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {

                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private Handler handler = new Handler();
    private void onRefreshPullList() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvnotify.onRefreshComplete();
            }
        }, 800);
    }

}
