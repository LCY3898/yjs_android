package com.cy.yigym.db.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.cy.yigym.db.DbHelper;
import com.cy.yigym.utils.AppUtils;

/**
 * Caiyuan Huang
 * <p>
 * 2015-3-31
 * </p>
 * <p>
 * 数据库操作基类升级版
 * </p>
 */
public abstract class BaseDao {
	protected DbHelper mDbHelper = null;
	protected SQLiteDatabase db = null;
	protected Context mContext = null;
	private String tableName = "";

	public BaseDao(String tableName) {
		this.tableName = tableName;
		mContext = AppUtils.getAppContext();
		mDbHelper = DbHelper.getInstance(mContext);
		db = DbHelper.getInstance(mContext).getWritableDatabase();
	}

	/**
	 * 
	 * 添加
	 * 
	 * @param tableName
	 *            表名
	 * 
	 * @param entity
	 *            数据对象
	 * @return 添加成功返回true，失败返回false
	 */
	protected boolean insert(String tableName, Object entity) {
		boolean isSuccess = false;

		try {
			if (!db.isOpen()) {
				db = mDbHelper.getWritableDatabase();
			}
			db.beginTransaction();
			long id = db.insertOrThrow(tableName, null,
					DbHelper.getInstance(mContext).getContentValues(entity));
			isSuccess = id != -1 ? true : false;
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			// db.close();
		}
		return isSuccess;

	}

	protected boolean insert(Object entity) {
		return insert(tableName, entity);
	}

	/**
	 * 删除
	 * 
	 * @param tableName
	 *            表名
	 * @param whereClause
	 *            判断条件
	 * @param whereArgs
	 *            判断条件的值数据
	 * @return 删除成功返回true，失败返回false
	 */
	protected boolean delete(String tableName, String whereClause,
			String[] whereArgs) {
		boolean isSuccess = false;
		try {
			if (!db.isOpen()) {
				db = mDbHelper.getWritableDatabase();
			}
			db.beginTransaction();
			int id = db.delete(tableName, whereClause, whereArgs);
			isSuccess = id > 0 ? true : false;
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			// db.close();
		}
		return isSuccess;
	}

	protected boolean delete(String whereClause, String[] whereArgs) {
		return delete(tableName, whereClause, whereArgs);
	}

	/**
	 * 查询数据,只要查询条件和条件值有一个为空，则会返回这张表的所有数据
	 * 
	 * @param tableName
	 *            表明
	 * @param whereClauseSql
	 *            查询条件
	 * @param whereArgs
	 *            sql条件语句对应的条件值数组
	 * @param retObClass
	 *            返回的实体类型的类
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> ArrayList<T> query(String tableName, String whereClause,
			String[] whereArgs, Class<T> retObClass) {
		ArrayList<T> listData = new ArrayList<T>();
		Cursor mCursor = null;
		try {
			if (!db.isOpen()) {
				db = mDbHelper.getWritableDatabase();
			}
			String whereClauseSql = "";
			if (!TextUtils.isEmpty(whereClause) && whereArgs != null
					&& whereArgs.length != 0)
				whereClauseSql = String.format("select * from %s where ",
						tableName) + whereClause;
			else
				whereClauseSql = String.format("select * from %s", tableName);
			mCursor = db.rawQuery(whereClauseSql, whereArgs);
			while (mCursor.moveToNext()) {
				T entity = (T) DbHelper.getInstance(mContext)
						.createObjectFromCursor(mCursor, retObClass);
				listData.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
			// db.close();
		}
		return listData;
	}

	/**
	 * 查询数据,只要查询条件和条件值有一个为空，则会返回这张表的所有数据
	 * 
	 * @param tableName
	 *            表明
	 * @param whereClauseSql
	 *            查询条件
	 * @param whereArgs
	 *            sql条件语句对应的条件值数组
	 * @param retObClass
	 *            返回的实体类型的类
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> ArrayList<T> query2(String tableName, String whereClause,
			String[] whereArgs, Class<T> retObClass) {
		ArrayList<T> listData = new ArrayList<T>();
		Cursor mCursor = null;
		try {
			if (!db.isOpen()) {
				db = mDbHelper.getWritableDatabase();
			}
			String whereClauseSql = "";
			whereClauseSql = String.format("select * from %s ", tableName)
					+ whereClause;
			mCursor = db.rawQuery(whereClauseSql, whereArgs);
			while (mCursor.moveToNext()) {
				T entity = (T) DbHelper.getInstance(mContext)
						.createObjectFromCursor(mCursor, retObClass);
				listData.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
			// db.close();
		}
		return listData;
	}

	protected <T> ArrayList<T> query(String whereClause, String[] whereArgs,
			Class<T> retObClass) {
		return query(tableName, whereClause, whereArgs, retObClass);
	}

	/**
	 * 更新记录
	 * 
	 * @param tableName
	 *            表名
	 * @param entity
	 *            记录对象
	 * @param whereClause
	 *            条件语句
	 * @param whereArgs
	 *            条件值
	 * @return true表示更新成功，false表示更新失败
	 */
	protected boolean update(String tableName, Object entity,
			String whereClause, String[] whereArgs) {
		boolean isSuccess = false;
		// synchronized (mDbHelper) {
		try {
			if (!db.isOpen()) {
				db = mDbHelper.getWritableDatabase();
			}
			db.beginTransaction();
			ContentValues values = DbHelper.getInstance(mContext)
					.getContentValues(entity);
			long id = db.update(tableName, values, whereClause, whereArgs);
			isSuccess = id > 0 ? true : false;
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			// db.close();
		}
		return isSuccess;
		// }
	}

	/**
	 * 执行含有占位符的sql语句
	 * 
	 * @param sql
	 * @param bindArgs
	 * @return
	 */
	protected boolean exec(String sql, Object[] bindArgs) {
		boolean isSuccess = false;
		// synchronized (mDbHelper) {
		try {
			if (!db.isOpen()) {
				db = mDbHelper.getWritableDatabase();
			}
			db.beginTransaction();
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			// db.close();
		}
		return isSuccess;
	}

	protected boolean update(Object entity, String whereClause,
			String[] whereArgs) {
		return update(tableName, entity, whereClause, whereArgs);
	}

	public void realse() {
		try {
			if (db.isOpen())
				db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
