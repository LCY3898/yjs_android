package com.cy.wbs;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/8/26 0026.
 */
public interface NotifyListener {
    void onNotifyEvent(String noticeType, String noticeData,
                       String obj,String act);
}
