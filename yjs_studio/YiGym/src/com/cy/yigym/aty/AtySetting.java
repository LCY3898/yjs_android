package com.cy.yigym.aty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.ActivityManager;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.content.DlgEdit;
import com.cy.widgetlibrary.content.DlgEdit.EditOkListener;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.yigym.CurrentUser;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.rsp.RspLogOut;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.StartThirdPartyAppUtils;
import com.cy.yigym.view.content.DlgCommentYjs;
import com.efit.sport.R;
import com.sport.efit.theme.ColorTheme;

import java.text.DecimalFormat;

//设置
public class AtySetting extends BaseFragmentActivity implements
		View.OnClickListener {
	// 标题
	@BindView
	private CustomTitleView vTitle;
	// 清除缓存
	@BindView
	private View ll_clear;
	// 评价软件
	@BindView
	private View ll_evaluation;
	// 关于软件
	@BindView
	private View ll_about;
	// 意见反馈
	@BindView
	private View ll_opinion;
	// 退出登入
	@BindView
	private Button bt_logout;
	private DlgCommentYjs dlgCommentYjs;
	private DecimalFormat df00 = new DecimalFormat("#0.00");

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.aty_setting;
	}

	@Override
	protected void initView() {
		vTitle.setTitle("设置");
		vTitle.setTxtLeftText("       ");
		vTitle.setTxtLeftIcon(R.drawable.icon_arrow_left);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		ll_clear.setOnClickListener(this);
		ll_evaluation.setOnClickListener(this);
		ll_about.setOnClickListener(this);
		ll_opinion.setOnClickListener(this);
		bt_logout.setOnClickListener(this);
		bt_logout.setBackground(ColorTheme.getBlueBgBtn());

		vTitle.getTxtCenter().setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(AtySetting.this, "当前是:" + (DataStorageUtils.isInClubMode() ? "健身" : "家庭"), Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				final String[] strarr = {"健身房模式", "家用模式"};
				builder.setItems(strarr, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int position) {
						DataStorageUtils.setInClubMode(position == 0);
						if(position == 0) {
							DlgEdit dlgEdit = new DlgEdit(mActivity, false,new EditOkListener() {
								@Override
								public void onOk(String bleAddress) {
									if(TextUtils.isEmpty(bleAddress)) {
										WidgetUtils.showToast("蓝牙地址不能为空");
										return;
									}
									if(bleAddress.length() != 12) {
										WidgetUtils.showToast("请输入12位蓝牙地址");
										return;
									}
									getDlg().dismiss();
									DataStorageUtils.setBleAddress(bleAddress);
									//休眠一分钟再跳转，以保存输入的蓝牙地址
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									Intent intent=new Intent(AtySetting.this,AtyMain2.class);
									startActivity(intent);
								}

								@Override
								public void onCancel() {
									super.onCancel();
									getDlg().dismiss();
									Intent intent=new Intent(AtySetting.this,AtyMain2.class);
									startActivity(intent);
								}
							});
							dlgEdit.show("请输入蓝牙地址", "输入蓝牙地址");
						}
					}
				}).setTitle("请选择使用场景");
				builder.show();
				return true;
			}
		});
	}

	@Override
	protected void initData() {
		commentYjs();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_clear:
			clearCache();
			break;
		case R.id.ll_evaluation:
			// TODO 评论软件
			dlgCommentYjs.show();
			break;
		case R.id.ll_about:
			// TODO 关于“e健身”
			startActivity(AtyAboutEjs.class);
			break;
		case R.id.ll_opinion:
			// TODO 意见反馈
			startActivity(AtyFeedBack.class);
			break;
		case R.id.bt_logout:
			// TODO 退出登入
			logOut();
			break;
		}
	}

	/**
	 * 清除缓存
	 */
	private void clearCache() {
		new Thread() {
			public void run() {

				final double cacheSize = ImageLoaderUtils.getInstance()
						.getCacheSize();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (cacheSize > 0)
							showClearChacheDialog(cacheSize);
						else
							WidgetUtils.showToast("没有缓存，无需清除");
					}
				});

			};
		}.start();
	}

	/**
	 * 显示清除缓存对话框
	 * 
	 * @param cacheSize
	 */
	private void showClearChacheDialog(double cacheSize) {
		final String size = df00.format(cacheSize);
		DlgTextMsg dlg0 = new DlgTextMsg(mActivity,
				new DlgTextMsg.ConfirmDialogListener() {
					@Override
					public void onCancel() {

					}

					@Override
					public void onOk() {
						ImageLoaderUtils.getInstance().cleanCache();
						WidgetUtils.showToast(String.format("共清除%s缓存", size));
					}

					@Override
					public void onCenter() {
					}
				});
		dlg0.setBtnString("取消", "确定");
		dlg0.show(String.format("清除缓存，有%sMb缓存，是否清除？", size));
	}

	/**
	 * 评论软件
	 */
	private void commentYjs() {
		dlgCommentYjs = new DlgCommentYjs(mActivity);
		dlgCommentYjs.getRlSupport().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						StartThirdPartyAppUtils.goToAppDetailInMarket(
								mActivity.getPackageName(), "");
						dlgCommentYjs.dismiss();
					}
				});
		dlgCommentYjs.getRlIgnore().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlgCommentYjs.dismiss();
					}
				});
		dlgCommentYjs.getRlNoOpinion().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(AtySetting.this,
								AtyFeedBack.class));
						dlgCommentYjs.dismiss();
					}
				});
	}

	// 退出账号
	private void logOut() {
		DlgTextMsg dlg = new DlgTextMsg(mActivity,
				new DlgTextMsg.ConfirmDialogListener() {
					@Override
					public void onCancel() {

					}

					@Override
					public void onOk() {
						YJSNet.logout(LOG_TAG,
								new YJSNet.OnRespondCallBack<RspLogOut>() {
									@Override
									public void onSuccess(RspLogOut data) {
										CurrentUser.instance().setPasswd("");
										ActivityManager.getInstance()
												.exitApplication();
										startActivity(AtyLogin.class);
										// finish();
									}

									@Override
									public void onFailure(String errorMsg) {
										WidgetUtils.showToast(String.format(
												"退出失败", errorMsg));
									}
								});
					}

					@Override
					public void onCenter() {
					}
				});
		dlg.setBtnString("取消", "确定");
		dlg.show("退出提示", "是否退出当前账号");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
	}

}
