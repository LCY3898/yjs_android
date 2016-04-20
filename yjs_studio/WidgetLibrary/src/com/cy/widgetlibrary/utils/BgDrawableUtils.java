package com.cy.widgetlibrary.utils;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import com.cy.widgetlibrary.WidgetLibInitializer;
import com.cy.widgetlibrary.WidgetUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-3-23
 * </p>
 * <p>
 * 资源工具类
 * </p>
 */
public class BgDrawableUtils {
	/**
	 * 创建shape
	 * 
	 * @param solidColor
	 *            内部填充颜色
	 * @param roundRadius
	 *            圆角的角度,单位为dp,小于等于0表示不设置
	 * @param strokeWidth
	 *            边框大小,单位为dp,小于等于0表示不设置
	 * @param strokeColor
	 *            边框颜色
	 * @param gradientColors
	 *            渐变颜色,eg:{ 0xff255779 , 0xff3e7492, 0xffa6c0cd
	 *            }分别为开始颜色，中间夜色，结束颜色
	 * @return
	 */
	public static GradientDrawable creShape(int solidColor, int roundRadius,
			int strokeWidth, int strokeColor, int gradientColors[]) {
		GradientDrawable shape = null;
		if (gradientColors != null)
			shape = new GradientDrawable(
					GradientDrawable.Orientation.TOP_BOTTOM, gradientColors);
		else
			shape = new GradientDrawable();
		shape.setColor(solidColor);
		if (roundRadius > 0) {
			shape.setCornerRadius(WidgetUtils.dpToPx(roundRadius));
		}
		if (strokeWidth > 0)
			shape.setStroke(WidgetUtils.dpToPx(strokeWidth), strokeColor);
		return shape;
	}

	/**
	 * 创建shape
	 * 
	 * @param solidColor
	 *            内部填充颜色
	 * @param roundRadius
	 *            圆角的角度,单位为dp,数组为空或者长度<8则表示不设置,数组的角度的顺序为左上、右上、右下、左下，
	 *            其中两个float表示一个圆角半径
	 * @param strokeWidth
	 *            边框大小,单位为dp,小于等于0表示不设置
	 * @param strokeColor
	 *            边框颜色
	 * @param gradientColors
	 *            渐变颜色,eg:{ 0xff255779 , 0xff3e7492, 0xffa6c0cd
	 *            }分别为开始颜色，中间夜色，结束颜色
	 * @return
	 */
	public static GradientDrawable creShape(int solidColor,
			float[] roundRadius, int strokeWidth, int strokeColor,
			int gradientColors[]) {
		GradientDrawable shape = null;
		if (gradientColors != null)
			shape = new GradientDrawable(
					GradientDrawable.Orientation.TOP_BOTTOM, gradientColors);
		else
			shape = new GradientDrawable();
		shape.setColor(solidColor);
		float[] temp = new float[roundRadius.length];
		if (roundRadius != null && roundRadius.length > 0) {
			for (int i = 0; i < roundRadius.length; i++) {
				temp[i] = WidgetUtils.dpToPx(roundRadius[i]);
			}
			shape.setCornerRadii(temp);
		}
		if (strokeWidth > 0)
			shape.setStroke(WidgetUtils.dpToPx(strokeWidth), strokeColor);
		return shape;
	}

	/**
	 * 创建shape
	 * 
	 * @param solidColor
	 *            内部填充颜色
	 * @return
	 */
	public static GradientDrawable creShape(int solidColor) {
		return creShape(solidColor, 0, 0, 0, null);
	}

	/**
	 * 创建shape
	 * 
	 * @param solidColor
	 *            内部填充颜色
	 * @param roundRadius
	 *            圆角的角度,单位为dp,小于等于0表示不设置
	 * @return
	 */
	public static GradientDrawable creShape(int solidColor, float[] roundRadius) {
		return creShape(solidColor, roundRadius, 0, 0, null);
	}

	/**
	 * 创建shape
	 * 
	 * @param solidColor
	 *            内部填充颜色
	 * @param roundRadius
	 *            圆角的角度,单位为dp,小于等于0表示不设置
	 * @return
	 */
	public static GradientDrawable creShape(int solidColor, int roundRadius) {
		return creShape(solidColor, roundRadius, 0, 0, null);
	}

