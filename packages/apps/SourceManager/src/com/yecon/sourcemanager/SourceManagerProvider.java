package com.yecon.sourcemanager;

import com.yecon.common.SourceManager;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class SourceManagerProvider extends ContentProvider {
	private final String ROOT_URI="com.yecon.sourcemanager.provider";
	private final int SOURCES = 1;
	private UriMatcher  matcher;
	private String TAG="SourceManagerProvider";
	
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
		matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(ROOT_URI, "sources", SOURCES);		
		return false;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void connect2Service(){
		if(service == null){
			Log.i(TAG, "bind");
			synchronized(serviceConnection){
				Context ct = getContext();
				ct.bindService(new Intent(ct, SourceManagerService.class), serviceConnection, 0);
			}
		}
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		if(arg1.containsKey("password")){
			String key = arg1.getAsString("password");
			if(key!=null && key.equals("y9y8e1e3c5c6o8o6n4n")){
				switch(matcher.match(arg0)){
				case SOURCES:
					if(arg1.containsKey("bind")){
						connect2Service();
					}
					else if(arg1.containsKey("release")){
						connect2Service();
						Integer srcNo =  arg1.getAsInteger("release");
						Log.i(TAG, "release: "+ "srcNo: "+srcNo);				
						if(service!=null && srcNo>=0 && srcNo<SourceManager.SRC_NO.max){
							try {
								service.release(srcNo);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else if(arg1.containsKey("request")){				
						connect2Service();
						Integer srcNo =  arg1.getAsInteger("request");
						Log.i(TAG, "request: "+ "srcNo: "+srcNo);				
						if(service!=null && srcNo>=0 && srcNo<SourceManager.SRC_NO.max){
							try {
								service.request(srcNo);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}			
					else if(arg1.containsKey("hotplug")){
						connect2Service();
						String devPath =  arg1.getAsString("hotplug");
						Integer srcNo =  arg1.getAsInteger("srcNo");
						boolean insert =  arg1.getAsBoolean("insert");
						Log.i(TAG, "hotplug: " + devPath + " srcNo: "+srcNo + "insert: "+insert);
						if(service!=null && srcNo>=0 && srcNo< SourceManager.SRC_NO.max){
							try {
								service.hotplug(srcNo, devPath, insert, true);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}	
					else if(arg1.containsKey("cancel_mediakey")){
						connect2Service();
						Integer srcNo =  arg1.getAsInteger("cancel_mediakey");
						Log.i(TAG, "cancel_mediakey: "+ "srcNo: "+srcNo);				
						if(service!=null && srcNo>=0 && srcNo<SourceManager.SRC_NO.max){
							try {
								service.releaseMediaKey(srcNo);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else if(arg1.containsKey("updatePlayingPath")){
						connect2Service();
						Integer srcNo =  arg1.getAsInteger("updatePlayingPath");
						String value = arg1.getAsString("value");
						Log.i(TAG, "updatePlayingPath: "+ "srcNo: "+srcNo);				
						if(service!=null && srcNo>=0 && srcNo<SourceManager.SRC_NO.max && value!=null){
							try {
								service.updatePlayingPath(srcNo, value);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					break;
				}
			}
		}		
		return 0;
	}
	
	private ISourceManagerService service;
	 private final ServiceConnection serviceConnection = new ServiceConnection() {
         public void onServiceConnected(ComponentName className, android.os.IBinder binder) {
            synchronized (this) {
            	Log.i(TAG, "onServiceConnected");
            	service = ISourceManagerService.Stub.asInterface(binder);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            synchronized (this) {
            	Log.i(TAG, "onServiceDisconnected");
            	service = null;
            }
        }
    };
}
