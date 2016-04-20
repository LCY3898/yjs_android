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
import com.cy.yigym.net.rsp.RspUpdatePersonInfo;
import com.efit.sport.R;

//填写签名
public class AtySign extends BaseFragmentActivity {

         @BindView
        //填写签名输入框
         private EditText et_sign;
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
            return R.layout.aty_sign;
        }
        @Override
        protected void initView() {

            vTitle.setTitle("我的资料");
            vTitle.setTxtLeftText("       ");
            vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
            vTitle.setTxtLeftClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
            vTitle.setTxtRightIcon(R.drawable.icon_check);
            vTitle.setTxtRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String sign=et_sign.getText().toString().trim();
                    if(sign.length()<=200){
                        YJSNet.updatePersonInfo(new ReqUpdatePersonInfo("","", "", "", "",
                                "", "", sign, "", ""), LOG_TAG, new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {
                            @Override
                            public void onSuccess(RspUpdatePersonInfo data) {
                                CurrentUser.instance().setUserSign(sign);
                                finish();
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                WidgetUtils.showToast(" 填写签名失败！"+errorMsg);
                            }
                        });
                    }else{
                        WidgetUtils.showToast("填写失败，签名最大长度为200");
                    }

                }
            });
        }
        @Override
        protected void initData(){
            editTextListener();
            Intent intent=getIntent();
            et_sign.setText(intent.getStringExtra("sign"));
            CharSequence text = et_sign.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }

        }
    private void editTextListener(){
        et_sign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (restNumber <= 200) {
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
