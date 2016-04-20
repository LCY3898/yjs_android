package com.cy.yigym.event;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2015/10/23.
 */
public class EventNaviToFrag {
    public String fragCls;

    public EventNaviToFrag(Class<? extends Fragment> fragCls) {
        this.fragCls = fragCls.getName();
    }
}
