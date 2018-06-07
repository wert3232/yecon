 package com.yecon.mediaservice;
 
 import android.annotation.SuppressLint;
 import android.content.AsyncQueryHandler;
 import android.content.ContentResolver;
 import android.database.Cursor;
 import android.net.Uri;
 
 public class MediaListQueryHandler
 {
   private IMediaListQueryHandler mHandlerInterface;
   private ContentResolver mCR;
   private AsyncQuery mAsyncQuery;
 
   public MediaListQueryHandler(ContentResolver cr, IMediaListQueryHandler HandlerInterface)
   {
     this.mCR = cr;
     this.mHandlerInterface = HandlerInterface;
     this.mAsyncQuery = new AsyncQuery(cr);
   }
 
   public void startQuery(Boolean bAsync, int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy)
   {
     if (bAsync.booleanValue()){
       this.mAsyncQuery.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
     }else{
       try {
         Cursor cursor = this.mCR.query(uri, projection, selection, selectionArgs, orderBy);
         this.mHandlerInterface.onQueryComplete(token, cookie, cursor);
       } catch (Exception e) {
         e.printStackTrace();
       }
   }
 }
   public Cursor startQuery(Uri uri, String[] columns, String selection, String[] selectionArgs, String orderBy)
   {
     Cursor c = null;
     try {
       c = this.mCR.query(uri, columns, selection, selectionArgs, orderBy);
     } catch (Exception e) {
       e.printStackTrace();
     }
     return c;
   }
 
   @SuppressLint({"HandlerLeak"})
   private class AsyncQuery extends AsyncQueryHandler
   {
     public AsyncQuery(ContentResolver cr) {
       super(cr);
     }
 
     protected void onQueryComplete(int token, Object cookie, Cursor cursor)
     {
       super.onQueryComplete(token, cookie, cursor);
       try {
         MediaListQueryHandler.this.mHandlerInterface.onQueryComplete(token, cookie, cursor);
         if (cursor != null){
           cursor.close();
       }
		}
       catch (Exception e){
         e.printStackTrace();
       }
     }
   }
 }

