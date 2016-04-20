package com.danikula.videocache;

import android.content.Context;

import com.danikula.videocache.internal.FileNameGenerator;
import com.danikula.videocache.internal.HttpProxyCacheServer;
import com.danikula.videocache.internal.Md5FileNameGenerator;
import com.hhtech.base.AppUtils;

/**
 * Created by Administrator on 2015/9/13 0013.
 */
public class VideoProxyFactory {
    private static HttpProxyCacheServer proxy = null;

    public static HttpProxyCacheServer getProxy() {
        if(proxy == null) {
            synchronized (VideoProxyFactory.class) {
                if(proxy == null) {
                    proxy = createProxy();
                }
            }
        }
        return proxy;
    }

    private static HttpProxyCacheServer createProxy() {
        Context context = AppUtils.getAppContext();
        FileNameGenerator nameGenerator = new Md5FileNameGenerator(
                context.getExternalCacheDir());
        return new HttpProxyCacheServer(nameGenerator);
    }
}
