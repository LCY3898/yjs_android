package com.cy.yigym.aty;

import android.view.View;
import android.widget.TextView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.entity.TaskData;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetTaskBaseInfo;
import com.cy.yigym.net.rsp.RspAddTarget;
import com.cy.yigym.net.rsp.RspCreateTask;
import com.cy.yigym.net.rsp.RspGetTaskBaseInfo;
import com.efit.sport.R;

/**
 * Created by eijianshen on 15/8/21.
 */
public class AtyTaskDetails extends BaseFragmentActivity {

    @BindView
    private CustomTitleView ctvTaskDetailsTitle;

    @BindView
    private CustomCircleImageView taskHeadView;

    @BindView
    private TextView tvStartTime;

    @BindView
    private TextView tvFinishTime;

    @BindView
    private TextView tvJoinPersonNum;

    @BindView
    private TextView tvTasktarget;

    @BindView
    private TextView tvJoinOrNo;
    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_task_details;
    }

    @Override
    protected void initView() {
        TaskData taskData=(TaskData)getIntent().getSerializableExtra(TaskData.TASK_DATA);
        ctvTaskDetailsTitle.setTitle(taskData.taskTitle);
        ctvTaskDetailsTitle.setTxtLeftText("    ");
        ctvTaskDetailsTitle.setTxtLeftIcon(R.drawable.header_back);
        ctvTaskDetailsTitle.setTxtLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvStartTime.setText(taskData.taskStartTime);
        tvFinishTime.setText(taskData.TaskFinishTime);
        tvTasktarget.setText("目标："+taskData.taskTarget);
        YJSNet.send(new ReqGetTaskBaseInfo(taskData.taskId), LOG_TAG, new YJSNet.OnRespondCallBack<RspGetTaskBaseInfo>() {
            @Override
            public void onSuccess(RspGetTaskBaseInfo data) {
                tvJoinPersonNum.setText(data.data.task_joiner_num);
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }

    @Override
    protected void initData() {

    }
}
