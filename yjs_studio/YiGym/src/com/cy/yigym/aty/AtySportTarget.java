package com.cy.yigym.aty;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.req.ReqGetExerciseTarget;
import com.cy.yigym.net.rsp.RspGetExerciseTarget;
import com.cy.yigym.net.rsp.RspGetExerciseTarget.DataEntity;
import com.cy.yigym.view.ThumbTextSeekbar;
import com.efit.sport.R;

/**
 * Created by ejianshen on 15/11/10.
 */
public class AtySportTarget extends BaseFragmentActivity {
	@BindView
	private CustomTitleView vTitle;
	@BindView
	private ThumbTextSeekbar sbTotalCalorie;
	@BindView
	private ThumbTextSeekbar sbTotalDistance;
	@BindView
	private ThumbTextSeekbar sbDlyDistance;
	@BindView
	private ThumbTextSeekbar sbDlyTotalCal;
	@BindView
	private TextView tvNextTotalCalorie;
	@BindView
	private TextView tvNextTotalDistance;
	@BindView
	private TextView tvNextDlyDistance;
	@BindView
	private TextView tvNextDailyTotalCal;
	private DecimalFormat df00 = new DecimalFormat("#0.00");
	private final String FORMAT_KM = "%.1fkm";
	private final String FORMAT_CAL = "%dcal";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_sport_target;
	}

	@Override
	protected void initView() {
		vTitle.setTitle("运动目标");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.header_back);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}

	@Override
	protected void initData() {
		YJSNet.getExerciseTarget(new ReqGetExerciseTarget(), LOG_TAG,
				new OnRespondCallBack<RspGetExerciseTarget>() {

					@Override
					public void onSuccess(RspGetExerciseTarget data) {
						DataEntity d = data.data;
						showExerciseData(d);

					}

					@Override
					public void onFailure(String errorMsg) {
						WidgetUtils.showToast(errorMsg);
					}
				});
	}

	/**
	 * 显示运动数据
	 * 
	 * @param d
	 */
	private void showExerciseData(DataEntity d) {
		if (d != null) {
			showContent(sbTotalCalorie, FORMAT_CAL, d.current_total_calorie,
					d.current_target_total_calorie);
			tvNextTotalCalorie.setText(d.next_target_total_calorie + "cal");
			showContent(sbTotalDistance, FORMAT_KM, d.current_total_distance,
					d.current_target_total_distance);
			tvNextTotalDistance.setText(df00
					.format(d.next_target_total_distance / 1000.0) + "km");
			showContent(sbDlyDistance, FORMAT_KM,
					d.current_day_farest_distance,
					d.current_target_day_farest_distance);
			tvNextDlyDistance.setText(df00
					.format(d.next_target_day_farest_distance / 1000.0) + "km");
			showContent(sbDlyTotalCal, FORMAT_CAL, d.current_day_most_calorie,
					d.current_target_day_most_calorie);
			tvNextDailyTotalCal.setText(d.next_target_day_most_calorie + "cal");

		}

	}

	private void showContent(final ThumbTextSeekbar sb, final String format,
			int progress, int max) {
		progress = progress < 0 ? 0 : progress;
		showContentInternal(sb, format, progress, max);

	}

	/**
	 * 显示内容
	 * 
	 * @param sb
	 * @param format
	 * @param progress
	 * @param max
	 */
	private void showContentInternal(final ThumbTextSeekbar sb,
			final String format, final int progress, int max) {
		if (format.equals(FORMAT_CAL)) {
			sb.tvTotal.setText((String.format(format, (int) max)));
		} else if (format.equals(FORMAT_KM)) {
			sb.tvTotal.setText(String.format(format, max / 1000.0));
		}

		sb.seekBar.setMax(max);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int p,
					boolean fromUser) {
				if (format.equals(FORMAT_CAL)) {
					// sb.setThumbText(String.format(format, progress));
					sb.tvCur.setText(progress + "/");
				} else if (format.equals(FORMAT_KM)) {
					// sb.setThumbText(String.format(format, progress /
					// 1000.0));
					sb.tvCur.setText(String.format("%.1f/", progress / 1000.0));
				}
			}
		});
		sb.setProgress(progress);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}
}
