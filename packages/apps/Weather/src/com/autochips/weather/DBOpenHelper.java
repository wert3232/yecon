package com.autochips.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public static final String DBNAME = "locList.db";
	private static final int VERSION = 1;

	public DBOpenHelper(Context context, String dbname) {

		super(context, dbname, null, VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS country_region (_id integer primary key autoincrement,country_name text,country_name_en text,country_code text,state_name text,state_name_en text,state_code text,city_name text,city_name_en text,city_code text,pinyin text,state_pinyin text,city_pinyin text,woeid text)");
		db.execSQL("CREATE TABLE IF NOT EXISTS current_city (_id integer primary key autoincrement,country_name text,country_name_en text,country_code text,state_name text,state_name_en text,state_code text,city_name text,city_name_en text,city_code text,woeid text)");
		db.execSQL("CREATE TABLE IF NOT EXISTS weather_cache (_id integer primary key autoincrement, firstday_condition text,firstday_high text,firstday_low text,firstday_code text, secday_condition text,secday_high text,secday_low text,secday_code text,current_temp text,current_condition text,current_code text,update_time text,source text,woeid text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
