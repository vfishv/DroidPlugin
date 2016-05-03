package com.example.ApiTest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.ApiTest.util.Util;

/**
 * Created by cheungquentin on 16/5/3.
 */
public class NewProcessActivity extends Activity {

    private static final String TAG = "NewProcessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "pid:" + Util.getCurProcessName(this));
    }
}
