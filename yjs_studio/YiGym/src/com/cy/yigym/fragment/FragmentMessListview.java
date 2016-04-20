package com.cy.yigym.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragment;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.yigym.adapter.NotifyListAdapter;
import com.cy.yigym.aty.AtyChaseHer;
import com.cy.yigym.entity.ChaseIntentEntity;
import com.cy.yigym.entity.NotifyListItem;
import com.cy.yigym.event.BusEventListener;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqDelInboxMsg;
import com.cy.yigym.net.req.ReqGetChaseRecord;
import com.cy.yigym.net.req.ReqLoadMsg;
import com.cy.yigym.net.rsp.RspDelInboxMsg;
import com.cy.yigym.net.rsp.RspGetChaseRecord;
import com.cy.yigym.net.rsp.RspLoadMsg;
import com.cy.yigym.net.rsp.RspLoadMsg.Messages.MessageListEntity;
import com.cy.yigym.utils.DataStorageUtils;
import com.efit.sport.R;
import com.efit.sport.notify.ChaseNotifyEvent;
import com.efit.sport.notify.NotifyType;
import com.efit.sport.utils.DateTimeUtis;
import com.hhtech.base.AppUtils;
import com.hhtech.pulltorefresh.PullToRefreshBase;
import com.hhtech.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by eijianshen on 15/8/6.
 */
public class FragmentMessListview extends BaseFragment implements View.OnClickListener {

    @BindView
    private PullToRefreshListView lvNotifyListView;

    @BindView
    private View editLayout;

    @BindView
    private View btnCancel;

    @BindView
    private View btnDel;

    private ArrayList<NotifyListItem> mList = new ArrayList<NotifyListItem>();

    private NotifyListAdapter etyListAdapter;

    private boolean editMode = false;

