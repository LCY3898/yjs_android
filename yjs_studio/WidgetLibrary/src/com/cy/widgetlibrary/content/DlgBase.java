package com.cy.widgetlibrary.content;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cy.widgetlibrary.R;
import com.cy.widgetlibrary.utils.BgDrawableUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2014-12-24下午6:02:00
 * </p>
 * <p>
 * 对话框基类，里面可以添加按钮
 * </p>
 */
public class DlgBase {
	protected CustomDialog dialog = null;
	protected Context mContext;
	protected LayoutInflater mInflater;
	private Button btnLeft, btnRight;
	private LinearLayout linContent, linFunction;
	private View vlineFunction;

	private Button btnCenter;

	private ImageView ivBike;

	public LinearLayout getLinContent() {
		return linContent;
	}

	public CustomDialog getDialog() {
		return dialog;
	}

	public Button getBtnLeft() {
		return btnLeft;
	}

	public Button getBtnRight() {
		return btnRight;
	}

	public ImageView getIvBike(){return ivBike;}


	/**
	 * 添加内容按钮，只能添加一个视图控件，若要满足添加多个的需求，请将他们放到viewGroup中，然后再调用本方法进行添加
	 * 
	 * @param contentView
	 */
	public void addContentView(View contentView) {
		//linContent.removeAllViews();
		linContent.addView(contentView);
	}

	public DlgBase(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		init(context);
	}

	public LinearLayout getLinFunction() {
		return linFunction;
	}

	private void init(Context context) {
		View dlgView = mInflater.inflate(R.layout.dlg_base, null);
		linContent = (LinearLayout) dlgView
				.findViewById(R.id.dlg_base_linContent);
		linContent.setBackground(BgDrawableUtils.creShape(0xffffffff,new float[] {
				5, 5, 5, 5,0, 0, 0, 0 }));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				mContext.getResources().getDisplayMetrics().heightPixels/3);

			//linContent.setLayoutParams(params);
		vlineFunction = (View) dlgView.findViewById(R.id.vlineFunction);
		linFunction = (LinearLayout) dlgView
				.findViewById(R.id.dlg_base_linFunction);
		btnLeft = (Button) dlgView.findViewById(R.id.dlg_base_btnLeft);
		btnRight = (Button) dlgView.findViewById(R.id.dlg_base_btnRight);
		btnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btnCenter =(Button)dlgView.findViewById(R.id.btnCenter);
		dialog = new CustomDialog(context).setContentView(dlgView,
				Gravity.CENTER).setCanceledOnTouchOutside(false);
		ivBike=(ImageView)dlgView.findViewById(R.id.ivBike);
	}

	public void show() {
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public View getLineFunction() {
		return vlineFunction;
	}

	public Button getBtnCenter(){return btnCenter;}
}
