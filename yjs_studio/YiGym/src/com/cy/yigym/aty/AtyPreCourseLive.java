package com.cy.yigym.aty;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.fragment.live.FragVideoBase;
import com.cy.yigym.view.content.live.LiveSportDatas;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-10-29
 * </p>
 * <p>
 * 往期课程直播
 * </p>
 */
public class AtyPreCourseLive extends BaseFragmentActivity {
	private FragVideoBase fragVideo;
	private static final String INTENT_KEY_VIDEO_URL = "videoUrl";
	private String videoUrl = "";

	@Override
	protected boolean isBindViewByAnnotation() {
		return false;
	}
	@BindView
	private LiveSportDatas sportData;
	@Override
	protected int getContentViewId() {
		return R.layout.aty_pre_course_live;
	}

	@Override
	protected void initView() {
		fragVideo = (FragVideoBase) mFragmentManager
				.findFragmentById(R.id.fragVideo);

	}

	@Override
	protected void initData() {
		if (getIntent() == null)
			return;
		videoUrl = getIntent().getStringExtra(INTENT_KEY_VIDEO_URL);
		if (TextUtils.isEmpty(videoUrl)) {
			WidgetUtils.showToast("该视频无法播放");
			return;
		}
		fragVideo
				.startPlay(videoUrl);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		fragVideo.stopPlay();
		sportData.fini();
	}

	/**
	 * 启动往期课程视频播放界面
	 * 
	 * @param context
	 *            发起启动的上下文
	 * @param videoUrl
	 *            视频地址
	 */
	public static void start(Context context, String videoUrl) {
		if (context == null || TextUtils.isEmpty(videoUrl))
			return;
		Intent intent = new Intent(context, AtyPreCourseLive.class);
		intent.putExtra(INTENT_KEY_VIDEO_URL, videoUrl);
		context.startActivity(intent);
	}

}
