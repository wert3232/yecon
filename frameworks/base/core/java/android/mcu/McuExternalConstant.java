
package android.mcu;

public class McuExternalConstant {

    /**
     * Mcu info type
     */
    public static final int MCU_RADIO_BAND_INFO_TYPE = 0x01;
    public static final int MCU_RADIO_PRESET_LIST_INFO_TYPE = 0x02;
    public static final int MCU_DEVICE_STATUS_INFO_TYPE = 0x03;
    public static final int MCU_SWC_DATA_INFO_TYPE = 0x04;
    public static final int MCU_SWC_SAMPLING_INFO_TYPE = 0x05;
    public static final int MCU_SWC_RESISTANCE_INFO_TYPE = 0x06;
    public static final int MCU_SYSTEM_PARAM_INFO_TYPE = 0x07;
    public static final int MCU_UPGRADE_INFO_TYPE = 0x08;
    public static final int MCU_CANBUS_INFO_TYPE = 0x09;
    public static final int MCU_VERSION_INFO_TYPE = 0x0A;
    public static final int MCU_ID_INFO_TYPE = 0x0B;

    /**
     * Mcu source define
     */
    public static final int MCU_SOURCE_OFF = 0x00;
    public static final int MCU_SOURCE_DISC = 0x01;
    public static final int MCU_SOURCE_SD = 0x02;
    public static final int MCU_SOURCE_USB = 0x03;
    public static final int MCU_SOURCE_RADIO = 0x04;
    public static final int MCU_SOURCE_TV = 0x05;
    public static final int MCU_SOURCE_CMMB = 0x06;
    public static final int MCU_SOURCE_DVB_T = 0x07;
    public static final int MCU_SOURCE_AUX1 = 0x08;
    public static final int MCU_SOURCE_AUX2 = 0x09;
    public static final int MCU_SOURCE_CDC = 0x0A;
    public static final int MCU_SOURCE_BT = 0x0B;
    public static final int MCU_SOURCE_CAMERA = 0x0C;
    public static final int MCU_SOURCE_NAVIGATION = 0x0D;
    public static final int MCU_SOURCE_MAIN = 0x0F;
    public static final int MCU_SOURCE_IPOD = 0x10;
    public static final int MCU_SOURCE_MCUTV = 0x11;
    public static final int MCU_SOURCE_ARM_UI_SRC = 0x12;
    public static final int MCU_SOURCE_IPOD_USB = 0x13;
    public static final int MCU_SOURCE_USB_REAR = 0x14;
    public static final int MCU_SOURCE_ISDBT = 0x15;
    public static final int MCU_SOURCE_ATV = 0x16;
    public static final int MCU_SOURCE_DRIVE_RECORD = 0x17;
	public static final int MCU_SOURCE_MUSIC = 0x18;
	public static final int MCU_SOURCE_VIDEO = 0x19;
	public static final int MCU_SOURCE_IMAGE = 0x20;
	public static final int MCU_SOURCE_SETTING = 0x21;
	public static final int MCU_SOURCE_CARLIFE= 0x22;
    public static final int MCU_SOURCE_STANDBY = 0xFF;

    /**
     * Mcu EQ type
     */
    public static final int MCU_EQ_USER = 0x00;
    public static final int MCU_EQ_POP = 0x01;
    public static final int MCU_EQ_NORMAL = 0x02;
    public static final int MCU_EQ_ROCK = 0x03;
    public static final int MCU_EQ_CLASSIC = 0x04;
    public static final int MCU_EQ_JAZZ = 0x05;
    public static final int MCU_EQ_AUTO = 0x06;

    /**
     * Mcu system param index
     */
    public static final int MCU_SYSTEM_PARAM_MPEG_INDEX = 0x00;
    public static final int MCU_SYSTEM_PARAM_BT_INDEX = 0x01;
    public static final int MCU_SYSTEM_PARAM_TIME_INDEX = 0x02;
    public static final int MCU_SYSTEM_PARAM_GENERNAL_INDEX = 0x03;
    public static final int MCU_SYSTEM_PARAM_VIDEOSTATE_INDEX = 0x04;
    public static final int MCU_SYSTEM_PARAM_FMRADIO_INDEX = 0x05;
    public static final int MCU_SYSTEM_PARAM_RDS_INDEX = 0x06;
    public static final int MCU_SYSTEM_PARAM_AUDIOVIDEO_INDEX = 0x07;
    public static final int MCU_SYSTEM_PARAM_BACKCAR_INDEX = 0x08;
    public static final int MCU_SYSTEM_PARAM_SLEEP_INDEX = 0x09;

