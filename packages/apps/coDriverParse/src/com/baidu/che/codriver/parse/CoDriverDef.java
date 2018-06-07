package com.baidu.che.codriver.parse;

public interface CoDriverDef {
	static final String JSON_TYPE[] = { "domain", "intent", "object", "appname", "item", "action_type", 
										"operation", "mode", "byartist", "song", "name", "number" };
	static final String APP_PACKAGE_LIST[][] = {
		{ "com.autochips.dvr", "com.autochips.dvr.MainActivity" },
		{ "com.yecon.carsetting", "com.yecon.carsetting.FragmentTabAcitivity" },
		{ "com.yecon.carsetting", "com.yecon.carsetting.NavigationAcitivity" },
		{ "com.yecon.imagebrowser", "com.yecon.imagebrowser.ImageBrowserActivity" }, 
		{ "com.yecon.fmradio", "com.yecon.fmradio.FMRadioMainActivity"},
		{ "com.autochips.bluetooth", "com.autochips.bluetooth.MainBluetoothActivity" },
		{ "com.yecon.avin", "com.yecon.avin.AVInActivity"},
		{ "com.android.settings", "com.android.settings.Settings"},
		{ "com.autochips.bluetooth", "com.autochips.bluetooth.BtPhonebookActivity"},};
	
	static final String ACTION_BACKCAR_START  = "com.yecon.action.BACKCAR_START";
	static final String ACTION_BACKCAR_STOP   = "com.yecon.action.BACKCAR_STOP";
	static final String ACTION_BT_CALLOUT = "com.autochips.bluetooth.BtDialPadActivity.action.BLUETOOTH_CALLOUT";
	static final String EXTRA_BT_CALLOUT_NUM = "com.autochips.bluetooth.BtDialPadActivity.extra.EXTRA_BLUETOOTH_CALLOUT_NUMBER";
	static final String ACTION_BT_SON_ACTIVITY = "action.bt.son.activity";
	static final String BT_BOOK	= "bt.book";
	static final String ACTION_MEDIA_INFO = "action.media.info";
	static final String ACTION_MEDIA_RANDOM = "action.media.random";
	static final String ACTION_MEDIA_REPEAT = "action.media.repeat";
	static final String MEDIA_RANDOM_ON = "true";
	static final String MEDIA_REPEAT_ALL = "all";
	
	static final String APP_NAME_DVR  = "行车记录";
	static final String APP_NAME_DOG  = "电子狗";
	static final String APP_NAME_KUWO = "酷我音乐";
	
	static final String HEAD_APP = "app";
	static final String HEAD_OPEN = "open";
	static final String HEAD_CLOSE = "close";
	static final String HEAD_APPNAME = "appname";
	
	// navi	
	static final String HEAD_MAP = "map";
	static final String MAP_ROUTE = "route";
	static final String HEAD_NAVIGATE_INSTRUCTION = "navigate_instruction";
	static final String NAVI_NAVIGATE = "navigate"; 	// 打开导航
	static final String NAVI_QUIT = "quit";				// 关闭导航
	static final String NAVI_VIEW_MAP = "view_map";		// 看
	static final String NAVI_ROUTE_HOME = "route_home"; // 回家
	static final String NAVI_ROUTE_WORK = "route_work"; // 回公司
	static final String NAVI_ZOOM_IN = "zoom_in";		// 放大地图
	static final String NAVI_ZOOM_OUT = "zoom_out"; 	// 缩小地图
	static final String NAVI_REFRESH_ROUTE = "refresh_route"; 			// 更新路线
	static final String NAVI_ROUTE_CONDITION = "route_condition"; 		// 路况
	static final String NAVI_HEAD_FORWARD = "head_forward"; 			// 跟随模式
	static final String NAVI_NORTH_FORWARD = "north_forward"; 			// 正北模式
	static final String NAVI_FULL_ROUTE = "full_route"; // 全览模式
	static final String NAVI_MAP_DOWN = "map_down"; 	// 地图向下
	static final String NAVI_MAP_UP = "map_up"; 		// 地图向上
	static final String NAVI_MAP_LEFT = "map_left";		// 地图向左
	static final String NAVI_MAP_RIGHT = "map_right";	// 地图向右
	static final String NAVI_LOCATION = "location";		// 我的位置
	
