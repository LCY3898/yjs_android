package com.cy.yigym.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.utils.BgDrawableUtils;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.aty.PopupWindows;
import com.cy.yigym.aty.AtyMyMedal.MedalType;
import com.cy.yigym.db.dao.GrowthRecordDao;
import com.cy.yigym.entity.GrowthRecordBean;
import com.cy.yigym.net.rsp.RspGrowthTravel.GrowthRecord;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.PhotoUtils;
import com.cy.yigym.view.content.DlgAddPhoto;
import com.cy.yigym.view.content.DlgGrowTravelShare;
import com.efit.sport.R;
import com.hhtech.utils.DimenUtils;
import com.sport.efit.theme.ColorTheme;
import com.zxing.qrcode.encode.QRCodeEncoder;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-28
 * </p>
 * <p>
 * 成长旅程列表适配器
 * </p>
 */
public class GrowthTravelListAdapter extends BaseExpandableListAdapter {
	private PopupWindows popupWindows;
	private DlgAddPhoto dlgAddPhoto;
	private Context context;
	private Activity mActivity;
	private LayoutInflater inflater;
	private DlgGrowTravelShare dlgGrowTravelShare;
	private LinkedHashMap<String, ArrayList<GrowthRecord>> grows = new LinkedHashMap<String, ArrayList<GrowthRecord>>();

	private GrowthRecordDao recordDao;

