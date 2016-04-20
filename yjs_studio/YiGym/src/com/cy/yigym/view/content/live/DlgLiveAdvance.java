package com.cy.yigym.view.content.live;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cy.widgetlibrary.content.CustomDialog;
import com.efit.sport.R;

/**
 * Created by ejianshen on 15/9/15.
 */
public class DlgLiveAdvance implements View.OnClickListener {
    private CustomDialog dialog;
    private TextView tvLiveAdvance;

    public DlgLiveAdvance(Context context) {

            LayoutInflater mInflater=(LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dlgView=mInflater.inflate(R.layout.dlg_live_advance,null);
            dialog=new CustomDialog(context).setContentView(dlgView, Gravity.CENTER).setCanceledOnTouchOutside(false);
            tvLiveAdvance=(TextView) dlgView.findViewById(R.id.tvDlgLive);
        }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
    }
    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
    public TextView getTvLiveAdvance(){
        return tvLiveAdvance;
    }
}
