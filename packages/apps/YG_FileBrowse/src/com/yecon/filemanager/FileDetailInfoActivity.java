package com.yecon.filemanager;

import java.io.File;
import java.net.URLConnection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * Created by chenchu on 15-3-12.
 */
public class FileDetailInfoActivity extends FragmentActivity {
    private static final String Tag = "file info activity";

    static String path;

    static String getSelection() {
        String result = null;
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
              result = path;
            }
        }
        if (result != null) {
            result = MediaColumns.DATA+" = ?";
        }
        return result;
    }

    private FragmentTabHost mTabHost;

    private TabWidget mTabWidget;

    public static TextView setText(View root, int resId,String text1, String text2) {
        View v = root.findViewById(resId);
        TextView textone =(TextView) v.findViewById(android.R.id.text1);
        TextView texttwo =(TextView) v.findViewById(android.R.id.text2);

        if (textone != null){
            if (text1 != null) {
                textone.setText(text1);
            }
        }

        if (texttwo != null){
            if (text2 != null){
                texttwo.setText(text2);
            }
        }

        return texttwo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        path = uri.getPath();
        String name = uri.getLastPathSegment();
        Log.d(Tag,name);
        //setTitle(name);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(Tag, "on create");
        setContentView(R.layout.activity_info);
        setLayoutPramas();
        initTabHost();

        /*
        //test
        MediaMetadataRetriever getter = new MediaMetadataRetriever();
        getter.setDataSource(path);
        String bitrate = getter.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.d(Tag, (bitrate==null)?"bitrate null":"bitrate not null");
        getter.release();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaExtractor extractor = new MediaExtractor();
                try {
                    extractor.setDataSource(path);
                    int counter = extractor.getTrackCount();

                    MediaFormat format = extractor.getTrackFormat(0);
                    if (format != null) {
                        String rate = format.getString(MediaFormat.KEY_MIME);
                        Log.d(Tag, (rate == null) ? "bitrate null" : rate);

                    }
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    extractor.release();
                }
            }
        }).start();


        String selection = FileDetailInfoActivity.getSelection();
        Log.d(Tag,selection);
        Uri sd = MediaStore.Images.Media.getContentUri(FileStorageStateListener.pathSD);
        Uri psd = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d(Tag,sd.toString()+"...."+psd.toString());
        String [] projection = {MediaColumns.DATA,MediaColumns.WIDTH,MediaColumns.HEIGHT, MediaStore.Images.ImageColumns.DESCRIPTION};
        Cursor cursor = getContentResolver().query(psd,projection,selection,new String[]{path},null);
        if (cursor != null) {
            if (cursor.getCount()>0) {
                Log.d(Tag, "ok");
                cursor.moveToFirst();
                Log.d(Tag,"column count is "+cursor.getColumnCount()+cursor.getCount());
                int index = cursor.getColumnIndex(MediaColumns.HEIGHT);
                Log.d(Tag,cursor.getString(index));
                index = cursor.getColumnIndex(MediaColumns.WIDTH);
                index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DESCRIPTION);
                String result = cursor.getString(index);
                Log.d(Tag,(result == null)?"result":"  get result");
            }
            Log.d(Tag,"cursor");
            cursor.close();
        }
        */

    }
    //0x metadata,snapshot,baseinfo
    public static final int audio = 0x100;
    public static final int video =0x111;
    public static final int image =0x011;
    public static final int no_media=0x000;
    public static final int media_mask=0x001;

    public static int media_type = no_media;

    private int isMediaType(String mime) {
        String result = URLConnection.guessContentTypeFromName(mime);
        if (result == null) {
            return no_media;
        }
        if (result.startsWith("audio")) {
            return audio;
        }
        if (result.startsWith("video")) {
            return video;
        }
        if (result.startsWith("image")) {
            return image;
        }
        return no_media;
    }

    private void initTabHost() {
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabWidget = (TabWidget) findViewById((android.R.id.tabs));
        mTabHost.setup(FileDetailInfoActivity.this,getSupportFragmentManager(),android.R.id.tabcontent);
        if (mTabHost != null) {
            TextView tab1 = (TextView) getLayoutInflater().inflate(R.layout.tab_l,mTabWidget,false);
            tab1.setText(getString(R.string.indicator_file_normal));
            mTabHost.addTab(mTabHost.newTabSpec("normal").setIndicator(tab1), FileInfoNormalFragment.class, null);
            String url = Uri.fromFile(new File(path)).toString();
            media_type = isMediaType(url);
            if (media_type != no_media) {
                TextView tab2 = (TextView) getLayoutInflater().inflate(R.layout.tab_r,mTabWidget,false);
                tab2.setText(getString(R.string.indicator_file_more));
                mTabHost.addTab(mTabHost.newTabSpec("more").setIndicator(tab2),FileInfoMoreFragment.class,null);
            }
            else
            {
            	tab1.setBackgroundResource(R.drawable.tabhost_tab);
            }

        }
    }

    private void setLayoutPramas() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //getWindow().setLayout(metrics.widthPixels>>1,metrics.heightPixels>>1);
        getWindow().setLayout(577,436);
    }
}
