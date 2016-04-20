package com.cy.yigym.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.entity.ScoreEntity;
import com.efit.sport.R;

import java.util.List;

/**
 * Created by ejianshen on 15/8/22.
 */
public class ScoreAdapter extends AdapterBase<ScoreEntity> {
    public ScoreAdapter(Context context, List<ScoreEntity> list) {
        super(context, list);
    }
    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent, ScoreEntity entity) {
        ViewHolder viewHolder=new ViewHolder();
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.item_score,null);
            viewHolder.content=(TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.detail=(TextView) convertView.findViewById(R.id.tvDetail);
            viewHolder.score=(TextView) convertView.findViewById(R.id.tvScoreNum);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.content.setText(entity.getContent());
        viewHolder.detail.setText(entity.getDetail());
        viewHolder.score.setText(entity.getScore()+"分");
        return convertView;
    }
    private class ViewHolder{
        private TextView content;
        private TextView detail;
        private TextView score;
    }
}
