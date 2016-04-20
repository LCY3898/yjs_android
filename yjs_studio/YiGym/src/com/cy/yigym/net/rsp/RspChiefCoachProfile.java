package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

/**
 * Created by xiaoshu on 15/11/14.
 */
public class RspChiefCoachProfile extends RspBase {

    public Data data=new Data();

    public static class Data{
        public String[] profile_fid_list=new String[]{};
    }

}
