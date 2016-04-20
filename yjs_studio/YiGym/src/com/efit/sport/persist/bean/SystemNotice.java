package com.efit.sport.persist.bean;

import com.efit.sport.notify.ChaseNotifyEvent;
import com.efit.sport.notify.NotifyType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_system_notice")
public class SystemNotice {
    public static SystemNotice fromSystemMsg(ChaseNotifyEvent notify) {
        SystemNotice notice = new SystemNotice();
        notice.avatarFid = notify.fid;
        notice.detailMsgId = notify.id;
        notice.msg = notify.msg;
        notice.serverNoticeId = notify.id;
        notice.title = notify.title;
        notice.msgType = NotifyType.CHASE.getTypeName();
        notice.timeInSecs = notify.notice_time;
        notice.readStatus = 0;
        return notice;
    }
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "notice_id")
    public String serverNoticeId;

    @DatabaseField(columnName = "msgtype")
    public String msgType;

    @DatabaseField(columnName = "msg")
    public String msg;

    @DatabaseField(columnName = "title")
    public String title;


    @DatabaseField(columnName = "avatar_id")
    public String avatarFid;

    @DatabaseField(columnName = "detail_id")
    public String detailMsgId;


    @DatabaseField(columnName = "timestamp")
    public long timeInSecs;

    @DatabaseField(columnName = "read_status")
    public int readStatus; // 0 for unread, 1 for read

    public SystemNotice() {
    }
}
