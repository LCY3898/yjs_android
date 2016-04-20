package com.cy.yigym.view.content;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cy.widgetlibrary.content.CustomDialog;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.yigym.aty.AtyEditContent;
import com.efit.sport.R;
import com.sport.efit.theme.ColorTheme;

/**
 * Created by xiaoshu on 15/11/19.
 */
public class DlgAddPhoto {
	private ImageView ivPhoto;
	private EditText etDesctibe;
	private Button btnDeletePhoto;
	private Button btnSureAddPthoto;
	private CustomDialog dialog;
	private ImageView ivaddPhotoClose;
	private LinearLayout llContent;

	public DlgAddPhoto(final Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dlgView = inflater.inflate(R.layout.dlg_add_photo, null);

		dialog = new CustomDialog(context).setContentView(dlgView,
				Gravity.CENTER).setCanceledOnTouchOutside(true);
		ivPhoto = (ImageView) dlgView.findViewById(R.id.ivPhoto);
		etDesctibe = (EditText) dlgView.findViewById(R.id.etDesctibe);
		btnDeletePhoto = (Button) dlgView.findViewById(R.id.btnDeletePhoto);
		btnDeletePhoto.setBackground(ColorTheme.getShareDlgGreySelector());
		btnSureAddPthoto = (Button) dlgView.findViewById(R.id.btnSureAddPthoto);
		btnSureAddPthoto.setBackground(ColorTheme.getShareDlgBlueSelector());
		ivaddPhotoClose = (ImageView) dlgView
				.findViewById(R.id.ivaddPhotoClose);
		ivaddPhotoClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		llContent = (LinearLayout) dlgView.findViewById(R.id.llContent);
		llContent.setBackground(BgDrawableUtils.creShape(0xffffffff,
				new float[] { 0, 0, 0, 0, 5, 5, 5, 5 }));
		etDesctibe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AtyEditContent.class);
				context.startActivity(intent);
				dismiss();
			}
		});

		// etDesctibe.setText(DataStorageUtils.getEditContent());
	}

	public ImageView getIvPhoto() {
		return ivPhoto;
	}

	public EditText getEtDesctibe() {
		return etDesctibe;
	}

	public Button getBtnDeletePhoto() {
		return btnDeletePhoto;
	}

	public Button getBtnSureAddPthoto() {
		return btnSureAddPthoto;
	}

	public void show() {
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public void setDescTxt(String des) {
		etDesctibe.setText(des);
	}

}
