package com.cy.yigym.view.content;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.content.CustomDialog;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-22
 * </p>
 * <p>
 * 我的勋章对话框
 * </p>
 */
public class DlgMyMedal {
	private CustomDialog dialog;
	private TextView tvMedalMean;
	private ImageView ivMedalIcon;
	private ImageView ivClose;

	public DlgMyMedal(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dlgView = inflater.inflate(R.layout.dlg_my_medal, null);
		dialog = new CustomDialog(context).setContentView(dlgView,
				Gravity.CENTER).setCanceledOnTouchOutside(true);
		tvMedalMean = (TextView) dlgView.findViewById(R.id.tvMedalMean);
		ivMedalIcon = (ImageView) dlgView.findViewById(R.id.ivMedalIcon);
		ivClose = (ImageView) dlgView.findViewById(R.id.ivClose);
		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public void show(String medalFid, String medalMean) {
		if (!TextUtils.isEmpty(medalFid))
			ImageLoaderUtils.getInstance().loadImage(
					DataStorageUtils.getHeadDownloadUrl(medalFid), ivMedalIcon);
		if (!TextUtils.isEmpty(medalMean))
			tvMedalMean.setText(medalMean);
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}
}
