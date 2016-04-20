package com.cy.imagelib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-29
 * </p>
 * <p>
 * 跟图片外形(如:大小，角度等)相关的工具类
 * </p>
 */
public class ImageShapeUtils {

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 *            旋转角度
	 * @param bitmap
	 *            原图
	 * @return 若图片为空，或者角度为负值则返回null。
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
		if (bitmap == null || angle < 0)
			return null;
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		matrix.setRotate(angle);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		if (newBitmap != bitmap && bitmap.isRecycled() == false) {
			bitmap.recycle();
		}
		return newBitmap;
	}


	/**
	 * 获取圆角图片
	 * @param bitmap
	 *        原图
	 * @param roundPx
	 *        圆角大小
	 * @return
	 */
	public static Bitmap getCornerBitmap(Bitmap bitmap,float roundPx){
		Bitmap outputBitmap=Bitmap.createBitmap(bitmap.getWidth()
		,bitmap.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas=new Canvas(outputBitmap);
		final Paint paint=new Paint();
		final Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
		final RectF rectF=new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap,rect,rect,paint);
		return outputBitmap;
	}


	/**
	 * 获取顶部圆角图片
	 * @param bitmap
	 *        原图
	 * @param roundPx
	 *        圆角大小
	 * @return
	 */
	public static Bitmap getTopCornerBitmap(Bitmap bitmap,int roundPx){
		Bitmap outputBitmap=Bitmap.createBitmap(bitmap.getWidth()
				,bitmap.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas=new Canvas(outputBitmap);
		final Paint paint=new Paint();
		final Rect rect=new Rect(0,roundPx,bitmap.getWidth(),bitmap.getHeight());
		final RectF rectF=new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap,rect,rect,paint);
		return outputBitmap;
	}
}
