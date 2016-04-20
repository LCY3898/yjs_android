/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.efit.hxmob.impl;

import android.text.TextUtils;
import android.util.Log;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.yigym.utils.DataStorageUtils;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.hhtech.base.AppUtils;
import com.hhtech.base.BuildConfig;
import com.hhtech.utils.LogToFile;

public class EaseContext {
    private final static String TAG = EaseContext.class.getSimpleName();
    private static EaseContext instance;

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";

    public static HXSDKHelperImpl hxSDKHelper = new HXSDKHelperImpl();
    private boolean isLogin = false;

    public void init() {
        /**
         * this function will initialize the HuanXin SDK
         *
         * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
         *
         * 环信初始化SDK帮助函数
         * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         *
         * for example:
         * 例子：
         *
         * public class DemoHXSDKHelper extends HXSDKHelper
         *
         * HXHelper = new DemoHXSDKHelper();
         * if(HXHelper.onInit(context)){
         *     // do HuanXin related work
         * }
         */
        boolean success = hxSDKHelper.onInit(AppUtils.getAppContext());
        if(!success) {
            LogToFile.e(TAG,"huanxin init fail");
            return;
        }
    }

    public static EaseContext getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (EaseContext.class) {
            if (instance == null) {
                instance = new EaseContext();
            }
        }
        return instance;
    }


  /*  *//**
     * 获取当前登陆用户名
     *
     * @return
     *//*
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }


    *//**
     * 设置用户名
     *
     * @param username
     *//*
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }
*/

    /**
     * 退出登录,清空数据
     */
    public void logout() {
        // 先调用sdk logout，在清理app中自己的数据
        hxSDKHelper.logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                isLogin = false;
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 注册或登录
     * @param userName
     * @param pwd
     * @param nickname
     */
    public void loginOrReg(final String userName,final String pwd, final String nickname) {
        AppUtils.runOnUIDelayed(200, new Runnable() {
            @Override
            public void run() {
                String account = DataStorageUtils.getHxAccount();
                if(TextUtils.isEmpty(account) || !account.equals(userName)) {
                    registerThenLogin(userName, pwd, nickname);
                } else {
                    login(userName,pwd,nickname);
                }
            }
        });
    }


    private void login(final String userName, String pwd, final String nickname) {
       /* if(isLogin) {
            return;
        }*/

        EMChatManager.getInstance().login(userName, pwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                isLogin = true;
                DataStorageUtils.setHxAccount(userName);
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                EMChatManager.getInstance().updateCurrentUserNick(nickname);
                Log.i(TAG, "EaseContext login success");
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                LogToFile.e(TAG, "视频聊天功能登录失败:" + message);
                if (BuildConfig.DEBUG) {
                    WidgetUtils.showToast("视频聊天功能登录失败:" + message);
                }
            }
        });
    }


    /**
     * 注册并登录
     *
     */
    private void registerThenLogin(final String userName, final String pwd, final String nickname) {

            new Thread(new Runnable() {
                public void run() {
                    try {
                        // 调用sdk注册方法
                        EMChatManager.getInstance().createAccountOnServer(userName, pwd);
                        AppUtils.runOnUI(new Runnable() {
                            @Override
                            public void run() {
                                login(userName, pwd, nickname);
                            }
                        });
                        DataStorageUtils.setHxAccount(userName);
                    } catch (final EaseMobException e) {
                        AppUtils.runOnUI(new Runnable() {
                            @Override
                            public void run() {
                                int errorCode = e.getErrorCode();
                                if (errorCode == EMError.USER_ALREADY_EXISTS) {//用户已存在，直接登录
                                    login(userName, pwd, nickname);
                                } else if (errorCode == EMError.UNAUTHORIZED) {
                                    WidgetUtils.showToast("应用未授权");
                                } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                    WidgetUtils.showToast("非法的用户名");
                                } else if (errorCode == EMError.NONETWORK_ERROR) {
                                    WidgetUtils.showToast("网络异常");
                                }

                            }
                        });
                    }
                }
            }).start();
    }

}
