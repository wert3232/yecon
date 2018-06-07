
package com.yecon.volumeadjust;

public class Constants {
    public static final String SDCARD1_PATH = "/mnt/ext_sdcard1";

    public static final String VOLUME_STREAM_MUSIC_FILE = "stream_music_data";
    public static final String VOLUME_STREAM_AVIN_FILE = "stream_avin_data";
    public static final String VOLUME_STREAM_BT_FILE = "stream_bt_data";

    public static final String FILE_SPACE_FLAG = ",";

    public static final int VOLUME_STREAM_MUSIC = 0;
    public static final int VOLUME_STREAM_AVIN = 1;
    public static final int VOLUME_STREAM_BT = 2;

    public static final int MAX_VOLUME_LEVEL = 40;
    public static final int MIN_VOLUME_LEVEL = 0;

    public static final int VOLUME_CHANGE_FLAG_SUB = 0;
    public static final int VOLUME_CHANGE_FLAG_ADD = 1;

    public static final int VOLUME_CHANGE_LEVEL1 = 10;
    public static final int VOLUME_CHANGE_LEVEL2 = 50;
    public static final int VOLUME_CHANGE_LEVEL3 = 100;
    public static final int VOLUME_CHANGE_LEVEL4 = 200;
    public static final int VOLUME_CHANGE_LEVEL5 = 500;
    public static final int VOLUME_CHANGE_LEVEL6 = 1000;

    public static final int VOLUME_KEY_DELAY_TIME = 500;

    public static final int[] VOLUME_MUSIC_DAC = {
            0,
            12, 22, 42, 82, 162,
            292, 542, 1092, 1372, 1712,
            2032, 2712, 2882, 3232, 3422,
            3832, 4122, 4482, 4992, 5522,
            6072, 6442, 7002, 7472, 7812,
            8262, 9932, 10922, 11772, 13142,
            13872, 14622, 14922, 15572, 16622,
            17372, 18822, 19872, 20672, 26272
    };

    public static final int[] VOLUME_AVIN_DAC = {
            0,
            22, 32, 52, 82, 232,
            512, 1142, 2592, 3192, 4012,
            5032, 6422, 6982, 7732, 8522,
            9232, 10122, 11082, 11992, 13022,
            14172, 15542, 17402, 18172, 20812,
            22562, 24932, 27322, 29272, 32042,
            34372, 37822, 40922, 43572, 45622,
            47372, 49922, 50872, 51872, 53272
    };

    public static final int[] VOLUME_BT_DAC = {
            0,
            22, 42, 62, 152, 342,
            722, 1602, 3542, 4372, 5612,
            7082, 8812, 9632, 10532, 11822,
            12632, 13822, 14782, 16092, 17622,
            19172, 20742, 22602, 24472, 27312,
            28862, 31532, 34622, 38172, 40842,
            44472, 48122, 52222, 54772, 57622,
            60372, 63022, 65872, 67772, 74272
    };

    public static final int[] VOLUME_MUSIC_DAC_BACKUP = {
            0,
            272, 370, 472, 572, 672,
            772, 872, 1072, 1372, 1672,
            1872, 2072, 3072, 4072, 5072,
            6072, 8072, 10072, 12072, 14072,
            16072, 16572, 19072, 21072, 24072,
            27072, 30072, 33072, 38072, 43072,
            48072, 54072, 57072, 60072, 64072,
            68072, 84072, 100072, 115072, 131072
    };

    public static final int[] VOLUME_AVIN_DAC_BACKUP = {
            0,
            272, 672, 800, 1072, 2072,
            2572, 3072, 4072, 6072, 8072,
            10072, 13072, 18072, 23072, 29072,
            36072, 47072, 58072, 70072, 82072,
            93072, 104072, 115072, 127072, 145072,
            163072, 181072, 198072, 230072, 262072,
            294072, 326072, 344072, 362072, 380072,
            399072, 499072, 599072, 699072, 799072
    };

}
