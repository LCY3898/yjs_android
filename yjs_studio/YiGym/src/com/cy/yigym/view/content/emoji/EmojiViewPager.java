package com.cy.yigym.view.content.emoji;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.view.content.CustomAutoSildeViewPager;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-5
 * </p>
 * <p>
 * 表情分页控件
 * </p>
 */
public class EmojiViewPager extends LinearLayout {
	private int perPageColumns = 7;
	private int perPageLines = 3;
	private int emojiSizeDp = EmojiConstance.DEFAULT_EMOJI_ICON_SIZE_DP;
	private int deleteDrawableId = EmojiConstance.DELETE_EMOJI_BUTTON_DRAWABLE_ID;
	private int verticalSpacing = dpToPx(20);
	private int paddingTop = dpToPx(20);
	private int paddingBottom = paddingTop;
	private int paddingLeft = dpToPx(10);
	private int paddingRight = paddingLeft;
	private CustomAutoSildeViewPager vPager;
	private ArrayList<EmojiEntity> emojis = new ArrayList<EmojiViewPager.EmojiEntity>();
	private ArrayList<ArrayList<EmojiEntity>> emojisInner = new ArrayList<ArrayList<EmojiEntity>>();
	private ArrayList<GridView> emojiViews = new ArrayList<GridView>();
	private ArrayList<EmojiAdapter> emojiAdapters = new ArrayList<EmojiViewPager.EmojiAdapter>();
	private OnEmojiItemClickListener onEmojiItemClickListener;
	private OnEmojiDeleteClickListener onEmojiDeleteClickListener;

	public EmojiViewPager(Context context) {
		super(context);
		init();
	}

	public EmojiViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(
				R.layout.view_emoji_viewpager, this, true);
		vPager = (CustomAutoSildeViewPager) findViewById(R.id.vPager);
		vPager.setDotsGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		vPager.setDotsMargins(-1, -1, -1, 6);
	}

	/**
	 * 设置表情图片的大小
	 * 
	 * @param sizeDp
	 */
	public void setEmojiSize(int sizeDp) {
		this.emojiSizeDp = sizeDp;
	}

	/**
	 * 设置表情
	 * 
	 * @param emojis
	 */
	public void setEmojis(ArrayList<EmojiEntity> emojis) {
		this.emojisInner.clear();
		this.emojiAdapters.clear();
		this.emojiViews.clear();
		this.emojis.addAll(emojis);
		int perPageEmojis = perPageColumns * perPageLines - 1;// 减去1,是因为最后一个为删除按钮
		int pages = (int) Math.ceil(emojis.size() * 1.0 / perPageEmojis);
		int index = 0;
		for (int i = 0; i < pages; i++) {
			ArrayList<EmojiEntity> page = new ArrayList<EmojiViewPager.EmojiEntity>();
			for (int j = 0; j < perPageEmojis; j++) {
				if (index < emojis.size()) {
					page.add(emojis.get(index));
					index++;
				} else
					break;
			}
			page.add(new EmojiEntity("", deleteDrawableId));
			emojisInner.add(page);
			createPageView(i);
		}
		// 解决ViewPager全屏显示的问题
		int vPagerHeight = paddingTop + paddingBottom + perPageLines
				* dpToPx(emojiSizeDp) + perPageLines * verticalSpacing;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, vPagerHeight);
		vPager.setItemViews(emojiViews);
		vPager.setLayoutParams(lp);
	}

	/**
	 * 创建页视图
	 * 
	 * @param pageIndex
	 * @return
	 */
	private GridView createPageView(int pageIndex) {
		GridView view = new GridView(getContext());
		EmojiAdapter adapter = new EmojiAdapter(getContext(),
				emojisInner.get(pageIndex));
		view.setAdapter(adapter);
		emojiAdapters.add(adapter);
		view.setNumColumns(perPageColumns);
		view.setBackgroundColor(Color.TRANSPARENT);
		view.setVerticalSpacing(verticalSpacing);
		view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		view.setCacheColorHint(0);
		view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		view.setSelector(new ColorDrawable(Color.TRANSPARENT));
		view.setGravity(Gravity.CENTER);
		emojiViews.add(view);
		return view;
	}

	public static interface OnEmojiItemClickListener {
		void onEmojiItemClick(EmojiEntity emoji);
	}

	public static interface OnEmojiDeleteClickListener {
		void onEmojiDeleteClick();
	}

	/**
	 * 设置表情点击监听事件
	 * 
	 * @param l
	 */
	public void setOnEmojiItemClickListener(OnEmojiItemClickListener l) {
		this.onEmojiItemClickListener = l;
	}

	/**
	 * 设置删除表情按钮点击监听事件
	 * 
	 * @param l
	 */
	public void setOnEmojiDeleteClickListener(OnEmojiDeleteClickListener l) {
		this.onEmojiDeleteClickListener = l;
	}

	public static class EmojiEntity {
		public String emojiKey;
		public int drawableId;

		public EmojiEntity(String emojiKey, int drawableId) {
			super();
			this.emojiKey = emojiKey;
			this.drawableId = drawableId;
		}

	}

	private class EmojiAdapter extends AdapterBase<EmojiEntity> {
		public EmojiAdapter(Context context, List<EmojiEntity> list) {
			super(context, list);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected View getItemView(int position, View convertView,
				ViewGroup parent, final EmojiEntity entity) {
			ImageView item = null;
			if (convertView == null) {
				item = new ImageView(mContext);
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						dpToPx(emojiSizeDp), dpToPx(emojiSizeDp));
				item.setLayoutParams(lp);
				item.setScaleType(ScaleType.FIT_XY);
				convertView = item;
			} else {
				item = (ImageView) convertView;
			}
			if (entity.drawableId == deleteDrawableId) {
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (onEmojiDeleteClickListener != null)
							onEmojiDeleteClickListener.onEmojiDeleteClick();
					}
				});

			} else {
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (onEmojiItemClickListener != null)
							onEmojiItemClickListener.onEmojiItemClick(entity);
					}
				});

			}
			item.setBackgroundResource(entity.drawableId);
			return item;
		}

	}

	/**
	 * 将dp转换成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	private int dpToPx(float dp) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
