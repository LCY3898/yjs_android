package com.cy.widgetlibrary.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Caiyuan Huang
 * <p>
 * 2014-12-17
 * </p>
 * <p>
 * 绑定控件注解类,属性名和控件id要一致
 * </p>
 */
@Target(ElementType.FIELD)
// 表示用在字段上
@Retention(RetentionPolicy.RUNTIME)
// 表示在生命周期是运行时
public @interface BindView {

}
