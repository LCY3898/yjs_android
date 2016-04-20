package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/10/12.
 */
public class RspJoinLiveCast extends RspBase {


    public Data data = new Data();

    public class Data {
        public List<Medal> medal_list = new ArrayList<Medal>();

    }

    public static class Medal {
        public long finished_time;
        public String medal_fid = ""; // 图片id
        public String medal_id = "";
        public String medal_meaning = "";
        public String medal_name = "";
        public String medal_type = "";
        public String share_fid = "";
        public String medal_number = "";
    }

}
