package com.cy.yigym.view.content.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.LiveRankAdapter;
import com.cy.yigym.entity.LiveRankEntity;
import com.cy.yigym.fragment.live.FragVideoBase;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqReviewHistoryDataRank;
import com.cy.yigym.net.rsp.RspGetReviewHistoryRank;
import com.cy.yigym.utils.AppUtils;
import com.cy.yigym.utils.DefaultAnimListener;
import com.efit.sport.R;
import com.sport.efit.theme.ColorTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * author: tangtt
 * <p>
 * create at 2015/11/23 13:46
 * </p>
 * <p>
 * 往期课程排名
 * </p>
 */
public class HistoryRankView extends BaseView {

    @BindView
    private View rlBack;
    @BindView
    private ListView lvLiveRank;
    @BindView
    private ImageView ivMenu;
    @BindView
    private TextView tvLiveRank, tvNickname, tvCalorie;

    @BindView
    private RelativeLayout rlRank;

    private LiveRankEntity entity;
    private List<LiveRankEntity> entityList;
    private LiveRankAdapter adapter;
    private String LOG_TAG;
    private int currentPageSize = 0;
    private List<RspGetReviewHistoryRank.RankItem> liveRanks = new ArrayList<RspGetReviewHistoryRank.RankItem>();
    AdapterView.OnItemClickListener mItemClickListener;
    private double myCalorie;

    private HistoryRankView rankView;
    private UITimer getRank;
    private HistoryInfoView infoView;
    private Animation hideAnim;
    private Animation showAnim;
    private FragVideoBase.PlayInfoRetriver retriver;

    public HistoryRankView(Context context) {
        super(context);
    }

    public HistoryRankView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPlayInfoRetriver(FragVideoBase.PlayInfoRetriver retriver) {
        this.retriver = retriver;
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void initView() {
    	rlRank.setBackgroundDrawable(ColorTheme.getLiveInfoBg());
        updateListView(liveRanks);
        getRank = new UITimer();
        hideAnim = AnimationUtils.loadAnimation(AppUtils.getAppContext(),
                R.anim.option_leave_from_right);
        showAnim = AnimationUtils.loadAnimation(AppUtils.getAppContext(),
                R.anim.option_entry_from_right);
        /*showAnim.setDuration(300);
        hideAnim.setDuration(300);*/

        hideAnim.setAnimationListener(new DefaultAnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                rlRank.setVisibility(INVISIBLE);
                ivMenu.setVisibility(VISIBLE);
            }
        });
        showAnim.setAnimationListener(new DefaultAnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                rlRank.setVisibility(VISIBLE);
                ivMenu.setVisibility(INVISIBLE);
            }
        });

        /**
         * 每秒获取一次数据
         */
        getRank.schedule(new Runnable() {
            @Override
            public void run() {
                if(retriver == null || !retriver.isJoinSucceed()) {
                    return;
                }
                getRank();
            }
        }, 4000);
        rlBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleShow(false);
            }
        });
        ivMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleShow(true);
            }
        });

    }

    private void toggleShow(boolean show) {
        if (show) {
            rlRank.startAnimation(showAnim);
        } else {
            rlRank.startAnimation(hideAnim);
        }
    }

    /**
     * @param LOG_TAG
     */
    public void setInfoView(HistoryInfoView infoView,HistoryRankView rankView, String LOG_TAG) {
        this.LOG_TAG = LOG_TAG;
        this.infoView = infoView;
        this.rankView=rankView;
    }

    /**
     * 从服务器拉取数据排名
     */
    private void getRank() {
        if(retriver == null) {
            return;
        }
        YJSNet.send(new ReqReviewHistoryDataRank(retriver.getCourseId(),
                HistorySportDatas.UPLOAD_INTERVAL,retriver.getPlayTime()), LOG_TAG, new YJSNet.OnRespondCallBack<RspGetReviewHistoryRank>() {
            @Override
            public void onSuccess(RspGetReviewHistoryRank data) {
                liveRanks = data.data.values;
                updateListView(liveRanks);
                tvLiveRank.setText(data.data.myRank.myrank + "");
                myCalorie = data.data.myRank.value;
                tvCalorie.setText(data.data.myRank.value + "");
                tvNickname.setText(data.data.myRank.nick_name);
            }

            @Override
            public void onFailure(String errorMsg) {
            }
        });
    }

    private double calcSpeed(double distance) {
        if(retriver.getPlayTime() == 0) {
            return 0;
        }
        return distance* 3600 / retriver.getPlayTime()/1000 ;
    }

    private void updateListView(List<RspGetReviewHistoryRank.RankItem> liveRanks) {
        if (entityList == null) {
            entityList = new ArrayList<LiveRankEntity>();
        }
        entityList.clear();
        if (liveRanks == null)
            return;
        for (int i = 0; i < liveRanks.size(); i++) {
            RspGetReviewHistoryRank.RankItem rankItem = liveRanks.get(i);
            entity = new LiveRankEntity("", rankItem.pid, rankItem.profile_fid,
                    i + "", rankItem.nick_name, rankItem.value + "",rankItem.speed+"");
            entity.setCalorie(rankItem.value + "");
            entity.setDistance(rankItem.distance / 1000 + "");
            entity.setRpm((int) rankItem.speed_per_min);
            entity.setResist((int)rankItem.resist);
            entity.setFid(rankItem.profile_fid);
            entityList.add(entity);
        }
        if (adapter == null) {
            adapter = new LiveRankAdapter(getContext(), entityList);
            lvLiveRank.setAdapter(adapter);
            currentPageSize = entityList.size();
        } else {
            onDataChange(entityList);
        }
        lvLiveRank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (infoView != null) {
                    LiveRankEntity entity = (LiveRankEntity) parent.getItemAtPosition(position);
                    toggleSelfVisibility(false);
                    infoView.setVisibility(View.VISIBLE);
                    infoView.setUserData(entity);
                }
            }
        });
    }

    private void toggleSelfVisibility(boolean show) {
        setVisibility(show ? VISIBLE : GONE);
    }

    private void onDataChange(List<LiveRankEntity> list) {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.view_live_rank;
    }

    public void resume() {

    }

    public void pause() {

    }

    public void fini() {
        getRank.cancel();
    }

}
