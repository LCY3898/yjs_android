package com.cy.yigym.db;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Caiyuan Huang
 * <p>
 * 2014-11-14上午11:11:01
 * </p>
 * <p>
 * Sqlite工具
 * </p>
 * <p>
 * 使用注意:<br>
 * 1.调用{@link #createTable}方法时，请不要将Class所在的类设置为内部类，因为在反射获取属性名的时候会多出一个class$0f字段。
 * 2.使用方法，{@code TableDemo为外部类，并且定义如下public class TableDemo public static final
 * String userId="userId"; public static final String userName="userName";
 * public static final String userPsw="userPsw"; }
 * ,记住属性名和属性值一定要一样,然后调用方法为：继承BaseDbHelper类，并实现createTable方法如下，createTable(db,
 * TableDemo.class, TableDemo.userId);}
 * </p>
 */
@SuppressWarnings("unused")
public abstract class BaseDbHelper extends SQLiteOpenHelper {
	private Context mContext;
	public final String LOG_TAG = this.getClass().toString();
	public static String DATABASE_NAME = "HCY.db";
	public static int DATABASE_VERSION = 1;
	/**
	 * 数据库的存储路径
	 */
	private String DATABASE_PATH = "";
	private final String FILE_SEPATATOR = File.separator.toString();

	public BaseDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
		// 初始化数据库存储路径
		DATABASE_PATH = FILE_SEPATATOR + "data"
				+ Environment.getDataDirectory().getAbsolutePath()
				+ FILE_SEPATATOR + context.getPackageName() + FILE_SEPATATOR
				+ "databases" + FILE_SEPATATOR;
	}

	/**
	 * 创建表，字段类型都为text，暂时不支持外键
	 * 
	 * @param db
	 * @param tableClass
	 *            表类名
	 * 
	 * @param priKey
	 *            主键字段，不定参数
	 * @return
	 */
	public boolean createTable(SQLiteDatabase db, Class<?> tableClass,
			String... priKey) {
		try {
			String[] aryPrikey = priKey;
			List<String> listPriKey = new ArrayList<String>();
			Field[] fields = tableClass.getDeclaredFields();
			for (int i = 0; i < aryPrikey.length; i++) {
				boolean isHave = false;
				for (int j = 0; j < fields.length; j++) {
					if (aryPrikey[i].equals(fields[j].getName())) {
						// 如果类中包含该主键字段
						listPriKey.add(aryPrikey[i]);
						isHave = true;
						break;
					}
				}
				if (isHave == false) {
					throw new RuntimeException("主键字段不是类中的属性字段");
				}

			}
			// 拼接主键字段
			StringBuilder strPriKey = new StringBuilder();
			strPriKey.append("primary key (");
			for (int i = 0; i < listPriKey.size(); i++) {
				strPriKey.append(String.format("%s,", listPriKey.get(i)));
			}
			strPriKey.deleteCharAt(strPriKey.length() - 1);
			strPriKey.append(")");
			// 拼接其它字段
			StringBuilder strNorKey = new StringBuilder();
			for (int i = 0; i < fields.length; i++) {
				strNorKey
						.append(String.format("%s text,", fields[i].getName()));
			}
			strNorKey.deleteCharAt(strNorKey.length() - 1);
			StringBuilder strSql = new StringBuilder();
			if (strPriKey.toString().equals("primary key )")) {
				// 如果主键为空
				strSql.append(String.format("create table %s(%s);",
						tableClass.getSimpleName(), strNorKey.toString()));
			} else {
				strSql.append(String.format("create table %s(%s,%s);",
						tableClass.getSimpleName(), strNorKey.toString(),
						strPriKey.toString()));
			}
			db.execSQL(strSql.toString());
			return true;
		} catch (Exception e) {
			throw new RuntimeException("创建表" + tableClass.getSimpleName()
					+ "失败");
		}

	}

	/**
	 * 创建表，字段类型都为text
	 * 
	 * @param db
	 * @param object
	 *            对象
	 * @param priKey
	 *            主键，不定参数
	 * @return
	 */
	public boolean createTable(SQLiteDatabase db, Object object,
			String... priKey) {
		return createTable(db, object.getClass(), priKey);
	}

	/**
	 * 创建表的操作，在子类中进行业务实现。
	 */
	public abstract void createTable(SQLiteDatabase db);

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// 数据库升级
	}

	/**
	 * 根据对象创建ContentValues对象，方便在表中插入数据
	 * 
	 * @param object
	 *            对象
	 * @return 创建失败则返回 null。
	 */
	public ContentValues getContentValues(Object object) {
		if (object == null)
			throw new RuntimeException("object 不能为空");
		try {
			ContentValues values = new ContentValues();
			Field[] fields = object.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				values.put(fields[i].getName(),
						(String) getFieldValueByName(fields[i], object));
			}
			return values;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据类型从查询游标中创建相应对象,注意要用次方发进行创建，clsType必须要有空的构造函数
	 * 
	 * @param mCursor
	 *            sqlite游标
	 * @param clsType
	 *            要创建对象的类的类型
	 * @return 如果创建失败则返回null
	 */
	public Object createObjectFromCursor(Cursor mCursor, Class<?> clsType) {
		try {
			Object mObject = clsType.newInstance();
			Field[] fields = clsType.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (mCursor.getString(mCursor.getColumnIndex(field.getName())) == null)
					field.set(mObject, "");
				else
					field.set(mObject, mCursor.getString(mCursor
							.getColumnIndex(field.getName())));

			}
			return mObject;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据属性名获取属性值
	 * 
	 * @param fieldName
	 *            属性名称
	 * @param o
	 *            对象
	 * @return
	 */
	public Object getFieldValueByName(Field field, Object o) {
		field.setAccessible(true);// 修改访问权限
		try {
			return field.get(o);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
