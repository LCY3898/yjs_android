package com.hhtech.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

import com.hhtech.base.AppUtils;

import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {
	

	/**
	 * Draw the view into a bitmap.
	 */
	public static Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);
	
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
	
		// Reset the drawing cache background color to fully transparent
		// for the duration of this operation
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
	
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			Log.e("getViewBitmap", "failed getViewBitmap(" + v + ")",
					new RuntimeException());
			return null;
		}

		Bitmap bitmap = Bitmap.createScaledBitmap(cacheBitmap,cacheBitmap.getWidth() / 2,cacheBitmap.getHeight() / 2,false);
		//Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
	
		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
	
		return bitmap;
	}


	public static Bitmap drawToBitmap(View view) {
		return drawToBitmap(view ,DimenUtils.getDisplayWidth(),DimenUtils.getDisplayHeight());
	}

	public static Bitmap drawToBitmap(View view,int width,int height) {
		Bitmap bitmap = null;
		try {

			view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
			height = view.getMeasuredHeight();
			width = view.getMeasuredWidth();
			view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
			if(width != 0 && height != 0){
				//view.layout(0, 0, width, height);
				bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				view.draw(canvas);
			}
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}

	/*public static Bitmap drawToBitmap(View view,int width,int height) {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(DimenUtils.getDisplayWidth(), DimenUtils.getDisplayHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			view.measure(MeasureSpec.makeMeasureSpec(canvas.getWidth(), MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(canvas.getHeight(),MeasureSpec.EXACTLY));
			view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
			int width = view.getWidth();
			int height = view.getHeight();
			if(width != 0 && height != 0){
				view.layout(0, 0, width, height);
				view.draw(canvas);
			}
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}*/


	public static String getTmpBmpPath() {
		String path = DeviceUtils.getSdDirectory() +
				File.separator + AppUtils.getAppContext().getPackageName() + File.separator + "temp";
		File destDir = new File(path);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return path;
	}

	public static void saveBitmap(Bitmap bmp,String outputPath) {
		try {
			FileOutputStream bitmapWtriter = new FileOutputStream(outputPath);
			bmp.compress(Bitmap.CompressFormat.JPEG, 90, bitmapWtriter);
		 } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveBitmap(int []pixels,int width,int height,String outputPath) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		bmp.setPixels(pixels,0,width,0,0,width,height);
		saveBitmap(bmp, outputPath);
		bmp.recycle();
	}
}
