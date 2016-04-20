package com.cy.yigym.aty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqAddTarget;
import com.efit.sport.R;


public class AtySetSports extends BaseFragmentActivity implements View.OnClickListener{


    @BindView
    private CustomTitleView targettitle;
    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_set_sports;
    }

    @Override
    public int getContentAreaId() {
        return 0;
    }

    @Override
    protected void initView() {
        targettitle.setTitle("设定目标");
        targettitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
        targettitle.setTxtLeftText("       ");
        targettitle.setTxtLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        targettitle.setTxtRightIcon(R.drawable.icon_finish);
        targettitle.setTxtRightText("");
        targettitle.setTxtRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YJSNet.send(new ReqAddTarget("time", "60"), LOG_TAG, new YJSNet.OnRespondCallBack<RspBase>() {
                    @Override
                    public void onSuccess(RspBase data) {
                        Log.d("AtySetSports", data.msg + "==" + data.code);
                        WidgetUtils.showToast("目标设定完成");
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        WidgetUtils.showToast("设定目标失败!");
                    }
                });

            }
        });


    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
