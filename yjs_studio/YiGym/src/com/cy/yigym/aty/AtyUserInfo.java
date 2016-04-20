package com.cy.yigym.aty;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.content.DlgBase;
import com.cy.widgetlibrary.content.DlgEdit;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.event.EventUpdateUserInfo;
import com.cy.yigym.fragment.FragmentDatePicker;
import com.cy.yigym.fragment.FragmentEditText;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqUpdatePersonInfo;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.net.rsp.RspUpdatePersonInfo;
import com.cy.yigym.net.rsp.RspUploadPhoto;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.HeaderHelper;
import com.cy.yigym.utils.PhotoUtils;
import com.cy.yigym.view.content.EventHeadImageView;
import com.efit.sport.R;

import de.greenrobot.event.EventBus;

//完善个人资料
public class AtyUserInfo extends BaseFragmentActivity implements
        View.OnClickListener {
    public static final int REQUEST_UPDATE_SIGN = 101;

    public static final int REQUEST_UPDATE_PROF = 102;

    public static final int REQUEST_UPDATE_AVATAR = 103;

    @BindView
    // 职业
    private RelativeLayout rl_professional;
    // 签名
    @BindView
    private RelativeLayout rl_sign;
    // 昵称
    @BindView
    private TextView tv_nickname;
    // 性别
    @BindView
    private RelativeLayout rl_sex;
    // 身高
    @BindView
    private RelativeLayout rl_height;
    // 体重
    @BindView
    private RelativeLayout rl_weight;
    // 生日
    @BindView
    private RelativeLayout rl_birth;
    //	// 所在地
//	@BindView
//	private RelativeLayout rl_place;
    // 标题
    @BindView
    private CustomTitleView vTitle;
    private DlgBase dlgBase;
    @BindView
    private TextView tv_sex, tv_height, tv_weight, tv_birth, tv_job, tv_place,
            tv_sign;
    @BindView
    private ImageView ivSex;
    @BindView
    private LinearLayout lvUpdateAvatar;
    @BindView
    private EventHeadImageView iv_header;

    private PopupWindows popupWindows;

    private RspGetUserInfo.PersonInfo info;

    @Override
    protected boolean isBindViewByAnnotation() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_user_info;
    }

    @Override
    protected void initView() {

        vTitle.setTitle("我的资料");
        vTitle.setTxtLeftText("       ");
        vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
        vTitle.setTxtLeftClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        rl_professional.setOnClickListener(this);
        rl_sign.setOnClickListener(this);
        rl_birth.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_height.setOnClickListener(this);
        rl_weight.setOnClickListener(this);
        tv_nickname.setOnClickListener(this);
//		rl_place.setOnClickListener(this);
        lvUpdateAvatar.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        // RspGetUserInfo userInfo=DataStorageUtils.getUserInfo();
        info = CurrentUser.instance().getPersonInfo();
        tv_nickname.setText(info.nick_name);
        tv_sex.setText(info.sex);
        updateSexIcon(info.sex);
        tv_height.setText(info.height);
        tv_weight.setText(info.weight);
        tv_birth.setText(info.birth);
        tv_job.setText(info.job);
        //tv_place.setText(info.location);
        tv_sign.setText(info.signature);
        notifyUserInfoChange(3, info.signature);

    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lvUpdateAvatar:
                updateAvatar();
                break;
            case R.id.tv_nickname:
                updateNickname();
                break;
            case R.id.rl_sex:
                updateSex();
                break;
            case R.id.rl_height:
                updateHeight();
                break;
            case R.id.rl_weight:
                updateWeight();
                break;
            case R.id.rl_birth:
                updateBirth();
                break;
            case R.id.rl_professional:
                Intent intent1 = new Intent(this, AtyProfessional.class);
                intent1.putExtra("job", tv_job.getText().toString().trim());
                startActivityForResult(intent1, REQUEST_UPDATE_PROF);
                break;
//		case R.id.rl_place:
//			updateLocation();
//			break;
            case R.id.rl_sign:
                Intent intent = new Intent(this, AtySign.class);
                intent.putExtra("sign", tv_sign.getText().toString().trim());
                startActivityForResult(intent, REQUEST_UPDATE_SIGN);
                break;
        }
    }

    private void updateAvatar() {
        popupWindows = new PopupWindows(this, itemsOnClick);
        // 显示窗口
        popupWindows.showAtLocation(
                this.findViewById(R.id.rlSetUserInfo), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

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



    /**
     * 上传头像图片
     *
     * @param photoPath
     */
    private void uploadHeadImage(String photoPath) {
        if (TextUtils.isEmpty(photoPath))
            return;
        // 先展示即将要上传的照片，如果上传失败，则替换成原来的
        iv_header.setImageBitmap(BitmapFactory.decodeFile(photoPath));
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

    /**
     * 上传失败,替换城原来的照片
     *
     * @param errMsg
     */
    private void uploadHeadFailure(String errMsg) {
        WidgetUtils.showToast(errMsg);
        HeaderHelper.loadSelf(iv_header, R.drawable.ic_launcher);
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
                        // 上传成功，通知所有头像控件更新头像
                        iv_header.notifyUpdateHead(DataStorageUtils
                                .getHeadDownloadUrl(fid));
                        info.profile_fid = fid;
                        saveUserInfo();

                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        uploadHeadFailure(errorMsg);
                    }
                });
    }

    /**
     * 保存用户信息
     */
    private void saveUserInfo() {
        //DataStorageUtils.setUserInfo(info);
        DataStorageUtils.setUserInfoChange(true);
        DataStorageUtils.setCurUserProfileFid(info.profile_fid);
        CurrentUser.instance().setUserInfo(info);
    }


    /**
     * 更新昵称
     */
    private void updateNickname() {

        FragmentEditText fragmentEdit = new FragmentEditText("请输入昵称", tv_nickname.getText().toString());
        fragmentEdit.setHint("昵称长度在1-10之间");
        fragmentEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        fragmentEdit.setCallBack(new FragmentEditText.EditFinishCallBack() {
            @Override
            public void onConfirm(final String text) {
                if (text.length() < 10 && text.length() > 0) {
                    YJSNet.updatePersonInfo(
                            new ReqUpdatePersonInfo(text, "", "", "",
                                    "", "", "", "", "", ""),
                            LOG_TAG,
                            new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {

                                @Override
                                public void onSuccess(
                                        RspUpdatePersonInfo data) {
                                    CurrentUser.instance()
                                            .setUserNickname(text);
                                    notifyUserInfoChange(0, text);
                                    DataStorageUtils.setUserNickName(text);
                                    DataStorageUtils.setUserInfoChange(true);
                                }

                                @Override
                                public void onFailure(String errorMsg) {
                                    WidgetUtils.showToast("更新失败"
                                            + errorMsg);
                                }
                            });
                    tv_nickname.setText(text);
                } else {
                    WidgetUtils.showToast("昵称长度为1-10");
                }

            }
        });
        fragmentEdit.show(getFragmentManager(), "FragmentEditText");

    }

    /**
     * 更新性别
     */
    private void updateSex() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AtyUserInfo.this);
        final String[] strarr = {"男", "女"};
        builder.setItems(strarr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                final String sex = strarr[arg1];
                YJSNet.updatePersonInfo(new ReqUpdatePersonInfo("", "", sex,
                                "", "", "", "", "", "", ""), LOG_TAG,
                        new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {

                            @Override
                            public void onSuccess(RspUpdatePersonInfo data) {
                                CurrentUser.instance().setSex(sex);
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                WidgetUtils.showToast("更新失败" + errorMsg);
                            }
                        });
                tv_sex.setText(sex);
                updateSexIcon(sex);
            }
        }).setTitle("请选择性别");
        builder.show();
    }

    private void notifyUserInfoChange(int i, String content) {
        switch (i) {
            case 0:
                EventBus.getDefault().post(new EventUpdateUserInfo(content, 0));
                break;
            case 1:
                EventBus.getDefault().post(new EventUpdateUserInfo(content, 1));
                break;
            case 2:
                EventBus.getDefault().post(new EventUpdateUserInfo(content, 2));
                break;
            case 3:
                EventBus.getDefault().post(new EventUpdateUserInfo(content, 3));
                break;
        }
    }

    /**
     * 更新身高
     */
    private void updateHeight() {

        FragmentEditText fragmentEdit = new FragmentEditText("请输入身高", tv_height.getText().toString());
        fragmentEdit.setHint("身高范围在50-250cm");
        fragmentEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        fragmentEdit.setCallBack(new FragmentEditText.EditFinishCallBack() {
            @Override
            public void onConfirm(final String text) {
                if (!text.isEmpty() && Integer.parseInt(text) <= 250
                        && Integer.parseInt(text) >= 50) {
                    YJSNet.updatePersonInfo(
                            new ReqUpdatePersonInfo("", "", "", text,
                                    "", "", "", "", "", ""),
                            LOG_TAG,
                            new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {

                                @Override
                                public void onSuccess(
                                        RspUpdatePersonInfo data) {
                                    CurrentUser.instance()
                                            .setUserHeight(text);
                                    notifyUserInfoChange(1, text);
                                }

                                @Override
                                public void onFailure(String errorMsg) {
                                    WidgetUtils.showToast("更新失败"
                                            + errorMsg);
                                }
                            });
                    tv_height.setText(text);
                } else {
                    WidgetUtils.showToast("修改失败，身高范围在50cm－250cm");
                }

            }
        });
        fragmentEdit.show(getFragmentManager(), "FragmentEditText");
    }

    /**
     * 更新体重
     */
    private void updateWeight() {

        FragmentEditText fragmentEdit = new FragmentEditText("请输入体重", tv_weight.getText().toString());
        fragmentEdit.setHint("体重在30kg-200kg");
        fragmentEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        fragmentEdit.setCallBack(new FragmentEditText.EditFinishCallBack() {
            @Override
            public void onConfirm(final String text) {
                if (!text.isEmpty() && Integer.parseInt(text) >= 30
                        && Integer.parseInt(text) <= 200) {
                    YJSNet.updatePersonInfo(
                            new ReqUpdatePersonInfo("", "", "", "",
                                    text, "", "", "", "", ""),
                            LOG_TAG,
                            new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {

                                @Override
                                public void onSuccess(
                                        RspUpdatePersonInfo data) {
                                    CurrentUser.instance()
                                            .setUserWeight(text);
                                    notifyUserInfoChange(2, text);
                                    Log.d("AtyUserInfo",
                                            CurrentUser.instance()
                                                    .getPersonInfo().weight);
                                }

                                @Override
                                public void onFailure(String errorMsg) {
                                    WidgetUtils.showToast("更新失败"
                                            + errorMsg);
                                }
                            });
                    tv_weight.setText(text);
                } else {
                    WidgetUtils.showToast("更新失败，体重范围30kg－200kg");
                }

            }
        });
        fragmentEdit.show(getFragmentManager(), "FragmentEditText");
    }

    /**
     * 更新生日
     */
    private void updateBirth() {

        FragmentDatePicker datePicker = new FragmentDatePicker();
        datePicker.setInitDateString(tv_birth.getText().toString());
        datePicker.setCallBack(new FragmentDatePicker.DatePickerCallBack() {
            @Override
            public void onConfirm(int year, int monthOfYear, int dayOfMonth) {
                final String birth = dateFormatter(year, monthOfYear, dayOfMonth);
                YJSNet.updatePersonInfo(
                        new ReqUpdatePersonInfo("", "", "", "", "",
                                birth, "", "", "", ""),
                        LOG_TAG,
                        new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {

                            @Override
                            public void onSuccess(
                                    RspUpdatePersonInfo data) {
                                CurrentUser.instance().setUserBirth(
                                        birth);
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                WidgetUtils
                                        .showToast("更新失败" + errorMsg);
                            }
                        });
                tv_birth.setText(birth);
            }
        });
        datePicker.show(getFragmentManager(), "FragmentDatePicker");
    }

    private String dateFormatter(int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear + 1;
        String dayStr = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String monthStr = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;

        return year + "-" + monthStr + "-" + dayStr;
    }

    /**
     * 性别图标
     */
    private void updateSexIcon(String sex) {
        if (sex.equals("男")) {
            ivSex.setImageResource(R.drawable.icon_man);
        } else {
            ivSex.setImageResource(R.drawable.icon_woman);
        }
    }

    /**
     * 更新所在地
     */
