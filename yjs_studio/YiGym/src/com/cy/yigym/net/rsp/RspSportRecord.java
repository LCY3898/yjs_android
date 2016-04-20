package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaoshu on 15/11/17.
 * <p>
 * <p>
 * <p>
 * {
 * sess: "",
 * io: "o",
 * obj: "precord",
 * act: "getPrecord",
 * data: {
 * prec_list: [
 * {
 * day_calorie: 0,
 * day_distance: 0,
 * day_time: 0,
 * day_training_time: 0,
 * day_watch_broadcast_time: 0,
 * day_watch_course_time: 0,
 * wday: 2
 * }
 * ],
 * week_calorie: 0,
 * week_distance: 0,
 * week_time: 0,
 * week_training_time: 0,
 * week_watch_broadcast_time: 0
 * }
 * }
 */
public class RspSportRecord extends RspBase {

    public Data data = new Data();

    public class Data {
        public double week_calorie = 0;
        public double week_distance = 0;
        public double week_time = 0;
        public int week_training_time = 0;
        public int week_watch_broadcast_time = 0;
        public ArrayList<PreDaySportData> prec_list = new ArrayList<PreDaySportData>();
    }

    public class PreDaySportData {
        public double day_calorie = 0;
        public double day_distance = 0;
        public double day_time = 0;
        public int day_training_time = 0;
        public int day_watch_broadcast_time = 0;
        public int day_watch_course_time = 0;
        public int wday = 0;
    }
}



