package com.cy.widgetlibrary.base;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Caiyuan Huang
 * <p>
 * tangtaotao add method isActivityVisible,finishActivity,isAppInFront
 * Activity管理器，用于完全退出程序
 * </p>
 */
public class ActivityManager {
	private static ActivityManager mInstance = null;
	
	/**
	 * 使用弱引用，若Activity被回收，则弱引用指针返回空
	 */
	private List< WeakReference<BaseFragmentActivity> > listActivity = 
						new ArrayList< WeakReference<BaseFragmentActivity> >();

	public static ActivityManager getInstance() {
		if (mInstance == null)
			mInstance = new ActivityManager();
		return mInstance;
	}

	/**
	 * 添加activity
	 * 在BaseFragmentActivity自动调用，程序中不应该调用此方法
	 * @param activity
	 */
	void addActivity(BaseFragmentActivity activity) {
		if (listActivity == null || activity == null)
			return;
		
		boolean needDoClean = false;
		boolean found = false;
		for(WeakReference<BaseFragmentActivity> weakPtr:listActivity) {
			BaseFragmentActivity act = weakPtr.get();
			if(act == null) {
				needDoClean = true;
			} else if(act == activity) {
				found = true;//already in list, should not add again
				break;
			}
		}
		if(needDoClean) {
			recycleEmpty();
		}
		if(!found) {
			listActivity.add(new WeakReference<BaseFragmentActivity>(activity));
		}		
	}
	
	/**
	 * 移除activity
	 * 在BaseFragmentActivity自动调用，程序中不应该调用此方法
	 * 如果有多个同一class的Activity实例，则移除最近压入的那个
	 * @param activity
	 */
	void removeActivity(BaseFragmentActivity activity) {
		if (listActivity == null || activity == null)
			return;
		
		boolean needDoClean = false;		
		WeakReference<BaseFragmentActivity> weakPtr;
		int size = listActivity.size();
		for(int i=size-1;i>=0;i--) {
			weakPtr = listActivity.get(i);
			BaseFragmentActivity act = weakPtr.get();
			if(act == null) { //是否为已失效的引用
				needDoClean = true;
			} else if(act == activity) {
				listActivity.remove(weakPtr);//找到匹配,移除
				break;  //important!! 如果不break掉,由于list执行了remove操作，会导致concurrent exception.
			}
		}
		
		if(needDoClean) {
			recycleEmpty();
		}
	}
	
	
	/**
	 * 在列表中删除已经失效的弱引用索引
	 */
	private void recycleEmpty() {
		boolean needClear = true;
		do {
			needClear = false;//important!! 如果不设置会变成死循环
			for(WeakReference<BaseFragmentActivity> weakPtr:listActivity) {
				BaseFragmentActivity act = weakPtr.get();
				if(act == null) {
					needClear = true;
					listActivity.remove(weakPtr);
					break;
				}
			}
		} while(needClear);
	}

	
	
	/**
	 * 判断activity是否在前台
	 * @param cls
	 * @return
	 */
	public boolean isActivityVisible(Class<? extends BaseFragmentActivity> cls) {
		for(WeakReference<BaseFragmentActivity> weakPtr:listActivity) {
			BaseFragmentActivity act = weakPtr.get();
			if(act != null) {
				String actName = act.getClass().getName();
				if(actName.equals(cls.getName())) {
					return act.isActivityVisible();
				}
			}
		}
		return false;
	}
	

	public BaseFragmentActivity getTopAty() {
		WeakReference<BaseFragmentActivity> weakPtr;
		int size = listActivity.size();
		//如果app运行在前台,通常最后加进去的Activity都是当前可见的Activity,因此逆序可以加快命中速度
		for(int i=size-1;i>=0;i--) {
			weakPtr = listActivity.get(i);
			BaseFragmentActivity act = weakPtr.get();
			if(act != null) {
				return act;
			}
		}
		return null;
	}

	/**
	 * 判断app是否在前台运行
	 * @return
	 */
	public boolean isAppInFront() {
		WeakReference<BaseFragmentActivity> weakPtr;
		int size = listActivity.size();
		//如果app运行在前台,通常最后加进去的Activity都是当前可见的Activity,因此逆序可以加快命中速度
		for(int i=size-1;i>=0;i--) {
			weakPtr = listActivity.get(i);
			BaseFragmentActivity act = weakPtr.get();
			if(act != null) {
				if(act.isActivityVisible()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 结束指定Activity，如果有多个同一class的Activity实例，则结束最近打开的那个
	 * @param cls
	 */
	public void finishActivity(Class<? extends BaseFragmentActivity> cls) {
		int size = listActivity.size();
		//逆序以结束最近push的一个activity实例
		for(int i=size-1;i>=0;i--) {
			BaseFragmentActivity act = listActivity.get(i).get();
			if(act != null) {
				String actName = act.getClass().getName();
				if(actName.equals(cls.getName())) {
					act.finish();
					break;
				}
			}
		}
	}
	
	/**
	 * 退出所有的activity
	 */
	public void exitApplication() {
		//若在非UI中调用,UI线程和当前线程可能并发交错执行，activity finish后会在UI线程执行onDestroy，进而执行removeActivity,
		//此时listActivity列表内容会发生变化,使用for循环可能出现concurrent exception
		//另一种情况: 若finish同步执行到onDestroy,也会执行到removeActivity，此时也可能导致for循环concurrent exception
		List<WeakReference<BaseFragmentActivity>> lists = new ArrayList<WeakReference<BaseFragmentActivity>>();
		lists.addAll(listActivity);
		for (WeakReference<BaseFragmentActivity> weakPtr : lists) {
			BaseFragmentActivity a = weakPtr.get();
			if(a != null) {
				a.finish();
			}
		}
	}

	/**
	 * 结束当前activity之前的所有activity
	 */
	public void finishAtyBeforeThis(){
		for(int i=listActivity.size()-2;i>=0;i--){
			BaseFragmentActivity a=listActivity.get(i).get();
			if(a!=null){
				a.finish();
			}
		}
	}
}
