package com.cy.yigym.aty;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.net.rsp.RspAddTarget;
import com.sport.efit.constant.SportType;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqAddTarget;
import com.cy.yigym.view.CircularSeekBar;
import com.efit.sport.R;


public class AtyTargetSetting extends BaseFragmentActivity implements View.OnClickListener{


    @BindView
    private CustomTitleView targettitle;  //顶部标题栏

    @BindView
    private Button sports_time; //时间按钮

    @BindView
    private Button sports_distance; //距离按钮

    @BindView
    private Button sports_cal; // 卡路里按钮

    @BindView
    private CircularSeekBar seekbar_targetsetting;

    @BindView
    private TextView tv_targetsetting; //  显示当前设置时间／距离／卡路里的textview

    @BindView
    private TextView tvCompany; //显示的单位（min／km／cal）

    //min 1-180; distance(km) 1-99; calorie 1~9999;

    private final static int MAX_PROGRESS = SportType.MAX_PROGRESS;

    private int targetPos; //当前设置的目标值
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
        return super.getContentAreaId();
    }

    private int currType = -1;

    private boolean isAdding = false;

    @Override
    protected void initView() {
        targettitle.setTitle("设定目标");
        targettitle.setTxtLeftText("       ");
        targettitle.setTxtLeftIcon(R.drawable.header_back);
        targettitle.setTxtLeftText("");
        targettitle.setTxtLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        targettitle.setTxtRightIcon(R.drawable.header_check);
        targettitle.setTxtRightText("");
        targettitle.setTxtRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetPos == 0) {
                    WidgetUtils.showToast("目标不能为0");
                    return;
                }

                if (isAdding) {
                    return;
                }

                isAdding = true;
                String type = null;
                if (currType == SportType.TYPE_TIME) {
                    type = "time";
                } else if (currType == SportType.TYPE_DISTANCE) {
                    type = "distance";
                } else {
                    type = "calorie";
                }

                String quantity = tv_targetsetting.getText().toString().trim();
                Log.d("AtySetSports", "calorie" + tv_targetsetting.getText().toString().trim());
                YJSNet.send(new ReqAddTarget(type, quantity), LOG_TAG, new YJSNet.OnRespondCallBack<RspAddTarget>() {
                    @Override
                    public void onSuccess(RspAddTarget data) {
                        Log.d("AtySetSports", data.msg + "==" + data.code);
                        onSetTargetSuccess(data.data._id);

                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        WidgetUtils.showToast("设定目标失败!" + errorMsg);
                        isAdding = false;
                    }
                });

            }
        });

        sports_time.setOnClickListener(this);
        sports_distance.setOnClickListener(this);
        sports_cal.setOnClickListener(this);
    }


    private void onSetTargetSuccess(String taskId) {
        WidgetUtils.showToast("目标设定完成");
        Intent resultIntent = new Intent();
        resultIntent.putExtra(SportType.INTENT_KEY, SportType.from(currType,getTargetValue()));
        resultIntent.putExtra("task_id", taskId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void initData() {

        seekbar_targetsetting.setMaxProgress(MAX_PROGRESS);
        seekbar_targetsetting.setSeekBarChangeListener(new CircularSeekBar.OnSeekChangeListener() {
            @Override
            public void onProgressChange(CircularSeekBar view, int newProgress) {
                targetPos = newProgress;
                refreshProgress();
            }
        });

        setType(SportType.TYPE_TIME);
        targetPos =seekbar_targetsetting.getProgress();
        refreshProgress();

    }


    private int getTargetValue() {
        int value = 0;
        switch (currType) {
            case SportType.TYPE_CALORIE:
                value = targetPos * 5000 / MAX_PROGRESS; //calorie
                break;
            case SportType.TYPE_TIME:
                value = (targetPos * 180 / MAX_PROGRESS) * 60; //secs
                break;

            case SportType.TYPE_DISTANCE:
                value =  targetPos * 30 * 1000 / MAX_PROGRESS;// in meters
                break;
        }
        return value;
    }

    private void refreshProgress() {
        switch (currType) {
            case SportType.TYPE_CALORIE:
                tv_targetsetting.setText(String.format("%d", getTargetValue()));
                tvCompany.setText("cal");
                break;
            case SportType.TYPE_TIME:
                tv_targetsetting.setText(String.format("%d", getTargetValue() / 60));
                tvCompany.setText("min");
                break;

            case SportType.TYPE_DISTANCE:

                tv_targetsetting.setText(String.format("%.2f", getTargetValue() / 1000.0));
                tvCompany.setText("km");
                break;
            default:
                break;
        }
    }



    private void setType(int type) {
        if(currType != type) {
            currType = type;
            sports_time.setSelected(false);
            sports_distance.setSelected(false);
            sports_cal.setSelected(false);

            switch (type) {
                case SportType.TYPE_CALORIE:
                    sports_cal.setSelected(true);
                    break;
                case SportType.TYPE_TIME:
                    sports_time.setSelected(true);
                    break;
                case SportType.TYPE_DISTANCE:
                    sports_distance.setSelected(true);
                    break;
                default:
                    break;
            }

            refreshProgress();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sports_time:
                setType(SportType.TYPE_TIME);
                break;
            case R.id.sports_distance:
                setType(SportType.TYPE_DISTANCE);
                break;
            case R.id.sports_cal:
                setType(SportType.TYPE_CALORIE);
                break;
            default:
                break;
        }
    }


}
