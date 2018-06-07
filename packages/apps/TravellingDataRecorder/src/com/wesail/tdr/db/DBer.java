package com.wesail.tdr.db;

import java.util.List;
import java.util.Map;

import com.wesail.tdr.L;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBer  extends SQLiteOpenHelper {
	public final static String OVER_TIME_DRIVER_ID = "driver_id";
	public final static String OVER_TIME_START_TIME = "start_time";
	public final static String OVER_TIME_END_TIME = "end_time";
	
	public final static String OVER_SPEED_TIME = "time";
	public final static String OVER_SPEED_overtime = "overtime";
	public final static String OVER_SPEED_AVERAGE_SPEED = "overspeed_average_speed";
	
	public final static String RECROD_ACCIDENT_EVENT = "event";
	public final static String RECROD_ACCIDENT_TRAVEL_END_TIME = "travel_end_time";
	
	public final static String RECROD_ACCIDENT_DETIL_PARKING_BEFORE_SECONDS = "parking_before_seconds";
	public final static String RECROD_ACCIDENT_DETIL_SPEED = "speed";
	public final static String RECROD_ACCIDENT_DETIL_BRAKE = "brake";
	public final static String RECROD_ACCIDENT_DETIL_TURN_LEFT = "turn_left";
	public final static String RECROD_ACCIDENT_DETIL_TURN_RIGHT = "turn_right";
	public final static String RECROD_ACCIDENT_DETIL_FAR_LIGHT = "far_light";
	public final static String RECROD_ACCIDENT_DETIL_DIPPED_LIGHT = "dipped_light";
	public final static String RECROD_ACCIDENT_DETIL_ALARM = "alarm";
	public final static String RECROD_ACCIDENT_DETIL_OPEN_THE_DOOR = "open_the_door";
	
	public final static String DRIVER_SPEED_DATE = "date";
	
	public final static String DRIVER_SPEED_DETIL_SECONDS = "seconds";
	public final static String DRIVER_SPEED_DETIL_SPEED = "speed";
	
	public final static String power_time_type = "time_type";
	public final static String power_time_of_occurrence = "time_of_occurrence";
	
	
	public DBer(Context context) {
		super(context, "tdr2", null, 1);
		L.e("DBer","init");
	}
	
	public DBer(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		L.e("DBer","onCreate");
		//超时记录
		db.execSQL("create table if not exists over_time("  
                + "id integer primary key autoincrement," 
				+ "driver_id varchar"		//驾驶员证件号
                + "start_time varchar,"  	//起始时间
                + "end_time varchar)");		//结束时间
		
		//超速记录
		db.execSQL("create table if not exists over_speed("  
                + "id integer primary key autoincrement,"
                + "time varchar,"  					//时间
                + "overtime integer,"  				//超速总时间
                + "overspeed_average_speed integer)");	//超速平均速度
		
		//事故疑点记录
		db.execSQL("create table if not exists recrod_accident("  
                + "id integer primary key autoincrement,"  
                + "event integer," //事件
                + "travel_end_time varchar)"); //行驶结束时间
		
		//事故疑点记录详情
		db.execSQL("create table if not exists recrod_accident_detil("  
                + "detil_id integer primary key autoincrement,"  
                + "parking_before_seconds varchar," //停车前/秒
                + "speed varchar,"  				//速度
                + "brake varchar,"  				//制动
                + "turn_left varchar,"				//左转
                + "turn_right varchar,"  			//右转
                + "far_light varchar,"  			//远光
                + "dipped_light varchar,"			//近光
                + "alarm varchar," 					//报警
                + "open_the_door varchar)");		//开门
		
		//行驶速度
		db.execSQL("create table if not exists driver_speed("  
                + "id integer primary key autoincrement,"  
                + "date varchar)"); //日期
		//行驶速度详情
		db.execSQL("create table if not exists driver_speed_detil("   
                + "detil_id integer primary key autoincrement,"
                + "seconds varchar," //秒钟
                + "speed varchar)"); //速度
		
		//上/掉电记录
		db.execSQL("create table if not exists power("  
                + "id integer primary key autoincrement,"  
                + "time_type varchar,"  	//时间类型
                + "time_of_occurrence varchar)");	//发生时间
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		L.e("DBer","Upgrading database from version " + oldVersion+
				"to " + newVersion + ".");
	}
	
	public void clearTable(String tableName){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("truncate table ?", new Object[]{tableName});
	}
	
	public long insert(String tableName,ContentValues contentValues){
		SQLiteDatabase db = this.getWritableDatabase();
		long l = db.insert(tableName, null, contentValues);
		return l; 
	}
	public void insert(String tableName,List<ContentValues> contentValuesList){
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		try {
			for(ContentValues contentValues : contentValuesList){		
				long l = db.insert(tableName, null, contentValues);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			L.e(e.toString());
		}finally{	
			db.endTransaction();
		}
	}
	
	public void select(String tableName){
		SQLiteDatabase db = this.getWritableDatabase();
		if(db.isOpen()){		
			Cursor cursor = db.rawQuery("select * from " + tableName,new String[]{});
			if(cursor != null && cursor.getCount() > 0){
			     while(cursor.moveToNext()){
			    	 L.e("select",tableName + ":" + cursor.getInt(0) + "  " + cursor.getString(1));
			     }	     
			     cursor.close();
			 }
		}
	}
}
