package com.example.TestPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;

import java.util.ArrayList;
import java.util.List;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

/**
 * Created by cheungquentin on 16/5/9.
 */
public class InstalledAppsFragment extends ListFragment {

    private static final String TAG = "InstalledAppsFragment";

    private ArrayAdapter<ApkItem> adapter;

    final Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayAdapter<ApkItem>(getActivity(), 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.apk_item, null);
                }
                ApkItem item = getItem(position);

                ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
                icon.setImageDrawable(item.icon);

                TextView title = (TextView) convertView.findViewById(R.id.textView1);
                title.setText(item.title);

                final TextView version = (TextView) convertView.findViewById(R.id.textView2);
                version.setText(String.format("%s(%s)", item.versionName, item.versionCode));

                TextView btn = (TextView) convertView.findViewById(R.id.button2);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onListItemClick(getListView(), view, position, getItemId(position));
                    }
                });

                btn = (TextView) convertView.findViewById(R.id.button3);
                btn.setText("卸载");
                btn.setVisibility(View.GONE);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onListItemClick(getListView(), view, position, getItemId(position));
                    }
                });

                return convertView;
            }
        };
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final ApkItem item = adapter.getItem(position);
        if (v.getId() == R.id.button2) {
            if (item.installing) {
                return;
            }
            if (!PluginManager.getInstance().isConnected()) {
                Toast.makeText(getActivity(), "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
            }
            try {
                if (PluginManager.getInstance().getPackageInfo(item.packageInfo.packageName, 0) != null) {
                    Toast.makeText(getActivity(), "已经安装了，不能再安装", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            doInstall(item);
                        }
                    }.start();

                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    PluginManager.getInstance().installPackage(item.apkfile, 0);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                adapter.remove(item);
            }
        }
    }

    private synchronized void doInstall(ApkItem item) {
        item.installing = true;

        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        try {
            final int re = PluginManager.getInstance().installPackage(item.apkfile, 0);
            item.installing = false;

            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (re) {
                        case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                            Toast.makeText(getActivity(), "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_FAILED_NOT_SUPPORT_ABI:
                            Toast.makeText(getActivity(), "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_SUCCEEDED:
                            Toast.makeText(getActivity(), "安装完成", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            break;
                    }

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setEmptyText("没有安装插件");
        setListAdapter(adapter);
        setListShown(false);
        getListView().setOnItemClickListener(null);

        getApps();
    }

    /**
     * 获取安装应用apk位置
     * @param ctt
     * @param packgename
     * @return
     */
    public static String getInstallApkPath(Context ctt, String packgename)
    {
        PackageManager pm = ctt.getPackageManager();
        try
        {
            PackageInfo info = pm.getPackageInfo(packgename, 0);
            if (info != null)
            {
                ApplicationInfo appInfo = info.applicationInfo;
                if(appInfo!=null)
                {
                    String sourceDir = appInfo.sourceDir;
                    return sourceDir;
                }
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void getApps()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Activity activity = getActivity();
                if (null == activity) {
                    return;
                }
                final PackageManager pm = activity.getPackageManager();
                final Intent mainIntent = new Intent(Intent.ACTION_MAIN);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);

                if (apps != null) {
                    //Collections.reverse(apps);

                    final ArrayList<ApkItem> list = new ArrayList<ApkItem>();
                    for (ResolveInfo ri : apps) {
                        String packageName = ri.activityInfo.packageName;
                        if (TextUtils.isEmpty(packageName)) {
                            continue;
                        }

                        String apkPath = getInstallApkPath(activity,packageName);

                        Drawable thumbnail = null;
                        //app.clsName = ri.activityInfo.name;//名字
                        //app.packageName = packageName;//包名
                        Drawable appIcon = thumbnail != null ? thumbnail : ri.activityInfo.loadIcon(pm);
                        String appName = ri.resolvePackageName;
                        int versionCode = 1;
                        String name = null;
                        String fingerprint = null;

                        try {
                            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                            appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                            //String versionName = packageInfo.versionName;
                            //String packagename = packageInfo.packageName;
                            //app.firstInstallTime = packageInfo.firstInstallTime;
                            //app.lastUpdateTime = packageInfo.lastUpdateTime;
                            versionCode = packageInfo.versionCode;
                            name = packageInfo.versionName;
                            //appIcon = packageInfo.applicationInfo.loadIcon(pm);
                            //packages.add(packagename);

                            final ApkItem item = new ApkItem(activity, packageInfo, apkPath);
                            list.add(item);
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.add(item);
//                                }
//                            });


                            Signature[] signs = packageInfo.signatures;
                            if(signs!=null && signs.length>0)
                            {
                                Signature sign = signs[0];
                                //parseSignature(sign.toByteArray());
                                //fingerprint = MD5.getMessageDigest(sign.toByteArray());
                                //Log.e(TAG, "packageName:"+packageName);
                                //Log.e(TAG, fingerprint);
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        finally{
                            //app.versionCode = versionCode;//version code
                            //app.name = appName;
                            //app.fingerprint = fingerprint;
                        }

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addAll(list);
                        }
                    });

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setListShown(true);
                        }
                    });
                }

            }
        }).start();
    }
}
