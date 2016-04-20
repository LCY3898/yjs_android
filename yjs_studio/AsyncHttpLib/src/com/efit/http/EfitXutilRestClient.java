package com.efit.http;

import android.text.TextUtils;

import com.efit.http.abs.CloudReqCbk;

import org.xutils.HttpManager;
import org.xutils.common.Callback.ProgressCallback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.http.body.StringBody;
import org.xutils.x;

import java.io.UnsupportedEncodingException;

/**
 * author: tangtt
 * <p>
 * 2016/2/3
 * </p>
 * <p>
 * <p/>
 * </p>
 */
public class EfitXutilRestClient {
    private static final String BASE_URL = "apisrv.51efit.com";
    private static final String TEST_BASE_URL = "apisrvtest.51efit.com";
    private static String VERSION_NAME = "0.1";

    private static HttpManager httpClient;

    private static EfitXutilRestClient sInstance;

    private EfitXutilRestClient() {
        httpClient = x.http();
    }

    public static void appendParam(RequestParams params,String reqStr,String token) {
        params.addHeader("Content-Type", "application/json");
        params.addHeader("version", VERSION_NAME);
        try {
            params.setRequestBody(new StringBody(reqStr,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(token)) {
            params.addHeader("Efit-Token", VERSION_NAME);
        }
    }

    public static void sendRequest(HttpMethod method,String url, String reqStr, String token, final CloudReqCbk cbk) {
        RequestParams params = new RequestParams(url);
        appendParam(params,reqStr,token);
        httpClient.request(method, params, new ProgressCallback<String>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


}
