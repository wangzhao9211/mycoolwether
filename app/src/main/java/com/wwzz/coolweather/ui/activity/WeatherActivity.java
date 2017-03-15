package com.wwzz.coolweather.ui.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wwzz.coolweather.R;
import com.wwzz.coolweather.gson.Forecast;
import com.wwzz.coolweather.gson.Weather;
import com.wwzz.coolweather.util.HttpUtil;
import com.wwzz.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView scrollViewWeatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private ImageView ImageBing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
//初始化控件
        initView();
// 获取天气信息
        getData();

    }

    private void getData() {

        SharedPreferences shaprefence
                = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = shaprefence.getString("weather", null);


        String bingPic= shaprefence.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(WeatherActivity.this).load(bingPic).into(ImageBing) ;
        }else{
            loadBingPic();
        }

        if (weatherString == null) {
            //无缓存时去服务器查询天气信息
            String weatherId = getIntent().getStringExtra("weather_id");
            scrollViewWeatherLayout
                    .setVisibility(View.INVISIBLE);//提前站好位置，防止画面闪动。提高用户体验

            requestWeatherInfo(weatherId);

        } else {
            // 有缓存时直接解析天气信息
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }
    }

    /*
    * 根据天气ID请求城市天气信息
    * */
       public void requestWeatherInfo(final String weatherId){
           String url="http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
           HttpUtil.sendOkHttpRequest(url, new Callback() {
               @Override
               public void onFailure(Call call, IOException e) {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                       }
                   });
               }

               @Override
               public void onResponse(Call call, Response response) throws IOException {
                   final String responseText=response.body().string();
                   final Weather weather=Utility.handleWeatherResponse(responseText);

                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                        if(weather==null||!"ok".equals(weather.status)){
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }else{
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                                    editor.putString("weather",responseText);
                            editor.apply();

                            showWeatherInfo(weather);
                        }
                       }
                   });

               }
           });

     }



/*
* 展示weather实体类中的数据
* */
    private void showWeatherInfo(Weather weather) {
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();//今日天气概况显示列表
        for (Forecast forecast:weather.forecastList){
//            遍历天气的预报list
//            动态添加view
            View view= LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dataText= (TextView) view.findViewById(R.id.tv_date);
            TextView infoText= (TextView) view.findViewById(R.id.tv_info);
            TextView maxText= (TextView) view.findViewById(R.id.tv_max);
            TextView minText= (TextView) view.findViewById(R.id.tv_min);
            dataText.setText(forecast.data);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }

        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carwash="洗车指数："+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carwash);
        sportText.setText(sport);
        scrollViewWeatherLayout.setVisibility(View.VISIBLE);

    }

    /*
    * 控件初始化
    * */
    private void initView() {
        scrollViewWeatherLayout= (ScrollView) findViewById(R.id.weatherScroll_layout);
        titleCity= (TextView) findViewById(R.id.tv_title_city);
        titleUpdateTime= (TextView) findViewById(R.id.tv_title_update_time);
        degreeText= (TextView) findViewById(R.id.tv_degree);
        weatherInfoText= (TextView) findViewById(R.id.tv_degree);
        forecastLayout= (LinearLayout) findViewById(R.id.layout_forecast);
        aqiText= (TextView) findViewById(R.id.tv_aqi);
        pm25Text= (TextView) findViewById(R.id.tv_PM25);
        comfortText= (TextView) findViewById(R.id.tv_comfort);
        carWashText= (TextView) findViewById(R.id.tv_car_wash);
        sportText= (TextView) findViewById(R.id.tv_sport);
        ImageBing= (ImageView) findViewById(R.id.img_bing_pic);

    }

    /*
    * 加载必应每日一图
    * */
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(ImageBing);
                    }
                });
            }
        });
    }

}
