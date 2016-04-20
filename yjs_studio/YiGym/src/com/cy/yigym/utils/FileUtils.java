package com.cy.yigym.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.os.Environment;
import android.text.TextUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-21
 * </p>
 * <p>
 * 文件工具类
 * </p>
 */
public class FileUtils {
	// 根缓存目录
	private static String cacheRootPath = "";

	/**
	 * sd卡是否可用
	 * 
	 * @return
	 */
	public static boolean isSdCardAvailable() {
		// if (!PackageUtils
		// .checkPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
		// throw new RuntimeException(
		// "请在Manifest里面添加android.permission.WRITE_EXTERNAL_STORAGE权限");
		// }
		// if (!PackageUtils
		// .checkPermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")) {
		// throw new RuntimeException(
		// "请在Manifest里面添加android.permission.MOUNT_UNMOUNT_FILESYSTEMS权限");
		// }
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 创建根缓存目录
	 * 
	 * @return
	 */
	private static String createRootPath() {
		if (isSdCardAvailable()) {
			// /sdcard/Android/data/<application package>/cache
			cacheRootPath = AppUtils.getAppContext().getExternalCacheDir()
					.getPath();
		} else {
			// /data/data/<application package>/cache
			cacheRootPath = AppUtils.getAppContext().getCacheDir().getPath();
		}
		return cacheRootPath;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param dirPath
	 * @return 创建失败返回""
	 */
	private static String createDir(String dirPath) {
		try {
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			return dir.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dirPath;
	}

	/**
	 * 获取图片缓存目录
	 * 
	 * @return 创建失败,返回""
	 */
	public static String getImageCachePath() {
		String path = createDir(createRootPath() + File.separator + "img"
				+ File.separator);
		return path;
	}

	/**
	 * 获取图片裁剪缓存目录
	 * 
	 * @return 创建失败,返回""
	 */
	public static String getImageCropCachePath() {
		String path = createDir(createRootPath() + File.separator + "imgCrop"
				+ File.separator);

		return path;
	}

	/**
	 * 删除文件或者文件夹
	 * 
	 * @param file
	 */
	public static void deleteFileOrDirectory(File file) {
		try {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				// 删除空文件夹
				if (childFiles == null || childFiles.length == 0) {
					file.delete();
					return;
				}
				// 递归删除文件夹下的子文件
				for (int i = 0; i < childFiles.length; i++) {
					deleteFileOrDirectory(childFiles[i]);
				}
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将内容写入文件
	 * 
	 * @param filePath
	 *            eg:/mnt/sdcard/demo.txt
	 * @param content
	 *            内容
	 */
	public static void writeFileSdcard(String filePath, String content,
			boolean isAppend) {

		try {
			FileOutputStream fout = new FileOutputStream(filePath, isAppend);
			byte[] bytes = content.getBytes();

			fout.write(bytes);

			fout.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}
