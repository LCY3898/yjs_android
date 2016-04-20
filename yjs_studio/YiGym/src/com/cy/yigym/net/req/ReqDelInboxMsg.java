package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * tangtt
 * <p>
 * 2015-8-27
 * </p>
 * <p>
 {"obj":"inbox","act":"delete","notice":{"pid":"o14410303497470839023","array":["o14410334838288199901"
  ,"o14410341558124420642"]},"client_info":{"clientType":"webapp","userId":null},"debug":1} * </p>
 */
public class ReqDelInboxMsg extends InboxReqBase {
    public String act = "delete";
    public DelNoticeInfo notice;

    public static class DelNoticeInfo {
        public String pid = DataStorageUtils.getPid();
        public List<String> array;
    }
    public ReqDelInboxMsg(List<String>recordIds) {
        notice = new DelNoticeInfo();
        notice.array = new ArrayList<String>();
        if(recordIds != null) {
            notice.array.addAll(recordIds);
        }
    }


}
