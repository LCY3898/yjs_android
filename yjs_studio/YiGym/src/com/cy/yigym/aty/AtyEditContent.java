package com.cy.yigym.aty;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.db.dao.GrowthRecordDao;
import com.cy.yigym.entity.GrowthRecordBean;
import com.cy.yigym.net.rsp.RspGrowthTravel;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

import org.w3c.dom.Text;

/**
 * Created by xiaoshu on 15/11/28.
 */
public class AtyEditContent extends BaseFragmentActivity {

    @BindView
    private CustomTitleView ctvEditTitle;
    @BindView
    private EditText etContent;

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    private int medalNumber = -1;

    private GrowthRecordDao recordDao;

    @Override
    protected void initView() {
        recordDao = new GrowthRecordDao();
        medalNumber = getMedalNumberFromCache();
        if (medalNumber > 0) {
            GrowthRecordBean bean = recordDao.getByMedalNumber(medalNumber);
            if (bean != null) {
                etContent.setText(bean.getSignContent());
                etContent.setSelection(bean.getSignContent().length());
            }
        }

        ctvEditTitle.setTxtLeftIcon(R.drawable.header_back);
        ctvEditTitle.setTitle("输入文字");
        ctvEditTitle.setTxtRightText("确认");
        ctvEditTitle.setTxtLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ctvEditTitle.setTxtRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etContent.getText().toString().trim() != null) {
                    //DataStorageUtils.setEditContent(etContent.getText().toString());
                    if (medalNumber > 0) {
                        GrowthRecordBean bean = new GrowthRecordBean();
                        bean.setMedalNumber(medalNumber);
                        bean.setSignContent(etContent.getText().toString());
                        recordDao.addOrUpdate(bean);
                    }
                }
                finish();
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_edit_share_content;
    }

    private int getMedalNumberFromCache() {
        int res = DataStorageUtils.getMedalNumForEditText();
        return res;
    }
}
