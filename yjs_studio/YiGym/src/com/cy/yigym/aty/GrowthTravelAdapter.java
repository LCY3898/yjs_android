package com.cy.yigym.aty;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.db.dao.GrowthRecordDao;
import com.cy.yigym.entity.GrowthRecordBean;
import com.cy.yigym.net.rsp.RspGrowthTravel;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.PhotoUtils;
import com.cy.yigym.view.content.DlgAddPhoto;
import com.efit.sport.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by xiaoshu on 15/11/13.
 */
public class GrowthTravelAdapter extends AdapterBase<RspGrowthTravel.GrowthRecord> {

    private PopupWindows popupWindows;
    private Activity mActivity;
    private DlgAddPhoto dlgAddPhoto;
    private GrowthRecordDao recordDao;

    public GrowthTravelAdapter(Activity activity, List<RspGrowthTravel.GrowthRecord> list) {
        super(activity, list);
        this.mActivity = activity;
        recordDao = new GrowthRecordDao();
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent, final RspGrowthTravel.GrowthRecord entity) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_growth_travel, null);
        }
        TextView tvTime = (TextView) getHolderView(convertView, R.id.tvTime);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(entity.finished_time * 1000));
        tvTime.setText(getDateString(calendar));
        TextView tvItem = (TextView) getHolderView(convertView, R.id.tvItem);
        tvItem.setText(entity.medal_meaning);
        final Button btnAddPic = (Button) convertView.findViewById(R.id.btnAddPic);
        btnAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStorageUtils.setMedalNum(entity.medal_number);
                popupWindows = new PopupWindows(mActivity, itemsOnClick);
                popupWindows.showAtLocation(mActivity.findViewById(R.id.llPersonal),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        ImageLoaderUtils.getInstance().loadImage(DataStorageUtils.getHeadDownloadUrl(entity.medal_fid)
                , ((ImageView) getHolderView(convertView, R.id.ivImageView)));
        final LinearLayout llPhotoShow = getHolderView(convertView, R.id.llPhotoShow);
        ImageView ivImageView = (ImageView) getHolderView(convertView, R.id.ivImageView);
        if ("".equals(entity.share_fid)) {
            btnAddPic.setVisibility(View.VISIBLE);
            llPhotoShow.setVisibility(View.GONE);
        } else {
            llPhotoShow.setVisibility(View.VISIBLE);
            btnAddPic.setVisibility(View.GONE);
            ImageLoaderUtils.getInstance().loadImage(DataStorageUtils.getHeadDownloadUrl(entity.share_fid)
                    , ivImageView);
        }
        //dlgAddPhoto = new DlgAddPhoto(mContext);

        DataStorageUtils.setEditContent(String.valueOf(entity.medal_number));
        ivImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageLoaderUtils.getInstance().loadImage(DataStorageUtils.getHeadDownloadUrl(entity.share_fid)
                        , dlgAddPhoto.getIvPhoto());
                dlgAddPhoto.getBtnDeletePhoto().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlgAddPhoto.dismiss();
                        llPhotoShow.setVisibility(View.GONE);
                        btnAddPic.setVisibility(View.VISIBLE);
                    }
                });
                dlgAddPhoto.getBtnSureAddPthoto().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlgAddPhoto.dismiss();
                    }
                });
                GrowthRecordBean bean = recordDao.getByMedalNumber(entity.medal_number);
                if (bean != null) {
                    dlgAddPhoto.setDescTxt(bean.getSignContent());
                }

                dlgAddPhoto.show();
            }
        });
        ImageView ivToShare = getHolderView(convertView, R.id.ivToShare);
        return convertView;
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
                    PhotoUtils.takePhotoBySystem(mContext, null);
                    break;
                case R.id.btn_pick_photo:
                    // 相册选择
                    PhotoUtils.pickPhotoFormGallery(mContext, null);
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 统一日期格式为两位数显示
     *
     * @param calendar
     * @return
     */
    private String getDateString(Calendar calendar) {
        String date;
        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
            if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
                date = "0" + (calendar.get(Calendar.MONTH) + 1)
                        + "月0" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
            } else {
                date = "0" + (calendar.get(Calendar.MONTH) + 1)
                        + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
            }
        } else {
            if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
                date = (calendar.get(Calendar.MONTH) + 1)
                        + "月0" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
            } else {
                date = (calendar.get(Calendar.MONTH) + 1)
                        + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
            }
        }
        return date;
    }
}
