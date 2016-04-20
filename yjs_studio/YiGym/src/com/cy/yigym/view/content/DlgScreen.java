package com.cy.yigym.view.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.content.CustomDialog;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.sport.efit.theme.ColorTheme;

/**
 * Created by eijianshen on 15/7/22.
 */
public class DlgScreen {
	private CustomDialog dialog;
	private SeekBar seekbar;
	private TextView tv_screendistance;
	private RadioGroup rgSex;
	private RadioButton btnMale, btnFemale;
	private Button btnCancel, btnSure;
	private LayoutInflater inflater;
	private ChaseInfoPref chaseInfoPref;
	private TextView txtTitle;
	private LinearLayout linContent;

	public final static class ChaseInfoPref {
		@SerializedName("distance")
		public int distance;
		@SerializedName("sex")
		public String sex;

		public static ChaseInfoPref create() {
			ChaseInfoPref chaseInfoPref = null;
			String chaseInfo = DataStorageUtils.getChasePrefInfo();
			if (TextUtils.isEmpty(chaseInfo)) {
				/*
				 * chaseInfoPref = new ChaseInfoPref(); chaseInfoPref.distance =
				 * 10; chaseInfoPref.sex = "女";
				 */
			} else {
				chaseInfoPref = new Gson().fromJson(chaseInfo,
						ChaseInfoPref.class);
			}
			return chaseInfoPref;
		}
	}

	public DlgScreen(Context context) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		init(context);
	}

	private void initChaseInfo() {
		String chaseInfo = DataStorageUtils.getChasePrefInfo();
		if (TextUtils.isEmpty(chaseInfo)) {
			chaseInfoPref = new ChaseInfoPref();
			chaseInfoPref.distance = 10;
			chaseInfoPref.sex = "女";
		} else {
			chaseInfoPref = new Gson().fromJson(chaseInfo, ChaseInfoPref.class);
		}
	}

	private void saveChaseInfo() {
		DataStorageUtils.setChasePrefInfo(new Gson().toJson(chaseInfoPref));
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		initChaseInfo();
		View view = inflater.inflate(R.layout.dlg_screen, null);
		linContent = (LinearLayout) view.findViewById(R.id.linContent);
		txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		dialog = new CustomDialog(context).setContentView(view, Gravity.CENTER)
				.setCanceledOnTouchOutside(false).setCancelable(true);
		tv_screendistance = (TextView) view
				.findViewById(R.id.tv_screendistance);

		// 获取对话框进度条ID，同时设置进度条监听事件
		seekbar = (SeekBar) view.findViewById(R.id.screenseekbar);

		seekbar.setProgress(chaseInfoPref.distance);
		tv_screendistance.setText(chaseInfoPref.distance + "km");
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tv_screendistance.setText(progress + "km");
				chaseInfoPref.distance = progress;
				saveChaseInfo();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		// 获取设置男性按钮ID，同时设置按钮监听事件
		btnMale = (RadioButton) view.findViewById(R.id.btn_male);
		// 获取设置女性按钮ID，同时设置按钮监听事件
		btnFemale = (RadioButton) view.findViewById(R.id.btn_female);

		rgSex = (RadioGroup) view.findViewById(R.id.rgSex);
		rgSex.check(chaseInfoPref.sex.equals("男") ? R.id.btn_male
				: R.id.btn_female);
		rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.btn_male) {
					chaseInfoPref.sex = "男";
				} else {
					chaseInfoPref.sex = "女";
				}
				saveChaseInfo();
			}
		});

		// 获取取消按钮ID，同时设置按钮监听事件
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// 获取确定按钮ID，同时设置按钮监听事件
		btnSure = (Button) view.findViewById(R.id.btn_sure);
		btnSure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		linContent.setBackgroundDrawable(ColorTheme.creWhiteShape());
		btnCancel.setBackgroundDrawable(ColorTheme.getDlgCancelBtnSelector());
		btnSure.setBackgroundDrawable(ColorTheme.getDlgEnsureBtnSelector());
		txtTitle.setBackgroundDrawable(ColorTheme.getDlgTitleShape());
		seekbar.setThumb(creThumb());
	}

	/**
	 * 创建seekbar的thumb图片
	 * 
	 * @return
	 */
	private BitmapDrawable creThumb() {
		int size = WidgetUtils.dpToPx(35);
		Bitmap thumb = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(thumb);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		int dp6 = WidgetUtils.dpToPx(8);
		paint.setStrokeWidth(dp6);
		canvas.drawCircle(size / 2, size / 2, (int) ((size - dp6) / 2.0), paint);
		return new BitmapDrawable(thumb);
	}

	public void show() {
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public CustomDialog getDialog() {
		return dialog;
	}

	public SeekBar getSeekBar() {
		return seekbar;
	}

	public TextView getprocess() {
		return tv_screendistance;
	}

	public Button getBtnFemaleMale() {
		return btnFemale;
	}

	public Button getBtnCancel() {
		return btnCancel;
	}

	public Button getBtnSure() {
		return btnSure;
	}

	public Button getBtnMale() {
		return btnMale;
	}

	public String getSex() {
		return chaseInfoPref.sex;
	}

	public int getDistance() {
		return chaseInfoPref.distance;
	}

}
