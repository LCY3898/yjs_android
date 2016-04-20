package com.cy.yigym.view.content;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2014-12-1下午12:42:32
 * </p>
 * <p>
 * 自定义能够支持自定切换的ViewPager,可以用于广告轮播、首次引导页面
 * </p>
 */
public class CustomAutoSildeViewPager extends LinearLayout {
	private Context mContext;
	private ViewPager vPager;
	private RadioGroup rdgDot;
	private List<View> listItemViews = new ArrayList<View>();
	private CustomViewPagerAdapter adapter;
	private int currentItem = 0;
	private ScheduledExecutorService scheduledExecutorService;
	private int dotDrawableId = R.drawable.dot_selector;
	private int dotSizeDp = 8;
	private int dotMarginDp = 2;
	/**
	 * 是否已经开启了定时器
	 */
	private boolean isStartTimer = false;

	public CustomAutoSildeViewPager(Context context) {
		super(context);
		initView(context);
	}

	public CustomAutoSildeViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		LayoutInflater.from(context).inflate(
				R.layout.view_custom_auto_slide_viewpager, this, true);
		vPager = (ViewPager) findViewById(R.id.view_banner_vPager);
		rdgDot = (RadioGroup) findViewById(R.id.view_banner_rdgDot);
		adapter = new CustomViewPagerAdapter(listItemViews);
		vPager.setAdapter(adapter);
		vPager.setCurrentItem(0);
		vPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentItem = arg0;
				if (rdgDot.getChildAt(arg0) != null)
					rdgDot.check(rdgDot.getChildAt(arg0).getId());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		// 设置切换的间隔时间
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	}

	/**
	 * 设置视图集合，可用于设置banner或者引导界面,此方法更新使用重写设置Adapter来达到更新的目的，目前还没有找到更好的解决方案
	 * 
	 * @param itemViews
	 */
	public void setItemViews(List<? extends View> itemViews) {
		if (itemViews == null || itemViews.size() <= 0) {
			return;
		}
		listItemViews.clear();
		listItemViews.addAll(itemViews);
		adapter = new CustomViewPagerAdapter(listItemViews);
		vPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		createDotView();
	}

	/**
	 * 设置点集合的参数
	 * 
	 * @param resId
	 * @param sizeDp
	 * @param marginDp
	 */
	public void setDotParams(int resId, int sizeDp, int marginDp) {
		this.dotDrawableId = resId;
		this.dotSizeDp = sizeDp;
		this.dotMarginDp = marginDp;
	}

	/**
	 * 设置ImageView集合，一般用于设置banner
	 * 
	 * @param bitmaps
	 *            图片集合，有多少张图片，里面就生成多少个ImageView
	 * @param l
	 *            点击事件监听
	 */
	@Deprecated
	public void setImageViews(List<Bitmap> bitmaps, OnClickListener l) {
		if (bitmaps == null || bitmaps.size() <= 0) {
			return;
		}
		listItemViews.clear();
		for (Bitmap bitmap : bitmaps) {
			ImageView img = new ImageView(mContext);
			img.setImageDrawable(new BitmapDrawable(mContext.getResources(),
					bitmap));
			if (l != null)
				img.setOnClickListener(l);
			listItemViews.add(img);
		}
		adapter.notifyDataSetChanged();
		createDotView();

	}

	/**
	 * 追加item子集
	 * 
	 * @param urls
	 */
	@Deprecated
	public void appendItemViews(ArrayList<View> views) {
		if (views == null || views.size() == 0)
			return;
		listItemViews.addAll(views);
		adapter.notifyDataSetChanged();
		createDotView();
	}

	/**
	 * 清除item视图集合
	 */
	public void clearItemViews() {
		listItemViews.clear();
		adapter.notifyDataSetChanged();
		rdgDot.removeAllViews();
	}

	/**
	 * 设置当前item
	 * 
	 * @param item
	 */
	public void setCurrentItem(int item) {
		if (item < 0 || item >= listItemViews.size())
			return;
		vPager.setCurrentItem(item);
	}

	/**
	 * 设置页面改变监听器
	 * 
	 * @param l
	 */
	public void setOnPageChangeListener(OnPageChangeListener l) {
		vPager.setOnPageChangeListener(l);
	}

	/**
	 * 设置点集合的位置，默认在右下角，请使用{@link Gravity}
	 * 里面的属性值(eg:Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM)水平居中朝底下
	 * 
	 * @param gravity
	 */
	public void setDotsGravity(int gravity) {
		if (rdgDot == null)
			return;
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, gravity);
		rdgDot.setLayoutParams(lp);
	}

	/**
	 * 设置点集合的显示属性
	 * 
	 * @param visibility
	 */
	public void setDotsVisibility(int visibility) {
		rdgDot.setVisibility(visibility);
	}

	/**
	 * 设置点集合的边距属性，单位为dp，只有大于等于0的值才有效，否则设置为原来的
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setDotsMargins(int left, int top, int right, int bottom) {
		if (rdgDot == null)
			return;
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rdgDot
				.getLayoutParams();
		if (left >= 0)
			lp.leftMargin = dpToPx(left);
		if (top >= 0)
			lp.topMargin = dpToPx(top);
		if (right >= 0)
			lp.rightMargin = dpToPx(right);
		if (bottom >= 0)
			lp.bottomMargin = dpToPx(bottom);
		rdgDot.setLayoutParams(lp);
	}

	/**
	 * 开启自动切换功能
	 * 
	 * @param isOpen
	 *            true表示开启,false表示关闭
	 */
	public void openAutoSlide(boolean isOpen) {
		if (scheduledExecutorService == null)
			return;
		if (isOpen && isStartTimer == false) {
			scheduledExecutorService.scheduleWithFixedDelay(
					new ViewPagerRunnable(), 5, 5, TimeUnit.SECONDS);
			isStartTimer = true;
		} else if (isOpen == false && isStartTimer == true) {
			scheduledExecutorService.shutdown();
			isStartTimer = false;
		}

	}

	/**
	 * 创建banner的点集合
	 */
	private void createDotView() {
		if (rdgDot.getVisibility() == View.INVISIBLE
				|| rdgDot.getVisibility() == View.GONE)
			return;
		rdgDot.removeAllViews();
		for (int i = 0; i < listItemViews.size(); i++) {
			RadioButton rdb = new RadioButton(mContext);
			rdb.setButtonDrawable(android.R.color.transparent);
			rdb.setBackgroundResource(dotDrawableId);
			RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
					dpToPx(dotSizeDp), dpToPx(dotSizeDp));
			lp.setMargins(dpToPx(dotMarginDp), 0, dpToPx(dotMarginDp), 0);
			rdb.setLayoutParams(lp);
			rdb.setGravity(Gravity.CENTER);
			rdgDot.addView(rdb);

		}
		rdgDot.check(rdgDot.getChildAt(0).getId());
		if (listItemViews.size() == 1)
			rdgDot.setVisibility(View.GONE);
	}

	private final class CustomViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews = new ArrayList<View>();

		public CustomViewPagerAdapter(List<View> mListViews) {
			this.mListViews.clear();
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			try {
				container.removeView(mListViews.get(position));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	/**
	 * Caiyuan Huang
	 * <p>
	 * 2014-12-1下午12:45:58
	 * </p>
	 * <p>
	 * 定时切换banner的Runnable
	 * </p>
	 */
	private class ViewPagerRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			currentItem = (currentItem + 1) % listItemViews.size();
			handler.obtainMessage().sendToTarget();
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			vPager.setCurrentItem(currentItem);
		}

	};

	/**
	 * 将dp转换成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	private int dpToPx(float dp) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
