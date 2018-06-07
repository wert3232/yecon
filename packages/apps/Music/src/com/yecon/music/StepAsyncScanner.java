package com.yecon.music;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

public class StepAsyncScanner {
	private static int SCAN_STEP_COUNT = 50;
    private static final int SCAN_STEP_TIME = 2000;
    private final String TAG="StepAsyncScanner";
    private Context context;
	private Handler mHandler;
	private OnResult onResult;
	 private final int QUERY_TOKEN = 1;;
	
	StepAsyncScanner(Context context, OnResult onResult){
		this.context = context;
		this.onResult = onResult;
		mHandler = new Handler(context.getMainLooper());
	}
	StepAsyncScanner(Context context, int stepCount, OnResult onResult){
		this.context = context;
		this.onResult = onResult;
		mHandler = new Handler(context.getMainLooper());
		SCAN_STEP_COUNT = stepCount;
	}
	
	public interface OnResult{
		void onResult(Cursor cursor, STATUS status);
	}
	
	public enum STATUS{
		SCANSTARTED,
		SCANPREPARING,
		SCANFIRST,
		SCANNING,
		SCANEND		
	};
	
	public class QueryArgs {
	        public Uri uri;
	        public String[] projection;
	        public String selection;
	        public String[] selectionArgs;
	        public String orderBy;
	        public int count;
	    }
    private TrackQueryHandler trackQueryHandler;
    private Runnable rnUpdateList=null;
    public class TrackQueryHandler extends AsyncQueryHandler {
      
		TrackQueryHandler(ContentResolver res) {
            super(res);
        }
        
		public void cancel(){
			mHandler.removeCallbacksAndMessages(null);
		}
		
        @Override
		public void startQuery(int token, Object cookie, Uri uri,
				String[] projection, String selection, String[] selectionArgs,
				String orderBy) {
			// TODO Auto-generated method stub
        	if(rnUpdateList!=null){
        		mHandler.removeCallbacks(rnUpdateList);
        		rnUpdateList = null;
        	}
			super.startQuery(token, cookie, uri, projection, selection, selectionArgs,
					orderBy);
		}

		@Override
        protected void onQueryComplete(final int token, Object cookie, Cursor cursor) {        	
            int count = 0;
            QueryArgs  args = (QueryArgs) cookie;
            Cursor scanCursor = context.getContentResolver().query(MediaStore.getMediaScannerUri(),
                    null, null, null, null);
            if(cursor!=null){
            	Log.d(TAG, "Got list count: " + cursor.getCount() + "totalCounts: "+args.count);     
            	
            	if(cursor.getCount() < SCAN_STEP_COUNT){            		
                    if (scanCursor != null) {
                        Log.d(TAG, "It's scanning,wait more data, nower count:" + cursor.getCount());
                        if(onResult!=null){
	    	        		onResult.onResult(null, STATUS.SCANPREPARING);
	    	        	}
                    } 
            	}
            	
            	if(null == scanCursor || (cursor!=null && cursor.getCount() >= args.count + SCAN_STEP_COUNT)){            	
            		Log.d(TAG, "scanning: " + cursor.getCount());
	            	if(onResult!=null){
	            		if(0==args.count){
	            			onResult.onResult(cursor, STATUS.SCANFIRST);
	            		}
	            		else{
	            			onResult.onResult(cursor, STATUS.SCANNING);
	            		}    	        		
    	        	}    	        	
	            	args.count =  cursor.getCount();
            	}
            	cursor.close();
            }
            
            if(scanCursor!=null){
        		Log.d(TAG, "It's scanning. update list later");
        		scanCursor.close();
        		final QueryArgs  arg = (QueryArgs) cookie;           
        		rnUpdateList = new Runnable(){
        	    	@Override
        			public void run() {
        				// TODO Auto-generated method stub
        				startQuery(token, arg, arg.uri, arg.projection, arg.selection,
        						arg.selectionArgs, arg.orderBy);
        			}
        	    };
        		mHandler.postDelayed(rnUpdateList, SCAN_STEP_TIME);
        	}
            else{
            	if(onResult!=null){
	        		onResult.onResult(null, STATUS.SCANEND);
	        	}
            }
        }
    }
    
    public void startQuery(Uri uri,
			String[] projection, String selection, String[] selectionArgs,
			String orderBy){
    	if(trackQueryHandler == null){
    		trackQueryHandler = new TrackQueryHandler(context.getContentResolver());
    	}
    	QueryArgs args = new QueryArgs();
        args.uri = uri;
        args.projection = projection;
        args.selection = selection;
        args.selectionArgs = null;
        args.orderBy = orderBy;
        args.count = 0;
        trackQueryHandler.cancel();
        trackQueryHandler.cancelOperation(QUERY_TOKEN);
    	trackQueryHandler.startQuery(QUERY_TOKEN, args, uri,
    			projection, selection, selectionArgs,
    			orderBy);
    }
    
    public void cancel(){
    	if(trackQueryHandler!=null){
    		trackQueryHandler.cancel();
    		trackQueryHandler.cancelOperation(QUERY_TOKEN);
    	}    	
    }

}
