package com.cy.yigym.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.ActivityManager;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.content.DlgEdit;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.widgetlibrary.view.AppMsg;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.aty.AtyLogin;
import com.cy.yigym.aty.AtyMain2;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.entity.LiveVideoEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGetLastCourse;
import com.cy.yigym.net.req.ReqJoinLiveCast;
import com.cy.yigym.net.rsp.RspGetLastCourse;
import com.cy.yigym.net.rsp.RspJoinLiveCast;
import com.cy.yigym.net.rsp.RspLogOut;
import com.cy.yigym.net.rsp.RspLogin;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.live.ClubPrepareSportDatas;
import com.efit.sport.R;
import com.efit.sport.utils.DateTimeUtis;
import com.hhtech.utils.NetUtils;
import com.hhtech.utils.UITimer;

import org.videolan.vlc.ui.ClubModeVideoActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * lijianqiang
 * <p>
 * 2015-11-4
 * </p>
 * <p>
 * club主界面内容区
 * </p>
 */
public class FragmentClubContent extends BaseFragment {

    private ClubPrepareSportDatas sportData;
    private ProgressBar progressBar;
    private View rlPlayAgain;

    private ImageView bgImage;

    //private CustomTitleView vTitle;

    private TextView tvTimeCutDown;

    private UITimer uiTimer = new UITimer();

    private TextView tvClubTimeCutDown;

    private ImageView btnClose;

    private Context mContext;


    private UITimer countDownTimer = new UITimer();
    /**
     * 来自服务器开始时间
     */
    private long beginTime;
    /**
     * 来自服务器结束时间
     */
    private long endTime;
    /**
     * 开始时间
     */
    private String timeBegin;
    /**
     * 结束时间
     */
    private String timeNow;
    private Date bTime = new Date();
    private Date nTime = new Date();
    /**
     * 加入直播的时间
     */
    private long joinTime;
    private long currentTimeMills;

    private long currentTimeFromServer; // 服务器的当前时间

    private SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /**
     * 课程ID
     */
    private String courseId;

    /***/
    private String courseFid;
    private long startTime;

    private boolean hasJoinLive = false;

    /**
     * 直播地址
     */
    private String liveUrl = "";

    private String tip = "距离开课时间还有 ";

    private Handler handler = new Handler();

    private Runnable getLastCourseTask = new Runnable() {
        @Override
        public void run() {
            getLastCourse();
        }
    };

    private Runnable checkAndShowTipTask = new Runnable() {

        public void run() {
            doCheckAndShowTip();
            handler.removeCallbacks(checkAndShowTipTask);
        }

    };

    private Runnable joinLiveTask = new Runnable() {
        @Override
        public void run() {
            joinLive();
        }
    };

