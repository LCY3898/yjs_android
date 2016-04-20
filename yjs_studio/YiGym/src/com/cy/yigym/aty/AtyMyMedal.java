package com.cy.yigym.aty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.req.ReqGetPevent;
import com.cy.yigym.net.rsp.RspGetPevent;
import com.cy.yigym.net.rsp.RspGetPevent.Medal;
import com.cy.yigym.view.content.ItemMyMedalRaw;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-18
 * </p>
 * <p>
 * 我的勋章界面
 * </p>
 */
public class AtyMyMedal extends BaseFragmentActivity {
	@BindView
	private CustomTitleView vTitle;
	@BindView
	private LinearLayout llMyMedal;
	private Map<String, ArrayList<Medal>> medalMap = new HashMap<String, ArrayList<Medal>>();

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_my_medal;
	}

	@Override
	protected void initView() {
		vTitle.setTitle("勋章");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.header_back);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	protected void initData() {
		fetchMedalList();
	}

	/**
	 * 获取勋章列表
	 */
	private void fetchMedalList() {
		YJSNet.getPevent(new ReqGetPevent(), LOG_TAG,
				new OnRespondCallBack<RspGetPevent>() {

					@Override
					public void onSuccess(RspGetPevent data) {
						parseMedal(data);
					}

					@Override
					public void onFailure(String errorMsg) {
						WidgetUtils.showToast(errorMsg);
					}
				});
	}

	/**
	 * 解析勋章数据
	 * 
	 * @param data
	 */
	private void parseMedal(RspGetPevent data) {
		if (data == null)
			return;
		ArrayList<Medal> listMedal = data.data.medal_list;
		if (listMedal == null || listMedal.size() == 0 || data.data == null)
			return;
		medalMap.clear();
		int size = listMedal.size();
		for (int i = 0; i < size; i++) {
			Medal medal = listMedal.get(i);
			if (TextUtils.isEmpty(medal.medal_type))
				continue;
			if (!medalMap.containsKey(medal.medal_type)) {
				ArrayList<Medal> ms = new ArrayList<RspGetPevent.Medal>();
				ms.add(medal);
				medalMap.put(medal.medal_type, ms);
			} else {
				medalMap.get(medal.medal_type).add(medal);
			}
		}
		if (medalMap.size() == 0)
			return;
		llMyMedal.removeAllViews();
		for (String type : medalMap.keySet()) {
			ArrayList<Medal> medals = medalMap.get(type);
			ArrayList<Medal> m1 = new ArrayList<Medal>();
			ArrayList<Medal> m2 = new ArrayList<Medal>();
			ArrayList<Medal> m3 = new ArrayList<Medal>();
			ArrayList<Medal> m4 = new ArrayList<Medal>();
			ArrayList<Medal> m5 = new ArrayList<Medal>();
			ArrayList<Medal> m6 = new ArrayList<Medal>();
			ArrayList<ArrayList<Medal>> unknowMedals = new ArrayList<ArrayList<Medal>>();
			MedalType medalType = MedalType.match(type);
			if (MedalType.match(type) == null) {
				unknowMedals.add(medals);
			} else {
				switch (medalType) {
				case DAILY_PERFFECT:
					m1.addAll(medals);
					break;
				case DATA_ACCUMULATED:
					m2.addAll(medals);
					break;
				case LIVE_ACHIEVEMENT:
					m3.addAll(medals);
					break;
				case CHASE_TA:
					m4.addAll(medals);
					break;
				case TRAIN_TIMES:
					m5.addAll(medals);
					break;
				case EMOTION_MEDAL:
					m6.addAll(medals);
					break;
				}
			}
			// 按顺序添加勋章列表
			if (m1.size() != 0) {
				ItemMyMedalRaw raw = new ItemMyMedalRaw(mContext);
				raw.setMedalList(m1, MedalType.DAILY_PERFFECT.type,
						data.data.medal_type_the_day_most);
				llMyMedal.addView(raw);
			}
			if (m2.size() != 0) {
				ItemMyMedalRaw raw = new ItemMyMedalRaw(mContext);
				raw.setMedalList(m2, MedalType.DATA_ACCUMULATED.type,
						data.data.medal_type_the_data_accumulated);
				llMyMedal.addView(raw);
			}
			if (m3.size() != 0) {
				ItemMyMedalRaw raw = new ItemMyMedalRaw(mContext);
				raw.setMedalList(m3, MedalType.LIVE_ACHIEVEMENT.type,
						data.data.medal_type_the_live_broadcast);
				llMyMedal.addView(raw);
			}
			if (m4.size() != 0) {
				ItemMyMedalRaw raw = new ItemMyMedalRaw(mContext);
				raw.setMedalList(m4, MedalType.CHASE_TA.type,
						data.data.medal_type_the_chase);
				llMyMedal.addView(raw);
			}
			if (m5.size() != 0) {
				ItemMyMedalRaw raw = new ItemMyMedalRaw(mContext);
				raw.setMedalList(m5, MedalType.TRAIN_TIMES.type,
						data.data.medal_type_the_training_time);
				llMyMedal.addView(raw);
			}
			if (m6.size() != 0) {
				ItemMyMedalRaw raw = new ItemMyMedalRaw(mContext);
				raw.setMedalList(m6, MedalType.EMOTION_MEDAL.type,
						data.data.medal_type_the_emotional);
				llMyMedal.addView(raw);
			}
			// 添加不知名的勋章列表
			int size2 = unknowMedals.size();
			for (int i = 0; i < size2; i++) {
				ItemMyMedalRaw raw = new ItemMyMedalRaw(mContext);
				raw.setMedalList(unknowMedals.get(i), unknowMedals.get(i)
						.size() == 0 ? ""
						: unknowMedals.get(i).get(0).medal_type, unknowMedals
						.get(i).size());
				llMyMedal.addView(raw);
			}

		}
	}

	/**
	 * 勋章类型
	 */
	public static enum MedalType {
		DAILY_PERFFECT("每日之最", 8, R.drawable.ic_info_grow_up), DATA_ACCUMULATED(
				"数据累计", 8, R.drawable.ic_info_target), LIVE_ACHIEVEMENT("直播成就",
				5, R.drawable.ic_grow_up_live), CHASE_TA("追ta成就", 3,
				R.drawable.ic_grow_up_chase), TRAIN_TIMES("训练次数", 6,
				R.drawable.ic_grow_up_drill), EMOTION_MEDAL("情感化勋章", 6,
				R.drawable.ic_grow_up_login),OTHER("其它",8888,R.drawable.ic_grow_up_login);
		public String type = "";
		public int totalCount = 0;
		public int thumbImgId = 0;//缩略图资源id

		MedalType(String type, int totalCount, int thumbImgId) {
			this.type = type;
			this.totalCount = totalCount;
			this.thumbImgId = thumbImgId;
		}

		public static MedalType match(String type) {
			MedalType[] types = MedalType.values();
			for (MedalType medalType : types) {
				if (medalType.type.equals(type))
					return medalType;
			}
			return null;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}
}
