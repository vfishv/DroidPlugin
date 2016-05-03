package com.example.ApiTest.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by cheungquentin on 16/5/3.
 */
public class Util {

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return "pid" + pid;
    }

}
