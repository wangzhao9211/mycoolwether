package com.wwzz.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.wwzz.coolweather.gson.Weather;
import com.wwzz.coolweather.util.HttpUtil;
import com.wwzz.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ServiceAutoUpdateWeather extends Service {
    public ServiceAutoUpdateWeather() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingic();
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour= 8*60*60*1000;//8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent intentService=new Intent(this,ServiceAutoUpdateWeather.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,intentService,0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        //为了保证软件不会消耗过多的流量，

        return super.onStartCommand(intent, flags, startId);
    }
/*
* 更新必应图片
* */
    private void updateBingic() {
        String url="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String picString=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(ServiceAutoUpdateWeather.this).edit();
                editor.putString("bing_pic",picString);
                editor.apply();

            }
        });

    }
    /*
    * 更新天气信息
    * */
    private void updateWeather() {
        SharedPreferences sha
                = PreferenceManager.getDefaultSharedPreferences(this);
        String  weatherString=sha.getString("weather",null);
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
//            如果是第一次的时候，已经缓存数据，所以直接从缓存的数据之中拿到weather——ID
            String weatherURL="http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatherURL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(responseText);
                    if(weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(ServiceAutoUpdateWeather.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }

                }
            });
        }

    }
}
