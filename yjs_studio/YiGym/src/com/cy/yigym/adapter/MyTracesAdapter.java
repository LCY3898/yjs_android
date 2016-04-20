package com.cy.yigym.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.entity.MyTracesEntity;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

import java.util.List;

/**
 * Created by ejianshen on 15/11/9.
 */
public class MyTracesAdapter extends AdapterBase<MyTracesEntity> {
    public MyTracesAdapter(Context context, List<MyTracesEntity> list) {
        super(context, list);
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent, MyTracesEntity entity) {
        if(convertView==null)
            convertView=mInflater.inflate(R.layout.item_my_traces,null);
        entity=mList.get(position);
        ((TextView) getHolderView(convertView,R.id.tvCourseName)).setText(entity.getCourseName());
        ((TextView) getHolderView(convertView,R.id.tvCoachName)).setText(entity.getCaochName());
        ((TextView) getHolderView(convertView,R.id.tvWhere)).setText(entity.getLiveWhere());
        ((TextView) getHolderView(convertView,R.id.tvTime)).setText(entity.getTime());
        ImageLoaderUtils.getInstance().loadImage(
                DataStorageUtils.getHeadDownloadUrl(entity.videoAvatarId),
                (ImageView) getHolderView(convertView,R.id.ivLiveBg));
        return convertView;
    }

}
