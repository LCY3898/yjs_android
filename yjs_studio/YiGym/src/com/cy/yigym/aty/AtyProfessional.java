package com.cy.yigym.aty;


import android.content.Intent;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqUpdatePersonInfo;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.net.rsp.RspUpdatePersonInfo;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

//填写职业
public class AtyProfessional extends BaseFragmentActivity {

        //填写职业输入框
        @BindView
        private EditText et_professional;
        //标题
        @BindView
        private CustomTitleView vTitle;
        @BindView
        private TextView tvSNumber;
        private int restNumber;
        @Override
        protected boolean isBindViewByAnnotation() {
            // TODO Auto-generated method stub
            return true;
        }
        @Override
        protected int getContentViewId() {
            return R.layout.aty_professional;
        }
        @Override
        protected void initView() {

            vTitle.setTitle("我的资料");
            vTitle.setTxtLeftText("       ");
            vTitle.setTxtLeftIcon(R.drawable.header_back);
            vTitle.setTxtLeftClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
            vTitle.setTxtRightIcon(R.drawable.header_check);
            vTitle.setTxtRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String professional = et_professional.getText().toString().trim();
                    if (professional.length() <= 20) {
                        YJSNet.updatePersonInfo(new ReqUpdatePersonInfo("", "", "", "", "",
                                "", professional, "", "", ""), LOG_TAG, new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {
                            @Override
                            public void onSuccess(RspUpdatePersonInfo data) {
                                CurrentUser.instance().setUserJob(professional);
                                finish();
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                WidgetUtils.showToast(" 填写职业失败！" + errorMsg);
                            }
                        });
                    } else {
                        WidgetUtils.showToast("填写失败，职业最大长度为20");
                    }

                }
            });

        }
        @Override
        protected void initData(){
            Intent intent=getIntent();
            editTextListener();
            et_professional.setText(intent.getStringExtra("job"));
            CharSequence text = et_professional.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }

        }
    private void editTextListener(){
        et_professional.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (restNumber <= 20) {
                    restNumber = s.length();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvSNumber.setText(restNumber + "");
            }
        });
    }
}
