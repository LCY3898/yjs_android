package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * 意见反馈
 * Created by ejianshen on 15/7/22.
 * {"obj":"person","act":"setPersonIdea","pid":"o14369483820400090217","content":"idea_msg","mark":"123456789","client_info":{"clientType":"webapp","userId":null},"debug":1}
 */
public class ReqFeedBack extends PersonReqBase{
    private String act = "setPersonIdea";
    private String content = "";
    private String mark = "0";
    private ClientInfo client_info=new ClientInfo();

    private static class ClientInfo{
         String clientType = "";
         String userId = "";

    }
    public ReqFeedBack(String content,String mark){
        this.content=content;
        this.mark=mark;
    }
}
