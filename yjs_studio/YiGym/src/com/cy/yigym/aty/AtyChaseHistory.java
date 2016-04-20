package com.cy.yigym.aty;

import android.view.View;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.fragment.FragmentChaseHistory;
import com.efit.sport.R;

/**
 * Created by ejianshen on 15/7/29.
 */
public class AtyChaseHistory extends BaseFragmentActivity  {

    @BindView
    private CustomTitleView vTitle;

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }
    @Override
    protected int getContentViewId() {
        return R.layout.aty_chase_history;
    }

    @Override
    public int getContentAreaId() {
        return R.id.frmContent;
    }

    @Override
    protected void initView() {
        vTitle.setVisibility(View.VISIBLE);
        vTitle.setTitle("历史纪录");
        vTitle.setTxtLeftText("       ");
        vTitle.setTxtLeftIcon(R.drawable.header_back);
        vTitle.setTxtLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        addFragment(new FragmentChaseHistory());
    }
    @Override
    protected void initData(){
    }
}
