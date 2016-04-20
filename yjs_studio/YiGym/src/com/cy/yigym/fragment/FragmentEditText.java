package com.cy.yigym.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efit.sport.R;

/**
 * Created by lijianqiang on 15/11/21.
 */
public class FragmentEditText extends DialogFragment {

    public static final String TAG = "FragmentEditText";

    private LinearLayout mRootView;

    private Button mConfirm;

    private Button mCancel;

    private TextView mTitle;

    private EditText mValue;

    private EditFinishCallBack callBack;

    private String title;

    private String value;

    private int inputType;

    private String hint;

    public FragmentEditText() {
        this("提示", "");
    }

    @SuppressLint("ValidFragment")
    public FragmentEditText(String title, String value) {
        this.title = title;
        this.value = value;
        this.inputType = InputType.TYPE_CLASS_TEXT;
        this.hint = "请输入";
    }


    public void setCallBack(EditFinishCallBack callBack) {
        this.callBack = callBack;
    }

    public void setTitile(String title) {
        this.title = title;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mRootView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_edittext,
                null);
        mConfirm = (Button) mRootView.findViewById(R.id.fragment_edittext_btn_confirm);
        mCancel = (Button) mRootView.findViewById(R.id.fragment_edittext_btn_cancel);
        mTitle = (TextView) mRootView.findViewById(R.id.fragment_edittext_title);
        mValue = (EditText) mRootView.findViewById(R.id.fragment_edittext_handler);

        initValue();

        initListener();

        return new AlertDialog.Builder(getActivity()).setView(mRootView).create();
    }

    private void initValue() {
        mTitle.setText(title);
        mValue.setText(value);
        mValue.setHint(hint);
        mValue.setInputType(inputType);
        if (TextUtils.isEmpty(value) == false) {
            mValue.setSelection(value.length());
        }
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

                String value = mValue.getText().toString();

                value = value.trim();

                if (TextUtils.isEmpty(value)) {
                    return;
                }


                if (callBack != null) {
                    Log.d(TAG, "callBack.onConfirm():" + value);
                    callBack.onConfirm(value);
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

    public interface EditFinishCallBack {
        public void onConfirm(String text);
    }


}
