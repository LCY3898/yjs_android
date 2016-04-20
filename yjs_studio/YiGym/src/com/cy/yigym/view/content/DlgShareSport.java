package com.cy.yigym.view.content;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.content.CustomDialog;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-30
 * </p>
 * <p>
 * 分享运动数据的对话框
 * </p>
 */
public class DlgShareSport implements OnClickListener {

	private CustomDialog dialog = null;
	private TextView tvSportShareContent;
	private TextView tvShareSportDis;
	private TextView tvShareSportCal;
	private TextView tvShareSportTime;
	private ShareView svSportData;

	public DlgShareSport(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dlgView = mInflater.inflate(R.layout.dlg_sport_data_share, null);
		svSportData = (ShareView) dlgView.findViewById(R.id.svSportData);
		dialog = new CustomDialog(context).setContentView(dlgView,
				Gravity.CENTER).setCanceledOnTouchOutside(false);

		tvSportShareContent = (TextView) dlgView.findViewById(R.id.tvSportShareContent);
		tvShareSportDis= (TextView) dlgView.findViewById(R.id.tvShareSportDis);
		tvShareSportCal= (TextView) dlgView.findViewById(R.id.tvShareSportCal);
		tvShareSportTime= (TextView) dlgView.findViewById(R.id.tvShareSportTime);

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


	public TextView getTvSportShareContent() {
		return tvSportShareContent;
	}

	public TextView getTvShareSportDis() {
		return tvShareSportDis;
	}

	public TextView getTvShareSportCal() {
		return tvShareSportCal;
	}

	public TextView getTvShareSportTime() {
		return tvShareSportTime;
	}


	/**
	 * 获取分享控件，可以设置响应的分享回调及设置分享数据
	 *
	 * @return
	 */

	public ShareView getSvSportData() {
		return svSportData;
	}
}
