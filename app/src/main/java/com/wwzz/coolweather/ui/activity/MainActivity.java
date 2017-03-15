package com.wwzz.coolweather.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wwzz.coolweather.R;
import com.wwzz.coolweather.gson.Weather;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        //如果之前用户已经选择过城市，则直接跳转到天气显示界面
        if(sharedPreferences.getString("weather",null)!=null){
            Intent intent=new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
