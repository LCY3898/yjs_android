package com.cy.yigym.view.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cy.yigym.adapter.PullDownMenuAdapter;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;

import com.efit.sport.R;

import java.util.List;

/**
 * Created by eijianshen on 15/9/15.
 */
public class SpinerMenuView extends BaseView implements View.OnClickListener
        , PullDownMenuAdapter.MenuOnItemClickListener {

    @BindView
    private LinearLayout dropDownMenu;

    @BindView
    private TextView tvMenuText;

    @BindView
    private ImageView ivArrow;


    private PullDownMenuWindow downMenuWindow;

    private Context mcontext;

    private List<String> mList;

    private int isClicked=0;

    private String text = null;


    public SpinerMenuView(Context context) {
        super(context);
    }

    public SpinerMenuView(Context context, AttributeSet attributeSet) {

        super(context, attributeSet);
        this.mcontext = context;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.view_drop_down_menu;
    }

    @Override
    protected void initView() {
        tvMenuText.setSelected(false);
        ivArrow.setSelected(false);
        dropDownMenu.setOnClickListener(this);
    }

    /**
     * 设置下拉菜单的数据
     * @param list
     */
    public void setMenuItemData(List<String> list) {
        this.mList = list;
        downMenuWindow = new PullDownMenuWindow(mcontext);
        downMenuWindow.refreshMenuData(mList, 0);
        downMenuWindow.setMenuOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dropDownMenu:
                showMenuWindow();
                break;
        }
    }

    /**
     * 弹出下拉菜单弹窗
     */
    public void showMenuWindow() {
        downMenuWindow.setWidth(((WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth());
        downMenuWindow.showAsDropDown(dropDownMenu);
        tvMenuText.setSelected(true);
        ivArrow.setSelected(true);
    }


    @Override
    public void onItemClick(int position) {
        if (isClicked == 0) {
            text = tvMenuText.getText().toString();
        }
        if (position > 0 && position <= mList.size()) {
            tvMenuText.setSelected(true);
            tvMenuText.setText(mList.get(position));
            ivArrow.setVisibility(GONE);
            isClicked = 1;
            if(listener!=null){
                listener.spinerMenuItemClicked();
            }
        }
        if (position == 0) {
            tvMenuText.setText(text);
            tvMenuText.setSelected(false);
            ivArrow.setSelected(false);
            ivArrow.setVisibility(VISIBLE);
            isClicked = 0;
            if(listener!=null){
                listener.spinerMenuItemClicked();
            }
        }
        if (position == -1) {
            if (isClicked == 1) {
                tvMenuText.setSelected(true);
            } else if (isClicked == 0) {
                tvMenuText.setSelected(false);
                ivArrow.setSelected(false);
            }
        }

    }

    public TextView getTvMenuText() {
        return tvMenuText;
    }

    public void setTvMenuText(String menuText) {
        tvMenuText.setText(menuText);
        text=menuText;
        isClicked = 0;
    }

    /**
     * 定义一个接口，用于监听下拉菜单是否点击
     */
    public interface  OnSpinerMenuItemClickListener{
        public void spinerMenuItemClicked();
    };

    private OnSpinerMenuItemClickListener listener;


    public void setOnSpinerMenuItemClickListener(OnSpinerMenuItemClickListener listener){
        this.listener=listener;
    }

}
