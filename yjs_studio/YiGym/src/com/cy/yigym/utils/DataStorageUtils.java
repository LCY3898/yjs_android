package com.cy.yigym.utils;

import android.text.TextUtils;

import com.cy.yigym.entity.LiveVideoSportData;
import com.cy.yigym.net.rsp.RspGetLastCourse;
import com.cy.yigym.net.rsp.RspGetUserInfo;
import com.cy.yigym.net.rsp.RspLogin;
import com.google.gson.Gson;
import com.sport.efit.constant.LoginChannel;

/**
 * Caiyuan Huang
 * <p>
 * 2015-7-13
 * </p>
 * <p>
 * 数据保存工具类，用于保存轻量级的用户数据
 * </p>
 */
public class DataStorageUtils {
    private static SharedPreferenceUtils sp = new SharedPreferenceUtils();

    /**
     * 设置被搜索用户的pid
     *
     * @param pid
     */
    public static void setOtherPid(String pid) {
        sp.saveString("otherPid", pid);
    }

    /**
     * 获取pid
     *
     * @return
     */
    public static String getOtherPid() {
        return sp.getString("otherPid", "");
    }

    /**
     * 设置pid
     *
     * @param pid
     */
    public static void setPid(String pid) {
        sp.saveString("pid", pid);
    }

    /**
     * 获取pid
     *
     * @return
     */
    public static String getPid() {
        return sp.getString("pid", "");
    }

    /**
     * 设置课程id
     *
     * @param courseId
     */
    public static void setCourseId(String courseId) {
        sp.saveString("CourseId", courseId);
    }

    /**
     * 获取课程id
     *
     * @return
     */
    public static String getCourseId() {
        return sp.getString("CourseId", "");
    }

    /**
     * 设置当前用户的昵称
     *
     * @param nickname
     */
    /**
     * 保存容联账号
     *
     * @param nickname
     */
    public static void setUserNickName(String nickname) {
        sp.saveString("NickName", nickname);
    }

    /**
     * 获取容联账号
     *
     * @return
     */
    public static String getVoipAccount() {
        RspLogin.SubAccount subAccount = getVoipObj();
        if (subAccount == null) {
            return "";
        }
        return subAccount.voipAccount;
    }

