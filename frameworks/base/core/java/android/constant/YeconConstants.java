
package android.constant;

public class YeconConstants {
    /**
     * broadcast action
     */
    public static final String ACTION_PREQB_POWERON = "autochips.intent.action.PREQB_POWERON";
    public static final String ACTION_QB_POWERON = "autochips.intent.action.QB_POWERON";
    public static final String ACTION_QB_POWEROFF = "autochips.intent.action.QB_POWEROFF";
    public static final String ACTION_QB_PREPOWEROFF = "autochips.intent.action.QB_PREPOWEROFF";

    public static final String ACTION_YECON_SCREEN_OFF = "com.yecon.action.SCREEN_OFF";

    public static final String ACTION_CARLIFE_REQUEST = "com.yecon.action.CARLIFE_REQUEST";
    public static final String ACTION_QUIT_APK = "autochips.intent.action.quit.apk";

    public static final String ACTION_BACKCAR_START = "com.yecon.action.BACKCAR_START";
    public static final String ACTION_BACKCAR_STOP = "com.yecon.action.BACKCAR_STOP";

    public static final String ACTION_SET_BACKLIGHT = "com.yecon.action.SET_BACKLIGHT";
    public static final String ACTION_SET_MUTE = "com.yecon.action.SET_MUTE";

    public static final String ACTION_ISSSR_KEY = "com.yecon.action.ISSSR_KEY";

    /**
     * properties
     */
    public static final String PROPERTY_KEY_STARTFM = "persist.sys.startfm";
    public static final String PROPERTY_KEY_STARTBT = "persist.sys.startbt";
    public static final String PROPERTY_KEY_STARTBACKCAR = "persist.sys.startbackcar";

    public static final String PROPERTY_QB_POWERON = "persist.sys.qbpoweron";
    public static final String PROPERTY_QB_POWEROFF = "persist.sys.qbpoweroff";

    public static final String PROPERTY_MUSIC_PLAY_FILE = "persist.sys.musicplayfile";
    public static final String PROPERTY_VIDEO_PLAY_FILE = "persist.sys.videoplayfile";

    public static final String PROPERTY_POWEROFF_REASON = "persist.sys.poweroffreason";

    public static final String PROPERTY_WIFI_ONOFF = "persist.sys.wifionoff";

    // navi properties
    public static final String PROPERTY_NAVI_REMIX = "persist.sys.navi_remix";
    public static final String PROPERTY_NAVI_LISTEN = "persist.sys.navi_listen";

    public static final String PROPERTY_KEY_IS_NAVI_APP = "persist.sys.isnaviapp";
    public static final String PROPERTY_KEY_NAVI_IS_FORE = "persist.sys.naviisfore";

    public static final String PROPERTY_REMIX_RATIO = "persist.sys.remix_ratio";

    public static final String PROPERTY_KEY_RESTORE_NAVI = "persist.sys.restore_navi";

    // volume properties
    public static final String PROPERTY_KEY_FUNC_UNIFIED_VOLUME_ADJUST = "ro.func.unified.volume.adjust";

    /**
     * app package and start activity
     */
    public static final String LAUNCHER_PACKAGE_NAME = "com.android.launcher";
    public static final String LAUNCHER_STARTUP_ACTIVITY = "com.android.launcher2.Launcher";

    public static final String WALLPAPER_SETTING_PACKAGE_NAME = "com.android.launcher";
    public static final String WALLPAPER_SETTING_STARTUP_ACTIVITY = "com.android.launcher2.WallpaperChooser";

    public static final String BROWSER_PACKAGE_NAME = "com.android.browser";
    public static final String BROWSER_STARTUP_ACTIVITY = "com.android.browser.BrowserActivity";

    public static final String CALENDAR_PACKAGE_NAME = "com.android.calendar";
    public static final String CALENDAR_STARTUP_ACTIVITY = "com.android.calendar.AllInOneActivity";

    public static final String DESKCLOCK_PACKAGE_NAME = "com.android.deskclock";
    public static final String DESKCLOCK_STARTUP_ACTIVITY = "com.android.deskclock.DeskClock";