    /**
     * Mcu SWC study request data
     */
    public static final int MCU_SWC_END = 0x00;
    public static final int MCU_SWC_STUDY = 0x01;
    public static final int MCU_SWC_SAVE = 0x02;
    public static final int MCU_SWC_RESET = 0x03;

    /**
     * Mcu upgrade state
     */
    public static final int MCU_UPGRADE_UNKNOWN = 0x00;
    public static final int MCU_UPGRADE_CONTINUE = 0x01;
    public static final int MCU_UPGRADE_RETRANS = 0x02;

    /**
     * Mcu common data value
     */
    public static final int MCU_DATA_STATUS_FORBIDDEN = 0;
    public static final int MCU_DATA_STATUS_ALLOWED = 1;

    public static final int MCU_DATA_STATUS_UNKNOWN = -1;
    public static final int MCU_DATA_STATUS_NOT_EXIST = 0;
    public static final int MCU_DATA_STATUS_EXIST = 1;

    public static final int MCU_DATA_NATURAL_DISH = 0;
    public static final int MCU_DATA_AUTO_INHALED = 1;

    public static final int MCU_DATA_SWITCH_OFF = 0;
    public static final int MCU_DATA_SWITCH_ON = 1;

    public static final int MCU_DATA_BRAKE_STARTUP = 0;
    public static final int MCU_DATA_BRAKE_NOT_STARTUP = 1;
	
	public static final int MCU_DATA_SLEEP_OFF = 0;
    public static final int MCU_DATA_SLEEP_ON = 1;
	
    public static final int MCU_DATA_TA_NOT_RECEIVED = 0;
    public static final int MCU_DATA_TA_RECEIVED = 1;

    public static final int MCU_DATA_VIDEO_INVALID = 0;
    public static final int MCU_DATA_VIDEO_VALID = 1;

    public static final int MCU_DATA_BT_AUDIO_NOT_CALLING = 0;
    public static final int MCU_DATA_BT_AUDIO_CALLING = 1;

    public static final int MCU_DATA_NAVI_AUDIO_NOT_REQUEST = 0;
    public static final int MCU_DATA_NAVI_AUDIO_REQUEST = 1;

    public static final int MCU_DATA_CAMERA_VIDEO_NOT_REQUEST = 0;
    public static final int MCU_DATA_CAMERA_VIDEO_REQUEST = 1;

    public static final int MCU_DATA_BT_MUSIC_STOP = 0;
    public static final int MCU_DATA_BT_MUSIC_PLAY = 1;

    public static final int MCU_DATA_NOT_IN_TC_MENU = 0;
    public static final int MCU_DATA_IN_TC_MENU = 1;

    public static final int MCU_DATA_BACKLIGHT_ON = 0;
    public static final int MCU_DATA_BACKLIGHT_OFF = 1;

    public static final int MCU_DATA_VOLUME_NOT_MUTE = 0;
    public static final int MCU_DATA_VOLUME_MUTE = 1;

    public static final int MCU_DATA_NOT_SAVED = 0;
    public static final int MCU_DATA_SAVED = 1;

    public static final int MCU_DATA_ACC_DISCONNECTED = 0;
    public static final int MCU_DATA_ACC_CONNECTED = 1;

    public static final int MCU_DATA_UPGRADE_END = 0;
    public static final int MCU_DATA_UPGRADE_START = 1;

    public static final int MCU_DATA_OUT_CYCLE = 0;
    public static final int MCU_DATA_IN_CYCLE = 1;

    public static final int MCU_DATA_NOT_DISPLAY = 0;
    public static final int MCU_DATA_DISPLAY = 1;

    public static final int MCU_DATA_NOT_LOCK = 0;
    public static final int MCU_DATA_LOCK = 1;

    /**
     * Mcu key code
     */
    public static final int K_NONE = 0x00;
    public static final int K_USER = 0xFF;
    public static final int K_SAVE_ALL = 0xFD;

