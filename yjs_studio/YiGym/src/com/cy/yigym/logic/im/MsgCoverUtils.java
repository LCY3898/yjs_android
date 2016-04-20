package com.cy.yigym.logic.im;

import java.util.ArrayList;

import com.cy.yigym.db.dao.IMChatMsgDao;
import com.cy.yigym.db.entity.IMChatMsg;
import com.cy.yigym.db.entity.IMRecentMsg;
import com.cy.yigym.logic.im.IMChatMsgConfig.MsgStatus;
import com.cy.yigym.logic.im.IMChatMsgConfig.MsgType;
import com.cy.yigym.net.req.ReqPrivateSend;
import com.cy.yigym.net.rsp.RspGetChats;
import com.cy.yigym.net.rsp.RspGetPrivateChat;
import com.cy.yigym.utils.DataStorageUtils;

public class MsgCoverUtils {
	public static String myHeadUrl = DataStorageUtils
			.getHeadDownloadUrl(DataStorageUtils.getCurUserProfileFid());

	public static ReqPrivateSend imChatMsg2ReqPrivateSend(IMChatMsg msg) {
		ReqPrivateSend reqMsg = new ReqPrivateSend(msg.msgReceiver,
				msg.msgContent, msg.msgType);
		return reqMsg;
	}

	public static IMChatMsg rspGetPrivateChat2IMChatMsg(RspGetPrivateChat msg) {
		IMChatMsg chatMsg = new IMChatMsg(System.currentTimeMillis() + "",
				MsgType.match(msg.chat_xtype).value,
				MsgStatus.SEND_RECEIVERED.value, msg.content, msg.sender_id,
				DataStorageUtils.getPid());
		return chatMsg;
	}

	public static IMChatMsg rspGetChats2IMChatMsg(String receiverId,
			RspGetChats.Chat chat) {
		String msgSender = "", msgReceiver = "";
		if (chat.userId.equals(DataStorageUtils.getPid())) {
			msgSender = DataStorageUtils.getPid();
			msgReceiver = receiverId;
		} else {
			msgSender = receiverId;
			msgReceiver = DataStorageUtils.getPid();
		}
		IMChatMsg msg = new IMChatMsg(chat.et + "", chat.xtype,
				MsgStatus.SEND_SUCCESS.value, chat.content, msgSender,
				msgReceiver);
		return msg;
	}

	public static IMChatMsg buildIMChatMsg(MsgType msgType,
			MsgStatus msgStatus, String msgContent, String receiverPid) {
		IMChatMsg msg = new IMChatMsg(System.currentTimeMillis() + "",
				msgType.value, msgStatus.value, msgContent,
				DataStorageUtils.getPid(), receiverPid);
		return msg;
	}

	public static IMChatMsg imRecentMsg2IMChatMsg(IMRecentMsg recentMsg) {
		ArrayList<IMChatMsg> msgs = IMChatMsgDao.getInstance().query(
				recentMsg.chatUid, DataStorageUtils.getPid(),
				recentMsg.lastChatTime);
		return msgs.size() > 0 ? msgs.get(0) : null;
	}

}
