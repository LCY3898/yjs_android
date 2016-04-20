package com.cy.yigym.view.content.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.entity.LiveRankEntity;
import com.cy.yigym.utils.HeaderHelper;
import com.efit.sport.R;
import com.efit.sport.videochat.LiveVideoView;
import com.sport.efit.theme.ColorTheme;

/**
 * author: tangtt
 * <p>
 * create at 2015/11/23
 * </p>
 * <p>
 * 往期课程个人详细信息
 * </p>
 */
public class HistoryInfoView extends BaseView {
	@BindView
	private View rlLiveInfo, rlBack;
	private LiveVideoView liveVideoView;
	private HistoryRankView rankView;
	@BindView
	private CustomCircleImageView ivHeader;

	private LiveRankEntity rankEntity;

	@BindView
	private TextView tvNickname, tvDistance, tvSpeed, tvCalorie, tvRate,tvRes;

	public HistoryInfoView(Context context) {
		super(context);
	}

	public HistoryInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView() {
		setBackgroundDrawable(ColorTheme.getLiveInfoBg());
		rlBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showView(false);
				rankView.setVisibility(VISIBLE);
			}
		});
	}

	private void showView(boolean show) {
		setVisibility(show ? VISIBLE : GONE);
	}

	public void setRankView(HistoryRankView rankView) {
		this.rankView = rankView;
	}

	public void setUserData(LiveRankEntity entity) {
		rankEntity = entity;
		HeaderHelper.load(entity.getFid(), ivHeader, R.drawable.h001);
		tvNickname.setText(entity.getNickname());

		tvDistance.setText(entity.getDistance() + "");
		tvSpeed.setText(entity.getSpeed() + "");
		tvCalorie.setText(entity.getCalorie() + "");
		tvRate.setText(entity.getRpm() + "");
		tvRes.setText(entity.getResist());
	}

	@Override
	protected int getContentViewId() {
		return R.layout.view_history_info;
	}
}
