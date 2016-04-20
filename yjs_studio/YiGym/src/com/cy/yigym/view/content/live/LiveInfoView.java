package com.cy.yigym.view.content.live;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.entity.LiveRankEntity;
import com.cy.yigym.utils.AppModeHelper;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;
import com.efit.sport.R;
import com.efit.sport.videochat.LiveVideoView;
import com.sport.efit.theme.ColorTheme;

import java.text.DecimalFormat;

/**
 * Created by ejianshen on 15/9/15.
 */
public class LiveInfoView extends BaseView {
	@BindView
	private Button btnSendVideo;
	private DlgLiveToSb dlgLiveToSb;
	@BindView
	private View rlLiveInfo, rlBack;
	private LiveVideoView liveVideoView;
	private LiveRankView liveRankView;
	@BindView
	private CustomCircleImageView ivHeader;

	private LiveRankEntity rankEntity;

	static DecimalFormat df = new DecimalFormat("0.00");

	@BindView
	private TextView tvNickname, tvDistance, tvSpeed, tvCalorie, tvRate,tvRes;

	public LiveInfoView(Context context) {
		super(context);
	}

	public LiveInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView() {
		setBackgroundDrawable(ColorTheme.getLiveInfoBg());
		btnSendVideo.setBackgroundDrawable(ColorTheme.getLiveRedBtnSelector());
		dlgLiveToSb = new DlgLiveToSb(mContext);
		btnSendVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (rankEntity == null || rankEntity.pid == null
						|| rankEntity.pid.equals(DataStorageUtils.getPid())) {
					WidgetUtils.showToast("亲，无法跟自己视频哦");
					return;
				}
				if (liveVideoView.getVisibility() == VISIBLE) {
					WidgetUtils.showToast("请先结束当前视频");
					return;
				}
				dlgLiveToSb.show();
			}
		});
		dlgLiveToSb.getBtnSure().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (rankEntity == null || TextUtils.isEmpty(rankEntity.pid)) {
					WidgetUtils.showToast("无效的用户");
					return;
				}
				liveVideoView.setVisibility(VISIBLE);
				liveVideoView.startCall(rankEntity);
				dlgLiveToSb.dismiss();
			}
		});
		dlgLiveToSb.getBtnCancel().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgLiveToSb.dismiss();
			}
		});
		rlBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showView(false);
				liveRankView.setVisibility(VISIBLE);
			}
		});
	}

	private void showView(boolean show) {
		setVisibility(show ? VISIBLE : GONE);
	}

	public void setLiveRankView(LiveRankView liveRankView) {
		this.liveRankView = liveRankView;
	}

	public void setVideoView(LiveVideoView liveVideoView) {
		this.liveVideoView = liveVideoView;
	}

	public void setUserData(LiveRankEntity entity) {
		rankEntity = entity;
		dlgLiveToSb.getTvNickname().setText(
				"向" + rankEntity.getNickname() + "放送视频请求？");
		HeaderHelper.load(entity.getFid(), ivHeader, R.drawable.h001);
		/*
		 * ImageLoaderUtils.getInstance().loadImage(
		 * DataStorageUtils.getHeadDownloadUrl(entity.getFid()), ivHeader);
		 */
		tvNickname.setText(entity.getNickname());
		tvDistance.setText(entity.getDistance() + "");
		tvSpeed.setText(entity.getSpeed() + "");
		tvCalorie.setText(entity.getCalorie() + "");
		tvRate.setText(entity.getRpm());
		tvRes.setText(entity.getResist());
		if (AppModeHelper.CLUB_MODE.equals(entity.getLoginMode()) || DataStorageUtils.getPid().equals(entity.pid)) {
			btnSendVideo.setVisibility(View.GONE); // 不能向健身房的人发视频, 自己
		} else {
			btnSendVideo.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.view_live_info;
	}
}
