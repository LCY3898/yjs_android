package com.cy.yigym.db;

import com.cy.yigym.db.table.T_IMChatMsg;
import com.cy.yigym.db.table.T_IMRecentMsg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Caiyuan Huang
 * <p>
 * 2014-12-01
 * </p>
 * <p>
 * 数据库操作者
 * </p>
 */
public class DbHelper extends BaseDbHelper {
	private static DbHelper mInstance = null;

	public static DbHelper getInstance(Context context) {
		if (mInstance == null) {
			synchronized (DbHelper.class) {
				if (mInstance == null) {
					mInstance = new DbHelper(context);
				}
			}
		}
		return mInstance;
	}

	public DbHelper(Context context) {
		super(context);
	}

	@Override
	public void createTable(SQLiteDatabase db) {
		// 创建IM聊天相关表
		createTable(db, T_IMChatMsg.class, T_IMChatMsg.msgSender,
				T_IMChatMsg.msgReceiver, T_IMChatMsg.msgTime);
		//创建最近聊天消息列表
		createTable(db, T_IMRecentMsg.class, T_IMRecentMsg.chatUid);
	}

	/**
	 * 释放单例
	 */
	public void realse() {
		mInstance = null;
	}

}
