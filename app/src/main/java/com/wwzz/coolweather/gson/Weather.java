package com.wwzz.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 作者：wz created on 2017/3/8 13:53
 * 邮箱：wangzhao9211@163.com
 * 功能：
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
