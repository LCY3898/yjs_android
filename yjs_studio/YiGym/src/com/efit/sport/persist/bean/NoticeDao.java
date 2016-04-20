package com.efit.sport.persist.bean;

import com.efit.sport.persist.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2015/9/1 0001.
 */
public class NoticeDao {
    public interface OnLoadMoreCbk {
        void onMoreItems(List<SystemNotice> noticeList);
    }

    public Dao<SystemNotice,Integer> getNoticeDao() {
        try {
            return DatabaseHelper.getDbHelper().getDao(SystemNotice.class);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public boolean save(SystemNotice notice) {
        Dao<SystemNotice,Integer> dao =  getNoticeDao();
        if(dao == null) {
            return false;
        }
        //查询是否已存在该条记录,如果存在则不需要添加
        try {
            List<SystemNotice> noticeList = dao.queryForMatchingArgs(notice);
            if(noticeList != null && noticeList.size() > 0) {
                return true;
            }
        }catch (SQLException e) {

        }
        try {
            dao.create(notice);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void loadMore(int lastTimestamp,OnLoadMoreCbk cbk) {
        Dao<SystemNotice,Integer> dao =  getNoticeDao();
        if(dao == null) {
            return;
        }
        QueryBuilder<SystemNotice,Integer> builder =
                dao.queryBuilder().limit(20).orderBy("timestamp",false);
        try {
            builder.where().le("timestamp",lastTimestamp);
            List<SystemNotice>noticeLsit = builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
