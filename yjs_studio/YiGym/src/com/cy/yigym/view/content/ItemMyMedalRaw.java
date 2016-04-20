package com.cy.yigym.view.content;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.net.rsp.RspGetPevent.Medal;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.CustomExpandGridView;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-18
 * </p>
 * <p>
 * 我的勋章之每一行的勋章列表
 * </p>
 */
public class ItemMyMedalRaw extends BaseView implements OnItemClickListener {
	// @BindView
	// private LinearLayout llMedalList;
	@BindView
	private TextView tvMedalType;
	@BindView
	private TextView tvMedalCount;
	private ArrayList<Medal> listMedal;
	private MedalListAdapter adapter;
	private int medalWidth = 0;
	private DlgMyMedal dlgMyMedal;
	@BindView
	private CustomExpandGridView gvMedalList;

	public ItemMyMedalRaw(Context context) {
		super(context);
	}

	public ItemMyMedalRaw(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initView() {
		listMedal = new ArrayList<Medal>();
		adapter = new MedalListAdapter(mContext, listMedal);
		gvMedalList.setAdapter(adapter);
		gvMedalList.setOnItemClickListener(this);
		dlgMyMedal = new DlgMyMedal(mContext);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.item_my_medal_row;
	}

	/**
	 * 设置勋章列表
	 * 
	 * @param acqMedal
	 * @param medalType
	 * @param totalMedalCount
	 */
	public void setMedalList(ArrayList<Medal> acqMedal, String medalType,
			int totalMedalCount) {
		if (!TextUtils.isEmpty(medalType))
			tvMedalType.setText(medalType);
		if (acqMedal == null || acqMedal.size() == 0)
			return;
		medalWidth = (int) (WidgetUtils.getScreenWidth() / 3.0);
		tvMedalCount.setText(acqMedal.size() + "/" + totalMedalCount);
		this.listMedal.clear();
		this.listMedal.addAll(acqMedal);
		// this.llMedalList.removeAllViews();
		// int size = adapter.getCount();
		// for (int i = 0; i < size; i++) {
		// final Medal medal = listMedal.get(i);
		// View item = adapter.getItemView(i, null, llMedalList, medal);
		// llMedalList.addView(item);
		// item.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// dlgMyMedal.show(medal.medal_fid, medal.medal_meaning);
		// }
		// });
		// }
		adapter.notifyDataSetChanged();

	}

	/**
	 * 勋章列表适配器
	 */
	public class MedalListAdapter extends AdapterBase<Medal> {

		public MedalListAdapter(Context context, List<Medal> list) {
			super(context, list);
		}

		@Override
		protected View getItemView(int position, View convertView,
				ViewGroup parent, Medal entity) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_medal, parent,
						false);
			}
			GridView.LayoutParams lp = new GridView.LayoutParams(medalWidth,
					LayoutParams.WRAP_CONTENT);
			convertView.setLayoutParams(lp);
			ImageView ivMedalIcon = getHolderView(convertView, R.id.ivMedalIcon);
			TextView tvMedalName = getHolderView(convertView, R.id.tvMedalName);
			tvMedalName.setText(entity.medal_name);
			ImageLoaderUtils.getInstance().loadImage(
					DataStorageUtils.getHeadDownloadUrl(entity.medal_fid),
					ivMedalIcon);
			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Medal medal = this.listMedal.get(position);
		dlgMyMedal.show(medal.medal_fid, medal.medal_meaning);
	}

}
