package com.yecon.videoplayer2;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class PlayListData {
	private static final int version = 1;
	private final int MAX_ITEM = 1000;
	public static final String TABLE_PLAYING = "playing";
	public static final String TABLE_PLAYING_LIST = "playing_list";
	public static final String TABLE_PLAYED_LIST = "played_list";
	
	public class ListItem{
		private long id =0;
		//private long mediaId;
		private String path = "";
		private String name = "";
		private int seekPos = 0;
		private int duration = 0;
		private STATUS status = STATUS.stoped;
		
		public STATUS getStatus() {
			return status;
		}
		public void setStatus(STATUS status) {
			this.status = status;
		}
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
//		public long getMediaId() {
//			return mediaId;
//		}
//		public void setMediaId(long mediaId) {
//			this.mediaId = mediaId;
//		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getSeekPos() {
			return seekPos;
		}
		public void setSeekPos(int seekPos) {
			this.seekPos = seekPos;
		}
		public int getDuration() {
			return duration;
		}
		public void setDuration(int duration) {
			this.duration = duration;
		}
		
	};
	
	public enum PLAYING_INDEX{
		;
		public static final int pos = 0, //the _ID of TABLE_PLAYING_LIST
		path = 1,
		fieldName = 2, //String: artist name or album name or folder path
		listType = 3, // enum LISTTYPE
		status = 4; //enum STATUS
	};
	public enum PLAYING_LIST_INDEX{
		;
		public static final int _id = 0,
		path = 1,
		name = 2,
		seekPos = 3,
		duration = 4,
		flag = 5;
	};
	public enum PLAYED_LIST_INDEX{
		;
		public static final int _id = 0,
		path = 1,
		seekPos = 2,
		duration = 3,
		flag = 4;
	};
	
	public interface LoadDataCallback{
		void loadData(String path, String name, int duration);
	};
	public interface BindDataCallback{
		String getPath(int index);
		String getName(int index);
		int getSeekPos(int index);
		int getDuration(int index);
	};
	
	public static enum LISTTYPE {
		all(0),
		artist(1),
		album(2),
		folder(3);
        private final int value;
        LISTTYPE(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
	
	public static enum STATUS {
		stoped(0),
		paused(1),
		playing(2),
		error(3);
        private final int value;
		STATUS(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
	
	private DBOpenHelper dbOpenHelper;
	
	public PlayListData(Context context){
		this.dbOpenHelper = new DBOpenHelper(context);
	}
	
	public static class DBOpenHelper extends SQLiteOpenHelper {

    	public DBOpenHelper(Context context) {
    		super(context, "playlist.db", null, version);
    	}

    	@Override
    	public void onCreate(SQLiteDatabase db) {	    	
    		
    		db.execSQL("CREATE TABLE "+TABLE_PLAYING +
    				"(pos integer DEFAULT 0, path nvarchar(2048) unique,  fieldName nvarchar(1024),  listType integer DEFAULT 0, " +
    				" status integer DEFAULT 0) ");
    		
    		db.execSQL("CREATE TABLE "+TABLE_PLAYING_LIST+" (_id integer primary key autoincrement, " +
    				"path nvarchar(2048), name nvarchar(1024), " +
    				" seekPos integer DEFAULT 0,  duration integer DEFAULT 0, " +
    				" flag integer DEFAULT 0)");
    		
    		db.execSQL("CREATE TABLE "+TABLE_PLAYED_LIST+" (_id integer primary key autoincrement, " +
    				"path nvarchar(2048) unique , "+
    				" seekPos integer DEFAULT 0,  duration integer DEFAULT 0, " +
    				" flag integer DEFAULT 0)");

    	}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PLAYING);
    		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PLAYING_LIST);
    		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PLAYED_LIST);
    		onCreate(db);
    	}
    }
	
	public void clearPlayingList(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from " + TABLE_PLAYING_LIST);
		db.close();
	}	
	
	public void clearPlaying(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from " + TABLE_PLAYING);
		db.close();
	}	
	
	public void clearPlayedList(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from " + TABLE_PLAYED_LIST);
		db.close();
	}	
	
	public void add2PlayedList(SQLiteDatabase db, String path, int seekPos, int duration){
		boolean close = false;
		if(db==null){
			close = true;
			db = dbOpenHelper.getWritableDatabase();
		}		
		
		db.execSQL("delete from "+TABLE_PLAYED_LIST + " where path = ?", new String[]{path});

		db.execSQL("insert into "+TABLE_PLAYED_LIST+" (path, seekPos, duration) values(?,?,?)",
				new String[]{path, Integer.toString(seekPos), Integer.toString(duration)});
		
		db.execSQL("delete from "+TABLE_PLAYED_LIST+" where (select count(_id) from "+TABLE_PLAYED_LIST+ ")>" + MAX_ITEM +
				" and _id in (select _id from " +TABLE_PLAYED_LIST+ " order by _id desc limit (select count(_id) from "+TABLE_PLAYED_LIST+ ") offset " + MAX_ITEM + " )");
		
		if(close){
			db.close();
		}
	}
	
	public void removePlayListByDevPath(SQLiteDatabase db, String devPath){
		boolean close = false;
		if(db==null){
			close = true;
			db = dbOpenHelper.getWritableDatabase();
		}		
		db.execSQL("delete from "+TABLE_PLAYING_LIST + " where path like '" + devPath + "%%'");
		db.execSQL("delete from "+TABLE_PLAYED_LIST + " where path like '" + devPath + "%%'");
		if(close){
			db.close();
		}
	}
	
	public void removeFromPlayedList(SQLiteDatabase db, String path){
		boolean close = false;
		if(db==null){
			close = true;
			db = dbOpenHelper.getWritableDatabase();
		}		
		
		db.execSQL("delete from "+TABLE_PLAYED_LIST + " where path = ?", new String[]{path});

		if(close){
			db.close();
		}
	}
	
	private int getSeekPosFromPlayedList(SQLiteDatabase db, String path){
		int ret = 0;
		boolean close = false;
		if(db==null){
			close = true;
			db = dbOpenHelper.getReadableDatabase();
		}
		
		Cursor cs = db.rawQuery("select * from "+TABLE_PLAYED_LIST + " where path = ?", new String[]{path});
		if(cs!=null && cs.moveToFirst()){
			ret = cs.getInt(PLAYED_LIST_INDEX.seekPos);
		}
		
		if(close){
			db.close();
		}
		
		return ret;
	}
	
	public int getSeekPosFromPlayedList(String path){
		return getSeekPosFromPlayedList(null, path);
	}
	
	public void loadPlayingList(LoadDataCallback loadDataCallback){
		if(loadDataCallback == null){
			return;
		}
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cs = db.rawQuery("select * from "+TABLE_PLAYING_LIST, null);
		if(cs!=null){
			if(cs.moveToFirst()){
				do{
					loadDataCallback.loadData(cs.getString(PLAYING_LIST_INDEX.path), cs.getString(PLAYING_LIST_INDEX.name), cs.getInt(PLAYING_LIST_INDEX.duration));
				}
				while(cs.moveToNext());
			}			
			cs.close();
		}
		db.close();
	}
	
	private long  getPosByPath(SQLiteDatabase db, String path) {
		long ret = -1;
		boolean close = false;
		if(db==null){
			close = true;
			db = dbOpenHelper.getReadableDatabase();
		}
		
		Cursor cs = db.rawQuery("select rowid from "+TABLE_PLAYING_LIST  + " where path = ?", new String[]{path});
		if(cs!=null){
			if(cs.moveToFirst()){
				long i = 0;
				long curRowId = cs.getLong(0);
				cs.close();
				cs = db.rawQuery("select rowid from "+TABLE_PLAYING_LIST, null);
				if(cs!=null){
					if(cs.moveToFirst()){
						do{
							if(curRowId == cs.getLong(0)){
								ret = i;
								break;
							}
							i++;
						}while(cs.moveToNext());
					}
					cs.close();
				}
			}
		}
		
		if(close){
			db.close();
		}
		
		return ret;
	}
	
	public long  getPosById(SQLiteDatabase db, long id) {
		long ret = -1;
		boolean close = false;
		if(db==null){
			close = true;
			db = dbOpenHelper.getReadableDatabase();
		}
		
		Cursor cs = db.rawQuery("select rowid from "+TABLE_PLAYING_LIST  + " where _id = ?", new String[]{Long.toString(id)});
		if(cs!=null){
			if(cs.moveToFirst()){
				long i = 0;
				long curRowId = cs.getLong(0);
				cs.close();
				cs = db.rawQuery("select rowid from "+TABLE_PLAYING_LIST, null);
				if(cs!=null){
					if(cs.moveToFirst()){
						do{
							if(curRowId == cs.getLong(0)){
								ret = i;
								break;
							}
							i++;
						}while(cs.moveToNext());
					}
					cs.close();
				}
			}
		}
		
		if(close){
			db.close();
		}
		
		return ret;
	}
	
	private long  getIdByPath(SQLiteDatabase db, String path) {
		long ret = -1;
		Cursor cs = db.rawQuery("select _id from "+TABLE_PLAYING_LIST  + " where path = ?", new String[]{path});
		if(cs!=null){
			if(cs.moveToFirst()){
				ret = cs.getLong(0);
				cs.close();
			}
		}
		return ret;
	}
	
	public boolean savePlaying(String path, int seekPos, int duration, String fieldName,   LISTTYPE listType, STATUS status){
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getWritableDatabase();
			add2PlayedList(db, path, seekPos, duration);
			db.execSQL("delete from " + TABLE_PLAYING);
			long _id = getIdByPath(db, path);
			if(_id>=0){
				db.execSQL("insert into "+TABLE_PLAYING+" (pos, path, fieldName, listType, status) values(?,?,?,?,?)",
						new String[]{Long.toString(_id), path, fieldName, Integer.toString(listType.ordinal()), Integer.toString(status.ordinal())});
			}			
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		finally{
			db.close();
		}		
		return true;
	}
	
	public ListItem getPlaying(){
		SQLiteDatabase db = null; 
		ListItem listItem=null;
        try { 
            db = dbOpenHelper.getReadableDatabase();	
            listItem = getPlaying(db);
		}
        catch(Exception e){
        	e.printStackTrace();
        }
        finally{
        	if(db!=null){
        		db.close();
        	}
        }
        return listItem;
	}
	private ListItem getPlaying(SQLiteDatabase db){
		ListItem li = null;
		Cursor cs = db.rawQuery("select * from "+TABLE_PLAYING, null);
		if(cs!=null){
			if(cs.moveToFirst()){
				long _id = cs.getLong(PLAYING_INDEX.pos);
				STATUS status = STATUS.values()[cs.getInt(PLAYING_INDEX.status)];
				cs.close();
				if(_id>=0){
					cs = db.rawQuery("select * from "+TABLE_PLAYING_LIST + " where _id = ? ", new String[]{Long.toString(_id)} );
					if(cs!=null){
						if(cs.moveToFirst()){
							li = new ListItem();
							li.setId(cs.getLong(PLAYING_LIST_INDEX._id));
							li.setPath(cs.getString(PLAYING_LIST_INDEX.path));
							li.setName(cs.getString(PLAYING_LIST_INDEX.name));
							li.setSeekPos(cs.getInt(PLAYING_LIST_INDEX.seekPos));
							li.setDuration(cs.getInt(PLAYING_LIST_INDEX.duration));
							li.setStatus(status);
						}		
						cs.close();
					}			
				}		
			}				
		}
		return li;
	}
	
    public boolean savePlayList(List<ListItem> list) { 
        if (null == list || list.size() <= 0) { 
            return false; 
        } 
        SQLiteDatabase db = null; 
        try { 
            db = dbOpenHelper.getWritableDatabase(); 
            db.execSQL("delete from " + TABLE_PLAYING_LIST);
            String sql = "insert into "+TABLE_PLAYING_LIST+" (path, name, seekPos, duration) values(?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql); 
            db.beginTransaction(); 
            for (ListItem item : list) { 
                stat.bindString(1, item.getPath()); 
                stat.bindString(2, item.getName()); 
                stat.bindLong(3, item.getSeekPos()); 
                stat.bindLong(4, item.getDuration()); 
                long result = stat.executeInsert(); 
                if (result < 0) { 
                    return false; 
                } 
            } 
            db.setTransactionSuccessful(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        } finally { 
            try { 
                if (null != db) { 
                    db.endTransaction(); 
                    db.close(); 
                } 
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        } 
        return true; 
    } 
    
    public boolean savePlayList( int count, BindDataCallback bindDataCallback) { 
        if (count <= 0 || bindDataCallback==null) { 
            return false; 
        } 
        SQLiteDatabase db = null; 
        try { 
            db = dbOpenHelper.getWritableDatabase(); 
            db.execSQL("delete from " + TABLE_PLAYING_LIST);
            String sql = "insert into "+TABLE_PLAYING_LIST+" (path, name, seekPos, duration) values(?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql); 
            db.beginTransaction(); 
            int i = 0;
            for (;i<count;i++) { 
            	 stat.bindString(1, bindDataCallback.getPath(i)); 
                 stat.bindString(2, bindDataCallback.getName(i)); 
                 stat.bindLong(3, bindDataCallback.getSeekPos(i)); 
                 stat.bindLong(4, bindDataCallback.getDuration(i)); 
                long result = stat.executeInsert(); 
                if (result < 0) { 
                    return false; 
                } 
            } 
            db.setTransactionSuccessful(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        } finally { 
            try { 
                if (null != db) { 
                    db.endTransaction(); 
                    db.close(); 
                } 
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        } 
        return true; 
    }

	
}
