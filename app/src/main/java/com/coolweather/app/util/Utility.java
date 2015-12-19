package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/17.
 */
public class Utility {
    /*解析和处理服务器返回的省级数据*/
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvince = response.split(",");
            if (allProvince != null && allProvince.length > 0) {
                for (String p : allProvince) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /*解析和处理服务器返回的市级数据*/
    public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city =new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /*解析和处理服务器返回的县级数据*/
    public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /*解析服务器返回的具体城市天气信息的JSON数据，并存储到本地*/
    public static void handleWeatherResponse(Context context, String response) {
       /*百度api*/
       /* try {
            *//*改成了从百度api里获取，所以一些字符串（键）不一样*//*
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("retData");//这个api返回的pinyin键对应的是县所在的市
            String cityName = weatherInfo.getString("city");
            String countyCode = weatherInfo.getString("citycode");//有问题，“cityid”对应的应该是countiCode，而且没啥用
            String temp1 = weatherInfo.getString("l_tmp");
            String temp2 = weatherInfo.getString("h_tmp");
            String weatherDesp= weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("time");
            saveWeatherInfo(context, cityName, countyCode, temp1, temp2, weatherDesp, publishTime);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*中国天气网api*/
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String countyCode = weatherInfo.getString("cityid");//有问题，“cityid”对应的应该是countiCode，而且没啥用
            String temp1 = weatherInfo.getString("temp2");
            String temp2 = weatherInfo.getString("temp1");
            String weatherDesp= weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, countyCode, temp1, temp2, weatherDesp, publishTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*将服务器返回的具体城市的天气信息存储到SharedPreference文件中*/
    public static void saveWeatherInfo(Context context, String cityName, String countyCode,
                                       String temp1, String temp2, String weatherDesp,
                                       String publishTime) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("county_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("county_code", countyCode);//问题同上
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
//        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
