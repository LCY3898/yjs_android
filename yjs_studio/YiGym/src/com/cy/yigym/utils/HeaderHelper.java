package com.cy.yigym.utils;


import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.cy.imagelib.ImageLoaderUtils;

/**
 * Caiyuan Huang
 *<p>2015-3-6</p>
 *<p>请在Application里面进行初始化</p>
 */
public class HeaderHelper {

	public static void loadSelf(ImageView v) {
		String fid = DataStorageUtils.getCurUserProfileFid();
		load(fid,v,0);
	}

	public static void loadSelf(ImageView v,int defautHeaderRes) {
		String fid = DataStorageUtils.getCurUserProfileFid();
		load(fid,v,defautHeaderRes);
	}

	public static void load(String fid,ImageView v) {
		load(fid,v,0);
	}
	public static void load(String fid,ImageView v,int defautHeaderRes) {
		if(!TextUtils.isEmpty(fid)) {
			if(fid.startsWith("http")) {//qq,weixin 登录时有头像，头像是网址
				ImageLoaderUtils.getInstance().loadImage(fid,v);
			} else {
				ImageLoaderUtils.getInstance().loadImage(DataStorageUtils
						.getHeadDownloadUrl(fid),v);
			}
		} else if(defautHeaderRes != 0) {
			try {
				v.setImageResource(defautHeaderRes);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
