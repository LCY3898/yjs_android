package com.cy.yigym.logic.im;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-16
 * </p>
 * <p>
 * 聊天消息配置文件，用于消息类型的扩展
 * </p>
 */
public class IMChatMsgConfig {
	/**
	 * IM支持的消息类型
	 */
	public static enum MsgType {
		TEXT("text", "文本类型含emoji表情"), IMAGE("image", "[图片]"), VOICE("voice",
				"[语音]"), LINK("link", "[网页链接]");
		public String value = "";
		public String desc = "";

		MsgType(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public static MsgType match(String value) {
			MsgType[] types = MsgType.values();
			for (MsgType msgType : types) {
				if (msgType.value.equals(value)) {
					return msgType;
				}
			}
			return MsgType.TEXT;
		}
	}

	/**
	 * IM支持的消息状态
	 */
	public static enum MsgStatus {
		SEND_SUCCESS("0", "消息发送成功"), SEND_FAIL("1", "消息发送失败"), SEND_RECEIVERED(
				"2", "消息被对方接收成功"), SEND_START("3", "消息开始上传");
		public String value = "";
		public String desc = "";

		MsgStatus(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public static MsgStatus match(String value) {
			MsgStatus[] status = MsgStatus.values();
			for (MsgStatus statu : status) {
				if (statu.value.equals(value)) {
					return statu;
				}
			}
			return MsgStatus.SEND_SUCCESS;
		}
	}

	/**
	 * 消息Item的类型
	 */
	public static enum MsgItemType {
		TEXT_RECEIVED(0, "文字接收"), TEXT_SENT(1, "文字发送"), IMAGE_RECEIVED(2,
				"图片接收"), IMAGE_SENT(3, "图片发送"), VOICE_RECEIVED(4, "语音接收"), VOICE_SENT(
				5, "语音发送"), LINK_RECEIVED(6, "链接接收"), LINK_SENT(7, "链接发送");
		public int value;
		public String desc = "";

		MsgItemType(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public static MsgItemType match(int value) {
			MsgItemType[] items = MsgItemType.values();
			for (MsgItemType item : items) {
				if (item.value == value) {
					return item;
				}
			}
			return MsgItemType.TEXT_RECEIVED;
		}
	}
}
