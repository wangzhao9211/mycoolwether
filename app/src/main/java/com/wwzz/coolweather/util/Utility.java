package com.wwzz.coolweather.util;

import android.text.TextUtils;

import com.wwzz.coolweather.model.City;
import com.wwzz.coolweather.model.County;
import com.wwzz.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：wz created on 2017/3/6 10:28
 * 邮箱：wangzhao9211@163.com
 * 功能：解析处理json数据
 */

public class Utility {

    /*
    * 解析和处理服务器返回的省级数据
    * */
    public static boolean handleProvinceResponse(String response){
//        response不为空，长度大于零
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces=new JSONArray(response);
                for(int i=0;i< allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    province.save();//保存省对象到数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }
    /*
    * 解析处理服务器返回的市级数据
    * */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i< allCities.length();i++){
                    JSONObject cityOject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityCode(cityOject.getInt("id"));
                    city.setCityName(cityOject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();//保存省对象到数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    /*
    * 解析处理服务器返回的县级数据
    *
    * */
    public static boolean handleCountyResponse(String response ,int CityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties=new JSONArray(response);
                for(int i=0;i< allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCityId(CityId);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }
}
