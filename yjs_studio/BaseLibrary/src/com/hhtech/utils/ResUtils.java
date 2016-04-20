package com.hhtech.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import com.hhtech.base.AppUtils;

public class ResUtils {
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
	 *            }分别为开始颜色，中间颜色，结束颜色
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
		if (roundRadius > 0)
			shape.setCornerRadius(DimenUtils.dpToPx(roundRadius));
		if (strokeWidth > 0)
			shape.setStroke(DimenUtils.dpToPx(strokeWidth), strokeColor);
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
	public static GradientDrawable creShape(int solidColor, int roundRadius) {
		return creShape(solidColor, roundRadius, 0, 0, null);
	}

	/**
	 * 创建按钮按下状态的背景图片
	 * 
	 * @param bgNorId
	 *            正常状态下的图片资源id,若为0则表示透明(transparent)
	 * @param bgPreId
	 *            按下状态的图片资源id,若为0则表示透明(transparent)
	 * @return
	 */
	public static StateListDrawable crePressSelector(int bgNorId, int bgPreId) {
		Drawable normal;
		Drawable press;
		if(bgNorId == 0) {
			normal = getColorDrawable(0x00000000);
		} else {
			normal = getRsDrawable(bgNorId);
		}
		
		if(bgPreId == 0) {
			press = getColorDrawable(0x00000000);
		} else {
			press = getRsDrawable(bgPreId);
		}
		
		return crePressSelector(normal,press);
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
	public static StateListDrawable crePressSelector(Drawable normal, Drawable press) {
		StateListDrawable bg = new StateListDrawable();

		
		 bg.addState(new int[]{android.R.attr.state_focused},
	                press);
	    
		 bg.addState(new int[]{android.R.attr.state_pressed},
	                press);
		 
		 bg.addState(new int[]{android.R.attr.state_selected},
	                press);
		 
		 bg.addState(new int[] {0}, normal);
	        
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
		return crePressSelector(creShape(bgNorColor, roundRadius), 
									creShape(bgPreColor, roundRadius));
	}

	
	/**
	 * 创建按钮选中状态的背景图片
	 * 
	 * @param bgNorId
	 *            正常状态下的图片资源id,若为0则表示透明(transparent)
	 * @param bgPreId
	 *            选中状态的图片资源id,若为0则表示透明(transparent)
	 * @return
	 */
	public static StateListDrawable creCheckSelector(int bgNorId, int bgPreId) {
		StateListDrawable bg = new StateListDrawable();
		if(bgPreId == 0) {
			bg.addState(new int[] { android.R.attr.state_checked },
					getColorDrawable(0x00000000));
		} else {
			bg.addState(new int[] { android.R.attr.state_checked },
					getRsDrawable(bgPreId));
		}
		
		if(bgNorId == 0) {
			bg.addState(new int[] {0},
					getColorDrawable(0x00000000));
		} else {
			bg.addState(new int[] {0},
					getRsDrawable(bgNorId));
		}
		return bg;
	}
	
	
	/**
	 * 根据颜色生成drawable
	 * @param color
	 * @return
	 */
	public static Drawable getColorDrawable(int color) {
		return new ColorDrawable(color);
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
				{ android.R.attr.state_focused },				
				{ android.R.attr.state_pressed },
				{ android.R.attr.state_selected },
				{ 0 }}, new int[] { preColor,preColor,preColor,norColor,
				});
	}

	public static Drawable getRsDrawable(int id) {
		return AppUtils.getAppContext().getResources().getDrawable(id);
	}

}
