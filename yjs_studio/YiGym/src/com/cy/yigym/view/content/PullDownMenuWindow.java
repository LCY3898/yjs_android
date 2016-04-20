package com.cy.yigym.view.content;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.cy.yigym.adapter.PullDownMenuAdapter;
import com.efit.sport.R;

import java.util.List;

/**
 * Created by eijianshen on 15/9/15.
 */
public class PullDownMenuWindow extends PopupWindow implements AdapterView.OnItemClickListener{

    private PopupWindow popupWindow;
    private String[] itemText;
    private Context mcontext;
    private PullDownMenuAdapter adapter;
    private ListView listView;
    private PullDownMenuAdapter.MenuOnItemClickListener menuOnItemClickListener;

    public PullDownMenuWindow(Context context){
        super(context);
        this.mcontext=context;
        initView();

    }


    /**
     * 为下拉菜单提供一个item点击事件的监听器，MenuView实例调用此方法设置监听
     */
    public void setMenuOnItemClickListener(PullDownMenuAdapter.MenuOnItemClickListener listener){
        this.menuOnItemClickListener=listener;
    }

    /**
     *  初始化弹窗列表布局
     */
    private void initView() {
        View view=LayoutInflater.from(mcontext).inflate(R.layout.view_menu_list, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);

        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        adapter=new PullDownMenuAdapter(mcontext);
        listView=(ListView)view.findViewById(R.id.menuListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (menuOnItemClickListener != null) {
                    menuOnItemClickListener.onItemClick(-1);
                }
            }
        });
    }

    /**
     * 刷新下拉列表数据
     * @param list
     * @param selfIndex
     */

    public void refreshMenuData(List<String> list,int selfIndex){
        if(list!=null&&selfIndex!=-1){
            adapter.refreshMenuData(list,selfIndex);
        }
    }


    /**
     * 下拉列表的点击监听事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.dismiss();
        if(menuOnItemClickListener!=null){
            menuOnItemClickListener.onItemClick(position);
        }
    }
}
