package com.cy.yigym.adapter;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.net.rsp.RspPreCourse.PreCourse;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Caiyuan Huang
 * <p>
 * 2015-10-21
 * </p>
 * <p>
 * 往期课程列表
 * </p>
 */
public class PreCourseAdapter extends AdapterBase<PreCourse> {
	private RelativeLayout.LayoutParams params;
	private DisplayImageOptions options;

	public PreCourseAdapter(Context context, List<PreCourse> list) {
		super(context, list);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources()
						.getDisplayMetrics().heightPixels / 3);
		options = ImageLoaderUtils.getInstance().createDisplayOptions(
				R.drawable.loading_show, R.drawable.loading_show, R.drawable.loading_show);

	}

	@Override
	protected View getItemView(int position, View convertView,
			ViewGroup parent, PreCourse entity) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_pre_course_listitem,
					null);
		}
		ImageView imgCover = getHolderView(convertView, R.id.preCourseImage);
		TextView txtName = getHolderView(convertView, R.id.courseTitle);
		TextView txtCoach = getHolderView(convertView, R.id.courseCoach);
		TextView txtWatchNum = getHolderView(convertView, R.id.watchCourseNum);
		imgCover.setLayoutParams(params);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(entity.course_fid),
				imgCover, 0, 0, -1, options);
		txtName.setText(entity.course_name);
		txtCoach.setText(entity.coach_name);
		txtWatchNum.setText(entity.current_num + "人观看了此视频");
		return convertView;
	}

}
