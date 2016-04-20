package com.cy.yigym.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.yigym.db.entity.IMChatMsg;
import com.cy.yigym.fragment.FragmentIMRecentMsgList.IMChatMsgExtend;
import com.cy.yigym.logic.im.IMChatMsgConfig.MsgType;
import com.cy.yigym.logic.im.IMTimeUtils;
import com.cy.yigym.view.content.im.ItemIMRecentMsg;
import com.efit.sport.R;

public class IMRecentMsgAdapter extends AdapterBase<IMChatMsgExtend> {
	public IMRecentMsgAdapter(Context context, List<IMChatMsgExtend> list) {
		super(context, list);
	}

	@Override
	protected View getItemView(int position, View convertView,
			ViewGroup parent, IMChatMsgExtend entity) {
		ItemIMRecentMsg item = null;
		if (convertView == null) {
			item = new ItemIMRecentMsg(mContext);
			convertView = item;
		} else {
			item = (ItemIMRecentMsg) convertView;
		}
		if (!TextUtils.isEmpty(entity.receiverAvatar))
			ImageLoaderUtils.getInstance().loadImage(entity.receiverAvatar,
					item.imgAvatar);
		else
			item.imgAvatar.setImageResource(R.drawable.head);
		item.txtNickName.setText(entity.receiverNickName);
		item.txtMsg.setText(getMsg(entity.msg));
		item.txtChatTime.setText(IMTimeUtils
				.getTimeDescFromTimeMillis(entity.msg.msgTime));
		return convertView;
	}

	private String getMsg(IMChatMsg entity) {
		MsgType type = MsgType.match(entity.msgType);
		if (type == MsgType.TEXT) {
			return entity.msgContent;
		}
		return type.desc;
	}

}
