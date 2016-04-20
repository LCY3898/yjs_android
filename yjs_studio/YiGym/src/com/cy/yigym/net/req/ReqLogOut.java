package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;
import com.tencent.mm.sdk.modelbase.BaseReq;

/**
 * Created by ejianshen on 15/7/20.
 * {"obj":"person","act":"logout","debug":1}
 * 退出登入请求
 */
public class ReqLogOut extends PersonReqBase {
    private String act="logout";
}
