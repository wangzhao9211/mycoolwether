package com.wwzz.coolweather;

import android.app.Application;

import org.litepal.LitePal;

/**
 * 作者：wz created on 2017/3/6 09:35
 * 邮箱：wangzhao9211@163.com
 * 功能：
 */

public class MyApplication  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
