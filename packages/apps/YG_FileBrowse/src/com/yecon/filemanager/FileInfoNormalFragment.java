package com.yecon.filemanager;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import net.micode.fileexplorer.Util;


/**
 * Created by chenchu on 15-3-13.
 */
public class FileInfoNormalFragment extends Fragment
{
    private static final String Tag = "file infonormalfragment";
    int[] textResId = {R.id.fragment_info_normal_type
                      ,R.id.fragment_info_normal_location,R.id.fragment_info_normal_size
                      ,R.id.fragment_info_normal_time};


    View mRoot;
    FileTypeLoader mTypeLoader;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mTypeLoader == null) {
            mTypeLoader = new FileTypeLoader(activity);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_file_normal,container,false);
        mRoot = v;
        init();
        return v;
    }

    private void init() {
        if (mRoot != null) {
            setTitles(mRoot);
            File file = new File(FileDetailInfoActivity.path);
            setName(mRoot,FileDetailInfoActivity.path);
            setImage(mRoot,file);
            setType(mRoot,file);
            setLocation(mRoot,file);
            asyncGetSize();
            setTime(mRoot,file);
            setPermission(mRoot,file);
        }
    }

    private void setName(View root, String path) {
        TextView name = (TextView) root.findViewById(R.id.fragment_info_normal_name);
        name.setText(Util.getNameFromFilepath(path));
        //name.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private Bitmap getImage(File file) {
        if (!file.exists()) {
            return null;
        }
        Bitmap icon;
        String path = file.getPath();
        icon = BitmapFactory.decodeFile(path);
        String name = file.getName();
        if (icon == null) {
            FileIconLoader iconLoader = new FileIconLoader();
            int resId = file.isDirectory()?R.drawable.folder:iconLoader.getIcon(Util.getExtFromFilename(name));
            icon = BitmapFactory.decodeResource(getResources(),resId);
        }
        return icon;
    }


    private void setSize(View v, long size) {
        FileDetailInfoActivity.setText(v, R.id.fragment_info_normal_size, null, Util.convertStorage(size));
    }

    private AsyncTask task;

    @SuppressWarnings("unchecked")
    private void asyncGetSize() {
        task = new AsyncTask<String,Long,Long>() {
            private long size = 0;
            @Override
            protected Long doInBackground(String... params) {
                String path = params[0];
                return getSize(path);
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
                if (mRoot != null) {
                    setSize(mRoot, aLong);
                }
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                Long value = values[0];
                if (mRoot != null) {
                    setSize(mRoot, value);
                }
            }

            private Long getSize(String path) {
                if (isCancelled())
                    return Long.valueOf(0);
                File file = new File(path);
                if (file.isDirectory()) {
                    File[] listFiles = file.listFiles();
                    if (listFiles == null)
                        return size;

                    for (File f : listFiles) {
                        if (isCancelled())
                            return Long.valueOf(0);

                        getSize(f.getPath());
                    }
                } else {
                    size += file.length();
                    publishProgress(size);
                }
                return size;
            }

        }.execute(FileDetailInfoActivity.path);
    }

    private void setTitles(View v) {
        String[] titles = getResources().getStringArray(R.array.fragment_info_normal_title);
        int size = titles.length;
        for(int i=0;i<size;++i){
            FileDetailInfoActivity.setText(v,textResId[i],titles[i],"");
        }
    }

    private void setLocation(View v,File file) {
        TextView location = FileDetailInfoActivity.setText(v,R.id.fragment_info_normal_location,null,file.getParent());
        location.setFocusable(true);
        location.setFocusableInTouchMode(true);
        location.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        location.setMarqueeRepeatLimit(-1);//for ever
        location.requestFocusFromTouch();
        location.requestFocus();
    }

    private void setType(View v,File file) {
        String path = file.getPath();
        FileDetailInfoActivity.setText(v,R.id.fragment_info_normal_type,null,mTypeLoader.getType(path));
    }

    private void setImage(View v,File file) {
        ImageView view = (ImageView) v.findViewById(R.id.fragment_info_normal_icon);
        view.setImageBitmap(getImage(file));
    }


    private void setTime(View v,File file) {
        FileDetailInfoActivity.setText(v,R.id.fragment_info_normal_time,null,DateFormat.getDateTimeInstance().format(new Date(file.lastModified())));
    }

    //TODO:seems not to work out here
    private void setPermission(View v,File file) {
        assert(file.exists());
        CheckBox boxRead = (CheckBox) v.findViewById(R.id.fragment_info_normal_permission_read);
        if (boxRead != null) {
            boxRead.setChecked(file.canRead());
        }
        CheckBox boxWrite = (CheckBox) v.findViewById(R.id.fragment_info_normal_permission_write);
        if (boxWrite != null) {
            boxWrite.setChecked(file.canWrite());
        }


        CheckBox boxExe = (CheckBox) v.findViewById(R.id.fragment_info_normal_permission_exe);
        if (boxExe != null) {
            Log.d(Tag, "set ");
            boxExe.setChecked(file.canExecute());
        }


    }

}
