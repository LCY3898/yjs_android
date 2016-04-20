package com.cy.yigym.aty;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.share.ShareHelper;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.entity.MeetSuccessData;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.StringUtils;
import com.cy.yigym.view.content.DlgMeetSuccShare;
import com.cy.yigym.view.content.DlgmeetSucc;
import com.efit.sport.R;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * Created by eijianshen on 15/7/29.
 */
public class AtyMeetSucc extends BaseFragmentActivity {

	@BindView
	private CustomTitleView ctvMeetSuccTitle;

	@BindView
	private CustomCircleImageView cuHeadview1;

	@BindView
	private CustomCircleImageView cuHeadview2;

	@BindView
	private TextView meTvDistance;

	@BindView
	private TextView meTMins;

	@BindView
	private TextView meTCalorie;

	@BindView
	private TextView tvMeetText;

	@BindView
	private CustomCircleImageView meetSuccMyID;

	@BindView
	private CustomCircleImageView meetSuccHerID;

	@BindView
	private TextView taTvDistance;

	@BindView
	private TextView taTMins;

	@BindView
	private TextView taTCalorie;

	@BindView
	private LinearLayout llToContact;

	@BindView
	private RelativeLayout rlMeetAll;

	private DlgmeetSucc dlgmeetSucc;
	private String herId="";
	private String myId="";
	private String hernickName="";

