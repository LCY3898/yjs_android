package com.cy.yigym.aty;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.base.FragmentViewPagerAdapter;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomViewPager;
import com.cy.yigym.fragment.FragmentCharmRank;
import com.cy.yigym.fragment.FragmentRule;
import com.cy.yigym.fragment.FragmentScore;
import com.cy.yigym.fragment.FragmentSpoonyRank;
import com.cy.yigym.fragment.FragmentTotalRank;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

import java.util.ArrayList;

/**
 * Created by ejianshen on 15/8/22.
 */
public class AtyScore extends BaseFragmentActivity implements View.OnClickListener {
    @BindView
    private TextView tvScore;
    @BindView
    private CustomTitleView tvTitle;
    @BindView
    private View llScore,llRule;
    @BindView
    private CustomViewPager vPager;
    private ArrayList<Fragment> fragments;
    private FragmentViewPagerAdapter fragmentViewPagerAdapter;
    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_score;
    }

    @Override
    protected void initView() {
        llScore.setSelected(true);
        tvTitle.setTitle("我的积分");
        tvTitle.setTxtLeftText("       ");
        tvScore.setText(DataStorageUtils.getTotalScore());
        tvTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
        tvTitle.setTxtLeftClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        fragments=new ArrayList<Fragment>();
        fragments.add(new FragmentScore());
        fragments.add(new FragmentRule());
        fragmentViewPagerAdapter=new FragmentViewPagerAdapter(getSupportFragmentManager(),fragments);
        vPager.setAdapter(fragmentViewPagerAdapter);
        vPager.setCurrentItem(0);
        vPager.setOffscreenPageLimit(2);
        llScore.setSelected(true);
        vPager.setSlideEnable(true);
        vPager.setSelected(true);
        vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                llScore.setSelected(0==position);
                llRule.setSelected(1==position);
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
        llRule.setSelected(v==llRule);
        llScore.setSelected(v==llScore);
        switch (v.getId()){
            case R.id.llScore:
                vPager.setCurrentItem(0);
                break;
            case R.id.llRule:
                vPager.setCurrentItem(1);
                break;
        }
    }
}
