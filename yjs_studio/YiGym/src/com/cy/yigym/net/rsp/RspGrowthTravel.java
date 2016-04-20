package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;

/**
 * Created by xiaoshu on 15/11/19.
 */
public class RspGrowthTravel extends RspBase {

    public Data data = new Data();

    public class Data {
        public long delta_sec=0; //注册多少天，秒数
        public int medal_type_the_chase=0;
        public int medal_type_the_data_accumulated=0;
        public int medal_type_the_day_most=0;
        public int medal_type_the_emotional=0;
        public int medal_type_the_live_broadcast=0;
        public int medal_type_the_training_time=0;
        public ArrayList<GrowthRecord> medal_list = new ArrayList<GrowthRecord>();
    }

    public static class GrowthRecord {
        public long finished_time = 0;
        public String medal_fid = "";
        public String medal_id = "";
        public String medal_meaning = "";
        public String medal_name = "";
        public String medal_type = "";
        public String share_fid = "";
        public int medal_number=0; // 一条成长纪录的唯一标示
    }
}

/*
{"obj":"pevent","act":"getPevent","pid":"o14432574323527400493","client_info":{"clientType":"webapp","userId":null},"debug":1}
{
    sess: "",
    io: "o",
    obj: "pevent",
    act: "getPevent",
    code: "SUCCEED",
    data: {
        delta_sec: 1447988958,
        medal_list: [
            {
                _id: "o14477411784882109165",
                finished_time: 1447988172,
                medal_fid: "f14476592516180970668001",
                medal_meaning: "老大",
                medal_name: "现场之王",
                medal_number: 21,
                medal_type: "直播成就",
                type: "medal"
            },
            {
                _id: "o14477412273359549045",
                finished_time: 1447988100,
                medal_fid: "f14476592516180970668001",
                medal_meaning: "初恋",
                medal_name: "初次见面",
                medal_number: 22,
                medal_type: "追TA成就",
                type: “medal”
            },
        ],
        medal_type_the_chase: 3,
        medal_type_the_data_accumulated: 8,
        medal_type_the_day_most: 8,
        medal_type_the_emotional: 3,
        medal_type_the_live_broadcast: 5,
        medal_type_the_training_time: 6
    },
    msg: “调用获取个人成长历程”
}}
 */