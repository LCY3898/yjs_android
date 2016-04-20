package com.cy.yigym.aty;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.base.FragmentViewPagerAdapter;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomViewPager;
import com.cy.yigym.fragment.FragmentChaseHistory;
import com.cy.yigym.fragment.FragmentMyTraces;
import com.efit.sport.R;

import java.util.ArrayList;

/**
 * Created by ejianshen on 15/11/9.
 * 我的足迹
 */
public class AtyTraces extends BaseFragmentActivity implements View.OnClickListener{

    private FragmentViewPagerAdapter viewPagerAdapter;
    private ArrayList<Fragment> arrayList=new ArrayList<Fragment>();
    @BindView
    private CustomViewPager viewPager;
    @BindView
    private CustomTitleView vTitle;


    @BindView
    private RelativeLayout rlWatchHistory;

    @BindView
    private RelativeLayout rlChaseHistory;

    @BindView
    private View historyIndicator;

    @BindView
    private View chaseIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_traces;
    }

    @Override
    protected void initView() {
        arrayList.add(new FragmentMyTraces());
        arrayList.add(new FragmentChaseHistory());
        viewPagerAdapter=new FragmentViewPagerAdapter(getSupportFragmentManager(),arrayList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setSlideEnable(true);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        rlWatchHistory.setSelected(true);
        historyIndicator.setVisibility(View.VISIBLE);
        vTitle.setTitle("历史纪录");
        vTitle.setTxtLeftText("       ");
        vTitle.setTxtLeftIcon(R.drawable.header_back);
        vTitle.setTxtLeftClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        rlWatchHistory.setOnClickListener(this);
        rlChaseHistory.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                rlWatchHistory.setSelected(position == 0);
                rlChaseHistory.setSelected(position==1);
                historyIndicator.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
                chaseIndicator.setVisibility(position == 1 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.rlWatchHistory
                || v.getId() == R.id.rlChaseHistory) {
            chaseIndicator.setVisibility(v == rlChaseHistory? View.VISIBLE:View.INVISIBLE);
            historyIndicator.setVisibility(v == rlWatchHistory? View.VISIBLE:View.INVISIBLE);
            rlWatchHistory.setSelected(v == rlWatchHistory);
            rlChaseHistory.setSelected(v == rlChaseHistory);
            viewPager.setCurrentItem(v == rlChaseHistory ? 1 : 0);
        }
    }
}
