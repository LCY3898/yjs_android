package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;
import com.efit.sport.notify.ChaseNotifyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/21.
 */
public class RspLoadMsg extends RspBase{

    public Messages data;

    public static class Messages {

        public int notice_count;
        public int unread_count;
        public List<MessageListEntity> message_list = new ArrayList<MessageListEntity>();

        public static class MessageListEntity {
            public ChaseNotifyEvent data;
            public String notice;
            public String notice_type;
        }
    }
}


