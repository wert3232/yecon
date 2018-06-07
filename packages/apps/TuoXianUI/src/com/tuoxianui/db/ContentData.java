package com.tuoxianui.db;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 提供ContentProvider对外的各种常量，当外部数据需要访问的时候，就可以参考这些常量操作数据。
 * @author HB
 *
 */
public class ContentData {
	public static final String AUTHORITY = "com.device.provider.state";
	public static final String DATABASE_NAME = "device_state.db";
	//创建 数据库的时候，都必须加上版本信息；并且必须大于4
	public static final int DATABASE_VERSION = 4;
	public static final String DEVICE_STATE_TABLE_NAME = "device_state";
	
	public static final class DeviceStateTableData implements BaseColumns {
		public static final String TABLE_NAME = DEVICE_STATE_TABLE_NAME;
		//Uri，外部程序需要访问就是通过这个Uri访问的，这个Uri必须的唯一的。
		public static final Uri CONTENT_URI_PRE_VOLUME = Uri.parse("content://"+ AUTHORITY + "/pre_volume");
		// 数据集的MIME类型字符串则应该以vnd.android.cursor.dir/开头  
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/hb.android." + DEVICE_STATE_TABLE_NAME;
		// 单一数据的MIME类型字符串应该以vnd.android.cursor.item/开头  
		public static final String CONTENT_TYPE_ITME = "vnd.android.cursor.item/hb.android." + DEVICE_STATE_TABLE_NAME;
		/* 自定义匹配码 */  
	    public static final int PROVIDER_ID_STORAGE = 1;
	    /* 自定义匹配码 */  
		public static final int PROVIDER_ID_BLUETOOTH = 2;
		/* 自定义匹配码 */  
		public static final int PROVIDER_ID_PRE_VOLUME = 3;
		/* 自定义匹配码 */  
		public static final int PROVIDER_ID_VOLUME_BALANCE = 4;
		
		public static final String STORAGE = "storage";
		public static final String BLUETOOTH = "bluetooth";
		public static final String PRE_VOLUME = "pre_volume";
		public static final String CURRENT_VOLUME = "current_volume";
		public static final String HAVE_VOLUME = "have_volume";
		public static final String DEFAULT_SORT_ORDER = "_id desc";
		
		public static final UriMatcher uriMatcher;  
	    static {  
	        // 常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码  
	        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);  
	        // 如果match()方法匹配content://hb.android.provider/storage路径,返回匹配码为PROVIDER_ID_STORAGE  
	        uriMatcher.addURI(ContentData.AUTHORITY, "storage", PROVIDER_ID_STORAGE);  
	        uriMatcher.addURI(ContentData.AUTHORITY, "bluetooth", PROVIDER_ID_BLUETOOTH);
	        uriMatcher.addURI(ContentData.AUTHORITY, "pre_volume", PROVIDER_ID_PRE_VOLUME);
	    }
	}
}