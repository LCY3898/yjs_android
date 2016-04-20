package com.cy.yigym.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cy.imagelib.ImageCoverUtils;
import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.utils.BgDrawableUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-11-29
 * </p>
 * <p>
 * 自定义显示圆角图片的ImageView
 * </p>
 */
public class CustomRoundedDrawableImageView extends ImageView {
	private float[] radii = new float[] { 5, 5, 5, 5, 0, 0, 0, 0 };

	public CustomRoundedDrawableImageView(Context context) {
		super(context);
		init();
	}

	public CustomRoundedDrawableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 初始化代码
	 */
	private void init() {
		// 让在xml文件中设置的图片圆角起作用
		Drawable d = getDrawable();
		if (d != null) {
			Bitmap bmp = ImageCoverUtils.drawable2Bitmap(d);
			setImageBitmap(bmp);
		}

	}

	/**
	 * 设置圆角数组
	 * 
	 * @param radii
	 */
	public void setRadii(float[] radii) {
		this.radii = radii;
	}

	@Override
	public void setImageBitmap(Bitmap bm) {

		int width = WidgetUtils.getScreenWidth() - WidgetUtils.dpToPx(80);
		int bmpWidth = bm.getWidth();
		double scale = bmpWidth * 1.0 / width;
		int radius = Math.round((float) (5 * scale));
		for (int i = 0; i < 4; i++) {
			radii[i] = radius;
		}
		Bitmap bmp = BgDrawableUtils.creRoundedDrawable(bm, radii);
		int height = (int) (bmp.getHeight() * width * 1.0 / bmp.getWidth());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,
				height);
		setLayoutParams(lp);
		super.setImageBitmap(bmp);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
