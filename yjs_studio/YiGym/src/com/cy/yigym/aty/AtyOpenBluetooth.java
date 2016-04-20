package com.cy.yigym.aty;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.DialerKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cy.ble.BLELibInitializer;
import com.cy.ble.BLEManager;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.efit.sport.R;

//打开蓝牙

public class AtyOpenBluetooth extends BaseFragmentActivity implements View.OnClickListener {

    @BindView
    private Button btn_openBt;
    @BindView
    private TextView tv_notOpenBt;
    private BLEManager bleManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    @Override
    protected boolean isBindViewByAnnotation() {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    protected int getContentViewId() {
        return R.layout.aty_open_bluetooth;
    }
    @Override
    protected void initView() {
        btn_openBt.setOnClickListener(this);
        tv_notOpenBt.setOnClickListener(this);
        BLELibInitializer.init(getApplicationContext());
//        bluetoothManager = (BluetoothManager) BLELibInitializer.getAppContext()
//                .getSystemService(Context.BLUETOOTH_SERVICE);
//        bluetoothAdapter = bluetoothManager.getAdapter();
    }
    @Override
    protected void initData(){
//        if(bluetoothAdapter!=null&&bluetoothAdapter.isEnabled()){
//            startActivity(AtyScannerBt.class);
//        }
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_openBt:
                //TODO 弹窗设置蓝牙
                new AlertDialog.Builder(AtyOpenBluetooth.this).setTitle("开启蓝牙提示")
                        .setMessage("打开蓝牙来允“E健身“连接设备")
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bleManager=new BLEManager();
                                bleManager.notForceEnable(AtyOpenBluetooth.this,0);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                break;
            case R.id.tv_notOpenBt:
                //TODO 先不绑定蓝牙
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                startActivity(AtyScannerBt.class);
        }
    }
}