    private boolean isQuery = false;

    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_system_msg;
    }

    @Override
    protected void initView(View contentView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addListviewItem();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        EventBus.getDefault().register(eventlistener);
        btnCancel.setOnClickListener(this);
        btnDel.setOnClickListener(this);
    }

    private BusEventListener.MainThreadListener<ChaseNotifyEvent> eventlistener =
            new BusEventListener.MainThreadListener<ChaseNotifyEvent>() {
                @Override
                public void onEventMainThread(ChaseNotifyEvent event) {

                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(eventlistener);
    }

    private void addListviewItem() {
        etyListAdapter = new NotifyListAdapter(mActivity, mList, true);
        lvNotifyListView.setAdapter(etyListAdapter);
        initList();
        querySystemMessage(0);
    }

    private void querySystemMessage(final int secs) {
        int timestamp = secs;
        if (secs == 0) {
            timestamp = (int) (System.currentTimeMillis() / 1000);
        }
        YJSNet.send(new ReqLoadMsg(timestamp, 20), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspLoadMsg>() {
                    @Override
                    public void onSuccess(RspLoadMsg rsp) {
                        if (rsp.data.notice_count > 0) {
                            List<NotifyListItem> list = new ArrayList<NotifyListItem>();
                            if (secs != 0) {
                                list.addAll(mList);

                            }
                            list.addAll(getNotifyList(rsp.data.message_list));
                            addAllItems(rsp.data.message_list);
                            mList.clear();
                            mList.addAll(list);
                            etyListAdapter.notifyDataSetChanged();

                        }
                        onQueryFinish();
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        onQueryFinish();
                    }
                });
    }

    private void onQueryFinish() {
        lvNotifyListView.onRefreshComplete();
    }


    private void addAllItems(List<MessageListEntity> list) {
        if (list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(getNotifyList(list));
            etyListAdapter.notifyDataSetChanged();
        }
    }

    private List<NotifyListItem> getNotifyList(List<MessageListEntity> list) {
        List<NotifyListItem> itemList = new ArrayList<NotifyListItem>();
        if (list != null && list.size() > 0) {
            for (MessageListEntity entity : list) {
                NotifyListItem item = new NotifyListItem();
                item.event = entity;
                item.headerFid = entity.data.fid;
                item.userName = entity.data.title;
                item.sysTime = DateTimeUtis.showCanonicalTime(entity.data.notice_time);
                item.content = entity.data.msg;
                itemList.add(item);
            }
        }
        return itemList;
    }


    private Handler handler = new Handler();

    private void onRefreshPullList() {
        querySystemMessage(0);
    }


    private void onMore() {
        if (mList.size() == 0) {
            querySystemMessage(0);
        } else {
            RspLoadMsg.Messages.MessageListEntity entity = mList.get(mList.size() - 1).event;
            if (entity != null && entity.data != null) {
                querySystemMessage(entity.data.notice_time);
            } else {
                querySystemMessage(0);
            }
        }

    }

    private void onItemEvent(final NotifyListItem item) {
        if (NotifyType.CHASE.getTypeName().equals(item.event.notice_type)) {
            if (isQuery) {
                return;
            }

            isQuery = true;
            YJSNet.send(new ReqGetChaseRecord(item.event.data.chase_id), LOG_TAG,
                    new YJSNet.OnRespondCallBack<RspGetChaseRecord>() {
                        @Override
                        public void onSuccess(RspGetChaseRecord data) {
                            Log.d(LOG_TAG, "id" + data.data.chaseRecord._id);
                            Intent intent = new Intent(mActivity, AtyChaseHer.class);
                            DataStorageUtils.setOtherPid(data.data.chaseRecord.sender_id);
                            ChaseIntentEntity chaseList = new ChaseIntentEntity(item.userName, data,
                                    data.data.chaseRecord.real_apart_distance > 0);
                            intent.putExtra("flag", 0);
                            intent.putExtra(ChaseIntentEntity.INTENT_KEY, chaseList);
                            startActivity(intent);
                            isQuery = false;
                        }

                        @Override
                        public void onFailure(String errorMsg) {
                            Log.d(LOG_TAG, "错误:" + errorMsg);
                            isQuery = false;
                        }
                    });
        }
    }

    private void initList() {
        lvNotifyListView.setMode(PullToRefreshBase.Mode.BOTH);
        lvNotifyListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                PullToRefreshBase.Mode mode = refreshView.getCurrentMode();
                if (mode == PullToRefreshBase.Mode.PULL_FROM_END) {
                    onRefreshPullList();
                } else if (mode == PullToRefreshBase.Mode.PULL_FROM_START) {
                    onRefreshPullList();
                }
                String label = DateUtils.formatDateTime(AppUtils.getAppContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);

            }
        });

        // Add an end-of-list listener
        lvNotifyListView
                .setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {

                    }
                });

        lvNotifyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotifyListItem item = (NotifyListItem) parent.getItemAtPosition(position);
                onItemEvent(item);
            }
        });

        lvNotifyListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                boolean edit = !editMode;
                setEditMode(edit);
                if (edit) {
                    NotifyListItem item = (NotifyListItem) parent.getItemAtPosition(position);
                    item.isSelected = true;
                }

                return true;
            }
        });
    }

    private void setEditMode(boolean edit) {
        editMode = edit;
        etyListAdapter.setEditMode(editMode);
        editLayout.setVisibility(editMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            setEditMode(false);
        } else if (v == btnDel) {
            confirmDel();
        }
    }

    private void confirmDel() {
        if (mList.size() == 0) {
            WidgetUtils.showToast("亲，您还没选择要删除的项目哦");
            return;
        }

        final List<String> itemToDel = new ArrayList<String>();
        for (NotifyListItem item : mList) {
            if (item.isSelected) {
                itemToDel.add(item.event.data.chase_id);
            }
        }
        if (itemToDel.size() == 0) {
            WidgetUtils.showToast("亲，您还没选择要删除的项目哦");
            return;
        }

        DlgTextMsg dlgTextMsg = new DlgTextMsg(mActivity, new DlgTextMsg.ConfirmOkListener() {
            @Override
            public void onOk() {
                YJSNet.send(new ReqDelInboxMsg(itemToDel), LOG_TAG,
                        new YJSNet.OnRespondCallBack<RspDelInboxMsg>() {
                            @Override
                            public void onSuccess(RspDelInboxMsg data) {
                                WidgetUtils.showToast("删除成功");
                                setEditMode(false);
                                querySystemMessage(0);
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                WidgetUtils.showToast("删除失败");
                            }
                        });
            }
        });
        dlgTextMsg.show("删除系统消息", "确定要删除?");
    }
}
