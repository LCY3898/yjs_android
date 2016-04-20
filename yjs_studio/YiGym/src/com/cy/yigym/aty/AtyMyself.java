package com.cy.yigym.aty;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.yigym.fragment.FragmentUserInfo;
import com.cy.yigym.fragment.live.FragVideoBase;
import com.efit.sport.R;

public class AtyMyself extends BaseFragmentActivity {
    private FragmentUserInfo fragMyself;


    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_myself;
    }

    @Override
    protected void initView() {

    }



    @Override
    protected void initData() {
        fragMyself = (FragmentUserInfo) mFragmentManager
                .findFragmentById(R.id.fragMyself);
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
