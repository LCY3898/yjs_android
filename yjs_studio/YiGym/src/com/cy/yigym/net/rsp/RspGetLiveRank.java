package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/10/12.
 */
public class RspGetLiveRank extends RspBase {
    public Data data = new Data();

    public class Data {
        public MyRef my_ref = new MyRef();
        public List<Values> values = new ArrayList<Values>();
    }

    public class MyRef {
        public double calorie = 0;
        public double distance = 0;
        public int myrank;
        public String nick_name = "";
        public String pid = "";
        public String profile_fid = "";
        public double speed = 0;
        public double speed_per_min = 0;
        public String voipAccount;
        public double resist = 0;
        public String login_mode = "";
    }

    public class Values {
        public double calorie = 121;
        public double distance = 222;
        public int myrank;
        public String nick_name = "";
        public String pid = "";
        public String profile_fid = "";
        public double speed = 0;
        public double speed_per_min = 0;
        @SerializedName("accid")
        public String voipAccount;
        public double resist = 0;
        public String login_mode = "";
    }
}
