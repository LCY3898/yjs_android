package com.cy.yigym.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.cy.wheelview.WheelScroller;
import com.efit.sport.R;

/**
 * Created by ejianshen on 15/8/31.
 */
public class AddListViewMore extends ListView implements ListView.OnScrollListener {

    private View footerView;
    private int totalItemCount;//总数量
    private int lastVisibleItem;
    private boolean isLoding;
    private ILoadListener iLoadListener;

    public AddListViewMore(Context context) {
        super(context);
        initView(context);
    }

    public AddListViewMore(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AddListViewMore(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);
        footerView=inflater.inflate(R.layout.footer_layout,null);
        footerView.findViewById(R.id.footer).setVisibility(GONE);
        this.addFooterView(footerView);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(lastVisibleItem==totalItemCount&&scrollState==SCROLL_STATE_IDLE){
            if(!isLoding){
                isLoding=true;
                footerView.findViewById(R.id.footer).setVisibility(VISIBLE);
                if (iLoadListener != null) {
                    iLoadListener.onLoad();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastVisibleItem=firstVisibleItem+visibleItemCount;
        this.totalItemCount=totalItemCount;
    }

    public void setInterface(ILoadListener iLoadListener){
        this.iLoadListener=iLoadListener;
    }

    /**
     * 加载完成
     */
    public void LoadComplate(){
        isLoding=false;
        footerView.findViewById(R.id.footer).setVisibility(GONE);

    }
    public interface ILoadListener{
        public void onLoad();
    }
}
