package com.cy.yigym.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;
import com.efit.sport.R;
import com.cy.yigym.entity.EtyListItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eijianshen on 15/8/6.
 */
public class EtyListAdapter extends AdapterBase<EtyListItem>{


    private Context mcontext;
    private LayoutInflater inflater;
    private Resources resources;
    private Boolean isMessageList;

    public EtyListAdapter(Context context,List<EtyListItem> list,Boolean isMessageList){
        super(context,list);
        this.mcontext=context;
        this.resources=mcontext.getResources();
        this.inflater=(LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isMessageList=isMessageList;
    }


    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent,
                               EtyListItem entity) {
        ListItemView holder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_task_notify,null);
            holder=new ListItemView();
            holder.ivUserHead=(ImageView)convertView.findViewById(R.id.ivUserHead);
            holder.tvUserName=(TextView)convertView.findViewById(R.id.tvUserName);
            holder.tvContent=(TextView)convertView.findViewById(R.id.tvContent);
            holder.tvSysTime=(TextView)convertView.findViewById(R.id.tvSysTime);
            holder.tvJoinNum = (TextView) convertView.findViewById(R.id.tvJoinNum);
            convertView.setTag(holder);
        }else{
            holder=(ListItemView)convertView.getTag();
        }
        HeaderHelper.load(entity.headerFid,holder.ivUserHead,0);
        holder.tvUserName.setText(entity.userName);
        holder.tvContent.setText(entity.content);
        holder.tvSysTime.setText(entity.sysTime);
        holder.tvJoinNum.setText(entity.joinNum);

        if(!isMessageList) {
            holder.tvJoinNum.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    /**
     * 封装listitem的四个控件
     */
    static class ListItemView{
        ImageView ivUserHead;
        TextView tvSysTime;
        TextView tvUserName;
        TextView tvContent;
        TextView tvJoinNum;
    }
}
