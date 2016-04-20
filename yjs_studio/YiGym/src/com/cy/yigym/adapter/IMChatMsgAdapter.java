package com.cy.yigym.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.widgetlibrary.base.AdapterBase;
import com.cy.widgetlibrary.view.CustomCircleImageView;
import com.cy.yigym.db.entity.IMChatMsg;
import com.cy.yigym.fragment.FragmentIM.AvatarEntity;
import com.cy.yigym.logic.im.IMChatMsgConfig.MsgItemType;
import com.cy.yigym.logic.im.IMChatMsgConfig.MsgStatus;
import com.cy.yigym.logic.im.IMChatMsgConfig.MsgType;
import com.cy.yigym.logic.im.IMTimeUtils;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.emoji.EmojiTextView;
import com.cy.yigym.view.content.im.ItemIMChatReceivedBase;
import com.cy.yigym.view.content.im.ItemIMChatReceivedText;
import com.cy.yigym.view.content.im.ItemIMChatSentBase;
import com.cy.yigym.view.content.im.ItemIMChatSentText;
import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-16
 * </p>
 * <p>
 * 聊天记录适配器
 * </p>
 */
public class IMChatMsgAdapter extends AdapterBase<IMChatMsg> {
	private final int DEFAULT_AVATAR_RESOURCE_ID = R.drawable.head;
	private String me = DataStorageUtils.getPid();
	private String myAvatar = DataStorageUtils
			.getHeadDownloadUrl(DataStorageUtils.getCurUserProfileFid());
	private AvatarEntity receiverAvatar;

	public IMChatMsgAdapter(Context context, List<IMChatMsg> list,
			AvatarEntity receiverAvatar) {
		super(context, list);
		this.receiverAvatar = receiverAvatar;
	}

	protected View createItemView(int position) {
		IMChatMsg msg = mList.get(position);
		String msgType = msg.msgType;
		boolean isBelongToMe = msg.msgSender.equals(me);
		switch (MsgType.match(msgType)) {
		case TEXT:
			return isBelongToMe ? new ItemIMChatSentText(mContext)
					: new ItemIMChatReceivedText(mContext);
		case IMAGE:
			break;
		case LINK:
			break;
		case VOICE:
			break;
		}
		return new ItemIMChatSentText(mContext);
	}

	@Override
	public int getViewTypeCount() {
		return MsgItemType.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		IMChatMsg msg = mList.get(position);
		String msgType = msg.msgType;
		boolean isBelongToMe = msg.msgSender.equals(me);
		switch (MsgType.match(msgType)) {
		case TEXT:
			return isBelongToMe ? MsgItemType.TEXT_SENT.value
					: MsgItemType.TEXT_RECEIVED.value;
		case IMAGE:
			return isBelongToMe ? MsgItemType.IMAGE_SENT.value
					: MsgItemType.IMAGE_RECEIVED.value;
		case LINK:
			return isBelongToMe ? MsgItemType.LINK_SENT.value
					: MsgItemType.LINK_RECEIVED.value;
		case VOICE:
			return isBelongToMe ? MsgItemType.VOICE_SENT.value
					: MsgItemType.VOICE_RECEIVED.value;

		}
		return MsgItemType.TEXT_SENT.value;

	}

	@Override
	protected View getItemView(int position, View convertView,
			ViewGroup parent, IMChatMsg entity) {
		if (convertView == null) {
			convertView = createItemView(position);
		}
		setItemInfo(position, convertView, entity);
		setItemTimeInfo(position, convertView, entity);
		setItemViewStatus(position, convertView, entity);
		return convertView;
	}

	/**
	 * 设置item的时间信息
	 * 
	 * @param position
	 * @param convertView
	 * @param entity
	 */
	protected void setItemTimeInfo(int position, View convertView,
			IMChatMsg entity) {
		boolean isBelongToMe = entity.msgSender.equals(me);
		TextView txtTime = isBelongToMe ? ((ItemIMChatSentBase) convertView).txtTime
				: ((ItemIMChatReceivedBase) convertView).txtTime;
		if (position == 0) {
			String time = IMTimeUtils.getTimeDescFromTimeMillis(entity.msgTime);
			txtTime.setText(time);
			txtTime.setVisibility(View.VISIBLE);
		} else {
			String preTime = mList.get(position - 1).msgTime;
			String curTime = entity.msgTime;
			boolean isMoreThanTwoMinutes = IMTimeUtils
					.isMoreThanPreMsgTimeTwoMinutes(preTime, curTime);
			if (!isMoreThanTwoMinutes) {
				txtTime.setVisibility(View.GONE);
			} else {
				String time = IMTimeUtils
						.getTimeDescFromTimeMillis(entity.msgTime);
				txtTime.setText(time);
				txtTime.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 设置item信息
	 * 
	 * @param position
	 */
	protected void setItemInfo(int position, View convertView, IMChatMsg entity) {
		switch (MsgType.match(entity.msgType)) {
		case TEXT:
			setTextItemInfo(position, convertView, entity);
			break;
		case IMAGE:
			break;
		case LINK:
			break;
		case VOICE:
			break;

		}
	}

	/**
	 * 设置文字item的信息
	 * 
	 * @param position
	 * @param convertView
	 * @param entity
	 */
	protected void setTextItemInfo(int position, View convertView,
			IMChatMsg entity) {
		boolean isBelongToMe = entity.msgSender.equals(me);
		CustomCircleImageView imgAvatar = isBelongToMe ? ((ItemIMChatSentText) convertView).imgAvatar
				: ((ItemIMChatReceivedText) convertView).imgAvatar;
		EmojiTextView txtMsg = isBelongToMe ? ((ItemIMChatSentText) convertView).txtMsg
				: ((ItemIMChatReceivedText) convertView).txtMsg;
		String avatar = isBelongToMe ? myAvatar : receiverAvatar.avatar;
		if (TextUtils.isEmpty(avatar)) {
			imgAvatar.setImageResource(DEFAULT_AVATAR_RESOURCE_ID);
		} else {
			ImageLoaderUtils.getInstance().loadImage(avatar, imgAvatar);
		}
		txtMsg.setText(entity.msgContent);
	}

	/**
	 * 设置item的控件状态
	 * 
	 * @param position
	 * @param convertView
	 * @param entity
	 */
	protected void setItemViewStatus(int position, View convertView,
			IMChatMsg entity) {
		boolean isBelongToMe = entity.msgSender.equals(me);
		if (!isBelongToMe)
			return;
		ItemIMChatSentBase item = (ItemIMChatSentBase) convertView;
		ProgressBar prgLoad = item.prgLoad;
		ImageView imgSentFail = item.imgSentFail;
		switch (MsgStatus.match(entity.msgStatus)) {
		case SEND_START:
			prgLoad.setVisibility(View.VISIBLE);
			imgSentFail.setVisibility(View.GONE);
			break;
		case SEND_SUCCESS:
			prgLoad.setVisibility(View.GONE);
			imgSentFail.setVisibility(View.GONE);
			break;
		case SEND_FAIL:
			prgLoad.setVisibility(View.GONE);
			imgSentFail.setVisibility(View.VISIBLE);
			break;
		case SEND_RECEIVERED:
			break;

		}
	}

}
