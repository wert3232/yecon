package com.yecon.carsetting.DateBase;
import java.util.ArrayList;
import java.util.List;
import com.yecon.carsetting.bean.AppInfo;
import com.yecon.carsetting.unitl.L;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 要注意数据库的开关问题
 * */

public class MapListDB {
	public static final String MSG_DBNAME = "CarSetting.db";
	private static final String RECENT_TABLE_NAME = "mapList";
	//private SharePreferenceUtil SpUtil;
	//private User user = new User();
	private SQLiteDatabase db;
	/**
	 * 增加 年 月 日  统计
	 * 他们分别作一个字段 
	 * 
	 * */
	public MapListDB(Context context) {
		db = context.openOrCreateDatabase(MSG_DBNAME, Context.MODE_PRIVATE,
				null);
		db.execSQL("CREATE table IF NOT EXISTS "
				+ RECENT_TABLE_NAME
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"mapName TEXT, packageName TEXT,className TEXT,"+
				"num_1 INTEGER,num_2 INTEGER,str_3 " +
				"TEXT,tag INTEGER)");
	}

	/**
	 * when touch the checkbox true,will insert the values
	 * */
	public void insertMapInfo(AppInfo item){
		//提高插入速度
		L.v(item.getAppName()+"&&&insertMapInfo&&"+item.getPackageName()+"&&&"+item.getClassName());
		if(isExistApp(item.packageName))return;
		db.execSQL(
				"insert into "
						+ RECENT_TABLE_NAME
						+ " (mapName,packageName,className) " +
						"values(?,?,? )",
						new Object[] {item.getAppName(), item.getPackageName(),item.getClassName()});	

 
	}

	/**
	 * get all map info
	 * */
	public List<AppInfo>selectAppInfo(){
		List<AppInfo> mAppList = new ArrayList<AppInfo>();
		Cursor c = db.rawQuery("select mapName,packageName,className from "+ RECENT_TABLE_NAME,null);
		while (c.moveToNext()) {
			AppInfo item =new AppInfo();
			String mapName = c.getString(0);
			String packageName = c.getString(1);
			String className =c.getString(2);

			//L.v(mapName+"&&"+packageName+"&&"+className );
			item.setAppName(mapName);
			item.setClassName(className);
			item.setPackageName(packageName);

			mAppList.add(item);
		}
		return mAppList;

	}

	public void delMap_Appinfo(AppInfo item) {
		L.v(item.getAppName()+"&&&delMapList&&"+item.getPackageName()+"&&&"+item.getClassName());
		db.delete(RECENT_TABLE_NAME, "packageName=?", new String[] { item.getPackageName() });
	}

	/**
	 * if the have package name
	 * */
	public void delMap_Package(String packageName) {
		db.delete(RECENT_TABLE_NAME, "packageName=?", new String[] {packageName});
	}

	@SuppressWarnings("unused")
	private String getClassName(String packageName) {
		if(!db.isOpen())return "";
		String className = null ;
		Cursor c = db.rawQuery("SELECT className FROM " + RECENT_TABLE_NAME
				+ " WHERE packageName = ?", new String[] { packageName });
		while (c.moveToNext()) {
			className = c.getString(0);
		}
		return className;
	}


	public String getAppName(String packageName) {
		if(!db.isOpen())return "";
		String className = null ;
		Cursor c = db.rawQuery("SELECT mapName FROM " + RECENT_TABLE_NAME
				+ " WHERE packageName = ?", new String[] { packageName });
		while (c.moveToNext()) {
			className = c.getString(0);
		}
		return className;
	}
	public boolean isExistApp(String packageName) {
		if(!db.isOpen())return false;
		Cursor c = db.rawQuery("SELECT packageName FROM " + RECENT_TABLE_NAME
				+ " WHERE packageName = ?", new String[] { packageName });
		return c.moveToFirst();

	}


	public void close() {
		if (db != null)
			db.close();
	}
}
