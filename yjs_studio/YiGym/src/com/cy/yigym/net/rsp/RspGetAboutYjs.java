package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by ejianshen on 15/7/22.
 */
public class RspGetAboutYjs extends RspBase {
    public Data data;

    public static class Data{
       public String email="";
       public String phone="";
       public String version="";
    }
}