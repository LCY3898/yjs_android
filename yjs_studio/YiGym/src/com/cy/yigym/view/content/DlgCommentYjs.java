package com.cy.yigym.view.content;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cy.widgetlibrary.content.CustomDialog;
import com.efit.sport.R;

/**
 * Created by ejianshen on 15/8/24.
 */
public class DlgCommentYjs implements View.OnClickListener {

    private View rlSupport, rlNoOpinion, rlIgnore;
    private CustomDialog dialog = null;

    public DlgCommentYjs(Context context) {
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dlgView = mInflater.inflate(R.layout.aty_comment_yjs, null);
        dialog = new CustomDialog(context).setContentView(dlgView, Gravity.CENTER).setCanceledOnTouchOutside(false);
        rlSupport = (View) dlgView.findViewById(R.id.rlSupport);
        rlNoOpinion = (View) dlgView.findViewById(R.id.rlNoOpinion);
        rlIgnore = (View) dlgView.findViewById(R.id.rlIgnore);
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

    public CustomDialog getDialog() {
        return dialog;
    }

    public View getRlSupport() {
        return rlSupport;
    }

    public View getRlNoOpinion() {
        return rlNoOpinion;
    }

    public View getRlIgnore() {
        return rlIgnore;
    }
}

