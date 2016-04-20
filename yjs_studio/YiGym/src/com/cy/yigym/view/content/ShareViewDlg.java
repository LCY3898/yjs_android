package com.cy.yigym.view.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.cy.share.ShareHelper;
import com.cy.share.ShareHelper.OnShareCallBack;
import com.cy.widgetlibrary.base.BaseView;
import com.cy.widgetlibrary.base.BindView;
import com.efit.sport.R;
import com.hhtech.utils.BitmapUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-30
 * </p>
 * <p>
 * 分享控件，触发分享功能
 * </p>
 */
public class ShareViewDlg extends BaseView implements OnClickListener {
	@BindView
	private ImageView imgWeixin, imgWeicircle, imgSinaweibo;
	private String title, content, imgUrl, url;
	private boolean isSetShareData = false;
	private OnShareCallBack weixinCbk, weicircleCbk, sinaWeiboCbk;

	private View viewToShare;

	/**
	 * tangtt
	 * 设置是否只显示图片，微信同时显示图片和内容会变成webPage，图片以缩略图显示
	 * */
	private boolean shareImageOnly = true;

	public ShareViewDlg(Context context) {
		super(context);
	}

	public ShareViewDlg(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initView() {
		imgWeixin.setOnClickListener(this);
		imgWeicircle.setOnClickListener(this);
		imgSinaweibo.setOnClickListener(this);
	}

	public void setViewToShare(View viewToShare) {
		this.viewToShare = viewToShare;
	}

	public void setShareImageOnly(boolean shareImageOnly) {
		this.shareImageOnly = shareImageOnly;
	}

	@Override
	public void onClick(View v) {
		isValid();
		if(viewToShare == null) {
			throw new RuntimeException("请先使用setViewToShare设置要分享的view");
		}
		switch (v.getId()) {
		case R.id.imgWeixin: {
			ShareHelper.getInstance().share(getContext(), SHARE_MEDIA.WEIXIN, title, getWXShareContent(),
					BitmapUtils.drawToBitmap(viewToShare),
					getWXShareUrl(), new OnShareCallBack() {

						@Override
						public void onSuccess() {
							if (weixinCbk != null)
								weixinCbk.onSuccess();
						}

						@Override
						public void onFailure(String errMsg) {
							if (weixinCbk != null)
								weixinCbk.onFailure(errMsg);
						}
					});
		}
			break;
		case R.id.imgWeicircle:
			ShareHelper.getInstance().share(getContext(), SHARE_MEDIA.WEIXIN_CIRCLE,title, getWXShareContent(),
					BitmapUtils.drawToBitmap(viewToShare), getWXShareUrl(), new OnShareCallBack() {

						@Override
						public void onSuccess() {
							if (weicircleCbk != null)
								weicircleCbk.onSuccess();
						}

						@Override
						public void onFailure(String errMsg) {
							if (weicircleCbk != null)
								weicircleCbk.onFailure(errMsg);
						}
					});
			break;
		case R.id.imgSinaweibo:
			ShareHelper.getInstance().share(getContext(), SHARE_MEDIA.SINA, title, content,
					BitmapUtils.drawToBitmap(viewToShare), url,
					new OnShareCallBack() {

						@Override
						public void onSuccess() {
							if (sinaWeiboCbk != null)
								sinaWeiboCbk.onSuccess();
						}

						@Override
						public void onFailure(String errMsg) {
							if (sinaWeiboCbk != null)
								sinaWeiboCbk.onFailure(errMsg);
						}
					});
			break;
		}
	}


	private void isValid() {
		if (!isSetShareData)
			throw new RuntimeException("请调用setShareData方法设置分享内容");
	}

	/**
	 * 设置分享数据
	 * 
	 * @param title
	 * @param content
	 * @param imgUrl
	 * @param url
	 */
	public void setShareData(String title, String content, String imgUrl,
			String url) {
		isSetShareData = true;
		this.title = title;
		this.content = content;
		this.imgUrl = imgUrl;
		this.url = url;
	}

	/**
	 * 设置微信分享回调
	 * 
	 * @param cbk
	 */
	public void setOnShareToWeixinCallBack(OnShareCallBack cbk) {
		this.weixinCbk = cbk;
	}

	/**
	 * 设置微信朋友圈分享回调
	 * 
	 * @param cbk
	 */
	public void setOnShareToWeicircleCallBack(OnShareCallBack cbk) {
		this.weicircleCbk = cbk;
	}

	/**
	 * 设置新浪微博分享回调
	 * 
	 * @param cbk
	 */
	public void setOnShareToSinaWeiboCallBack(OnShareCallBack cbk) {
		this.sinaWeiboCbk = cbk;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.view_sharedlg;
	}


	private String getWXShareContent() {
		if(shareImageOnly) {
			return "";
		} else {
			return content;
		}
	}

	private String getWXShareUrl() {
		if(shareImageOnly) {
			return "";
		} else {
			return url;
		}
	}
}
