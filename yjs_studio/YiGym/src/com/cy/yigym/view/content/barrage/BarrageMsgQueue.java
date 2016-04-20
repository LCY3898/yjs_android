package com.cy.yigym.view.content.barrage;

import java.util.Vector;

import com.cy.yigym.view.content.barrage.BarrageMsgBase.OnDeathListener;

public class BarrageMsgQueue {
	private int maxQueueSize = 5;
	private Vector<BarrageMsgBase> queue = new Vector<BarrageMsgBase>();
	private OnElementRemoveListener onElementRemoveListener;

	/**
	 * 元素移除监听
	 */
	public static interface OnElementRemoveListener {
		/**
		 * 被强制移除,当队列满的时候,加入一个新的元素,则队头会被强制移除
		 * 
		 * @param element
		 */
		void onForceRemove(BarrageMsgBase element);

		/**
		 * 自动移除,当元素完成生命周期,自动从队列里面移除
		 * 
		 * @param element
		 */
		void onAutoRemove(BarrageMsgBase element);
	}

	/**
	 * 设置元素移除监听器
	 * 
	 * @param l
	 */
	public void setOnElementRemoveListener(OnElementRemoveListener l) {
		this.onElementRemoveListener = l;
	}

	/**
	 * 加入元素,当队列满的时候会先移除队头元素,然后再加入到队列中
	 * 
	 * @param element
	 */
	public void put(final BarrageMsgBase element) {
		if (queue.size() >= maxQueueSize) {
			queue.get(0).death(false);
			BarrageMsgBase top = deepCopy(queue.get(0));
			queue.remove(0);
			if (onElementRemoveListener != null) {
				onElementRemoveListener.onForceRemove(top);
				top = null;
			}

		}
		element.attach();
		queue.add(element);
		element.setOnDeathListener(new OnDeathListener() {

			@Override
			public void onDeath() {
				BarrageMsgBase top = deepCopy(element);
				queue.remove(element);
				if (onElementRemoveListener != null) {
					onElementRemoveListener.onAutoRemove(top);
					top = null;
				}

			}
		});
	}

	/**
	 * 获取队列
	 * 
	 * @return
	 */
	public Vector<BarrageMsgBase> getQueue() {
		return queue;
	}

	/**
	 * 设置队列的最大容量
	 * 
	 * @param maxSize
	 */
	public void setMaxQueueSize(int maxSize) {
		this.maxQueueSize = maxSize;
	}

	/**
	 * 深拷贝元素对象
	 * 
	 * @param element
	 * @return
	 */
	private BarrageMsgBase deepCopy(BarrageMsgBase element) {
		if (element == null)
			return null;
		BarrageMsgBase newElement = element.clone();
		return newElement;
	}

}
