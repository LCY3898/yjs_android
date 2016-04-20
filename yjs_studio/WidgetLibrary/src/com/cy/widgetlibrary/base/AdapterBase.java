package com.cy.widgetlibrary.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Caiyuan Huang
 * <p>
 * 2014-11-3下午3:59:01
 * </p>
 * <p>
 * 适配器基类
 * </p>
 */
public abstract class AdapterBase<T> extends BaseAdapter {
	protected List<T> mList = new ArrayList<T>();
	protected Context mContext;
	protected Resources mResources;
	protected LayoutInflater mInflater;

	public AdapterBase(Context context, List<T> list) {
		this.mContext = context;
		this.mList = (List<T>) list;
		this.mResources = mContext.getResources();
		this.mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 获取在适配器中的数据集合
	 * 
	 * @return
	 */
	public List<T> getList() {
		return mList;
	}

	/**
	 * 将集合添加至尾部
	 * 
	 * @param list
	 */
	public void appendListToBottom(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(list);
		notifyDataSetChanged();
	}

	/**
	 * 将集合添加到头部
	 * 
	 * @param list
	 */
	public void appendListToTop(List<T> list) {
		appendListToWhere(list, 0);
	}

	/**
	 * 将集合数据添加至列表的某一个位置
	 * 
	 * @param list
	 * @param position
	 *            集合将在这一个位置进行插入
	 */
	public void appendListToWhere(List<T> list, int position) {
		if (list == null || mList.get(position) == null)
			return;
		mList.addAll(position, list);
		notifyDataSetChanged();

	}

	/**
	 * 清除所有集合数据
	 */
	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mList == null || position > mList.size() - 1) {
			return null;
		}
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		T value = position >= mList.size() ? null : mList.get(position);
		View itemView = getItemView(position, convertView, parent, value);
		addOnItemInnerViewClickListeners(itemView, position, value);
		return itemView;
	}

	/**
	 * 获取item控件
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @param entity
	 * @return
	 */
	protected abstract View getItemView(int position, View convertView,
			ViewGroup parent, T entity);

	/**
	 * 获取缓存控件
	 * 
	 * @param convertView
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <E extends View> E getHolderView(View convertView, int id) {

		SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			convertView.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = convertView.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (E) childView;
	}

	public SparseArray<OnItemInnerViewClickListener> onItemInnerViewClickListeners;

	/**
	 * 添加item内部控件的点击事件
	 * 
	 * @param itemView
	 * @param position
	 * @param data
	 */
	private void addOnItemInnerViewClickListeners(final View itemView,
			final Integer position, final Object data) {
		if (onItemInnerViewClickListeners != null) {
			for (int i = 0; i < onItemInnerViewClickListeners.size(); i++) {
				Integer innerViewId = onItemInnerViewClickListeners.keyAt(i);
				final OnItemInnerViewClickListener l = onItemInnerViewClickListeners
						.get(innerViewId);
				View innerView = itemView.findViewById(innerViewId);
				if (innerView != null && l != null) {
					innerView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							l.onItemInnerViewClick(itemView, v,
									position, data);
						}
					});
				}

			}
		}
	}

	/**
	 * Item内部控件的点击事件
	 */
	public static interface OnItemInnerViewClickListener {
		/**
		 * item内部控件点击
		 * 
		 * @param itemView
		 *            item控件
		 * @param innerView
		 *            被点击的控件
		 * @param position
		 *            item所在的位置
		 * @param data
		 *            item绑定的数据
		 */
		public void onItemInnerViewClick(View itemView, View innerView,
				Integer position, Object data);
	}

	/**
	 * 设置item内部控件的点击监听事件
	 * 
	 * @param innerViewId
	 *            要监听的内部控件的id
	 * @param l
	 *            点击事件
	 */
	public void setOnItemInnerViewClickListener(Integer innerViewId,
			OnItemInnerViewClickListener l) {
		if (onItemInnerViewClickListeners == null)
			onItemInnerViewClickListeners = new SparseArray<AdapterBase.OnItemInnerViewClickListener>();
		onItemInnerViewClickListeners.put(innerViewId, l);
	}
}
