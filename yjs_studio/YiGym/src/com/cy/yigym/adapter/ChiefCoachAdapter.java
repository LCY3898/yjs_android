package com.cy.yigym.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.entity.LiveVideoEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqAppointment;
import com.cy.yigym.net.req.ReqGetLastCourse;
import com.cy.yigym.net.req.ReqJoinLiveCast;
import com.cy.yigym.net.rsp.RspChiefCoach;
import com.cy.yigym.net.rsp.RspGetLastCourse;
import com.cy.yigym.net.rsp.RspJoinLiveCast;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.StringUtils;
import com.cy.yigym.view.content.live.DlgLiveAdvance;
import com.efit.sport.R;
import com.efit.sport.livecast.AtyLiveCast;
import com.efit.sport.livecast.LiveCastHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.videolan.vlc.ui.VlcVideoActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaoshu on 15/11/11.
 */
public class ChiefCoachAdapter extends AdapterBase<RspChiefCoach.Data> {

    private RelativeLayout.LayoutParams params;
    private DisplayImageOptions options;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private RelativeLayout rlCourseOrder;
    private TextView tvCourseOrder;
    private DlgLiveAdvance dlgLiveAdvance;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ChiefCoachAdapter(Context context, List<RspChiefCoach.Data> list) {
        super(context, list);
        params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources()
                .getDisplayMetrics().heightPixels / 4);
        options = ImageLoaderUtils.getInstance().createDisplayOptions(
                R.drawable.bg_third, R.drawable.bg_third, R.drawable.bg_third);
    }

    @Override
    protected View getItemView(int position, View convertView,
                               ViewGroup parent, RspChiefCoach.Data entity) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_chief_coach_view,
                    parent, false);
        }
        CustomCircleImageView chiefCoachHeadView = getHolderView(convertView,
                R.id.chiefCoachHeadView);
        ImageLoaderUtils.getInstance().loadImage(
                DataStorageUtils.getHeadDownloadUrl(entity.profile_fid),
                chiefCoachHeadView);
        TextView tvChiefCoachName = getHolderView(convertView,
                R.id.tvChiefCoachName);
        tvChiefCoachName.setText(entity.coach_name);
        TextView tvCoachNum = getHolderView(convertView, R.id.tvCoachNum);
        tvCoachNum.setText("今日有" + entity.course_number + "节课程");
        LinearLayout llViewAdd = getHolderView(convertView, R.id.llViewAdd);
        llViewAdd.removeAllViews();
        for (RspChiefCoach.CourseList course : entity.course_list) {
            if (entity.coach_name.equals(course.coach_name)) {
                llViewAdd.addView(creatChildView(llViewAdd, course));
            }
        }

        return convertView;
    }

    @SuppressWarnings("deprecation")
    private View creatChildView(ViewGroup parent,
                                final RspChiefCoach.CourseList course) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_chief_coach_course, parent, false);
        ImageView ivChiefCourseImage = (ImageView) view
                .findViewById(R.id.ivChiefCourseImage);
        ImageLoaderUtils.getInstance().loadImage(
                DataStorageUtils.getHeadDownloadUrl(course.course_fid),
                ivChiefCourseImage, 0, 0, -1, options);
        // ivChiefCourseImage.setLayoutParams(params);
        TextView tvChiefCoachTitle = (TextView) view
                .findViewById(R.id.tvChiefCoachTitle);
        tvChiefCoachTitle.setText(course.course_name);
        TextView tvCoursebeginTime = (TextView) view
                .findViewById(R.id.tvCoursebeginTime);
        SpannableStringBuilder sb = StringUtils.creSpanString(new String[]{
                        format.format(new Date(course.begin_time * 1000)), "开始"},
                new int[]{0xffffffff, 0xffffffff}, new int[]{24, 17});
        tvCoursebeginTime.setText(sb);
        tvCourseOrder = (TextView) view.findViewById(R.id.tvCourseOrder);
        rlCourseOrder = (RelativeLayout) view.findViewById(R.id.rlCourseOrder);
        rlCourseOrder.setTag(course);
        ImageView ivImageprompt = (ImageView) view
                .findViewById(R.id.ivImageprompt);
        // 课程结束时间
        final long endTime = course.end_time;
        tvCourseOrder.setText(parseCourseStatusType(course.isSubscribed,
                course.begin_time, endTime).desc);
        if (CourseStatusType.LIVE.desc.equals(tvCourseOrder.getText()
                .toString())
                || CourseStatusType.WATCH.desc.equals(tvCourseOrder.getText()
                .toString())) {
            ivImageprompt.setBackgroundResource(R.drawable.casting_icon);
            StateListDrawable bg = BgDrawableUtils.crePressSelector(0xff5ed1fe,
                    0xff4abbe7, 5);
            rlCourseOrder.setBackgroundDrawable(bg);

        } else {
            ivImageprompt.setBackgroundResource(R.drawable.yueyueic);
            StateListDrawable bg = BgDrawableUtils.crePressSelector(0xff818aa9,
                    0xff697497, 5);
            rlCourseOrder.setBackgroundDrawable(bg);
        }
        rlCourseOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseStatusType status = parseCourseStatusType(
                        course.isSubscribed, course.begin_time, endTime);
                switch (status) {
                    case SUBSCRIBE:
                        onCourseClick(course, 1);
                        break;
                    case CANCEL_SUBSCRIBE:
                        onCourseClick(course, -1);
                        break;
                    // 观看和直播都进入到视频播放页面
                    case LIVE:
                        JoinLive();
                        break;
                    case WATCH:
                        String videoUrl = DataStorageUtils
                                .getMp4Url(course.video_link);
                        if (TextUtils.isEmpty(videoUrl)) {
                            WidgetUtils.showToast("链接为空,视频已下架");
                            return;
                        }
                        Intent intent = new Intent(mContext, AtyLiveCast.class);
                        LiveCastHelper.saveVideoInfo(intent, LiveCastHelper
                                .genVideoInfo(course.course_id, course.coach_name,
                                        course.course_name, -1, videoUrl
                                        , course.course_fid));
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
        return view;
    }

    /**
     * 课程状态类型
     */
    public static enum CourseStatusType {
        SUBSCRIBE("点击预约"), CANCEL_SUBSCRIBE("取消预约"), WATCH("点击观看"), LIVE("点击观看");
        public String desc = "";

        CourseStatusType(String desc) {
            this.desc = desc;
        }
    }

    /**
     * 解析课程状态
     *
     * @param isSubscribed -1为未预约，1为预约
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @return
     */
    public static CourseStatusType parseCourseStatusType(int isSubscribed,
                                                         long beginTime, long endTime) {
        long curTime = System.currentTimeMillis() / 1000;
        if (curTime >= beginTime && curTime <= endTime) {
            // 如果已经是处于直播状态了
            return CourseStatusType.LIVE;
        } else if (curTime > endTime) {
            // 直播完毕显示“点击播放”状态
            return CourseStatusType.WATCH;
        } else if (isSubscribed == -1) {
            // 如果未预约，显示“点击预约”状态
            return CourseStatusType.SUBSCRIBE;
        } else if (isSubscribed == 1 && curTime < beginTime) {
            // 如果已经预约了，并且还没开始，则可以“取消预约”
            return CourseStatusType.CANCEL_SUBSCRIBE;
        }
        return CourseStatusType.SUBSCRIBE;

    }

    /**
     * 课程点击操作
     *
     * @param course
     * @param isSubscribe
     */
    private void onCourseClick(final RspChiefCoach.CourseList course,
                               final int isSubscribe) {
        if (course == null)
            return;
        dlgLiveAdvance = new DlgLiveAdvance(mContext);
        final long dismissTime = 2000;
        YJSNet.send(new ReqAppointment(course.course_id, isSubscribe),
                ChiefCoachAdapter.class.getSimpleName(),
                new YJSNet.OnRespondCallBack<RspBase>() {
                    @Override
                    public void onSuccess(RspBase data) {
                        if (isSubscribe == 1) {
                            dlgLiveAdvance.getTvLiveAdvance().setText(
                                    "预约成功，提前30分钟提醒");
                            course.isSubscribed = 1;
                        } else if (isSubscribe == -1) {
                            dlgLiveAdvance.getTvLiveAdvance()
                                    .setText("已为您取消预约");
                            course.isSubscribed = -1;
                        }
                        dlgLiveAdvance.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dlgLiveAdvance.dismiss();
                                notifyDataSetChanged();
                            }
                        }, dismissTime);

                    }

                    @Override
                    public void onFailure(String errorMsg) {

                        if (isSubscribe == 1) {
                            dlgLiveAdvance.getTvLiveAdvance().setText(
                                    "预约失败，请重试");
                        } else if (isSubscribe == -1) {
                            dlgLiveAdvance.getTvLiveAdvance().setText(
                                    "取消预约失败，请重试");

                        }
                        dlgLiveAdvance.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dlgLiveAdvance.dismiss();
                            }
                        }, dismissTime);

                    }
                });
    }

    /**
     * 加入直播
     */

    private void JoinLive() {
        if (DataStorageUtils.getLastCourse().lastCourse._id == null) {
            WidgetUtils.showToast("课程id不能为空");
            return;
        }
        if (TextUtils.isEmpty(DataStorageUtils.getLastCourse().live_broadcast_addr)) {
            WidgetUtils.showToast("直播地址不能为空");
            return;
        }
        long currentTime = System.currentTimeMillis();
        long joinTime = currentTime / 1000 - DataStorageUtils
                .getLastCourse().lastCourse.begin_time;
        YJSNet.send(
                new ReqJoinLiveCast(DataStorageUtils.getLastCourse().lastCourse._id,
                        DataStorageUtils.getNetEaseAccount().accid, DataStorageUtils.getNetEaseAccount().token, joinTime), mContext.getClass().getName(),
                new YJSNet.OnRespondCallBack<RspJoinLiveCast>() {
                    @Override
                    public void onSuccess(RspJoinLiveCast data) {
                        Intent intent = new Intent(mContext,
                                VlcVideoActivity.class);
                        if (DataStorageUtils.isGetLastCourse()) {
                            intent.putExtra("live_course", new LiveVideoEntity(DataStorageUtils
                                    .getLastCourse().lastCourse._id, DataStorageUtils
                                    .getLastCourse().live_broadcast_addr, DataStorageUtils
                                    .getLastCourse().lastCourse.begin_time, DataStorageUtils
                                    .getLastCourse().lastCourse.end_time));
                        } else {
                            intent.putExtra("live_course", getLastCourse());
                        }
                        DataStorageUtils.setCourseId(DataStorageUtils.getLastCourse().lastCourse._id);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        Toast.makeText(mContext, "加入直播失败", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

    }

    private LiveVideoEntity liveVideoEntity;

    /**
     * 主页没有加载到直播课程时调用该方法，再次加载直播课程信息
     *
     * @return
     */
    public LiveVideoEntity getLastCourse() {
        YJSNet.send(new ReqGetLastCourse(), mContext.getClass().getName(), new YJSNet.OnRespondCallBack<RspGetLastCourse>() {
            @Override
            public void onSuccess(RspGetLastCourse data) {
                liveVideoEntity = new LiveVideoEntity(data.data.lastCourse._id, data.data.live_broadcast_addr
                        , data.data.lastCourse.begin_time, data.data.lastCourse.end_time);
                DataStorageUtils.saveLastCourse(data.data);
                DataStorageUtils.setGetLastCourse(true);
                DataStorageUtils.setCourseId(data.data.lastCourse._id);
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
        return liveVideoEntity;
    }
}
