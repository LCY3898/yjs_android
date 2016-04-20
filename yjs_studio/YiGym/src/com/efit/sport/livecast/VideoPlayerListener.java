package com.efit.sport.livecast;

/**
 * Created by Administrator on 2015/11/3 0003.
 */
public interface VideoPlayerListener {
    void onNetworkError(int code);
    void onServerError();
    void onUriAddrError();
    void onBuffering(boolean isBuffering);
}
