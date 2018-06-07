package com.yecon.canbus.info;

public class CANBusConstants {
	// 功能定义
	public static final String YECON_CANBUS = "yecon_canbus";
	public static final String YECON_CANBUS_TYPE = "yecon_canbus_type";
	
	/**
	 * CANBus测试功能
	 */
	public static final boolean CANBUS_TEST_FUNC = false;

	// 冷暖档位是否有中心档位
	public static final boolean TEMPERATURE_HAS_CENTER_GEAR = true;

	// 消息ID定义
	public static final int MSG_UPDATE_UI = 1;
	public static final int MSG_EXIT_ACTIVITY = 2;

	public static final int MSG_TEST_TEMPERATURE = 10;

	// 延时宏定义
	public static final int ACTIVITY_AUTO_FINISH_DELAY_TIME = 5000;

	public static final int TEST_TEMPERATURE_DELAY_TIME = 1000;

	/**
	 * CANBus类型定义
	 */
	public static final int CANBUS_TYPE_UNKOWN = 0x00; // 未知
	public static final int CANBUS_TYPE_VW = 0x01; // 大众
	public static final int CANBUS_TYPE_HONDA = 0x02; // 本田
	public static final int CANBUS_TYPE_PEUGEOT = 0x03; // 东风标致
	public static final int CANBUS_TYPE_HONDA_CAMRY = 0x04; // 丰田RAV4
	public static final int CANBUS_TYPE_HYUNDAI = 0x05; // 韩系现代
	public static final int CANBUS_TYPE_MAZDA = 0x06; // 马自达
	public static final int CANBUS_TYPE_FORD = 0x07; // 福特
	public static final int CANBUS_TYPE_GM = 0x08; // 通用
	public static final int CANBUS_TYPE_GC7 = 0x09; // 吉利GC7
	public static final int CANBUS_TYPE_VW_GOLF7 = 0x10; // 大众GOLF7

	// 冷暖档位
	/**
	 * 如果有中心档位，则冷暖档位要在MAX_STALLS基础上加1
	 */
	public static final int MAX_STALLS = 16;
	public static final int HALF_MAX_STALLS = MAX_STALLS / 2;

	// 风量单位
	public static final int MAX_WIND_SPEED = 7;

	// 温度单位
	public static final int TEMP_UNIT_C = 0;
	public static final int TEMP_UNIT_F = 1;

	/**
	 * CANBus数据状态
	 */
	public static final int CANBUS_DATA_OFF = 0;
	public static final int CANBUS_DATA_ON = 1;

}
