package com.cy.yigym.net.req;

import com.cy.yigym.utils.DataStorageUtils;

/**
 * Created by ejianshen on 15/7/21.
 * 完善个人信息（注册后跳转的页面）
 * {"obj":"person","act":"accomplishInfo","phone":"18559160624","display_name":"cmc","sex":"male","age":"21","height":"173","weight":"80","debug":1}
 */
public class ReqAccomplishInfo extends PersonReqBase {
    private String act="accomplishInfo";
    private String nick_name="";
    private String sex="";
    private String age="";
    private String height="";
    private String weight="";
    public ReqAccomplishInfo(String nick_name,String sex,String age,String height,String weight){
        super();
        this.nick_name=nick_name;
        this.sex=sex;
        this.age=age;
        this.height=height;
        this.weight=weight;
    }


}
