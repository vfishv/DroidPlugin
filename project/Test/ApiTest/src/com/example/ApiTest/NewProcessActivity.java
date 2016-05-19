package com.example.ApiTest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

    private Dialog selectDialog;

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
                //dialog.show();
                /* 初始化普通对话框。并设置样式 */
                selectDialog = new Dialog(NewProcessActivity.this, R.style.dialog);
                selectDialog.setCancelable(true);
				/* 设置普通对话框的布局 */
                selectDialog.setContentView(R.layout.slt_cnt_type);

                final View rootView = selectDialog.findViewById(R.id.bgDialog);

				/* +2+取得布局中的文本控件，并赋值需要显示的内容+2+ */
                TextView textView01 = (TextView) selectDialog
                        .findViewById(R.id.TextView01);
                textView01
                        .setText("光阴似箭，日月如梭。譬如流水，不舍昼夜。时光匆匆，岁月悠悠。红尘过客，流年几许。前世今生，春去秋来。愿君惜时，比肩圣人。");

                Button btnItem1 = (Button) selectDialog.findViewById(R.id.ly1btn1);
                btnItem1.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animation = AnimationUtils.loadAnimation(NewProcessActivity.this,R.anim.zoom_exit);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                selectDialog.dismiss();//隐藏对话框
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        rootView.startAnimation(animation);
                        //selectDialog.dismiss();//隐藏对话框
                    }
                });
                selectDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        rootView.startAnimation(AnimationUtils.loadAnimation(NewProcessActivity.this,R.anim.zoom_enter));
                    }
                });
                selectDialog.show();//显示对话框
            }
        },1000);



    }
}
