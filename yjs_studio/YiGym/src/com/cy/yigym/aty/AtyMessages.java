package com.cy.yigym.aty;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.base.FragmentViewPagerAdapter;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomViewPager;
import com.cy.yigym.fragment.FragmentIMRecentMsgList;
import com.cy.yigym.fragment.FragmentMessListview;
import com.efit.sport.R;

import java.util.ArrayList;

/**
 * Created by eijianshen on 15/8/12.
 */
public class AtyMessages extends BaseFragmentActivity implements View.OnClickListener{

    @BindView
    private CustomViewPager view_page_notify;

    @BindView
    private View rlChat;

    @BindView
    private View rlNotify;

    @BindView
    private View vLineChat;

    @BindView
    private View vLineNotify;

    @BindView
    private CustomTitleView ctvMessTitle;

    private ArrayList<Fragment> fragmentList;
    private FragmentViewPagerAdapter fragmentViewPagerAdapter;
    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_mess_notify;
    }

    @Override
    protected void initView() {

        ctvMessTitle.setTitle("消息");
        ctvMessTitle.setTxtLeftText("    ");
        ctvMessTitle.setTxtLeftIcon(R.drawable.header_back);
        ctvMessTitle.setTxtLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlChat.setOnClickListener(this);
        rlNotify.setOnClickListener(this);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new FragmentMessListview());
        fragmentList.add(new FragmentIMRecentMsgList());
        fragmentViewPagerAdapter = new FragmentViewPagerAdapter(
                getSupportFragmentManager(), fragmentList);
        view_page_notify.setAdapter(fragmentViewPagerAdapter);
        view_page_notify.setCurrentItem(0);
        view_page_notify.setOffscreenPageLimit(2);
        view_page_notify.setSlideEnable(true);
        view_page_notify.setSelected(true);
        vLineNotify.setVisibility(View.VISIBLE);
        vLineChat.setVisibility(View.INVISIBLE);
        //rlChat.setSelected(true);
        rlNotify.setSelected(true);

        view_page_notify.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        vLineNotify.setVisibility(View.VISIBLE);
                        vLineChat.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        vLineChat.setVisibility(View.VISIBLE);
                        vLineNotify.setVisibility(View.INVISIBLE);
                        break;
                    default:break;
                }
                rlChat.setSelected(position==1);
                rlNotify.setSelected(position==0);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlChat:
                vLineChat.setVisibility(View.VISIBLE);
                vLineNotify.setVisibility(View.GONE);
                break;
            case R.id.rlNotify:
                vLineNotify.setVisibility(View.VISIBLE);
                vLineChat.setVisibility(View.GONE);
                break;
            default:break;
        }
        rlChat.setSelected(view==rlChat);
        rlChat.setSelected(view==rlNotify);
        view_page_notify.setCurrentItem(view==rlChat?1:0);
    }
}
