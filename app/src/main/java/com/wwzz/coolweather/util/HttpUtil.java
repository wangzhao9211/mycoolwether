package com.wwzz.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 作者：wz created on 2017/3/6 10:23
 * 邮箱：wangzhao9211@163.com
 * 功能：网络访问工具类，对okhttp简单封装
 */

public class HttpUtil {
//    发送网络请求
    /*
    * 网络地址address
    * 网络访问回调事件callback
    */
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
