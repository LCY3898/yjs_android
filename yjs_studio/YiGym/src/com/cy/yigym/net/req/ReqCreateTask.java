package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * {"obj":"tasks","act":"createTask","pid":"o14370593611595270633","task_title":"跑步！",
 * "task_xtype":"distance","task_size":555,"begin_mon":"8","begin_mday":"19",
 * "begin_hour":"10","begin_min":"15","end_mon":"8","end_mday":"19","end_hour":"10",
 * "end_min":"19","client_info":{"clientType":"webapp","userId":null},"debug":1}
 *
 *
 * Created by eijianshen on 15/8/21.
 */
public class ReqCreateTask extends TaskReqBase{
    private String act="createTask";
    private String task_title="";
    private String task_xtype="";
    private String task_size="";
    private String begin_year="";
    private String begin_mon="";
    private String begin_mday="";
    private String begin_hour="";
    private String begin_min="";
    private String end_year="";
    private String end_mon="";
    private String end_mday="";
    private String end_hour="";
    private String end_min="";
    private String task_description;


    public ReqCreateTask(String task_title,String task_xtype,String task_size,String begin_year,
                         String begin_mon, String begin_mday, String begin_hour,String begin_min,
                         String end_year,String end_mon,String end_mday,String end_hour,String end_min,String task_description){
        this.task_title=task_title;
        this.task_xtype=task_xtype;
        this.task_size=task_size;
        this.begin_year=begin_year;
        this.begin_mon=begin_mon;
        this.begin_mday=begin_mday;
        this.begin_hour=begin_hour;
        this.begin_min=begin_min;
        this.end_year=end_year;
        this.end_mon=end_mon;
        this.end_mday=end_mday;
        this.end_hour=end_hour;
        this.end_min=end_min;
        this.task_description=task_description;
    }
}
