package com.cy.yigym.view.content;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.content.CustomDialog;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.efit.sport.R;

/**
 * Created by xiaoshu on 15/12/1.
 */
public class DlgAllDataToShare {

    private LayoutInflater inflater;
    private TextView tvSportNum;
    private CustomCircleImageView ccvMyHead;
    private TextView tvMyName;
    private TextView tvAllTime;
    private TextView tvAllDistance;
    private TextView tvAllCalrie;
    private ShareView svAllDataToShare;
    private LinearLayout llShareView;
    private RelativeLayout rlAllDataShare;
    private CustomDialog dialog;

    public DlgAllDataToShare(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dlg_all_data_to_share, null);
        tvSportNum = (TextView) view.findViewById(R.id.tvSportNum);
        ccvMyHead = (CustomCircleImageView) view.findViewById(R.id.ccvMyHead);
        tvMyName = (TextView) view.findViewById(R.id.tvMyName);
        tvAllTime = (TextView) view.findViewById(R.id.tvAllTime);
        tvAllDistance = (TextView) view.findViewById(R.id.tvAllDistance);
        tvAllCalrie = (TextView) view.findViewById(R.id.tvAllCalrie);
        svAllDataToShare = (ShareView) view.findViewById(R.id.svAllDataToShare);
        llShareView = (LinearLayout) view.findViewById(R.id.llShareView);
        llShareView.setBackground(BgDrawableUtils.creShape(0xffffffff, new float[]{0, 0, 0, 0, 5, 5, 5, 5}));
        rlAllDataShare = (RelativeLayout) view.findViewById(R.id.rlAllDataShare);
        //rlAllDataShare.setBackground(BgDrawableUtils.creShape(0xffffffff, new float[]{5, 5, 5, 5, 0, 0, 0, 0}));
        dialog = new CustomDialog(context).setContentView(view, Gravity.CENTER)
                .setCanceledOnTouchOutside(false);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public TextView getTvSportNum() {
        return tvSportNum;
    }

    public CustomCircleImageView getCcvMyHead() {
        return ccvMyHead;
    }

    public TextView getTvMyName() {
        return tvMyName;
    }

    public TextView getTvAllTime() {
        return tvAllTime;
    }

    public TextView getTvAllDistance() {
        return tvAllDistance;
    }

    public TextView getTvAllCalrie() {
        return tvAllCalrie;
    }

    public ShareView getSvAllDataToShare() {
        return svAllDataToShare;
    }
}
