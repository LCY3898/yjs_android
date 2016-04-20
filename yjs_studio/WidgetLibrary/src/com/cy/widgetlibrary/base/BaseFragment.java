package com.cy.widgetlibrary.base;

import java.lang.reflect.Field;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;

/**
 * Caiyuan Huang
 * <p>
 * 2015-6-9
 * </p>
 * <p>
 * Fragment基类
 * </p>
 */
public abstract class BaseFragment extends Fragment {
	protected BaseFragmentActivity mActivity = null;
	protected Resources mResources = null;
	protected LayoutInflater mInflater = null;
	protected FragmentManager mFragmentManager = null;
	public String LOG_TAG = this.getClass().getSimpleName();
	protected View contentView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(getContentViewId(), null);
		// 防止多个fragment重叠在一个界面上的时候，点击事件会穿透。对AdapterView不能设置OnClick事件，否则直接抛出异常。
		if ((contentView instanceof AdapterView<?>) == false)
			contentView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});
		if (isAdded()) {
			mInflater = inflater;
			mActivity = (BaseFragmentActivity) getActivity();
			mResources = getResources();
		}
		if (isBindViewByAnnotation()) {
			bindView();
		}
		initView(contentView, inflater, container, savedInstanceState);
		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isAdded()) {
			mFragmentManager = mActivity.getSupportFragmentManager();
			initHeadViewData();
			initData(savedInstanceState);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 执行回退动作
	 */
	protected void finish() {
		if (mFragmentManager == null)
			return;
		if (mFragmentManager.getBackStackEntryCount() == 0 && mActivity != null) {
			// 针对没有压入栈的fragment
			mActivity.finish();

		} else {
			mFragmentManager.popBackStack();
		}
	}

	/**
	 * @tangtt 窗口焦点通知<br>
	 * 查找到嵌套的Fragment,并执行回调
	 * @param hasFocus
	 * 		是否聚焦
	 */
	void onFocusChange(boolean hasFocus) {
		onWindowFocusChanged(hasFocus);
		FragmentManager mgr = getChildFragmentManager();
		if(mgr != null) {
			List<Fragment> list = mgr.getFragments();
			if(list == null || list.size() == 0) {
				return;
			}
			for(Fragment frag:list) {
				//判断是否为自身，防止不断递归
				if(frag != this && frag instanceof BaseFragment) {
					((BaseFragment) frag).onFocusChange(hasFocus);
				}
			}
		}
	}

	/**
	 * 窗口焦点改变
	 * 
	 * @param hasFocus
	 */
	public void onWindowFocusChanged(boolean hasFocus) {

	}

	/**
	 * 关闭目标fragment之上的所有fragment
	 * 
	 * @param targetFragment
	 */
	protected void finishAllAboveFragment(String targetFragment) {
		if (mFragmentManager == null)
			return;
		mFragmentManager.popBackStack(targetFragment, 0);
	}

	/**
	 * 关闭目标fragment之上的所有fragment及自己
	 * 
	 * @param targetFragment
	 */
	protected void finishAllAboveAndSelfFragment(String targetFragment) {
		if (mFragmentManager == null)
			return;
		mFragmentManager.popBackStack(targetFragment,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	/**
	 * 获取界面视图的layout的id，一定要在此处进行设置。
	 * 
	 * @return
	 */
	protected abstract int getContentViewId();

	/**
	 * 
	 * 采用注解方式绑定控件，此方法绑定{@link #getContentViewId()}指定视图里面且带有{@link BindView}注解的控件
	 */
	private void bindView() {

		Class<?> mClass = this.getClass();
		Field[] fields = mClass.getDeclaredFields();//
		// 获取只在此类中定义的属性，不包括继承的
		// Field[] fields = mClass.getFields();// 可以获取包括继承来的属性
		for (Field field : fields) {
			// 判断该字段是否含有BindView注解
			if (field.isAnnotationPresent(BindView.class)) {
				try {
					String id = field.getName();
					View view = findViewById(id);
					if (view != null) {
						field.setAccessible(true);
						field.set(this, view);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}

	}

	/**
	 * 是否通过注解{@link BindView}来绑定控件
	 * 
	 * @return
	 */
	protected abstract boolean isBindViewByAnnotation();

	/**
	 * 初始化控件
	 * 
	 * @param contentView
	 *            界面视图
	 * @param container
	 * @param savedInstanceState
	 */
	protected abstract void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	/**
	 * 初始化头部控件
	 * 
	 * @return
	 */
	protected void initHeadViewData() {
	};

	/**
	 * 初始化数据
	 */
	protected abstract void initData(Bundle savedInstanceState);

	/**
	 * 根据id找控件
	 * 
	 * @param id
	 * @return
	 */
	public <T extends View> T findViewById(int id) {
		return (T) contentView.findViewById(id);
	}

	/**
	 * 绑定view控件,{@link 请使用getContentViewName设置布局名称},id的命名规则为"布局名_控件的变量名"
	 * <p>
	 * 此方法性能较低，用{@link #findViewById(int)}方法更高效。
	 * 
	 * @param viewClass
	 *            控件的类名
	 * @param viewName
	 *            控件的id名称后缀
	 * @return
	 */

	private View findViewById(String viewName) {
		View view = findViewById(mResources.getIdentifier(viewName, "id",
				mActivity.getPackageName()));
		return view;
	}

}
