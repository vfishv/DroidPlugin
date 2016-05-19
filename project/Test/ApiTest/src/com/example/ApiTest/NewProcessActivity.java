package com.example.ApiTest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ApiTest.util.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by cheungquentin on 16/5/3.
 */
public class NewProcessActivity extends Activity {

    private static final String TAG = "NewProcessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "pid:" + Util.getCurProcessName(this));

        final int count = 1000;
        LinearLayout vg = new LinearLayout(this);
        vg.setOrientation(LinearLayout.VERTICAL);



        long tmp = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            TextView view = new TextView(this);
            view.setText("" + i);
            vg.addView(view);
        }

        Log.e(TAG, "time1: " + (System.currentTimeMillis() - tmp));
        vg.removeAllViews();

        Class cls = TextView.class;
        Class[] paramTypes = {Context.class};
        Object[] params = {this}; // 方法传入的参数
        tmp = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            Constructor con = null;     //主要就是这句了
            try {
                con = cls.getConstructor(paramTypes);
                TextView base = (TextView) con.newInstance(params);
                base.setText("" + i);
                vg.addView(base);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "time2: " + (System.currentTimeMillis() - tmp));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle("Test")
                .setMessage("Test Message")
                .create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyDialogAnimation;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        },1000);



    }
}
