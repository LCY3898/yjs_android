package com.cy.yigym.aty;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.entity.TaskData;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqCreateTask;
import com.cy.yigym.net.rsp.RspAddTarget;
import com.cy.yigym.net.rsp.RspCreateTask;
import com.cy.yigym.view.CircularSeekBar;
import com.cy.yigym.view.content.DlgDateTimePicker;
import com.efit.sport.R;
import com.sport.efit.constant.SportType;
import com.sport.efit.theme.ColorTheme;
import com.umeng.socialize.utils.Log;

/**
 * Created by eijianshen on 15/8/19.
 */
public class AtyCreatTask extends BaseFragmentActivity implements View.OnClickListener{

    @BindView
    private CustomTitleView ctvCreatTaskTitle;

    @BindView
    private EditText etTaskTitle;
    @BindView
    private EditText etStartTime;

    @BindView
    private EditText etFinishTime;

    @BindView
    private Button btnTasktime;
    @BindView
    private Button btnTaskDis;
    @BindView
    private Button btnTaskCal;

    @BindView
    private TextView tv_taskSetting;

    @BindView
    private TextView tvTaskCompany;

    @BindView
    private EditText etTaskDescribe;

    @BindView
    private CircularSeekBar seekbar_taskSetting;

    private DlgDateTimePicker dlgStratDateTimePicker,dlgFinishDateTimePicker;
    private Context context;
    private int taskTarget;

    private int currentType= SportType.TYPE_INVALID;

    private Boolean isTargetSet=false;

    private int taskTargetValue=0;

    private static String taskTitle;
    private static String taskDescribe;

    private static int StartMonth;
    private static int StartYear;
    private static int StartDay;
    private static int StartHour;
    private static int StartMin;

    private static int FinishMonth;
    private static int FinishYear;
    private static int FinishDay;
    private static int FinishHour;
    private static int FinishMin;

