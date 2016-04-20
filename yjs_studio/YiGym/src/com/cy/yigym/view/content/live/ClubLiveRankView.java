package com.cy.yigym.view.content.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cy.wbs.UITimer;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.ClubLiveRankAdapter;
import com.cy.yigym.adapter.LiveRankAdapter;
import com.cy.yigym.entity.LiveRankEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetLiveRank;
import com.cy.yigym.net.rsp.RspGetLiveRank;
import com.cy.yigym.utils.AppModeHelper;
import com.cy.yigym.utils.AppUtils;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.DefaultAnimListener;
import com.efit.sport.R;
import com.sport.efit.theme.ColorTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/9/14.
 */
public class ClubLiveRankView extends BaseView {

	@BindView
	private View rlBack;

	@BindView
	private ListView lvLiveRank;

	@BindView
	private View rlRank;

	@BindView
	private ImageView ivMenu;
	@BindView
	private TextView tvLiveRank, tvNickname, tvCalorie;
	private LiveRankEntity entity;
	private List<LiveRankEntity> entityList;
	private AdapterBase adapter;
	private ClubLiveRankView liveRankView;
	private String LOG_TAG;
	private int currentPageSize = 0;
	private List<RspGetLiveRank.Values> liveRanks = new ArrayList<RspGetLiveRank.Values>();
	AdapterView.OnItemClickListener mItemClickListener;
	private LiveInfoView infoView;
	private double myCalorie;

	private TextView tvResist;
	private TextView tvRpm;

	private UITimer getLiveRank;
	Animation hideAnim;
	Animation showAnim;
	private boolean isLoginInClubMode = DataStorageUtils.isInClubMode();

	public ClubLiveRankView(Context context) {
		super(context);
	}

	public ClubLiveRankView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView() {
		isLoginInClubMode = DataStorageUtils.isInClubMode();
		if(isLoginInClubMode) {
			tvResist = (TextView)findViewById(R.id.tvResist);
			tvRpm = (TextView)findViewById(R.id.tvRpm);
		}

		rlRank.setBackgroundDrawable(ColorTheme.getLiveInfoBg());
		updateListView(liveRanks);
		getLiveRank = new UITimer();
		hideAnim = AnimationUtils.loadAnimation(AppUtils.getAppContext(),
				R.anim.option_leave_from_right);
		showAnim = AnimationUtils.loadAnimation(AppUtils.getAppContext(),
				R.anim.option_entry_from_right);
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

		showAnim.setDuration(300);
		hideAnim.setDuration(300);
		/**
		 * 每秒获取一次数据
		 */
		getLiveRank.schedule(new Runnable() {
			@Override
			public void run() {
				getRank();
			}
		}, 4000);
		/*rlBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleVisibility(false);
			}
		});
		ivMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleVisibility(true);
			}
		});
		*/

	}

	private void toggleVisibility(boolean visible) {
		if (visible) {
			rlRank.startAnimation(showAnim);
		} else {
			rlRank.startAnimation(hideAnim);
		}
	}

	/**
	 * 
	 * @param infoView
	 * @param LOG_TAG
	 */
	public void setInfoView(LiveInfoView infoView, ClubLiveRankView liveRankView,
			String LOG_TAG) {
		this.infoView = infoView;
		this.LOG_TAG = LOG_TAG;
		this.liveRankView = liveRankView;
	}

	/**
	 * 从服务器拉取数据排名
	 */
	private void getRank() {
		YJSNet.send(new ReqGetLiveRank(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGetLiveRank>() {
					@Override
					public void onSuccess(RspGetLiveRank data) {
						liveRanks = data.data.values;
						updateListView(liveRanks);
						tvLiveRank.setText(data.data.my_ref.myrank + "");
						/*myCalorie = data.data.my_ref.calorie;
						tvCalorie.setText(data.data.my_ref.calorie + "");
						tvNickname.setText(data.data.my_ref.nick_name);
						if (isLoginInClubMode) {
							tvResist.setText(String.valueOf(data.data.my_ref.resist));
							tvRpm.setText(String.valueOf(data.data.my_ref.speed_per_min));
						}*/
					}

					@Override
					public void onFailure(String errorMsg) {
					}
				});
	}

	private void updateListView(List<RspGetLiveRank.Values> liveRanks) {
		if (entityList == null) {
			entityList = new ArrayList<LiveRankEntity>();
		}
		entityList.clear();
		if (liveRanks == null)
			return;

		int clubUserCount = 0;

		for (int i = 0; i < liveRanks.size(); i++) {
			RspGetLiveRank.Values rankItem = liveRanks.get(i);
			int rank = rankItem.myrank;
			// 根据现场模式和家庭模式过滤
			if(isLoginInClubMode) {
				// 是现场模式，只要显示现场pad的用户排名
				if (!AppModeHelper.CLUB_MODE.equals(rankItem.login_mode)) {
					continue;
				}
				clubUserCount++;
				rank = clubUserCount;
			}
			entity = new LiveRankEntity(rankItem.voipAccount, rankItem.pid,
					rankItem.profile_fid, rank + "",
					rankItem.nick_name, rankItem.calorie + "", rankItem.speed
							+ "");
			entity.setCalorie(rankItem.calorie + "");
			entity.setDistance(rankItem.distance / 1000 + "");
			entity.setRpm((int) rankItem.speed_per_min);
			entity.setResist((int)rankItem.resist);
			entity.setFid(rankItem.profile_fid);
			entityList.add(entity);
		}
		if (adapter == null) {
			if(isLoginInClubMode) {
				adapter = new ClubLiveRankAdapter(getContext(),entityList);
			} else {
				adapter = new LiveRankAdapter(getContext(), entityList);
			}
			lvLiveRank.setAdapter(adapter);
			currentPageSize = entityList.size();
		} else {
			onDataChange(entityList);
		}
		/*lvLiveRank
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						if (infoView != null) {
							LiveRankEntity entity = (LiveRankEntity) parent
									.getItemAtPosition(position);
							liveRankView.setVisibility(GONE);
							infoView.setVisibility(View.VISIBLE);
							infoView.setUserData(entity);
						}
					}
				});*/
	}

	private void onDataChange(List<LiveRankEntity> list) {
		adapter.notifyDataSetChanged();
	}

	public void updateSelfData(int calorie,int resist,int rpm) {
		tvCalorie.setText(calorie + "");
		tvNickname.setText(DataStorageUtils.getUserNickName());
		if (isLoginInClubMode) {
			tvResist.setText(String.valueOf(resist));
			tvRpm.setText(String.valueOf(rpm));
		}
	}

	@Override
	protected int getContentViewId() {
		//tangtt do not remove this
		isLoginInClubMode = DataStorageUtils.isInClubMode();
			return R.layout.view_club_live_rank;
	}

	public void resume() {
	}

	public void pause() {

	}

	public void fini() {
		getLiveRank.cancel();
	}

	public String getMyRank() {
		return tvLiveRank.getText().toString();
	}

}
