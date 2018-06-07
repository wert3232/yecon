package com.yecon.videoplayer2;

import com.yecon.videoplayer2.PlayListData.DBOpenHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlayListProvider extends ContentProvider {
	public static final String ROOT_URI = "com.yecon.videoplayer2.provider";
	PlayListData.DBOpenHelper dbOpenHelper;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);   
    private static final int PLAYING = 1; 
    private static final int PLAYLIST = 2;   
    private static final int LISTITEM = 3;   
    static{   
        MATCHER.addURI(ROOT_URI, "playing", PLAYING);    
        MATCHER.addURI(ROOT_URI, "playlist", PLAYLIST);    
        MATCHER.addURI(ROOT_URI, "listitem/#", LISTITEM);
    } 
    
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		this.dbOpenHelper = new DBOpenHelper(this.getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,   
            String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();   
        switch (MATCHER.match(uri)) {   
        case PLAYING:   
            return db.query(PlayListData.TABLE_PLAYING, projection, selection, selectionArgs, null, null, sortOrder);   
        case PLAYLIST:   
            return db.query(PlayListData.TABLE_PLAYING_LIST, projection, selection, selectionArgs, null, null, sortOrder);   
        case LISTITEM:   
            long id = ContentUris.parseId(uri);   
            String where = "_id = "+ id;   
            if(selection!=null && !"".equals(selection)){   
                where = selection + " and " + where;   
            }   
            return db.query(PlayListData.TABLE_PLAYING_LIST, projection, where, selectionArgs, null, null, sortOrder);   
        default:   
            throw new IllegalArgumentException("Unkwon Uri:"+ uri.toString());   
        }   
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
