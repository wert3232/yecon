
package com.yecon.yeconfactorytesting;

public class Constants {
    /**
     * 功能项测试开关
     */
    // 测试carlife开关
    public static final boolean NEED_TEST_CARLIFE = true;

    // 测试 wifi开关
    public static final boolean NEED_TEST_WIFI = true;

    // wifi测试模式： 只需要获取热点列表
    public static final boolean WIFI_TEST_HOT_LIST = false;

    // wifi测试模式：1、只需要连接上热点；2、连接某个固定热点，获取网页数据
    public static final boolean WIFI_TEST_CONNECT = true;

    // BT测试连接某个固定机，进行通话测试，因此可以把录音测试屏蔽
    public static final boolean BT_TEST_CALL = true;

    /**
     * 通用宏定义
     */
    public static final int THREAD_POOL_MAX_NUM = 5;

    public static final int DEFAULT_VOLUME = 15;

    // 如果某一项测试失败，重复测试的次数
    public static final int REPEAT_TEST_COUNT = 3;

    public static final int GPS_MIN_SNR_VALUE = 30;
    public static final int GPS_MAX_SNR_VALUE = 70;

    public static final int REC_STOP = 0;
    public static final int REC_START = 1;

    // AVIN音频默认音量大小
    public static final int VOLUME_DEFAULT_VALUE = 1000;

    public static final String WIFI_DEFAULT_CONNECT_SSID = "test03";
    public static final String WIFI_DEFAULT_CONNECT_PASSWORD = "yanfayibu!2223";

    /**
     * 内置存储共享设备路径
     */
    // public static final String SDCARD_ROOT_PATH = Environment
    // .getExternalStorageDirectory().getPath();

    public static final String EXT_SDCARD1_PATH = "/mnt/ext_sdcard1";
    public static final String EXT_SDCARD2_PATH = "/mnt/ext_sdcard2";
    public static final String UDISK1_PATH = "/mnt/udisk1";
    public static final String UDISK2_PATH = "/mnt/udisk2";
    public static final String UDISK3_PATH = "/mnt/udisk3";
    public static final String UDISK4_PATH = "/mnt/udisk4";
    public static final String UDISK5_PATH = "/mnt/udisk5";

    public static final String TEST_FILENAME = "/test.txt";
    public static final String TEST_FILE_CONTENTS = "test file";

    public static final String RECORD_FILENAME = "/mnt/sdcard/rec.pcm";

    public static final String FACTORY_CONFIG_FILENAME = EXT_SDCARD1_PATH + "/config.xml";

    /**
     * 测试类型
     */
    public static final int TEST_TYPE_DEFAULT = 1;
    public static final int TEST_TYPE_BT = 2;
    public static final int TEST_TYPE_CARLIFE = 3;

    /**
     * 内存和Flash大小类型宏定义
     */
    public static final int MEM_1G_FLASH_8G = 1;
    public static final int MEM_1G_FLASH_16G = 2;
    public static final int MEM_1G_FLASH_32G = 3;
    public static final int MEM_2G_FLASH_8G = 4;
    public static final int MEM_2G_FLASH_16G = 5;
    public static final int MEM_2G_FLASH_32G = 6;

    /**
     * COM测试相关宏定义
     */
    public static final String UART_SOCKET_NAME = "skt_uart";

    public static final int COM3_TEST = 3;
    public static final int COM4_TEST = 4;
    public static final int COM5_TEST = 5;
    public static final int COM6_TEST = 6;

    public static final int COM_COMMAND_BYTES = 6;

    public static final int COM_TEST_COUNT = 3;