	/**
	 * 创建按钮按下状态的背景图片
	 * 
	 * @param bgNorId
	 *            正常状态下的图片资源id
	 * @param bgPreId
	 *            按下状态的图片资源id
	 * @return
	 */
	public static StateListDrawable crePressSelector(int bgNorId, int bgPreId) {
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[] { -android.R.attr.state_pressed },
				getRsDrawable(bgNorId));
		bg.addState(new int[] { android.R.attr.state_pressed },
				getRsDrawable(bgPreId));
		return bg;
	}

	/**
	 * 创建按钮按下状态的背景图片
	 * 
	 * @param bgNorId
	 *            正常状态下的图片资源id
	 * @param bgPreId
	 *            按下状态的图片资源id
	 * @return
	 */
	public static StateListDrawable crePressEnableSelector(
			int bgNorAndEnableId, int bgPreId) {
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[] { android.R.attr.state_enabled },
				getRsDrawable(bgNorAndEnableId));
		bg.addState(new int[] { -android.R.attr.state_enabled },
				getRsDrawable(bgPreId));

		return bg;
	}

	public static StateListDrawable crePressSelector(Drawable drawableNor,
			Drawable drawablePre) {
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[] { -android.R.attr.state_pressed }, drawableNor);
		bg.addState(new int[] { android.R.attr.state_pressed }, drawablePre);
		return bg;
	}

	public static StateListDrawable creCheckSelector(int bgNorId, int bgPreId) {
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[] { -android.R.attr.state_checked },
				getRsDrawable(bgNorId));
		bg.addState(new int[] { android.R.attr.state_checked },
				getRsDrawable(bgPreId));
		return bg;
	}

	/**
	 * 创建按钮按下状态的背景图片
	 * 
	 * @param bgNorId
	 *            正常状态下的图片资源id
	 * @param bgPreId
	 *            按下状态的图片资源id
	 * @roundRadius 圆角，单位为dp，小于等于0表示不设置
	 * @return
	 */
	public static StateListDrawable crePressSelector(int bgNorColor,
			int bgPreColor, int roundRadius) {
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[] { -android.R.attr.state_pressed },
				creShape(bgNorColor, roundRadius));
		bg.addState(new int[] { android.R.attr.state_pressed },
				creShape(bgPreColor, roundRadius));
		return bg;
	}

	/**
	 * 创建按钮按下状态的背景图片
	 * 
	 * @param bgNorId
	 *            正常状态下的图片资源id
	 * @param bgPreId
	 *            按下状态的图片资源id
	 * @param roundRadius
	 *            圆角的角度,单位为dp,数组为空或者长度<8则表示不设置,数组的角度的顺序为左上、右上、右下、左下，
	 *            其中两个float表示一个圆角半径
	 * @return
	 */
	public static StateListDrawable crePressSelector(int bgNorColor,
			int bgPreColor, float[] roundRadius) {
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[] { -android.R.attr.state_pressed },
				creShape(bgNorColor, roundRadius));
		bg.addState(new int[] { android.R.attr.state_pressed },
				creShape(bgPreColor, roundRadius));
		return bg;
	}

	/**
	 * 创建按下状态的color资源
	 * 
	 * @param norColor
	 * @param preColor
	 * @return
	 */
	public static ColorStateList crePressColorSelector(int norColor,
			int preColor) {
		return new ColorStateList(new int[][] {
				{ -android.R.attr.state_pressed },
				{ android.R.attr.state_pressed } }, new int[] { norColor,
				preColor });
	}

	public static Drawable getRsDrawable(int id) {
		return WidgetLibInitializer.getAppContext().getResources()
				.getDrawable(id);
	}

	/**
	 * 获取资源id
	 * 
	 * @param resName
	 * @param defType
	 * @return
	 */
	public static int getResId(String resName, String defType) {
		return WidgetLibInitializer
				.getAppContext()
				.getResources()
				.getIdentifier(resName, defType,
						WidgetLibInitializer.getAppContext().getPackageName());
	}

	/**
	 * 获取图片资源id
	 * 
	 * @param drawableName
	 * @return
	 */
	public static int getDrawableId(String drawableName) {
		return getResId(drawableName, "drawable");
	}

	/**
	 * 创建圆角图片
	 * 
	 * @param oldBitmap
	 *            原图
	 * @param radii
	 *            圆角的角度,单位为dp,数组为空或者长度<8则表示不设置,数组的角度的顺序为左上、右上、右下、左下，
	 *            其中两个float表示一个圆角半径
	 * @return
	 */
	public static Bitmap creRoundedDrawable(Bitmap oldBitmap, float[] radii) {
		if (radii == null || oldBitmap == null)
			return oldBitmap;
		if (radii.length != 8) {
			throw new IllegalArgumentException("radii数组的长度必须为8");
		}
		float[] temp = new float[radii.length];
		for (int i = 0; i < radii.length; i++) {
			temp[i] = WidgetUtils.dpToPx(radii[i]);
		}
		Bitmap newBmp = Bitmap.createBitmap(oldBitmap.getWidth(),
				oldBitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(newBmp);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, oldBitmap.getWidth(), oldBitmap.getHeight());
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		Path path = new Path();
		path.addRoundRect(rectF, temp, Path.Direction.CW);
		canvas.drawPath(path, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(oldBitmap, rect, rect, paint);
		if (!oldBitmap.isRecycled())
			oldBitmap.recycle();
		return newBmp;
	}

}
