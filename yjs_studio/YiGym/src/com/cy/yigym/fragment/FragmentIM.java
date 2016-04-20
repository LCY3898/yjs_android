package com.cy.yigym.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cy.wbs.RspBase;
import com.cy.widgetlibrary.base.AdapterBase.OnItemInnerViewClickListener;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.CustomTitleView;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.widgetlibrary.content.DlgTextMsg.ConfirmDialogListener;
import com.cy.yigym.adapter.IMChatMsgAdapter;
import com.cy.yigym.db.dao.IMChatMsgDao;
import com.cy.yigym.db.dao.IMRecentMsgDao;
import com.cy.yigym.db.entity.IMChatMsg;
import com.cy.yigym.db.entity.IMRecentMsg;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventIMMsgReceived;
import com.cy.yigym.logic.im.IMChatMsgConfig.MsgStatus;
import com.cy.yigym.logic.im.IMChatMsgConfig.MsgType;
import com.cy.yigym.logic.im.MsgCoverUtils;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.req.ReqGetChats;
import com.cy.yigym.net.req.ReqGetUserInfo;
import com.cy.yigym.net.req.ReqPrivateSend;
import com.cy.yigym.net.rsp.RspGetChats;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.im.IMSendBoard;
import com.cy.yigym.view.content.im.IMSendBoard.OnKeyBoardOpCallBack;
import com.efit.sport.R;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.hhtech.pulltorefresh.PullToRefreshBase.onSizeChangedListener;
import com.hhtech.pulltorefresh.PullToRefreshListView;

import de.greenrobot.event.EventBus;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-17
 * </p>
 * <p>
 * IM聊天功能
 * </p>
 */
