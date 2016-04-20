package com.cy.yigym.entity;

import java.io.Serializable;

/**
 * Created by eijianshen on 15/8/20.
 */
public class TaskData implements Serializable{

    public final  static String TASK_DATA="task_data";
    public String  taskTitle;
    public String taskStartTime;
    public String TaskFinishTime;
    public String taskTarget;
    public String taskId;

    public TaskData(String taskTitle, String taskStartTime, String TaskFinishTime, String taskTarget,String taskId){
        this.taskTitle=taskTitle;
        this.taskStartTime=taskStartTime;
        this.TaskFinishTime=TaskFinishTime;
        this.taskTarget=taskTarget;
        this.taskId=taskId;
    }

}
