
package com.yecon.videoplayer2;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.VideoColumns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yecon.videoplayer2.VideoPlaybackListActivity.VideoInfo;
import com.yecon.videoplayer2.VideoPlaybackMainActivity.PlayStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.yecon.videoplayer2.DebugUtil.*;
import static com.yecon.videoplayer2.VideoPlaybackConstant.*;

public class VideoPlaybackAllListFragment extends Fragment implements OnItemClickListener {
    private VideoPlaybackListActivity mActivity;

    private ArrayList<VideoInfo> mVideoList = new ArrayList<VideoInfo>();

    private Cursor mCursor;

    private ListView mListView;
    private VideoAdapter mAdapter;

    private View mLayoutError;
    private View mLayoutScan;

    private int mPosition;
    private boolean mIsMediaEject = false;
    private int mCursorOffset = -1;

    private int mMediaScanStatus = MEDIA_FILE_EXIST;

    private QueryDataThread mQueryDataThread = null;
    private final static ThreadPoolExecutor gQueryDataExecutor = new ThreadPoolExecutor(1, 1, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    private String[] mMediaColumns = new String[] {
            BaseColumns._ID,
            MediaColumns.DATA,
            MediaColumns.DISPLAY_NAME,
            MediaColumns.MIME_TYPE,
            VideoColumns.DURATION,
            VideoColumns.DATE_TAKEN,
            MediaColumns.SIZE,
    };

    private BroadcastReceiver mMediaScannerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Uri uri = intent.getData();
            String action = intent.getAction();
            String path = uri.getPath();
            printLog("VideoPlaybackAllListFragment - mMediaScannerReceiver - action: " + action
                    + " - path: " + path);

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                mIsMediaEject = false;

            } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                mIsMediaEject = true;

                mHandler.removeMessages(SHOW_PROGRESS_DIALOG);
                mHandler.sendMessage(mHandler.obtainMessage(DELETE_DATA, (String) path));

                mHandler.removeMessages(DISMISS_PROGRESS_DIALOG);
                mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
                mCursorOffset = -1;

                if (mIsMediaEject) {
                    mIsMediaEject = false;
                } else {
                    if (mVideoList.size() == 0) {
                        mHandler.removeMessages(SHOW_PROGRESS_DIALOG);
                        mHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
                    }
                }
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                mCursorOffset = -1;

                mHandler.removeMessages(CREATE_QUERY_DATA_THREAD);
                mHandler.sendEmptyMessage(CREATE_QUERY_DATA_THREAD);

