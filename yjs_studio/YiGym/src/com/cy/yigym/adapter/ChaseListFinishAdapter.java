package com.cy.yigym.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.entity.ChaseListEntity;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/11.
 */
public class ChaseListFinishAdapter extends AdapterBase<ChaseListEntity> {
    private Context context;
    private List<ChaseListEntity> listItems=new ArrayList<ChaseListEntity>();
    private LayoutInflater inflater;
    private Resources resources;
    private ChaseListEntity chaseListEntity;
    DecimalFormat df= new DecimalFormat("######0.00");

    public ChaseListFinishAdapter(Context context,List<ChaseListEntity> listItems){
        super(context,listItems);
        this.context=context;
        this.listItems=listItems;
        this.resources=context.getResources();
        this.inflater=(LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent, ChaseListEntity entity) {
        ViewHodler viewHodler=new ViewHodler();
        if(convertView==null){
            convertView=inflater.inflate(R.layout.aty_chase_history_item,null);
            viewHodler.fid=(ImageView) convertView.findViewById(R.id.user_picture);
            viewHodler.nickname=(TextView) convertView.findViewById(R.id.user_name);
            viewHodler.distance=(TextView) convertView.findViewById(R.id.user_feel);
            convertView.setTag(viewHodler);
        }else{
            viewHodler=(ViewHodler)convertView.getTag();
        }
        chaseListEntity =listItems.get(position);
        ImageLoaderUtils.getInstance().loadImage(
                DataStorageUtils.getHeadDownloadUrl(chaseListEntity.getFid()),viewHodler.fid);
        viewHodler.nickname.setText(chaseListEntity.getNickname());
        viewHodler.distance.setText("成功会面");
        return convertView;
    }

    private class ViewHodler{
        private ImageView fid;
        private TextView nickname;
        private TextView distance;
    }
}
