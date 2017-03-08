package com.wwzz.coolweather.gson;

/**
 * 作者：wz created on 2017/3/8 11:26
 * 邮箱：wangzhao9211@163.com
 * 功能：
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
