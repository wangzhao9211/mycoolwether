package com.wwzz.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：wz created on 2017/3/8 11:48
 * 邮箱：wangzhao9211@163.com
 * 功能：
 */

public class Forecast {
    @SerializedName("date")
    public String data;
//
    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt_d")
        public String info;
    }
    public class Temperature{
        public String max;
        public String min;
    }
}
