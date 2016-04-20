package com.cy.yigym.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.yigym.adapter.IMRecentMsgAdapter;
import com.cy.yigym.aty.AtyIM;
import com.cy.yigym.db.dao.IMRecentMsgDao;
import com.cy.yigym.db.entity.IMChatMsg;
import com.cy.yigym.db.entity.IMRecentMsg;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.event.EventIMMsgReceived;
import com.cy.yigym.logic.im.MsgCoverUtils;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.YJSNet.OnRespondCallBack;
import com.cy.yigym.net.req.ReqGetUserInfo;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshBase.Mode;
import com.hhtech.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.hhtech.pulltorefresh.PullToRefreshListView;

import de.greenrobot.event.EventBus;

public class FragmentIMRecentMsgList extends BaseFragment implements
		OnRefreshListener2<ListView>, OnItemClickListener {
	@BindView
	private PullToRefreshListView lvMsg;
	private ArrayList<IMChatMsgExtend> recentMsgs = new ArrayList<IMChatMsgExtend>();
	private IMRecentMsgAdapter adapter;
	private int page = 0;
	private int pageSize = 20;
	private boolean isCanPullUp = true;// 是否可以继续上拉

	@Override
	protected int getContentViewId() {
		return R.layout.fragment_im_recent_msg_list;
	}

	@Override
	protected boolean isBindViewByAnnotation() {
		return true;
	}

	@Override
	protected void initView(View contentView, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		lvMsg.setOnRefreshListener(this);
		lvMsg.setMode(Mode.BOTH);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		EventBus.getDefault().register(msgReceived);
		adapter = new IMRecentMsgAdapter(mActivity, recentMsgs);
		lvMsg.setAdapter(adapter);
		lvMsg.setOnItemClickListener(this);
		fetchData(true);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		fetchData(true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		fetchData(false);
	}

	private BusEventListener.MainThreadListener<EventIMMsgReceived> msgReceived = new BusEventListener.MainThreadListener<EventIMMsgReceived>() {
		@Override
		public void onEventMainThread(EventIMMsgReceived event) {
			// 有消息来，刷新界面
			fetchData(true);
		}
	};

	/**
	 * 获取数据
	 * 
	 * @param isPullDown
	 *            是否下拉刷新
	 */
	private void fetchData(boolean isPullDown) {
		if (isPullDown) {
			isCanPullUp = true;
		}
		if (!isPullDown && !isCanPullUp) {
			refreshComplete();
			return;
		}
		ArrayList<IMRecentMsg> msgs = IMRecentMsgDao.getInstance().query(page,
				pageSize);
		isCanPullUp = msgs.size() < pageSize ? false : true;
		if (isPullDown) {

			page = 0;
			recentMsgs.clear();
		} else {
			page = isCanPullUp ? page : page + 1;

		}
		for (int i = 0; i < msgs.size(); i++) {
			IMRecentMsg msg = msgs.get(i);
			IMChatMsg cMsg = MsgCoverUtils.imRecentMsg2IMChatMsg(msg);
			if (cMsg != null) {
				IMChatMsgExtend data = new IMChatMsgExtend(cMsg);
				data.chatUid = msg.chatUid;
				getReceiverInfo(msg.chatUid, data);
				recentMsgs.add(data);
			}
		}
		adapter.notifyDataSetChanged();
		refreshComplete();

	}

	private void refreshComplete() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				lvMsg.onRefreshComplete();
			}
		}, 1000);
	}

	/**
	 * 聊天信息扩展，增加接收者昵称和头像
	 */
	public static class IMChatMsgExtend {
		public String receiverNickName;
		public String receiverAvatar;
		public String chatUid;
		public IMChatMsg msg;

		public IMChatMsgExtend(IMChatMsg msg) {
			super();
			this.msg = msg;
		}

	}

	/**
	 * 获取接收者的个人信息
	 * 
	 * @param pid
	 * @param msg
	 */
	private void getReceiverInfo(String pid, final IMChatMsgExtend msg) {
		ReqGetUserInfo req = new ReqGetUserInfo();
		req.pid = pid;
		YJSNet.getUserInfo(req, LOG_TAG,
				new OnRespondCallBack<RspGetUserInfo>() {

					@Override
					public void onSuccess(RspGetUserInfo data) {
						msg.receiverAvatar = DataStorageUtils
								.getHeadDownloadUrl(data.data.personInfo.profile_fid);
						msg.receiverNickName = data.data.personInfo.nick_name;
						adapter.notifyDataSetChanged();
					}

					@Override
					public void onFailure(String errorMsg) {

					}
				});
	}

	@Override
	public void onDestroy() {
		YJSNet.removeRspCallBacks(LOG_TAG);
		EventBus.getDefault().unregister(msgReceived);
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AtyIM.luanchIM(mActivity, recentMsgs.get(position - 1).chatUid,
				recentMsgs.get(position - 1).receiverNickName);
	}

	@Override
	public void onResume() {
		super.onResume();
		onPullDownToRefresh(lvMsg);
	}
}
