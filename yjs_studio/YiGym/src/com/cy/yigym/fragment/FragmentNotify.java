package com.cy.yigym.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.base.FragmentViewPagerAdapter;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomViewPager;
import com.cy.yigym.aty.AtyMeetSucc;
import com.efit.sport.R;

import java.util.ArrayList;

/**
 * Created by ejianshen on 15/7/16.
 */
public class FragmentNotify extends BaseFragment{
    @BindView
    private CustomTitleView vTitle;

    @BindView
    private Button btnTest;

    private Context context;
    private LayoutInflater inflater;

    @BindView
    private View rlChat;
    @BindView
    private View rlNotify;



    private ArrayList<Fragment> fragmentList;
    private FragmentViewPagerAdapter fragmentViewPagerAdapter;
    // viewpage
    @BindView
    private CustomViewPager view_page_notify;


    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_notify;
    }

    @Override
    protected void initView(View contentView, LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {

        rlChat.setOnClickListener(titileClickListener);
        rlNotify.setOnClickListener(titileClickListener);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new FragmentNotifyListview());
        fragmentList.add(new FragmentNotifyListview());

        fragmentViewPagerAdapter = new FragmentViewPagerAdapter(
                super.getChildFragmentManager(), fragmentList);
        view_page_notify.setAdapter(fragmentViewPagerAdapter);
        view_page_notify.setCurrentItem(0);
        view_page_notify.setOffscreenPageLimit(2);
        view_page_notify.setSlideEnable(true);

        rlChat.setSelected(true);

        view_page_notify.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                rlChat.setSelected(position==0);
                rlNotify.setSelected(position==1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    protected View.OnClickListener titileClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v== rlChat){
                //WidgetUtils.showToast("siliao");

            }
            else if(v== rlNotify){
                //WidgetUtils.showToast("notify");
            }
            rlChat.setSelected(v==rlChat);
            rlNotify.setSelected(v==rlNotify);
            view_page_notify.setCurrentItem(v==rlChat?0:1);
        }
    };

    @Override
    protected void initData(Bundle savedInstanceState) {

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AtyMeetSucc.class);
                startActivity(intent);
            }
        });
    }
}
