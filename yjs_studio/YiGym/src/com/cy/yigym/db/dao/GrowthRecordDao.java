package com.cy.yigym.db.dao;


import android.util.Log;

import com.cy.yigym.entity.GrowthRecordBean;
import com.efit.sport.persist.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

/**
 * Created by lijianqiang on 15/11/30.
 */
public class GrowthRecordDao {

    private static final String TAG = "GrowthRecordDao";

    private Dao<GrowthRecordBean, Integer> dataDaoOpe;

    public GrowthRecordDao() {
        try {
            dataDaoOpe = DatabaseHelper.getDbHelper().getDao(GrowthRecordBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "GrowthRecordDao()");
        }
    }


    public GrowthRecordBean getById(int id) {

        GrowthRecordBean bean = null;

        try {
            bean = dataDaoOpe.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "getById()");
        }

        return bean;
    }

    public GrowthRecordBean getByMedalNumber(int medalNumber) {

        try {
            QueryBuilder<GrowthRecordBean, Integer> queryBuilder = dataDaoOpe.queryBuilder();
            queryBuilder.where().eq("medalNumber", medalNumber);
            GrowthRecordBean bean = queryBuilder.queryForFirst();

            return bean;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "getByMedalNumber()");
        }
        return null;
    }

    public boolean add(GrowthRecordBean bean) {
        try {
            dataDaoOpe.create(bean);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "add()");
        }
        return false;
    }

    public boolean update(GrowthRecordBean bean) {

        try {
            dataDaoOpe.update(bean);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "update()");
        }
        return false;
    }

    public boolean addOrUpdate(GrowthRecordBean bean) {
        GrowthRecordBean old = getByMedalNumber(bean.getMedalNumber());

        boolean res;

        if (old == null) {
            res = add(bean);
        } else {
            bean.setId(old.getId());
            res = update(bean);
        }
        return res;
    }


}
