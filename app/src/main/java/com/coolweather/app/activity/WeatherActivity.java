package com.coolweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2015/12/18.
 */
public class WeatherActivity extends Activity {

    private LinearLayout weatherInfoLayout;

    private TextView cityNameText;

    private TextView publishText;

    private TextView currentDateText;

    private TextView weatherDespText;

    private TextView temp1Text;

    private TextView temp2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        currentDateText = (TextView) findViewById(R.id.current_date);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);


        String countyCode = getIntent().getStringExtra("county_code");//是从主活动传过来的

        if (!TextUtils.isEmpty(countyCode)) {
            //有县级代号，countyCode时，就去查天气，先查对应的天气码，再根据天气码查具体天气
            //上一条注释是从中国天气网api查，但是不行。换一个api，直接用城市id就可查，即countyCode就可查天气信息。
            publishText.setText("同步中");
            cityNameText.setVisibility(View.INVISIBLE);
            weatherInfoLayout.setVisibility(View.INVISIBLE);
//            queryWeatherCode(countyCode);//中国天气网的
            queryWeatherInfoFromBaidu(countyCode);//从百度api
        } else {
            showWeather();
        }
    }

    /*查询县级代号所对应的天气代号*//*
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }
    *//*查询天气代号所对应的天气*//*
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }
    *//*根据传入的地址和类型去服务器查询天气代号或天气信息*//*
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }*/

    private void queryWeatherInfoFromBaidu(String countyCode){
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityid";
        String httpArg = "cityid=101" + countyCode;
        String address = httpUrl + "?" + httpArg;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /*从SharedPreferences文件中读取存储的天气信息，并显示在界面上*/
    private void showWeather() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        cityNameText.setText(prefs.getString("city_name", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
//        currentDateText.setText("current_date", "");
        currentDateText.setText(sdf.format(new Date()));//这里再获取时间，再格式化时间才对吧。
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));

        cityNameText.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
    }

}