    private static String taskId;
    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_creat_task;
    }

    @Override
    protected void initView() {
        ctvCreatTaskTitle.setTxtLeftText("    ");
        ctvCreatTaskTitle.setTxtLeftIcon(R.drawable.header_back);
        ctvCreatTaskTitle.setTxtLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        ctvCreatTaskTitle.setTxtRightIcon(R.drawable.icon_check);
        ctvCreatTaskTitle.setTxtRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTaskData();
            }
        });
        etTaskTitle.setBackground(ColorTheme.getEditorDrawable());
        etStartTime.setCursorVisible(false);
        etStartTime.setInputType(InputType.TYPE_NULL);
        etStartTime.setOnClickListener(this);
        etFinishTime.setCursorVisible(false);
        etFinishTime.setInputType(InputType.TYPE_NULL);
        etFinishTime.setOnClickListener(this);
        btnTaskCal.setOnClickListener(this);
        btnTaskDis.setOnClickListener(this);
        btnTasktime.setOnClickListener(this);
        etTaskTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                taskTitle = etTaskTitle.getText().toString().trim();
            }
        });

        etTaskDescribe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                taskDescribe = etTaskDescribe.getText().toString();
            }
        });
    }

    @Override
    protected void initData() {

        dlgStratDateTimePicker =new DlgDateTimePicker(mContext);
        dlgStratDateTimePicker.getBtnSure().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartYear = dlgStratDateTimePicker.getPvTimePicker().getSelectedYear();
                StartMonth = dlgStratDateTimePicker.getPvTimePicker().getSelectedMonth();
                StartDay = dlgStratDateTimePicker.getPvTimePicker().getSelectedDay();
                StartHour = dlgStratDateTimePicker.getPvTimePicker().getSelectedHour();
                StartMin = dlgStratDateTimePicker.getPvTimePicker().getSelectedMin();
                final String selectedStartTime = StartYear + "-" + StartMonth + "-" + StartDay + " "
                        + StartHour + ":" + StartMin;
                etStartTime.setText(selectedStartTime);
                dlgStratDateTimePicker.dismiss();
            }
        });

        dlgFinishDateTimePicker=new DlgDateTimePicker(mContext);
        dlgFinishDateTimePicker.getBtnSure().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinishYear = dlgFinishDateTimePicker.getPvTimePicker().getSelectedYear();
                FinishMonth = dlgFinishDateTimePicker.getPvTimePicker().getSelectedMonth();
                FinishDay = dlgFinishDateTimePicker.getPvTimePicker().getSelectedDay();
                FinishHour = dlgFinishDateTimePicker.getPvTimePicker().getSelectedHour();
                FinishMin = dlgFinishDateTimePicker.getPvTimePicker().getSelectedMin();
                final String selectedFinishTime = FinishYear + "-" + FinishMonth + "-" + FinishDay
                        + " " + FinishHour + ":" + FinishMin;
                final Boolean FiniIsAfterStart=TimeCompare(StartYear,StartMonth,StartDay
                        ,StartHour,StartMin, FinishYear,FinishMonth,FinishDay,
                        FinishHour,FinishMin);
                if(!FiniIsAfterStart){
                    WidgetUtils.showToast("设置的结束时间有误，请重新设置");
                }
                else {
                    etFinishTime.setText(selectedFinishTime);
                    dlgFinishDateTimePicker.dismiss();
                }
            }
        });

        seekbar_taskSetting.setMaxProgress(SportType.MAX_PROGRESS);
        seekbar_taskSetting.setSeekBarChangeListener(new CircularSeekBar.OnSeekChangeListener() {
            @Override
            public void onProgressChange(CircularSeekBar view, int newProgress) {
                taskTargetValue = newProgress;
                refreshTaskProgess();
            }
        });
        setTaskType(SportType.TYPE_TIME);
        taskTargetValue=seekbar_taskSetting.getProgress();
        refreshTaskProgess();

    }

    /**
     * 将数据传送给服务器，并跳转到任务详情页面
     */
    public void sendTaskData(){
        if(taskTargetValue==0){
            WidgetUtils.showToast("任务目标不能为空");
            return;
        }
        if(isTargetSet){
            return;
        }
        isTargetSet=true;
        String xtype=null;
        switch (currentType){
            case SportType.TYPE_TIME:
                xtype="time";
                break;
            case  SportType.TYPE_DISTANCE:
                xtype="distance";
                break;
            case SportType.TYPE_CALORIE:
                xtype="calorie";
                break;
            default:break;
        }

        YJSNet.send(new ReqCreateTask(taskTitle, xtype, getTaskTargetValue() + "",
                StartYear+"",StartMonth+"", StartDay+"", StartHour+"", StartMin+"",
                FinishYear+"", FinishMonth+"", FinishDay+"", FinishHour+""
                , FinishMin+"",taskDescribe)
                , LOG_TAG, new YJSNet.OnRespondCallBack<RspCreateTask>() {
            @Override
            public void onSuccess(RspCreateTask data) {
                taskId=data.data.task._id;
                onCreatTaskSucc();
            }

            @Override
            public void onFailure(String errorMsg) {
                WidgetUtils.showToast("任务创建失败"+errorMsg);
                isTargetSet=false;
            }
        });
    }

    public void onCreatTaskSucc(){
        WidgetUtils.showToast("设置任务成功");
        Intent intent=new Intent(AtyCreatTask.this,AtyTaskDetails.class);
        intent.putExtra(TaskData.TASK_DATA, new TaskData(etTaskTitle.getText().toString().trim()
                , etStartTime.getText().toString().trim()
                , etFinishTime.getText().toString().trim(),tv_taskSetting.getText().toString().trim()
                +"  "+ tvTaskCompany.getText().toString().trim(),taskId));
        setResult(RESULT_OK, intent);
        startActivity(intent);
        finish();
    }


    public void setTaskType(int type){
        if(currentType!=type){
            currentType=type;
            btnTaskDis.setSelected(false);
            btnTasktime.setSelected(false);
            btnTaskCal.setSelected(false);
            switch (type){
                case SportType.TYPE_TIME:
                    btnTasktime.setSelected(true);break;
                case  SportType.TYPE_DISTANCE:
                    btnTaskDis.setSelected(true);break;
                case SportType.TYPE_CALORIE:
                    btnTaskCal.setSelected(true);
                    default:break;
            }
            refreshTaskProgess();
        }
    }

    public int getTaskTargetValue(){
        int value=0;
        switch (currentType){
            case SportType.TYPE_DISTANCE:
                value=taskTargetValue*1000*1000/SportType.MAX_PROGRESS;
                break;
            case SportType.TYPE_TIME:
                value=taskTargetValue*3*24*60/SportType.MAX_PROGRESS;
                break;
            case SportType.TYPE_CALORIE:
                value=taskTargetValue*10000/SportType.MAX_PROGRESS;
                break;
            default:break;

        }
        return  value;
    }


    /**
     *日期的比较
     */
    public Boolean TimeCompare(int year1,int month1,int day1,int hour1,int min1,
                               int year2,int month2,int day2,int hour2,int min2){
        if(year1<year2){
            return true;
        }
        else if(year1==year2&&month1<month2){
            return true;
        }
        else if(year1==year2&&month1==month2&&day1<day2){
            return true;
        }
        else if(year1==year2&&month1==month2&&day1==day2&&hour1<hour2){
            return true;
        }
        else if(year1==year2&&month1==month2&&day1==day2&&hour1==hour2&&min1<min2){
            return true;
        }
        else
        return false;
    }
    public void refreshTaskProgess(){
        switch (currentType){
            case SportType.TYPE_DISTANCE:
                tv_taskSetting.setText(String.format("%.2f",getTaskTargetValue()/1000.0));
                tvTaskCompany.setText("km");
                break;
            case SportType.TYPE_TIME:
                tv_taskSetting.setText(String.format("%d",getTaskTargetValue()));
                tvTaskCompany.setText("min");
                break;
            case SportType.TYPE_CALORIE:
                tv_taskSetting.setText(String.format("%d",getTaskTargetValue()));
                tvTaskCompany.setText("cal");
                break;
            default:break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.etStartTime:
                dlgStratDateTimePicker.show();
                break;
            case R.id.etFinishTime:
                dlgFinishDateTimePicker.show();
                break;
            case R.id.btnTasktime:
                setTaskType(SportType.TYPE_TIME);
                break;
            case R.id.btnTaskDis:
                setTaskType(SportType.TYPE_DISTANCE);
                break;
            case R.id.btnTaskCal:
                setTaskType(SportType.TYPE_CALORIE);
                break;

            default: break;
        }
    }

}