    public static final String MIRACAST_PACKAGE_NAME = "com.autochips.miracast";
    public static final String MIRACAST_STARTUP_ACTIVITY = "com.autochips.miracast.MainActivity";

    public static final String SETTINGS_PACKAGE_NAME = "com.android.settings";
    public static final String SETTINGS_STARTUP_ACTIVITY = "com.android.settings.Settings";

    public static final String CALCULATOR_PACKAGE_NAME = "com.android.calculator2";
    public static final String CALCULATOR_STARTUP_ACTIVITY = "com.android.calculator2.Calculator";

    public static final String QUICKSEARCH_PACKAGE_NAME = "com.android.quicksearchbox";
    public static final String QUICKSEARCH_STARTUP_ACTIVITY = "com.android.quicksearchbox.SearchActivity";

    public static final String DOWNLOADS_PACKAGE_NAME = "com.android.providers.downloads.ui";
    public static final String DOWNLOADS_STARTUP_ACTIVITY = "com.android.providers.downloads.ui.DownloadList";

    public static final String SOUND_SETTING_PACKAGE_NAME = "com.yecon.sound.setting";
    public static final String SOUND_SETTING_STARTUP_ACTIVITY = "com.yecon.sound.setting.AudioSetting";

    public static final String VIDEO_SETTING_PACKAGE_NAME = "com.yecon.carsetting";
    public static final String VIDEO_SETTING_STARTUP_ACTIVITY = "com.yecon.carsetting.VideosetActivity";

    public static final String CAR_SETTING_PACKAGE_NAME = "com.yecon.carsetting";
    public static final String CAR_SETTING_START_ACTIVITY = "com.yecon.carsetting.FragmentTabAcitivity";
    public static final String CAR_SETTING_NAVIGATION_ACTIVITY = "com.yecon.carsetting.NavigationAcitivity";
    public static final String CAR_SETTING_NAVIGATION_SET_ACTIVITY = "com.yecon.carsetting.NavigationsetActivity";

    public static final String FILEBROWSER_PACKAGE_NAME = "com.yecon.filemanager";
    public static final String FILEBROWSER_START_ACTIVITY = "com.yecon.filemanager.LauncherActivity";

    public static final String GPSTEST_PACKAGE_NAME = "com.yecon.gpstest";
    public static final String GPSTEST_START_ACTIVITY = "com.yecon.gpstest.GPSTestActivity";

    public static final String PICBROWSER_PACKAGE_NAME = "com.yecon.imagebrowser";
    public static final String PICBROWSER_START_ACTIVITY = "com.yecon.imagebrowser.ImageBrowserActivity";

    public static final String TPMS_PACKAGE_NAME = "com.yecon.tpms";
    public static final String TPMS_START_ACTIVITY = "com.yecon.tpms.TPMSActivity";

    public static final String CANBUS_PACKAGE_NAME = "com.can.activity";
    public static final String CANBUS_START_ACTIVITY = "com.can.ui.CanActivity";

    public static final String FMRADIO_PACKAGE_NAME = "com.yecon.fmradio";
    public static final String FMRADIO_START_ACTIVITY = "com.yecon.fmradio.FMRadioMainActivity";

    public static final String VIDEO_PACKAGE_NAME = "com.yecon.video";
    public static final String VIDEO_START_ACTIVITY = "com.yecon.video.VideoPlaybackMainActivity";

    public static final String BLUETOOTH_PACKAGE_NAME = "com.autochips.bluetooth";
    public static final String BLUETOOTH_START_ACTIVITY = "com.autochips.bluetooth.MainBluetoothActivity";

    public static final String DVD_PACKAGE_NAME = "com.yecon.dvdplayer";
    public static final String DVD_START_ACTIVITY = "com.yecon.dvdplayer.DvdStartupActivity";
    public static final String DVD_EJECT_ACTIVITY = "com.yecon.dvdplayer.DvdEjectActivity";

