package com.cy.yigym.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.entity.EtyListItem;
import com.cy.yigym.entity.NotifyListItem;
import com.cy.yigym.utils.HeaderHelper;
import com.efit.sport.R;

import java.util.List;

/**
 * Created by eijianshen on 15/8/6.
 */
public class NotifyListAdapter extends AdapterBase<NotifyListItem>{


    private Context mcontext;
    private LayoutInflater inflater;
    private Resources resources;
    private Boolean isMessageList;

    private boolean editMode = false;

    public NotifyListAdapter(Context context, List<NotifyListItem> list, Boolean isMessageList){
        super(context,list);
        this.mcontext=context;
        this.resources=mcontext.getResources();
        this.inflater=(LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isMessageList=isMessageList;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    @Override
    protected View getItemView(final int position, View convertView, ViewGroup parent,
                               final NotifyListItem entity) {
        ListItemView holder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.list_item_notify,parent,false);
            holder=new ListItemView();
            holder.ivUserHead=(ImageView)convertView.findViewById(R.id.ivUserHead);
            holder.tvUserName=(TextView)convertView.findViewById(R.id.tvUserName);
            holder.tvContent=(TextView)convertView.findViewById(R.id.tvContent);
            holder.tvSysTime=(TextView)convertView.findViewById(R.id.tvSysTime);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        }else{
            holder=(ListItemView)convertView.getTag();
        }
        HeaderHelper.load(entity.headerFid, holder.ivUserHead, 0);
        holder.tvUserName.setText(entity.userName);
        holder.tvContent.setText(entity.content);
        holder.tvSysTime.setText(entity.sysTime);
        holder.checkBox.setVisibility(editMode ? View.VISIBLE : View.GONE);
        if(editMode) {
            holder.checkBox.setChecked(entity.isSelected);
        } else {
            entity.isSelected = false;
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entity.isSelected = isChecked;
            }
        });

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
        CheckBox checkBox;
    }
}
