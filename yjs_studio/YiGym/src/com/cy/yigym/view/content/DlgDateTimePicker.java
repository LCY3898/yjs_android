package com.cy.yigym.view.content;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.cy.widgetlibrary.content.CustomDialog;
import com.efit.sport.R;
import com.sport.efit.theme.ColorTheme;

/**
 * Created by eijianshen on 15/8/20.
 */
public class DlgDateTimePicker implements View.OnClickListener{
    private  DateTimePickView pvTimePicker;
    private CustomDialog timePickerDlg;
    private LayoutInflater inflater;
    private Button btnCancel;
    private Button btnSure;
    private String time;



    public DlgDateTimePicker(Context context){
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        init(context);
    }

    public void init(Context context){
        View view=inflater.inflate(R.layout.dlg_date_time_picker,null);
        timePickerDlg=new CustomDialog(context).setContentView(view, Gravity.CENTER)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true).setAnimations(R.style.PopupAnimation);
        pvTimePicker=(DateTimePickView)view.findViewById(R.id.pvTimePicker);
        btnCancel=(Button)view.findViewById(R.id.btnCancel);
        btnSure=(Button)view.findViewById(R.id.btnSure);
        btnCancel.setBackground(ColorTheme.getSkyBlueBgBtn());
        btnSure.setBackground(ColorTheme.getSkyBlueBgBtn());
        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCancel :dismiss();
                break;
            case R.id.btnSure:   break;
        }
    }


    public DateTimePickView getPvTimePicker(){
        return pvTimePicker;}

    public Button getBtnCancel(){return btnCancel;}

    public Button getBtnSure(){return btnSure;}

    public void show(){
        timePickerDlg.show();
    }

    public void dismiss(){
        timePickerDlg.dismiss();
    }
}