public class FragmentIM extends BaseFragment implements OnKeyBoardOpCallBack,
		OnRefreshListener2<ListView>, OnItemInnerViewClickListener,
		onSizeChangedListener {
	@BindView
	private CustomTitleView vTitle;
	@BindView
	private PullToRefreshListView lvMsg;
	@BindView
	private IMSendBoard vSendBoard;
	private IMChatMsgAdapter adapter;
	private ArrayList<IMChatMsg> listMsg = new ArrayList<IMChatMsg>();
	public static final String INTENT_KEY_RECEIVER_NICK_NAME = "receiver_nickname";// 接收者昵称
	public static final String INTENT_KEY_RECEIVER_PID = "receiver_pid";// 接收者id
	private String receiverPid = "";
	private String receiverNickName = "";
	private AvatarEntity receiverAvatar = new AvatarEntity();
	private DlgTextMsg dlgTextMsg;
	private IMChatMsg reSendMsg = null;
	private int page = 0;
	private int pageSize = 20;
	private boolean isCanPullUp = true;// 是否可以继续上拉

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_im;
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		vTitle.setTxtLeftText("   ");
		vTitle.setTxtLeftIcon(R.drawable.header_back);
		vTitle.setTxtLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		dlgTextMsg = new DlgTextMsg(mActivity, new ConfirmDialogListener() {

			@Override
			public void onCancel() {

			}

			@Override
			public void onOk() {
				dlgTextMsg.dismiss();
				if (reSendMsg == null)
					return;
				reSendMsg(reSendMsg);
			}
		});
		dlgTextMsg.setText("确定重发此条消息?");
		lvMsg.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		lvMsg.setOnRefreshListener(this);
		lvMsg.setOnSizeChangedListener(this);
		vSendBoard.setOnKeyBoardOpCallBack(this);
		vSendBoard.setOnSendClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String msg = vSendBoard.getText();
				if (TextUtils.isEmpty(msg))
					return;
				vSendBoard.clearText();
				sendMsg(msg);
			}
		});
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		try {
			receiverNickName = getArguments().getString(
					INTENT_KEY_RECEIVER_NICK_NAME);
			receiverPid = getArguments().getString(INTENT_KEY_RECEIVER_PID);
			getReceiverAvatar();
			if (TextUtils.isEmpty(receiverPid)) {
				finish();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			finish();
			return;
		}
		EventBus.getDefault().register(msgReceived);
		adapter = new IMChatMsgAdapter(mActivity, listMsg, receiverAvatar);
		lvMsg.setAdapter(adapter);
		adapter.setOnItemInnerViewClickListener(R.id.imgSentFail, this);
		// ArrayList<IMChatMsg> list = IMChatMsgDao.getInstance().query(
		// DataStorageUtils.getPid(), receiverPid);
		// listMsg.addAll(list);
		vTitle.setTitle(receiverNickName);
		fetchData();

	}

	private void sendMsg(String msg) {
		final IMChatMsg chatMsg = MsgCoverUtils.buildIMChatMsg(MsgType.TEXT,
				MsgStatus.SEND_START, msg, receiverPid);
		ReqPrivateSend reqMsg = MsgCoverUtils.imChatMsg2ReqPrivateSend(chatMsg);
		IMChatMsgDao.getInstance().insert(chatMsg);
		IMRecentMsgDao.getInstance().update(
				new IMRecentMsg(receiverPid, chatMsg.msgTime));
		YJSNet.privateSend(reqMsg, LOG_TAG, new OnRespondCallBack<RspBase>() {

			@Override
			public void onSuccess(RspBase data) {
				chatMsg.msgStatus = MsgStatus.SEND_SUCCESS.value + "";
				adapter.notifyDataSetChanged();
				IMChatMsgDao.getInstance().update(chatMsg);
				scrollToBottom();
			}

			@Override
			public void onFailure(String errorMsg) {
				chatMsg.msgStatus = MsgStatus.SEND_FAIL.value + "";
				adapter.notifyDataSetChanged();
				IMChatMsgDao.getInstance().update(chatMsg);
				scrollToBottom();
			}
		});
		listMsg.add(chatMsg);
		adapter.notifyDataSetChanged();

	}

	private void reSendMsg(final IMChatMsg chatMsg) {
		chatMsg.msgStatus = MsgStatus.SEND_START.value + "";
		ReqPrivateSend reqMsg = MsgCoverUtils.imChatMsg2ReqPrivateSend(chatMsg);
		YJSNet.privateSend(reqMsg, LOG_TAG, new OnRespondCallBack<RspBase>() {

			@Override
			public void onSuccess(RspBase data) {
				chatMsg.msgStatus = MsgStatus.SEND_SUCCESS.value + "";
				adapter.notifyDataSetChanged();
				IMChatMsgDao.getInstance().update(chatMsg);
				scrollToBottom();
			}

			@Override
			public void onFailure(String errorMsg) {
				chatMsg.msgStatus = MsgStatus.SEND_FAIL.value + "";
				adapter.notifyDataSetChanged();
				IMChatMsgDao.getInstance().update(chatMsg);
				scrollToBottom();
			}
		});
	}

	/**
	 * 获取接收者的头像信息
	 */
	private void getReceiverAvatar() {
		ReqGetUserInfo req = new ReqGetUserInfo();
		req.pid = receiverPid;
		YJSNet.getUserInfo(req, LOG_TAG,
				new OnRespondCallBack<RspGetUserInfo>() {

					@Override
					public void onSuccess(RspGetUserInfo data) {
						String avatar = data.data.personInfo.profile_fid;
						receiverAvatar.avatar = DataStorageUtils
								.getHeadDownloadUrl(avatar);
						adapter.notifyDataSetChanged();
					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	public static class AvatarEntity {
		public String avatar;
	}

	private BusEventListener.MainThreadListener<EventIMMsgReceived> msgReceived = new BusEventListener.MainThreadListener<EventIMMsgReceived>() {
		@Override
		public void onEventMainThread(EventIMMsgReceived event) {
			// 这里只显示消息，保存消息的操作在WSService里面做了
			String myId = DataStorageUtils.getPid();
			if (!event.msg.msgReceiver.equals(myId)
					|| !event.msg.msgSender.equals(receiverPid))
				return;
			listMsg.add(event.msg);
			adapter.notifyDataSetChanged();
			scrollToBottom();
		}
	};

	@Override
	public void onShowKeyBoard() {
		mActivity.showKeyboard();
	}

	@Override
	public void onCloseKeyBoard() {
		mActivity.closeKeyboard();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		fetchData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		fetchData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		YJSNet.removeRspCallBacks(LOG_TAG);
		EventBus.getDefault().unregister(msgReceived);
	}

	@Override
	public void onItemInnerViewClick(View itemView, View innerView,
			Integer position, Object data) {
		switch (innerView.getId()) {
		case R.id.imgSentFail:
			reSendMsg = (IMChatMsg) data;
			dlgTextMsg.show();
			break;
		}
	}

	private void fetchDataFromNet() {
		YJSNet.send(new ReqGetChats(DataStorageUtils.getPid(), receiverPid,
				reqTimeStamp), LOG_TAG, new OnRespondCallBack<RspGetChats>() {
			@Override
			public void onSuccess(RspGetChats data) {
				refreshComplete();
				// 没有更多数据了
				if (!(data.data.chatList.size() + "").equals(pageSize + ""))
					isCanFetch = false;
				if (data.data.chatList.size() == 0)
					return;
				for (int i = 0; i < data.data.chatList.size(); i++) {
					IMChatMsg msg = MsgCoverUtils.rspGetChats2IMChatMsg(
							receiverPid, data.data.chatList.get(i));
					listMsg.add(0, msg);
					IMChatMsgDao.getInstance().insert(msg);
					if (i == data.data.chatList.size() - 1) {
						ArrayList<IMChatMsg> s = new ArrayList<IMChatMsg>();
						s.add(msg);
						updateReqTimeStamp(s);
					}
				}
				adapter.notifyDataSetChanged();
				scrollToTop();

			}

			@Override
			public void onFailure(String errorMsg) {
				refreshComplete();
			}
		});

	}

	private boolean isCanFetch = true;
	private boolean isFetchFromDb = true;
	private String reqTimeStamp = "0";

	/**
	 * 获取数据
	 * 
	 */
	private void fetchData() {
		if (!isCanFetch) {
			// 没有更多数据
			refreshComplete();
			return;
		}
		if (isFetchFromDb) {
			// 从缓存中获取
			ArrayList<IMChatMsg> msgs = IMChatMsgDao.getInstance().query(
					DataStorageUtils.getPid(), receiverPid, page, pageSize);
			for (int i = 0; i < msgs.size(); i++) {
				listMsg.add(0, msgs.get(i));
			}
			adapter.notifyDataSetChanged();
			scrollToTop();
			if (msgs.size() < pageSize) {
				// 在缓存的最后一页了
				if (msgs.size() > 0) {
					updateReqTimeStamp(msgs);
				}

				isFetchFromDb = false;
				fetchDataFromNet();
				return;
			} else {
				updateReqTimeStamp(msgs);
				page++;
				refreshComplete();

			}
		} else {
			// 从网络上获取
			fetchDataFromNet();
		}
	}

	/**
	 * 更新请求时间戳
	 * 
	 * @param timeStamp
	 */
	private void updateReqTimeStamp(ArrayList<IMChatMsg> msgs) {
		if (msgs == null || msgs.size() == 0) {
			this.reqTimeStamp = "";
			return;
		}
		String t = msgs.get(msgs.size() - 1).msgTime;
		if (t.length() == 13) {
			try {
				long lt = Long.parseLong(t) / 1000;
				t = lt + "";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.reqTimeStamp = t;
	}

	private void refreshComplete() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				lvMsg.onRefreshComplete();
			}
		}, 1000);
	}

	private void scrollToTop() {
		lvMsg.getRefreshableView().setSelection(0);
	}

	private void scrollToBottom() {
		lvMsg.getRefreshableView().setSelection(listMsg.size() - 1);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		// 当键盘顶起，并且listview选中的是最后一行时，则滚动到最底部
		ListView view = lvMsg.getRefreshableView();
		view.setSelection(view.getLastVisiblePosition());
	}

}
