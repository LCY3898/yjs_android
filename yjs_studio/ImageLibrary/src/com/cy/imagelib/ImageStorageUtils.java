package com.cy.imagelib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-29
 * </p>
 * <p>
 * 图片存储工具类
 * </p>
 */
public class ImageStorageUtils {

	/**
	 * 保存Bitmap到SD卡
	 * 
	 * @param bmp
	 * @param savePath
	 */
	public static void saveBitmapToSdCard(Bitmap bmp, String savePath) {
		if (bmp == null || TextUtils.isEmpty(savePath))
			return;
		byte[] bytes = ImageCoverUtils.bitmap2Bytes(bmp);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(savePath));
			out.write(bytes);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
