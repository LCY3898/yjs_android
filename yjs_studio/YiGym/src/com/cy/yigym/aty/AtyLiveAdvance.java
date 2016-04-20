package com.cy.yigym.aty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.adapter.LiveAdvanceAdapter;
import com.cy.yigym.entity.LiveAdvanceEntity;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqNotifyCourses;
import com.cy.yigym.net.rsp.RspNotifyCourses;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ejianshen on 15/9/21.
 */
public class AtyLiveAdvance extends BaseFragmentActivity implements View.OnClickListener {

    @BindView
    private View rlAdvanceTime1,rlAdvanceTime2,rlAdvanceTime3,rlAdvanceTime4;
    /**预告星期*/
    @BindView
    private TextView tvDay1,tvDay2,tvDay3,tvDay4;
    /**预告日期*/
    @BindView
    private TextView tvDate1,tvDate2,tvDate3,tvDate4;
    @BindView
    private CustomTitleView tvTitle;
    @BindView
    private ListView listView;
    @BindView
    private ImageView ivLiveBg;
    private LiveAdvanceEntity entity;
    private List<LiveAdvanceEntity> entityList=new ArrayList<LiveAdvanceEntity>();
    private LiveAdvanceAdapter adapter;
    /**
     * 1~4天
     */
    private final static int FIRSTDAY=0;
    private final static int SECONDDAY=1;
    private final static int THIRDDAY=2;
    private final static int FORTHDAY=3;
    /**格式化时间*/
    private SimpleDateFormat format=new SimpleDateFormat("HH:mm");
    //private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_live_advance;
    }

    @Override
    protected void initView() {
        tvTitle.setTitle("直播预告");
        tvTitle.setTxtLeftText("       ");
        tvTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
        tvTitle.setTxtLeftClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        rlAdvanceTime1.setOnClickListener(this);
        rlAdvanceTime2.setOnClickListener(this);
        rlAdvanceTime3.setOnClickListener(this);
        rlAdvanceTime4.setOnClickListener(this);
        rlAdvanceTime1.setSelected(true);

    }

    @Override
    protected void initData() {
      setAdvanceDatas(FIRSTDAY);
        initDatePick();
        adapter=new LiveAdvanceAdapter(mActivity,entityList,LOG_TAG);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlAdvanceTime1:
                setAdvanceDatas(FIRSTDAY);
                break;
            case R.id.rlAdvanceTime2:
                setAdvanceDatas(SECONDDAY);
                break;
            case R.id.rlAdvanceTime3:
                setAdvanceDatas(THIRDDAY);
                break;
            case R.id.rlAdvanceTime4:
                setAdvanceDatas(FORTHDAY);
                break;
        }
        ResetBgAndTextColor(v.getId());

    }
    private void ResetBgAndTextColor(int id){
        rlAdvanceTime1.setSelected(id==R.id.rlAdvanceTime1);
        rlAdvanceTime2.setSelected(id==R.id.rlAdvanceTime2);
        rlAdvanceTime3.setSelected(id==R.id.rlAdvanceTime3);
        rlAdvanceTime4.setSelected(id == R.id.rlAdvanceTime4);
    }
    private void setAdvanceDatas(int flag){
        YJSNet.send(new ReqNotifyCourses(flag), LOG_TAG, new YJSNet.OnRespondCallBack<RspNotifyCourses>() {
            @Override
            public void onSuccess(RspNotifyCourses data) {
                RspNotifyCourses.Data data1 = data.data;
                entityList.clear();
                if (adapter != null) {
                    adapter.clear();
                }
                if(data1.course_list==null||data1.course_list.size()==0){
                    return;
                }else {
                    for (int i = 0; i < data1.course_list.size(); i++) {
                        String beginTime = format.format(new Date(data1.course_list.get(i).begin_time * 1000));
                        entity = new LiveAdvanceEntity(data1.course_list.get(i).course_name
                                , beginTime
                                , data1.course_list.get(i).coach_name
                                , "" + (data1.course_list.get(i).course_time) / 60
                                , "" + data1.course_list.get(i).calorie + "K");
                        entity.setCourseId(data1.course_list.get(i)._id);
                        entity.setIsSubscribed(data1.course_list.get(i).isSubscribed);
                        entity.setCourseFid(data1.course_list.get(i).course_fid);
                        entityList.add(entity);
                    }
                    if (adapter == null) {
                        adapter = new LiveAdvanceAdapter(mContext, entityList, LOG_TAG);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(mContext, "获取直播预告失败" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 初始化选择控件
     */
    private void initDatePick(){

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        tvDate1.setText(mMonth+"月"+mDay+"日");
        tvDate2.setText(mMonth+"月"+(Integer.parseInt(mDay)+1)+"日");
        tvDate3.setText(mMonth+"月"+(Integer.parseInt(mDay)+2)+"日");
        tvDate4.setText(mMonth+"月"+(Integer.parseInt(mDay)+3)+"日");
        if("1".equals(mWay)){
            tvDay1.setText("星期日");
            tvDay2.setText("星期一");
            tvDay3.setText("星期二");
            tvDay4.setText("星期三");
        }else if("2".equals(mWay)){
            tvDay4.setText("星期四");
            tvDay1.setText("星期一");
            tvDay2.setText("星期二");
            tvDay3.setText("星期三");
        }else if("3".equals(mWay)){
            tvDay3.setText("星期四");
            tvDay4.setText("星期五");
            tvDay1.setText("星期二");
            tvDay2.setText("星期三");
        }else if("4".equals(mWay)){
            tvDay2.setText("星期四");
            tvDay3.setText("星期五");
            tvDay4.setText("星期六");
            tvDay1.setText("星期三");
        }else if("5".equals(mWay)){
            tvDay1.setText("星期四");
            tvDay2.setText("星期五");
            tvDay3.setText("星期六");
            tvDay4.setText("星期日");
        }else if("6".equals(mWay)){
            tvDay1.setText("星期五");
            tvDay2.setText("星期六");
            tvDay3.setText("星期日");
            tvDay4.setText("星期一");
        }else if("7".equals(mWay)){
            tvDay1.setText("星期六");
            tvDay2.setText("星期日");
            tvDay3.setText("星期一");
            tvDay4.setText("星期二");
        }
    }
}