    public static final String MUSIC_PACKAGE_NAME = "com.yecon.music";
    public static final String MUSIC_START_ACTIVITY = "com.yecon.music.MusicPlaybackMainActivity";

    public static final String IPOD_PACKAGE_NAME = "com.yecon.ipodplayer";
    public static final String IPOD_START_ACTIVITY = "com.yecon.ipodplayer.MainActivity";

    public static final String AVIN_PACKAGE_NAME = "com.yecon.avin";
    public static final String AVIN_START_ACTIVITY = "com.yecon.avin.AVInActivity";

    public static final String AVIN_EXT_PACKAGE_NAME = "com.yecon.avin";
    public static final String AVIN_EXT_START_ACTIVITY = "com.yecon.avin.AVInExtActivity";

    public static final String TV_PACKAGE_NAME = "com.yecon.avin";
    public static final String TV_START_ACTIVITY = "com.yecon.avin.TvActivity";

    public static final String ATV_PACKAGE_NAME = "com.yecon.avin";
    public static final String ATV_START_ACTIVITY = "com.yecon.avin.ATvActivity";

    public static final String DTV_PACKAGE_NAME = "com.yecon.avin";
    public static final String DTV_START_ACTIVITY = "com.yecon.avin.DTvActivity";

    public static final String DVR_PACKAGE_NAME = "com.autochips.dvr";
    public static final String DVR_START_ACTIVITY = "com.autochips.dvr.MainActivity";
	
	public static final String DVR_UART_PACKAGE_NAME = "com.yecon.avin";
    public static final String DVR_UART_START_ACTIVITY = "com.yecon.avin.DvrActivity";

    public static final String CARLIFE_PACKAGE_NAME = "com.baidu.carlifevehicle";
    public static final String CARLIFE_START_ACTIVITY = "com.baidu.carlifevehicle.CarlifeActivity";

    public static final String FAWLINK_PACKAGE_NAME = "com.neusoft.ssp.faw.j6p.car.assistant";
    public static final String FAWLINK_START_ACTIVITY = "com.neusoft.ssp.faw.j6p.car.assistant.ActivateActivity";

    public static final String YECON_MEDIA_PROVIDER_PACKAGE_NAME = "com.yecon.yeconmediaprovider";

    public static final String OPERATEINTRO_PACKAGE_NAME = "com.yecon.OperateIntro";
    public static final String OPERATEINTRO_START_ACTIVITY = "com.yecon.OperateIntro.mainActivity";

    public static final String CODRIVE_PACKAGE_NAME = "com.baidu.che.codriver";
    public static final String CODRIVE_START_ACTIVITY = "com.baidu.che.codriver.ui.MainActivity";

    /**
     * volume src
     */
    public static final int SRC_VOLUME_UNMUTE = 0x00000000;
    public static final int SRC_VOLUME_DVD = 0x00000001;
    public static final int SRC_VOLUME_MUSIC = 0x00000002;
    public static final int SRC_VOLUME_VIDEO = 0x00000004;
    public static final int SRC_VOLUME_RADIO = 0x00000008;
    public static final int SRC_VOLUME_TV = 0x00000010;
    public static final int SRC_VOLUME_DVR = 0x00000020;
    public static final int SRC_VOLUME_AUX1 = 0x00000040;
    public static final int SRC_VOLUME_AUX2 = 0x00000080;
    public static final int SRC_VOLUME_BT_PHONE = 0x00000100;
    public static final int SRC_VOLUME_BT_MUSIC = 0x00000200;
    public static final int SRC_VOLUME_IPOD = 0x00000400;
    public static final int SRC_VOLUME_IPOD_USB = 0x00000800;
    public static final int SRC_VOLUME_GIS = 0x00001000;
    public static final int SRC_VOLUME_BACKCAR = 0x00002000;
    public static final int SRC_VOLUME_UP = 0x02000000;
    public static final int SRC_VOLUME_DOWN = 0x04000000;
    public static final int SRC_VOLUME_MUTE = Integer.MAX_VALUE;
}
