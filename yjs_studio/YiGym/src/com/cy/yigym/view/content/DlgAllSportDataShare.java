package com.cy.yigym.view.content;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.cy.widgetlibrary.content.CustomDialog;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.efit.sport.R;

/**
 * Created by eijianshen on 15/9/11.
 */
public class DlgAllSportDataShare extends PopupWindow implements View.OnClickListener{

    private ShareViewDlg allDataShare;
    private View viewToShare;
    private Button btnShareCancel;

    public ShareViewDlg getAllDataShare(){
        return allDataShare;
    }
    public DlgAllSportDataShare(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewToShare=inflater.inflate(R.layout.dlg_all_sport_data_share,null);
        //dialog=new CustomDialog(context).setContentView(viewToShare, Gravity.BOTTOM)
         //       .setCanceledOnTouchOutside(false).setAnimations(R.anim.option_entry_from_bottom);
        allDataShare=(ShareViewDlg)viewToShare.findViewById(R.id.allDataShare);

        //设置SelectPicPopupWindow的View
        this.setContentView(viewToShare);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(GridLayout.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popStyle);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        viewToShare.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = viewToShare.findViewById(R.id.pop_share).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        btnShareCancel= (Button) viewToShare.findViewById(R.id.btnShareCancel);
        btnShareCancel.setBackgroundDrawable(BgDrawableUtils.crePressSelector(
                BgDrawableUtils.creShape(0xffC4C5C6, 0),
                BgDrawableUtils.creShape(0xff48CDFC, 0)));
        btnShareCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
       dismiss();
    }

}