    // Function keys
    public static final int K_POWER = 0x01;
    public static final int K_MUTE = 0x02;
    public static final int K_EQ = 0x03;
    public static final int K_VOL_UP = 0x04;
    public static final int K_VOL_DN = 0x05;
    public static final int K_PHONE_ON = 0x06;
    public static final int K_PHONE_OFF = 0x07;
    public static final int K_SOURCE_MODE = 0x08;
    public static final int K_SOURCE_HOME = 0x09;
    public static final int K_HOME_FRONTSRC = 0x0A;
    public static final int K_RECENT = 0x10;

    // DVD discs out of keys
    public static final int K_EJECT = 0x1D;

    // Generic keys
    public static final int K_0 = 0x20;
    public static final int K_1 = 0x21;
    public static final int K_2 = 0x22;
    public static final int K_3 = 0x23;
    public static final int K_4 = 0x24;
    public static final int K_5 = 0x25;
    public static final int K_6 = 0x26;
    public static final int K_7 = 0x27;
    public static final int K_8 = 0x28;
    public static final int K_9 = 0x29;
    public static final int K_UP = 0x2A;
    public static final int K_DOWN = 0x2B;
    public static final int K_LEFT = 0x2C;
    public static final int K_RIGHT = 0x2D;
    public static final int K_ENTER = 0x2E;
    public static final int K_RETURN = 0x2F;
    public static final int K_STAR = 0x30;
    public static final int K_NUMBER = 0x31;
    public static final int K_CLEAR = 0x32;

    // Quick function keys
    public static final int K_MUSIC = 0x40;
    public static final int K_PHOTO = 0x41;
    public static final int K_VIDEO = 0x42;
    public static final int K_NAVI = 0x43;
    public static final int K_TUNER = 0x44;
    public static final int K_DVD = 0x45;
    public static final int K_BLUETOOTH = 0x46;
    public static final int K_SETTING = 0x47;
    public static final int K_TV = 0x48;
    public static final int K_USB = 0x49;
    public static final int K_SD_CARD = 0x4A;
    public static final int K_TOUCH_MENU = 0x4F;

    // Radio keys
    public static final int T_RADIO_BAND = 0x50;
    public static final int T_RADIO_TUNE_UP = 0x51;
    public static final int T_RADIO_TUNING_DOWN = 0x52;
    public static final int T_RADIO_SEEK_UP = 0x53;
    public static final int T_RADIO_SEEK_DOWN = 0x54;
    public static final int T_RADIO_AS = 0x55;
    public static final int T_RADIO_PS = 0x56;
    public static final int T_RADIO_LOC = 0x57;
    public static final int T_RADIO_TA = 0x58;
    public static final int T_RADIO_PTY = 0x59;
    public static final int T_RADIO_AF = 0x5A;
    public static final int T_RADIO_REG = 0x5B;
    public static final int T_RADIO_CT = 0x5C;
    public static final int T_RADIO_EON = 0x5D;
    public static final int T_RADIO_RDS = 0x5E;
    public static final int T_RADIO_RDS_TYPE = 0x5F;
    public static final int T_RADIO_FM_FREQ = 0x60;
    public static final int T_RADIO_AM_FREQ = 0x61;
    public static final int T_RADIO_PRESET_SAVE = 0x62;
    public static final int T_RADIO_PRESET_LOAD = 0x63;
    public static final int T_RADIO_PTY_SEEK = 0x64;
    public static final int T_RADIO_STEREO_STATE_CHANGE = 0x65;
    public static final int T_RADIO_PRESET_PRE = 0x66;
    public static final int T_RADIO_PRESET_NEXT = 0x67;
    public static final int T_RADIO_DEL_PRESET = 0x68;
    public static final int T_RADIO_AUTO_SEEK = 0x69;
    //public static final int T_RADIO_FM = 0x69;
    //public static final int T_RADIO_AM = 0x6A;

    // Play controller keys
    public static final int T_STOP = 0x70;
    public static final int T_PLAY = 0x71;
    public static final int T_NEXT = 0x72;
    public static final int T_PREV = 0x73;
    public static final int T_FASTF = 0x74;
    public static final int T_FASTB = 0x75;
    public static final int T_REPEAT = 0x76;
    public static final int T_SHUFFLE = 0x77;
    public static final int T_SCAN = 0x78;
    public static final int T_A_B = 0x79;
    public static final int T_ZOOM = 0x7A;
    public static final int T_TITLE = 0x7B;
    public static final int T_SUB_T = 0x7C;
    public static final int T_PBC = 0x7D;
    public static final int T_ANGLE = 0x7E;
    public static final int T_STEP = 0x7F;
    public static final int T_TRACK = 0x80;
    public static final int T_FULL_SCREEN = 0x81;
    public static final int T_DVD_MENU = 0x82;

