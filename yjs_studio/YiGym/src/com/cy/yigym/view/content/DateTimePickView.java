package com.cy.yigym.view.content;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.efit.sport.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xiaoshu on 15/8/19.
 */
public class DateTimePickView extends BaseView implements
        DatePicker.OnDateChangedListener,TimePicker.OnTimeChangedListener {


    @BindView
    private TextView tvTimeSet;
    @BindView
    private DatePicker dpDatePicker;
    @BindView
    private TimePicker tpTimerPicker;

    public int selectedYear;
    public int selectedMonth;
    public int  selectedDay;
    public int selectedHour;
    public int selectedMin;


    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
    String CurDateTime = formatter.format(curDate);

    public DateTimePickView(Context context) {
        super(context);
    }

    public DateTimePickView(Context context, AttributeSet atrrs) {
        super(context, atrrs);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.view_datetimepicker;
    }

    @Override
    protected void initView() {
        InitDataTime();
    }


    /**
     * 初始化DatePicker、TimePicker控件，默认显示为系统时间
     */
    public void InitDataTime() {
        tpTimerPicker.setIs24HourView(true);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dpDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
        tpTimerPicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        tpTimerPicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }


    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        this.selectedHour=hourOfDay;
        this.selectedMin=minute;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.selectedYear=year;
        this.selectedMonth=monthOfYear;
        this.selectedDay=dayOfMonth;
    }
    /**
     *  获取dpDatePicker控件所选中的年份
     */
    public int getSelectedYear() {
        selectedYear=dpDatePicker.getYear();
        return selectedYear;
    }

    /**
     *  获取dpDatePicker控件所选中的月份
     */
    public int getSelectedMonth(){
        selectedMonth=dpDatePicker.getMonth()+1;
        return selectedMonth;
    }

    /**
     *  获取dpDatePicker控件所选中的日期
     */
    public int getSelectedDay(){
        selectedDay=dpDatePicker.getDayOfMonth();
        return selectedDay;
    }
    /**
     *  获取dpDatePicker控件所选中的小时
     */
    public int getSelectedHour(){
        selectedHour=tpTimerPicker.getCurrentHour();
        return selectedHour;
    }
    /**
     *  获取tpTimerPicker控件所选中的分钟
     */
    public int getSelectedMin(){
        selectedMin =tpTimerPicker.getCurrentMinute();
        return selectedMin;
    }

}
