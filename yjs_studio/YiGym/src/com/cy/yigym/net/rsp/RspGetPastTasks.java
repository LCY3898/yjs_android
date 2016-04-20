package com.cy.yigym.net.rsp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/8/21.
 */
public class RspGetPastTasks {
    public class Data{
        public List<Tasks> tasks=new ArrayList<Tasks>();
    }

    public class Tasks{
        public List<TaskJoinerList> task_joiner_list=new ArrayList<TaskJoinerList>();
        public String _id="";
        public String et="";
        public String status="";
        public String task_begin="";
        public String task_description="";
        public String task_end="";
        public int task_joiner_num=1;
        public String task_size="";
        public String task_sponsor="";
        public String task_state="";
        public String time="";
        public String task_title="";
        public String task_xtype="";
    }
    public class TaskJoinerList{

    }
}
