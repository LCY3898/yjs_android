package com.efit.http.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * author: tangtt
 * <p>
 * 2016/2/3
 * </p>
 * <p>
 * <p/>
 * http响应消息体
 * </p>
 */
public class RspRawBody {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;


    @SerializedName("data")
    public JsonObject data;

    @SerializedName("srv_info")
    public ServerInfo serverInfo;

    public static class ServerInfo {
        @SerializedName("host_id")
        public String hostId; //服务器ID(目前采用IP标识)

        @SerializedName("server_time")
        public String time;
    }
}
