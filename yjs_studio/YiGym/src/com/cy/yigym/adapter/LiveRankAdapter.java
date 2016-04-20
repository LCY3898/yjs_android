package com.cy.yigym.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.entity.LiveRankEntity;
import com.efit.sport.R;

import java.util.List;

/**
 * Created by ejianshen on 15/9/15.
 */
public class LiveRankAdapter extends AdapterBase<LiveRankEntity> {

	public LiveRankAdapter(Context context, List<LiveRankEntity> list) {
		super(context, list);
	}

	@Override
	protected View getItemView(int position, View convertView,
			ViewGroup parent, LiveRankEntity entity) {
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.item_live_rank, null);
		entity = mList.get(position);
		if (position == 0) {
			((ImageView) getHolderView(convertView, R.id.ivStar))
					.setVisibility(View.VISIBLE);
			((ImageView) getHolderView(convertView, R.id.ivStar))
					.setImageResource(R.drawable.live_star_1);
			((TextView) getHolderView(convertView, R.id.tvLiveRank))
					.setText(1 + "");
			((TextView) getHolderView(convertView, R.id.tvNickname))
					.setText(entity.getNickname());
			((TextView) getHolderView(convertView, R.id.tvDistance))
					.setText(entity.getCalorie());
		} else if (position == 1) {
			((ImageView) getHolderView(convertView, R.id.ivStar))
					.setVisibility(View.VISIBLE);
			((ImageView) getHolderView(convertView, R.id.ivStar))
					.setImageResource(R.drawable.live_star_2);
			((TextView) getHolderView(convertView, R.id.tvLiveRank))
					.setText(2 + "");
			((TextView) getHolderView(convertView, R.id.tvNickname))
					.setText(entity.getNickname());
			((TextView) getHolderView(convertView, R.id.tvDistance))
					.setText(entity.getCalorie());
		} else if (position == 2) {
			((ImageView) getHolderView(convertView, R.id.ivStar))
					.setVisibility(View.VISIBLE);
			((ImageView) getHolderView(convertView, R.id.ivStar))
					.setImageResource(R.drawable.live_star_3);
			((TextView) getHolderView(convertView, R.id.tvLiveRank))
					.setText(3 + "");
			((TextView) getHolderView(convertView, R.id.tvNickname))
					.setText(entity.getNickname());
			((TextView) getHolderView(convertView, R.id.tvDistance))
					.setText(entity.getCalorie());
		} else {
			((ImageView) getHolderView(convertView, R.id.ivStar))
					.setVisibility(View.INVISIBLE);
			((TextView) getHolderView(convertView, R.id.tvLiveRank))
					.setText((position + 1) + "");
			((TextView) getHolderView(convertView, R.id.tvNickname))
					.setText(entity.getNickname());
			((TextView) getHolderView(convertView, R.id.tvDistance))
					.setText(entity.getCalorie());
		}

		return convertView;
	}
}
