package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eijianshen on 15/8/21.
 */
public class RspCreateTask extends RspBase {


    public Data data=new Data();
    public class Data{
        public Task task=new Task();
    }


    public class Task{
        public String _id="";
        public String et="";
        public String status="";
        public String task_begin="";
        public String task_description="";
        public String task_end="";
        public List<TaskJoinerList> task_joiner_list=new ArrayList<TaskJoinerList>();
        public String task_joiner_num="";
        public String task_size="";
        public String task_sponsor="";
        public String task_state="";
        public String ask_time="";
        public String task_xtype="";


    }
    public class TaskJoinerList{
        public String f_id="";
        public String id="";
        public String join_colorie="";
        public String join_distance="";
        public String join_time="";
        public String nick_name="";
    }
}