    public static RspLogin.SubAccount getVoipObj() {
        String json = sp.getString("voip_obj", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return new Gson().fromJson(json, RspLogin.SubAccount.class);
    }

    public static void setVoipObj(RspLogin.SubAccount account) {
        if (account == null) {
            sp.saveString("voip_obj", "");
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(account);
        sp.saveString("voip_obj", json);
    }

    /**
     * 获取网易账号信息
     *
     * @return
     */
    public static RspLogin.NetEaseAccount getNetEaseAccount() {
        String json = sp.getString("netEase_account", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return new Gson().fromJson(json, RspLogin.NetEaseAccount.class);
    }

    /**
     * 保存网易账号信息
     *
     * @param account
     */
    public static void setNetEaseAccount(RspLogin.NetEaseAccount account) {
        if (account == null) {
            sp.saveString("netEase_account", "");
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(account);
        sp.saveString("netEase_account", json);
    }

    /**
     * 获取当前用户的昵称
     *
     * @return
     */
    public static String getUserNickName() {
        return sp.getString("NickName", "");
    }

    /**
     * 设置当前用户的头像文件id
     *
     * @param profileFid
     */
    public static void setCurUserProfileFid(String profileFid) {
        sp.saveString("cur_user_profile_fid", profileFid);
    }

    /**
     * 设置总积分
     *
     * @param score
     */
    public static void setTotalScore(String score) {
        sp.saveString("score", score);
    }

    public static String getTotalScore() {
        return sp.getString("score", "");
    }

    /**
     * 获取当前用户的头像文件id
     *
     * @return
     */
    public static String getCurUserProfileFid() {
        return sp.getString("cur_user_profile_fid", "");
    }

    /**
     * 设置图片上传地址
     *
     * @param url
     */
    public static void setUploadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }
        sp.saveString("upload_url", url);
    }

    /**
     * 获取图片上传地址
     *
     * @return
     */
    public static String getUploadUrl() {
        return sp.getString("upload_url",
                "http://121.40.16.113:8081/cgi-bin/upload.pl");
    }

    /**
     * 设置下载地址
     *
     * @param url
     */
    public static void setDownloadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        sp.saveString("download_url", url);
    }

    /**
     * 获取文件下载地址
     *
     * @return
     */
    public static String getDownloadUrl() {
        return sp.getString("download_url",
                "http://121.40.16.113:8081/cgi-bin/download.pl");
    }

    /**
     * 获取头像下载地址
     *
     * @param fid
     * @return
     */
    public static String getHeadDownloadUrl(String fid) {
        if (TextUtils.isEmpty(fid))
            return "";
        if (fid.contains("http://"))
            return fid;

        String url = getDownloadUrl();
        if (url.endsWith("fid=")) {
            return url + fid;
        }
        return url + fid; // 去掉 "?fid=" 20151207 ljq
    }

    /**
     * 设置mp4文件的网址前缀
     *
     * @param url
     */
    public static void setMp4LinkPrefix(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        sp.saveString("mp4_link_prefix", url);
    }

    /**
     * 获取mp4文件的网址前缀
     *
     * @param url
     */
    public static String getMp4LinkPrefix() {
        return sp.getString("mp4_link_prefix",
                "http://121.40.16.113:8082/yjs/mp4/");
    }

    /**
     * 获取Mp4视频播放地址
     *
     * @param fid 视频文件id
     * @return
     */
    public static String getMp4Url(String fid) {
        if (TextUtils.isEmpty(fid))
            return "";
        if (fid.startsWith("http")) {
            return fid;
        }
        return getMp4LinkPrefix() + fid + ".mp4";
    }

    /**
     * 同步download_url等信息的日期
     */
    public static String getServerInfoSyncDate() {
        return sp.getString("server_info_sync_date", "");
    }

    public static void setServerInfoSyncDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return;
        }
        sp.saveString("server_info_sync_date", date);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static RspGetUserInfo getUserInfo() {
        return (RspGetUserInfo) sp.getObject("user_info", new RspGetUserInfo());
    }

    /**
     * 设置用户信息
     *
     * @param userInfo
     */
    public static void setUserInfo(RspGetUserInfo userInfo) {
        sp.saveObject("user_info", userInfo);
    }

    public static String getBleAddress() {
        return sp.getString("ble_addr", "");
    }

    public static void setBleAddress(String bleAddress) {
        if (bleAddress == null) {
            bleAddress = "";
        }
        sp.saveString("ble_addr", bleAddress);
    }

    public static void setChasePrefInfo(String info) {
        if (info == null) {
            info = "";
        }
        sp.saveString("chase_pref_info", info);
    }

    public static String getChasePrefInfo() {
        return sp.getString("chase_pref_info", "");
    }

    public static void setContentViewHeight(int contentViewHeight) {
        sp.saveInt("content_view_height", contentViewHeight);
    }

    public static int getContentViewHeight() {
        return sp.getInt("content_view_height", 0);
    }

    public static boolean isBleFirstOp() {
        return sp.getBoolean("is_ble_first", true);
    }

    public static void setIsBleFirstOp(boolean flag) {
        sp.saveBoolean("is_ble_first", flag);
    }

    public static String getSession() {
        return sp.getString("session", "");
    }

    public static void setSession(String session) {
        if (session == null) {
            session = "";
        }
        sp.saveString("session", session);
    }

    public static void setUnreadMsgCount(int num) {
        sp.saveInt("unread_system_notice", num);
    }

    public static int getUnreadMsgCount() {
        return sp.getInt("unread_system_notice", 0);
    }

    public static void setLoginChannel(LoginChannel channel) {
        if (channel == null) {
            return;
        }
        sp.saveString("login_channel", channel.getChannelName());
    }

    public static LoginChannel getLoginChannel() {
        String name = sp.getString("login_channel",
                LoginChannel.CHANNEL_NORMAL.getChannelName());
        return LoginChannel.fromName(name);
    }