    // Other keys
    public static final int T_BT_AUDIO_MUTE = 0xA0;
    public static final int T_BLACKOUT = 0xA1;
    public static final int T_NAVI_MUTE = 0xA2;
    public static final int T_BEEP = 0xA3;
    public static final int T_SELECT = 0xA4;
    public static final int T_SETTING_RESET = 0xA5;
    public static final int T_SEND_WHEEL_STUDY = 0xA6;
    public static final int T_WHEEL_STUDY_OK = 0xA7;
    public static final int T_WHEEL_STUDY_RESET = 0xA8;
    public static final int T_SET_TQ = 0xA9;
    public static final int T_LOUDNESS = 0xAA;
    public static final int T_SUBWOOFER = 0xAB;
    public static final int T_SET_WHEEL_R_SW = 0xAC;

    public static final int T_IR_GOTO = 0xB1;
    public static final int T_KEY_DISP = 0xB2;
    public static final int T_REARVIEW = 0xB3;
    public static final int T_MOUSE_POS = 0xB4;
    public static final int T_CAMERA_RECORD = 0xB5;
    public static final int T_ONSTAR = 0xB6;
    public static final int T_DISPLAY_ON_OFF = 0xB7;
    public static final int T_SYS_RESTART = 0xB8;

    public static final int T_QB_POWER = 0xC0;
    public static final int T_QB_POWER_OFF = 0xC1;
    public static final int K_REAR_MUTE = 0xD0;

    public static final int K_BACKLIGHT_STATE = 0xFC;

    /**
     * Mcu broadcast actions and extra keys
     */
    // broadcast actions
    public static final String MCU_ACTION_DISC_STATUS = "com.yecon.action.DISC_STATUS";
    public static final String MCU_ACTION_DISC_NOT_EXIST = "com.yecon.action.DISC_NOT_EXIST";
    public static final String MCU_ACTION_DISC_EXIST = "com.yecon.action.DISC_EXIST";

    public static final String MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS = "com.yecon.action.HEADLIGHTS_AND_BRAKE_STATUS";
    public static final String MCU_ACTION_HEADLIGHTS_STATUS = "com.yecon.action.HEADLIGHTS_STATUS";
    public static final String MCU_ACTION_BRAKE_STATUS = "com.yecon.action.BRAKE_STATUS";

    public static final String MCU_ACTION_MEDIA_STOP = "com.yecon.action.MEDIA_STOP";
    public static final String MCU_ACTION_MEDIA_PLAY_PAUSE = "com.yecon.action.MEDIA_PLAY_PAUSE";
    public static final String MCU_ACTION_MEDIA_PAUSE = "com.yecon.action.MEDIA_PAUSE";
    public static final String MCU_ACTION_MEDIA_PLAY = "com.yecon.action.MEDIA_PLAY";
    public static final String MCU_ACTION_MEDIA_NEXT = "com.yecon.action.MEDIA_NEXT";
    public static final String MCU_ACTION_MEDIA_PREVIOUS = "com.yecon.action.MEDIA_PREVIOUS";
    public static final String MCU_ACTION_MEDIA_FAST_FORWARD = "com.yecon.action.MEDIA_FAST_FORWARD";
    public static final String MCU_ACTION_MEDIA_REWIND = "com.yecon.action.MEDIA_REWIND";
    
    public static final String MCU_ACTION_RADIO_PRESET_PRE = "com.yecon.action.RADIO_PRESET_PRE";
    public static final String MCU_ACTION_RADIO_PRESET_NEXT = "com.yecon.action.RADIO_PRESET_NEXT";

    public static final String MCU_ACTION_BACKCAR_OFF = "com.yecon.action.BACKCAR_OFF";
    public static final String MCU_ACTION_BACKCAR_ON = "com.yecon.action.BACKCAR_ON";

    public static final String MCU_ACTION_ACC_OFF = "com.yecon.action.ACC_OFF";
    public static final String MCU_ACTION_ACC_ON = "com.yecon.action.ACC_ON";

