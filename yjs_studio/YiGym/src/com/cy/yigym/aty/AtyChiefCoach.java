package com.cy.yigym.aty;

import java.util.ArrayList;

import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.adapter.ChiefCoachAdapter;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqChiefCoach;
import com.cy.yigym.net.rsp.RspChiefCoach;
import com.efit.sport.R;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshListView;

/**
 * Created by xiaoshu on 15/11/10.
 */
public class AtyChiefCoach extends BaseFragmentActivity implements
		PullToRefreshBase.OnRefreshListener2<ListView> {
	@BindView
	private CustomTitleView ctvChiefCoachTitle;
	@BindView
	private PullToRefreshListView chiefCoachListview;
	@BindView
	private RelativeLayout rvEmptyTip;
	private boolean isCanPullUp = true;

	private ChiefCoachAdapter chiefCoachAdapter;
	private ArrayList<RspChiefCoach.Data> chiefCoachList = new ArrayList<RspChiefCoach.Data>();

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_chief_coach;
	}

	@Override
	protected void initView() {
		ctvChiefCoachTitle.setTxtLeftIcon(R.drawable.header_back);
		ctvChiefCoachTitle.setTxtLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ctvChiefCoachTitle.setTitle("今日主教");
		chiefCoachListview.setEmptyView(rvEmptyTip);
	}

	@Override
	protected void initData() {
		chiefCoachAdapter = new ChiefCoachAdapter(mActivity, chiefCoachList);
		chiefCoachListview.setAdapter(chiefCoachAdapter);
		chiefCoachListview.setOnRefreshListener(this);
		chiefCoachListview.setMode(PullToRefreshBase.Mode.BOTH);
		fetchData();
	}

	private void fetchData() {
		YJSNet.send(new ReqChiefCoach(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspChiefCoach>() {
					@Override
					public void onSuccess(RspChiefCoach data) {
						refreshListview();
						if (data.data == null || data.data.size() == 0) {
							isCanPullUp = false;
							// return;
						}
						chiefCoachList.clear();
						chiefCoachList.addAll(data.data);
						chiefCoachAdapter.notifyDataSetChanged();
					}

					@Override
					public void onFailure(String errorMsg) {
						WidgetUtils.showToast(errorMsg);
						refreshListview();
					}
				});
	}

	private void refreshListview() {
		chiefCoachListview.onRefreshComplete();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isCanPullUp) {
			fetchData();
		} else {
			refreshView.postDelayed(new Runnable() {
				@Override
				public void run() {
					refreshListview();
				}
			}, 1000);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		fetchData();
	}
}
