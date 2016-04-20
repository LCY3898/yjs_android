package com.cy.yigym.adapter;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.yigym.entity.LiveAdvanceEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqAppointment;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.live.DlgLiveAdvance;
import com.efit.sport.R;
import com.umeng.socialize.utils.Log;

import java.util.List;

/**
 * Created by ejianshen on 15/9/22.
 */
public class LiveAdvanceAdapter extends AdapterBase<LiveAdvanceEntity> {
    private Button btnOrder;
    private String LOG_TAG;
    private DlgLiveAdvance dlgLiveAdvance;
    private Handler handler = new Handler();
    private StateListDrawable selectBg;
    private StateListDrawable CancelBg;
    private ImageView ivLiveBg;

    public LiveAdvanceAdapter(Context context, List<LiveAdvanceEntity> list, String LOG_TAG) {
        super(context, list);
        this.LOG_TAG = LOG_TAG;
        this.selectBg = BgDrawableUtils.crePressSelector(0xff5ed1fe, 0xff4abbe7, 2);
        this.CancelBg = BgDrawableUtils.crePressSelector(0xff818aa9, 0xff697497, 2);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    protected View getItemView(final int position, View convertView,
                               ViewGroup parent, LiveAdvanceEntity entity1) {
        final LiveAdvanceEntity entity = mList.get(position);
        final View view = mInflater.inflate(R.layout.item_live_advance, null);
        ((TextView) getHolderView(view, R.id.tvCourse)).setText(entity.getCourse());
        ((TextView) getHolderView(view, R.id.tvStartTime)).setText(entity.getStartTime());
        ((TextView) getHolderView(view, R.id.tvCoach)).setText(entity.getCoach());
        ((TextView) getHolderView(view, R.id.tvCourseTime)).setText(entity.getCourseTime());
        ((TextView) getHolderView(view, R.id.tvCalorie)).setText(entity.getCalorie());
        if (entity.getIsSubscribed() == 1) {
            ((Button) getHolderView(view, R.id.btnIsOrder)).setText("取消预约");
            ((Button) getHolderView(view, R.id.btnIsOrder)).setBackgroundDrawable(CancelBg);
        } else {
            ((Button) getHolderView(view, R.id.btnIsOrder)).setText("立即预约");
            ((Button) getHolderView(view, R.id.btnIsOrder)).setBackgroundDrawable(selectBg);
        }
        btnOrder = ((Button) getHolderView(view, R.id.btnIsOrder));
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tttt", entity.getIsSubscribed() + "");
                if (entity.getIsSubscribed() == 1) {
                    ((Button) getHolderView(view, R.id.btnIsOrder)).setText("立即预约");
                    ((Button) getHolderView(view, R.id.btnIsOrder)).setBackgroundDrawable(selectBg);
                    IsOrder(entity.getCourseId(), -1, view, entity);
                } else {
                    ((Button) getHolderView(view, R.id.btnIsOrder)).setText("取消预约");
                    ((Button) getHolderView(view, R.id.btnIsOrder)).setBackgroundDrawable(CancelBg);
                    IsOrder(entity.getCourseId(), 1, view, entity);
                }

            }
        });


        ImageLoaderUtils.getInstance().loadImage(
                DataStorageUtils.getHeadDownloadUrl(entity.getCourseFid()),
                (ImageView) getHolderView(view,R.id.ivLiveBg));
        return view;
    }

    private void IsOrder(final String courseId, final int isOrder, final View view, final LiveAdvanceEntity entity) {
        dlgLiveAdvance = new DlgLiveAdvance(mContext);
        YJSNet.send(new ReqAppointment(courseId, isOrder), LOG_TAG, new YJSNet.OnRespondCallBack<RspBase>() {
            @Override
            public void onSuccess(RspBase data) {
                if (isOrder == 1) {
                    dlgLiveAdvance.getTvLiveAdvance().setText("预约成功，提前30分钟提醒");
                    dlgLiveAdvance.show();
                    entity.setIsSubscribed(1);
                } else {
                    dlgLiveAdvance.getTvLiveAdvance().setText("已为您取消预约");
                    dlgLiveAdvance.show();
                    entity.setIsSubscribed(-1);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dlgLiveAdvance.dismiss();
                    }
                }, 1000);

            }

            @Override
            public void onFailure(String errorMsg) {
                Log.d("tttt", errorMsg);
                if (isOrder == -1) {
                    ((Button) getHolderView(view, R.id.btnIsOrder)).setText("取消预约");
                    ((Button) getHolderView(view, R.id.btnIsOrder)).setBackgroundDrawable(CancelBg);
                } else {
                    ((Button) getHolderView(view, R.id.btnIsOrder)).setText("立即预约");
                    ((Button) getHolderView(view, R.id.btnIsOrder)).setBackgroundDrawable(selectBg);
                }
                dlgLiveAdvance.getTvLiveAdvance().setText("预约状态修改失败,请重试!");
                dlgLiveAdvance.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dlgLiveAdvance.dismiss();
                    }
                }, 1000);

            }
        });
    }

}