	private String content = String.format("跑了%d公里，耗时%.1f分钟，消耗了%d卡路里", 10,
			30.0, 20);
	private String receiverId = "", receiverNickName = "";

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_meet_succ;
	}

	@Override
	protected void initView() {
		ctvMeetSuccTitle.setTitle("去追TA");
		ctvMeetSuccTitle.setTitleViewBackGroundColor(Color.rgb(5, 188, 225));
		ctvMeetSuccTitle.setTxtLeftText("    ");
		ctvMeetSuccTitle.setTxtLeftIcon(R.drawable.header_back);
		ctvMeetSuccTitle.setTxtLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				mContext.getResources().getDisplayMetrics().heightPixels/2);
		rlMeetAll.setLayoutParams(params);
		llToContact.setOnClickListener(connectClickListener);
		dlgmeetSucc=new DlgmeetSucc(mActivity);
	}

	@Override
	protected void initData() {
		MeetSuccessData meetSuccessData = (MeetSuccessData) getIntent()
				.getSerializableExtra(MeetSuccessData.INTENT_KEY);

		herId=meetSuccessData.herId;
		myId=meetSuccessData.myId;
		hernickName=meetSuccessData.nickname;
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(meetSuccessData.myId),
				cuHeadview1);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(meetSuccessData.herId),
				cuHeadview2);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(meetSuccessData.myId),
				meetSuccMyID);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(meetSuccessData.herId),
				meetSuccHerID);
		receiverId = meetSuccessData.herId;
		receiverNickName = meetSuccessData.nickname;
		content = String.format("跑了%.1f公里，耗时%.1f分钟，消耗了%d卡路里",
				meetSuccessData.distance / 1000.0,
				meetSuccessData.timeInSecs / 60.0,
				(int) meetSuccessData.calorie);
		SpannableStringBuilder sbMyDintance=StringUtils.creSpanString(new String[]{
				String.format("%.2f", meetSuccessData.distance / 1000.0),"km"}
				,new int[]{0xffffffff,0xffffffff},new int[]{17,10});
		meTvDistance.setText(sbMyDintance);

		SpannableStringBuilder sbMyTMins=StringUtils.creSpanString(new String[]{
				String.format("%.1f", meetSuccessData.timeInSecs / 60.0),"min"}
				,new int[]{0xffffffff,0xffffffff},new int[]{17,10});
		meTMins.setText(sbMyTMins);

		SpannableStringBuilder sbMyTCalorie=StringUtils.creSpanString(new String[]{
				String
						.format("%d", (int) meetSuccessData.calorie),"cal"}
				,new int[]{0xffffffff,0xffffffff},new int[]{17,10});
		meTCalorie.setText(sbMyTCalorie);

		SpannableStringBuilder sbTaTvDistance=StringUtils.creSpanString(new String[]{
				String.format("%.2f",
						meetSuccessData.otherDistance / 1000.0),"km"}
				,new int[]{0xffffffff,0xffffffff},new int[]{17,10});
		taTvDistance.setText(sbTaTvDistance);

		SpannableStringBuilder sbTaTMins=StringUtils.creSpanString(new String[]{
				String.format("%.1f", meetSuccessData.otherTimeInSecs / 60.0),"min"}
				,new int[]{0xffffffff,0xffffffff},new int[]{17,10});
		taTMins.setText(sbTaTMins);

		SpannableStringBuilder sbTaTCalorie=StringUtils.creSpanString(new String[]{
				String.format("%d", (int) meetSuccessData.otherCalorie),"cal"}
				,new int[]{0xffffffff,0xffffffff},new int[]{17,10});
		taTCalorie.setText(sbTaTCalorie);

		tvMeetText.setText(String.format("您和%s会面成功", meetSuccessData.nickname));
		Log.i("会面成功", "距离：" + meetSuccessData.timeInSecs);
		String content="哈哈，我在E健身健康生活成功追上了"+meetSuccessData.nickname;

		SpannableStringBuilder myDis=StringUtils.creSpanString(new String[]{
				String.format("%.2f", meetSuccessData.distance / 1000.0),"km"}
				,new int[]{0xff05bbe2,0xff05bbe2},new int[]{17,12});
		meTvDistance.setText(sbMyDintance);

		SpannableStringBuilder myTime=StringUtils.creSpanString(new String[]{
				String.format("%.1f", meetSuccessData.timeInSecs / 60.0),"min"}
				,new int[]{0xff05bbe2,0xff05bbe2},new int[]{17,12});
		meTMins.setText(sbMyTMins);

		SpannableStringBuilder myCal=StringUtils.creSpanString(new String[]{
				String
						.format("%d", (int) meetSuccessData.calorie),"cal"}
				,new int[]{0xff05bbe2,0xff05bbe2},new int[]{17,12});
		setShareData(content,tvMeetText.getText().toString().trim(),myDis,myCal,myTime);
	}

	private View.OnClickListener connectClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if(view==llToContact){
				WidgetUtils.showToast("点击进入IM");//o14410071809656069278
				Log.i("ssssssss",DataStorageUtils.getOtherPid()+"----"+hernickName+"--------------"+DataStorageUtils.getPid());
				AtyIM.luanchIM(mActivity, DataStorageUtils.getOtherPid(), hernickName);
			}
		}
	};

	private void setShareData(String content,String meetPoint,SpannableStringBuilder myDis
			,SpannableStringBuilder myCal,SpannableStringBuilder myTime) {
		String title = "分享";
		String imgUrl = "http://img0w.pconline.com.cn/pconline/1309/11/3464151_10.jpg";
		String url = "http://mp.weixin.qq.com/s?__biz=MzI2MDAxODY3Mg==&mid=218040101&idx=1&sn=2396374941dda10297d0b61326c8456e#rd";
		dlgmeetSucc.getShareMeetData().setViewToShare(
				AtyMeetSucc.this.findViewById(R.id.rlMeet));
		dlgmeetSucc.getShareMeetData().setShareData(title, content,
				null, null);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(myId),
				dlgmeetSucc.getMyHead());
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(herId),
				dlgmeetSucc.getReceiverHead());
		dlgmeetSucc.getTvMeetPoint().setText(meetPoint);
		dlgmeetSucc.getTvMeetMyDis().setText(myDis);
		dlgmeetSucc.getTvMeetMyCal().setText(myCal);
		dlgmeetSucc.getTvMeetMyTime().setText(myTime);
		dlgmeetSucc.getLlSendMsg().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AtyIM.luanchIM(mActivity,DataStorageUtils.getOtherPid(), hernickName);
			}
		});
		dlgmeetSucc.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 根据requestCode获取对应的SsoHandler
		UMSocialService service = ShareHelper.getInstance().getShareService();
		if (service != null) {
			UMSsoHandler ssoHandler = service.getConfig().getSsoHandler(
					requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}

	}
}
