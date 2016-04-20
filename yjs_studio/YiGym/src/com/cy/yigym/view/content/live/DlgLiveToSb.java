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
public class DlgLiveToSb implements View.OnClickListener {
    private CustomDialog dialog;
    private TextView tvNickname;
    private Button btnSure,btnCancel;

    public DlgLiveToSb(Context context) {

            LayoutInflater mInflater=(LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dlgView=mInflater.inflate(R.layout.dlg_view_send,null);
            dialog=new CustomDialog(context).setContentView(dlgView, Gravity.CENTER).setCanceledOnTouchOutside(false);
            tvNickname=(TextView) dlgView.findViewById(R.id.tvNickname);
            btnSure=(Button) dlgView.findViewById(R.id.btnSure);
            btnCancel=(Button) dlgView.findViewById(R.id.btnCancel);
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
    public TextView getTvNickname(){
        return tvNickname;
    }
    public Button getBtnSure(){
        return btnSure;
    }
    public Button getBtnCancel(){
        return btnCancel;
    }
}
