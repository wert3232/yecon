package com.can.parser.raise.dz;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import android.os.Message;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CarService;
import com.can.parser.DDef.CarTimeInfo;
import com.can.parser.DDef.ControlEnable;
import com.can.parser.DDef.ConvConsumers;
import com.can.parser.DDef.DrivingData;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.parser.raise.nissan.ReNissanProtocol.MediaType;
import com.can.parser.raise.nissan.ReNissanProtocol.Source;
import com.can.parser.raise.nissan.ReNissanProtocol.TunerBound;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReMQB7Protocol
 * 
 * @function:睿志城高7平台协议
 * @author Kim
 * @Date: 2016-6-4 上午10:13:26
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReMQB7Protocol extends ReProtocol {

	/**
	 * 协议数据类型定义 大众7代平台 ver1.8
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x21; // 空调信息
	public static final byte DATA_TYPE_CAR_INFO = (byte) 0x41; // 车辆信息
	public static final byte DATA_TYPE_BACK_RADAR_INFO = (byte) 0x22; // 后雷达信息
	public static final byte DATA_TYPE_FRONT_RADAR_INFO = (byte) 0x23; // 前雷达信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x24; // 基本信息
	public static final byte DATA_TYPE_PARKASSIST_INFO = (byte) 0x25; // 辅助系统信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x30; // 版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x29; // 方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x20; // 方向盘按键信息
	public static final byte DATA_TYPE_BACK_LIGHT_INFO = (byte) 0x14; // 背光调节
	public static final byte DATA_TYPE_CAR_TIME_INFO = (byte) 0x26; // 车身时间
	public static final byte DATA_TYPE_ENVIRONMENT_TEMP_INFO = (byte) 0x27; // 环境温度
	public static final byte DATA_TYPE_WHELL_CODE = (byte) 0x2F; // 方向盘指令
	public static final byte DATA_TYPE_CAR_STATE_INFO = (byte) 0x40; // 车身状态信息
	public static final byte DATA_TYPE_CONTROL_MAKE = (byte) 0x41; // 中控使能
	public static final byte DATA_TYPE_DRIVING_DATA = (byte) 0x50; // 行驶数据
	public static final byte DATA_TYPE_CONV_CONSUMERS = (byte) 0x62; // conv.consumers
	public static final byte DATA_TYPE_SERVICES = (byte) 0x63; // Services
	public static final byte DATA_TYPE_TPMS = (byte) 0x65; // 胎压信息

	// Host -> Slave
	public static final byte DATA_TYPE_MEINFO_ONELINE = (byte) 0x70; // 媒体信息第一行
	public static final byte DATA_TYPE_MEINFO_SECONDLINE = (byte) 0x71; // 媒体信息第二行
	public static final byte DATA_TYPE_MEINFO_THRIDLINE = (byte) 0x72; // 媒体信息第三行
	public static final byte DATA_TYPE_CAR_INFO_REQUEST = (byte) 0x90; // 请求控制信息
	public static final byte DATA_TYPE_CAR_TIME_SET = (byte) 0xA6; // 设置时间
	public static final byte DATA_TYPE_CAR_SOURCE = (byte) 0xC0; // 源
	public static final byte DATA_TYPE_CAR_ICON = (byte) 0xC1; // ICON
	public static final byte DATA_TYPE_CAR_VOL = (byte) 0xC4; // 音量显示
	public static final byte DATA_TYPE_PHONE_STATE = (byte) 0xC5; // 电话状态
	public static final byte DATA_TYPE_CAR_SET = (byte) 0xC6; // 原车设定控制
	public static final byte DATA_TYPE_PHONE_TX_INFO = (byte) 0xCA; // 电话文本信息
	public static final byte DATA_TYPE_TIME_INFO = (byte) 0xA6;

	// 车辆设置
	public static final byte LANG_SET = (byte) 0x00;
	public static final byte ESC_SYSTEM = (byte) 0x10;
	public static final byte TYRES_SPEED_WARN = (byte) 0x20;
	public static final byte TYRES_SPEED_VAL = (byte) 0x21;
	public static final byte TYRES_SET = (byte) 0x22;
	public static final byte DRIVER_LANE_ASSIST = (byte) 0x30;
	public static final byte DRIVER_ALERT_SYSTEM = (byte) 0x31;
	public static final byte DRIVER_LAST_DIS = (byte) 0x32;
	public static final byte DRIVER_FRONT_ASSIST_ACTIVE = (byte) 0x33;
	public static final byte DRIVER_FRONT_ASSIST_ADVANT = (byte) 0x34;
	public static final byte DRIVER_FRONT_ASSIST_DISPLAY = (byte) 0x35;
	public static final byte DRIVER_ASSISTANCE = (byte) 0x36;
	public static final byte DRIVER_ACC_DRIVE_PRO = (byte) 0x37;
	public static final byte DRIVER_ACC_DIS = (byte) 0x38;
	public static final byte PARK_ACTIVE_AUTO = (byte) 0x40;
	public static final byte PARK_FRONT_VOL = (byte) 0x41;
	public static final byte PARK_FRONT_TONE = (byte) 0x42;
	public static final byte PARK_REAR_VOL = (byte) 0x43;
	public static final byte PARK_REAR_TONE = (byte) 0x44;
	public static final byte LIGHT_SW_ONTIME = (byte) 0x50;
	public static final byte LIGHT_AUTO_HEAD = (byte) 0x51;
	public static final byte LIGHT_LANE_FLASH = (byte) 0x52;
	public static final byte LIGHT_INSTRUMENT = (byte) 0x53;
	public static final byte LIGHT_COME_HOME = (byte) 0x54;
	public static final byte LIGHT_LEAVE_HOME = (byte) 0x55;
	public static final byte LIGHT_TRAVEL_MODE = (byte) 0x56;
	public static final byte LIGHT_DOOR_AMBIENT = (byte) 0x57;
	public static final byte LIGHT_FOOTWELL = (byte) 0x58;
	public static final byte LIGHT_MOTORWAY = (byte) 0x59;
	public static final byte LIGHT_DYMIC = (byte) 0x5A;
	public static final byte WIPER_SYNC_ADJUST = (byte) 0x60;
	public static final byte WIPER_LOWER_REVERSE = (byte) 0x61;
	public static final byte WIPER_AUTO_IN_RAIN = (byte) 0x62;
	public static final byte WIPER_REAR_WIND = (byte) 0x63;
	public static final byte WIPER_FOLD_AWAY = (byte) 0x64;
	public static final byte OPNECLSE_CONV = (byte) 0x70;
	public static final byte OPNECLSE_DOOR_UNLOCK = (byte) 0x71;
	public static final byte OPNECLSE_AUTO_LOCK = (byte) 0x72;

	public static final byte MFD_CURFUEL = (byte) 0x80;
	public static final byte MFD_AVGFUEL = (byte) 0x81;
	public static final byte MFD_CONVS = (byte) 0x82;
	public static final byte MFD_ECO = (byte) 0x83;
	public static final byte MFD_TRAVELTIME = (byte) 0x84;
	public static final byte MFD_DIS = (byte) 0x85;
	public static final byte MFD_AVGSPEED = (byte) 0x86;
	public static final byte MFD_DIGITAL = (byte) 0x87;
	public static final byte MFD_SPEEDWARN = (byte) 0x88;
	public static final byte MFD_OIL_TEMP = (byte) 0x89;
	public static final byte MFD_SINCE_START = (byte) 0x8A;
	public static final byte MFD_LONG_TERM = (byte) 0x8B;

	public static final byte UNIT_DISTANCE = (byte) 0x90;
	public static final byte UNIT_SPEED = (byte) 0x91;
	public static final byte UNIT_TEMP = (byte) 0x92;
	public static final byte UNIT_VOLUME = (byte) 0x93;
	public static final byte UNIT_COMSUMPTION = (byte) 0x94;
	public static final byte UNIT_PRESSURE = (byte) 0x95;

	public static final byte FACTORY_ASSIST = (byte) 0xc1;
	public static final byte FACTORY_PARK = (byte) 0xc2;
	public static final byte FACTORY_LIGHT = (byte) 0xc3;
	public static final byte FACTORY_WIPER = (byte) 0xc4;
	public static final byte FACTORY_OPNECLE = (byte) 0xc5;
	public static final byte FACTORY_MFD = (byte) 0xc6;
	public static final byte FACTORY_ALL = (byte) 0xc7;
	public static final byte DATTIME_LIGHT = (byte) 0xc8;

	// 按键类型
	public class KeyCode {
		public static final byte KEY_CODE_VOL_UP = (byte) 0x01;
		public static final byte KEY_CODE_VOL_DOWN = (byte) 0x02;
		public static final byte KEY_CODE_NEXT = (byte) 0x03;
		public static final byte KEY_CODE_PREV = (byte) 0x04;
		public static final byte KEY_CODE_TEL = (byte) 0x05;
		public static final byte KEY_CODE_MUTE = (byte) 0x06;
		public static final byte KEY_CODE_SRC = (byte) 0x07;
		public static final byte KEY_CODE_MIC = (byte) 0x08;
	}

	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_AIR_INFO & 0xFF);
	}

	@Override
	public int GetCarInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_CAR_STATE_INFO & 0xFF);
	}

	@Override
	public int GetCarInfoRequestCmdId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_BACK_RADAR_INFO & 0xFF);
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_FRONT_RADAR_INFO & 0xFF);
	}

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_BASE_INFO & 0xFF);
	}

	@Override
	public int GetParkAssistCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_PARKASSIST_INFO & 0xFF);
	}

	@Override
	public int GetVersionInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_VERSION_INFO & 0xFF);
	}

	@Override
	public int GetWheelInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_WHEEL_INFO & 0xFF);
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_WHEEL_KEY_INFO & 0xFF);
	}

	@Override
	public int GetBackLightInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_BACK_LIGHT_INFO & 0xFF);
	}

	public int GetCarTimeInfoCmdId() {
		return (int) (DATA_TYPE_CAR_TIME_INFO & 0xFF);
	}

	public int GetCarEnviTempCmdId() {
		return (int) (DATA_TYPE_ENVIRONMENT_TEMP_INFO & 0xFF);
	}

	public int GetControlEnableCmdId() {
		return (int) (DATA_TYPE_CONTROL_MAKE & 0xFF);
	}

	public int GetDrivingDataCmdId() {
		return (int) (DATA_TYPE_DRIVING_DATA & 0xFF);
	}

	public int GetConvConsumersCmdId() {
		return (int) (DATA_TYPE_CONV_CONSUMERS & 0xFF);
	}

	public int GetCarServiceCmdId() {
		return (int) (DATA_TYPE_SERVICES & 0xFF);
	}

	public int GetTpmsInfoCmdId() {
		return (int) (DATA_TYPE_TPMS & 0xFF);
	}

	@Override
	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo) {
		// TODO Auto-generated method stub
		int cursor = 3;

		if (packet[cursor] == 0x00) {

			carInfo.mbylangIndex = packet[cursor + 1];

			if (carInfo.mbylangIndex == 0x17) {
				carInfo.mbylangIndex = 0x01;
			} else {
				carInfo.mbylangIndex = 0x00;
			}

		} else if (packet[cursor] == 0x10) {

			carInfo.mbyEscSysState = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
		} else if (packet[cursor] == 0x20) {

			carInfo.mbySpeedUnit = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			carInfo.mbySpeedWarn = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			carInfo.miSpeedValue = DataConvert.byteToInt(packet[cursor + 2]);

		} else if (packet[cursor] == 0x30) {

			carInfo.mbyLaneAssistEnable = (byte) DataConvert.GetBit(
					packet[cursor + 2], 0);
			carInfo.mbyLaneAssist = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			carInfo.mbyDriverAlertSys = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);

		} else if (packet[cursor] == 0x31) {

			carInfo.mbyLastDistance = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			carInfo.mbyFrontAssistActive = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			carInfo.mbyFrontAssistAdvance = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			carInfo.mbyFrontAssistDisplay = (byte) DataConvert.GetBit(
					packet[cursor + 1], 3);
			carInfo.mbyAccDriveProgram = (byte) (packet[cursor + 2] & 0x0F);
			carInfo.mbyAccDistance = (byte) ((packet[cursor + 2] >> 4) & 0x0F);

		} else if (packet[cursor] == 0x40) {

			carInfo.mbyActivateAutoMatically = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			carInfo.mbyFrontVolume = (byte) (packet[cursor + 2] & 0x0F);
			carInfo.mbyFrontToneSetting = (byte) ((packet[cursor + 2] >> 4) & 0x0F);
			carInfo.mbyRearVolume = (byte) (packet[cursor + 3] & 0x0F);
			carInfo.mbyRearToneSetting = (byte) ((packet[cursor + 3] >> 4) & 0x0F);

		} else if (packet[cursor] == 0x50) {

			carInfo.mbySwicthOnTime = (byte) (packet[cursor + 1] & 0x0F);
			carInfo.mbyAutoHeadlightCtrlInRain = (byte) DataConvert.GetBit(
					packet[cursor + 1], 4);
			carInfo.mbyLaneChangeFlash = (byte) DataConvert.GetBit(
					packet[cursor + 1], 5);
			carInfo.mbyDaytimeRunlight = (byte) DataConvert.GetBit(
					packet[cursor + 1], 6);
			carInfo.mbyInstrumentSwlight = packet[cursor + 2];
			carInfo.mbyComeHomeFunction = packet[cursor + 3];
			carInfo.mbyLeaveHomeFunction = packet[cursor + 4];

		} else if (packet[cursor] == 0x51) {

			carInfo.mbyTravlMode = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			carInfo.mbyDoorAmbientlight = packet[cursor + 2];
			carInfo.mbyFootwelllight = packet[cursor + 3];
			carInfo.mbyDynclightAssist = (byte) DataConvert.GetBit(
					packet[cursor + 4], 1);
			carInfo.mbyDyncBiglightAssist = (byte) DataConvert.GetBit(
					packet[cursor + 4], 0);

		} else if (packet[cursor] == 0x60) {

			carInfo.mbySynchronousAdjust = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			carInfo.mbyLowerWhileReverse = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			carInfo.mbyAutomaticWiperInRain = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			carInfo.mbyRearWindWiperIngrear = (byte) DataConvert.GetBit(
					packet[cursor + 1], 3);
			carInfo.mbyFoldAwayAfterParking = (byte) DataConvert.GetBit(
					packet[cursor + 2], 0);

		} else if (packet[cursor] == 0x70) {

			carInfo.mbyConvOpen = (byte) (packet[cursor + 1] & 0x0F);
			carInfo.mbyDoorUnlock = (byte) ((packet[cursor + 1] >> 4) & 0x0F);
			carInfo.mbyAutomaticlock = (byte) DataConvert.GetBit(
					packet[cursor + 2], 0);
			carInfo.mbyAcousticConfirmation = (byte) DataConvert.GetBit(
					packet[cursor + 3], 0);

		} else if (packet[cursor] == 0x80) {

			carInfo.mbyCurrentConsumption = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			carInfo.mbyAverageConsumption = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			carInfo.mbyConvenienceConsumers = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			carInfo.mbyEcoTips = (byte) DataConvert.GetBit(packet[cursor + 1],
					3);
			carInfo.mbyTravellingTime = (byte) DataConvert.GetBit(
					packet[cursor + 1], 4);
			carInfo.mbyDistanceTraveled = (byte) DataConvert.GetBit(
					packet[cursor + 1], 5);
			carInfo.mbyAverageSpeed = (byte) DataConvert.GetBit(
					packet[cursor + 1], 6);
			carInfo.mbyDigitalSpeedDisplay = (byte) DataConvert.GetBit(
					packet[cursor + 1], 7);
			carInfo.mbySpeedWarning = (byte) DataConvert.GetBit(
					packet[cursor + 2], 0);
			carInfo.mbyOilTemperature = (byte) DataConvert.GetBit(
					packet[cursor + 2], 1);

		} else if (packet[cursor] == 0x90) {

			carInfo.mbyDistanceUnit = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			carInfo.mbySpeedUnint = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			carInfo.mbyTemperatureUnit = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			carInfo.mbyVolumeUnit = (byte) ((packet[cursor + 1] >> 4) & 0x0F);
			carInfo.mbyConsumptionUnit = (byte) (packet[cursor + 2] & 0x0F);
			carInfo.mbyPressureUnit = (byte) ((packet[cursor + 2] >> 4) & 0x0F);
		}

		return carInfo;
	}

	public ControlEnable parseControlEnable(byte[] packet, ControlEnable aEnable) {

		int cursor = 3; // 跳过2E、Data Type、Length字段

		if (packet[cursor] == 0x10) {

			aEnable.mbyEscSysStateEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
		} else if (packet[cursor] == 0x20) {

			aEnable.mbySpeedWarnEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);

		} else if (packet[cursor] == 0x30) {

			aEnable.mbyLaneAssistEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			aEnable.mbyLaneAssistEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbyDriverAlertSysEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);

		} else if (packet[cursor] == 0x31) {

			aEnable.mbyLastDistanceEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbyFrontAssistActiveEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			aEnable.mbyFrontAssistAdvanceEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			aEnable.mbyFrontAssistDisplayEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 3);
			aEnable.mbyAccDriveProgramEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 4);
			aEnable.mbyAccDistanceEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 5);

		} else if (packet[cursor] == 0x40) {

			aEnable.mbyActivateAutoMaticallyEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbyFrontVolumeEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			aEnable.mbyFrontToneSettingEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			aEnable.mbyRearVolumeEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 3);
			aEnable.mbyRearToneSettingEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 4);

		} else if (packet[cursor] == 0x50) {

			aEnable.mbySwicthOnTimeEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbyAutoHeadlightCtrlInRainEnable = (byte) DataConvert
					.GetBit(packet[cursor + 1], 1);
			aEnable.mbyLaneChangeFlashEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			aEnable.mbyDaytimeRunlightEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 3);
			aEnable.mbyInstrumentSwlightEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 4);
			aEnable.mbyComeHomeFunctionEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 5);
			aEnable.mbyLeaveHomeFunctionEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 6);

		} else if (packet[cursor] == 0x51) {

			aEnable.mbyTravlModeEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbyDoorAmbientlightEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			aEnable.mbyFootwelllightEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);

		} else if (packet[cursor] == 0x60) {

			aEnable.mbySynchronousAdjustEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbyLowerWhileReverseEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			aEnable.mbyAutomaticWiperInRainEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			aEnable.mbyRearWindWiperIngrearEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 3);
			aEnable.mbyFoldAwayAfterParkingEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 4);

		} else if (packet[cursor] == 0x70) {

			aEnable.mbyConvOpenEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbyDoorUnlockEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			aEnable.mbyAutomaticlockEnable = (byte) DataConvert.GetBit(
					packet[cursor + 2], 2);

		} else if (packet[cursor] == 0x80) {

			aEnable.mbyCurrentConsumptionEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbyAverageConsumptionEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			aEnable.mbyConvenienceConsumersEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			aEnable.mbyEcoTipsEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 3);
			aEnable.mbyTravellingTimeEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 4);
			aEnable.mbyDistanceTraveledEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 5);
			aEnable.mbyAverageSpeedEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 6);
			aEnable.mbyDigitalSpeedDisplayEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 7);
			aEnable.mbySpeedWarningEnable = (byte) DataConvert.GetBit(
					packet[cursor + 2], 0);
			aEnable.mbyOilTemperatureEnable = (byte) DataConvert.GetBit(
					packet[cursor + 2], 1);

		} else if (packet[cursor] == 0x90) {

			aEnable.mbyDistanceUnitEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			aEnable.mbySpeedUnintEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 1);
			aEnable.mbyTemperatureUnitEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 2);
			aEnable.mbyVolumeUnitEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 3);
			aEnable.mbyConsumptionUnitEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 4);
			aEnable.mbyPressureUnitEnable = (byte) DataConvert.GetBit(
					packet[cursor + 1], 5);
		}

		return aEnable;
	}

	public DrivingData parseDrivingData(byte[] packet, DrivingData data) {

		int cursor = 3; // 跳过2E、Data Type、Length字段

		if (packet[cursor] == 0x10) {

			data.miRange = DataConvert.byte2Short(packet, cursor + 2);
			data.mbyRangeUnit = (byte) DataConvert
					.GetBit(packet[cursor + 1], 0);
		} else if (packet[cursor] == 0x20) {

			data.mbyDistanceUnit = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			data.miDistanceSinceStart = (DataConvert
					.byteToInt(packet[cursor + 2])
					+ (DataConvert.byteToInt(packet[cursor + 3]) << 8)
					+ (DataConvert.byteToInt(packet[cursor + 4]) << 16) + (DataConvert
					.byteToInt(packet[cursor + 5]) << 24)) / 10;
		} else if (packet[cursor] == 0x21) {

			data.miDistanceSinceRefuel = (DataConvert
					.byteToInt(packet[cursor + 2])
					+ (DataConvert.byteToInt(packet[cursor + 3]) << 8)
					+ (DataConvert.byteToInt(packet[cursor + 4]) << 16) + (DataConvert
					.byteToInt(packet[cursor + 5]) << 24)) / 10;
		} else if (packet[cursor] == 0x22) {
			data.miDistanceLongTerm = (DataConvert
					.byteToInt(packet[cursor + 2])
					+ (DataConvert.byteToInt(packet[cursor + 3]) << 8)
					+ (DataConvert.byteToInt(packet[cursor + 4]) << 16) + (DataConvert
					.byteToInt(packet[cursor + 5]) << 24)) / 10;
		} else if (packet[cursor] == 0x30) {

			data.mbyConsumptionUnit = (byte) (packet[cursor + 1] & 0x03);
			data.mfAvgConsSinceStart = (DataConvert
					.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) / 10;
		} else if (packet[cursor] == 0x31) {

			data.mfAvgConsSinceRefuel = (DataConvert
					.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) / 10;
		} else if (packet[cursor] == 0x32) {

			data.mfAvgConsLongTerm = (DataConvert.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) / 10;
		} else if (packet[cursor] == 0x40) {

			data.mbyAvgSpeedUnit = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);

			data.mfAvgSpeedSinceStart = (DataConvert
					.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) / 10;
		} else if (packet[cursor] == 0x41) {

			data.mfAvgSpeedSinceRefuel = (DataConvert
					.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) / 10;
		} else if (packet[cursor] == 0x42) {

			data.mfAvgSpeedLongTerm = (DataConvert
					.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) / 10;
		} else if (packet[cursor] == 0x50) {

			data.miTravellTimeSinceStart = DataConvert
					.byteToInt(packet[cursor + 1])
					+ (DataConvert.byteToInt(packet[cursor + 2]) << 8)
					+ (DataConvert.byteToInt(packet[cursor + 3]) << 16);
		} else if (packet[cursor] == 0x51) {

			data.miTravellTimeSinceRefuel = DataConvert
					.byteToInt(packet[cursor + 1])
					+ (DataConvert.byteToInt(packet[cursor + 2]) << 8)
					+ (DataConvert.byteToInt(packet[cursor + 3]) << 16);
		} else if (packet[cursor] == 0x52) {

			data.miTravellTimeLongTerm = DataConvert
					.byteToInt(packet[cursor + 1])
					+ (DataConvert.byteToInt(packet[cursor + 2]) << 8)
					+ (DataConvert.byteToInt(packet[cursor + 3]) << 16);
		} else if (packet[cursor] == 0x60) {

			data.mbyConvConsumersUnit = (byte) DataConvert.GetBit(
					packet[cursor + 1], 0);
			data.miConvConsumers = DataConvert.byteToInt(packet[cursor + 2])
					+ DataConvert.byteToInt(packet[cursor + 3]) * 256;
		} else if (packet[cursor] == 0x61) {

			data.mbyInstantFuelUnit = (byte) (packet[cursor + 1] & 0x03);
			data.mbyInstantFuel = (DataConvert.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) / 10;
		}

		return data;
	}

	public ConvConsumers parseConvConsumers(byte[] packet,
			ConvConsumers consumers) {

		int cursor = 3; // 跳过2E、Data Type、Length字段

		consumers.mbyConvNum = packet[cursor];

		if (consumers.mbyConvNum < 3) {

		} else {
			consumers.mbyConvConsumers = new byte[3];
			for (int i = 0; i < 3; i++) {
				consumers.mbyConvConsumers[i] = packet[cursor + 1 + i];
			}
		}

		return consumers;
	}

	public CarService parseCarService(byte[] packet, CarService carService) {

		int cursor = 3;

		if (packet[cursor] == 0x00) {

			byte[] VehicleNo = new byte[packet.length - 5];
			System.arraycopy(packet, cursor + 1, VehicleNo, 0,
					packet.length - 5); 

			try {
				carService.mStrVehicleNo = new String(VehicleNo, "GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (packet[cursor] == 0x10) {

			carService.mbyVolksWagenDaysType = (byte) (DataConvert
					.byteToInt(packet[cursor + 1]) & 0x0F);
			carService.miVolksWagenDays = DataConvert
					.byteToInt(packet[cursor + 2])
					+ DataConvert.byteToInt(packet[cursor + 3]) * 256;
		} else if (packet[cursor] == 0x11) {

			carService.mbyVolksWagenDisUnit = (byte) ((packet[cursor + 1] >> 4) & 0x0F);
			carService.mbyVolksWagenDisType = (byte) (packet[cursor + 1] & 0x0F);
			carService.miVolksWagenDistance = (DataConvert
					.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) * 100;
		} else if (packet[cursor] == 0x20) {

			carService.mbyOilChangeSerivesDayType = (byte) (packet[cursor + 1] & 0x0F);
			carService.miOilChangeSerivesDays = packet[cursor + 2]
					+ packet[cursor + 3] * 256;
		} else if (packet[cursor] == 0x21) {

			carService.mbyOilChangeServiceDisUnit = (byte) ((packet[cursor + 1] >> 4) & 0x0F);
			carService.mbyOilChangeServiceDisType = (byte) (packet[cursor + 1] & 0x0F);
			carService.miOilChangeServiceDistance = (DataConvert
					.byteToInt(packet[cursor + 2]) + DataConvert
					.byteToInt(packet[cursor + 3]) * 256) * 100;
		}

		return carService;
	}

	public TPMSInfo parseTpmsInfo(byte[] packet, TPMSInfo tpmsInfo) {

		int cursor = 3; // 跳过2E、Data Type、Length字段

		tpmsInfo.mFLTirePressure = packet[cursor]; // 前左轮胎气压
		tpmsInfo.mFRTirePressure = packet[cursor + 1]; // 前右轮胎气压
		tpmsInfo.mBLTirePressure = packet[cursor + 2]; // 后左轮胎气压
		tpmsInfo.mBRTirePressure = packet[cursor + 3]; // 后右轮胎气压

		return tpmsInfo;
	}

	private byte[] mbyAirInfo = null;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过2E、Data Type、Length字段

		// Data 0
		byte airData0 = (byte) packet[cursor];
		airInfo.mAiron = (byte) ((airData0 >> 7) & 0x1);
		airInfo.mAcState = (byte) ((airData0 >> 6) & 0x1);
		airInfo.mCircleState = (byte) ((airData0 >> 5) & 0x1);
		airInfo.mAutoLight1 = (byte) ((airData0 >> 4) & 0x1);
		airInfo.mAutoLight2 = (byte) ((airData0 >> 3) & 0x1);
		airInfo.mDaulLight = (byte) ((airData0 >> 2) & 0x1);
		airInfo.mMaxForntLight = (byte) ((airData0 >> 1) & 0x1);
		airInfo.mRearLight = (byte) ((airData0 >> 0) & 0x1);

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		airInfo.mUpwardWind = (byte) ((airData1 >> 7) & 0x1);
		airInfo.mParallelWind = (byte) ((airData1 >> 6) & 0x1);
		airInfo.mDowmWind = (byte) ((airData1) >> 5 & 0x1);
		airInfo.mDisplay = (byte) ((airData1) >> 4 & 0x1);
		airInfo.mWindRate = (byte) ((airData1) & 0x07);

		// Data 2
		airInfo.mMinTemp = 0;
		airInfo.mMaxTemp = (float) 31;
		airInfo.mTempUnit = (byte) (packet[7] & 0x01);

		cursor += 1;
		byte airData2 = (byte) packet[cursor];

		if (airInfo.mTempUnit == 0) {

			if (airData2 == 0x00 || airData2 == 0x1F) {
				airInfo.mLeftTemp = airData2;
			} else {
				airInfo.mLeftTemp = (float) (16 + (airData2 - 1) * 0.5);
			}
		} else {
			airInfo.mLeftTemp = (float) (60 + (airData2 - 1));
			
			airInfo.mMinTemp = 59;
			airInfo.mMaxTemp = (float) 90;
		}

		// Data 3
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		if (airInfo.mTempUnit == 0) {

			if (airData3 == 0x00 || airData3 == 0x1F) {
				airInfo.mRightTemp = airData3;
			} else {
				airInfo.mRightTemp = (float) (16 + (airData3 - 1) * 0.5);
			}
		} else {
			airInfo.mRightTemp = (float) (60 + (airData3 - 1));
			
			airInfo.mMinTemp = 59;
			airInfo.mMaxTemp = (float) 90;
		}

		// Data 4
		cursor += 1;
		byte airData4 = (byte) packet[cursor];
		airInfo.mFWDefogger = (byte) ((airData4 >> 7) & 0x1);
		airInfo.mRearLight = (byte) ((airData4 >> 6) & 0x1);
		airInfo.mAqsInCircle = (byte) ((airData4 >> 5) & 0x1);

		// Data 5
		cursor += 1;
		byte airData5 = (byte) packet[cursor];
		airInfo.mLeftHotSeatTemp = (byte) ((airData5 >> 4) & 0x7);
		airInfo.mRightHotSeatTemp = (byte) ((airData5 >> 0) & 0x7);

		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!mbyAirInfo.equals(packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		// TODO Auto-generated method stub
		int cursor = 0;
		int iRadarDis = 0;
		byte DataType = (byte) packet[cursor + 1];
		cursor += 3; // 跳过 2E、Data Type、Length字段

		if (DataType == DATA_TYPE_BACK_RADAR_INFO) {

			iRadarDis = packet[cursor] & 0xFF;

			if (0x00 <= iRadarDis && iRadarDis <= 0x3c) {
				radarInfo.mBackLeftDis = (byte) (iRadarDis / 6);
			} else {
				radarInfo.mBackLeftDis = 0;
			}

			iRadarDis = packet[cursor + 3] & 0xFF;

			if (0x00 <= iRadarDis && iRadarDis <= 0x3c) {
				radarInfo.mBackRightDis = (byte) (iRadarDis / 6);
			} else {
				radarInfo.mBackRightDis = 0;
			}

			iRadarDis = packet[cursor + 1] & 0xFF;

			if (0x00 <= iRadarDis && iRadarDis <= 0xA5) {
				radarInfo.mBackLeftCenterDis = (byte) (iRadarDis / 9);
			} else {
				radarInfo.mBackLeftCenterDis = 0;
			}

			iRadarDis = packet[cursor + 2] & 0xFF;

			if (0x00 <= iRadarDis && iRadarDis <= 0xA5) {
				radarInfo.mBackRightCenterDis = (byte) (iRadarDis / 9);
			} else {
				radarInfo.mBackRightCenterDis = 0;
			}
		} else if (DataType == DATA_TYPE_FRONT_RADAR_INFO) {

			iRadarDis = packet[cursor] & 0xFF;

			if (0x00 <= iRadarDis && iRadarDis <= 0x3c) {
				radarInfo.mFrontLeftDis = (byte) (iRadarDis / 6);
			} else {
				radarInfo.mFrontLeftDis = 0;
			}

			iRadarDis = packet[cursor + 3] & 0xFF;

			if (0x00 <= iRadarDis && iRadarDis <= 0x3c) {
				radarInfo.mFrontRightDis = (byte) (iRadarDis / 6);
			} else {
				radarInfo.mFrontRightDis = 0;
			}

			iRadarDis = packet[cursor + 1] & 0xFF;

			if (0x00 <= iRadarDis && iRadarDis <= 0x78) {
				radarInfo.mFrontLeftCenterDis = (byte) (iRadarDis / 7);
			} else {
				radarInfo.mFrontLeftCenterDis = 0;
			}

			iRadarDis = packet[cursor + 2] & 0xFF;

			if (0x00 <= iRadarDis && iRadarDis <= 0x78) {
				radarInfo.mFrontRightCenterDis = (byte) (iRadarDis / 7);
			} else {
				radarInfo.mFrontRightCenterDis = 0;
			}
		}

		radarInfo.mbyRightShowType = 1;

		return radarInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		// Data0
		byte baseData0 = (byte) packet[cursor];

		if (((baseData0 & 0x01) == 1)) {
			baseInfo.mFrontBoxDoor = (byte) ((baseData0 >> 2) & 0x1);
			baseInfo.mTailBoxDoor = (byte) ((baseData0 >> 3) & 0x1);
			baseInfo.mRightBackDoor = (byte) ((baseData0 >> 5) & 0x1);
			baseInfo.mLeftBackDoor = (byte) ((baseData0 >> 4) & 0x1);
			baseInfo.mRightFrontDoor = (byte) ((baseData0 >> 7) & 0x1);
			baseInfo.mLeftFrontDoor = (byte) ((baseData0 >> 6) & 0x1);
		}

		// Data1
		byte baseData1 = (byte) packet[cursor + 1];

		baseInfo.mLightState = (byte) ((baseData1 >> 2) & 0x1);
		baseInfo.mPStopBlockState = (byte) ((baseData1 >> 1) & 0x1);
		baseInfo.mBackCarState = (byte) ((baseData1 >> 0) & 0x1);

		return baseInfo;
	}

	@Override
	public ParkAssistInfo parseParkAssistantInfo(byte[] packet) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		ParkAssistInfo parkAssistInfo = new ParkAssistInfo();

		// Data0
		byte baseData0 = (byte) packet[cursor];
		parkAssistInfo.mParkSystemState = (byte) ((baseData0 >> 1) & 0x1);
		parkAssistInfo.mRadarSoundState = (byte) ((baseData0 >> 0) & 0x1);

		return parkAssistInfo;
	}

	@Override
	public VerInfo parseVersionInfo(byte[] packet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mEps = DataConvert.byte2Short(packet, cursor);
		wheelInfo.mEps = (wheelInfo.mEps * 45 / 10000);

		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = packet[cursor + 1];

		return TranslateKey(wheelInfo);
	}

	@Override
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		backLightInfo.mLight = DataConvert.byteToInt((byte) packet[cursor]);
		return backLightInfo;
	}

	public CarTimeInfo parseCarTimeInfo(byte[] packet, CarTimeInfo objTimeInfo) {

		int cursor = 3;

		objTimeInfo.miYear = 2000 + packet[cursor];
		cursor += 1;
		objTimeInfo.mbyMonth = packet[cursor];
		cursor += 1;
		objTimeInfo.mbyDay = packet[cursor];
		cursor += 1;
		objTimeInfo.mby24Mode = (byte) DataConvert.GetBit(packet[cursor], 7);
		objTimeInfo.mbyHour = (byte) (packet[cursor] & 0x7F);
		cursor += 1;
		objTimeInfo.mbyMinute = packet[cursor];
		cursor += 1;
		objTimeInfo.mbySecond = packet[cursor];
		cursor += 1;
		objTimeInfo.mbyFormat = packet[cursor];

		return objTimeInfo;
	}

	public OutTemputerInfo parseOutTempInfo(byte[] packet, OutTemputerInfo eTemp) {

		int cursor = 3;
		byte byUnit = (byte) (packet[cursor] & 0x01);

		eTemp.mbEnable = false;
		eTemp.mbFEndble = false;

		if (byUnit == 0) {
			eTemp.mbEnable = true;
		} else {
			eTemp.mbFEndble = true;
		}

		float fTemp = 0;
		boolean bNegative = (DataConvert.GetBit(packet[cursor + 1], 7) == 1) ? true
				: false;

		byte[] bydata = new byte[2];
		bydata[0] = packet[cursor + 1];
		bydata[1] = packet[cursor + 2];

		if (bNegative) {
			fTemp = -(DataConvert.byte2Short(bydata, 0) / 10);
		} else {
			fTemp = DataConvert.byte2Short(bydata, 0) / 10;
		}

		if (eTemp.mbEnable) {
			eTemp.mOutCTemp = fTemp;
		} else if (eTemp.mbFEndble) {
			eTemp.mOutFTemp = fTemp;
		}

		return eTemp;
	}

	private String mstrKeyCode = null;

	public WheelKeyInfo TranslateKey(WheelKeyInfo wheelKeyInfo) {
		// TODO Auto-generated method stub
		switch (wheelKeyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_VOL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_MIC:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_PREV:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_NEXT:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_SRC:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_TEL:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		default:
			break;
		}

		if (wheelKeyInfo.mKeyCode != 0) {
			mstrKeyCode = wheelKeyInfo.mstrKeyCode;
		}
		if (wheelKeyInfo.mKeyCode == 0) {
			wheelKeyInfo.mstrKeyCode = mstrKeyCode;
			mstrKeyCode = null;
		}

		return wheelKeyInfo;
	}

	@Override
	public byte TranslateSource(int iSource) {
		// TODO Auto-generated method stub
		byte bySource = Source.OFF;

		switch (iSource) {
		case DDef.SOURCE_DEF.Src_radio:
			bySource = Source.TUNER;
			break;
		case DDef.SOURCE_DEF.Src_music:
			bySource = Source.USB;
			break;
		case DDef.SOURCE_DEF.Src_video:
			bySource = Source.USB;
			break;
		case DDef.SOURCE_DEF.Src_bluetooth:
			bySource = Source.PHONE;
			break;
		case DDef.SOURCE_DEF.Src_dvd:
			bySource = Source.DISC;
			break;
		case DDef.SOURCE_DEF.Src_dvr:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_dtv:
			bySource = Source.TV;
			break;
		case DDef.SOURCE_DEF.Src_atv:
			bySource = Source.TV;
			break;
		case DDef.SOURCE_DEF.Src_avin:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_ipod:
			bySource = Source.IPOD;
			break;
		case DDef.SOURCE_DEF.Src_phonelink:
			bySource = Source.OTHER;
			break;
		case DDef.SOURCE_DEF.Src_backcar:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_bt_phone:
			bySource = Source.PHONE;
			break;
		case DDef.SOURCE_DEF.Src_photo:
			bySource = Source.USB;
			break;
		default:
			bySource = Source.OTHER;
			break;
		}

		return bySource;
	}

	private byte getMediaType(byte bySource) {

		byte byMeType = MediaType.OFF;

		switch (bySource) {
		case Source.TUNER:
			byMeType = MediaType.TUNER;
			break;
		case Source.AUX:
			byMeType = MediaType.AUX;
			break;
		case Source.DISC:
			byMeType = MediaType.EAM;
			break;
		case Source.IPOD:
			byMeType = MediaType.IPOD;
			break;
		case Source.OTHER:
			byMeType = MediaType.AUX;
			break;
		case Source.PHONE:
			byMeType = MediaType.PHONE;
			break;
		case Source.PHONE_A2DP:
			byMeType = MediaType.EAM;
			break;
		case Source.TV:
			byMeType = MediaType.AUX;
			break;
		case Source.USB:
			byMeType = MediaType.EAM;
			break;
		default:
			byMeType = MediaType.AUX;
			break;
		}

		return byMeType;
	}

	public byte TranslateBand(byte byBand) {
		byte bybandEx = TunerBound.TUNER_FM;

		switch (byBand) {
		case 0x00:
			bybandEx = TunerBound.TUNER_FM1;
			break;
		case 0x01:
			bybandEx = TunerBound.TUNER_FM2;
			break;
		case 0x02:
			bybandEx = TunerBound.TUNER_FM3;
			break;
		case 0x03:
			bybandEx = TunerBound.TUNER_AM1;
			break;
		case 0x04:
			bybandEx = TunerBound.TUNER_AM2;
			break;
		default:
			break;
		}

		return bybandEx;
	}

	@Override
	public Message getBtInfo(int iType, int iValue, MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		byte[] bydata = null;

		if (iType == 0x01) {
			switch (mediaInfo.getPhonestate()) {
			case DDef.PHONE_STATE_INCOMING:
			case DDef.PHONE_STATE_OUTGOING:
			case DDef.PHONE_STATE_SPEAKING:
				String strPhoneNum = mediaInfo.getPhoneNumber();
				if (strPhoneNum != null) {
					if (strPhoneNum.isEmpty()) {
						bydata = new byte[8];
						bydata[0] = 0x01;
						bydata[1] = 0x01;
					} else {
						bydata = new byte[strPhoneNum.length() + 2];

						bydata[0] = 0x01;
						bydata[1] = 0x01;

						for (int i = 2; i < strPhoneNum.length() + 2; i++) {
							bydata[i] = (byte) strPhoneNum.charAt(i - 2);
						}
					}
				}
				break;
			case DDef.PHONE_NONE:
			case DDef.PHONE_STATE_IDLE:
				bydata = new byte[8];
				bydata[0] = 0x01;
				bydata[1] = 0x01;
				break;
			default:
				break;
			}
		}

		return CanTxRxStub.getTxMessage(DATA_TYPE_PHONE_TX_INFO,
				DATA_TYPE_PHONE_TX_INFO, bydata, this);
	}

	public byte[] getPhoneInfo(MediaInfo mediaInfo) {
		byte[] bydata = null;

		switch (mediaInfo.getPhonestate()) {
		case DDef.PHONE_STATE_INCOMING:
		case DDef.PHONE_STATE_OUTGOING:
		case DDef.PHONE_STATE_SPEAKING:
			String strPhoneNum = mediaInfo.getPhoneNumber();
			if (strPhoneNum != null) {
				if (strPhoneNum.isEmpty()) {
					bydata = new byte[8];
					bydata[0] = 0x01;
					bydata[1] = 0x01;
				} else {
					bydata = new byte[strPhoneNum.length() + 2];

					bydata[0] = 0x01;
					bydata[1] = 0x01;

					for (int i = 2; i < strPhoneNum.length() + 2; i++) {
						bydata[i] = (byte) strPhoneNum.charAt(i - 2);
					}
				}
			}
			break;
		case DDef.PHONE_NONE:
		case DDef.PHONE_STATE_IDLE:
			bydata = new byte[8];
			bydata[0] = 0x01;
			bydata[1] = 0x01;
			break;
		default:
			bydata = new byte[8];
			break;
		}

		return bydata;
	}

	public byte[] MediaPlayInfo(MediaInfo mediaInfo) {

		byte[] bydata = null;
		byte bySource = TranslateSource(mediaInfo.getSource());
		byte byMediaType = getMediaType(bySource);

		switch (byMediaType) {
		case MediaType.SAM:
			break;
		case MediaType.EAM:
			bydata = new byte[8];
			bydata[0] = bySource;
			bydata[1] = byMediaType;
			byte[] byCurTrack = DataConvert.Hi2Lo2Byte(mediaInfo.getCurTrack());
			bydata[2] = byCurTrack[0];
			bydata[3] = byCurTrack[1];
			byte[] iTotalTrack = DataConvert.Hi2Lo2Byte(mediaInfo
					.getTotalTrack());
			bydata[4] = iTotalTrack[0];
			bydata[5] = iTotalTrack[1];
			int icurTime = mediaInfo.getPlayTime();
			bydata[6] = (byte) ((icurTime / 60) % 60);
			bydata[7] = (byte) (icurTime % 60);
			break;
		case MediaType.TUNER:
			bydata = new byte[8];
			bydata[0] = bySource;
			bydata[1] = byMediaType;

			bydata[2] = TranslateBand(mediaInfo.getBand());
			int iFreq = mediaInfo.getMainFreq();
			byte[] itmpFreq = DataConvert.Hi2Lo2Byte(iFreq);
			bydata[3] = itmpFreq[0];
			bydata[4] = itmpFreq[1];
			bydata[5] = mediaInfo.getFreqIndex();
			bydata[6] = 0x00;
			bydata[7] = 0x00;
			break;
		case MediaType.IPOD:
			break;
		case MediaType.FBV:
			break;
		case MediaType.DVDV:
			break;
		case MediaType.OV:
			break;
		case MediaType.AUX:
			bydata = new byte[8];
			bydata[0] = bySource;
			bydata[1] = byMediaType;
			break;
		case MediaType.PHONE:
			bydata = getPhoneInfo(mediaInfo);
			break;
		default:
			bydata = new byte[8];
			break;
		}

		return bydata;
	}

	private Message getMediaData(MediaInfo mediaInfo) {

		return CanTxRxStub.getTxMessage(DATA_TYPE_CAR_SOURCE,
				DATA_TYPE_CAR_SOURCE, MediaPlayInfo(mediaInfo), this);
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		ArrayList<Message> aListMsg = new ArrayList<Message>();
		aListMsg.add(getMediaData(mediaInfo));
		aListMsg.add(CanTxRxStub.getTxMessage(DATA_TYPE_TIME_INFO,
				DATA_TYPE_TIME_INFO, getTime(mediaInfo), this));
		return aListMsg;
	}

	@Override
	public Message getVolInfo(int iVol) {
		// TODO Auto-generated method stub
		return CanTxRxStub.getTxMessage(DATA_TYPE_CAR_VOL, DATA_TYPE_CAR_VOL,
				getVol(iVol), this);
	}

	private byte[] getVol(int iVol) {
		byte[] byData = { 0 };

		byte byVol = (byte) iVol;

		byData[0] = (byte) ((byVol > 0) ? (byData[0] & 0x00)
				: (byData[0] | 0x80));
		byData[0] = (byte) (byData[0] | ((0x7F & byData[0]) | byVol));

		return byData;
	}

	private byte[] getTime(MediaInfo mediaInfo) {
		byte[] byData = new byte[7];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();

		byData[0] = (byte) (timeInfo.iYear - 2000);
		byData[1] = (byte) timeInfo.byMonth;
		byData[2] = (byte) timeInfo.byDay;

		byData[3] = (byte) ((timeInfo.by24Mode == 0) ? (byData[3] | 0x80)
				: (byData[3] & 0x00));
		byData[3] = (byte) (byData[3] | ((0x7E & byData[3]) | timeInfo.byHour));
		byData[4] = timeInfo.byMinute;
		byData[5] = timeInfo.bySecond;
		byData[6] = 0x01;

		return byData;
	}

	@Override
	public boolean IsSuportMeInfo() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Message getMuteRadarData(boolean isMute) {
		// TODO Auto-generated method stub
		byte[] muteData = new byte[2];
		muteData[0] = (byte) 0xAB;

		if (isMute) {
			muteData[1] = 0x01;
		} else {
			muteData[1] = 0x00;
		}

		return CanTxRxStub.getTxMessage(DATA_TYPE_CAR_SET, DATA_TYPE_CAR_SET,
				muteData, this);
	}

	@Override
	public Message getInquiryTrackData() {
		// TODO Auto-generated method stub
		byte[] byData = { 0x29, 0x00 };

		return CanTxRxStub.getTxMessage(DATA_TYPE_CAR_INFO_REQUEST,
				DATA_TYPE_CAR_INFO_REQUEST, byData, this);
	}
}
