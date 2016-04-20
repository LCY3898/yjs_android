package com.sport.efit.constant;

import java.nio.channels.Channel;

/**
 * Created by Administrator on 2015/8/13 0013.
 */
public enum LoginChannel {
    CHANNEL_QQ("qq"),
    CHANNEL_WEIXIN("weixin"),
    CHANNEL_WEIBO("weibo"),
    CHANNEL_NORMAL("normal");


    private String channelName;
    private LoginChannel(String name) {
        this.channelName = name;
    }

    public String getChannelName() {
        return channelName;
    }

    public static LoginChannel fromName(String name) {
        LoginChannel []channels = values();
        for(LoginChannel channel:channels) {
            if(channel.getChannelName().equals(name)) {
                return channel;
            }
        }
        return CHANNEL_NORMAL;
    }

}