                mHandler.removeMessages(DISMISS_PROGRESS_DIALOG);
                mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
            }
        }
    };
    private BroadcastReceiver mPlayStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Uri uri = intent.getData();
            String action = intent.getAction();
            // String path = uri.getPath();
            if (action.equals(VideoPlayerContext.ACTION_PLAY_FILE_CHANGED)) {
                updataListView(true);
            }
        }
    };

    private int updateListPlaying() {
        int ret = -1;
        String curPath = VideoPlayerApp.GetInstance().getVideoPlayerContext().getCurrentTrackPath();
        
        if(curPath==null){
        	//It's not playing, get the track played last time.
        	PlayListData.ListItem listItem = VideoPlayerApp.GetInstance().getVideoPlayerContext().getPlaying();
        	if(listItem!=null){
        		curPath = listItem.getPath();
        	}
        }
        if(curPath!=null){
        	 for (int i = 0; i < mVideoList.size(); i++) {
                 if (curPath.equals(mVideoList.get(i).filePath)) {
                     mVideoList.get(i).isPlaying = true;
                     ret = i;
                 }
                 else {
                     mVideoList.get(i).isPlaying = false;
                 }
             }
        }       
        return ret;
    }

    public void updataListView(final boolean listChanged) {
        if (listChanged) {
        	 mPosition = updateListPlaying();     
        	 mAdapter.notifyDataSetChanged();
        }       
        if (mPosition < mListView.getFirstVisiblePosition()
                || mPosition > mListView.getLastVisiblePosition()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mPosition < mListView.getFirstVisiblePosition()) {
                        mListView.setSelection(mPosition);
                    }
                    else if (mPosition > mListView.getLastVisiblePosition()) {
                        mListView.setSelection(1
                                + mPosition
                                - (mListView.getLastVisiblePosition() - mListView
                                        .getFirstVisiblePosition()));
                    }
                    printLog("VideoPlaybackAllListFragment - onResume() - mPosition: " + mPosition);
                }
            }, 50);
        }
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VIEW_DATA:
                    if (1 == msg.arg1) {
                        ArrayList<VideoInfo> newlist = (ArrayList<VideoInfo>) msg.obj;

                        for (int i = 0; i < newlist.size(); i++) {
                            mVideoList.add(newlist.get(i));
                        }
                    } else {
                        mVideoList.clear();
                        mVideoList = (ArrayList<VideoInfo>) msg.obj;
                    }

                    if (mVideoList.size() != 0) {
                        mHandler.removeMessages(DISMISS_PROGRESS_DIALOG);
                        mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
                    }

                    // fillPlayList();
                    updataListView(true);
                    break;

                case SHOW_PROGRESS_DIALOG:
                    mLayoutScan.setVisibility(View.VISIBLE);
                    mLayoutError.setVisibility(View.GONE);
                    break;

                case DISMISS_PROGRESS_DIALOG:
                    mLayoutScan.setVisibility(View.GONE);
                    break;

                case DELETE_DATA:
                    String path = (String) msg.obj;
                    printLog("delte all file about path:" + path);

                    for (int i = 0; i < mVideoList.size();) {
                        // printLog("removing filepath(" + i + ")" +
                        // mVideoList.get(i).filePath);
                        if ((mVideoList.get(i).filePath).startsWith(path)) {
                            mVideoList.remove(i);
                            continue;
                        }
                        i++;
                    }

                    // fillPlayList();

                    mAdapter.notifyDataSetChanged();
                    break;

                case CREATE_QUERY_DATA_THREAD:
                    mListView.setVisibility(View.VISIBLE);
                    mLayoutError.setVisibility(View.GONE);

                    mHandler.removeMessages(CREATE_THUMB_THREAD);
                    mHandler.removeMessages(UPDATE_VIEW_DATA);

                    if (null != mQueryDataThread) {
                        gQueryDataExecutor.remove(mQueryDataThread);
                    }
                    mQueryDataThread = new QueryDataThread();
                    gQueryDataExecutor.execute(mQueryDataThread);
                    break;

                case SHOW_ERROR:
                    boolean errorVisible = msg.arg1 == 1 ? true : false;
                    if (errorVisible) {
                        mListView.setVisibility(View.GONE);
                        mLayoutError.setVisibility(View.VISIBLE);
                    } else {
                        mListView.setVisibility(View.VISIBLE);
                        mLayoutError.setVisibility(View.GONE);
                    }
                    break;

                case QUERY_DATA_END:
                    if (mVideoList.size() == 0) {
                        Message message = new Message();
                        message.what = SHOW_ERROR;
                        message.arg1 = 1;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = SHOW_ERROR;
                        message.arg1 = 0;
                        mHandler.sendMessage(message);
                    }
                    break;

            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
        
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (VideoPlaybackListActivity) activity;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initData();

        View view = inflater.inflate(R.layout.videoplayback_all_list_fragment, container, false);

        mLayoutError = view.findViewById(R.id.layout_error);
        mLayoutScan = view.findViewById(R.id.layout_scan);

        mListView = (ListView) view.findViewById(R.id.lv_all);
        mListView.setAdapter(mAdapter);
        int Color = 00000000;
        mListView.setCacheColorHint(Color);
        mListView.setOnItemClickListener(this);

        mHandler.sendEmptyMessage(CREATE_QUERY_DATA_THREAD);
        return view;
    }

    private void initData() {
        mCursorOffset = -1;

        mAdapter = new VideoAdapter(mActivity);

        registerMediaScannerReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(VideoPlayerContext.ACTION_PLAY_FILE_CHANGED);
        mActivity.registerReceiver(mPlayStatusReceiver, filter);

        try {
            Cursor cursor = mActivity.getContentResolver().query(MediaStore.getMediaScannerUri(),
                    null, null, null, null);
            if (null != cursor) {
                printLog("VideoPlaybackAllListFragment - initData - Media Scanner is working");
                mHandler.removeMessages(SHOW_PROGRESS_DIALOG);
                mHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);

                cursor.close();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        printLog("VideoPlaybackAllListFragment - onResume - start");

        updataListView(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mMediaScannerReceiver);
        mActivity.unregisterReceiver(mPlayStatusReceiver);
    }

    private void registerMediaScannerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        mActivity.registerReceiver(mMediaScannerReceiver, filter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (VideoPlaybackMainActivity.isRDSActive()) {
            return;
        }

        // Intent intentStop = new
        // Intent("com.android.music.musicservicecommand");
        // intentStop.putExtra("command", "stopservice");
        // sendBroadcast(intentStop);

        mPosition = position;

        Intent intent = new Intent(mActivity, VideoPlaybackMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        /*
         * Bundle bundle = new Bundle(); // bundle.putStringArrayList("PLAYLIST", mPlaylist);
         * bundle.putString("URI_TYPE", "ThumbnailList"); bundle.putInt("POSITION", position);
         * intent.putExtras(bundle);
         */
        //
        intent.putExtra(VideoPlayerContext.OPEN_LIST, "list");
        if (VideoPlayerApp.GetInstance().getVideoPlayerContext().openList(mVideoList, position, true)) {
            intent.putExtra(VideoPlayerContext.OPEN_POS, position);
        }
        startActivity(intent);
    }

    private class QueryDataThread implements Runnable {
        @Override
        public void run() {
            ArrayList<VideoInfo> tmpVideoList = new ArrayList<VideoInfo>();

            try {
                boolean clear_oldlist = true;
                int step_count = 0;

                mCursor = mActivity.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        mMediaColumns, null, null, null);
                if (null == mCursor) {
                    printLog("Maybe there is no multimedia files in multimedia card ");
                    return;
                }
                printLog("video item count: " + mCursor.getCount() + " - mCursorOffset: "
                        + mCursorOffset);

                if (-1 != mCursorOffset) {
                    mCursor.moveToPosition(mCursorOffset);
                    clear_oldlist = false;
                }

                // printLog("clear_oldlist: " + clear_oldlist);

                long start = SystemClock.uptimeMillis();
                String curPath = mActivity.getCurPath();
                
                while (mCursor.moveToNext()) {
                    VideoInfo info = new VideoInfo();
                    info.filePath = mCursor.getString(mCursor
                            .getColumnIndexOrThrow(MediaColumns.DATA));
                    info.mimeType = mCursor.getString(mCursor
                            .getColumnIndexOrThrow(MediaColumns.MIME_TYPE));
                    info.title = mCursor.getString(mCursor
                            .getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME));
                    info.size = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaColumns.SIZE));
                    info.duration = mCursor.getString(mCursor
                            .getColumnIndexOrThrow(VideoColumns.DURATION));
                    info.modified = mCursor.getString(mCursor
                            .getColumnIndexOrThrow(VideoColumns.DATE_TAKEN));
                    info.id = mCursor.getLong(mCursor.getColumnIndexOrThrow(BaseColumns._ID));

                    if(curPath!=null && info.filePath.equals(curPath)){
                    	 info.isPlaying = true;
                    }
                    else{
                    	 info.isPlaying = false;
                    }
                    
                    tmpVideoList.add(info);

                    mCursorOffset = mCursor.getPosition();

                    step_count++;

                    if (step_count >= UPDATEVIEW_COUNT) {
                        if (clear_oldlist) {
                            mHandler.sendMessage(mHandler.obtainMessage(UPDATE_VIEW_DATA,
                                    0, 0, (ArrayList<VideoInfo>) tmpVideoList));
                            clear_oldlist = false;
                        } else {
                            mHandler.sendMessage(mHandler.obtainMessage(UPDATE_VIEW_DATA,
                                    1, 0, (ArrayList<VideoInfo>) tmpVideoList));
                        }
                        step_count = 0;
                        tmpVideoList = new ArrayList<VideoInfo>();
                        start = SystemClock.uptimeMillis();
                    } else {
                        long current = SystemClock.uptimeMillis();

                        if (current >= 1000 + start) {
                            if (clear_oldlist) {
                                mHandler.sendMessage(mHandler.obtainMessage(UPDATE_VIEW_DATA,
                                        0, 0, (ArrayList<VideoInfo>) tmpVideoList));
                                clear_oldlist = false;
                            } else {
                                mHandler.sendMessage(mHandler.obtainMessage(UPDATE_VIEW_DATA,
                                        1, 0, (ArrayList<VideoInfo>) tmpVideoList));
                            }
                            step_count = 0;
                            tmpVideoList = new ArrayList<VideoInfo>();
                            start = SystemClock.uptimeMillis();
                        }
                    }
                }
                mCursor.close();
                mCursor = null;

                if (clear_oldlist) {
                    mHandler.sendMessage(mHandler.obtainMessage(UPDATE_VIEW_DATA,
                            0, 0, (ArrayList<VideoInfo>) tmpVideoList));
                } else if (tmpVideoList.size() > 0) {
                    mHandler.sendMessage(mHandler.obtainMessage(UPDATE_VIEW_DATA,
                            1, 0, (ArrayList<VideoInfo>) tmpVideoList));
                }

                try {
                    Cursor cursor = mActivity.getContentResolver().query(
                            MediaStore.getMediaScannerUri(), null, null, null, null);
                    if (null != cursor) {
                        printLog("VideoPlaybackAllListFragment - run - Media Scanner is working");
                        mHandler.sendEmptyMessageDelayed(CREATE_QUERY_DATA_THREAD, 1000);

                        cursor.close();
                    } else {
                        printLog("VideoPlaybackAllListFragment - run - Media Scanner is finished");
                        mHandler.sendEmptyMessage(QUERY_DATA_END);

                        mHandler.removeMessages(DISMISS_PROGRESS_DIALOG);
                        mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
                    }
                } catch (Exception e) {
                }
            } catch (Exception e) {
                if (null != mCursor) {
                    mCursor.close();
                    mCursor = null;
                }
                printLog("Access media scanner database exception:  " + e);
                return;
            }
        }
    }

    private class VideoAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public class ViewHolder {
            TextView indexText;
            TextView titleText;
            TextView durationText;
            TextView sizeText;
            TextView modifiedText;
            ImageView playIndicator;
        }

        public VideoAdapter(Context context) {
            this.mContext = context;
            this.mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mVideoList.size();
        }

        @Override
        public Object getItem(int p) {
            return mVideoList.get(p);
        }

        @Override
        public long getItemId(int p) {
            return p;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_layout, null);
                holder.indexText = (TextView) convertView.findViewById(R.id.tv_index);
                holder.titleText = (TextView) convertView.findViewById(R.id.tv_title);
                holder.durationText = (TextView) convertView.findViewById(R.id.tv_duration);
                holder.durationText.setVisibility(View.GONE);
                holder.modifiedText = (TextView) convertView.findViewById(R.id.tv_modify);
                holder.sizeText = (TextView) convertView.findViewById(R.id.tv_size);
                holder.playIndicator = (ImageView) convertView.findViewById(R.id.play_indicator);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // display the infomation of widget.
            VideoInfo info = mVideoList.get(position);
            if (null == info) {
                return convertView;
            }

            holder.titleText.setText(info.title);

            /*
             * if (info.duration != null) { String time = convertDurationToTime(info.duration);
             * holder.durationText.setText(time); } else { holder.durationText.setText("00:00:00");
             * }
             */

            long videosize = 100 * Long.valueOf(mVideoList.get(position).size) / (1024 * 1024);
            float ft_size = videosize / (float) 100.0;
            holder.sizeText.setText(ft_size + "M");
            if (info.modified != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String st = sdf.format(new Date(Long.valueOf(info.modified)));
                holder.modifiedText.setText(st);
            }

            holder.indexText.setText((position + 1) + ".");

            //if (VideoPlayerApp.GetInstance().getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_NONE
            //        && info.isPlaying) {
            if(info.isPlaying){
                // convertView.setBackgroundResource(R.drawable.list_item_down);
                holder.playIndicator.setVisibility(View.VISIBLE);
            } else {
                // convertView.setBackgroundResource(R.drawable.list_item_normal);
                holder.playIndicator.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

}
