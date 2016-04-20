package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/11/9.
 */
public class RspReviewHistoryDataRank extends RspBase {

    public class Data{
        public List<ListData> list_data=new ArrayList<ListData>();
        public int myRank=0;
    }
    public class ListData{
        public String nickname="";
        public String profile_fid="";
        public String value="";
    }
}
