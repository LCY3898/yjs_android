package com.cy.yigym.view.content;

import android.content.Context;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.content.CustomDialog;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.aty.AtyIM;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

/**
 * Created by eijianshen on 15/9/9.
 */
public class DlgmeetSucc implements View.OnClickListener {

    private CustomDialog dialog = null;
    private ImageView ivShareClose;
    private CustomCircleImageView myHead;
    private CustomCircleImageView receiverHead;
    private TextView tvMeetPoint;
    private TextView tvMeetMyDis;
    private TextView tvMeetMyCal;
    private TextView tvMeetMyTime;
    private ShareView shareMeetData;
    private LinearLayout llSendMsg;

    public DlgmeetSucc(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dlg_succ_share, null);
        ivShareClose = (ImageView) view.findViewById(R.id.ivShareClose);
        myHead=(CustomCircleImageView)view.findViewById(R.id.myHead);
        receiverHead=(CustomCircleImageView)view.findViewById(R.id.receiverHead);
        tvMeetPoint=(TextView)view.findViewById(R.id.tvMeetPoint);
        tvMeetMyDis=(TextView)view.findViewById(R.id.tvMeetMyDis);
        tvMeetMyCal=(TextView)view.findViewById(R.id.tvMeetMyCal);
        tvMeetMyTime=(TextView)view.findViewById(R.id.tvMeetMyTime);
        shareMeetData=(ShareView)view.findViewById(R.id.shareMeetData);
        llSendMsg=(LinearLayout)view.findViewById(R.id.llSendMsg);
        ivShareClose.setOnClickListener(this);
        dialog = new CustomDialog(context).setContentView(view, Gravity.CENTER)
                .setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivShareClose:
                dialog.dismiss();
                break;
            default:
                break;
        }
        dialog.dismiss();
    }

    public void show() {
        dialog.show();
    }

    public CustomCircleImageView getMyHead() {
        return myHead;
    }

    public CustomCircleImageView getReceiverHead() {
        return receiverHead;
    }

    public TextView getTvMeetPoint() {
        return tvMeetPoint;
    }

    public TextView getTvMeetMyDis() {
        return tvMeetMyDis;
    }

    public TextView getTvMeetMyCal() {
        return tvMeetMyCal;
    }

    public TextView getTvMeetMyTime() {
        return tvMeetMyTime;
    }

    public ShareView getShareMeetData() {
        return shareMeetData;
    }

    public LinearLayout getLlSendMsg() {
        return llSendMsg;
    }
}
