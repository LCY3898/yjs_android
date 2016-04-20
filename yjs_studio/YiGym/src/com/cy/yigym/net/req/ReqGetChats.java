package com.cy.yigym.net.req;

import com.cy.wbs.ReqBase;

public class ReqGetChats extends ReqBase{


    /**
     * obj : chat
     * act : getChats
     * pid : o14406473304927809238
     * aid : o14406448912972009181
     * time_stamp : 0
     * client_info : {"clientType":"webapp","userId":null}
     * debug : 1
     */

    private String obj="chat";
    private String act="getChats";
    private String aid;
    private String time_stamp;
    private ClientInfoEntity client_info;
    private String debug="1";
    
    public ReqGetChats(String pid, String aid, String time_stamp) {
		super();
		this.pid = pid;
		this.aid = aid;
		this.time_stamp = time_stamp;
	}

	public void setObj(String obj) {
        this.obj = obj;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public void setClient_info(ClientInfoEntity client_info) {
        this.client_info = client_info;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public String getObj() {
        return obj;
    }

    public String getAct() {
        return act;
    }

    public String getPid() {
        return pid;
    }

    public String getAid() {
        return aid;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public ClientInfoEntity getClient_info() {
        return client_info;
    }

    public String getDebug() {
        return debug;
    }

    public static class ClientInfoEntity {
        /**
         * clientType : webapp
         * userId : null
         */

        private String clientType="webapp";
        private Object userId;

        public void setClientType(String clientType) {
            this.clientType = clientType;
        }

        public void setUserId(Object userId) {
            this.userId = userId;
        }

        public String getClientType() {
            return clientType;
        }

        public Object getUserId() {
            return userId;
        }
    }
}
