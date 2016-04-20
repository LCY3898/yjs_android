package com.cy.yigym.aty;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.base.FragmentViewPagerAdapter;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomViewPager;
import com.cy.yigym.fragment.FragmentCharmRank;
import com.cy.yigym.fragment.FragmentSpoonyRank;
import com.cy.yigym.fragment.FragmentTotalRank;
import com.efit.sport.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/20.
 */
public class AtyRank extends BaseFragmentActivity implements View.OnClickListener {

    @BindView
    private TextView tvTRank,tvMRank,tvCRank;
    @BindView
    private ImageView ivT,ivM,ivC;
    @BindView
    private CustomViewPager vPageRank;
    @BindView
    private CustomTitleView vTitle;
    private ArrayList<Fragment> fragments;
    private FragmentViewPagerAdapter fragmentViewPagerAdapter;
    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_rank;
    }

    @Override
    protected void initView() {
        tvCRank.setOnClickListener(this);
        tvMRank.setOnClickListener(this);
        tvTRank.setOnClickListener(this);
        tvTRank.setSelected(true);

        vTitle.setTitle("排行榜");
        vTitle.setTxtLeftText("       ");
        vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
        vTitle.setTxtLeftClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        fragments=new ArrayList<Fragment>();
        fragments.add(new FragmentTotalRank());
        fragments.add(new FragmentCharmRank());
        fragments.add(new FragmentSpoonyRank());
        fragmentViewPagerAdapter=new FragmentViewPagerAdapter(getSupportFragmentManager(),fragments);
        vPageRank.setAdapter(fragmentViewPagerAdapter);
        vPageRank.setCurrentItem(0);
        vPageRank.setOffscreenPageLimit(3);
        vPageRank.setSlideEnable(true);
        vPageRank.setSelected(true);
        vPageRank.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               if(0==position){
                   ivT.setVisibility(View.VISIBLE);
                   ivM.setVisibility(View.GONE);
                   ivC.setVisibility(View.GONE);
               }else if(1==position){
                   ivT.setVisibility(View.GONE);
                   ivM.setVisibility(View.VISIBLE);
                   ivC.setVisibility(View.GONE);
               }else{
                   ivT.setVisibility(View.GONE);
                   ivM.setVisibility(View.GONE);
                   ivC.setVisibility(View.VISIBLE);
               }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvTRank:
                vPageRank.setCurrentItem(0);
                ivT.setVisibility(View.VISIBLE);
                ivM.setVisibility(View.GONE);
                ivC.setVisibility(View.GONE);
                break;
            case R.id.tvMRank:
                vPageRank.setCurrentItem(1);
                ivT.setVisibility(View.GONE);
                ivM.setVisibility(View.VISIBLE);
                ivC.setVisibility(View.GONE);
                break;
            case R.id.tvCRank:
                vPageRank.setCurrentItem(3);
                ivT.setVisibility(View.GONE);
                ivM.setVisibility(View.GONE);
                ivC.setVisibility(View.VISIBLE);
                break;
        }
    }
}