    public static final byte[] COM_CMD_TEST = {
            (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66
    };

    public static final byte[] COM_CMD_QUIT = {
            (byte) 0x0F, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0xF0
    };

    /**
     * 延时宏定义
     */
    public static final int REPEAT_TEST_DELAY_TIME = 200;

    public static final int COM_TEST_DELAY_TIME = 1000;
    public static final int AUX_TEST_DELAY_TIME = 1000;
    public static final int DVD_TEST_DELAY_TIME = 1000;
    public static final int DVD_TEST_ERROR_DELAY_TIME = 20000;
    public static final int WIFI_TEST_HOT_DELAY_TIME = 5000;
    public static final int WIFI_TEST_WEB_DELAY_TIME = 2000;
    public static final int WIFI_TEST_ERROR_DELAY_TIME = 15000;
    public static final int GPS_TEST_DELAY_TIME = 10000;
    public static final int REC_TEST_DELAY_TIME = 1500;
    public static final int REC_DURATION_TIME = 5000;
    public static final int GPIO_TEST_DELAY_TIME = 200;
    public static final int READ_SOCKET_DELAY_TIME = 3000;

    /**
     * 消息ID
     */
    public static final int MSG_SDCARD1_ERROR = 0x1001;
    public static final int MSG_SDCARD1_SUCCESS = 0x1002;
    public static final int MSG_SDCARD1_DONE = 0x1003;
    public static final int MSG_SDCARD2_ERROR = 0x1004;
    public static final int MSG_SDCARD2_SUCCESS = 0x1005;
    public static final int MSG_SDCARD2_DONE = 0x1006;

    public static final int MSG_UDISK0_ERROR = 0x2001;
    public static final int MSG_UDISK0_SUCCESS = 0x2002;
    public static final int MSG_UDISK1_ERROR = 0x2003;
    public static final int MSG_UDISK1_SUCCESS = 0x2004;
    public static final int MSG_UDISK_DONE = 0x2005;

    public static final int MSG_COM_ALL_ERROR = 0x3001;
    public static final int MSG_COM_TEST = 0x3002;
    public static final int MSG_COM3_TEST = 0x3003;
    public static final int MSG_COM4_TEST = 0x3004;
    public static final int MSG_COM6_TEST = 0x3005;
    public static final int MSG_COM3_ERROR = 0x3006;
    public static final int MSG_COM3_SUCCESS = 0x3007;
    public static final int MSG_COM4_ERROR = 0x3008;
    public static final int MSG_COM4_SUCCESS = 0x3009;
    public static final int MSG_COM6_ERROR = 0x300A;
    public static final int MSG_COM6_SUCCESS = 0x300B;
    public static final int MSG_COM_DONE = 0x300C;

    public static final int MSG_AUX_NO_INPUT_TEST = 0x4001;
    public static final int MSG_AUX_AUDIO_REPEAT_TEST = 0x4002;

    public static final int MSG_AUX1_LEFT_ERROR = 0x4011;
    public static final int MSG_AUX1_RIGHT_ERROR = 0x4012;
    public static final int MSG_AUX1_VIDEO_ERROR = 0x4013;
    public static final int MSG_AUX1_LEFT_SUCCESS = 0x4014;
    public static final int MSG_AUX1_RIGHT_SUCCESS = 0x4015;
    public static final int MSG_AUX1_VIDEO_SUCCESS = 0x4016;
    public static final int MSG_AUX1_DONE = 0x4017;

    public static final int MSG_AUX2_LEFT_ERROR = 0x4021;
    public static final int MSG_AUX2_RIGHT_ERROR = 0x4022;
    public static final int MSG_AUX2_VIDEO_ERROR = 0x4023;
    public static final int MSG_AUX2_LEFT_SUCCESS = 0x4024;
    public static final int MSG_AUX2_RIGHT_SUCCESS = 0x4025;
    public static final int MSG_AUX2_VIDEO_SUCCESS = 0x4026;
    public static final int MSG_AUX2_DONE = 0x4027;

    public static final int MSG_AUX3_LEFT_ERROR = 0x4031;
    public static final int MSG_AUX3_RIGHT_ERROR = 0x4032;
    public static final int MSG_AUX3_VIDEO_ERROR = 0x4033;
    public static final int MSG_AUX3_LEFT_SUCCESS = 0x4034;
    public static final int MSG_AUX3_RIGHT_SUCCESS = 0x4035;
    public static final int MSG_AUX3_VIDEO_SUCCESS = 0x4036;
    public static final int MSG_AUX3_DONE = 0x4037;

    public static final int MSG_AUX4_LEFT_ERROR = 0x4041;
    public static final int MSG_AUX4_RIGHT_ERROR = 0x4042;
    public static final int MSG_AUX4_VIDEO_ERROR = 0x4043;
    public static final int MSG_AUX4_LEFT_SUCCESS = 0x4044;
    public static final int MSG_AUX4_RIGHT_SUCCESS = 0x4045;
    public static final int MSG_AUX4_VIDEO_SUCCESS = 0x4046;
    public static final int MSG_AUX4_DONE = 0x4047;

    public static final int MSG_AUX5_LEFT_ERROR = 0x4051;
    public static final int MSG_AUX5_RIGHT_ERROR = 0x4052;
    public static final int MSG_AUX5_VIDEO_ERROR = 0x4053;
    public static final int MSG_AUX5_LEFT_SUCCESS = 0x4054;
    public static final int MSG_AUX5_RIGHT_SUCCESS = 0x4055;
    public static final int MSG_AUX5_VIDEO_SUCCESS = 0x4056;
    public static final int MSG_AUX5_DONE = 0x4057;

    public static final int MSG_BT_TEST = 0x5001;
    public static final int MSG_BT_ERROR = 0x5002;
    public static final int MSG_BT_SUCCESS = 0x5003;
    public static final int MSG_BT_DONE = 0x5004;
    public static final int MSG_DVD_TEST = 0x5005;
    public static final int MSG_DVD_ERROR = 0x5006;
    public static final int MSG_DVD_SUCCESS = 0x5007;
    public static final int MSG_DVD_DONE = 0x5008;
    public static final int MSG_WIFI_TEST = 0x5009;
    public static final int MSG_WIFI_ERROR = 0x500A;
    public static final int MSG_WIFI_SUCCESS = 0x500B;
    public static final int MSG_WIFI_DONE = 0x500C;

    public static final int MSG_ALLAUTOTEST_DONE = 0x6001;

    public static final int MSG_REC_START_REC = 0x7001;
    public static final int MSG_REC_RECORDING = 0x7002;
    public static final int MSG_REC_STOP_REC = 0x7003;
    public static final int MSG_REC_START_PLAY = 0x7004;
    public static final int MSG_REC_STOP_PLAY = 0x7005;

    public static final int MSG_GPIO_START = 0x8001;
    public static final int MSG_GPIO_END = 0x8002;

    public static final int MSG_GPS_TEST = 0x9001;
    public static final int MSG_GPS_ERROR = 0x9002;
    public static final int MSG_GPS_SUCCESS = 0x9003;

}
