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
public class ClubLiveRankAdapter extends AdapterBase<LiveRankEntity> {

	public ClubLiveRankAdapter(Context context, List<LiveRankEntity> list) {
		super(context, list);
	}

	@Override
	protected View getItemView(int position, View convertView,
			ViewGroup parent, LiveRankEntity entity) {
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.item_club_live_rank, null);

		ImageView rankBadge = getHolderView(convertView, R.id.ivStar);
		TextView tvLiveRank = getHolderView(convertView, R.id.tvLiveRank);
		TextView tvNickname = getHolderView(convertView, R.id.tvNickname);
		TextView tvCalorie = getHolderView(convertView, R.id.tvCalorie);
		TextView tvResist = getHolderView(convertView, R.id.tvResist);
		TextView tvRpm = getHolderView(convertView, R.id.tvRpm);

		entity = mList.get(position);
		if (position == 0) {
			rankBadge.setVisibility(View.VISIBLE);
			rankBadge.setImageResource(R.drawable.live_star_1);
		} else if (position == 1) {
			rankBadge.setVisibility(View.VISIBLE);
			rankBadge.setImageResource(R.drawable.live_star_2);
		} else if (position == 2) {
			rankBadge.setVisibility(View.VISIBLE);
			rankBadge.setImageResource(R.drawable.live_star_3);
		} else {
			rankBadge.setVisibility(View.INVISIBLE);
		}

		tvLiveRank.setText((position + 1) + "");
		tvNickname.setText(entity.getNickname());
		tvCalorie.setText(entity.getCalorie());
		tvResist.setText(entity.getResist());
		tvRpm.setText(entity.getRpm());

		return convertView;
	}
}
