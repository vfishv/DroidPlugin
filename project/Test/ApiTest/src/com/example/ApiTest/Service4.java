package com.example.ApiTest;

import android.util.Log;

import com.example.ApiTest.util.Util;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class Service4 extends Service2 {

    private static final String TAG = "Service4";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "pid:" + Util.getCurProcessName(this));
    }

    @Override
    String getTag() {
        return Service4.class.getSimpleName();
    }

    public Service4() {
        tag = getTag();
    }


}
