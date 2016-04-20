package com.cy.yigym.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.entity.RankEntity;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/21.
 */
public class SpoonyRankListAdapter extends AdapterBase<RankEntity> {

    DecimalFormat df= new DecimalFormat("######0.00");

    public SpoonyRankListAdapter(Context context, List<RankEntity> listItems){
        super(context,listItems);
    }
    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent, RankEntity entity) {
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.item_c_rank,null);
        }
        if(position==0){
            ((TextView)getHolderView(convertView,R.id.tvRank)).setTextColor(convertView.getResources().getColor(R.color.rank_f));
            ((TextView)getHolderView(convertView,R.id.tvRank)).setText("1");
        }else if(position==1){
            ((TextView)getHolderView(convertView,R.id.tvRank)).setTextColor(convertView.getResources().getColor(R.color.rank_s));
            ((TextView)getHolderView(convertView,R.id.tvRank)).setText("2");
        }else if(position==2){
            ((TextView)getHolderView(convertView,R.id.tvRank)).setTextColor(convertView.getResources().getColor(R.color.rank_t));
            ((TextView)getHolderView(convertView,R.id.tvRank)).setText("3");
        }else{
            ((TextView)getHolderView(convertView,R.id.tvRank)).setTextColor(convertView.getResources().getColor(R.color.rank_normal));
            ((TextView)getHolderView(convertView,R.id.tvRank)).setText((position+1)+"");
        }
        ((TextView)getHolderView(convertView,R.id.tvNickname)).setText(entity.getNickname());
        ((TextView)getHolderView(convertView,R.id.tvDistance)).setText(entity.getTotalDistance() + "公里");
        if(TextUtils.isEmpty(entity.getFid())) {
            ((ImageView) getHolderView(convertView,R.id.ivImageHeader)).setImageResource(R.drawable.h001);
        } else {
            ImageLoaderUtils.getInstance().loadImage(
                    DataStorageUtils.getHeadDownloadUrl(entity.getFid()), ((ImageView) getHolderView(convertView, R.id.ivImageHeader)));
        }
         return convertView;
    }

}