//	private void updateLocation() {
//		DlgEdit dlgEdit = new DlgEdit(AtyUserInfo.this,
//				new DlgEdit.EditDialogListener() {
//					@Override
//					public void onOk(final String text) {
//						if (text!=null&&text.length() <= 15 && text.length() >= 6) {
//							YJSNet.updatePersonInfo(
//									new ReqUpdatePersonInfo("", "", "", "", "",
//											"", "", "", text, ""),
//									LOG_TAG,
//									new YJSNet.OnRespondCallBack<RspUpdatePersonInfo>() {
//
//										@Override
//										public void onSuccess(
//												RspUpdatePersonInfo data) {
//											CurrentUser.instance()
//													.setUserLocation(text);
//										}
//
//										@Override
//										public void onFailure(String errorMsg) {
//											WidgetUtils.showToast("更新失败"
//													+ errorMsg);
//										}
//									});
//							tv_place.setText(text);
//						} else {
//							WidgetUtils.showToast("更新失败，所在地长度在6-15");
//						}
//					}
//
//					@Override
//					public void onCancel() {
//
//					}
//				});
//		dlgEdit.show("长度在3-15之间", " 请输入所在地");
//	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_UPDATE_SIGN:
                initData();
                break;
            case REQUEST_UPDATE_PROF:
                initData();
                break;
            default:
                processAvatar(requestCode, resultCode, data);
                break;
//

        }
    }

    private void processAvatar(final int requestCode, final int resultCode, Intent data) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YJSNet.removeRspCallBacks(LOG_TAG);
    }
}
