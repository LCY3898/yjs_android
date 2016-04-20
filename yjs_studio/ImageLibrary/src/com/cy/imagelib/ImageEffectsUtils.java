package com.cy.imagelib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-29
 * </p>
 * <p>
 * 图片特效工具类,参考资料:http://blog.csdn.net/sjf0115/article/details/7267056
 * </p>
 */
public class ImageEffectsUtils {
	/**
	 * 水印效果选型
	 */
	public static class WaterMarkOptions {
		public int paddingLeft = 0;// 与左边框的边距，单位为px
		public int paddingBottom = 0;// 与底边框的边距，单位为px
		public int color = Color.GRAY;// 水印颜色
		public int width = 300;// 水印文字的宽度
		public int size = 16;// 水印文字大小，单位为sp
		public String content = "";// 水印内容
	}

	/**
	 * 水印效果
	 * 
	 * @param bmp
	 * @param options
	 *            水印效果选型
	 * @return 若bmp为null，则返回null，若options为null,bmp不为null，则返回bmp。
	 */
	public static Bitmap watermark(Bitmap bmp, WaterMarkOptions options) {
		if (bmp == null)
			return null;
		if (options == null || TextUtils.isEmpty(options.content)
				|| options.width <= 0 || options.size <= 0)
			return bmp;
		android.graphics.Bitmap.Config bitmapConfig = bmp.getConfig();
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		bmp = bmp.copy(bitmapConfig, true);
		Canvas canvas = new Canvas(bmp);
		TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(options.color);
		paint.setTextSize(options.size);
		paint.setAntiAlias(true);
		StaticLayout layout = new StaticLayout(options.content, paint, 300,
				Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
		int paddingLeft = options.paddingLeft;
		int paddingBottom = bmp.getHeight() - layout.getHeight()
				- options.paddingBottom;
		canvas.save();
		canvas.translate(paddingLeft, paddingBottom);
		layout.draw(canvas);
		canvas.restore();
		return bmp;
	}

	/**
	 * 底片效果,会释放原图,返回处理副本
	 * <p>
	 * 实现原理:</br> 1.将当前像素点的RGB值分别与255之差后的值作为当前点的RGB值。
	 * </p>
	 * 
	 * @param bmp
	 * @return 若bmp为null则返回null
	 */
	public static Bitmap negative(Bitmap bmp) {
		if (bmp == null)
			return null;
		final Bitmap bitmap = bmp.copy(bmp.getConfig(), true);
		getPixels(bitmap, new OnGetPixelsCallBack() {
			@Override
			public void onGetPixels(int[] pixels, int offset, int stride,
					int x, int y, int width, int height) {
				for (int i = 0; i < pixels.length; i++) {
					int color = pixels[i];
					int alpha = Color.alpha(color);
					int red = Color.red(color);
					int green = Color.green(color);
					int blue = Color.blue(color);
					red = 255 - red;
					green = 255 - green;
					blue = 255 - blue;
					pixels[i] = Color.argb(alpha, colorRange(red),
							colorRange(green), colorRange(blue));
				}
				bitmap.setPixels(pixels, offset, stride, x, y, width, height);

			}
		});

		if (!bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
		return bitmap;
	}

	/**
	 * 浮雕效果
	 * <p>
	 * 实现原理:</br>1.用前一个像素点的RGB值分别减去当前像素点的RGB值并加上127作为当前像素点的RGB值.</br>
	 * 例如:ABC</br> 求B点的浮雕效果如下:</br> B.r = C.r - B.r + 127;</br> B.g = C.g - B.g
	 * + 127;</br> B.b = C.b - B.b + 127;</br>
	 * </p>
	 * 
	 * @param bmp
	 * @return 若bmp为null则返回null
	 */
	public static Bitmap relief(Bitmap bmp) {
		if (bmp == null)
			return null;
		final Bitmap bitmap = bmp.copy(bmp.getConfig(), true);
		final int standardValue = 127;// 浮雕效果标准值
		getPixels(bitmap, new OnGetPixelsCallBack() {
			@Override
			public void onGetPixels(int[] pixels, int offset, int stride,
					int x, int y, int width, int height) {
				int[] newPixels = new int[pixels.length];
				for (int i = 1; i < pixels.length; i++) {
					int color = pixels[i - 1];
					int red = Color.red(color);
					int green = Color.green(color);
					int blue = Color.blue(color);
					int color2 = pixels[i];
					int alpha2 = Color.alpha(color2);
					int red2 = Color.red(color2);
					int green2 = Color.green(color2);
					int blue2 = Color.blue(color2);
					red = red - red2 + standardValue;
					green = green - green2 + standardValue;
					blue = blue - blue2 + standardValue;
					newPixels[i] = Color.argb(alpha2, colorRange(red),
							colorRange(green), colorRange(blue));
				}
				bitmap.setPixels(newPixels, offset, stride, x, y, width, height);
			}
		});
		if (!bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
		return bitmap;
	}

	/**
	 * 将color的范围确定在[0,255]
	 * 
	 * @param color
	 * @return
	 */
	private static int colorRange(int color) {
		return color > 255 ? 255 : (color < 0 ? 0 : color);
	}

	/**
	 * 获取一个像素所占的字节大小
	 * 
	 * @param bmp
	 * @return 0表示无法计算
	 */
	private static int getPerPixelSize(Bitmap bmp) {
		if (bmp == null)
			return 0;
		switch (bmp.getConfig()) {
		case ALPHA_8:
			return 1;
		case ARGB_4444:
			return 2;
		case ARGB_8888:
			return 4;
		case RGB_565:
			return 2;
		}
		return 0;
	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-6-5
	 * </p>
	 * <p>
	 * 获取像素数组回调接口
	 * </p>
	 */
	private static interface OnGetPixelsCallBack {
		void onGetPixels(int[] pixels, int offset, int stride, int x, int y,
				int width, int height);
	}

	/**
	 * 获取像素数组，防止数组大小超出最大范围
	 * 
	 * @param bmp
	 * @param callBack
	 */
	private static void getPixels(Bitmap bmp, OnGetPixelsCallBack callBack) {
		if (bmp == null || callBack == null || getPerPixelSize(bmp) == 0)
			return;
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int maxHandleSize = 2 * 1024 * 1024 / getPerPixelSize(bmp);// 一次2M的处理大小
		int handleHeight = (int) (maxHandleSize / bmpWidth * 1.0);// 一次处理的行数
		double times = bmpHeight / (handleHeight * 1.0);
		int intergerHandleTimes = (int) times;// 整数部分处理次数
		int handleTimes = (int) Math.ceil(times);// 总处理次数
		for (int i = 1; i <= handleTimes; i++) {
			int offset = 0;
			int stride = bmpWidth;
			int x = 0;
			int y = 0;
			int height = 0;
			if (intergerHandleTimes < handleTimes && i == handleTimes) {
				// 处理最后一次且容量小于预处理大小的情况
				int pixels[] = new int[bmpWidth * bmpHeight
						- (bmpWidth * handleHeight * intergerHandleTimes)];
				y = (handleTimes - 1) * handleHeight;
				height = bmpHeight - handleHeight * intergerHandleTimes;
				bmp.getPixels(pixels, offset, stride, x, y, bmpWidth, height);
				callBack.onGetPixels(pixels, offset, stride, x, y, bmpWidth,
						height);
				return;
			}
			int pixels[] = new int[bmpWidth * handleHeight];
			y = (i - 1) * handleHeight;
			height = handleHeight;
			bmp.getPixels(pixels, offset, stride, x, y, bmpWidth, height);
			callBack.onGetPixels(pixels, offset, stride, x, y, bmpWidth, height);
		}

	}
}
