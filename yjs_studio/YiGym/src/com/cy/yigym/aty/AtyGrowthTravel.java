package com.cy.yigym.aty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cy.imagelib.ImageCompressUtils;
import com.cy.imagelib.ImageLoaderUtils;
import com.cy.share.ShareHelper;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.adapter.GrowthTravelListAdapter;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqGrowthTravel;
import com.cy.yigym.net.rsp.RspGrowthTravel;
import com.cy.yigym.net.rsp.RspGrowthTravel.GrowthRecord;
import com.cy.yigym.net.rsp.RspUploadPhoto;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.PhotoUtils;
import com.cy.yigym.view.content.DlgAddPhoto;
import com.efit.sport.R;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshScrollView;
import com.hhtech.utils.BitmapUtils;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by xiaoshu on 15/11/11.
 */
public class AtyGrowthTravel extends BaseFragmentActivity implements
		View.OnClickListener, PullToRefreshBase.OnRefreshListener2<ScrollView> {

	@BindView
	private CustomTitleView vTitle;
	@BindView
	private TextView tvNickname;
	@BindView
	private TextView tvTag;
	@BindView
	private Button btnToMedal;
	@BindView
	private PullToRefreshScrollView ptrfsvGrow;
	@BindView
	private LinearLayout llPersonal;
	@BindView
	private CustomCircleImageView ivHeader;
	private DlgAddPhoto dlgAddPhoto;
	private boolean isCanUp = true;// 判断列表是否能够上拉
	@BindView
	private ExpandableListView elvGrowList;
	private LinkedHashMap<String, ArrayList<GrowthRecord>> listGrow = new LinkedHashMap<String, ArrayList<GrowthRecord>>();
	private GrowthTravelListAdapter growthTravelListAdapter;

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_growth_travel;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initView() {
		vTitle.setTitle("成长旅程");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.header_back);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		btnToMedal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(AtyMyMedal.class);
			}
		});
		btnToMedal.setOnClickListener(this);
		dlgAddPhoto = new DlgAddPhoto(mContext);
		btnToMedal.setBackgroundDrawable(BgDrawableUtils.crePressSelector(
				BgDrawableUtils.creShape(0xffa0aab6, 5),
				BgDrawableUtils.creShape(0xff8793a1, 5)));
	}

	@Override
	protected void initData() {

		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(DataStorageUtils
						.getCurUserProfileFid()), ivHeader);
		tvNickname.setText(DataStorageUtils.getUserNickName());
		ptrfsvGrow.setOnRefreshListener(this);
		ptrfsvGrow.setMode(PullToRefreshBase.Mode.BOTH);
		getGrowthTravel();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnToMedal:
			startActivity(new Intent(mContext, AtyMyMedal.class));
			break;
		default:
			break;
		}
	}

	public void refreshCompelte() {
		ptrfsvGrow.onRefreshComplete();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		if (isCanUp) {
			getGrowthTravel();
		} else {
			refreshView.postDelayed(new Runnable() {
				@Override
				public void run() {
					refreshCompelte();
				}
			}, 1000);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		getGrowthTravel();
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PhotoUtils.RequestCode.TAKE_PHOTO_BY_SYSTEM
				|| requestCode == PhotoUtils.RequestCode.PICK_PHOTO_FROM_GALLERY
				|| requestCode == PhotoUtils.RequestCode.CROP_PHOTO_BY_SYSTEM) {

			PhotoUtils.setOnPhotoResultListener(mContext, requestCode,
					resultCode, data, new PhotoUtils.OnPhotoResultListener() {
						@Override
						public void onPhotoResult(String photoPath, Bitmap photo) {
							if (photo != null && photo.isRecycled()) {
								photo.recycle();
							}
							switch (requestCode) {
							case PhotoUtils.RequestCode.TAKE_PHOTO_BY_SYSTEM:
								addPhoto(photoPath);
								//PhotoUtils.cropPhotoBySystem(mActivity, null,
										//photoPath, DimenUtils.getDisplayWidth() *2/ 3 , DimenUtils.getDisplayHeight() * 2 /3);
								break;
							case PhotoUtils.RequestCode.PICK_PHOTO_FROM_GALLERY:
								addPhoto(photoPath);
								//PhotoUtils.cropPhotoBySystem(mActivity, null,
										//photoPath, DimenUtils.getDisplayWidth() *2/ 3 , DimenUtils.getDisplayHeight() * 2 /3);
								break;
							case PhotoUtils.RequestCode.CROP_PHOTO_BY_SYSTEM:
								addPhoto(photoPath);
								break;
							}
						}
					});
		} else {
			// 根据requestCode获取对应的SsoHandler
			UMSocialService service = ShareHelper.getInstance()
					.getShareService();
			if (service != null) {
				UMSsoHandler ssoHandler = service.getConfig().getSsoHandler(
						requestCode);
				if (ssoHandler != null) {
					ssoHandler.authorizeCallBack(requestCode, resultCode, data);
				}
			}
		}
	}

	private void addPhoto(final String photoPath) {
		if (TextUtils.isEmpty(photoPath)) {
			return;
		}

		try {

			Bitmap bitmap = ImageCompressUtils.compress(photoPath,
					WidgetUtils.getScreenWidth(), WidgetUtils.getScreenHeight());
			String currSec = String.valueOf(System.currentTimeMillis() / 1000);
			final String newPath = BitmapUtils.getTmpBmpPath() + File.separator + currSec + ".jpg";
			BitmapUtils.saveBitmap(bitmap, newPath);
			try {
				Thread.sleep(100);//flush file may need some time
			}catch(InterruptedException e) {}

			dlgAddPhoto.show();
			dlgAddPhoto.getIvPhoto().setImageBitmap(bitmap);
			dlgAddPhoto.getBtnDeletePhoto().setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dlgAddPhoto.dismiss();
						}
					});
			dlgAddPhoto.getBtnSureAddPthoto().setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// 上传图片
							YJSNet.uploadPhoto(newPath,
									new YJSNet.OnRespondCallBack<RspUploadPhoto>() {
										@Override
										public void onSuccess(RspUploadPhoto data) {
											reLoadGrowthTravel(
													DataStorageUtils.getMedalNum(),
													data.fid);
										}

										@Override
										public void onFailure(String errorMsg) {
											refreshCompelte();
										}
									});
							dlgAddPhoto.dismiss();
						}
					});
		} catch(Exception e) {
			e.printStackTrace();
			WidgetUtils.showToast("添加图片失败");
		}
	}

	/**
	 * 首次加载成长旅程数据列表
	 */
	private void getGrowthTravel() {
		YJSNet.send(new ReqGrowthTravel(), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGrowthTravel>() {
					@Override
					public void onSuccess(RspGrowthTravel data) {
						if (data.data == null
								|| data.data.medal_list.size() == 0) {
							isCanUp = false;
							return;
						}
						tvTag.setText("来e健身第"
								+ (data.data.delta_sec / (24 * 3600) + 1)
								+ "天，共获得" + data.data.medal_list.size() + "块勋章");
						parseGrowList(data.data.medal_list);
						refreshCompelte();
						parseGrowList(data.data.medal_list);

					}

					@Override
					public void onFailure(String errorMsg) {
						refreshCompelte();
					}
				});
	}

	/**
	 * 图片上传成功后重新加载数据
	 * 
	 * @param medalNum
	 * @param photoFid
	 */
	private void reLoadGrowthTravel(int medalNum, String photoFid) {
		YJSNet.send(new ReqGrowthTravel(medalNum, photoFid), LOG_TAG,
				new YJSNet.OnRespondCallBack<RspGrowthTravel>() {
					@Override
					public void onSuccess(RspGrowthTravel data) {
						if (data.data == null
								|| data.data.medal_list.size() == 0) {
							isCanUp = false;
							return;
						}
						parseGrowList(data.data.medal_list);
						refreshCompelte();
					}

					@Override
					public void onFailure(String errorMsg) {
						refreshCompelte();
					}
				});
	}

	/**
	 * 解析成长列表，按照年份进行分类
	 * 
	 * @param medals
	 */
	@SuppressLint("SimpleDateFormat")
	private void parseGrowList(ArrayList<GrowthRecord> medals) {
		listGrow.clear();
		if (medals == null || medals.size() == 0)
			return;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		for (GrowthRecord medal : medals) {
			String year = "";
			try {
				year = sdf.format(new Date(medal.finished_time * 1000));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (TextUtils.isEmpty(year))
				continue;
			if (listGrow.containsKey(year)) {
				listGrow.get(year).add(medal);
			} else {
				ArrayList<GrowthRecord> ms = new ArrayList<GrowthRecord>();
				ms.add(medal);
				listGrow.put(year, ms);
			}

		}
		growthTravelListAdapter = new GrowthTravelListAdapter(mActivity,
				listGrow);
		elvGrowList.setAdapter(growthTravelListAdapter);
		growthTravelListAdapter.notifyDataSetChanged();

	}

}
