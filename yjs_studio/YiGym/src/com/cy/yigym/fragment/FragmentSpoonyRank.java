package com.cy.yigym.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.adapter.CharmRankListAdapter;
import com.cy.yigym.adapter.SpoonyRankListAdapter;
import com.cy.yigym.entity.RankEntity;

import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetCrazeRank;
import com.cy.yigym.net.req.ReqGetRank;
import com.cy.yigym.net.rsp.RspGetCrazeRank;
import com.cy.yigym.net.rsp.RspGetRank;
import com.cy.yigym.utils.AddListViewMore;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.hhtech.base.AppUtils;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 痴情榜
 * Created by ejianshen on 15/8/20.
 */
public class FragmentSpoonyRank extends BaseFragment implements AddListViewMore.ILoadListener {
    @BindView
    private AddListViewMore lvRank;
    @BindView
    private ImageView ivImageHeader;
    @BindView
    private ImageView ivSex;
    @BindView
    private TextView tvNickname;
    @BindView
    private TextView tvRank,tvContent,tvContent0;
    private int myRank;
    /**第几页*/
    private int pageNum=1;
    /**每页的数量*/
    private int pageSize=15;
    private RankEntity rankEntity;
    private List<RankEntity> rankEntities=new ArrayList<RankEntity>();
    private SpoonyRankListAdapter spoonyRankListAdapter;
    private Context context;
    private boolean isFirst=true;
    private int currentCnt,currentI;
    private List<RspGetRank.Array> rankList=new ArrayList<RspGetRank.Array>();

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_rank;
    }

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected void initView(View contentView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getSpoonyRank(pageNum);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
            context=this.getActivity();

    }

    /**
     * 第一次获取痴情榜
     */
    private void getSpoonyRank(final int pages){
        YJSNet.send(new ReqGetRank("crazeRank",pages,pageSize), LOG_TAG, new YJSNet.OnRespondCallBack<RspGetRank>() {
            @Override
            public void onSuccess(RspGetRank data) {
                RspGetRank.Data rank=data.data;
                lvRank.setInterface(FragmentSpoonyRank.this);
                pageNum=pages+1;
                rankList=rank.rank_list;
                myRank=rank.myrank;
                initMyData(myRank);
               for(int i=0;i<rank.rank_list.size();i++){
                   rankEntity = new RankEntity(rank.rank_list.get(i).nick_name
                           , rank.rank_list.get(i).profile_fid
                           ,rank.rank_list.get(i).total_distance);
                   rankEntities.add(rankEntity);
               }
                if(rankEntities!=null){
                    spoonyRankListAdapter=new SpoonyRankListAdapter(context,rankEntities);
                    lvRank.setAdapter(spoonyRankListAdapter);
                }else{
                    onDataChange(rankEntities);
                }
               // refreshData();

            }

            @Override
            public void onFailure(String errorMsg) {
                WidgetUtils.showToast("获取痴情榜失败！"+errorMsg);
            }
        });
    }

    /**
     * 初始化自己的排行
     * @param rank
     */
    private void initMyData(int rank){
        tvRank.setText(myRank+"");
        tvContent0.setText("痴情榜第");
        tvContent.setText("名,唯爱和健康不可辜负!");
        ImageLoaderUtils.getInstance().loadImage(
                DataStorageUtils.getHeadDownloadUrl(DataStorageUtils.getCurUserProfileFid()), ivImageHeader);
        tvNickname.setText(CurrentUser.instance().getNickname());
        if(CurrentUser.instance().getPersonInfo().sex.equals("男")){
            ivSex.setImageResource(R.drawable.cat_man_on);
        }else{
            ivSex.setImageResource(R.drawable.cat_woman_on);
        }
    }
    private void onDataChange(List<RankEntity> rankEntities){
        this.rankEntities=rankEntities;
        spoonyRankListAdapter.notifyDataSetChanged();
    }
    /**
     * 加载更多
     */
    @Override
    public void onLoad() {
        Log.d("dddd","rank_list"+rankList.size()+pageNum);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rankList.size()>0) {
                    getSpoonyRank(pageNum);
                    lvRank.LoadComplate();
                } else {
                    lvRank.LoadComplate();
                    WidgetUtils.showToast("已经没有了！");
                }
            }
        },1000);
    }
}
