package com.tuoxianui.view;

import com.media.constants.MediaConstants;
import com.tuoxianui.db.ContentData;
import com.tuoxianui.db.DBOpenHelper;
import com.tuoxianui.view.util.TopLog;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

public class DeviceStateContentProvider  extends ContentProvider {
//	public static final int PROVIDER_ID_STORAGE = 1;
//	public static final int PROVIDER_ID_BLUETOOTH = 2;
//	public static final int PROVIDER_ID_PRE_VOLUME = 3;
//	static final UriMatcher sMatcher;
//	static{
//		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//		sMatcher.addURI("com.device.state.provider", "storage", PROVIDER_ID_STORAGE);
//		sMatcher.addURI("com.device.state.provider", "bluetooth", PROVIDER_ID_BLUETOOTH);
//		sMatcher.addURI("com.device.state.provider", "preVolume", PROVIDER_ID_PRE_VOLUME);
//	}
//	
	private DBOpenHelper dbOpenHelper = null;  
	@Override
	public boolean onCreate() {
		TopLog.e("Launcher"," onCreate() DeviceStateContentProvider");
		dbOpenHelper = new DBOpenHelper(this.getContext(), ContentData.DATABASE_NAME, ContentData.DATABASE_VERSION);  
		return true;
	}
	

	@Override
	public String getType(Uri uri) {
		switch (ContentData.DeviceStateTableData.uriMatcher.match(uri)) {
			case ContentData.DeviceStateTableData.PROVIDER_ID_BLUETOOTH:{
				
			}
			break;
			case ContentData.DeviceStateTableData.PROVIDER_ID_STORAGE:{
				
			}
			break;
			case ContentData.DeviceStateTableData.PROVIDER_ID_PRE_VOLUME:{
				return ContentData.DeviceStateTableData.CONTENT_TYPE_ITME;
			}
			default:
				break;
			}
		return null;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
	
		switch (ContentData.DeviceStateTableData.uriMatcher.match(uri)) {
		case ContentData.DeviceStateTableData.PROVIDER_ID_PRE_VOLUME:{
			if(values == null){
				values = new ContentValues();
				values.put(ContentData.DeviceStateTableData._ID, 1);
				values.put(ContentData.DeviceStateTableData.STORAGE, 0);
				values.put(ContentData.DeviceStateTableData.BLUETOOTH, 0);
				values.put(ContentData.DeviceStateTableData.HAVE_VOLUME, Context.DEFUALT_VOLUME);
				values.put(ContentData.DeviceStateTableData.CURRENT_VOLUME, Context.DEFUALT_VOLUME);
				values.put(ContentData.DeviceStateTableData.PRE_VOLUME, Context.DEFUALT_VOLUME);
			}else{
				values.put(ContentData.DeviceStateTableData._ID, 1);
				if(!values.containsKey(ContentData.DeviceStateTableData.STORAGE)){
					values.put(ContentData.DeviceStateTableData.STORAGE, 0);			
				}
				if(!values.containsKey(ContentData.DeviceStateTableData.BLUETOOTH)){
					values.put(ContentData.DeviceStateTableData.BLUETOOTH, 0);
				}
				if(!values.containsKey(ContentData.DeviceStateTableData.HAVE_VOLUME)){
					values.put(ContentData.DeviceStateTableData.HAVE_VOLUME, Context.DEFUALT_VOLUME);
				}
				if(!values.containsKey(ContentData.DeviceStateTableData.CURRENT_VOLUME)){
					values.put(ContentData.DeviceStateTableData.CURRENT_VOLUME, Context.DEFUALT_VOLUME);
				}
				if(!values.containsKey(ContentData.DeviceStateTableData.PRE_VOLUME)){
					values.put(ContentData.DeviceStateTableData.PRE_VOLUME, Context.DEFUALT_VOLUME);
				}
			}
			
			TopLog.e("Launcher","query :" + querySql);
			Cursor cursor = db.rawQuery(querySql, null);
			if(cursor != null && cursor.getCount() > 0){
				update(uri, values, null, null);
			}else{
				TopLog.e("Launcher","insert :" + ContentData.DeviceStateTableData.TABLE_NAME +" "+ values.toString());
				long id = db.insert(ContentData.DeviceStateTableData.TABLE_NAME, null, values);
			}
			return ContentUris.withAppendedId(uri, 1);  
		}
		default:
			break;
		}
		return null;
	}
	String querySql = " select " +  " * " +
			 " from " + ContentData.DEVICE_STATE_TABLE_NAME + " " +
			 " where " + ContentData.DeviceStateTableData._ID + " = '1' ";
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		switch (ContentData.DeviceStateTableData.uriMatcher.match(uri)) {
		case ContentData.DeviceStateTableData.PROVIDER_ID_PRE_VOLUME:{
			TopLog.e("Launcher","query :" + querySql);
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(querySql, selectionArgs);
			return cursor;  
		}
		default:
			break;
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int count = 0;
		switch (ContentData.DeviceStateTableData.uriMatcher.match(uri)) {    
        case ContentData.DeviceStateTableData.PROVIDER_ID_PRE_VOLUME:    
            // 下面的方法用于从URI中解析出id，对这样的路径content://com.ljq.provider.personprovider/person/10    
            // 进行解析，返回值为10    
            /*long personid = ContentUris.parseId(uri);    
            String where = "_ID=" + personid;// 获取指定id的记录    
            where += !TextUtils.isEmpty(selection) ? " and (" + selection + ")" : "";// 把其它条件附加上    
            count = db.update("teacher", values, where, selectionArgs);    */
        	db.beginTransaction();
        	String sql = " update " + ContentData.DEVICE_STATE_TABLE_NAME + " "+
        				" set " + ContentData.DeviceStateTableData.PRE_VOLUME + " = ? " +
        				" , " + ContentData.DeviceStateTableData.CURRENT_VOLUME + " = ? " +
        				" , " + ContentData.DeviceStateTableData.HAVE_VOLUME + " = ? " +
        			    " where " + ContentData.DeviceStateTableData._ID + " = '1' ";
        	TopLog.e("Launcher", sql + "  |  values:" + values);
        	Object[] objects = {0,0,0};
        	if(values != null){
        		if(values.containsKey(ContentData.DeviceStateTableData.PRE_VOLUME)){
        			objects[0] = values.get(ContentData.DeviceStateTableData.PRE_VOLUME);
        		}
        		if(values.containsKey(ContentData.DeviceStateTableData.CURRENT_VOLUME)){
        			objects[1] = values.get(ContentData.DeviceStateTableData.CURRENT_VOLUME);
        		}
        		if(values.containsKey(ContentData.DeviceStateTableData.HAVE_VOLUME)){
        			objects[2] = values.get(ContentData.DeviceStateTableData.HAVE_VOLUME);
        		}
        	}
        	db.execSQL(sql, objects);
        	count = 1;
        	db.setTransactionSuccessful();
            break;    
        default:    
            throw new IllegalArgumentException("Unknown URI " + uri);    
        }
		db.endTransaction();
        db.close();    
        return count;   
	}

}
