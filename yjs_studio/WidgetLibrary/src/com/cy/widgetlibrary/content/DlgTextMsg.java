package com.cy.widgetlibrary.content;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class DlgTextMsg extends DlgBase {
	ConfirmDialogListener listener;

	boolean showCancel = true;
	private TextView textView;

	public DlgTextMsg(Context context, ConfirmDialogListener l) {
		super(context);
		textView = new TextView(context);
		textView.setTextColor(Color.parseColor("#ff747474"));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		textView.setGravity(Gravity.CENTER);
		addContentView(textView);
		listener = l;
	}

	public void setIvBike() {
		getIvBike().setVisibility(View.VISIBLE);
	}

	public TextView getTextView() {
		return textView;
	}

	public void setText(String text) {
		getTextView().setText(text);

	}

	public static abstract class ConfirmDialogListener {
		public abstract void onCancel();

		public abstract void onOk();

		public void onCenter() {

		}
	}

	public static abstract class ConfirmOkListener extends
			ConfirmDialogListener {
		@Override
		public void onCancel() {

		}
	}

	public void showCancel(boolean showCancel) {
		this.showCancel = showCancel;
	}

	/**
	 * @param cancelString
	 *            通常为"取消"
	 * 
	 * @param okString
	 *            通常为"确定"
	 */
	public void setBtnString(String cancelString, String okString) {
		getBtnLeft().setText(cancelString);
		getBtnRight().setText(okString);
	}

	public void setCenterbtnString(String centerString) {
		getBtnCenter().setVisibility(View.VISIBLE);
		getLinFunction().setVisibility(View.GONE);
		getBtnCenter().setText(centerString);
	}

	@Override
	public void show() {
		show(null, null);
	}

	public void show(String message) {
		show(null, message);
	}

	public void show(String title, String message) {

		if (message != null) {
			getTextView().setText(message);
		}

		getBtnLeft().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onCancel();
				}
				dialog.dismiss();
			}
		});

		getBtnRight().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onOk();
				}
				dialog.dismiss();
			}
		});

		getBtnCenter().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onCenter();
				}
				dismiss();
			}
		});
		super.show();

		if (!showCancel) {
			getBtnRight().setVisibility(View.GONE);
		}
	}
}
