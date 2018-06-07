package com.tuoxianui.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 这个类继承SQLiteOpenHelper抽象类，用于创建数据库和表。创建数据库是调用它的父类构造方法创建。
 * @author HB
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	// 在SQLiteOepnHelper的子类当中，必须有该构造函数，用来创建一个数据库；
	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		// 必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
	}

	// public DBOpenHelper(Context context, String name) {
	// this(context, name, VERSION);
	// }

	public DBOpenHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}
	
	/**
	 * 只有当数据库执行创建 的时候，才会执行这个方法。如果更改表名，也不会创建，只有当创建数据库的时候，才会创建改表名之后 的数据表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("Launcher","db onCreate()");
		String sql = "create table " + ContentData.DeviceStateTableData.TABLE_NAME
				+ "(" + ContentData.DeviceStateTableData._ID
				+ " INTEGER PRIMARY KEY, " // autoincrement
				+ ContentData.DeviceStateTableData.STORAGE + " integer, "
				+ ContentData.DeviceStateTableData.BLUETOOTH + " integer,"
				+ ContentData.DeviceStateTableData.HAVE_VOLUME + " integer,"
				+ ContentData.DeviceStateTableData.CURRENT_VOLUME + " integer,"
				+ ContentData.DeviceStateTableData.PRE_VOLUME + " integer)" + ";";
		db.execSQL(sql); 
		Log.e("Launcher",sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS" + ContentData.DeviceStateTableData.TABLE_NAME);   
        onCreate(db);  
	}

}


