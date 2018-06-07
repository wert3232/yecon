
package com.yecon.videoplayer2;

public class VideoPlaybackConstant {
    public static final int UPDATE_VIEW_DATA = 1;
    public static final int UPDATE_UI = 2;
    public static final int CREATE_THUMB_THREAD = 3;
    public static final int SHOW_PROGRESS_DIALOG = 4;
    public static final int DISMISS_PROGRESS_DIALOG = 5;
    public static final int DELETE_DATA = 6;
    public static final int CREATE_QUERY_DATA_THREAD = 7;
    public static final int SHOW_ERROR = 8;
    public static final int QUERY_DATA_END = 9;

    public static final int MEDIA_FILE_EXIST = 0;
    public static final int MEDIA_FILE_NOTEXIST = 1;
    public static final int MEDIA_FILE_SCANING = 2;

    public static final int THUMB_TIME = 1500;
    public static final int BATCH_UPDATE_NUM = 3;

    public static final int UPDATEVIEW_COUNT = 10;

    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;

    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;

    public static final int SONG_FF_SPEED_NORMAL = 1;
    public static final int SONG_FF_SPEED_2X = 2;
    public static final int SONG_FF_SPEED_4X = 4;
    public static final int SONG_FF_SPEED_8X = 8;
    public static final int SONG_FF_SPEED_16X = 16;
    public static final int SONG_FF_SPEED_32X = 32;
    public static final int SONG_RW_SPEED_2X = -2;
    public static final int SONG_RW_SPEED_4X = -4;
    public static final int SONG_RW_SPEED_8X = -8;
    public static final int SONG_RW_SPEED_16X = -16;
    public static final int SONG_RW_SPEED_32X = -32;

    public static final int CAP_FILE_SEEK_UNSUPPORT = 0x00000001;
    public static final int CAP_FILE_FF_UNSUPPORT = CAP_FILE_SEEK_UNSUPPORT << 1;
    public static final int CAP_FILE_RW_UNSUPPORT = CAP_FILE_FF_UNSUPPORT << 1;
    public static final int CAP_VIDEO_RESOLUTION_UNSUPPORT = CAP_FILE_RW_UNSUPPORT << 1;
    public static final int CAP_VIDEO_BITRATE_UNSUPPORT = CAP_VIDEO_RESOLUTION_UNSUPPORT << 1;
    public static final int CAP_VIDEO_FRAMERATE_UNSUPPORT = CAP_VIDEO_BITRATE_UNSUPPORT << 1;
    public static final int CAP_VIDEO_CODEC_UNSUPPORT = CAP_VIDEO_FRAMERATE_UNSUPPORT << 1;
    public static final int CAP_VIDEO_PROFILE_LEVEL_UNSUPPORT = CAP_VIDEO_CODEC_UNSUPPORT << 1;
    public static final int CAP_AUDIO_BITRATE_UNSUPPORT = CAP_VIDEO_PROFILE_LEVEL_UNSUPPORT << 1;
    public static final int CAP_AUDIO_SAMPLERATE_UNSUPPORT = CAP_AUDIO_BITRATE_UNSUPPORT << 1;
    public static final int CAP_AUDIO_CODEC_UNSUPPORT = CAP_AUDIO_SAMPLERATE_UNSUPPORT << 1;
    public static final int CAP_AUDIO_PROFILE_LEVEL_UNSUPPORT = CAP_AUDIO_CODEC_UNSUPPORT << 1;

    public static final int FRONTREAR_STATE_CLOSED = 0;
    public static final int FRONTREAR_STATE_BACKGROUND = 1;
    public static final int FRONTREAR_STATE_FOREGROUND = 2;

    public final static int SCREEN_FULL = 0;
    public final static int SCREEN_DEFAULT = 1;

    public static final int FULL_SCREEN_HEIGHT = 480;
    public static final int DELAY_TIME = 300;

    public static final long BLACK_TIMEOUT = 500;

    public static final int HIDEDELAYTIME = 8000;
    public static final int TIMEGOHOME = 2000;
    public static final int AUTO_DISMISS_ERROR_TOAST_TIME = 4000;

    public static final int PROGRESS_CHANGED = 0;
    public static final int HIDE_CONTROLER = 1;
    public static final int HANDLEERROR_PLAYNEXT = 2;
    public static final int PREPARE_FINISH = 3;
    public static final int JUDGE_TOUCH_DELAY = 4;
    public static final int SHOW_ERROR_TOAST = 5;
    public static final int GO_HOME = 6;
    public static final int ENABLEBUTTON6 = 7;
    public static final int SEEK_TO_FILESTART = 10;
    public static final int LOADINGFILE_PROGRESS_CHANGED = 11;
    public static final int HIDE_REARTOAST = 12;
    public static final int UNMOUNT_EXITAPP = 13;
    public static final int SHOW_VIDEOVIEW = 14;
    public static final int PLAY_PAUSE = 15;

    public static final String INT_SDCARD_PATH = "/mnt/sdcard";
    public static final String EXT_SDCARD1_PATH = "/mnt/ext_sdcard1";
    public static final String EXT_SDCARD2_PATH = "/mnt/ext_sdcard2";
    public static final String UDISK1_PATH = "/mnt/udisk1";
    public static final String UDISK2_PATH = "/mnt/udisk2";
    public static final String UDISK3_PATH = "/mnt/udisk3";
    public static final String UDISK4_PATH = "/mnt/udisk4";
    public static final String UDISK5_PATH = "/mnt/udisk5";

    public static final boolean IS_AUTO_HIDE_PANEL = true;
    public static final boolean IS_CYCLE_PLAY_LIST = true;

    public static final String KEY_AUDIO_INDEX = "audioindex";
    public static final String KEY_SUB_INDEX = "subindex";
    // public static final String KEY_PLAY_TIME = "playtime";
    // public static final String KEY_PLAY_PATH = "playpath";

    public static final String CBM_FORBIDEN_EXCEP = "cbm forbid";
    
}
