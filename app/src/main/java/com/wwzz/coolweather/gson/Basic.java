package com.wwzz.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：wz created on 2017/3/8 11:21
 * 邮箱：wangzhao9211@163.com
 * 功能：
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
