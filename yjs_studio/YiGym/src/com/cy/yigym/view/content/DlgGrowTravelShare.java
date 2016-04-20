package com.cy.yigym.view.content;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.cy.widgetlibrary.content.CustomDialog;
import com.efit.sport.R;

/**
 * Created by xiaoshu on 15/11/29.
 */
public class DlgGrowTravelShare {
    private ShareView svGrowTravel;
    private CustomDialog customDialog;
    public DlgGrowTravelShare(Context context){
        LayoutInflater mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dlgView=mInflater.inflate(R.layout.dlg_grow_travel_to_share,null);
        customDialog=new CustomDialog(context).setContentView(dlgView, Gravity.CENTER).setCanceledOnTouchOutside(false);
        svGrowTravel= (ShareView) dlgView.findViewById(R.id.svGrowTravel);
    }

    public ShareView getSvGrowTravel() {
        return svGrowTravel;
    }
    public void show(){
        customDialog.show();
    }
    public void dismiss(){
        customDialog.dismiss();
    }
}
