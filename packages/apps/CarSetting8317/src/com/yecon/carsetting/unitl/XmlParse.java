package com.yecon.carsetting.unitl;

import java.util.ArrayList;
import java.util.Vector;

public class XmlParse {
	public static int system_reboot_flag = 0xFFFFFFFF;

	public static String default_language = "zh_CN";
	public static String support_language = "zh_CN,zh_TW,en_US";
	public static String default_timezone = "Asia/Shanghai";
	// bright,contrast,chroma,saturation
	public static int audio[] = { 0, 0, 0, 0, 15, 0, 0 };
	public static int volume[] = { 11, 11, 11, 11, 7 };
	public static int backlight[] = { 80, 30, 50, 80 };
	public static int rgb_video[][] = { { 40, 25, 50, 50 }, { 115, 115, 32, 247 },
			{ 115, 115, 32, 247 }, { 115, 115, 32, 247 }, { 115, 115, 32, 247 },
			{ 115, 115, 32, 247 }, { 56, 43, 49, 49 }, { 115, 115, 32, 247 }, { 102, 64, 32, 128 },
			{ 115, 115, 32, 247 }, { 102, 64, 32, 128 }, { 115, 115, 32, 247 },
			{ 115, 115, 32, 247 } };

	public static int yuv_gain[][] = { { 201, 121, 231 }, { 201, 121, 231 }, { 201, 121, 231 },
			{ 201, 121, 231 }, { 201, 121, 231 }, { 201, 121, 231 }, { 201, 121, 231 },
			{ 128, 91, 128 }, { 201, 121, 231 }, { 128, 91, 128 }, { 201, 121, 231 },
			{ 201, 121, 231 } };

	public static String default_radio_type = "6851";
	public static String support_radio_type = "4702,4730,4755,4754,6621,6851,6856";

	public static int radio_area_enable[] = { 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0 };
	public static String default_radio_area = "China";
	public static String support_radio_area = "EUROPE,USA,OIRT,JAPAN,M_East,Latin,Australia,Asia,SAmer1,Arabia,China,SAmer2,Korea,Russian,FM";

	public static String default_navi_path = "SDCARD";
	public static String support_navi_path = "SDCARD,EXT_SDCARD1,EXT_SDCARD2,USB1,USB2,USB3,USB4";

	public static String default_video_out_format = "NTSC";
	public static String support_video_out_format = "NTSC,PAL";

	public static String default_tv_type = "ATV";
	public static String support_tv_type = "ATV,DVB";

	public static String default_tv_area = "China";
	public static String support_tv_area = "USA,Europe,China,Italy,OIRT,New Zealand,South Africa";

	public static String default_dvr_type = "Huan Xiang love";
	public static String support_dvr_type = "Huan Xiang love,QiYuHaiChuang";
	
	public static String default_lcd_type = "TTL";
	public static String support_lcd_type = "TTL,LVDS";
	
	public static String default_lvds_bit = "8";
	public static String support_lvds_bit = "6,8";
	
	public static String default_lcd_resolution = "800x480";
	public static String support_lcd_resolution = "800x480,1024x600,1024x768,1280x480,1280x720,1280x800";
	
	public static String default_tft_drive_current = "6mA";
	public static String support_tft_drive_current = "Default,2mA,4mA,6mA,8mA";
	
	public static String default_lvds_drive_current = "300mV";
	public static String support_lvds_drive_current = "Default,300mV,400mV,500mV,600mV";
	
	public static String default_dithering_output = "6";
	public static String support_dithering_output = "4,6,8,10,close";

	public static int backcar_enable = 1;
	public static int backcar_mirror = 0;
	public static int backcar_guidelines = 0;
	public static int backcar_radar = 0;
	public static int backcar_mute = 0;

	public static int bt_noice_enable = 0;
	public static String bt_device = "IFAW";
	public static String bt_pair = "0000";
	public static int bt_auto_connect = 0;
	public static int bt_auto_answer = 0;
	public static int bt_discover = 0;
	public static int bt_device_auto_set=0;
	public static String bt_device_name_oem="";

	public static int audioport_crv_audio_input = 0;
	public static int mic_volume = 44;
	public static int dvd_area_code = 0;
	public static String factory_password="8317";

