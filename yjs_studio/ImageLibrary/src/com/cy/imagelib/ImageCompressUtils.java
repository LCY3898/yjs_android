package com.cy.imagelib;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-20
 * </p>
 * <p>
 * 图片压缩工具类</br> 1.支持从网络上下载的图片压缩。</br> 2.支持从资源目中的图片压缩。</br> 3.支持从SD卡中的图片压缩。</br>
 * </p>
 */
public class ImageCompressUtils {
	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-7-20
	 * </p>
	 * <p>
	 * 压缩类型
	 * </p>
	 */
	private static enum CompressType {
		FROM_INPUTSTREAM("inputstream", InputStream.class), FROM_FILE("file",
				String.class), FROM_RESOURCE("resouce", Integer.class);
		public String tag;
		public Class<?> resClass;

		CompressType(String tag, Class<?> resClass) {
			this.tag = tag;
			this.resClass = resClass;
		}
	}

	/**
	 * 压缩图片,用于从网络上下载的图片
	 * 
	 * @param bmpInputstream
	 *            输入流
	 * @param reqWidth
	 *            压缩后的宽度
	 * @param reqHeight
	 *            压缩后的高度
	 * @return 如果is为null,或者reqWidth及reqHeight有一个<=0,则返回null
	 */
	public static Bitmap compress(InputStream bmpInputstream, int reqWidth,
			int reqHeight) {
		if (bmpInputstream == null || reqHeight <= 0 || reqWidth <= 0)
			return null;
		return compressInternal(CompressType.FROM_INPUTSTREAM, bmpInputstream,
				reqWidth, reqHeight);
	}

	/**
	 * 压缩图片,用于从资源目录中获取
	 * 
	 * @param bmpResId
	 * @param reqWidth
	 * @param reqHeight
	 * @return 如果bmpResId为0,或者reqWidth及reqHeight有一个<=0,则返回null
	 */
	public static Bitmap compress(int bmpResId, int reqWidth, int reqHeight) {
		if (bmpResId == 0 || reqHeight <= 0 || reqWidth <= 0)
			return null;
		return compressInternal(CompressType.FROM_RESOURCE, bmpResId, reqWidth,
				reqHeight);
	}

	/**
	 * 压缩图片,用于从Sd卡中获取的图片
	 * 
	 * @param bmpPath
	 * @param reqWidth
	 * @param reqHeight
	 * @return 如果bmpPath为null或者"",或者reqWidth及reqHeight有一个<=0,则返回null
	 */
	public static Bitmap compress(String bmpPath, int reqWidth, int reqHeight) {
		if (TextUtils.isEmpty(bmpPath) || reqHeight <= 0 || reqWidth <= 0)
			return null;
		return compressInternal(CompressType.FROM_FILE, bmpPath, reqWidth,
				reqHeight);
	}

	/**
	 * 压缩方法的内部实现
	 * <p>
	 * 实现原理，先设置{@link BitmapFactory#Options}
	 * 的inJustDecodeBounds为true,此时用BitampFactory的decode方法不会为Bitmap分配内存
	 * ,但是可以获取bitmap的宽度和高度以及MIME等信息.</br>
	 * 用获取到的宽高来计算需要设置的inSampleSize,假设该值为4,则宽、高会缩小为原来的1/4,大小会缩小为原来的1/16.</br>
	 * 之后将inJustDecodeBounds设置为false,再次decode,就会得缩放后的图片了。
	 * </p>
	 * 
	 * @param compressType
	 * @param res
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static Bitmap compressInternal(CompressType compressType,
			Object res, int reqWidth, int reqHeight) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			switch (compressType) {
			case FROM_RESOURCE:
				if (res.getClass() != CompressType.FROM_RESOURCE.resClass) {
					throw new IllegalArgumentException(
							String.format("参数res的类型不是%s",
									CompressType.FROM_RESOURCE.resClass
											.getSimpleName()));
				}
				BitmapFactory
						.decodeResource(ImageLibInitializer.getResources(),
								(Integer) res, opts);
				opts.inSampleSize = calculateInSampleSize(opts, reqWidth,
						reqHeight);
				opts.inJustDecodeBounds = false;
				return BitmapFactory
						.decodeResource(ImageLibInitializer.getResources(),
								(Integer) res, opts);
			case FROM_FILE:
				if (res.getClass() != CompressType.FROM_FILE.resClass) {
					throw new IllegalArgumentException(String.format(
							"参数res的类型不是%s",
							CompressType.FROM_FILE.resClass.getSimpleName()));
				}
				BitmapFactory.decodeFile((String) res, opts);
				opts.inSampleSize = calculateInSampleSize(opts, reqWidth,
						reqHeight);
				opts.inJustDecodeBounds = false;
				return BitmapFactory.decodeFile((String) res, opts);
			case FROM_INPUTSTREAM:
				if (res.getClass() != CompressType.FROM_INPUTSTREAM.resClass) {
					throw new IllegalArgumentException(String.format(
							"参数res的类型不是%s",
							CompressType.FROM_INPUTSTREAM.resClass
									.getSimpleName()));
				}
				BitmapFactory.decodeStream((InputStream) res, null, opts);
				opts.inSampleSize = calculateInSampleSize(opts, reqWidth,
						reqHeight);
				opts.inJustDecodeBounds = false;
				return BitmapFactory
						.decodeStream((InputStream) res, null, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 计算inSampleSize
	 * 
	 * @param opts
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options opts,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = opts.outHeight;
		final int width = opts.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
}