	// other
	static final String HEAD_CODRIVER = "codriver";		
	static final String CODRIVER_YES = "yes";			// 确定
	static final String CODRIVER_NO = "no";				// 取消
	static final String CODRIVER_QUIT = "quit";			// 退出
	static final String CODRIVER_LOCK_SCREEN = "lock_screen"; 			// 关屏
	static final String CODRIVER_HELP = "help";			// 帮助
	static final String CODRIVER_BACK_HOME = "back_home"; 				// 返回主界面
	static final String CODRIVER_IND_CENTER = "ind_center"; 			// 打开个人中心
	static final String CODRIVER_SYSTEM_SETTING = "system_setting"; 	// 打开设置
	static final String CODRIVER_VIDEO = "video";		// 视频
	static final String CODRIVER_PICTURE = "picture";	// 图片:open close
	static final String CODRIVER_TAKE_PICTURE = "take_picture"; 		// 拍照
	static final String CODRIVER_MUTE = "mute";			// 静音:open close
	static final String CODRIVER_LIGHT_UP = "light_up";	// 增加亮度
	static final String CODRIVER_LIGHT_DOWN = "light_down"; 			// 降低亮度
	static final String CODRIVER_LIGHT_UP_MAX = "light_up_max"; 		// 亮度调为最大
	static final String CODRIVER_LIGHT_DOWN_MIN = "light_down_min"; 	// 亮度调为最小
	static final String CODRIVER_WIFI = "wifi";			// wifi
	static final String CODRIVER_NETWORK_SHARING = "network_sharing"; 	// wifi热点
	static final String CODRIVER_TRAFFIC_MANAGE = "traffic_manage";		// 流量管理
	static final String CODRIVER_COLLISION_DETECT = "collision_detect"; // 碰撞检测
	static final String CODRIVER_RADIO = "radio";		// 收音机
	static final String CODRIVER_PHOTO_SHOOT = "photo_shoot";			// 违章抓拍
	static final String CODRIVER_NOT_UPLOAD = "not_upload";				// 取消上传
	static final String CODRIVER_NETWORK = "network";	// 打开移动网络
	static final String CODRIVER_BACK = "back";			// 返回
	static final String CODRIVER_VOICE_SETTING = "voice_setting";		// 语音设置
	static final String CODRIVER_BLUETOOTH = "bluetooth";				// 蓝牙
	static final String CODRIVER_VOLUME_UP = "volume_up";				// 增加音量
	static final String CODRIVER_VOLUME_DOWN = "volumn_down";			// 降低音量
	static final String CODRIVER_VOLUME_UP_MAX = "volume_up_max";		// 音量调到最大
	static final String CODRIVER_VOLUME_DN_MIX = "volime_dn_mix";		// 音量调为最小
	static final String CODRIVER_OPTION = "option";		// 第几个
	static final String CODRIVER_MEDIA_PLAYER = "media_player";			// 音乐
	static final String CODRIVER_ENTERTAINMENT = "entertainment";		// 娱乐
	static final String CODRIVER_DVD = "disc";							// 碟片
	static final String CODRIVER_TV = "tv";								// 电视
	static final String CODRIVER_AVIN = "avin";							// avin
	
	// contacts.view 查看联系人
	static final String HEAD_CONTACTS = "contacts";		//
	static final String HEAD_VIEW = "view";				
	
	// music
	static final String HEAD_PLAYER = "player";			
	static final String PLAYER_SET = "set";
	static final String PLAYER_ACTION_TYPE = "action_type";				//
	static final String PLAYER_MODE = "mode";
	static final String PLAYER_PLAY = "play";			// 播放
	static final String PLAYER_EXITPLAYER = "exitplayer";				// 停止/退出音乐
	static final String PLAYER_PAUSE = "pause";			// 暂停
	static final String PLAYER_PREVIOUS = "previous";	// 上一个
	static final String PLAYER_NEXT = "next";			// 下一个
	static final String PLAYER_RANDOM = "random";		// 随机播放
	static final String PLAYER_FULL_LOOP = "full_loop";	// 循环播放
	static final String PLAYER_ORDER = "order";			// 顺序播放
	static final String PLAYER_SINGLE = "single";		// 单曲播放
	static final String PLAYER_SINGLE_LOOP = "single_loop";				// 单曲循环播放
	
	static final String HEAD_MUSIC = "music";			
	static final String MUSIC_PLAY = "play";			// 听歌
	static final String BYARTIST = "byartist";			// 艺术家
	static final String SONG = "song";					// 
	
	// telephone
	static final String HEAD_TELEPHONE = "telephone";
	static final String TELEPHONE_SETTING = "telephone_setting";
	static final String TELEPHONE_OPERATION = "operation";
	static final String TELEPHONE_ANSWERCALL = "answercall";			// 接听
	static final String TELEPHONE_HANGUP = "hangup";	// 挂断
	static final String TELEPHONE_CALL = "call";	// 打电话
	
	// backlight
    static final int BKL_HIGH = 3;
    static final int BKL_MID = 2;
    static final int BKL_LOW = 1;
    static final int BKL_OFF = 0;
    
    static final int BRIGHTNESS_HIGH = 80 * 255 / 100; // 80; //Jade, Backlight // Range: 0-255;
    static final int BRIGHTNESS_MID = 50 * 255 / 100; // 50;
    static final int BRIGHTNESS_LOW = 35 * 255 / 100; // 35;
    static final int BRIGHTNESS_OFF = -1;
    
    static final int[] BRIGHT_TAB = { BRIGHTNESS_OFF , 
        BRIGHTNESS_LOW, BRIGHTNESS_MID, BRIGHTNESS_HIGH };
    static final String Style_light_hue_tag = "persist.sys.yeconHue";
    static final String Style_light_saturation_tag = "persist.sys.yeconSaturation";
    static final String Style_light_contrast_tag = "persist.sys.yeconContrast";
    static final String Style_light_bright_tag = "persist.sys.yeconBrightness";
    static final String Style_light_backlight_tag = "persist.sys.yeconBacklight";
    static final String Style_backcar_mirror_tag = "persist.sys.yeconMirror";
    static final String YECON_ACTION_BRIGHTNESS_CHANGED = "com.yecon.action_BRIGHTNESS_CHANGED";
    
    static final int DEFAULT_HUE = 50;
    static final int DEFAULT_SATURATION = 50;
    static final int DEFAULT_CONTRAST = 25;
    static final int DEFAULT_BRIGHTNESS = 40;
    static final int DEFAULT_BACKLIGHT = 204;
    
    static final int ADD_BKL = 0;
    static final int DEL_BKL = 1;
    static final int MAX_BKL = 2;
    static final int MIN_BKL = 3;
}
