package com.cy.yigym.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cy.imagelib.ImageCoverUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Caiyuan Huang
 * <p>
 * 2015-4-1
 * </p>
 * 拍照工具类</br> 使用{@link #setOnPhotoResultListener}
 * 等回调方法，在调用者所在的Activity或者Fragment的onActivityResult方法中获取操作结果。
 * <p>
 * </p>
 * <p>
 * 需要的权限列表为:<br>
 * &lt;uses-permission android:name="android.permission.CAMERA"/> <br>
 * &lt;uses-permission
 * android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * </p>
 */
public class PhotoUtils {
	public static class RequestCode {
		public static final int TAKE_PHOTO_BY_SYSTEM = 0;
		public static final int PICK_PHOTO_FROM_GALLERY = 1;
		public static final int CROP_PHOTO_BY_SYSTEM = 2;
	}

	private static Uri tempPhotoUri = null;
	private static Uri tempPhotoCropUri = null;

	private static void startActivityForResult(Context context, Intent intent,
			int requestCode, Fragment fragment) {
		if (intent.resolveActivity(context.getPackageManager()) != null) {
			if (fragment != null) {
				fragment.startActivityForResult(intent, requestCode);
			} else if (context instanceof Activity) {
				Activity activity = (Activity) context;
				activity.startActivityForResult(intent, requestCode);
			}
		} else {
			Toast.makeText(context, "本手机没有安装对应的应用程序", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2015-4-1
	 * </p>
	 * <p>
	 * 照片返回监听接口
	 * </p>
	 */
	public interface OnPhotoResultListener {
		/**
		 * @param photoPath
		 *            照片的路径
		 * @param photo
		 *            照片原文件
		 */
		void onPhotoResult(String photoPath, Bitmap photo);
	}

	/**
	 * 使用系统相机进行拍照
	 * 
	 * @param context
	 * @param fragment
	 *            若为null则表示在Activity中启动，否则在Fragment中启动F
	 */
	public static void takePhotoBySystem(Context context, Fragment fragment) {
		checkPermission();
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File photoFile = new File(FileUtils.getImageCachePath(), "imgTemp");
			tempPhotoUri = Uri.fromFile(photoFile);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(photoFile));
			startActivityForResult(context, intent,
					RequestCode.TAKE_PHOTO_BY_SYSTEM, fragment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置照片回调接口,一般用于只在Activity或Fragment使用了本类中的一种照片操作方法的结果回调
	 * 
	 * @param context
	 * @param requestCode
	 *            请求码
	 * @param resultCode
	 *            结果码
	 * @param data
	 *            onActivityResult返回的意图
	 * @param l
	 */
	public static void setOnPhotoResultListener(Context context,
			int requestCode, int resultCode, Intent data,
			OnPhotoResultListener l) {
		switch (requestCode) {
		case RequestCode.TAKE_PHOTO_BY_SYSTEM:
			setOnTakePhotoResultListener(resultCode, data, l);
			break;
		case RequestCode.PICK_PHOTO_FROM_GALLERY:
			setOnPickPhotoResultListener(context, resultCode, data, l);
			break;
		case RequestCode.CROP_PHOTO_BY_SYSTEM:
			setOnCropPhotoResultListener(resultCode, data, l);
			break;
		}
	}

	/**
	 * 系统相机拍照回调
	 * 
	 * @param resultCode
	 * @param data
	 * @param l
	 */
	public static void setOnTakePhotoResultListener(int resultCode,
			Intent data, OnPhotoResultListener l) {
		if (l == null || resultCode != Activity.RESULT_OK
				|| tempPhotoUri == null)
			return;
		handleDegree(tempPhotoUri.getPath());
		l.onPhotoResult(tempPhotoUri.getPath(), null);

	}

	/**
	 * 从图库中选择照片
	 * 
	 * @param context
	 * @param fragment
	 */
	public static void pickPhotoFormGallery(Context context, Fragment fragment) {
		checkPermission();
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(context, intent,
				RequestCode.PICK_PHOTO_FROM_GALLERY, fragment);
	}

	/**
	 * 系统图库照片选择回调
	 * 
	 * @param context
	 * @param resultCode
	 * @param data
	 * @param l
	 */
	public static void setOnPickPhotoResultListener(Context context,
			int resultCode, Intent data, OnPhotoResultListener l) {
		if (context == null || l == null || resultCode != Activity.RESULT_OK)
			return;
		try {
			ContentResolver mContentResolver = context.getContentResolver();
			// Bitmap photo =
			// MediaStore.Images.Media.getBitmap(mContentResolver,
			// data.getData());
			// 获取图片路径
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = mContentResolver.query(data.getData(), proj, null,
					null, null);
			String path;
			// @tangtaotao cursor有可能为空
			if (cursor != null) {
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				path = cursor.getString(column_index);
			} else {
				// @tangtaotao 临时使用此地址，这个地址不是真实的path
				path = data.getData().getPath();
			}
			handleDegree(path);
			l.onPhotoResult(path, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 系统裁剪图片回调
	 * 
	 * @param context
	 * @param fragment
	 * @param photoPath
	 *            照片的路径
	 * @param width
	 *            裁剪宽度
	 * @param height
	 *            裁剪高度
	 */
	public static void cropPhotoBySystem(Context context, Fragment fragment,
			String photoPath, int width, int height) {
		checkPermission();
		try {
			File cropFile = new File(FileUtils.getImageCropCachePath(),
					"imgCropTemp");
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(Uri.fromFile(new File(photoPath)), "image/*");
			tempPhotoCropUri = Uri.fromFile(cropFile);
            Log.d("xxxxx","tempPhotoCropUri"+tempPhotoCropUri);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					tempPhotoCropUri);
			intent.putExtra("crop", "true");
			// aspectX aspectY 是宽高的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", width);
			intent.putExtra("outputY", height);
			intent.putExtra("return-data", false);
			intent.putExtra("circleCrop", true);
			startActivityForResult(context, intent,
					RequestCode.CROP_PHOTO_BY_SYSTEM, fragment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 系统照片裁剪回调
	 * 
	 * @param resultCode
	 * @param data
	 * @param l
	 */
	public static void setOnCropPhotoResultListener(int resultCode,
			Intent data, OnPhotoResultListener l) {
		if (data == null || l == null || resultCode != Activity.RESULT_OK)
			return;
		// Bitmap photo = data.getParcelableExtra("data");
		l.onPhotoResult(tempPhotoCropUri.getPath(), null);
	}

	/**
	 * 检查所需权限
	 */
	private static void checkPermission() {
		if (!PackageUtils.checkPermission("android.permission.CAMERA")) {
			throw new RuntimeException(
					"请在Manifest里面添加android.permission.CAMERA权限");
		}
		if (!PackageUtils
				.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
			throw new RuntimeException(
					"请在Manifest里面添加android.permission.WRITE_EXTERNAL_STORAGE权限");
		}
	}

	/**
	 * 获取照片角度
	 * 
	 * @param picPath
	 *            照片路径
	 * @return
	 */
	private static int getPicDegree(String picPath) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(picPath);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 处理竖屏拍照90度的问题
	 * 
	 * @param picPath
	 *            照片路径
	 */
	private static void handleDegree(String picPath) {
		if (TextUtils.isEmpty(picPath))
			return;
		File picFile = new File(picPath);
		if (!picFile.exists())
			return;
		int degree = getPicDegree(picPath);
		if (degree == 0)
			return;
		Bitmap bmpSrc = compressBitmap(picPath, 800, 480);
		Bitmap bmpRotate = rotateBitmap(bmpSrc, degree);
		if (!bmpSrc.isRecycled()) {
			bmpSrc.recycle();
			bmpSrc = null;
			System.gc();
		}
		picFile.delete();
		saveBitmapToSdCard(bmpRotate, picPath);
		if (!bmpRotate.isRecycled()) {
			bmpRotate.recycle();
			bmpRotate = null;
			System.gc();
		}

	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 *            旋转角度
	 * @param bitmap
	 *            原图
	 * @return 若图片为空则返回null。
	 */
	private static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
		if (bitmap == null)
			return null;
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		matrix.postRotate(angle);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBitmap;
	}

	/**
	 * 压缩图片
	 * 
	 * @param picPath
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static Bitmap compressBitmap(String picPath, int reqWidth,
			int reqHeight) {
		Bitmap bmp = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picPath, opts);
			opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
			opts.inJustDecodeBounds = false;
			bmp = BitmapFactory.decodeFile((String) picPath, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
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

	/**
	 * 保存Bitmap到SD卡
	 * 
	 * @param bmp
	 * @param savePath
	 */
	private static void saveBitmapToSdCard(Bitmap bmp, String savePath) {
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
