package com.cy.widgetlibrary.content;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

import com.cy.widgetlibrary.utils.BgDrawableUtils;

public class DlgEdit extends DlgBase {
	EditDialogListener listener;
	
	boolean showCancel = true;
	public EditText editText;
	private Object data;
	private boolean okToDismiss = true;

	public DlgEdit(Context context, EditDialogListener l) {
		this(context,true,l);
	}

	public DlgEdit(Context context,boolean okToDismiss, EditDialogListener l) {
		super(context);
		if(l != null) {
			l.dlgEdit = this;
		}
		this.okToDismiss = okToDismiss;
		editText = new EditText(context);
		editText.setBackgroundDrawable(BgDrawableUtils.creShape(Color.WHITE, 5, 1,
				Color.parseColor("#ffcacaca"), null));
		editText.setTextColor(Color.parseColor("#ff474747"));
		editText.setHintTextColor(Color.parseColor("#ff9d9c9c"));
		addContentView(editText);
		listener = l;
	}
	
	
	public EditText getEditText() {
		return editText;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public static abstract class EditDialogListener {
		public abstract void onOk(String text);
		public abstract void onCancel();
		public DlgEdit getDlg() {
			return dlgEdit;
		}

		protected DlgEdit dlgEdit;
	}
	
	public static abstract class EditOkListener extends EditDialogListener {
		@Override
		public void onCancel() {
			
		}
	}
	
	public void showCancel(boolean showCancel) {
		this.showCancel = showCancel;
	}
	
	
	@Override
	public void show() {
		show(null,null,null);
	}
	
	public void show(String hint,String title) {
		show(hint,title,null);
	}
		
	public void show(String hint,String title,String defVal) {
		
		if(hint != null) {
			getEditText().setHint(hint);
		}
		
		if(defVal != null) {
			getEditText().setText(defVal);
		}
		
		getBtnLeft().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onCancel();
				}
				dialog.dismiss();
			}
		});
		
		getBtnRight().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onOk(getEditText().getText().toString().trim());
				}
				if(okToDismiss) {
					dialog.dismiss();
				}
			}
		});
		super.show();
	}
}
