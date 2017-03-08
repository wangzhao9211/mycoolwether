package com.wwzz.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：wz created on 2017/3/8 11:27
 * 邮箱：wangzhao9211@163.com
 * 功能：
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public  class More{
        @SerializedName("txt")
        public String info;
    }
}