	public GrowthTravelListAdapter(Context context,
			LinkedHashMap<String, ArrayList<GrowthRecord>> grows) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.grows.clear();
		this.grows.putAll(grows);
		mActivity = (Activity) context;
		// dlgAddPhoto = new DlgAddPhoto(context);
		popupWindows = new PopupWindows((Activity) context, itemsOnClick);
		dlgGrowTravelShare = new DlgGrowTravelShare(context);
		recordDao = new GrowthRecordDao();
	}

	@Override
	public int getGroupCount() {
		return grows.keySet().size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		try {
			return grows.get(getKeyByPosition(groupPosition)).size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		try {
			return grows.get(getKeyByPosition(groupPosition));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<GrowthRecord>();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		try {
			return grows.get(getKeyByPosition(groupPosition));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new GrowthRecord();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View group = inflater.inflate(R.layout.item_grow_year, parent, false);
		TextView tvYear = (TextView) group.findViewById(R.id.tvYear);
		tvYear.setText(getKeyByPosition(groupPosition).toString());
		RelativeLayout rlHalfLine = (RelativeLayout) group
				.findViewById(R.id.rlHalfLine);
		RelativeLayout rlFullLine = (RelativeLayout) group
				.findViewById(R.id.rlFullLine);
		rlHalfLine.setVisibility(groupPosition == 0 ? View.VISIBLE : View.GONE);
		rlFullLine.setVisibility(groupPosition == 0 ? View.GONE : View.VISIBLE);
		return group;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		GrowthRecord entity = null;
		try {
			entity = grows.get(getKeyByPosition(groupPosition)).get(
					childPosition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getItemView(entity);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@SuppressWarnings("deprecation")
	private View getItemView(final GrowthRecord entity) {
		View convertView = inflater.inflate(R.layout.item_growth_travel, null);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
		TextView tvItem = (TextView) convertView.findViewById(R.id.tvItem);
		ImageView ivView = (ImageView) convertView.findViewById(R.id.ivView);
		final Button btnAddPic = (Button) convertView
				.findViewById(R.id.btnAddPic);
		final LinearLayout llPhotoShow = (LinearLayout) convertView
				.findViewById(R.id.llPhotoShow);
		ImageView ivImageView = (ImageView) convertView
				.findViewById(R.id.ivImageView);
		btnAddPic.setBackgroundDrawable(ColorTheme.getAddPhotoBtnSelector());

		if (entity != null) {
			tvTime.setText(getDateString(entity.finished_time * 1000));
			tvItem.setText(entity.medal_meaning);
			MedalType medalType = MedalType.match(entity.medal_type);
			if (medalType == null)
				medalType = MedalType.OTHER;
			ivView.setBackgroundResource(medalType.thumbImgId);
			ImageLoaderUtils.getInstance().loadImage(
					DataStorageUtils.getHeadDownloadUrl(entity.medal_fid),
					((ImageView) convertView.findViewById(R.id.ivImageView)));
			btnAddPic.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DataStorageUtils.setMedalNum(entity.medal_number);
					popupWindows.showAtLocation(
							mActivity.findViewById(R.id.llPersonal),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			});
			if ("".equals(entity.share_fid)) {
				btnAddPic.setVisibility(View.VISIBLE);
				llPhotoShow.setVisibility(View.GONE);
			} else {
				llPhotoShow.setVisibility(View.VISIBLE);
				btnAddPic.setVisibility(View.GONE);
				ImageLoaderUtils.getInstance().loadImage(
						DataStorageUtils.getHeadDownloadUrl(entity.share_fid),
						ivImageView);
			}
			ivImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dlgAddPhoto = new DlgAddPhoto(context);

					DataStorageUtils
							.setMedalNumForEditText(entity.medal_number);

					GrowthRecordBean bean = recordDao
							.getByMedalNumber(entity.medal_number);
					if (bean != null) {
						dlgAddPhoto.setDescTxt(bean.getSignContent());
					}

					ImageLoaderUtils.getInstance().loadImage(
							DataStorageUtils
									.getHeadDownloadUrl(entity.share_fid),
							dlgAddPhoto.getIvPhoto());
					dlgAddPhoto.getBtnDeletePhoto().setOnClickListener(
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									dlgAddPhoto.dismiss();
									llPhotoShow.setVisibility(View.GONE);
									btnAddPic.setVisibility(View.VISIBLE);
								}
							});
					dlgAddPhoto.getBtnSureAddPthoto().setOnClickListener(
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									dlgAddPhoto.dismiss();
								}
							});
					dlgAddPhoto.show();
				}
			});
			ImageView ivToShare = (ImageView) convertView
					.findViewById(R.id.ivToShare);
			ivToShare.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setShareData(getShareView(entity.share_fid,
							DataStorageUtils.getCurUserProfileFid(),
							entity.medal_meaning));
				}
			});
		}
		return convertView;
	}

	/**
	 * 根据位置获取key，LinkedHashMap为有序的map，所以可以这么做
	 * 
	 * @param position
	 * @return
	 */
	private Object getKeyByPosition(int position) {
		if (grows.size() == 0)
			return "";
		int count = 0;
		for (String key : grows.keySet()) {
			if (count == position)
				return key;
			count++;
		}
		return "";
	}

	/**
	 * 统一日期格式为两位数显示
	 * 
	 * @param calendar
	 * @return
	 */
	private String getDateString(long finishTime) {
		String date = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
			date = sdf.format(new Date(finishTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 设置调用系统摄像头和本地相册的监听事件
	 */
	private View.OnClickListener itemsOnClick = new View.OnClickListener() {

		public void onClick(View v) {
			popupWindows.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				// 系统相机拍照
				PhotoUtils.takePhotoBySystem(context, null);
				break;
			case R.id.btn_pick_photo:
				// 相册选择
				PhotoUtils.pickPhotoFormGallery(context, null);
				break;
			default:
				break;
			}
		}

	};

	private ImageView ivSharePhoto;
	private CustomCircleImageView ccivHeader;
	private TextView tvShareText;
	private ImageView ivCode;
	private LinearLayout llMySelf;
	private RelativeLayout rlGrowTravelToShare;

	/**
	 * 获取分享图片的view
	 * 
	 * @param photoFid
	 * @param fid
	 * @param text
	 * @return
	 */
	private View getShareView(String photoFid, String fid, String content) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_grow_travel_to_share, null);
		ivSharePhoto = (ImageView) view.findViewById(R.id.ivSharePhoto);
		int width = WidgetUtils.getScreenWidth() - DimenUtils.dpToPx(80);
		int height = WidgetUtils.getScreenHeight() * 3 / 4;
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(photoFid), ivSharePhoto,
				width, height);
		ccivHeader = (CustomCircleImageView) view.findViewById(R.id.ccivHeader);
		ImageLoaderUtils.getInstance().loadImage(
				DataStorageUtils.getHeadDownloadUrl(fid), ccivHeader);
		tvShareText = (TextView) view.findViewById(R.id.tvShareText);
		tvShareText.setText(content);
		llMySelf = (LinearLayout) view.findViewById(R.id.llMySelf);
		rlGrowTravelToShare = (RelativeLayout) view
				.findViewById(R.id.rlGrowTravelToShare);
		llMySelf.setBackground(BgDrawableUtils.creShape(0xffffffff,
				new float[] { 0, 0, 0, 0, 5, 5, 5, 5 }));
		view.setDrawingCacheEnabled(true);
		String url = "http://www.pgyer.com/jlQm";
		ivCode = (ImageView) view.findViewById(R.id.ivCode);
		ivCode.setImageBitmap(QRCodeEncoder.encodeAsBitmap(url,
				DimenUtils.dpToPx(70), DimenUtils.dpToPx(70)));
		return view;
	}

	/**
	 * 设置分享数据
	 */

	private void setShareData(View view) {
		String title = "E健身健康生活";
		dlgGrowTravelShare.getSvGrowTravel().setViewToShare(view);
		dlgGrowTravelShare.getSvGrowTravel().setShareData(title, null, null,
				null);
		dlgGrowTravelShare.show();
	}

}
