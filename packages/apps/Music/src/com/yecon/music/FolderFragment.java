
package com.yecon.music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.yecon.music.MediaPlaybackService;
import com.yecon.music.MusicUtils;

import java.util.ArrayList;

import static com.yecon.music.MediaScanStatus.*;
import static com.yecon.music.MusicConstant.*;
import static com.yecon.music.MusicUtils.Defs.*;
import static com.yecon.music.util.DebugUtil.*;

@SuppressLint("HandlerLeak")
public class FolderFragment extends Fragment implements OnTouchListener, OnItemClickListener {
    private static int mLastListPosCourse = -1;
    @SuppressWarnings("unused")
    private static int mLastListPosFine = -1;

    private RelativeLayout mLayoutError;

    private FolderListAdapter mFolderAdapter;
    private TrackListAdapter mTrackAdapter;
    private ListView mFolderList;
    private Cursor mFolderCursor;

    private int mFolderPlayingPosition;

    private Activity mActivity;

    private ArrayList<MusicFileInfo> mFolderInfo = new ArrayList<MusicFileInfo>();

    private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mFolderList.invalidateViews();

            mFolderList.setSelection(getPlayingPosition());

            autoExit();
        }
    };

    private BroadcastReceiver mScanListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
                setMediaScanStatus(MEDIA_SCAN_STATUS_STARTED);
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                setMediaScanStatus(MEDIA_SCAN_STATUS_FINISHED);
            }

            if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                mReScanHandler.sendEmptyMessage(0);
            }

            if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                MusicUtils.clearAlbumArtCache();
            }
        }
    };

    private Handler mReScanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mFolderAdapter != null) {
                getFolderCursor(mFolderAdapter.getQueryHandler(), null);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        printLog("FolderFragment - onCreate", true);

        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        printLog("FolderFragment - onCreateView", true);

        View view = initUI(inflater, container);

        initRecevier();

        autoExit();

        return view;
    }

    private View initUI(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.yecon_folder_fragment_layout, container, false);

        mLayoutError = (RelativeLayout) view.findViewById(R.id.layout_error);

        mFolderList = (ListView) view.findViewById(R.id.lv_folder);
        mFolderList.setCacheColorHint(0);
        mFolderList.setOnItemClickListener(this);
        mFolderList.setTextFilterEnabled(true);
        mFolderList.setOnTouchListener(this);

        mFolderAdapter = new FolderListAdapter(mActivity, this);

        mTrackAdapter = new TrackListAdapter(mActivity,
                this,
                R.layout.track_list_item_group,
                null, // cursor
                new String[] {},
                new int[] {},
                0);

        if (mFolderList != null) {
            mFolderList.setAdapter(mFolderAdapter);
            if (mFolderCursor != null) {
                init(mFolderCursor);
            } else {
                getFolderCursor(mFolderAdapter.getQueryHandler(), null);
            }
        }

        return view;
    }

    private void initRecevier() {
        IntentFilter f = new IntentFilter();
        f.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        f.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        f.addDataScheme("file");
        mActivity.registerReceiver(mScanListener, f);
    }

    @Override
    public void onResume() {
        super.onResume();

        printLog("FolderFragment - onResume", true);

        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.QUEUE_CHANGED);
        mActivity.registerReceiver(mTrackListListener, f);
        // mTrackListListener.onReceive(null, null);
    }

    @Override
    public void onPause() {
        super.onPause();

        printLog("FolderFragment - onPause", true);

        mActivity.unregisterReceiver(mTrackListListener);
        mReScanHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        printLog("FolderFragment - onDestroyView", true);

        if (mFolderList != null) {
            mLastListPosCourse = mFolderList.getFirstVisiblePosition();
            View view = mFolderList.getChildAt(0);
            if (view != null) {
                mLastListPosFine = view.getTop();
            }
        }

        if (mFolderCursor != null) {
            mFolderCursor.close();
            mFolderCursor = null;
        }

        mActivity.unregisterReceiver(mScanListener);

        mFolderAdapter = null;
    }

    public void init(Cursor cursor) {
        if (mFolderAdapter == null) {
            return;
        }

        if (!checkStorageExist()) {
            cursor = null;
        }

        if (cursor != mFolderCursor) {
            mFolderCursor = cursor;
        }

        if (updateErrorLayout()) {
            return;
        }

        printLog("FolderFragment - init - start", true);

        // restore previous position
        if (mLastListPosCourse >= 0) {
            // mFolderListView.setSelectionFromTop(mLastListPosCourse,
            // mLastListPosFine);
            mLastListPosCourse = -1;
        }

        initFolderInfo();

        printLog("FolderFragment - init - end", true);
    }

    private void initFolderInfo() {
        printLog("FolderFragment - initFolderInfo - start", true);

        getFolderInfo();

        mFolderList.setSelection(getPlayingPosition());
    }

    private void getFolderInfo() {
        printLog("FolderFragment - getFolderInfo - get folder info - start", true);

        if (mFolderCursor == null) {
            return;
        }

        mFolderPlayingPosition = 0;

        if (mFolderInfo != null && mFolderInfo.size() > 0) {
            mFolderInfo.clear();
        }

        /**
         * 1、调用接口getFolderCursor()去查询媒体文件信息表来获取文件id、文件名及文件路径等信息；
         * 2、在同一个文件夹下的音乐文件，其parent信息是相同的，因此根据parent来获取非重复的音乐文件所属的文件夹信息；
         */
        int fileIdx = mFolderCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
        int folderIdx = mFolderCursor
                .getColumnIndexOrThrow(MediaStore.Files.FileColumns.FOLDER_NAME);
        int parentIdx = mFolderCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT);
        int titleIdx = mFolderCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);

        int parent = 0;
        int count = 0;
        String folder = "";
        String title = "";
        String artist = "";

        long audioId = MusicUtils.getCurrentAudioId();

        mFolderCursor.moveToFirst();
        while (!mFolderCursor.isAfterLast()) {
            int tempParent = mFolderCursor.getInt(parentIdx);

            if (parent == 0 || parent == tempParent) {
                count++;
            }

            if (parent != tempParent) {
                folder = mFolderCursor.getString(folderIdx);

                if (parent != 0) {
                    if (mFolderInfo != null) {
                        int size = mFolderInfo.size();
                        if (size > 0) {
                            MusicFileInfo folderInfo = mFolderInfo.get(size - 1);
                            folderInfo.setCount(count);
                        }
                    }

                    count = 1;
                }

                MusicFileInfo folderInfo = new MusicFileInfo();

                long id = mFolderCursor.getLong(fileIdx);
                folderInfo.setId(id);

                parent = tempParent;

                // "/mnt/ext_sdcard1/music/xx.mp3"
                int last = folder.lastIndexOf("/");
                int length = folder.length();
                String name = folder.substring(last + 1, length);

                title = mFolderCursor.getString(titleIdx);

                // StringBuffer logBuffer = new StringBuffer();
                // logBuffer.append("FolderFragment - init - group - title: ");
                // logBuffer.append(title);
                // logBuffer.append(" - path: ");
                // logBuffer.append(path);
                // logBuffer.append(" - name: ");
                // logBuffer.append(name);
                // logBuffer.append(" - artist: ");
                // logBuffer.append(artist);
                // logBuffer.append(" - parent: ");
                // logBuffer.append(parent);
                // printLog(logBuffer.toString(), true);

                folderInfo.setParent(parent);
                folderInfo.setDirName(name);
                folderInfo.setDirPath(folder + "/");
                folderInfo.setFilename(title);
                folderInfo.setArtistName(artist);
                mFolderInfo.add(folderInfo);
            }

            long fileId = mFolderCursor.getLong(fileIdx);

            // StringBuffer logBuffer = new StringBuffer();
            // logBuffer.append("FolderFragment - init - fileId: ");
            // logBuffer.append(fileId);
            // logBuffer.append(" - audioId: ");
            // logBuffer.append(audioId);
            // printLog(logBuffer.toString(), false);

            if (mFolderInfo != null) {
                int size = mFolderInfo.size();
                if (size > 0) {
                    MusicFileInfo folderInfo = mFolderInfo.get(size - 1);
                    if (fileId == audioId) {
                        mFolderPlayingPosition = size - 1;
                        folderInfo.setIsPlaying(true);
                    }
                }
            }

            mFolderCursor.moveToNext();
        }

        if (mFolderInfo != null) {
            int size = mFolderInfo.size();
            if (size > 0) {
                MusicFileInfo folderInfo = mFolderInfo.get(size - 1);
                folderInfo.setCount(count);
            }
        }

        printLog("FolderFragment - getFolderInfo - get folder info - end", true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCAN_DONE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    mActivity.finish();
                } else {
                    init(null);
                    // getFolderCursor(mAdapter.getQueryHandler(), null);
                }
                break;
        }

    }

    private long curParentId;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListAdapter adapter = mFolderList.getAdapter();
        if (adapter != null) {
            if (adapter instanceof FolderListAdapter) {
                mFolderAdapter.setLastFirstVisible(mFolderList.getFirstVisiblePosition());

                curParentId = mFolderInfo.get(position).getParent();
                Cursor cursor = getTrackCursor(curParentId);
                mTrackAdapter.changeCursor(cursor);
                mFolderList.setAdapter(mTrackAdapter);
                mFolderList.setSelection(MusicUtils.getPlayingTrackPosition(cursor));
            } else if (adapter instanceof TrackListAdapter) {
                TrackListAdapter adapterTrack = (TrackListAdapter) adapter;
                MusicUtils.playFolder(mActivity, adapterTrack.getCursor(), position, curParentId);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        autoExit();

        return false;
    }

    public boolean onBackPressed() {
        ListAdapter adapter = mFolderList.getAdapter();
        if (adapter == null) {
            return true;
        }

        if (adapter instanceof FolderListAdapter) {
            return true;
        } else {
            autoExit();

            mFolderList.setAdapter(mFolderAdapter);
            mFolderList.setSelection(mFolderAdapter.getLastFirstVisible());
        }
        return false;
    }

    private boolean updateErrorLayout() {
        if (mFolderCursor == null || (mFolderCursor != null && mFolderCursor.getCount() == 0)) {
            mLayoutError.setVisibility(View.VISIBLE);
            mReScanHandler.sendEmptyMessageDelayed(0, 1000);
            return true;
        } else {
            mLayoutError.setVisibility(View.GONE);
            return false;
        }
    }

    private int getPlayingPosition() {
        int position = 0;

        ListAdapter adapter = mFolderList.getAdapter();
        if (adapter != null) {
            if (adapter instanceof FolderListAdapter) {
                position = mFolderPlayingPosition;
            } else if (adapter instanceof TrackListAdapter) {
                TrackListAdapter adapterTrack = (TrackListAdapter) adapter;
                position = MusicUtils.getPlayingTrackPosition(adapterTrack.getCursor());
            }
        }

        return position;
    }

    private void autoExit() {
        ((MusicTrackActivity) mActivity).autoExit();
    }

    private boolean checkStorageExist() {
        return ((MusicTrackActivity) mActivity).checkStorageExist();
    }

    @SuppressLint("InlinedApi")
    private Cursor getFolderCursor(AsyncQueryHandler async, String filter) {
        printLog("FolderFragment - getFolderCursor - start", true);

        String[] cols = new String[] {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.FOLDER_NAME,
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        String sortOrder = MediaStore.Files.FileColumns.FOLDER_NAME;

        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Files.FileColumns.FOLDER_NAME);
        where.append(" != '' AND ");
        where.append(MediaStore.Audio.Media.IS_MUSIC);
        where.append("=1");

        Uri uri = MediaStore.Files.getContentUri("external");
        if (!TextUtils.isEmpty(filter)) {
            uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
        }

        Cursor ret = null;

        if (async != null) {
            async.startQuery(0, null, uri, cols, where.toString(), null, sortOrder);
        } else {
            ret = MusicUtils.query(mActivity, uri, cols, null, null, sortOrder);
        }

        printLog("FolderFragment - getFolderCursor - end", true);

        return ret;
    }

    private Cursor getTrackCursor(long parent) {
        String[] cols = new String[] {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.FOLDER_NAME,
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Audio.Media.ARTIST,
        };

        String sortOrder = MediaStore.Audio.Media.TITLE_KEY;

        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Files.FileColumns.TITLE);
        where.append(" != '' AND ");
        where.append(MediaStore.Files.FileColumns.PARENT);
        where.append("=");
        where.append(parent);
        where.append(" AND ");
        where.append(MediaStore.Audio.Media.IS_MUSIC);
        where.append("=1");

        Uri uri = MediaStore.Files.getContentUri("external");

        Cursor cursor = MusicUtils
                .query(mActivity, uri, cols, where.toString(), null, sortOrder);

        // if (cursor != null) {
        // cursor.moveToFirst();
        // while (!cursor.isAfterLast()) {
        // int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
        // String title = cursor.getString(titleIdx);
        // printLog("title: " + title);
        // cursor.moveToNext();
        // }
        // }
        return cursor;
    }

    private static class ViewHolder {
        TextView icon;
        TextView line1;
        TextView line2;
        ImageView play_indicator;
    }

    private class FolderListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        private FolderFragment mFragment;

        private AsyncQueryHandler mQueryHandler;

        private int mLastFirstVisible = 0;

        class QueryHandler extends AsyncQueryHandler {
            QueryHandler(ContentResolver res) {
                super(res);
            }

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                printLog("ArtistFragment - onQueryComplete", true);

                mFragment.init(cursor);
            }
        }

        public FolderListAdapter(Context context, FolderFragment currentFragment) {
            mInflater = LayoutInflater.from(context);
            mContext = context;

            mFragment = currentFragment;
            mQueryHandler = new QueryHandler(context.getContentResolver());
        }

        public AsyncQueryHandler getQueryHandler() {
            return mQueryHandler;
        }

        public int getLastFirstVisible() {
            return mLastFirstVisible;
        }

        public void setLastFirstVisible(int lastFirstVisible) {
            this.mLastFirstVisible = lastFirstVisible;
        }

        @Override
        public int getCount() {
            return mFolderInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return mFolderInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.track_list_item_group, null);
                holder.line1 = (TextView) convertView.findViewById(R.id.line1);
                holder.line2 = (TextView) convertView.findViewById(R.id.line2);
                holder.play_indicator = (ImageView) convertView.findViewById(R.id.play_indicator);
                holder.icon = (TextView) convertView.findViewById(R.id.icon);
                holder.icon.setBackgroundResource(R.drawable.ic_play_folder);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MusicFileInfo folderInfo = mFragment.mFolderInfo.get(position);

            holder.line1.setText(folderInfo.getDirName());
            holder.line2.setText(MusicUtils.makeFolderLabel(mContext, folderInfo.getCount()));

            if (folderInfo.isPlaying()) {
                holder.play_indicator.setVisibility(View.VISIBLE);
            } else {
                holder.play_indicator.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

    }

    private class TrackListAdapter extends SimpleCursorAdapter {
        private String mUnknownArtist;

        private int mAudioIdIdx;
        private int mTitleIdx;
        private int mArtistIdx;

        public TrackListAdapter(Context context, FolderFragment currentFragment, int layout,
                Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);

            mUnknownArtist = context.getString(R.string.unknown_artist_name);
        }

        private void getColumnIndices(Cursor cursor) {
            if (cursor != null) {
                mAudioIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                mTitleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                mArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = super.newView(context, cursor, parent);

            ViewHolder holder = new ViewHolder();
            holder.line1 = (TextView) view.findViewById(R.id.line1);
            holder.line2 = (TextView) view.findViewById(R.id.line2);
            holder.play_indicator = (ImageView) view.findViewById(R.id.play_indicator);
            holder.icon = (TextView) view.findViewById(R.id.icon);

            view.setTag(holder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            holder.icon.setText((cursor.getPosition() + 1) + ". ");

            String title = cursor.getString(mTitleIdx);
            holder.line1.setText(title);

            String artist = cursor.getString(mArtistIdx);
            if (artist == null || artist.equals(MediaStore.UNKNOWN_STRING)) {
                artist = mUnknownArtist;
            }
            holder.line2.setText(artist);

            long currentartistid = MusicUtils.getCurrentAudioId();
            long id = cursor.getLong(mAudioIdIdx);
            if (currentartistid == id) {
                holder.play_indicator.setVisibility(View.VISIBLE);
                // view.setBackgroundResource(R.drawable.list_item_down);
            } else {
                holder.play_indicator.setVisibility(View.INVISIBLE);
                // view.setBackgroundResource(R.drawable.list_item_normal);
            }
        }

        @Override
        public void changeCursor(Cursor cursor) {
            Cursor lastCursor = getCursor();
            if (lastCursor != null) {
                lastCursor.close();
                lastCursor = null;
            }
            if (cursor != lastCursor) {
                if (cursor != null) {
                    getColumnIndices(cursor);
                }
                super.changeCursor(cursor);
            }
        }

    }

}
