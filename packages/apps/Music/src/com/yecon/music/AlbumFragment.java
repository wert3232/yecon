
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.yecon.music.MediaPlaybackService;
import com.yecon.music.MusicUtils;

import static com.yecon.music.MusicUtils.Defs.*;
import static com.yecon.music.util.DebugUtil.*;

@SuppressLint("HandlerLeak")
public class AlbumFragment extends Fragment implements OnTouchListener, OnItemClickListener {
    private static final String ALBUM_SHARE_FILE = "album_share";

    protected static final String TAG = "AlbumFragment";

    private static int mLastListPosCourse = -1;
    @SuppressWarnings("unused")
    private static int mLastListPosFine = -1;

    private String mArtistId;
    private String mCurrentAlbumId;

    private RelativeLayout mLayoutError;

    private AlbumListAdapter mAlbumAdapter;
    private TrackListAdapter mTrackAdapter;
    private ListView mAlbumList;
    private Cursor mAlbumCursor;

    private Activity mActivity;

    private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAlbumList.invalidateViews();

            mAlbumList.setSelection(getPlayingPosition());

            autoExit();
        }
    };

    private BroadcastReceiver mScanListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            printLog("AlbumFragment - mScanListener - action: " + action, true);

            if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                mReScanHandler.sendEmptyMessage(0);
            }

            if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                MusicUtils.clearAlbumArtCache();
            }
        }
    };

    private Handler mReScanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAlbumAdapter != null) {
                getAlbumCursor(mAlbumAdapter.getQueryHandler(), null);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        printLog("AlbumFragment - onCreateView", true);

        initData();

        View view = initUI(inflater, container);

        initRecevier();

        autoExit();

        return view;
    }

    private void initData() {
        SharedPreferences prefs = mActivity.getSharedPreferences(ALBUM_SHARE_FILE,
                Context.MODE_PRIVATE);
        if (prefs != null) {
            mArtistId = prefs.getString("artist", "");
            mCurrentAlbumId = prefs.getString("selectedalbum", "");

            printLog("AlbumFragment - onCreate - mCurrentAlbumId: " + mCurrentAlbumId
                    + " - mArtistId: " + mArtistId, false);
        }
    }

    private View initUI(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.yecon_album_fragment_layout, container, false);

        mLayoutError = (RelativeLayout) view.findViewById(R.id.layout_error);

        mAlbumList = (ListView) view.findViewById(R.id.lv_album);
        mAlbumList.setCacheColorHint(0);
        mAlbumList.setOnItemClickListener(this);
        mAlbumList.setTextFilterEnabled(true);
        mAlbumList.setOnTouchListener(this);

        mAlbumAdapter = new AlbumListAdapter(mActivity,
                this,
                R.layout.track_list_item_group,
                null, // cursor
                new String[] {},
                new int[] {},
                0);

        mTrackAdapter = new TrackListAdapter(mActivity,
                this,
                R.layout.track_list_item_group,
                null, // cursor
                new String[] {},
                new int[] {},
                0);

        if (mAlbumAdapter != null) {
            mAlbumList.setAdapter(mAlbumAdapter);
            mAlbumCursor = mAlbumAdapter.getCursor();
            if (mAlbumCursor != null) {
                init(mAlbumCursor);
            } else {
                getAlbumCursor(mAlbumAdapter.getQueryHandler(), null);
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

        printLog("AlbumFragment - onResume", true);

        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.QUEUE_CHANGED);
        mActivity.registerReceiver(mTrackListListener, f);
        // mTrackListListener.onReceive(null, null);
    }

    @Override
    public void onPause() {
        super.onPause();

        printLog("AlbumFragment - onPause", true);

        mActivity.unregisterReceiver(mTrackListListener);
        mReScanHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        printLog("AlbumFragment - onDestroyView", true);

        SharedPreferences prefs = mActivity.getSharedPreferences(ALBUM_SHARE_FILE,
                Context.MODE_PRIVATE);
        if (prefs != null) {
            Editor editor = prefs.edit();
            editor.putString("artist", mArtistId);
            editor.putString("selectedalbum", mCurrentAlbumId);
            editor.commit();

            printLog("AlbumFragment - onDestroyView - mCurrentAlbumId: " + mCurrentAlbumId
                    + " - mArtistId: " + mArtistId, false);
        }

        if (mAlbumList != null) {
            mLastListPosCourse = mAlbumList.getFirstVisiblePosition();
            View view = mAlbumList.getChildAt(0);
            if (view != null) {
                mLastListPosFine = view.getTop();
            }
        }

        if (mAlbumAdapter != null) {
            mAlbumAdapter.changeCursor(null);
        }

        mActivity.unregisterReceiver(mScanListener);
        mAlbumAdapter = null;
    }

    public void init(Cursor cursor) {
        if (mAlbumAdapter == null) {
            return;
        }

        if (!checkStorageExist()) {
            cursor = null;
        }

        mAlbumAdapter.changeCursor(cursor);

        if (updateErrorLayout()) {
            return;
        }

        mAlbumList.setSelection(getPlayingPosition());

        // restore previous position
        if (mLastListPosCourse >= 0) {
            // mAlbumListView.setSelectionFromTop(mLastListPosCourse,
            // mLastListPosFine);
            mLastListPosCourse = -1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case SCAN_DONE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    mActivity.finish();
                } else {
                    getAlbumCursor(mAlbumAdapter.getQueryHandler(), null);
                }
                break;
        }
    }
    
    private long curAlbumId = 0;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListAdapter adapter = mAlbumList.getAdapter();
        if (adapter != null) {
            if (adapter instanceof AlbumListAdapter) {
                AlbumListAdapter albumAdapter = (AlbumListAdapter) adapter;
                Cursor cursor = albumAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    mAlbumAdapter.setLastFirstVisible(mAlbumList.getFirstVisiblePosition());

                    curAlbumId = cursor.getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Albums._ID));
                    Cursor cs = MusicUtils.getTrackCursorByAlbum(getActivity(), curAlbumId);
                    mTrackAdapter.changeCursor(cs);
                    mAlbumList.setAdapter(mTrackAdapter);
                    mAlbumList.setSelection(MusicUtils.getPlayingTrackPosition(cs));
                }
            } else if (adapter instanceof TrackListAdapter) {
                TrackListAdapter adapterTrack = (TrackListAdapter) adapter;
                MusicUtils.playAlbum(mActivity, adapterTrack.getCursor(), position, curAlbumId);
            }
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        autoExit();

        return false;
    }

    public boolean onBackPressed() {
        ListAdapter adapter = mAlbumList.getAdapter();
        if (adapter == null) {
            return true;
        }

        if (adapter instanceof AlbumListAdapter) {
            return true;
        } else {
            autoExit();

            mAlbumList.setAdapter(mAlbumAdapter);
            mAlbumList.setSelection(mAlbumAdapter.getLastFirstVisible());
        }
        return false;
    }

    private boolean updateErrorLayout() {
        if (mAlbumCursor == null || (mAlbumCursor != null && mAlbumCursor.getCount() == 0)) {
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

        ListAdapter adapter = mAlbumList.getAdapter();
        if (adapter != null) {
            if (adapter instanceof AlbumListAdapter) {
                AlbumListAdapter albumAdapter = (AlbumListAdapter) adapter;
                Cursor cursor = albumAdapter.getCursor();
                if (cursor != null) {
                    long currentAlbumId = MusicUtils.getCurrentAlbumId();

                    int albumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);

                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        long albumId = cursor.getLong(albumIdx);

                        if (currentAlbumId == albumId) {
                            break;
                        }

                        position++;
                        cursor.moveToNext();
                    }
                }
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

    private Cursor getAlbumCursor(AsyncQueryHandler async, String filter) {
        String[] cols = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
        };

        Cursor ret = null;
        if (mArtistId != null && mArtistId.length() != 0) {
            Uri uri = MediaStore.Audio.Artists.Albums.getContentUri("external",
                    Long.valueOf(mArtistId));
            if (!TextUtils.isEmpty(filter)) {
                uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
            }
            if (async != null) {
                async.startQuery(0, null, uri, cols, null, null,
                        MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            } else {
                ret = MusicUtils.query(mActivity, uri, cols, null, null,
                        MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            }
        } else {
            Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            if (!TextUtils.isEmpty(filter)) {
                uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
            }
            if (async != null) {
                async.startQuery(0, null, uri, cols, null, null,
                        MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            } else {
                ret = MusicUtils.query(mActivity, uri, cols, null, null,
                        MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            }
        }
        return ret;
    }

    private static class ViewHolder {
        TextView icon;
        TextView line1;
        TextView line2;
        ImageView play_indicator;
    }

    private static class AlbumBaseAdapter extends SimpleCursorAdapter {
        private int mLastFirstVisible = 0;

        public AlbumBaseAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                int flags) {
            super(context, layout, c, from, to, flags);
        }

        public int getLastFirstVisible() {
            return mLastFirstVisible;
        }

        public void setLastFirstVisible(int lastFirstVisible) {
            this.mLastFirstVisible = lastFirstVisible;
        }
    }

    private static class AlbumListAdapter extends AlbumBaseAdapter {
        private final Resources mResources;

        private final BitmapDrawable mDefaultAlbumIcon;

        private final String mUnknownAlbum;

        @SuppressWarnings("unused")
        private int mAlbumIdIdx;
        private int mAlbumIdx;
        private int mNumSongsIdx;

        private AlbumFragment mFragment;
        private AsyncQueryHandler mQueryHandler;

        class QueryHandler extends AsyncQueryHandler {
            QueryHandler(ContentResolver res) {
                super(res);
            }

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                mFragment.init(cursor);
            }
        }

        public AlbumListAdapter(Context context, AlbumFragment currentFragment, int layout,
                Cursor cursor, String[] from, int[] to, int flags) {
            super(context, layout, cursor, from, to, flags);

            mResources = context.getResources();

            mUnknownAlbum = context.getString(R.string.unknown_album_name);

            Bitmap b = BitmapFactory
                    .decodeResource(mResources, R.drawable.albumart_mp_unknown_list);
            mDefaultAlbumIcon = new BitmapDrawable(context.getResources(), b);
            // no filter or dither, it's a lot faster and we can't tell the
            // difference
            mDefaultAlbumIcon.setFilterBitmap(false);
            mDefaultAlbumIcon.setDither(false);

            mFragment = currentFragment;

            mQueryHandler = new QueryHandler(context.getContentResolver());

            getColumnIndices(cursor);
        }

        private void getColumnIndices(Cursor cursor) {
            if (cursor != null) {
                mAlbumIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
                mAlbumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                mNumSongsIdx = cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
            }
        }

        public AsyncQueryHandler getQueryHandler() {
            return mQueryHandler;
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

            holder.icon.setBackgroundResource(R.drawable.ic_play_album);

            String name = cursor.getString(mAlbumIdx);
            String displayname = name;
            boolean unknown = (name == null || name.equals(MediaStore.UNKNOWN_STRING));
            if (unknown) {
                displayname = mUnknownAlbum;
            }
            holder.line1.setText(displayname);

            int numsongs = cursor.getInt(mNumSongsIdx);
            String songs = MusicUtils.makeAlbumsLabel(context, 0, numsongs, true);
            holder.line2.setText(songs);

            long aid = cursor.getLong(0);
            long currentalbumid = MusicUtils.getCurrentAlbumId();
            if (currentalbumid == aid) {
                holder.play_indicator.setVisibility(View.VISIBLE);
                // view.setBackgroundResource(R.drawable.list_item_down);
            } else {
                holder.play_indicator.setVisibility(View.INVISIBLE);
                // view.setBackgroundResource(R.drawable.list_item_normal);
            }
        }

        @Override
        public void changeCursor(Cursor cursor) {
            if ((mFragment.isDetached() || mFragment.isRemoving())
                    && mFragment.mAlbumCursor != null) {
                printLog("AlbumFragment - changeCursor- mArtistCursor will close", true);
                mFragment.mAlbumCursor.close();
                mFragment.mAlbumCursor = null;
            }

            if (cursor != mFragment.mAlbumCursor) {
                if (cursor == null && mFragment.mAlbumCursor != null) {
                    mFragment.mAlbumCursor.close();
                }
                mFragment.mAlbumCursor = cursor;
                getColumnIndices(cursor);
                super.changeCursor(cursor);
            }
        }

    }

    private static class TrackListAdapter extends AlbumBaseAdapter {
        private final String mUnknownArtist;

        private int mAudioIdIdx;
        private int mTitleIdx;
        private int mArtistIdx;

        public TrackListAdapter(Context context, AlbumFragment currentFragment, int layout,
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
