package com.efit.sport.persist.bean;

import android.text.TextUtils;

import com.efit.sport.persist.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/1 0001.
 */
public class VideoHistDao {

    public static Dao<VideoHistory,Integer> getVideoHistDao() {
        try {
            return DatabaseHelper.getDbHelper().getDao(VideoHistory.class);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static boolean addOrUpdate(VideoHistory history) {
        if(history.id != 0) {
            return update(history);
        } else {
            return save(history);
        }
    }

    public static boolean updateContinueTipStatus(VideoHistory history) {
        return addOrUpdate(history);
    }

    public static VideoHistory getVideoHistory(String courseId) {
        if(TextUtils.isEmpty(courseId)) {
            return null;
        }
        Dao<VideoHistory,Integer> dao =  getVideoHistDao();
        if(dao == null) {
            return null;
        }
        try {
            List<VideoHistory> list = dao.queryForEq(VideoHistory.COLUMN_COURSE_ID, courseId);
            if(list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean save(VideoHistory history) {
        Dao<VideoHistory,Integer> dao =  getVideoHistDao();
        if(dao == null) {
            return false;
        }
        try {
            dao.create(history);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean update(VideoHistory history) {
        Dao<VideoHistory,Integer> dao =  getVideoHistDao();
        if(dao == null) {
            return false;
        }
        try {
            dao.update(history);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<VideoHistory> getMyAllHistory() {
        Dao<VideoHistory,Integer> dao =  getVideoHistDao();
        List<VideoHistory> res = new ArrayList<VideoHistory>();
        if(dao == null) {
            return res;
        }
        try {
            res = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}
