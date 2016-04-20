package com.cy.yigym.view.content;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.cy.widgetlibrary.content.CustomDialog;
import com.efit.sport.R;

/**
 * Created by eijianshen on 15/7/30.
 */
public class DlgMeetSuccShare {

    private ShareView meetSuccDataShareView;

    private LayoutInflater inflater;

    private CustomDialog shareDialog;
    public DlgMeetSuccShare(Context context){
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init(context);
    }

    private void init(Context context){
        View view=inflater.inflate(R.layout.dlg_share,null);
        shareDialog=new CustomDialog(context).setContentView(view, Gravity.BOTTOM).setCanceledOnTouchOutside(false)
        .setCancelable(true).setAnimations(R.style.PopupAnimation);
        meetSuccDataShareView =(ShareView)view.findViewById(R.id.sportShare);


    }

    public ShareView getMeetSuccDataShareView() {
        return meetSuccDataShareView;
    }
   private  View.OnClickListener clickListener=new View.OnClickListener() {

       @Override
       public void onClick(View view) {
           shareDialog.dismiss();
       }
   };



    public void show(){
        shareDialog.show();
    }

    public void dismiss(){
        shareDialog.dismiss();
    }

    public CustomDialog getShareDialog(){return shareDialog;}



}
