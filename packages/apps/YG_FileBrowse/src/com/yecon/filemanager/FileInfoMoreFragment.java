package com.yecon.filemanager;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by chenchu on 15-3-23.
 */
public class FileInfoMoreFragment extends Fragment implements MediaScannerConnection.OnScanCompletedListener{

    private static final String Tag ="File infomoreFragment";

    private Handler mHandler = new Handler();

    //private final Cursor mBaseInfoCurosr;

    private static final String WIDTH ="width";
    private static final String HEIGHT ="height";
    private static final String DESCP = "descprition";
    private static final String DATETAKEN = "datetaken";
    private static final String LONGTITUDE = "longtitude";
    private static final String LATITUDE = "latitude";
    private static final String DURATION = "duration";
    private static final String ARTIST ="artist";
    private static final String ALBUM = "album";

    @Override
    public void onScanCompleted(String path, Uri uri) {
        if (path.equals(FileDetailInfoActivity.path)){
            if (uri != null) {
                   update();
            } else {
                Log.d(Tag, "scan fail");
            }
        } else {
            Log.d(Tag,"error or exception");
        }
    }

    private void update() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                doUpdate();
            }
        });
    }

    static Cursor mInfo;

    private Uri getUri(int mediaType) {
        switch (mediaType) {
            case FileDetailInfoActivity.audio:
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            case FileDetailInfoActivity.image:
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            case FileDetailInfoActivity.video:
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            default:
                Log.d(Tag,"whoops");
                return null;
        }
    }

    private void doUpdate() {
        Log.d(Tag,"do update");
        mInfo = getCursor(getUri(FileDetailInfoActivity.media_type));
        if (mInfo!=null && mInfo.getCount()==1 && mInfo.moveToFirst()) {
            Log.d(Tag, "got it");

            if ((FileDetailInfoActivity.media_type >> 0 & FileDetailInfoActivity.media_mask) == 0x001) {
                Fragment baseinfo = getChildFragmentManager().findFragmentByTag("baseinfo");
                if (baseinfo == null) {
                    getChildFragmentManager().beginTransaction().add(R.id.fragment_info_file_more_container, new BaseInfoFragment(), "baseinfo").commit();
                }
            }

            if ((FileDetailInfoActivity.media_type >> 4 & FileDetailInfoActivity.media_mask) == 0x001) {
                Fragment snapshot = getChildFragmentManager().findFragmentByTag("snapshot");
                if (snapshot == null) {
                    getChildFragmentManager().beginTransaction().add(R.id.fragment_info_file_more_container, new SnapShotFragment(), "snapshot").commit();
                }
            }

            if ((FileDetailInfoActivity.media_type >> 8 & FileDetailInfoActivity.media_mask) == 0x001) {
                Fragment metadata = getChildFragmentManager().findFragmentByTag("metadata");
                if (metadata == null) {
                    getChildFragmentManager().beginTransaction().add(R.id.fragment_info_file_more_container, new MetaDataFragment(), "metadata").commit();
                }
            }
           }
        }

    private Cursor getCursor(Uri uri) {
        return getActivity().getContentResolver().query(uri,null,FileDetailInfoActivity.getSelection()
                ,new String[]{FileDetailInfoActivity.path},null);
    }

    private void commitFragment(Class<?> fragment,String tag) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(Tag,"on attach");
        doScan(FileDetailInfoActivity.path);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(Tag,"on detach");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Tag,"on Destroy");
        if (mInfo != null){
            mInfo.close();
            mInfo = null;
        }
    }

    private void doScan(String path){
        //mSpinner.setVisibility(View.VISIBLE);
        Cursor cursor = getCursor(MediaStore.Files.getContentUri("external"));
        if ( cursor != null && cursor.getCount() == 0) {
            Log.d(Tag,"on scan");
            MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null, FileInfoMoreFragment.this);
        } else {
            if (cursor != null) {
                cursor.close();
            }
            update();
        }
    }

    ImageView mSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View root = inflater.inflate(R.layout.fragment_info_file_more,container,false);
       mSpinner =(ImageView) root.findViewById(R.id.fragment_info_file_more_spinner);
       //((AnimationDrawable) mSpinner.getBackground()).start();
       Log.d(Tag,"on create view");
       return root;
    }

    public static void setTextHelper(TextView tv, String str) {
        if (tv != null) {
            tv.setText(str);
        }
    }

    public static class BaseInfoFragment extends Fragment {

        private TextView mResolution;

        public void setResolution(String res){
            setTextHelper(mResolution,res);
        }

        private String getRes(Cursor cursor) {
            String result = null;
            if (cursor != null) {
                int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.RESOLUTION);
                if (index != -1) {
                    result = cursor.getString(index);
                    if (result!= null && result.equalsIgnoreCase("nullxnull")) {
                        return null;
                    }
                    return result;
                }
                index = cursor.getColumnIndex(WIDTH);
                if (index != -1) {
                    result = cursor.getString(index);
                    if (result != null && result.equalsIgnoreCase("null")) {
                        return null;
                    }
                }
                index = cursor.getColumnIndex(HEIGHT);
                String height = null;
                if (index != -1) {
                     height = cursor.getString(index);
                    if (result != null && result.equalsIgnoreCase("null")) {
                        return null;
                    }
                }
                result +="x"+height;
            }
            return result;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_more_base_info,container,false);
            mResolution = FileDetailInfoActivity.setText(root, R.id.fragment_more_base_info_res, getString(R.string.resolution), getRes(mInfo));
            Log.d(Tag,"baseinfo oncreateview");
            return root;
        }
    }

    public static class SnapShotFragment extends Fragment {
        private TextView mDateTaken;
        private TextView mLongtitude;
        private TextView mLatitude;
        private TextView mDescrpition;

        public void setDateTaken(String str) {
            setTextHelper(mDateTaken, str);
        }

        public void setLongtitude(String str) {
            setTextHelper(mLongtitude, str);
        }

        public void setLatitude(String str) {
            setTextHelper(mLatitude, str);
        }

        public void setDescrpition(String str) {
            setTextHelper(mDescrpition, str);
        }

        private String getDateTaken(Cursor cursor) {
            Long result = null;
            if (cursor != null) {
                int index = cursor.getColumnIndex(DATETAKEN);
                if (index != -1) {
                    result = cursor.getLong(index);
                }
            }
            if (result == null) {
                return null;
            }
            return DateFormat.getDateTimeInstance().format(new Date(result));
        }

        private String getLongtitude(Cursor cursor) {
            Double result = null;
            if (cursor != null) {
                int index = cursor.getColumnIndex(LONGTITUDE);
                if (index != -1) {
                    result = cursor.getDouble(index);
                }
            }
            if (result == null) {
                return null;
            }
            return Location.convert(result,Location.FORMAT_DEGREES);
        }

        private String getLatitude(Cursor cursor) {
            Double result = null;
            if (cursor != null) {
                int index = cursor.getColumnIndex(LATITUDE);
                if (index != -1) {
                    result = cursor.getDouble(index);
                }
            }
            if (result == null) {
                return null;
            }
            return Location.convert(result,Location.FORMAT_DEGREES);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_more_snapshot_info, container, false);
            mDateTaken = FileDetailInfoActivity.setText(root, R.id.fragment_more_snapshot_datetaken, getString(R.string.dateTaken), getDateTaken(mInfo));
            mLongtitude = FileDetailInfoActivity.setText(root, R.id.fragment_more_snapshot_longtitude, getString(R.string.longtitude), getLongtitude(mInfo));
            mLatitude = FileDetailInfoActivity.setText(root, R.id.fragment_more_snapshot_latitude, getString(R.string.latitude), getLatitude(mInfo));
            Log.d(Tag,"snapshot oncreateview");
            return root;
        }
    }


    public static class MetaDataFragment extends Fragment {
        private TextView mDuration;
        private TextView mArtist;
        private TextView mAlbum;

        public void setDuration(String str) {
           setTextHelper(mDuration, str);
        }

        public void setArtist(String str) {
            setTextHelper(mArtist, str);
        }

        public void setAlbum(String str) {
            setTextHelper(mAlbum, str);
        }


        private String getDuration(Cursor cursor) {
            Long result = null;
            if (cursor != null) {
                int index = cursor.getColumnIndex(DURATION);
                if (index != -1) {
                   result = cursor.getLong(index);
                }
            }
            if (result == null){
                return null;
            }
            return getDurationHelper(result);
        }

        private String getDurationHelper(long milliseconds) {
            long seconds = milliseconds/1000;
            long minitues = seconds/60;
            long second = seconds%60;
            long hour = minitues/60;
            long minitue = minitues%60;
            return myFormat(hour)+":"+myFormat(minitue)+":"+myFormat(second);
        }

        private String myFormat(long i) {
            return (i>9)?(""+i):("0"+i);
        }

        private String getArtist(Cursor cursor) {
            String result = null;
            if (cursor != null) {
                int index = cursor.getColumnIndex(ARTIST);
                if (index != -1) {
                   result = cursor.getString(index);
                }
            }
            return result;
        }

        private String getAlbum(Cursor cursor) {
            String result = null;
            if (cursor != null) {
                int index = cursor.getColumnIndex(ALBUM);
                if (index != -1) {
                    result = cursor.getString(index);
                }
            }
            return result;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_more_metadata_info, container, false);
            mDuration = FileDetailInfoActivity.setText(root, R.id.fragment_more_metadata_duration, getString(R.string.duration), getDuration(mInfo));
            mArtist = FileDetailInfoActivity.setText(root, R.id.fragment_more_metadata_artist, getString(R.string.artist), getArtist(mInfo));
            mAlbum = FileDetailInfoActivity.setText(root, R.id.fragment_more_metadata_album, getString(R.string.album), getAlbum(mInfo));
            Log.d(Tag,"metadata oncreateview");
            return root;
        }
    }

}
