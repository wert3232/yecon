
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
import android.content.res.Resources;
import android.database.Cursor;
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
public class ArtistFragment extends Fragment implements OnTouchListener, OnItemClickListener {

    private static int mLastListPosCourse = -1;
    @SuppressWarnings("unused")
    private static int mLastListPosFine = -1;

    private RelativeLayout mLayoutError;

    private ArtistListAdapter mArtistAdapter;
    private TrackListAdapter mTrackAdapter;
    private ListView mArtistList;
    private Cursor mArtistCursor;

    private Activity mActivity;

    private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mArtistList.invalidateViews();

            mArtistList.setSelection(getPlayingPosition());

            autoExit();
        }
    };

    private BroadcastReceiver mScanListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            printLog("ArtistFragment - mScanListener - action: " + action, true);

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
            if (mArtistAdapter != null) {
                printLog("ArtistFragment - mReScanHandler", true);
                getArtistCursor(mArtistAdapter.getQueryHandler(), null);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        printLog("ArtistFragment - onCreate", true);

        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        printLog("ArtistFragment - onCreateView - start", true);

        View view = initUI(inflater, container);

        initRecevier();

        autoExit();

        printLog("ArtistFragment - onCreateView - end", false);

        return view;
    }

    private View initUI(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.yecon_artist_fragment_layout, container, false);

        mLayoutError = (RelativeLayout) view.findViewById(R.id.layout_error);

        mArtistList = (ListView) view.findViewById(R.id.lv_artist);
        mArtistList.setCacheColorHint(0);
        mArtistList.setOnItemClickListener(this);
        mArtistList.setTextFilterEnabled(true);
        mArtistList.setOnTouchListener(this);

        mArtistAdapter = new ArtistListAdapter(mActivity,
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

        if (mArtistAdapter != null) {
            mArtistList.setAdapter(mArtistAdapter);
            mArtistCursor = mArtistAdapter.getCursor();
            if (mArtistCursor != null) {
                init(mArtistCursor);
            } else {
                getArtistCursor(mArtistAdapter.getQueryHandler(), null);
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

        printLog("ArtistFragment - onResume", true);

        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.QUEUE_CHANGED);
        mActivity.registerReceiver(mTrackListListener, f);
        // mTrackListListener.onReceive(null, null);
    }

    @Override
    public void onPause() {
        printLog("ArtistFragment - onPause", true);

        mActivity.unregisterReceiver(mTrackListListener);
        mReScanHandler.removeCallbacksAndMessages(null);

        super.onPause();
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        printLog("ArtistFragment - onDestroyView", true);

        if (mArtistList != null) {
            mLastListPosCourse = mArtistList.getFirstVisiblePosition();
            View view = mArtistList.getChildAt(0);
            if (view != null) {
                mLastListPosFine = view.getTop();
            }
        }

        if (mArtistAdapter != null) {
            // mAdapter.changeCursor(mArtistCursor);
            mArtistAdapter.changeCursor(null);
        }

        mActivity.unregisterReceiver(mScanListener);
        mArtistAdapter = null;
    }

    public void init(Cursor cursor) {
        if (mArtistAdapter == null) {
            return;
        }

        if (!checkStorageExist()) {
            cursor = null;
        }

        mArtistAdapter.changeCursor(cursor);

        if (updateErrorLayout()) {
            return;
        }

        mArtistList.setSelection(getPlayingPosition());

        // restore previous position
        if (mLastListPosCourse >= 0) {
            // mArtistListView.setSelectionFromTop(mLastListPosCourse,
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
                    getArtistCursor(mArtistAdapter.getQueryHandler(), null);
                }
                break;
        }
    }

    private long curArtistId;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListAdapter adapter = mArtistList.getAdapter();
        if (adapter != null) {
            if (adapter instanceof ArtistListAdapter) {
                ArtistListAdapter artistAdapter = (ArtistListAdapter) adapter;
                Cursor cursor = artistAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    mArtistAdapter.setLastFirstVisible(mArtistList.getFirstVisiblePosition());

                    curArtistId = cursor.getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));
                    Cursor cs = MusicUtils.getTrackCursorByArtist(getActivity(), curArtistId);
                    mTrackAdapter.changeCursor(cs);
                    mArtistList.setAdapter(mTrackAdapter);
                    mArtistList.setSelection(MusicUtils.getPlayingTrackPosition(cs));
                }
            } else if (adapter instanceof TrackListAdapter) {
                TrackListAdapter adapterTrack = (TrackListAdapter) adapter;
                MusicUtils.playArtist(mActivity, adapterTrack.getCursor(), position,curArtistId);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        autoExit();

        return false;
    }

    public boolean onBackPressed() {
        ListAdapter adapter = mArtistList.getAdapter();
        if (adapter == null) {
            return true;
        }

        if (adapter instanceof ArtistListAdapter) {
            return true;
        } else {
            autoExit();

            mArtistList.setAdapter(mArtistAdapter);
            mArtistList.setSelection(mArtistAdapter.getLastFirstVisible());
        }
        return false;
    }

    private boolean updateErrorLayout() {
        if (mArtistCursor == null || (mArtistCursor != null && mArtistCursor.getCount() == 0)) {
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

        ListAdapter adapter = mArtistList.getAdapter();
        if (adapter != null) {
            if (adapter instanceof ArtistListAdapter) {
                ArtistListAdapter artistAdapter = (ArtistListAdapter) adapter;
                Cursor cursor = artistAdapter.getCursor();
                if (cursor != null) {
                    long currentArtistId = MusicUtils.getCurrentArtistId();

                    int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);

                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        long artistId = cursor.getLong(artistIdx);

                        if (currentArtistId == artistId) {
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

    private Cursor getArtistCursor(AsyncQueryHandler async, String filter) {
        String[] cols = new String[] {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        if (!TextUtils.isEmpty(filter)) {
            uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
        }

        Cursor ret = null;
        if (async != null) {
            async.startQuery(0, null, uri, cols, null, null, MediaStore.Audio.Artists.ARTIST_KEY);
        } else {
            ret = MusicUtils.query(mActivity, uri, cols, null, null,
                    MediaStore.Audio.Artists.ARTIST_KEY);
        }
        return ret;
    }

    private static class ViewHolder {
        TextView icon;
        TextView line1;
        TextView line2;
        ImageView play_indicator;
    }

    private static class ArtistBaseAdapter extends SimpleCursorAdapter {
        private int mLastFirstVisible = 0;

        public ArtistBaseAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
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

    private static class ArtistListAdapter extends ArtistBaseAdapter {
        private final Resources mResources;

        private final BitmapDrawable mDefaultAlbumIcon;

        private final String mUnknownArtist;

        private int mArtistIdIdx;
        private int mArtistIdx;
        private int mAlbumIdx;
        private int mSongIdx;

        private ArtistFragment mFragment;
        private AsyncQueryHandler mQueryHandler;

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

        ArtistListAdapter(Context context, ArtistFragment currentFragment, int layout,
                Cursor cursor, String[] from, int[] to, int flags) {
            super(context, layout, cursor, from, to, flags);

            mResources = context.getResources();

            mFragment = currentFragment;

            mQueryHandler = new QueryHandler(context.getContentResolver());

            mDefaultAlbumIcon = (BitmapDrawable) mResources
                    .getDrawable(R.drawable.albumart_mp_unknown_list);
            // no filter or dither, it's a lot faster and we can't tell the
            // difference
            mDefaultAlbumIcon.setFilterBitmap(false);
            mDefaultAlbumIcon.setDither(false);

            getColumnIndices(cursor);

            mUnknownArtist = context.getString(R.string.unknown_artist_name);
        }

        private void getColumnIndices(Cursor cursor) {
            if (cursor != null) {
                mArtistIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
                mArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                mAlbumIdx = cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
                mSongIdx = cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
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

            holder.icon.setBackgroundResource(R.drawable.ic_play_artist);

            String artist = cursor.getString(mArtistIdx);
            String displayartist = artist;
            boolean unknown = artist == null || artist.equals(MediaStore.UNKNOWN_STRING);
            if (unknown) {
                displayartist = mUnknownArtist;
            }
            holder.line1.setText(displayartist);

            int numalbums = cursor.getInt(mAlbumIdx);
            int numsongs = cursor.getInt(mSongIdx);

            String songs = MusicUtils.makeArtistLabel(context, numsongs, unknown);
            holder.line2.setText(songs);

            long currentartistid = MusicUtils.getCurrentArtistId();
            long artistid = cursor.getLong(mArtistIdIdx);
            if (currentartistid == artistid) {
                holder.play_indicator.setVisibility(View.VISIBLE);
                // view.setBackgroundResource(R.drawable.list_item_down);
            } else {
                holder.play_indicator.setVisibility(View.INVISIBLE);
                // view.setBackgroundResource(R.drawable.list_item_normal);
            }

            StringBuffer logBuffer = new StringBuffer();
            logBuffer.append("ArtistFragment - bindGroupView - Artists._ID: ");
            logBuffer.append(artistid);
            logBuffer.append(" - Artists.ARTIST: ");
            logBuffer.append(displayartist);
            logBuffer.append(" - Artists.NUMBER_OF_ALBUMS: ");
            logBuffer.append(numalbums);
            logBuffer.append(" - Artists.NUMBER_OF_TRACKS: ");
            logBuffer.append(numsongs);
            logBuffer.append(" - path: ");
            logBuffer.append(MusicUtils.getCurrentPath());
            printLog(logBuffer.toString(), true);
        }

        @Override
        public void changeCursor(Cursor cursor) {
            if ((mFragment.isDetached() || mFragment.isRemoving())
                    && mFragment.mArtistCursor != null) {
                printLog("TrackListFragment - changeCursor- mArtistCursor will close", true);
                mFragment.mArtistCursor.close();
                mFragment.mArtistCursor = null;
            }

            if (cursor != mFragment.mArtistCursor) {
                if (cursor == null && mFragment.mArtistCursor != null) {
                    mFragment.mArtistCursor.close();
                }
                mFragment.mArtistCursor = cursor;
                getColumnIndices(cursor);

                super.changeCursor(cursor);
            }
        }

    }

    private static class TrackListAdapter extends ArtistBaseAdapter {
        private final String mUnknownArtist;

        private int mAudioIdIdx;
        private int mTitleIdx;
        private int mArtistIdx;

        public TrackListAdapter(Context context, ArtistFragment currentFragment, int layout,
                Cursor c,
                String[] from, int[] to, int flags) {
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