	public static int outside_temp_test = 0;
	public static int lcd_enable = 0;
	public static int antenna_ctrl_enable = 0;

	// FM DX,FM LOC,AM DX,AM LOC,OIRT DX,OIRT LOC
	public static int DX_LOC[] = { 19, 39, 33, 33, 19, 39 };

	public static int videoport_drive_record = 0;
	public static int videoport_front_camera = 0;

	// bt ,gis,other
	public static int gain[] = { 128, 128, 112 };

	public static int power_amplifier_ctrl_enable = 0;

	public static String default_air_temp_ctrl = "Together";
	public static String support_air_temp_ctrl = "Together,Alone";

	public static int chramatic_lamp = 0;

	// aux,a2dp,fm,am,tv
	public static int db[] = { 105, 120, 98, 101, 128 };

	public static int headlight_enable = 1;
	public static int brake_enable = 0;

	public static int remix_ratio = 20;
	public static int remix_enable = 1;
	public static int listen_enable = 0;
	public static int aux_port = 2;
	

	// function config
	public static int fun_dvd_enable = 0;
	public static int fun_fm_enable = 1;
	public static int fun_gps_enable = 0;
	public static int fun_avin_enable = 0;
	public static int fun_avin2_enable = 0;
	public static int fun_tv_enable = 0;
	public static int fun_ipod_enable = 0;
	public static int fun_music_enable = 1;
	public static int fun_video_enable = 1;
	public static int fun_bt_enable = 0;
	public static int fun_dvr_enable = 1;
	public static int fun_dvr_uart_enable = 0;
	public static int fun_micracast_enable = 1;
	public static int fun_canbus_enable = 1;
	public static int fun_tpms_enable = 1;

	public static int fun_carlife_enable = 1;
	public static int fun_img_enable = 1;
	
	// qucaideng
	public static int light_mode = 0;
	public static int light_r = 28;
	public static int light_g = 28;
	public static int light_b = 28;
	
	public static int tyre_com = 6;

	// touch key learningn
	public static Vector<Integer> touch_key_array = new Vector<Integer>();
	public static String touch_key_btn = "";
	public static int touch_x_point_reverse = 1;
	public static int touch_y_point_reverse = 0;

	public static int voice_touch_enable = 1;
	public static int voice_wakeup_enable = 1;
	public static int assistive_touch_enable[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	// public static int sleep_poweroff_enable = 1;
	// headlight assi_touch voice_touch swc wallpaper wifi alto subwoofer
	public static int funs_other[] = { 1, 1, 1, 1, 1, 1, 1, 1, 0 };
	
	public static int boot_logo_enable = 0;
	public static int sleep_time = 0;
	public static int sleep_power_enable = 0;
	public static int sleep_ready_time = 0;

	public static final ArrayList<String> list_radio_area_all = XmlParse
			.getStringArray(XmlParse.support_radio_area);
	public static int[] intarray_sleep_time;
	public static int[] intarray_power_off_time;

	public XmlParse() {
		// TODO
	}

	/*
	 * public static void setLanguage(String value) { default_language = value;
	 * }
	 * 
	 * public static String getLanguage() { return default_language; }
	 */

	public static ArrayList<String> getStringArray(String value) {

		int nStartPos = 0;
		int nPos = 0;

		ArrayList<String> list = new ArrayList<String>();
		while (true) {
			nPos = value.indexOf(",", nStartPos);
			if (nPos < nStartPos) {
				String strTmp = value.substring(nStartPos);
				list.add(strTmp);
				break;
			} else {
				String strTmp = value.substring(nStartPos, nPos);
				list.add(strTmp);
				nStartPos = nPos + 1;
			}
		}

		/*
		 * for (int i = 0; i < list.size(); i++) {
		 * Log.i("test","list........................." + i + "............." +
		 * list.get(i)); }
		 */
		return list;
	}

	/*
	 * public static void setAllLanguage(String value) { support_language =
	 * value; }
	 * 
	 * public static String getAllLanguage() { return support_language; }
	 */

	public static void setValue(int[] array, int i, int value) {
		array[i] = value;
	}

	public static int[][] getValue(int[][] array) {
		return array;
	}

}
