package com.cy.yigym.db.dao;

import java.util.ArrayList;

import com.cy.yigym.db.DbConstant;
import com.cy.yigym.db.entity.IMChatMsg;
import com.cy.yigym.db.table.T_IMChatMsg;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-16
 * </p>
 * <p>
 * 聊天记录数据库操作
 * </p>
 */
public class IMChatMsgDao extends BaseDao {

	public IMChatMsgDao() {
		super(DbConstant.TableName.T_IMChatMsg);
	}

	private static IMChatMsgDao mInstance = null;

	public static IMChatMsgDao getInstance() {
		if (mInstance == null) {
			synchronized (IMChatMsgDao.class) {
				if (mInstance == null) {
					mInstance = new IMChatMsgDao();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 添加聊天记录
	 * 
	 * @param entity
	 * @return
	 */
	public boolean insert(IMChatMsg entity) {
		return super.insert(entity);
	}

	/**
	 * 删除聊天记录
	 * 
	 * @param entity
	 * @return
	 */
	public boolean delete(IMChatMsg entity) {
		return super.delete(String.format("%s=? and %s=? and %s=?",
				T_IMChatMsg.msgSender, T_IMChatMsg.msgReceiver,
				T_IMChatMsg.msgTime), new String[] { entity.msgSender,
				entity.msgReceiver, entity.msgTime });
	}

	/**
	 * 查询聊天记录
	 * 
	 * @param sender
	 * @param receiver
	 * @return
	 */
	public ArrayList<IMChatMsg> query(String sender, String receiver) {
		return super.query(String.format(
				"(%s=? and %s=?) or (%s=? and %s=?) order by %s asc",
				T_IMChatMsg.msgSender, T_IMChatMsg.msgReceiver,
				T_IMChatMsg.msgSender, T_IMChatMsg.msgReceiver,
				T_IMChatMsg.msgTime), new String[] { sender, receiver,
				receiver, sender }, IMChatMsg.class);
	}

	/**
	 * 查询聊天记录
	 * 
	 * @param sender
	 * @param receiver
	 * @param msgTime
	 * @return
	 */
	public ArrayList<IMChatMsg> query(String sender, String receiver,
			String msgTime) {
		return super.query(String.format(
				"((%s=? and %s=?) or (%s=? and %s=?)) and %s=?",
				T_IMChatMsg.msgSender, T_IMChatMsg.msgReceiver,
				T_IMChatMsg.msgSender, T_IMChatMsg.msgReceiver,
				T_IMChatMsg.msgTime), new String[] { sender, receiver,
				receiver, sender, msgTime }, IMChatMsg.class);
	}

	/**
	 * 更新记录
	 * 
	 * @param msg
	 * @return
	 */
	public boolean update(IMChatMsg msg) {
		return super.update(msg, String.format("%s=? and %s=? and %s=?",
				T_IMChatMsg.msgSender, T_IMChatMsg.msgReceiver,
				T_IMChatMsg.msgTime), new String[] { msg.msgSender,
				msg.msgReceiver, msg.msgTime });
	}

	/**
	 * 降序获取分页数据
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public ArrayList<IMChatMsg> query(String sender, String receiver, int page,
			int pageSize) {
		return super
				.query(DbConstant.TableName.T_IMChatMsg,
						String.format(
								"(%s=? and %s=?) or (%s=? and %s=?) order by %s desc limit ? offset ?",
								T_IMChatMsg.msgSender, T_IMChatMsg.msgReceiver,
								T_IMChatMsg.msgSender, T_IMChatMsg.msgReceiver,
								T_IMChatMsg.msgTime), new String[] { sender,
								receiver, receiver, sender, pageSize + "",
								page * pageSize + "" }, IMChatMsg.class);
	}
}
