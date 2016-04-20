package com.efit.sport.p2p.internal;

import android.util.SparseArray;


public class CallFailReason {

    private static final SparseArray<String> sReasonsMap = new SparseArray<String>();

    static {

        sReasonsMap.put(175603 , "对方拒绝了您的请求");//视频通话未接听
        // 对方不在线
        sReasonsMap.put(175404 , "对方不在线");
        // 呼叫超时
        sReasonsMap.put(175408 , "呼叫超时");
        // 无人应答vl
        sReasonsMap.put(175409 , "无人应答");
        // 对方正忙(对方非主动拒接)
        sReasonsMap.put(175486 , "对方正忙");
        // 媒体协商失败(有可能初始化音频失败)
        sReasonsMap.put(175488 , "媒体协商失败");
        // 第三方鉴权地址连接失败
        sReasonsMap.put(175700 ,"第三方鉴权地址连接失败");
        // 第三方应用ID未找到
        sReasonsMap.put(175702 , "第三方应用ID未找到");
        // 第三方未上线应用仅限呼叫已配置测试号码
        sReasonsMap.put(175704 , "第三方未上线应用仅限呼叫已配置测试号码");
        // 第三方鉴权失败，子账号余额不足
        sReasonsMap.put(175705 ,"第三方鉴权失败，子账号余额不足");
        // 第三方主账号余额不足
        sReasonsMap.put(175710 , "第三方主账号余额不足");
    }

    public static String getCallFailReason(int reason) {
        if(sReasonsMap == null || sReasonsMap.indexOfKey(reason) < 0) {
            return "呼叫失败";
        }
        return sReasonsMap.get(reason);
    }
}
