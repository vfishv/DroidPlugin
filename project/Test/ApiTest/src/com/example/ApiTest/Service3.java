package com.example.ApiTest;

import android.util.Log;

import com.example.ApiTest.util.Util;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class Service3 extends Service1 {

    private static final String TAG = "Service3";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "pid:" + Util.getCurProcessName(this));
    }

    @Override
    String getTag() {
        return Service3.class.getSimpleName();
    }

    public Service3() {
        tag = getTag();
    }


}
