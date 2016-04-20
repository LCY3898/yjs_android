package com.cy.yigym.fragment;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.adapter.TotalRankListAdapter;
import com.cy.yigym.entity.RankEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetRank;
import com.cy.yigym.net.rsp.RspGetRank;
import com.cy.yigym.utils.AddListViewMore;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

/**
 * 总排行榜
 * Created by ejianshen on 15/8/20.
 */
public class FragmentTotalRank extends BaseFragment implements AddListViewMore.ILoadListener {
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
    private TotalRankListAdapter totalRankListAdapter;
    private List<RspGetRank.Array> rankList=new ArrayList<RspGetRank.Array>();
    private boolean isFirst=true;
//    private int cnt,i;
    private int currentCnt,currentI;
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
            getTotalRank(pageNum);
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    /**
     *  第一次获取获取运动总排行榜
     */
    private void getTotalRank(final int pages){
        YJSNet.send(new ReqGetRank("totalRank",pages,pageSize), LOG_TAG, new YJSNet.OnRespondCallBack<RspGetRank>() {
            @Override
            public void onSuccess(RspGetRank data) {
                isFirst=false;
                RspGetRank.Data rank = data.data;
                lvRank.setInterface(FragmentTotalRank.this);
                myRank=rank.myrank;
                pageNum=pages+1;
                rankList=rank.rank_list;
                initMyData();
                for (int i = 0; i < rank.rank_list.size(); i++) {
                    rankEntity = new RankEntity(rank.rank_list.get(i).total_distance
                            , rank.rank_list.get(i).total_time
                            , rank.rank_list.get(i).total_calorie
                            , rank.rank_list.get(i).nick_name
                            , rank.rank_list.get(i).profile_fid);
                    rankEntities.add(rankEntity);
                }
                if(totalRankListAdapter==null){
                    totalRankListAdapter = new TotalRankListAdapter(getActivity(), rankEntities);
                    lvRank.setAdapter(totalRankListAdapter);
                }else{
                    totalRankListAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }
    /**
     * 初始化自己的排行
     * @param rank
     */
    private void initMyData(){
        tvRank.setText(myRank+"");
        tvContent0.setText("总排行第");
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
        totalRankListAdapter.notifyDataSetChanged();
    }
    /**
     *加载更多
     */

    @Override
    public void onLoad() {
        Handler handler=new Handler();
        Log.d(LOG_TAG,"currentI"+currentI+"currentCnt"+currentCnt+"size"+rankList.size());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rankList.size()>0) {
                    getTotalRank(pageNum);
                    lvRank.LoadComplate();
                } else {
                    lvRank.LoadComplate();
                    WidgetUtils.showToast("已经没有了！");
                }
            }
        },1000);
    }
}
