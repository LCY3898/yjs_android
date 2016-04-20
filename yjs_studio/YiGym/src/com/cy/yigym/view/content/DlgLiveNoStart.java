package com.cy.yigym.view.content;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.content.CustomDialog;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

/**
 * Created by eijianshen on 15/9/21.
 */
public class DlgLiveNoStart implements View.OnClickListener {

    private TextView countDownTime;
    private CustomCircleImageView coachHeadImage;
    private TextView coachName;
    private Button btnWarmUp;
    private Button btnLookPreCourse;
    private Context mContext;
    private ImageView ivClose;

    private CustomDialog dialog;

    private LinearLayout llDlgLiveNoStart;
    private LinearLayout llLivenoStartBottom;


    private String courseFid = "";

    public DlgLiveNoStart(Context context) {
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dlg_live_no_start, null);
        countDownTime = (TextView) view.findViewById(R.id.countDownTime);
        coachHeadImage = (CustomCircleImageView) view.findViewById(R.id.coachHeadImage);
        coachName = (TextView) view.findViewById(R.id.coachName);
        btnWarmUp = (Button) view.findViewById(R.id.btnWarmUp);
        btnLookPreCourse = (Button) view.findViewById(R.id.btnLookPreCourse);
        ivClose = (ImageView) view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = new CustomDialog(mContext).setContentView(view, Gravity.CENTER).setCanceledOnTouchOutside(true);
        llDlgLiveNoStart= (LinearLayout) view.findViewById(R.id.llDlgLiveNoStart);
        llDlgLiveNoStart.setBackground(BgDrawableUtils.creShape(0xee283140,new float[]{5,5,5,5,5,5,5,5}));
        llLivenoStartBottom= (LinearLayout) view.findViewById(R.id.llLivenoStartBottom);
        llLivenoStartBottom.setBackground(BgDrawableUtils.creShape(0xffffffff,new float[]{0,0,0,0,5,5,5,5}));
    }

    @Override
    public void onClick(View v) {
    }

    public TextView getCountDownTime() {
        return countDownTime;
    }

    public CustomCircleImageView getCoachHeadImage() {
        return coachHeadImage;
    }

    public TextView getCoachName() {
        return coachName;
    }

    public Button getBtnWarmUp() {
        return btnWarmUp;
    }

    public Button getBtnLookPreCourse() {
        return btnLookPreCourse;
    }

    // 弹窗显示
    public void show() {

        if (!TextUtils.isEmpty(courseFid)) {
            ImageLoaderUtils.getInstance().loadImage(
                    DataStorageUtils.getHeadDownloadUrl(courseFid),
                    coachHeadImage);
        }
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setCoachHeadImage(String courseFid) {

        this.courseFid = courseFid;
    }
}
