package com.efit.http;

import android.os.Looper;

import com.efit.http.abs.CloudReqCbk;
import com.efit.http.abs.ReqBase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * author: tangtt
 * <p>
 * 2016/2/3
 * </p>
 * <p>
 * <p/>
 * </p>
 */
public class EfitRestClient {
    private static final String BASE_URL = "apisrv.51efit.com";
    private static final String TEST_BASE_URL = "apisrvtest.51efit.com";
    private static String VERSION_NAME = "0.1";

    private static AsyncHttpClient client;

    static  {
        client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        client.addHeader("version",VERSION_NAME);
    }

    public static void appendParam(RequestParams params) {
        params.setUseJsonStreamer(true);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("test","test");
        appendParam(params);
        client.get(getAbsoluteUrl(url), params, responseHandler);

    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        appendParam(params);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        appendParam(params);
        client.put(getAbsoluteUrl(url),params,responseHandler);
    }


    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        appendParam(params);
        client.delete(url,params,responseHandler);
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }




    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }





    public static void sendRequest(ReqBase req,String subUrl, CloudReqCbk cbk) {

    }

      /**
     * 获取JsonObject
     * @param json
     * @return
     */
    public static JsonObject parseJson(String json){
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(json).getAsJsonObject();
        return jsonObj;
    }

    public static String buildGetReqData(ReqBase req, boolean appendQuestionMark) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(appendQuestionMark ? "?" : "");
            Map<String,Object> map = getReqMap(req);
            Set<Entry<String ,Object>> entrySet = map.entrySet();
            boolean first = true;
            for (Iterator<Entry<String, Object>> iter = entrySet.iterator(); iter.hasNext(); ){
                Entry<String, Object> entry = iter.next();
                if(!first) {
                    sb.append("&");
                }
                first = false;
                sb.append(String.format("%s=%s", entry.getKey(),entry.getValue()));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,Object> getReqMap(ReqBase req){
        JsonElement element = new Gson().toJsonTree(req,req.getClass());
        JsonObject json = element.getAsJsonObject();
        return toMap(json);
    }

    /**
     * 将JsonObject对象转换成Map-List集合
     * @param json
     * @return
     */
    public static Map<String, Object> toMap(JsonObject json){
        Map<String, Object> map = new HashMap<String, Object>();
        Set<Entry<String, JsonElement>> entrySet = json.entrySet();
        for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){
            Entry<String, JsonElement> entry = iter.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof JsonArray)
                map.put((String) key, toList((JsonArray) value));
            else if(value instanceof JsonObject)
                map.put((String) key, toMap((JsonObject) value));
            else
                map.put((String) key, value);
        }
        return map;
    }


    /**
     * 将JSONArray对象转换成List集合
     * @param json
     * @return
     */
    public static List<Object> toList(JsonArray json){
        List<Object> list = new ArrayList<Object>();
        for (int i=0; i<json.size(); i++){
            Object value = json.get(i);
            if(value instanceof JsonArray){
                list.add(toList((JsonArray) value));
            }
            else if(value instanceof JsonObject){
                list.add(toMap((JsonObject) value));
            }
            else{
                list.add(value);
            }
        }
        return list;
    }
}
