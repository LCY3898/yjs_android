package com.cy.yigym.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.efit.sport.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ejianshen on 15/7/16.
 */
public class FragmentServicPolicy extends BaseFragment{
    @BindView
    private CustomTitleView vTitle;

    @BindView
    private TextView tvContent;

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_service_policy;
    }

    @Override
    protected void initView(View contentView, LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {

        vTitle.setTitle("服务条款");
        vTitle.setTxtLeftText("       ");
        vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
        vTitle.setTxtLeftClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

    }
    @Override
    protected void initData(Bundle savedInstanceState) {
        try {
            InputStream is = mActivity.getAssets().open("servicepolicy.txt");
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String text = new String(buffer, "UTF-8");
            tvContent.setText(text);
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
}
