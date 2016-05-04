package com.example.TestPlugin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by cheungquentin on 16/5/4.
 */
public class FolderFragment extends ListFragment {

    private static final String TAG = "FolderFragment";

    public static final String FOLDER = "\ud83d\udcc1";//
    public static final String FILE = "\ud83d\udcc4";//
    
    private ArrayAdapter<FileItem> adapter;
    final Handler handler = new Handler();

    public FolderFragment() {
    }

    class FileItem{
        String name;
        String path;
        boolean isDir;
        long lastModified;
        long size;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayAdapter<FileItem>(getActivity(), 0) {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_file_item, null);
                }
                FileItem item = getItem(position);

                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText((item.isDir?FOLDER:FILE) + item.name);

                TextView path = (TextView) convertView.findViewById(R.id.path);
                path.setText(item.path);

                TextView time = (TextView) convertView.findViewById(R.id.time);
                time.setText("" + item.lastModified);

                TextView size = (TextView) convertView.findViewById(R.id.size);
                size.setText("" + item.size);

                return convertView;
            }
        };

    }

    boolean isViewCreated = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        setEmptyText("没有在sdcard找到apk");
        setListAdapter(adapter);
        setListShown(false);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileItem fileItem = adapter.getItem(position);
                if (fileItem != null) {
                    File file = new File(fileItem.path);
                    if (file != null && file.exists() && file.canRead()) {
                        startLoad(file.getAbsolutePath());
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "file no exits or no readable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        startLoad(null);
    }

    @Override
    public void onDestroyView() {
        isViewCreated = false;
        super.onDestroyView();
    }

    @Override
    public void setListShown(boolean shown) {
        if (isViewCreated) {
            super.setListShown(shown);
        }
    }

    private void startLoad(String dir) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startLoadInner(dir);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0x1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x1) {
            if (permissions != null && permissions.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    String permisson = permissions[i];
                    int grantResult = grantResults[i];
                    if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permisson)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            startLoadInner(null);
                        } else {
                            Toast.makeText(getActivity(), "没有授权，无法使用", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                for (String permisson : permissions) {

                }
            }
        }
    }

    private void startLoadInner(final String dir) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setListShown(true);
            }
        });
        if (!isViewCreated) {
            return;
        }
        new Thread("FileScanner") {
            @Override
            public void run() {
                File file = null;
                if (TextUtils.isEmpty(dir)) {
                    file = getContext().getCacheDir();
                    file = file.getParentFile();
                } else {
                    file = new File(dir);
                }

                File parent = file.getParentFile();
                final FileItem parentItem = new FileItem();
                parentItem.name = "..";
                parentItem.path = parent.getAbsolutePath();
                parentItem.isDir = parent.isDirectory();
                parentItem.lastModified = parent.lastModified();
                parentItem.size = parent.length();

                final List<FileItem> list = new ArrayList<FileItem>(24);
                File[] files = file.listFiles();
                if (files != null && files.length>0) {
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clear();
                                adapter.add(parentItem);
                                list.add(parentItem);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (File f : files) {
                        final FileItem fileItem = new FileItem();
                        fileItem.name = f.getName();
                        fileItem.path = f.getAbsolutePath();
                        fileItem.isDir = f.isDirectory();
                        fileItem.lastModified = f.lastModified();
                        fileItem.size = f.length();
                        list.add(fileItem);

                        try {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.add(fileItem);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /*
                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addAll(list);
                        }
                    });
                } catch (Exception e) {
                }
                */
            }
        }.start();
    }


}
