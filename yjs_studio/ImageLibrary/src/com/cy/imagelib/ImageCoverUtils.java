package com.cy.imagelib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-29
 * </p>
 * <p>
 * 图片数据转换工具类
 * </p>
 */
public class ImageCoverUtils {

	/**
	 * 字节数组转Bitmap
	 * 
	 * @param data
	 *            图片字节数组
	 * @return 如果字节数组为空则返回null。
	 */
	public static Bitmap bytes2Bitmap(byte[] data) {
		if (data == null || data.length == 0)
			return null;
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	/**
	 * Bitmap转字节数组
	 * 
	 * @param bmp
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bmp) {
		if (bmp == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * Bitmap转Drawable
	 * 
	 * @param bmp
	 * @return 若bmp为空，则返回null。
	 */
	public static Drawable bitmap2Drawable(Bitmap bmp) {
		if (bmp == null)
			return null;
		return new BitmapDrawable(bmp);
	}

	/**
	 * Drawable转Bitmap
	 * 
	 * @param drawable
	 * @return 若drawable为空，则返回null。
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable == null)
			return null;
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Bitmap转Base64字符串
	 * 
	 * @param bmp
	 * @return 若bmp为空，则返回""
	 */
	public static String bitmapToBase64(Bitmap bmp) {
		if (bmp == null)
			return "";
		String strBase64 = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bmp != null) {
				baos = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				baos.flush();
				baos.close();
				byte[] bitmapBytes = baos.toByteArray();
				strBase64 = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return strBase64;
	}

	/**
	 * Base64字符串转Bitmap
	 * 
	 * @param base64Data
	 * @return 若base64Data为空，则返回null
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		if (TextUtils.isEmpty(base64Data))
			return null;
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

	}

}
