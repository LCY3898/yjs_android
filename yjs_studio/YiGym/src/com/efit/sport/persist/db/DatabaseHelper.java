package com.efit.sport.persist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cy.yigym.entity.GrowthRecordBean;
import com.efit.sport.persist.bean.SystemNotice;
import com.efit.sport.persist.bean.VideoHistory;
import com.hhtech.base.AppUtils;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "yjs_msg.db";

    private Map<String, Dao> daos = new HashMap<String, Dao>();

    /**
     * important!!!
     * tangtt 增加表或者修改字段都需要增加版本号
     */
    private final static int DATABASE_VERSION = 8;

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SystemNotice.class);
            TableUtils.createTable(connectionSource, VideoHistory.class);
            TableUtils.createTable(connectionSource, GrowthRecordBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource,VideoHistory.class,true);
            TableUtils.dropTable(connectionSource, VideoHistory.class, true);
            TableUtils.dropTable(connectionSource, GrowthRecordBean.class, true);
            //important!!!
            onCreate(database,connectionSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;


    public static DatabaseHelper getDbHelper() {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(AppUtils.getAppContext());
            }
        }

        return instance;
    }



    @Override
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        if(daos != null) {
            daos.clear();
        }
    }

}
