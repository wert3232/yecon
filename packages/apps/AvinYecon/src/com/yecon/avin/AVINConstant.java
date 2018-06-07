
package com.yecon.avin;

import android.mcu.McuExternalConstant;

public class AVINConstant {

	public static final String ACTION_AVIN_STATE_CHANGE_FORECAST = "android.avin.action.STATE_CHANGE_FORECAST";
	public static final String ACTION_VIDEO_STATE_CHANGE_FORECAST = "android.video.action.STATE_CHANGE_FORECAST";
	public static final String ACTION_MUSIC_STATE_CHANGE_FORECAST = "android.music.action.STATE_CHANGE_FORECAST";
	public static final String ACTION_DVD_STATE_CHANGE_FORECAST = "android.dvd.action.STATE_CHANGE_FORECAST";
	public static final String ACTION_BACKCAR_OFF = McuExternalConstant.MCU_ACTION_BACKCAR_OFF;
	public static final String ACTION_BACKCAR_ON = McuExternalConstant.MCU_ACTION_BACKCAR_ON;
	public static final String ACTION_AVIN_REQUEST_NOTIFY = "yecon.intent.action.AVIN.REQUEST";
	public static final String ACTION_AUX_PORT_SET = "action.aux_port_set";
	
	public static final String ACTION_RDS_ON = "autochips.intent.action.RDS_ON";
	public static final String ACTION_RDS_OFF = "autochips.intent.action.RDS_OFF";
	public static final String PROPERTY_KEY_AVIN_TYPE = "persist.sys.avintype";
	
	public final static String PERSYS_TV_TYPE = "persist.sys.tv_type";
	public final static String PERSYS_TV_AREA = "persist.sys.tv_area";
	public final static String default_tv_type = "ATV";
	public final static String support_tv_type[] = {"ATV","DVB"};
	public final static String default_tv_area = "China";
	
	public static final String ACTION_BLUETOOTH_CALL_STATUS_CHANGE =
            "com.autochips.bluetooth.PhoneCallActivity.action.BLUETOOTH_CALL_STATUS_CHANGE";
	
	public static final String ACTION_BLUETOOTH_CALL_STATUS =
            "com.autochips.bluetooth.PhoneCallActivity.action.BLUETOOTH_CALL_STATUS";
	
	/*
	 * 	ATVAREA_RUSSIA = 0,//俄罗斯
	ATVAREA_Europe=1,//欧洲
	ATVAREA_China,//中国
	ATVAREA_Italy,//意大利
	ATVAREA_EasternEurope,//东欧
	ATVAREA_Brazil,//巴西
	ATVAREA_South_Africa,//南非
	 */
	public final static String support_tv_area[] = {"USA","Europe","China","Italy","OIRT","New Zealand","South Africa"};
	public final static String atv_preference = "com.yecon.atv.preference";
	public final static String atv_preference_len = "pref.len";
	public final static String atv_preference_data = "pref.data";
	
	public final static int TIME = 10000;
	public final static int SIG_STATUS_TIME = 1000;
	public final static int SELECT_SOURCE_CODE = 100;
	public final static int HIDE_TOP_BAT_TIME = 5000;
	public final static int SET_AUX_DELAY_TIME = 1;
	public final static int SHOW_SURFACE_DELAY_TIME = 1;
	
	public final static int DVR_SCREEN_WIDTH = 720;
	public final static int DVR_SCREEN_HEIGHT = 480;

	
	
	public static int sScreenWidth = 0;
	public static int sScreenHeight = 0;
	
	public static boolean rdsTest = false;
	
	
	public final static int PROGRESS_CHANGED = 0;
	public final static int HIDE_CONTROLER = 1;
	public final static int DISMISS_ERROR_DIALOG = 2;
	public final static int SHOW_NO_SIGNAL_STATE = 3;
	// add for the case we new two Activity Client ,the will be conflict because
	// of cbm
	public final static int SHOW_SURFACEVIEW = 4;
	public final static int SET_INPUTSOURCE = 5;
	public final static int SHOW_TOP_BAR = 6;
	public final static int HIDE_TOP_BAR = 7;
	public final static int SHOW_BT_SURFACEVIEW = 8;
	public final static int SET_AUX_VIDEO_PARA = 9;
	public final static int CHECK_SIGNAL = 10;
	public final static int SEND_DVR_TIME = 11;
	
	
	public static final int    DVR_KEYCOD_MENU = 0x85;
	public static final int    DVR_KEYCODE_UP  = 0x81;
	public static final int    DVR_EKYCODE_DW  = 0x80;
	public static final int    DVR_KEYCDOE_MODE = 0x9c;
	public static final int    DVR_KEYCODE_OK   = 0x87;

}
