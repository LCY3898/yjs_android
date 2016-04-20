package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/6.
 */
public class RspGetNearOnline extends RspBase {

    public Data data=new Data();
    public class Data{
        public String total="0";
        public String page_num="0";
        public String download="";
        public List<ArrayLocation> arrayLocation=new ArrayList<ArrayLocation>();

    }
    public class ArrayLocation{
        public String latitude="";
        public String longitude="";
        public String nick_name="";
        public String dis="0";
        public String pid="";
        public String profile_fid="";
        public String sex="";
        public String type="";
    }

}
