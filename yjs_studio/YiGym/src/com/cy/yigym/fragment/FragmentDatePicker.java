package com.cy.yigym.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efit.sport.R;

import java.util.Calendar;

/**
 * Created by lijianqiang on 15/11/21.
 */
public class FragmentDatePicker extends DialogFragment {

    public static final String TAG = "FragmentDatePicker";

    private LinearLayout mRootView;

    private Button mConfirm;

    private Button mCancel;

    private TextView mTitle;

    private DatePicker mDatePicker;

    private DatePickerCallBack callBack;

    private int mYear;

    private int mMonthOfYear;

    private int mDayOfMonth;


    public FragmentDatePicker() {
        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonthOfYear = cal.get(Calendar.MONTH);
        mDayOfMonth = cal.get(Calendar.DATE);
    }

    public void setInitDate(int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonthOfYear = monthOfYear;
        mDayOfMonth = dayOfMonth;
    }

    public void setInitDateString(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return;
        }
        String[] date = dateStr.split("-");
        if (date.length < 2) {
            return;
        }
        try {
            mYear = Integer.valueOf(date[0]);
            mMonthOfYear = Integer.valueOf(date[1]) - 1;
            mDayOfMonth = Integer.valueOf(date[2]);
        } catch (Exception e) {
            Calendar cal = Calendar.getInstance();
            mYear = cal.get(Calendar.YEAR);
            mMonthOfYear = cal.get(Calendar.MONTH);
            mDayOfMonth = cal.get(Calendar.DATE);
        }

    }

    public void setCallBack(DatePickerCallBack callBack) {
        this.callBack = callBack;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mRootView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_datepicker, null);
        mConfirm = (Button) mRootView.findViewById(R.id.fragment_datepicker_btn_confirm);
        mCancel = (Button) mRootView.findViewById(R.id.fragment_datepicker_btn_cancel);
        mTitle = (TextView) mRootView.findViewById(R.id.fragment_datepicker_txt_title);
        mDatePicker = (DatePicker) mRootView.findViewById(R.id.fragment_datepicker_picker_handler);

        initValue();

        initListener();

        return new AlertDialog.Builder(getActivity()).setView(mRootView).create();
    }

    /**
     * <p>Description:              </p>
     *
     * @author lijianqiang
     */
    private void initValue() {

        mDatePicker.init(mYear, mMonthOfYear, mDayOfMonth, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonthOfYear = monthOfYear;
                mDayOfMonth = dayOfMonth;
            }
        });

    }

    /**
     * <p>Description:              </p>
     *
     * @author lijianqiang
     */
    private void initListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "mCancel");
                dismiss();
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "mConfirm");

                if (callBack != null) {
                    Log.d(TAG, "callBack.onProcess():dayOfMonth" + mDayOfMonth + "&monthOfYear:" + mMonthOfYear);
                    callBack.onConfirm(mYear, mMonthOfYear, mDayOfMonth);
                }

                dismiss();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public interface DatePickerCallBack {
        public void onConfirm(int year, int monthOfYear, int dayOfMonth);
    }
}
