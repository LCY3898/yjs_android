package com.cy.widgetlibrary.base;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Caiyuan Huang
 * <p>
 * 2015-4-21
 * </p>
 * <p>
 * 自定义View基类
 * </p>
 */
public abstract class BaseView extends RelativeLayout {
	protected Context mContext;

	public BaseView(Context context) {
		super(context);
		init(context);
	}

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		LayoutInflater.from(context).inflate(getContentViewId(), this, true);
		bindView();
		initView();
	}

	/**
	 * 
	 * 采用注解方式绑定控件
	 */
	private void bindView() {
		try {
			Class<?> mClass = this.getClass();
			Field[] fields = mClass.getDeclaredFields();// 获取只在此类中定义的属性，不包括继承的
			// Field[] fields = mClass.getFields();//可以获取包括继承来的属性
			for (Field field : fields) {
				// 判断该字段是否含有BindView注解
				if (field.isAnnotationPresent(BindView.class)) {
					String id = field.getName();
					View view = findViewById(id);
					if (view != null) {
						field.setAccessible(true);
						field.set(this, view);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绑定view控件
	 * 
	 * @param viewName
	 *            控件的id名称后缀
	 * @return
	 */
	public View findViewById(String viewName) {
		View view = findViewById(getContext().getResources().getIdentifier(
				viewName, "id", getContext().getPackageName()));
		return view;
	}

	protected abstract void initView();

	protected abstract int getContentViewId();

}