    public static void setLogin(boolean isLogin) {
        sp.saveBoolean("is_login", isLogin);
    }

    public static boolean isLogin() {
        return sp.getBoolean("is_login", false);
    }

    public static void setUserInfoComplete(boolean isComplete) {
        sp.saveBoolean("is_user_info_complete", isComplete);
    }

    public static boolean isUserInfoComplete() {
        return sp.getBoolean("is_user_info_complete", false);
    }

    public static void setVideoPauseTime(int time) {
        sp.saveInt("last_pause_time", time);
    }

    public static int getVideoPauseTime() {
        return sp.getInt("last_pause_time", 0);
    }

    /**
     * 是否显示新消息的小红点
     */
    public static boolean isShowMsgIndicator() {
        return sp.getBoolean("is_show_msg_indicator", false);
    }

    public static void setShowMsgIndicator(boolean show) {
        sp.saveBoolean("is_show_msg_indicator", show);
    }

    public static void setLiveVideoData(LiveVideoSportData data) {
        if (data == null) {
            sp.saveString("last_live_video_data", "");
            return;
        }
        sp.saveString("last_live_video_data", new Gson().toJson(data));
    }

    public static LiveVideoSportData getLiveVideoData() {
        String json = sp.getString("last_live_video_data", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return new Gson().fromJson(json, LiveVideoSportData.class);
    }

    public static boolean isUserInfoChange() {
        return sp.getBoolean("is_user_info_change", false);
    }

    public static void setUserInfoChange(boolean status) {
        sp.saveBoolean("is_user_info_change", status);
    }

    public static void setMedalNum(int medalNum) {
        sp.saveInt("is_click_medal_num", medalNum);
    }

    public static int getMedalNum() {
        return sp.getInt("is_click_medal_num", 0);
    }

    public static void setEditContent(String content) {
        sp.saveString("is_edit_content", content);
    }

    public static String getEditContent() {
        return sp.getString("is_edit_content", "");
    }

    /**
     * 环信account
     *
     * @param account
     */
    public static void setHxAccount(String account) {
        if (account == null) {
            account = "";
        }

        sp.saveString("hx_account", account);
    }

    /**
     * 获得环信account
     *
     * @param
     */
    public static String getHxAccount() {
        return sp.getString("hx_account", "");
    }

    public static void setMedalNumForEditText(int medalNum) {
        sp.saveInt("is_click_medal_num_ForEditText", medalNum);
    }

    public static int getMedalNumForEditText() {
        return sp.getInt("is_click_medal_num_ForEditText", 0);
    }

    public static void saveLastCourse(RspGetLastCourse.Data data) {
        if (data == null) {
            sp.saveString("getLastCourse", "");
        }
        String json = new Gson().toJson(data);
        sp.saveString("getLastCourse", json);
    }

    public static RspGetLastCourse.Data getLastCourse() {
        String json = sp.getString("getLastCourse", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return new Gson().fromJson(json, RspGetLastCourse.Data.class);
    }

    public static void setInClubMode(boolean isClubMode) {
        sp.saveBoolean("is_in_club_mode", isClubMode);
    }

    public static boolean isInClubMode() {
        return sp.getBoolean("is_in_club_mode", false);
    }

    /**
     * 是否已经引导过了
     *
     * @return
     */
    public static boolean isHasGuide() {
        return sp.getBoolean("guide_flag", false);
    }

    /**
     * 设置是否已经引导的标记
     *
     * @param isHasGuide
     */
    public static void setGuideFlag(boolean isHasGuide) {
        sp.saveBoolean("guide_flag", isHasGuide);
    }

    /**
     * 设置是否已经加载直播课程
     * @param isGetLastCourse
     */
    public static void setGetLastCourse(boolean isGetLastCourse) {
        sp.saveBoolean("is_get_last_course", isGetLastCourse);
    }

    /**
     * 获得是否加载直播课程的标志
     * @return
     */
    public static boolean isGetLastCourse() {
        return sp.getBoolean("is_get_last_course", true);
    }
}
