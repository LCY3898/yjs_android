package com.cy.yigym.aty;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.efit.sport.R;

/**
 * Created by ejianshen on 15/7/14.
 * 蓝牙成功后的界面，可以从此界面进入主界面或从新扫描蓝牙
 */
public class AtyConnectBtSuccess extends BaseFragmentActivity implements View.OnClickListener {
    //返回首页按钮
    @BindView
    private Button btn_backHome;
    //重新扫描按钮
    @BindView
    private Button btn_searchAgain;
    //显示用户连接成功
    @BindView
    private TextView tv_userConnected;
    //显示中断连接
    @BindView
    private TextView tv_userDisconnected;
    //
    @Override
    protected boolean isBindViewByAnnotation() {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    protected int getContentViewId() {
        return R.layout.aty_connect_bt_success;
    }
    @Override
    protected void initView() {
        btn_backHome.setOnClickListener(this);
        btn_searchAgain.setOnClickListener(this);
    }
    @Override
    protected void initData(){


    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_backHome:
                //TODO  返回首页
                break;
            case R.id.btn_searchAgain:
                //TODO  重新扫描
                break;
        }
    }
}
