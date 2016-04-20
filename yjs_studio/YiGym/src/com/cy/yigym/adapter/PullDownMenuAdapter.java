package com.cy.yigym.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efit.sport.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eijianshen on 15/9/16.
 */
public class PullDownMenuAdapter<T> extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private List<T> mobject =new ArrayList<T>();

    ViewHolder holder=new ViewHolder();

    public PullDownMenuAdapter(Context context){
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mobject.size();
    }

    @Override
    public Object getItem(int position) {
        return mobject.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.item_drop_down_menuitem, null);
            holder.menuItemText = (TextView) convertView.findViewById(R.id.menuItemText);
            holder.llMenuItem=(LinearLayout)convertView.findViewById(R.id.llMenuItem);
            holder.menuItemText.setSelected(false);
            holder.llMenuItem.setSelected(false);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.menuItemText.setText((String)getItem(position));
        return convertView;
    }

    public static class ViewHolder{
        public TextView menuItemText;
        public LinearLayout llMenuItem;
    }

    /**
     * 刷新菜单列表数据
     *
     */
    public void refreshMenuData(List<T> object,int selfIndex){
        this.mobject =object;
        if(selfIndex<0){
            selfIndex=0;
        }
        if(selfIndex>=mobject.size()){
            selfIndex=mobject.size()-1;
        }

    }


    /**
     * 设置一个接口监听item点击
     */
    public interface MenuOnItemClickListener{
         void onItemClick(int position);
    }

}
