package com.autochips.bluetooth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


public class DBManager {

	private final int MOBILE_FIELD_MOBILE		= 0;
	private final int MOBILE_FIELD_PROVINCE	= 1;
	private final int MOBILE_FIELD_CITY		= 2;
	private final int MOBILE_FIELD_PROVIDER	= 3;
	private final int TELZONE_FIELD_PROVINCE	= 0;
	private final int TELZONE_FIELD_CITY		= 1;
	private final int TELZONE_FIELD_TELZONE		= 2;

    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "tel.db"; //保存的数据库文件名
    public static final String PACKAGE_NAME = "com.autochips.bluetooth";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;  //在手机里存放数据库的位置(/data/data/com.cssystem.activity/cssystem.db)
    
    
    private SQLiteDatabase database;
    private Context context;
    SQLiteDatabase m_db;

    public DBManager(Context context) {
        this.context = context;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public void openDatabase() {
    	System.out.println(DB_PATH + "/" + DB_NAME);
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    private SQLiteDatabase openDatabase(String dbfile) {

        try {
            if (!(new File(dbfile).exists())) {
            	//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(
                        R.raw.tel); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
            m_db = db;
            return db;

        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }
    public boolean isCallnum(String num){
		Pattern p = Pattern.compile("[0-9\\+]+"); 
		Matcher m = p.matcher(num);
		return m.matches();
    }
    String GetTelZone(String tel)
    {
    	String result = GetSpecialTel(tel);
    	if (!result.isEmpty())	// 匹配到数据了
    	{
    		return result;
    	}

    	if (tel.length() >= 6 && tel.charAt(0) == '1')	// 如果是手机，并匹配到结果直接返回
    	{
    		Pattern p = Pattern.compile("[0-9]*"); 
    		Matcher m = p.matcher(tel);
    		if (!m.matches()) {
				return result;
			}
			String szSQL = new String();
			int value1 = Integer.parseInt(tel.substring(0, 6));
			int value2 = value1;
			if (tel.length() > 6) {
				value2 = Integer.parseInt(tel.substring(0, 7));
			}
			szSQL = String.format("select * from mobile where mobile = %d or mobile = %d ;", value1, value2);
			Cursor cursor = m_db.rawQuery(szSQL, null);
			if (cursor.moveToNext()) {
				result = _get_result(cursor.getString(MOBILE_FIELD_PROVINCE), cursor.getString(MOBILE_FIELD_CITY), cursor.getInt(MOBILE_FIELD_PROVIDER));
				cursor.close();
			}
	    	if (!result.isEmpty())
	    	{
	    		return result;
	    	}
    	}

    	String str = tel;
    	// 检查区号
    	int nIndex = str.indexOf("0");
    	if (nIndex == 0) {
			str = str.substring(1, str.length());
		}
    	nIndex = str.indexOf("+");
    	if (nIndex == 0) {
			str = str.substring(1, str.length());
		}
    	if (str.length() >= 2)
    	{
    		Pattern p = Pattern.compile("[0-9]*"); 
    		Matcher m = p.matcher(str);
    		if (!m.matches()) {
				return result;
			}
			String szSQL = new String();
			int value1 = Integer.parseInt(str.substring(0, 2));
			int value2 = 0;
			if (str.length() == 2) {
				value2 = value1;
			}else{
				value2 = Integer.parseInt(str.substring(0, 3));	
			}
			szSQL = String.format("select * from telzone where telzone = %s or telzone = %s ;", value1, value2);
			Cursor cursor = m_db.rawQuery(szSQL, null);
			if (cursor.moveToNext()) {
				result = _get_result(cursor.getString(TELZONE_FIELD_PROVINCE), cursor.getString(TELZONE_FIELD_CITY), 0);
				cursor.close();
			}
	    	if (!result.isEmpty())
	    	{
	    		return result;
	    	}
    	}

    	return result;
    }
    String _get_result(String province, String city, int provider)
    {
    	String result = null;
    	if (!province.isEmpty() && !city.isEmpty())
    	{
    		result = province;
    		if (!province.equals(city)) {
				result = result + city;
			}

    		if (provider == 1)
    		{
    			result = result + "移动";
    		}
    		else if (provider == 2)
    		{
    			result = result + "联通";
    		}
    		else if (provider == 3)
    		{
    			result = result + "电信";
    		}
    	}
    	return result;
    }
    
    private String GetSpecialTel(String tel)
    {
    	class SPECIAL_TEL 
    	{
    		String tel;
    		String desc;
    		public SPECIAL_TEL(String tel, String desc) {
    			this.tel = tel;
    			this.desc = desc;
			}
    	};

    	SPECIAL_TEL pst[] = {
        		new SPECIAL_TEL("110", "公安报警"),
        		new SPECIAL_TEL("119", "火警"),
        		new SPECIAL_TEL("114", "查号台"),
        		new SPECIAL_TEL("160", "160声讯台"),
        		new SPECIAL_TEL("10000", "中国电信客服"),
        		new SPECIAL_TEL("10086", "中国移动客服"),
        		new SPECIAL_TEL("10010", "联通客服热线"),
        		new SPECIAL_TEL("122", "交通事故"),
        		new SPECIAL_TEL("120", "急救中心"),
        		new SPECIAL_TEL("121", "天气预报"),
        		new SPECIAL_TEL("112", "电话故障"),
        		new SPECIAL_TEL("4008005005", "翼卡在线"),
        		new SPECIAL_TEL("4001050868", "翼卡在线"),
        		new SPECIAL_TEL("075787807155", "翼卡在线"),
        		new SPECIAL_TEL("075788303000", "翼卡在线"),
        		new SPECIAL_TEL("075536860630", "翼卡在线"),
    	};

    	if (!tel.isEmpty())
    	{
    		for (int i = 0; i < pst.length; i++)
    		{
    			if (pst[i].tel.equals(tel)) {
					return pst[i].desc;
				}
    		}
    	}

    	return "";
    }
    public void closeDatabase() {
        this.database.close();

    }
}