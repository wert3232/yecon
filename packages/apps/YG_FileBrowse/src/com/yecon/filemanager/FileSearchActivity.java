package com.yecon.filemanager;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import net.micode.fileexplorer.Util;


/**
 * Created by chenchu on 15-3-19.
 */
public class FileSearchActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String Tag ="file search activity";

    static final Uri exUri = MediaStore.Files.getContentUri("external");
    static final Uri inUri = MediaStore.Files.getContentUri("internal");

    //static final String[] projection = {BaseColumns._ID,MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.TITLE};

    static final String[] projection = {BaseColumns._ID,MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.TITLE};

    static final String selection = MediaStore.MediaColumns.TITLE+ " LIKE ?";

    static final String[] exteral_storage_path = {FileStorageStateListener.pathSD1,
    																	FileStorageStateListener.pathSD2,
    																	FileStorageStateListener.pathUSB1,
    																	FileStorageStateListener.pathUSB2,
    																	FileStorageStateListener.pathUSB3,
    																	FileStorageStateListener.pathUSB4,
    																	FileStorageStateListener.pathUSB5,    																	
    																	FileStorageStateListener.USB_ROOT_PATH,
                                                };
    static final String[] internal_storage_path = {Util.getSdDirectory()};

    static final String[] getSelectionArgs(String query) {
        String arg = "%"+query+"%";
        Log.d(Tag,arg);
        return new String[]{arg};
    }

    private FileCursorAdapter mAdapter;

    private String mQuery;

    private static class FileCursorAdapter extends CursorAdapter {

        FileCursorAdapter(Context context,Cursor cursor) {
            super(context,cursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(Tag,"on bind view");
            TextView tv1 = (TextView) view.findViewById(R.id.search_item_name);
            int index = cursor.getColumnIndex(projection[2]);
            tv1.setText(cursor.getString(index));
            TextView tv2 = (TextView) view.findViewById(R.id.search_item_path);
            index = cursor.getColumnIndex(projection[1]);
            tv2.setText(cursor.getString(index));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.d(Tag,"on new view");
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.fragment_list_search_item,parent,false);
            return v;
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(Tag,"on load create");
        CursorLoader loader = null;
        if (id == 0 && args != null) {
            String query = args.getString(SearchManager.QUERY);
            loader = new CursorLoader(FileSearchActivity.this,exUri,projection,selection,getSelectionArgs(query),null);
        }
        return loader;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(Tag,"on load reset");
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //mAdapter.swapCursor(data);
        Log.d(Tag,"on load finished");
        if (data != null) {
            if (mAdapter == null) {
                Log.d(Tag,"create adapter = "+ data.getCount());
                mAdapter = new FileCursorAdapter(FileSearchActivity.this, data);
                FileSearchActivity.this.setListAdapter(mAdapter);
            } else {
                Log.d(Tag,"swap cursor");
                mAdapter.swapCursor(data);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Tag, "on create");
       // setContentView(R.layout.activity_search);
        //mAdapter = new FileCursorAdapter(this);
        //setListAdapter(mAdapter);
        //((ListFragment) getFragmentManager().findFragmentById(R.id.fragment_list)).setListAdapter(mAdapter);
        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mQuery = bundle.getString(SearchManager.QUERY,"whatever");
                Log.d(Tag,bundle.getString(SearchManager.QUERY,"whatever"));
                onQuery(bundle);
            } else {
                Log.d(Tag,"bundle is null");
            }
        }
    }

     MediaScannerConnection.OnScanCompletedListener mOnScanCompletedListener = new MediaScannerConnection.OnScanCompletedListener() {

        boolean part1 = false;
        boolean part2 = false;
        boolean part3 = false;
        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (uri != null) {
                if (path.equals(exteral_storage_path[0])) {
                    part1 = true;
                } else if (path.equals(exteral_storage_path[1])) {
                    part2 = true;
                } else if (path.equals(exteral_storage_path[2])) {
                    part3 = true;
                }
                if (part1 && part2) {
                    Log.d(Tag,"true");
                    part1 = part2 = part3 = false;
                    Bundle bundle = new Bundle();
                    bundle.putString(SearchManager.QUERY, mQuery);
                    getLoaderManager().initLoader(0, bundle, FileSearchActivity.this);
                }
            } else {
                part1 = part2 = part3 = false;
            }
        }
    };

    private void onQuery(Bundle bundle) {
        getLoaderManager().initLoader(0, bundle, this);
    }
}
