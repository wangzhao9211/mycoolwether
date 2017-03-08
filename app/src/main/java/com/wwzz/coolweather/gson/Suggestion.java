package com.wwzz.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：wz created on 2017/3/8 11:36
 * 邮箱：wangzhao9211@163.com
 * 功能：
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    @SerializedName("sport")
    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }

}
