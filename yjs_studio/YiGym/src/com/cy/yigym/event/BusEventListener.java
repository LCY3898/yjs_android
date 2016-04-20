package com.cy.yigym.event;


public class BusEventListener {
	/**
	 * 在UI线程中执行onEvent*
	 * @param <T>
	 */
	public static interface MainThreadListener<T> {
		void onEventMainThread(T event);
	}
	
	/**
	 * 在后台线程中执行onEvent*
	 * 如果发送事件的线程是后台线程，会直接执行事件，
	 * 如果当前线程是UI线程，事件会被加到一个队列中，
	 * 由一个线程依次处理这些事件，如果某个事件处理时间太长，会阻塞后面的事件的派发或处理。
	 * @param <T>
	 */
	public static interface BgThreadListener<T> {
		void onEventBackgroundThread(T event);
	}	
	
	/**
	 * 在异步线程中执行onEvent*
	 * 事件处理会在单独的线程中执行，主要用于在后台线程中执行耗时操作，
	 * 每个事件会开启一个线程（有线程池），但最好限制线程的数目。
	 * 
	 * @param <T>
	 */
	public static interface AsyncListener<T> {
		void onEventAsync(T event);
	}
	
	/**
	 * 在发布事件的线程中执行onEvent*
	 * 
	 * @param <T>
	 */
	public static interface PostListener<T> {
		void onEvent(T event);
	}
}
