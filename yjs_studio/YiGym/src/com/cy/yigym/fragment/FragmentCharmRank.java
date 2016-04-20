package com.cy.yigym.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.adapter.CharmRankListAdapter;
import com.cy.yigym.adapter.TotalRankListAdapter;
import com.cy.yigym.entity.RankEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetRank;
import com.cy.yigym.net.rsp.RspGetRank;
import com.cy.yigym.utils.AddListViewMore;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 魅力榜
 * Created by ejianshen on 15/8/20.
 */
public class FragmentCharmRank extends BaseFragment implements AddListViewMore.ILoadListener {
    @BindView
    private AddListViewMore lvRank;
    @BindView
    private ImageView ivImageHeader;
    @BindView
    private ImageView ivSex;
    @BindView
    private TextView tvNickname;
    @BindView
    private TextView tvRank,tvContent,tvContent0,tvMore;
    private RankEntity rankEntity;
    private List<RankEntity> rankEntities=new ArrayList<RankEntity>();
    private CharmRankListAdapter charmRankListAdapter;
    private int myRank=0;
    private boolean isFirst=true;
    /**第几页*/
    private int pageNum=1;
    /**每页的数量*/
    private int pageSize=15;
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
        if(isFirst){
            getCharmRank(pageNum);
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    /**
     * 第一次获取魅力榜
     */
    private void getCharmRank(final int pages){
        YJSNet.send(new ReqGetRank("charmRank",pages,pageSize), LOG_TAG, new YJSNet.OnRespondCallBack<RspGetRank>() {
            @Override
            public void onSuccess(RspGetRank data) {
                isFirst=false;
                pageNum=pages+1;
                RspGetRank.Data rank = data.data;
                lvRank.setInterface(FragmentCharmRank.this);
                myRank = rank.myrank;
                rankList=rank.rank_list;
                initMyData();
                for (int i = 0; i < rank.rank_list.size(); i++) {
                    rankEntity = new RankEntity(rank.rank_list.get(i).nick_name
                            , rank.rank_list.get(i).profile_fid
                            ,rank.rank_list.get(i).total_chase
                            , rank.rank_list.get(i).total_distance);
                    rankEntities.add(rankEntity);
                }
                if(charmRankListAdapter==null){
                    charmRankListAdapter = new CharmRankListAdapter(getActivity(), rankEntities);
                    lvRank.setAdapter(charmRankListAdapter);
                }else{
                    onDataChange(rankEntities);
                }

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }
    /**
     * 初始化自己的排行
     * @param
     */
    private void initMyData(){
        tvRank.setText(myRank+"");
        tvContent0.setText("魅力榜第");
        tvContent.setText("名,努力就能上榜!");
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
        charmRankListAdapter.notifyDataSetChanged();
    }
    /**
     * 加载更多数据
     */
    @Override
    public void onLoad() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rankList.size()>0) {
                    getCharmRank(pageNum);
                    lvRank.LoadComplate();
                } else {

                    lvRank.LoadComplate();
                    WidgetUtils.showToast("已经没有了！");
                }
            }
        },1000);
    }
}
