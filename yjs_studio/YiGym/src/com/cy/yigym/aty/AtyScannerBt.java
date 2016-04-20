package com.cy.yigym.aty;

import android.view.View;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.efit.sport.R;
import com.zbar.lib.CaptureCodeActivity;

/**
 * Created by ejianshen on 15/7/14.
 */
public class AtyScannerBt extends BaseFragmentActivity {
    @BindView
    private CustomTitleView vTitle;
    @Override
    protected boolean isBindViewByAnnotation() {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    protected int getContentViewId() {
        return R.layout.aty_scanner_bt;
    }
    @Override
    protected void initView() {
        vTitle.setTitle("扫一扫");
        vTitle.setTxtLeftText("       ");
        vTitle.setTxtRightText("历史纪录");
        vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
        vTitle.setTxtLeftClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        vTitle.setTxtRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AtyConnectBtHistory.class);
            }
        });

    }
    @Override
    protected void initData(){
       startActivity(CaptureCodeActivity.class);

    }
}
