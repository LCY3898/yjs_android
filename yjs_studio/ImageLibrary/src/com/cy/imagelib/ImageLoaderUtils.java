package com.cy.imagelib;

import java.io.File;
import java.util.Collection;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.cy.imagelibrary.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageLoaderUtils {
	private static ImageLoaderUtils mInstance = null;

	private ImageLoaderUtils() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				ImageLibInitializer.getAppContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50M
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // 发布时可以移除
				.build();
		ImageLoader.getInstance().init(config);
	}

	public static ImageLoaderUtils getInstance() {
		if (mInstance == null)
			mInstance = new ImageLoaderUtils();
		return mInstance;
	}

	/**
	 * 图片地址类型
	 */
	public static enum ImageUriType {
		FROM_WEB("http://", "http://site.com/image.png", "来自网络"), FROM_SD_CARD(
				"file:///", "file:///mnt/sdcard/image.png", "来自SD卡"), FROM_SD_CARD_VIDEO_THUMB(
				"file:///", "file:///mnt/sdcard/video.mp4", "来自SD卡的视频缩略图"), FROM_CONTENT_PROVIDER(
				"content://", "content://media/external/images/media/13",
				"来自content provider"), FROM_CONTENT_PROVIDER_THUMB(
				"content://", "content://media/external/video/media/13",
				"来自content provider的缩略图"), FROM_ASSETS("assets://",
				"assets://image.png", "来自assets"), FROM_DRAWABLE_NOT_NINE_PATCH(
				"drawable://", "drawable:// + R.drawable.img",
				"来自drawable并且不是.9图");
		public String prefix, example, tag;

		ImageUriType(String prefix, String example, String tag) {
			this.prefix = prefix;
			this.example = example;
			this.tag = tag;
		}
	}

	/**
	 * 根据图片类型创建URI
	 * 
	 * @param relativePath
	 *            图片原来的相对地址，不包含文件分隔符/
	 * @param uriType
	 * @return
	 */
	public static String createUriByImageType(String relativePath,
			ImageUriType uriType) {
		return uriType.prefix + relativePath;
	}

	/**
	 * 创建显示图片属性
	 * 
	 * @param loadingId
	 *            加载时的图片id
	 * @param emptyId
	 *            服务端图片为空时的图片id
	 * @param failId
	 *            加载失败时的图片id
	 * @return
	 */
	public DisplayImageOptions createDisplayOptions(int loadingId, int emptyId,
			int failId) {
		return new DisplayImageOptions.Builder().showImageOnLoading(loadingId)
				.showImageForEmptyUri(emptyId).showImageOnFail(failId)
				.resetViewBeforeLoading(false).delayBeforeLoading(10)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.displayer(new SimpleBitmapDisplayer()).handler(new Handler())
				.build();
	}

	/**
	 * 创建显示图片属性，可以根据业务需求，添加自己的图片
	 * 
	 * @return
	 */
	public DisplayImageOptions createDisplayOptions() {
		return createDisplayOptions(R.drawable.icon_transpt,
				R.drawable.icon_transpt, R.drawable.icon_transpt);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 *            图片的URI
	 * @param imgView
	 *            图片控件
	 */
	public void loadImage(String uri, final ImageView imgView) {
		loadImage(uri, imgView, 0, 0, -1, null);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 *            图片的URI
	 * @param imgView
	 *            图片控件
	 * @param width
	 *            图片宽度，若<=0，忽略该参数
	 * @param height
	 *            图片高度，若<=0，忽略该参数
	 */
	public void loadImage(String uri, final ImageView imgView, int width,
			int height) {
		loadImage(uri, imgView, width, height, -1, null);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 *            图片的URI
	 * @param imgView
	 *            图片控件
	 * @param width
	 *            图片宽度，若<=0，忽略该参数
	 * @param height
	 *            图片高度，若<=0，忽略该参数
	 * @param defImgId默认图片id
	 */
	public void loadImage(String uri, final ImageView imgView, int width,
			int height, int defImgId) {
		loadImage(uri, imgView, width, height, defImgId, null);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 *            图片的URI
	 * @param imgView
	 *            图片控件
	 * @param width
	 *            图片宽度，若<=0，忽略该参数
	 * @param height
	 *            图片高度，若<=0，忽略该参数
	 * @param defImgId默认图片id
	 * @param options
	 *            图片显示属性，请通过 {@link ImageLoaderUtils#createDisplayOptions()}
	 *            方法进行创建，若为null，则忽略
	 */
	public void loadImage(String uri, final ImageView imgView, int width,
			int height, int defImgId, DisplayImageOptions options) {
		if (TextUtils.isEmpty(uri) || imgView == null)
			return;
		if (defImgId != -1 && defImgId != 0) {
			try {
				imgView.setImageResource(defImgId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (options == null) {
			if (width <= 0 || height <= 0) {
				ImageLoader.getInstance().displayImage(uri, imgView);
			} else {
				SimpleImageLoadingListener l = new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						imgView.setImageBitmap(loadedImage);
					}
				};
				ImageLoader.getInstance().loadImage(uri,
						new ImageSize(width, height), l);
			}

		} else {
			if (width <= 0 || height <= 0) {
				ImageLoader.getInstance().displayImage(uri, imgView, options);
			} else {
				SimpleImageLoadingListener l = new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						imgView.setImageBitmap(loadedImage);
					}
				};
				ImageLoader.getInstance().loadImage(uri,
						new ImageSize(width, height), options, l);
			}
		}
	}

	/**
	 * 清除缓存
	 */
	public void cleanCache() {
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
	}

	/**
	 * 释放单例
	 */
	public void release() {
		cleanCache();
		mInstance = null;
	}

	/**
	 * 获取磁盘缓存大小,此方法可能比较耗时，请另启线程计算
	 * 
	 * @return 单位为MB
	 */
	public double getDiskCacheSize() {
		try {
			File dir = ImageLoader.getInstance().getDiskCache().getDirectory();
			return getDirectorySize(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取内存缓存大小
	 * 
	 * @return 单位为MB
	 */
	public double getMemoryCacheSize() {
		try {
			Collection<String> keys = ImageLoader.getInstance()
					.getMemoryCache().keys();
			double size = 0;
			if (keys == null || keys.size() == 0)
				return size;
			for (String key : keys) {
				Bitmap bmp = ImageLoader.getInstance().getMemoryCache()
						.get(key);
				size += bmp.getRowBytes() * bmp.getHeight();
			}
			return size / 1024.0 / 1024.0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * 获取缓存大小
	 * 
	 * @return 单位为MB
	 */
	public double getCacheSize() {
		double diskCache = getDiskCacheSize();
		double memoryCache = getMemoryCacheSize();
		double cacheSize = diskCache + memoryCache;
		return cacheSize;
	}

	/**
	 * 获取文件目录大小
	 * 
	 * @param directory
	 *            文件目录
	 * @return 该目录的大小，单位为M
	 */
	private double getDirectorySize(File directory) {
		if (directory.exists()) {
			if (directory.isDirectory()) {
				File[] files = directory.listFiles();
				double size = 0;
				for (File f : files)
					size += getDirectorySize(f);
				return size;
			} else {
				// 计算journal文件大小会很大，所以忽略次文件
				if (directory.getName().contains("journal"))
					return 0;
				else {
					double size = (double) directory.length() / 1024.0 / 1024.0;
					return size;
				}
			}
		} else {
			return 0.0;
		}
	}
}
