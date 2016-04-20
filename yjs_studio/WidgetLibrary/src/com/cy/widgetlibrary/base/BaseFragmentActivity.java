package com.cy.widgetlibrary.base;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.umeng.analytics.MobclickAgent;

/**
 * Caiyuan Huang
 * <p>
 * 2015-6-9
 * </p>
 * <p>
 * FragmentActivity基类
 * </p>
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
	public FragmentManager mFragmentManager = null;
	protected Context mContext = null;
	private Resources mResources = null;
	public String LOG_TAG = this.getClass().getSimpleName();
	protected LayoutInflater mInflater;
	private boolean isCanAddView = true;
	protected Activity mActivity;
	protected Handler mHandler = new Handler(Looper.getMainLooper());
	/**
	 * activity 是否可见
	 */
	public AtyState mAtyState = AtyState.INIT;

	public enum AtyState {
		INIT, CREATED, STARTED, RESUME, PAUSE, STOP, DESTORY;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		mActivity = this;
		mContext = this;
		mInflater = this.getLayoutInflater();
		mResources = mContext.getResources();
		mFragmentManager = getSupportFragmentManager();
		setContentView(getContentViewId());
		if (isBindViewByAnnotation()) {
			bindView();
		}
		initView();
		initData();

		// 加入到Activity 管理器
		ActivityManager.getInstance().addActivity(this);
		mAtyState = AtyState.CREATED;
	}

	/**
	 * 是否通过注解{@link BindView}来绑定控件
	 * 
	 * @return
	 */
	protected abstract boolean isBindViewByAnnotation();

	/**
	 * 获取内容视图id
	 * 
	 * @return
	 */
	protected abstract int getContentViewId();

	/**
	 * 初始化控件
	 */
	protected abstract void initView();

	/**
	 * 初始化数据
	 */
	protected abstract void initData();

	/**
	 * 获得内容区id,用于添加Fragment
	 * 
	 * @return
	 */
	public int getContentAreaId() {
		return 0;
	};

	/**
	 * 
	 * 采用注解方式绑定控件，此方法绑定{@link #getContentViewId()}指定视图里面且带有{@link BindView}注解的控件
	 */
	private void bindView() {
		Class<?> mClass = this.getClass();
		Field[] fields = mClass.getDeclaredFields();// 获取只在此类中定义的属性，不包括继承的
		// Field[] fields = mClass.getFields();//可以获取包括继承来的属性
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
	 * 在内容区添加fragment
	 * 
	 * @param areaId
	 *            区域id
	 * @param fragment
	 *            要添加的fragment
	 */
	public void addFragment(int areaId, Fragment fragment) {
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		mFragmentTransaction.add(areaId, fragment);
		mFragmentTransaction.commit();
	}

	/**
	 * 在内容区添加fragment
	 * 
	 * @param fragment
	 */
	public void addFragment(Fragment fragment) {
		addFragment(fragment, null, "", false);
	}

	/**
	 * 在内容区添加fragment
	 * 
	 * @param fragment
	 * @param isAllowQuickAdd
	 *            是否允许快速添加,true则可以直接添加,false要隔1秒之后添加才有效
	 */
	public void addFragment(Fragment fragment, boolean isAllowQuickAdd) {
		addFragment(getContentAreaId(), fragment, null, "", false,
				isAllowQuickAdd);
	}

	/**
	 * 在内容区添加Fragment
	 * 
	 * @param fragment
	 * @param bundle
	 *            额外数据
	 * @param isAnimation
	 *            是否要动画
	 */
	public void addFragment(Fragment fragment, Bundle bundle,
			boolean isAnimation) {
		addFragment(fragment, bundle, "", isAnimation);
	}

	/**
	 * 在内容区添加fragment，此时若tag不为空，则会加入到栈中
	 * 
	 * @param fragment
	 * @param tag
	 *            fragment的标记
	 * @param isAnimation
	 *            是否要动画
	 */
	public void addFragment(Fragment fragment, String tag, boolean isAnimation) {
		addFragment(fragment, null, tag, isAnimation);
	}

	/**
	 * 在内容区添加fragment，此时若tag不为空，则会加入到栈中
	 * 
	 * @param fragment
	 * @param bundle
	 *            额外数据
	 * @param tag
	 *            fragment的标记
	 * @param isAnimation
	 *            是否要动画
	 */
	public void addFragment(Fragment fragment, Bundle bundle, String tag,
			boolean isAnimation) {
		addFragment(getContentAreaId(), fragment, bundle, tag, isAnimation,
				false);
	}

	/**
	 * 移除fragment
	 * 
	 * @param fragment
	 */
	public void removeFragment(Fragment fragment) {
		try {
			FragmentTransaction mFragmentTransaction = mFragmentManager
					.beginTransaction();
			mFragmentTransaction.remove(fragment);
			mFragmentTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动activity
	 * 
	 * @param cls
	 *            目标activity的类名
	 * @param name
	 *            不定的参数值
	 */
	public void startActivity(Class<?> cls, BasicNameValuePair... name) {
		Intent intent = new Intent();
		intent.setClass(mContext, cls);
		for (int i = 0; i < name.length; i++) {
			intent.putExtra(name[i].getName(), name[i].getValue());
		}
		mContext.startActivity(intent);
		// overridePendingTransition(R.anim.slide_left_in,
		// R.anim.slide_left_out);
	}

	/**
	 * 添加fragment
	 * 
	 * @param areaId
	 *            添加的区域id
	 * @param fragment
	 *            要添加的fragment
	 * @param bundle
	 *            数据
	 * @param tag
	 *            标签，设置为非（“”或者null）则会加入回退栈
	 * @param isAnimation
	 *            是否开启动画效果
	 * @param isAllowQuickAdd
	 *            是否允许快速添加
	 */
	public void addFragment(int areaId, Fragment fragment, Bundle bundle,
			String tag, boolean isAnimation, boolean isAllowQuickAdd) {
		if (isAllowQuickAdd) {
			// 允许快速添加，直接添加
			priAddFragment(areaId, fragment, bundle, tag, isAnimation);
			return;
		}
		// 添加fragment之后1秒内不允许再添加，防止快速点击，会添加多个fragment
		if (isCanAddView) {
			isCanAddView = false;
			priAddFragment(areaId, fragment, bundle, tag, isAnimation);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					isCanAddView = true;
				}
			}, 1000);
		}
	}

	/**
	 * 添加fragment的实现方法
	 * 
	 * @param areaId
	 * @param fragment
	 * @param bundle
	 * @param tag
	 * @param isAnimation
	 */
	private void priAddFragment(int areaId, Fragment fragment, Bundle bundle,
			String tag, boolean isAnimation) {
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		if (isAnimation) {
			// mFragmentTransaction.setCustomAnimations(R.anim.slide_left_in,
			// R.anim.slide_left_out, R.anim.slide_right_in,
			// R.anim.slide_right_out);
		}
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
		mFragmentTransaction.add(areaId, fragment);
		if (!TextUtils.isEmpty(tag)) {
			mFragmentTransaction.addToBackStack(tag);
		}
		mFragmentTransaction.commit();
	}

	public boolean isActivityVisible() {
		return mAtyState == AtyState.RESUME;
	}

	public boolean isActivityDetroyed() {
		return mAtyState == AtyState.DESTORY;
	}

	/**
	 * 绑定view控件,{@link 请使用getContentViewName设置布局名称}"
	 * <p>
	 * 此方法性能较低，用{@link #findViewById(int)}方法更高效。
	 * 
	 * @param viewName
	 *            控件的id名称后缀
	 * @return
	 */
	public View findViewById(String viewName) {
		View view = findViewById(mResources.getIdentifier(viewName, "id",
				getPackageName()));
		return view;
	}

	/***
	 * 控制软键盘(隐藏-显示，显示-隐藏)
	 */
	public void ControlKeyboard() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 300);
	}

	/**
	 * 显示软键盘
	 */
	public void showKeyboard() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		}, 300);
	}

	/**
	 * 关闭软键盘
	 */
	public void closeKeyboard() {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public void onResume() {
		mAtyState = AtyState.RESUME;
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		mAtyState = AtyState.PAUSE;
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onStart() {
		mAtyState = AtyState.STARTED;
		super.onStart();
	}

	@Override
	protected void onStop() {
		mAtyState = AtyState.STOP;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		closeKeyboard();
		ActivityManager.getInstance().removeActivity(this);
		mAtyState = AtyState.DESTORY;
		super.onDestroy();
	}

	public void finish() {
		super.finish();
		// overridePendingTransition(R.anim.slide_right_in,
		// R.anim.slide_right_out);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		List<Fragment> frags = mFragmentManager.getFragments();
		if (frags == null || frags.size() == 0)
			return;
		int size = frags.size();
		for (int i = 0; i < size; i++) {
			Fragment fragment = frags.get(i);
			if (fragment instanceof BaseFragment) {
				BaseFragment baseFragment = (BaseFragment) fragment;
				//tangtt 需要找到所有子Fragment以及嵌套的Fragment
				baseFragment.onFocusChange(hasFocus);
			}
		}
	}
}
