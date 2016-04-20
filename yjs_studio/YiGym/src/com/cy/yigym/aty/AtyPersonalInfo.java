package com.cy.yigym.aty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cy.wheelview.AbstractWheel;
import com.cy.wheelview.HoriWheelView;
import com.cy.wheelview.OnWheelChangedListener;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqAccomplishInfo;
import com.cy.yigym.net.req.ReqUpdatePersonInfo;
import com.cy.yigym.net.rsp.RspAccomplishInfo;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.net.rsp.RspUpdatePersonInfo;
import com.cy.yigym.net.rsp.RspUploadPhoto;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;
import com.cy.yigym.utils.PhotoUtils;
import com.efit.sport.R;

import java.util.ArrayList;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-9
 * </p>
 * <p>
 * 完善个人信息界面
 * </p>
 */
public class AtyPersonalInfo extends BaseFragmentActivity implements
		View.OnClickListener {
	@BindView
	private EditText et_nickname;
	//性别单选框
	@BindView
	private RadioGroup rg_sex;
	@BindView
	private RadioButton chbMan, chbWoman;
	@BindView
	private HoriWheelView wheelYearOld, wheelHeight, wheelWeight;
	@BindView
	private CustomTitleView vTitle;

	@BindView
	private CustomCircleImageView avatar;

	private String height,weight,yearOld,sex,nickname;
	private RspGetUserInfo userInfo;

	private PopupWindows popupWindows;

	private final static int START_AGE = 18;
	private final static int END_AGE = 100;
	private final static int START_HEIGHT=100;
	private final static int END_HEIGHT=230;
	private final static int START_WEIGHT=30;
	private final static int END_WEIGHT=300;

	private final static int PRESET_AGE = 30;
	private final static int PRESET_HEIGHT = 170;
	private final static int PRESET_WEIGHT = 60;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_personal_info;
	}

	@Override
	protected void initView() {
		setWheelViewStyle(wheelYearOld);
		setWheelViewStyle(wheelHeight);
		setWheelViewStyle(wheelWeight);

		setWheelValueRange(START_AGE, END_AGE, wheelYearOld);
		setWheelValueRange(START_HEIGHT, END_HEIGHT, wheelHeight);
		setWheelValueRange(START_WEIGHT, END_WEIGHT, wheelWeight);

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				wheelYearOld.getWheel().setCurrentItem(PRESET_AGE - START_AGE, true);
				wheelHeight.getWheel().setCurrentItem(PRESET_HEIGHT - START_HEIGHT, true);
				wheelWeight.getWheel().setCurrentItem(PRESET_WEIGHT - START_WEIGHT, true);
			}
		}, 1000);

		//获取昵称
		vTitle.setTitle("完善个人信息");
		vTitle.setTxtLeftIcon(R.drawable.header_back);
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtRightIcon(R.drawable.header_check);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				//Log.d("AtyPersonalInfo",sex+"==="+height+nickname);
			}
		});
		vTitle.setTxtRightClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nickname = et_nickname.getText().toString().trim();
				if (TextUtils.isEmpty(nickname)) {
					WidgetUtils.showToast("请填写昵称");
					return;
				}
				perfectPersonalInfo();
			}
		});

		avatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindows = new PopupWindows((Activity) mContext, itemsOnClick);
				popupWindows.showAtLocation(avatar,
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		HeaderHelper.loadSelf(avatar, R.drawable.head);
	}


	private View.OnClickListener itemsOnClick = new View.OnClickListener(){

		public void onClick(View v) {
			popupWindows.dismiss();
			switch (v.getId()) {
				case R.id.btn_take_photo:
					// 系统相机拍照
					PhotoUtils.takePhotoBySystem(mActivity, null);
					break;
				case R.id.btn_pick_photo:
					// 相册选择
					PhotoUtils.pickPhotoFormGallery(mActivity, null);
					break;
				default:
					break;
			}


		}

	};

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		//第三方登入后，第三方信息自动填入控件
		sex = "男";
		yearOld = String.valueOf(PRESET_AGE);
		height = String.valueOf(PRESET_HEIGHT);
		weight = String.valueOf(PRESET_WEIGHT);
		Intent intent=getIntent();
		int isAuthorize=intent.getIntExtra("authorize",0);
		if(isAuthorize==1){
			String nick = intent.getStringExtra("nickname");
			et_nickname.setText(nick == null?"":nick);
			sex = intent.getStringExtra("sex");
			if ("男".equals(sex)) {
				chbWoman.setTextColor(getResources().getColor(R.color.black));
				chbMan.setTextColor(getResources().getColor(R.color.blue));
				sex = "男";
				chbMan.setChecked(true);
			} else {
				chbMan.setTextColor(getResources().getColor(R.color.black));
				chbWoman.setTextColor(getResources().getColor(R.color.blue));
				chbWoman.setChecked(true);
				sex = "女";
			}
		}

		// 获取性别
		rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.chbMan) {
					chbWoman.setTextColor(getResources().getColor(R.color.black));
					chbMan.setTextColor(getResources().getColor(R.color.blue));
					sex = "男";
				} else {
					chbMan.setTextColor(getResources().getColor(R.color.black));
					chbWoman.setTextColor(getResources().getColor(R.color.blue));
					sex = "女";
				}
			}
		});
		// 获取身高
		wheelHeight.setOnWheelChangedListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				height = Integer.toString(100 + newValue);
			}
		});
		// 获取体重
		wheelWeight.setOnWheelChangedListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				weight = Integer.toString(30 + newValue);
			}
		});
		// 获取年龄
		wheelYearOld.setOnWheelChangedListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				yearOld = Integer.toString(18 + newValue);
			}
		});

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.et_nickname:
					et_nickname.setCursorVisible(true);
				break;
		}
	}

	/**
	 * 设置滚轮的值范围
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param wheel
	 */
	private void setWheelValueRange(int minValue, int maxValue,
			HoriWheelView wheel) {
		ArrayList<String> values = new ArrayList<String>();
		for (int i = minValue; i < maxValue; i++) {
			values.add(i + "");
		}
		wheel.setDatas(values);
		wheel.setCurrentItem(0);

	}

	/**
	 * 设置滚轮样式
	 * 
	 * @param wheel
	 */
	private void setWheelViewStyle(HoriWheelView wheel) {
		wheel.setTxtSizeAndColor(new int[]{12, 16, 27, 16, 12}, new int[]{
				0xffa8a8a8, 0xffa8a8a8, 0xff20C9DD, 0xffa8a8a8, 0xffa8a8a8});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}
	/**
	 * 完善个人消息
	 * Perfect personal information
	 */
	private void perfectPersonalInfo(){

		YJSNet.accomplishInfo(new ReqAccomplishInfo(nickname,
						sex, yearOld, height, weight), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspAccomplishInfo>() {
					@Override
					public void onSuccess(RspAccomplishInfo data) {
						Log.d("AtyPersonalInfo", "完善成功" + data);
						WidgetUtils.showToast("完善成功");
						DataStorageUtils.setUserNickName(nickname);
						DataStorageUtils.setUserInfoComplete(true);
						startActivity(AtyMain2.class);
						finish();
					}
					@Override
					public void onFailure(String errorMsg) {
						WidgetUtils.showToast("完善信息失败！" + errorMsg);
					}
				});
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		PhotoUtils.setOnPhotoResultListener(mActivity, requestCode, resultCode,
				data, new PhotoUtils.OnPhotoResultListener() {
					@Override
					public void onPhotoResult(String photoPath, Bitmap photo) {
						if (photo != null && photo.isRecycled()) {
							photo.recycle();
						}
						switch (requestCode) {
							case PhotoUtils.RequestCode.TAKE_PHOTO_BY_SYSTEM:
								PhotoUtils.cropPhotoBySystem(mActivity, null,
										photoPath, 300, 300);
								break;
							case PhotoUtils.RequestCode.PICK_PHOTO_FROM_GALLERY:
								PhotoUtils.cropPhotoBySystem(mActivity, null,
										photoPath, 300, 300);
								break;
							case PhotoUtils.RequestCode.CROP_PHOTO_BY_SYSTEM:
								uploadHeadImage(photoPath);
								break;

						}
					}
				});
	}


	/**
	 * 真正的头像上传操作
	 *
	 * @param fid
	 */
	private void uploadHead(final String fid) {
		if (TextUtils.isEmpty(fid)) {
			uploadHeadFailure("上传失败");
			return;
		}
		// 更新头像
		YJSNet.updatePersonInfo(new ReqUpdatePersonInfo("", fid, "", "", "",
						"", "", "", "", ""), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {

					@Override
					public void onSuccess(RspUpdatePersonInfo data) {

					}

					@Override
					public void onFailure(String errorMsg) {
						uploadHeadFailure(errorMsg);
					}
				});
	}


	private void uploadHeadFailure(String errMsg) {
		WidgetUtils.showToast(errMsg);
		HeaderHelper.loadSelf(avatar, R.drawable.head);
	}

	/**
	 * 上传头像图片
	 *
	 * @param photoPath
	 */
	private void uploadHeadImage(String photoPath) {
		if (TextUtils.isEmpty(photoPath))
			return;
		// 先展示即将要上传的照片，如果上传失败，则替换成原来的
		avatar.setImageBitmap(BitmapFactory.decodeFile(photoPath));
		// 上传照片
		YJSNet.uploadPhoto(photoPath, new YJSNet.OnRespondCallBack<RspUploadPhoto>() {
			@Override
			public void onSuccess(RspUploadPhoto data) {
				uploadHead(data.fid);
			}

			@Override
			public void onFailure(String errorMsg) {
				// 上传失败替换成原来的照片
				uploadHeadFailure(errorMsg);
			}
		});

	}
}
