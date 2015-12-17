package com.coolweather.app.util;

/**
 * Created by Administrator on 2015/12/17.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
