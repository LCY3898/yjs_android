package com.cy.wheelview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import comcy.wheelviewlibrary.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-9
 * </p>
 * <p>
 * 横向滚轮选择器
 * </p>
 */
public class HoriWheelView extends LinearLayout {
	private int width = 0, height = 0;
	private WheelHorizontalView wheelHorizontalView;
	private ArrayList<String> datas = new ArrayList<String>();
	private WheelAdapter adapter;
	private OnViewAddCompleteListener onViewAddCompleteListener,
			onViewAddCompleteListener2,onViewAddCompleteListener3;
	private OnGlobalLayoutListener listener = null;
	private OnWheelChangedListener onWheelChangedListener;

	public HoriWheelView(Context context) {
		super(context);
		init();
	}

	public HoriWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	public WheelHorizontalView getWheel() {
		return wheelHorizontalView;
	}

	/**设置当前数值
	 * @param index
	 */
	public void setCurrentItem(final int index) {
		onViewAddCompleteListener3=new OnViewAddCompleteListener() {
			
			@Override
			public void onViewAddComplete() {
				wheelHorizontalView.setCurrentItem(index);				
			}
		};

	}

	private void init() {
		final ViewTreeObserver vto = this.getViewTreeObserver();
		listener = new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				height = HoriWheelView.this.getMeasuredHeight();
				width = HoriWheelView.this.getMeasuredWidth();

				addWheelView();
				HoriWheelView.this.getViewTreeObserver()
						.removeGlobalOnLayoutListener(listener);
			}
		};
		vto.addOnGlobalLayoutListener(listener);
	}

	private void addWheelView() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_hori_wheelview,
				this, true);
		wheelHorizontalView = (WheelHorizontalView) findViewById(R.id.vWheel);
		//TODO: tangtaotao add,temporary solution
//		ColorDrawable colorDrawable = new ColorDrawable(0xfff6b70c);
//		colorDrawable.setBounds(0, 0, dpToPx(1), 0);
//		wheelHorizontalView.setSelectionDivider(colorDrawable); // 新样式不用分隔符
		LayoutParams lp = new LayoutParams(width,
				height);
		wheelHorizontalView.setLayoutParams(lp);
		if (onViewAddCompleteListener != null)
			onViewAddCompleteListener.onViewAddComplete();
		if (onViewAddCompleteListener2 != null)
			onViewAddCompleteListener2.onViewAddComplete();
		if(onViewAddCompleteListener3!=null)
			onViewAddCompleteListener3.onViewAddComplete();

	}

	public class WheelAdapter extends AbstractWheelTextAdapter {
		private ArrayList<String> numbers = new ArrayList<String>();

		protected WheelAdapter(Context context) {
			super(context, R.layout.item_wheelview, NO_RESOURCE);
			setItemTextResource(R.id.text);
		}

		protected WheelAdapter(Context context, ArrayList<String> numbers) {
			super(context, R.layout.item_wheelview, NO_RESOURCE);
			setItemTextResource(R.id.text);
			this.numbers.clear();
			this.numbers.addAll(numbers);
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			TextView view = (TextView) super
					.getItem(index, convertView, parent);
			LayoutParams lp = new LayoutParams(
					(int) (width / 5.0), dpToPx(50));
			view.setLayoutParams(lp);
			return view;
		}

		@Override
		public int getItemsCount() {
			return numbers.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return numbers.get(index);
		}

	}

	private interface OnViewAddCompleteListener {
		void onViewAddComplete();
	}

	/**
	 * 将dp转换成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public int dpToPx(float dp) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * 设置数据集
	 * 
	 * @param datas
	 */
	public void setDatas(final ArrayList<String> datas) {
		if (datas == null || datas.size() == 0)
			return;
		onViewAddCompleteListener = new OnViewAddCompleteListener() {

			@Override
			public void onViewAddComplete() {
				HoriWheelView.this.datas.addAll(datas);
				adapter = new WheelAdapter(getContext(), datas);
				wheelHorizontalView.setViewAdapter(adapter);
			}
		};

	}

	/**
	 * 设置滑轮滚动监听
	 * 
	 * @param l
	 */
	public void setOnWheelChangedListener(OnWheelChangedListener l) {
		this.onWheelChangedListener = l;
	}

	private void priSetTxtSizeAndColor(final int[] textSp, final int[] textColor) {
		if (wheelHorizontalView.getVisibleItems() % 2 == 0) {
			throw new RuntimeException("可见条目数量为偶数，此方法不可用，请设置成奇数");
		}
		if (textSp == null || textColor == null)
			return;
		if (textSp.length != wheelHorizontalView.getVisibleItems()) {
			return;
		}
		if (textColor.length != wheelHorizontalView.getVisibleItems()) {
			return;
		}

		final String[] visiText = new String[textSp.length];

		wheelHorizontalView.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				if (onWheelChangedListener != null)
					onWheelChangedListener.onChanged(wheel, oldValue, newValue);
				try {
					int centerIndex = wheelHorizontalView.getCurrentItem();
					for (int i = 0; i < visiText.length; i++) {
						visiText[i] = isValidIndex(centerIndex - visiText.length
								/ 2 + i) ? datas.get(centerIndex - visiText.length
								/ 2 + i) : "";
					}
					int count = wheelHorizontalView.getItemParentView()
							.getChildCount();
					for (int i = 0; i < count; i++) {
						for (int j = 0; j < visiText.length; j++) {

							TextView txt = (TextView) wheelHorizontalView
									.getItemParentView().getChildAt(i);
							String text = txt.getText().toString();
							if (!TextUtils.isEmpty(visiText[j])
									&& text.equals(visiText[j])) {
								txt.setTextSize(TypedValue.COMPLEX_UNIT_SP,
										textSp[j]);
								txt.setTextColor(textColor[j]);
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 设置字体大小和颜色
	 * 
	 * @param textSp
	 * @param textColor
	 */
	public void setTxtSizeAndColor(final int[] textSp, final int[] textColor) {
		onViewAddCompleteListener2 = new OnViewAddCompleteListener() {

			@Override
			public void onViewAddComplete() {
				priSetTxtSizeAndColor(textSp, textColor);
			}
		};

	}

	private boolean isValidIndex(int index) {
		if (index < 0 || index >= datas.size())
			return false;
		return true;
	}
}
