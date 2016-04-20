package com.cy.yigym.db.dao;

import java.util.ArrayList;

import com.cy.yigym.db.DbConstant;
import com.cy.yigym.db.entity.IMRecentMsg;
import com.cy.yigym.db.table.T_IMRecentMsg;

public class IMRecentMsgDao extends BaseDao {
	public IMRecentMsgDao() {
		super(DbConstant.TableName.T_IMRecentMsg);
	}

	private static IMRecentMsgDao mInstance = null;

	public static IMRecentMsgDao getInstance() {
		if (mInstance == null) {
			synchronized (IMChatMsgDao.class) {
				if (mInstance == null) {
					mInstance = new IMRecentMsgDao();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 增加
	 * 
	 * @param entity
	 * @return
	 */
	public boolean insert(IMRecentMsg entity) {
		return super.insert(entity);
	}

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return
	 */
	public boolean update(IMRecentMsg entity) {
		ArrayList<IMRecentMsg> msgs = super.query(
				String.format("%s=?", T_IMRecentMsg.chatUid),
				new String[] { entity.chatUid }, IMRecentMsg.class);
		if (msgs.size() == 0)
			return insert(entity);
		return super.update(entity,
				String.format("%s=?", T_IMRecentMsg.chatUid),
				new String[] { entity.chatUid });
	}

	/**
	 * 删除
	 * 
	 * @param entity
	 * @return
	 */
	public boolean delete(IMRecentMsg entity) {
		return super.delete(String.format("%s=? and %s=?",
				T_IMRecentMsg.chatUid, T_IMRecentMsg.lastChatTime),
				new String[] { entity.chatUid, entity.lastChatTime });
	}

	/**
	 * 降序获取分页的消息数据
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public ArrayList<IMRecentMsg> query(int page, int pageSize) {
		return super.query2(DbConstant.TableName.T_IMRecentMsg, String
				.format("order by %s desc limit ? offset ?",
						T_IMRecentMsg.lastChatTime), new String[] {
				pageSize + "", page * pageSize + "" }, IMRecentMsg.class);
	}
}
