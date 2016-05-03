package com.example.ApiTest;

import android.app.Application;
import android.util.Log;

import com.example.ApiTest.util.Util;

/**
 * Created by cheungquentin on 16/5/3.
 */
public class MyApp extends Application {

    private static final String TAG = "MyApp";
    
    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "pid:" + Util.getCurProcessName(this));
    }
}