    private void doCheckAndShowTip() {
        BleConnect instance = BleConnect.instance();
        if (instance == null) {
            WidgetUtils.showToast("请检查蓝牙");
        }

        if (instance.isBleConnected()) {
            WidgetUtils.showToast("蓝牙已连上");
        } else {
            AppMsg.makeText(mActivity, "蓝牙还未连接，请骑动自行车或联系管理员扫描二维码", AppMsg.STYLE_ALERT).show();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_club_video_vlc;
    }

    @Override
    protected boolean isBindViewByAnnotation() {
        return false;
    }

    @Override
    protected void initView(View contentView, LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        handler.postDelayed(checkAndShowTipTask, 12 * 1000);

//        vTitle = (CustomTitleView) findViewById(R.id.vTitle);
//        vTitle.setTxtLeftText("    ");
//        vTitle.setTxtLeftIcon(R.drawable.header_back);
//        vTitle.addLeftView(tvTimeCutDown, CustomTitleView.ViewAddDirection.right, null);
//        vTitle.setTitle("课程直播");
//        vTitle.setTxtLeftClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        vTitle.setBackgroundColor(0xcc363d4d);
        sportData = (ClubPrepareSportDatas) findViewById(R.id.sportData);

        btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        rlPlayAgain = findViewById(R.id.rlPlayAgain);

        sportData.setLogTag(LOG_TAG);
//        LiveVideoSportData data = DataStorageUtils.getLiveVideoData();
//        if (data != null && !TextUtils.isEmpty(data.courseId)
//                && data.courseId.equals(DataStorageUtils.getCourseId())) {
//            sportData.initData(data.distance, data.seconds, data.calorie);
//        } else {
//            sportData.initData(0, 0, 0);
//        }
        sportData.initData(0, 0, 0);


        tvTimeCutDown = new TextView(getActivity());
        tvTimeCutDown.setTextColor(0xffedf1f6);
        tvTimeCutDown.setTextSize(14);
        tvTimeCutDown.setText("00:00");
        tvClubTimeCutDown = (TextView) findViewById(R.id.tvClubTimeCutDown);
        tvClubTimeCutDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setMode();
                return true;
            }
        });

        //结束当前activity之前的所有activity
        ActivityManager.getInstance().finishAtyBeforeThis();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if (!NetUtils.isNetConnected(mContext)) {
            WidgetUtils.showToast("网络不可用,请先开启网络");
        }

        getLastCourse();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (hasJoinLive) {
            getLastCourse();
            hasJoinLive = false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(getLastCourseTask);
        handler.removeCallbacks(joinLiveTask);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * 获取直播课程信息
     */
    private void getLastCourse() {
        YJSNet.send(new ReqGetLastCourse(), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspGetLastCourse>() {
                    @Override
                    public void onSuccess(RspGetLastCourse data) {
                        RspGetLastCourse.Data data1 = data.data;
                        if (TextUtils.isEmpty(data1.lastCourse._id)) {
//							btnEnterLive.setText("暂无直播");
                            tvClubTimeCutDown.setText("暂无课程预告");
                            handler.postDelayed(getLastCourseTask, 30 * 1000);
                            return;
                        }

                        currentTimeFromServer = data1.server_current_time;
                        currentTimeMills = currentTimeFromServer * 1000;

                        beginTime = data1.lastCourse.begin_time + 5; // 推迟5秒
                        endTime = data1.lastCourse.end_time;
                        courseId = data1.lastCourse._id;
                        courseFid = data1.lastCourse.course_fid;
                        liveUrl = data1.live_broadcast_addr;
                        DataStorageUtils.setCourseId(courseId);
                        DataStorageUtils.saveLastCourse(data.data);
                        long beginTimeMills = beginTime * 1000;
                        timeBegin = format.format(new Date(beginTimeMills));
                        //tvClubTimeCutDown.setText(timeBegin + "开始");// 倒计时

                        uiTimer.schedule(new Runnable() {
                            @Override
                            public void run() {
                                //currentTimeMills = System.currentTimeMillis(); // 本机时钟与服务器不一致会加入失败
                                joinTime = currentTimeMills / 1000 - beginTime;

                                long nowSec = currentTimeMills / 1000;
                                long secsDiff = Math.max(beginTime - nowSec, 0L);
                                tvClubTimeCutDown.setText(tip + DateTimeUtis.formatTimeDuration((int) secsDiff));
                                if (secsDiff == 0) {
                                    joinTime = 1;
                                    tvClubTimeCutDown.setText("课程进行中...");
                                    uiTimer.cancel();
                                }

                                currentTimeMills += 1000;
                            }
                        }, 1000);
                        if (beginTime > currentTimeFromServer) {
                            handler.postDelayed(joinLiveTask, (beginTime - currentTimeFromServer + 2) * 1000);
                        } else {
                            joinTime = currentTimeFromServer - beginTime;
                            joinLive();
                        }
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        handler.postDelayed(getLastCourseTask, 60 * 1000);
                    }
                });
    }


    /**
     * 加入直播
     */
    private void joinLive() {
        if (courseId == null) {
            WidgetUtils.showToast("课程id不能为空");
            getLastCourse();
            return;
        }
        if (TextUtils.isEmpty(liveUrl)) {
            WidgetUtils.showToast("直播地址不能为空");
            getLastCourse();
            return;
        }
        if (isLiveOverTime()) {
            WidgetUtils.showToast("直播时间已过");
            getLastCourse();
            return;
        }
        RspLogin.NetEaseAccount netEaseAccount = DataStorageUtils.getNetEaseAccount();
        if (netEaseAccount == null) {
            WidgetUtils.showToast("云账号为空");
            return;
        }
        YJSNet.send(
                new ReqJoinLiveCast(courseId, netEaseAccount.accid, netEaseAccount.token, joinTime), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspJoinLiveCast>() {
                    @Override
                    public void onSuccess(RspJoinLiveCast data) {
                        hasJoinLive = true;
                        DataStorageUtils.setCourseId(courseId);
                        if (isVisible()) {
                            // 进入健身房模式
                            Intent intent = new Intent(mActivity, ClubModeVideoActivity.class);
                            intent.putExtra("live_course", new LiveVideoEntity(
                                    courseId, liveUrl, beginTime, endTime));
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        Log.e("------xxx-----", "errorMsg:" + errorMsg);
                        Toast.makeText(mActivity, "加入直播失败", Toast.LENGTH_SHORT).show();
                        handler.postDelayed(getLastCourseTask, 5 * 1000);
                    }
                });
    }

    private boolean isLiveOverTime() {
        Date courseEndTime = new Date(endTime * 1000);
        Date currentTime = new Date(System.currentTimeMillis());
        return currentTime.after(courseEndTime);
    }

    private void setMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final String[] strArr = {"健身房模式", "家用模式"};
        builder.setItems(strArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int position) {
                DataStorageUtils.setInClubMode(position == 0);
                if (position == 0) {
                    DlgEdit dlgEdit = new DlgEdit(mActivity, false, new DlgEdit.EditOkListener() {
                        @Override
                        public void onOk(String bleAddress) {
                            if (TextUtils.isEmpty(bleAddress)) {
                                WidgetUtils.showToast("蓝牙地址不能为空");
                                return;
                            }
                            if (bleAddress.length() != 12) {
                                WidgetUtils.showToast("请输入12位蓝牙地址");
                                return;
                            }
                            getDlg().dismiss();
                            DataStorageUtils.setBleAddress(bleAddress);
                        }
                    });
                    dlgEdit.show("请输入蓝牙地址", "输入蓝牙地址");
                }
                Intent intent=new Intent(mContext,AtyMain2.class);
                mContext.startActivity(intent);
            }
        }).setTitle("请选择使用场景");
        builder.show();
    }

    // 退出账号
    private void logOut() {
        DlgTextMsg dlg = new DlgTextMsg(mActivity,
                new DlgTextMsg.ConfirmDialogListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOk() {
                        YJSNet.logout(LOG_TAG,
                                new YJSNet.OnRespondCallBack<RspLogOut>() {
                                    @Override
                                    public void onSuccess(RspLogOut data) {
                                        CurrentUser.instance().setPasswd("");
                                        ActivityManager.getInstance()
                                                .exitApplication();
                                        mActivity.startActivity(AtyLogin.class);
                                        // finish();
                                    }

                                    @Override
                                    public void onFailure(String errorMsg) {
                                        WidgetUtils.showToast(String.format(
                                                "退出失败", errorMsg));
                                    }
                                });
                    }

                    @Override
                    public void onCenter() {
                    }
                });
        dlg.setBtnString("取消", "确定");
        dlg.show("退出提示", "是否退出当前账号");
    }


}
