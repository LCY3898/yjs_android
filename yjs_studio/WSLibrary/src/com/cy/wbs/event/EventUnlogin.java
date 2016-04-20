package com.cy.wbs.event;

/**
 * author: tangtt
 * <p>
 * 2015/11/26
 * </p>
 * <p>
 * <p/>
 * </p>
 */
public class EventUnlogin {

    public UnloginReason reason;
    public enum UnloginReason {
        USER_NOT_EXIST,
        KICK_OFF,
        SESSION_TIMEOUT;
    }
    public EventUnlogin(UnloginReason reason) {
        this.reason = reason;
    }
}