    public static final String MCU_ACTION_VOLUME_MUTE = "com.yecon.action.VOLUME_MUTE";
    public static final String MCU_ACTION_VOLUME_UNMUTE = "com.yecon.action.VOLUME_UNMUTE";
    public static final String MCU_ACTION_VOLUME_UP = "com.yecon.action.VOLUME_UP";
    public static final String MCU_ACTION_VOLUME_DOWN = "com.yecon.action.VOLUME_DOWN";
    public static final String MCU_ACTION_VOLUME_CHANGED = "com.yecon.action.VOLUME_CHANGED";

    public static final String MCU_ACTION_NAVI_KEY = "com.yecon.action.NAVI_KEY";
    public static final String MCU_ACTION_TUNER_KEY = "com.yecon.action.TUNER_KEY";
    public static final String MCU_ACTION_DVD_KEY = "com.yecon.action.DVD_KEY";
    public static final String MCU_ACTION_LOUDNESS_KEY = "com.yecon.action.LOUDNESS_KEY";

    public static final String YECON_ACTION_YECON_BOOT_COMPLETED = "com.yecon.action.YECON_BOOT_COMPLETED";

    public static final String YECON_ACTION_BOOTANIM_EXIT = "com.yecon.action.BOOTANIM_EXIT";
	
	public static final String ACTION_BT_IS_CALLING = "com.hzh.action.bt.calling";
	public static final String ACTION_BT_IS_IDLE = "com.hzh.action.bt.idle";
    // extra keys
    public static final String INTENT_EXTRA_STREAM_TYPE = "EXTRA_STREAM_TYPE";
    public static final String INTENT_EXTRA_CURR_VOLUME = "EXTRA_CURR_VOLUME";

    public static final String INTENT_EXTRA_DISC_STATUS = "EXTRA_DISC_STATUS";

    public static final String INTENT_EXTRA_HEADLIGHTS_STATUS = "EXTRA_HEADLIGHTS_STATUS";
    public static final String INTENT_EXTRA_BRAKE_STATUS = "EXTRA_BRAKE_STATUS";
    public static final String INTENT_EXTRA_BACKCAR_STATUS = "EXTRA_BACKCAR_STATUS";

    // system property key
    public static final String PROPERTY_KEY_CBMSOURCE = "persist.sys.cbmsource";

    public static final String PROPERTY_KEY_POWERKEY = "persist.sys.powerkey";
    public static final String PROPERTY_KEY_BACKOUTKEY = "persist.sys.backoutkey";

    public static final String PROPERTY_KEY_BTPHONE_STARTUP = "persist.sys.btphone_startup";

    public static final String PROPERTY_KEY_BOOTLOGO_ENABLE = "persist.sys.bootlogo";
    public static final String PROPERTY_KEY_BOOTLOGO_STARTUP = "persist.sys.bootlogo_startup";

    public static final String PROPERTY_KEY_STREAM_MUSIC_VOLUME = "persist.sys.stream_music_volume";
    public static final String PROPERTY_KEY_STREAM_BT_VOLUME = "persist.sys.stream_bt_volume";

    public static final String PROPERTY_KEY_APP_REBOOT_FLAG = "persist.sys.app_reboot_flag";

    public static final String PROPERTY_KEY_FUNC_MUTE = "ro.func.mute";
    public static final String PROPERTY_KEY_FUNC_SWC = "ro.func.swc";

    /**
     * Mcu data length
     */
    public static final int MCU_RADIO_PRESETLIST_LENGTH = 12;

    public static final int MCU_SWC_DATA_LENGTH = 38;
    public static final int MCU_SWC_SAMPLING_RESULTS_LENGTH = 2;

    public static final int MCU_MPEG_SET_DATA_LENGTH = 12;
    public static final int MCU_BT_SET_DATA_LENGTH = 4;
    public static final int MCU_TIME_SET_DATA_LENGTH = 9;
    public static final int MCU_GENERNAL_SET_DATA_LENGTH = 3;
    public static final int MCU_VIDEO_STATE_SET_DATA_LENGTH = 11;
    public static final int MCU_BACKCAR_SET_DATA_LENGTH = 6;
    public static final int MCU_FMRADIO_SET_DATA_LENGTH = 7;
    public static final int MCU_RDS_SET_DATA_LENGTH = 4;
    public static final int MCU_AUDIO_VIDEO_SET_DATA_LENGTH = 3;
    public static final int MCU_SET_FACTORY_INFO_LENGTH = 3;
    public static final int MCU_ID_INFO_LENGTH = 4;
	
	
}
