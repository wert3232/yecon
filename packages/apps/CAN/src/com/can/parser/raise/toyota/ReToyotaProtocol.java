package com.can.parser.raise.toyota;

import android.os.Message;
import com.can.assist.CanKey;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CurOilInfo;
import com.can.parser.DDef.IntantOilInfo;
import com.can.parser.DDef.OilVatteryInfo;
import com.can.parser.DDef.PowerAmplifier;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.SetInfo;
import com.can.parser.DDef.SystemInfo;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.DDef.VehicleSettings;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReToyotaProtocol
 * 
 * @function:睿志城丰田协议
 * @author Kim
 * @Date: 2016-6-4 上午10:27:30
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReToyotaProtocol extends ReProtocol {
	/**
	 * 协议数据类型定义 丰田全兼容平台 ver2.2
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_WHEEL_KEY_INFO               = (byte) 0x20; // 方向盘按键信息
	public static final byte DATA_TYPE_TRIP_INFORMATION_MIN_OIL     = (byte) 0x21; // 每分钟油耗
	public static final byte DATA_TYPE_TRIP_INFORMATION_INSTANT_OIL = (byte) 0x22; // 瞬时油耗信息
	public static final byte DATA_TYPE_TRIP_INFORMATION_HISTORY_OIL = (byte) 0x23; // HISTORY
																					// 历史油耗信息
	public static final byte DATA_TYPE_BASE_INFO                    = (byte) 0x24; // 基本信息
	public static final byte DATA_TYPE_TPMS_INFO                    = (byte) 0x25; // TPMS 信息
	public static final byte DATA_TYPE_VEHICLE_SETTINGS             = (byte) 0x26; // VEHICLE
	public static final byte DATA_TYPE_PANAL_KEY            		 = (byte) 0x51; // VEHICLE
	public static final byte DATA_TYPE_SETTINGS_INFO                = (byte) 0x52; // VEHICLE
																		// SETTINGS
	public static final byte DATA_TYPE_TRIP_INFORMATION_15MIN_OIL   = (byte) 0x27; // 15分钟油耗
	public static final byte DATA_TYPE_AIR_INFO                     = (byte) 0x28; // 空调信息
	public static final byte DATA_TYPE_WHEEL_INFO                   = (byte) 0x29; // EPS方向盘转角信息
	public static final byte DATA_TYPE_VERSION_INFO                 = (byte) 0x30; // 版本信息
	public static final byte DATA_TYPE_FUNC_INFO                    = (byte) 0x31; // 功放信息
	public static final byte DATA_TYPE_SYSTEM_INFO                  = (byte) 0x32; // 系统信息
	public static final byte DATA_TYPE_OILELE_FALG                  = (byte) 0x1f; // 油电混合指示
	public static final byte DATA_TYPE_BACK_RADAR_INFO              = (byte) 0x1E; // 后雷达信息
	public static final byte DATA_TYPE_FRONT_RADAR_INFO             = (byte) 0x1D; // 前雷达信息
	public static final byte DATA_TYPE_CAR_INFO                     = (byte) 0x41; // 兼容欣朴丰田

	// Host -> Slave
	public static final byte DATA_TYPE_SETTING_REQUEST = (byte) 0x83; // 请求设定信息
	public static final byte DATA_TYPE_INQUIRY_REQUEST = (byte) 0x90; // 请求设定信息
	public static final byte DATA_TYPE_DSP_REQUEST     = (byte) 0x84; 
	// 设定命令
	// 灯光设置
	public static final byte DAYTIME_RUNNING_LIGHTS        = (byte) 0x04;
	public static final byte HEADLAMPS_ON_SENSITIVITY      = (byte) 0x06;
	public static final byte HRADLAMPS_AUTO_SHUTDOWN_TIMER = (byte) 0x0C;
	public static final byte INTERLAMPS_SHUTDOWN_SET       = (byte) 0x07;

	// 车锁设置
	public static final byte AUTOLOCK_BY_SPEED             = (byte) 0x00;
	public static final byte AUTOLOCK_BY_SHIFT_FROM_P      = (byte) 0x01;
	public static final byte AUTOUNLOCK_BY_SHIFT_TO_P      = (byte) 0x02;
	public static final byte REMOTE_2_PRESS_UNLOCK         = (byte) 0x03;
	public static final byte LOCK_UNLOCK_FEEDBACK_TONE     = (byte) 0x05;
	public static final byte INCAR_LIGHT_CLOSE_TIME        = (byte) 0x0C;
	public static final byte REMOTE_2_KEY_UNLOCK           = (byte) 0x0D;
	public static final byte DRIVE_DOOR_LINKAGE_UNLOCK     = (byte) 0x0E;
	public static final byte SMART_DOOR_UNLOCK 			   = (byte) 0x0F;
	public static final byte SMART_CAR_LOCK_AND_A_BUTTON_TO_START = (byte) 0x10;
	public static final byte LOCK_UNLOCK_LIGHT_FLASHING    = (byte) 0x11;
	public static final byte RADAR_TRACK_SET               = (byte) 0x22;
	public static final byte ELECTRIC_DOOR_AdJUST          = (byte) 0x23;

	// 空调设置
	public static final byte AC_AUTO_LINKAGE    = (byte) 0x12;
	public static final byte CHANGE_AIR_LINKAGE = (byte) 0x13;
	public static final byte AUTO_LOCK_TIME     = (byte) 0x14;
	public static final byte SET_RADAR_VOL      = (byte) 0x15;
	public static final byte SET_FUEL_UNIT      = (byte) 0x24;
	
	public static final byte CLEAR_CUR_TRIP_INFORMATION      = (byte) 0x0A;
	public static final byte CLEAR_HISTORY_TRIP_INFORMATION  = (byte) 0x09;
	public static final byte UPDATA_HISTORY_TRIP_INFORMATION = (byte) 0x08;

	// 按键类型
	public class KeyCode {
		public static final byte KEY_CODE_NONE = (byte) 0x00;
		public static final byte KEY_CODE_VOL_UP = (byte) 0x01;
		public static final byte KEY_CODE_VOL_DOWN = (byte) 0x02;
		public static final byte KEY_CODE_SRC_RIGHT = (byte) 0x03;
		public static final byte KEY_CODE_SRC_LEFT = (byte) 0x04;
		public static final byte KEY_CODE_SRC1 = (byte) 0x07;
		public static final byte KEY_CODE_SPEECH = (byte) 0x08;
		public static final byte KEY_CODE_PICKUP = (byte) 0x09;
		public static final byte KEY_CODE_HANGUP = (byte) 0x0a;
		public static final byte KEY_CODE_OTHER = (byte) 0x0b;
		public static final byte KEY_CODE_SRC_UP = (byte) 0x13;
		public static final byte KEY_CODE_SRC_DOWN = (byte) 0x14;
		public static final byte KEY_CODE_BACK = (byte) 0x15;
		public static final byte KEY_CODE_ENTER = (byte) 0x16;

		public static final byte KEY_CODE_VOL_UP_1 = (byte) 0x81;
		public static final byte KEY_CODE_VOL_DOWN_1 = (byte) 0x82;
		public static final byte KEY_CODE_CH_FLD_UP = (byte) 0x83;
		public static final byte KEY_CODE_CH_FLD_DOWN = (byte) 0x84;
		public static final byte KEY_CODE_TUNE_TRACK_UP = (byte) 0x85;
		public static final byte KEY_CODE_TUNE_TRACK_DOWN = (byte) 0x86;
		public static final byte KEY_CODE_PWR = (byte) 0x87;
		public static final byte KEY_CODE_MODE = (byte) 0x88;
	}

	public CarInfo parseTripInfoMinOil(byte[] packet, CarInfo carInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte Data0 = (byte) packet[cursor];
		byte Data1 = (byte) packet[cursor + 1];
		byte Data2 = (byte) packet[cursor + 2];
		byte Data3 = (byte) packet[cursor + 3];
		byte Data4 = (byte) packet[cursor + 4];
		byte Data5 = (byte) packet[cursor + 5];
		byte Data6 = (byte) packet[cursor + 6];

		carInfo.mAverageSpeedMin = (float) ((DataConvert.byteToInt(Data0) * 256 + DataConvert.byteToInt(Data1)) * 0.1);
		carInfo.mElapsedTimeMin = DataConvert.byteToInt(Data2) * 256 + DataConvert.byteToInt(Data3);
		carInfo.mCruisingRangeMin = DataConvert.byteToInt(Data4) * 256 + DataConvert.byteToInt(Data5);
		carInfo.mUintMin = Data6;

		return carInfo;
	}

	public IntantOilInfo parseTripInfoInstantOil(byte[] packet,
			IntantOilInfo intantOilInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte Data0 = (byte) packet[cursor];
		byte Data1 = (byte) packet[cursor + 1];
		byte Data2 = (byte) packet[cursor + 2];

		intantOilInfo.mIntantConsumeOilUint = Data0;
		intantOilInfo.mIntantConsumeOil = (float) ((DataConvert.byteToInt(Data1) * 256 + DataConvert.byteToInt(Data2)) * 0.1);

		return intantOilInfo;
	}

	public CarInfo parseTripInfoHistoryOil(byte[] packet, CarInfo carInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte Data0 = (byte) packet[cursor];
		byte Data1 = (byte) packet[cursor + 1];
		byte Data2 = (byte) packet[cursor + 2];
		byte Data3 = (byte) packet[cursor + 3];
		byte Data4 = (byte) packet[cursor + 4];
		byte Data5 = (byte) packet[cursor + 5];
		byte Data6 = (byte) packet[cursor + 6];
		byte Data7 = (byte) packet[cursor + 7];
		byte Data8 = (byte) packet[cursor + 8];
		byte Data9 = (byte) packet[cursor + 9];
		byte Data10 = (byte) packet[cursor + 10];
		byte Data11 = (byte) packet[cursor + 11];
		byte Data12 = (byte) packet[cursor + 12];

		carInfo.mHistoryOilUnit = Data0;
		carInfo.mCurrentHistoryOil = (float) ((DataConvert.byteToInt(Data1) * 256 + DataConvert.byteToInt(Data2)) * 0.1);
		carInfo.mTripFuel1 = (float) ((DataConvert.byteToInt(Data3) * 256 + DataConvert.byteToInt(Data4)) * 0.1);
		carInfo.mTripFuel2 = (float) ((DataConvert.byteToInt(Data5) * 256 + DataConvert.byteToInt(Data6)) * 0.1);
		carInfo.mTripFuel3 = (float) ((DataConvert.byteToInt(Data7) * 256 + DataConvert.byteToInt(Data8)) * 0.1);
		carInfo.mTripFuel4 = (float) ((DataConvert.byteToInt(Data9) * 256 + DataConvert.byteToInt(Data10)) * 0.1);
		carInfo.mTripFuel5 = (float) ((DataConvert.byteToInt(Data11) * 256 + DataConvert.byteToInt(Data12)) * 0.1);
		return carInfo;
	}

	public TPMSInfo parseTPMSInfo(byte[] packet, TPMSInfo tpmsInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte Data0 = (byte) packet[cursor];
		byte Data1 = (byte) packet[cursor + 1];
		byte Data2 = (byte) packet[cursor + 2];
		byte Data3 = (byte) packet[cursor + 3];
		byte Data4 = (byte) packet[cursor + 4];
		byte Data5 = (byte) packet[cursor + 5];

		tpmsInfo.mIsExistDevice = (byte) ((Data0 >> 7) & 0x1);
		tpmsInfo.mIsNormal = (byte) ((Data0 >> 6) & 0x1);
		tpmsInfo.mShowSpareTire = (byte) ((Data0 >> 5) & 0x1);
		tpmsInfo.mTireShowMode = (byte) ((Data0 >> 2) & 0x1);
		tpmsInfo.mTpmsUnit = (byte) ((Data0) & 0x3);

		tpmsInfo.mFLTirePressure = DataConvert.byteToInt(Data1);
		tpmsInfo.mFRTirePressure = DataConvert.byteToInt(Data2);
		tpmsInfo.mBLTirePressure = DataConvert.byteToInt(Data3);
		tpmsInfo.mBRTirePressure = DataConvert.byteToInt(Data4);
		tpmsInfo.mSpareTirePressure = DataConvert.byteToInt(Data5);

		return tpmsInfo;
	}

	public VehicleSettings parseVehicleSettingsInfo(byte[] packet,
			VehicleSettings vehicleSettingInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte Data0 = (byte) packet[cursor];
		byte Data1 = (byte) packet[cursor + 1];
		byte Data2 = (byte) packet[cursor + 2];
		byte Data3 = (byte) packet[cursor + 3];

		vehicleSettingInfo.mDayTimeRunningLights = (byte) ((Data0 >> 7) & 0x1);
		vehicleSettingInfo.mHeadlampsOnSensitivity = (byte) ((Data0 >> 4) & 0x7);
		vehicleSettingInfo.mLightsOffTimer = (byte) ((Data0 >> 2) & 0x3);
		vehicleSettingInfo.mHeadlampsAutoOFFTimer = (byte) ((Data0 >> 0) & 0x3);

		vehicleSettingInfo.mAutoLockBySpeed = (byte) ((Data1 >> 7) & 0x1);
		vehicleSettingInfo.mAutoLockBySHIFTFORMP = (byte) ((Data1 >> 6) & 0x1);
		vehicleSettingInfo.mAutoLockBySHIFTToP = (byte) ((Data1 >> 5) & 0x1);
		vehicleSettingInfo.mRemote2PressUnlock = (byte) ((Data1 >> 4) & 0x1);
		vehicleSettingInfo.mLockUnLockFeedBackTONE = (byte) ((Data1 >> 0) & 0x7);

		vehicleSettingInfo.mLockHandleKeyTwoTimes = (byte) ((Data2 >> 7) & 0x1);
		vehicleSettingInfo.mLockDrivingSeatOpenDoor = (byte) ((Data2 >> 6) & 0x1);
		vehicleSettingInfo.mLockIntelligentDoor = (byte) ((Data2 >> 5) & 0x1);
		vehicleSettingInfo.mLockIntelligentVehicle = (byte) ((Data2 >> 4) & 0x1);
		vehicleSettingInfo.mUnLockFlash = (byte) ((Data2 >> 3) & 0x1);
		vehicleSettingInfo.mElectricdooradjust = (byte)(Data2 & 0x7);

		vehicleSettingInfo.mAirSwitchAndAutoLinkage = (byte) ((Data3 >> 7) & 0x1);
		vehicleSettingInfo.mAirCircleAndAutoLinkage = (byte) ((Data3 >> 6) & 0x1);
		vehicleSettingInfo.mAutoRelockTimer = (byte) (Data3 & 0x3);

		return vehicleSettingInfo;
	}

	public CurOilInfo parseTripInformation15MinOil(byte[] packet,
			CurOilInfo curOilInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte Data0 = (byte) packet[cursor];
		byte Data1 = (byte) packet[cursor + 1];
		byte Data2 = (byte) packet[cursor + 2];
		byte Data3 = (byte) packet[cursor + 3];
		byte Data4 = (byte) packet[cursor + 4];
		byte Data5 = (byte) packet[cursor + 5];
		byte Data6 = (byte) packet[cursor + 6];
		byte Data7 = (byte) packet[cursor + 7];
		byte Data8 = (byte) packet[cursor + 8];
		byte Data9 = (byte) packet[cursor + 9];
		byte Data10 = (byte) packet[cursor + 10];
		byte Data11 = (byte) packet[cursor + 11];
		byte Data12 = (byte) packet[cursor + 12];
		byte Data13 = (byte) packet[cursor + 13];
		byte Data14 = (byte) packet[cursor + 14];
		byte Data15 = (byte) packet[cursor + 15];
		byte Data16 = (byte) packet[cursor + 16];
		byte Data17 = (byte) packet[cursor + 17];
		byte Data18 = (byte) packet[cursor + 18];
		byte Data19 = (byte) packet[cursor + 19];
		byte Data20 = (byte) packet[cursor + 20];
		byte Data21 = (byte) packet[cursor + 21];
		byte Data22 = (byte) packet[cursor + 22];
		byte Data23 = (byte) packet[cursor + 23];
		byte Data24 = (byte) packet[cursor + 24];
		byte Data25 = (byte) packet[cursor + 25];
		byte Data26 = (byte) packet[cursor + 26];
		byte Data27 = (byte) packet[cursor + 27];
		byte Data28 = (byte) packet[cursor + 28];
		byte Data29 = (byte) packet[cursor + 29];
		byte Data30 = (byte) packet[cursor + 30];

		curOilInfo.m15minOilFuelUint = Data0;
		curOilInfo.m15minOilFuel_15 = (float) ((DataConvert.byteToInt(Data1) * 256 + DataConvert.byteToInt(Data2)) * 0.1);
		curOilInfo.m15minOilFuel_14 = (float) ((DataConvert.byteToInt(Data3) * 256 + DataConvert.byteToInt(Data4)) * 0.1);
		curOilInfo.m15minOilFuel_13 = (float) ((DataConvert.byteToInt(Data5) * 256 + DataConvert.byteToInt(Data6)) * 0.1);
		curOilInfo.m15minOilFuel_12 = (float) ((DataConvert.byteToInt(Data7) * 256 + DataConvert.byteToInt(Data8)) * 0.1);
		curOilInfo.m15minOilFuel_11 = (float) ((DataConvert.byteToInt(Data9) * 256 + DataConvert.byteToInt(Data10)) * 0.1);
		curOilInfo.m15minOilFuel_10 = (float) ((DataConvert.byteToInt(Data11) * 256 + DataConvert.byteToInt(Data12)) * 0.1);
		curOilInfo.m15minOilFuel_9 = (float) ((DataConvert.byteToInt(Data13) * 256 + DataConvert.byteToInt(Data14)) * 0.1);
		curOilInfo.m15minOilFuel_8 = (float) ((DataConvert.byteToInt(Data15) * 256 + DataConvert.byteToInt(Data16)) * 0.1);
		curOilInfo.m15minOilFuel_7 = (float) ((DataConvert.byteToInt(Data17) * 256 + DataConvert.byteToInt(Data18)) * 0.1);
		curOilInfo.m15minOilFuel_6 = (float) ((DataConvert.byteToInt(Data19) * 256 + DataConvert.byteToInt(Data20)) * 0.1);
		curOilInfo.m15minOilFuel_5 = (float) ((DataConvert.byteToInt(Data21) * 256 + DataConvert.byteToInt(Data22)) * 0.1);
		curOilInfo.m15minOilFuel_4 = (float) ((DataConvert.byteToInt(Data23) * 256 + DataConvert.byteToInt(Data24)) * 0.1);
		curOilInfo.m15minOilFuel_3 = (float) ((DataConvert.byteToInt(Data25) * 256 + DataConvert.byteToInt(Data26)) * 0.1);
		curOilInfo.m15minOilFuel_2 = (float) ((DataConvert.byteToInt(Data27) * 256 + DataConvert.byteToInt(Data28)) * 0.1);
		curOilInfo.m15minOilFuel_1 = (float) ((DataConvert.byteToInt(Data29) * 256 + DataConvert.byteToInt(Data30)) * 0.1);

		return curOilInfo;
	}

	/**
	 * 
	 * @Title:TempChange
	 * @Description: 空调温度，特殊值转换
	 * 
	 */
	public float TempChange(byte data) {
		float temp = 0.0f;

		switch (data) {
		case 0x20:
			temp = 16f;
			break;
		case 0x21:
			temp = 16.5f;
			break;
		case 0x22:
			temp = 17f;
			break;
		case 0x23:
			temp = 17.5f;

		default:
			break;
		}
		return temp;
	}

	public boolean isTempMinMax(byte temp) {
		if (temp == 0x00 || temp == 0x1f || temp == 0xff)
			return true;
		return false;
	}

	public PowerAmplifier parsePowerAmplifier(byte[] packet,
			PowerAmplifier powerAmp) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		byte Data0 = (byte) packet[cursor];
		byte Data1 = (byte) packet[cursor + 1];
		byte Data2 = (byte) packet[cursor + 2];
		byte Data3 = (byte) packet[cursor + 3];
		byte Data4 = (byte) packet[cursor + 4];

		powerAmp.mFAD = (byte) ((Data0 >> 4) & 0xF);
		powerAmp.mBAL = (byte) ((Data0) & 0xF);
		powerAmp.mBASS = (byte) ((Data1 >> 4) & 0xF);
		powerAmp.mTRE = (byte) ((Data1) & 0xF);
		powerAmp.mMID = (byte) ((Data2 >> 4) & 0xF);
		powerAmp.mASL = (byte) ((Data2) & 0x0F);
		powerAmp.mCurVol = Data3;
		powerAmp.mVolByASL = (byte) ((Data4) & 0x01);

		return powerAmp;
	}

	public SystemInfo parseSystemInfo(byte[] packet, SystemInfo sysInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte Data0 = (byte) packet[cursor];
		sysInfo.mVehiclePowerAmplifier = (byte) ((Data0) & 0x1);
		sysInfo.mBackseat = (byte) ((Data0 >> 1) & 0x1);
		sysInfo.mPanoramicCamera = (byte) ((Data0 >> 2) & 0x1);
		sysInfo.mPanoramicView = (byte) ((Data0 >> 3) & 0x1);
		sysInfo.mVehiclePowerAmplifierSwitch = (byte) ((Data0 >> 6) & 0x1);
		sysInfo.mMuteSwitch = (byte) ((Data0 >> 7) & 0x1);

		return sysInfo;
	}
	
	public SetInfo parseSetInfo(byte[] packet, SetInfo setInfo){
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		byte bydata = (byte) packet[cursor];
		setInfo.mbyRadarDis = (byte)((bydata >> 7) & 0x1);
		
		bydata = (byte) packet[cursor+1];
		setInfo.mbyFuelUnit = (byte)((bydata >> 7) & 0x1);
		
		bydata = (byte) packet[cursor+2];
		setInfo.mbyColorTheme = (byte)(bydata & 0x7);
		
		return setInfo;
	}

	public OilVatteryInfo parseOilVatteryInfo(byte[] packet,
			OilVatteryInfo oilValInfo) {
		int cursor = 3; // 跳过2E、Data Type、Length字段
		byte Data0 = (byte) packet[cursor];
		byte Data1 = (byte) packet[cursor + 1];
		oilValInfo.mBatteryVoltage = (byte) ((Data0) & 0x7);
		oilValInfo.mHybridEleVehicle = (byte) ((Data0 >> 7) & 0x1);
		oilValInfo.mMotorDriveVattery = (byte) ((Data1) & 0x1);
		oilValInfo.mMotorDriveWheel = (byte) ((Data1 >> 1) & 0x1);
		oilValInfo.mEngineDriveMotor = (byte) ((Data1 >> 2) & 0x1);
		oilValInfo.mEngineDriveWheel = (byte) ((Data1 >> 3) & 0x1);
		oilValInfo.mVatteryDriveMotor = (byte) ((Data1 >> 4) & 0x1);
		oilValInfo.mWheelDriveMotor = (byte) ((Data1 >> 5) & 0x1);
		return oilValInfo;
	}

	public int GetTripInfoMinOilCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_TRIP_INFORMATION_MIN_OIL & 0xFF);
	}

	public int GetTripInfoInstantOilCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_TRIP_INFORMATION_INSTANT_OIL & 0xFF);
	}

	public int GetTripInfoHistoryOilCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_TRIP_INFORMATION_HISTORY_OIL & 0xFF);
	}

	public int GetTPMSCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_TPMS_INFO & 0xFF);
	}

	public int GetVehicleSettingsCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_VEHICLE_SETTINGS & 0xFF);
	}

	public int GetTripInfo15MinOilCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_TRIP_INFORMATION_15MIN_OIL & 0xFF);
	}

	public int GetPowerAmplifierCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_FUNC_INFO & 0xFF);
	}

	public int GetSystemInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_SYSTEM_INFO & 0xFF);
	}

	public int GetOilEleInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_OILELE_FALG & 0xFF);
	}
	/**
	 * 
	 * @Title:GetLightSetData
	 * @Description: 灯光数据设置
	 * 
	 */
	public static byte[] GetSettingData(byte CMD_ID, int value) {
		byte[] datas = new byte[2];
		datas[0] = CMD_ID;
		datas[1] = (byte) value;
		return datas;
	}

	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_AIR_INFO & 0xFF);
	}

	@Override
	public int GetCarInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_CAR_INFO & 0xFF);
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
	
	public int GetSettingInfo(){
		return (int) (DATA_TYPE_SETTINGS_INFO & 0xFF);
	}
	
	public int GetPanelkeyId(){
		return (int) (DATA_TYPE_PANAL_KEY & 0xFF);
	}

	@Override
	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte carData0 = (byte) packet[cursor];

		if (carData0 == 0x01) {
			// Data 1
			byte carData1 = (byte) packet[cursor + 1];
			carInfo.mHoodBoxDoor = (byte) ((carData1 >> 5) & 0x1);
			carInfo.mTailBoxDoor = (byte) ((carData1 >> 4) & 0x1);
			carInfo.mRightBackDoor = (byte) ((carData1 >> 3) & 0x1);
			carInfo.mLeftBackDoor = (byte) ((carData1 >> 2) & 0x1);
			carInfo.mRightFrontDoor = (byte) ((carData1 >> 1) & 0x1);
			carInfo.mLeftFrontDoor = (byte) ((carData1 >> 0) & 0x1);

			// Data2
			byte carData2 = (byte) packet[cursor + 2];
			carInfo.mHandbrake = (byte) ((carData2 >> 4) & 0x1);
			carInfo.mGears = (byte) ((carData2) & 0xF);

			// Data3 保留
			// Data4
			byte carData4 = (byte) packet[cursor + 4];
			carInfo.mNearlyHeadlight = (byte) ((carData4 >> 7) & 0x1);
			carInfo.mFarHeadlight = (byte) ((carData4 >> 6) & 0x1);
			carInfo.mWidlelight = (byte) ((carData4 >> 5) & 0x1);

			// Data5
			byte carData5 = (byte) packet[cursor + 5];
			carInfo.mBackCarLight = (byte) ((carData5 >> 7) & 0x1);
			carInfo.mBrakeLight = (byte) ((carData5 >> 6) & 0x1);
			carInfo.mLeftTurnLight = (byte) ((carData5 >> 4) & 0x1);
			carInfo.mRightTurnLight = (byte) ((carData5 >> 5) & 0x1);
			carInfo.mWarningLight = (byte) ((carData5 >> 3) & 0x1);
			carInfo.mBackFogLamp = (byte) ((carData5 >> 2) & 0x1);
			carInfo.mFontFogLamp = (byte) ((carData5 >> 1) & 0x1);

			// Data6
			byte carData6 = (byte) packet[cursor + 5];
			carInfo.mBackDoorLockState = (byte) ((carData6 >> 2) & 0x1);
			carInfo.mFrontLeftDoorLockState = (byte) ((carData6 >> 1) & 0x1);
			carInfo.mFrontRightDoorLockState = (byte) ((carData6 >> 0) & 0x1);

		} else if (carData0 == 0x02) {
			byte carData1 = (byte) packet[cursor + 1];
			byte carData2 = (byte) packet[cursor + 2];
			byte carData3 = (byte) packet[cursor + 3];
			byte carData4 = (byte) packet[cursor + 4];
			byte carData5 = (byte) packet[cursor + 5];
			byte carData6 = (byte) packet[cursor + 6];
			byte carData7 = (byte) packet[cursor + 7];
			byte carData8 = (byte) packet[cursor + 8];
			byte carData9 = (byte) packet[cursor + 9];
			byte carData10 = (byte) packet[cursor + 10];
			byte carData11 = (byte) packet[cursor + 11];
			byte carData12 = (byte) packet[cursor + 12];
			byte carData13 = (byte) packet[cursor + 13];
			byte carData14 = (byte) packet[cursor + 14];
			byte carData15 = (byte) packet[cursor + 15];

			carInfo.mTotalMileage = DataConvert.byteToInt(carData1) * 65536
					+ DataConvert.byteToInt(carData2) * 256 + DataConvert.byteToInt(carData3);
			carInfo.mMileage = DataConvert.byteToInt(carData4) * 256 + DataConvert.byteToInt(carData5);

			carInfo.mMileageA = DataConvert.byteToInt(carData6) * 65536
					+ DataConvert.byteToInt(carData7) * 256 + DataConvert.byteToInt(carData8);
			carInfo.mMileageB = DataConvert.byteToInt(carData9) * 65536
					+ DataConvert.byteToInt(carData10) * 256 + DataConvert.byteToInt(carData11);
			carInfo.mCurrentSpeed = (float) ((DataConvert.byteToInt(carData12) * 256 + DataConvert.byteToInt(carData13)) * 0.01);
			carInfo.mAverageSpeed = (float) ((DataConvert.byteToInt(carData14) * 256 + DataConvert.byteToInt(carData15)) * 0.1);

		} else if (carData0 == 0x03) {

			byte carData1 = (byte) packet[cursor + 1];
			byte carData2 = (byte) packet[cursor + 2];
			byte carData7 = (byte) packet[cursor + 7];

			carInfo.mRotationlSpeed = DataConvert.byteToInt(carData1) * 256
					+ DataConvert.byteToInt(carData2);
			carInfo.mOutCarTemp = DataConvert.byteToInt(carData7);
		}

		return carInfo;
	}
	
	private byte[] mbyAirInfo;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过2E、Data Type、Length字段

		// Data 0
		byte airData0 = (byte) packet[cursor];
		airInfo.mAiron = (byte) ((airData0 >> 7) & 0x1);
		airInfo.mAcState = (byte) ((airData0 >> 6) & 0x1);
		airInfo.mCircleState = (byte) ((airData0 >> 5) & 0x1);
		airInfo.mAutoLight1 = (byte) DataConvert.GetBit(airData0, 4);
		airInfo.mAutoLight2 = (byte) ((airData0 >> 3) & 0x1);
		airInfo.mDaulLight = (byte) DataConvert.GetBit(airData0, 2);

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		airInfo.mUpwardWind = (byte) ((airData1 >> 7) & 0x1);
		airInfo.mParallelWind = (byte) ((airData1 >> 6) & 0x1);
		airInfo.mDowmWind = (byte) ((airData1) >> 5 & 0x1);

		airInfo.mDisplay = (byte) ((airData1) >> 4 & 0x1);
		airInfo.mWindRate = (byte) ((airData1) & 0x07);

		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];
		int leftTemp = (int) (airData2 & 0xFF);
		// Data 3
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		int rightTemp = (int) (airData3 & 0xFF);

		// Data 4
		cursor += 1;
		byte airData4 = (byte) packet[cursor];
		airInfo.mFWDefogger = (byte) ((airData4 >> 7) & 0x1);
		airInfo.mRearLight = (byte) ((airData4 >> 6) & 0x1);
		airInfo.mAcMax = (byte) ((airData4 >> 3) & 0x1);

		airInfo.mTempUnit = (byte) ((airData4 >> 0) & 0x1);

		// 左边温度
		if (airInfo.mTempUnit == 0x01) {
			airInfo.mLeftTemp = leftTemp;
		} else {
			if (isTempMinMax(airData2)) {
				if (airData2 == 0x1F) {
					airInfo.mLeftTemp = 238f;
				} else {
					airInfo.mLeftTemp = leftTemp; // 0xEE ,自定义区别转换后的31度跟最大标识区别
				}
			} else {
				airInfo.mLeftTemp = TempChange((byte) leftTemp);
				if (airInfo.mLeftTemp == 0) {
					airInfo.mLeftTemp = (float) (18 + (leftTemp - 1) * 0.5);
				}
			}
		}
		
		// 右边温度
		if (airInfo.mTempUnit == 0x01) {
			airInfo.mRightTemp = rightTemp;
		} else {

			if (isTempMinMax(airData3)) {
				if (airData3 == 0x1F) {
					airInfo.mRightTemp = 238f;
				} else {
					airInfo.mRightTemp = rightTemp; // 0xEE ,自定义区别转换后的31度跟最大标识区别
				}
			} else {
				airInfo.mRightTemp = TempChange((byte) rightTemp);
				if (airInfo.mRightTemp == 0) {
					airInfo.mRightTemp = (float) (18 + (rightTemp - 1) * 0.5);
				}
			}
		}
		
		if (airInfo.mTempUnit == 0) {
			airInfo.mMinTemp = 0;
			airInfo.mMaxTemp = 238f;
		}else {
			airInfo.mMinTemp = 0;
			airInfo.mMaxTemp = 254;
		}
		
        if (mbyAirInfo != null) {
			
        	airInfo.bAirUIShow = (!mbyAirInfo.equals(packet) && 
        			(airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true : false;
		}
        
        mbyAirInfo = packet;

		return airInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		// TODO Auto-generated method stub
		int cursor = 0;
		byte DataType = (byte) packet[cursor + 1];
		cursor += 3; // 跳过 2E、Data Type、Length字段

		if (DataType == DATA_TYPE_BACK_RADAR_INFO) {

			radarInfo.mBackLeftDis = packet[cursor];
			radarInfo.mBackLeftCenterDis = packet[cursor + 1];
			radarInfo.mBackRightCenterDis = packet[cursor + 2];
			radarInfo.mBackRightDis = packet[cursor + 3];

			byte Data4 = packet[cursor + 4];
			radarInfo.mRadarShowSwitch = (byte) ((Data4 >> 7) & 0x1);
			radarInfo.mDistance = (byte) ((Data4 >> 6) & 0x1);
			radarInfo.mRadarSwtich = (byte) ((Data4 >> 5) & 0x1);
			radarInfo.mVol = (byte) ((Data4) & 0x7);

		} else if (DataType == DATA_TYPE_FRONT_RADAR_INFO) {

			radarInfo.mFrontLeftDis = packet[cursor];
			radarInfo.mFrontLeftCenterDis = packet[cursor + 1];
			radarInfo.mFrontRightCenterDis = packet[cursor + 2];
			radarInfo.mFrontRightDis = packet[cursor + 3];
		}
		
		radarInfo.mbyRightShowType = 0;

		return radarInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte Data0 = (byte) packet[cursor];
		baseInfo.mRightFrontDoor = (byte) ((Data0 >> 7) & 0x1);
		baseInfo.mLeftFrontDoor = (byte) ((Data0 >> 6) & 0x1);
		baseInfo.mRightBackDoor = (byte) ((Data0 >> 5) & 0x1);
		baseInfo.mLeftBackDoor = (byte) ((Data0 >> 4) & 0x1);
		baseInfo.mTailBoxDoor = (byte) ((Data0 >> 3) & 0x1);
		baseInfo.mTailElectricDoor = (byte) ((Data0 >> 2) & 0x1);
		baseInfo.mTailElectricDoorDirect = (byte) ((Data0 >> 1) & 0x1);

		// Data1
		byte Data1 = (byte) packet[cursor + 1];
		baseInfo.mIG = (byte) ((Data1 >> 1) & 0x1);

		return baseInfo;
	}

	@Override
	public VerInfo parseVersionInfo(byte[] packet) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		VerInfo version = new VerInfo();
		byte[] ver = new byte[16];
		System.arraycopy(packet, cursor, ver, 0, 16);
		version.mVersion = DataConvert.Bytes2Str(ver);
		return version;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		byte data1 = (byte) packet[cursor + 1];
		byte dire = (byte) (data1 >> 3 & 0x1);
		short sWangle = DataConvert.byte2Short(packet, cursor);
		System.out.println(sWangle);
		if (dire == 1) { // 向右
			sWangle = (short) -(((0xFFF - sWangle) + 1) * 40 / 380);
		} else {
			sWangle = (short) (sWangle * 40 / 380);
		}
		wheelInfo.mDirect = dire;
		wheelInfo.mEps = sWangle;
		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = packet[cursor + 1];
		
		return TranslateKey(wheelInfo, false);
	}
	
	public WheelKeyInfo parsePanelkey(byte[] packet, WheelKeyInfo wheelKeyInfo){
		
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelKeyInfo.mKeyCode = packet[cursor];
		wheelKeyInfo.mKeyStatus = packet[cursor + 1];
		
		return TranslateKey(wheelKeyInfo,true);
	}
	
	private String mstrKeyCode = null;
	
	public WheelKeyInfo TranslateKey(WheelKeyInfo wheelKeyInfo, boolean bPanalkey) {
		// TODO Auto-generated method stub
		switch (wheelKeyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_BACK:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case KeyCode.KEY_CODE_SRC_DOWN:
		case KeyCode.KEY_CODE_CH_FLD_DOWN:			
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_TUNE_TRACK_DOWN:
			if (bPanalkey) {
				wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
				wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
				wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			}else {
				wheelKeyInfo.mstrKeyCode = DDef.Media_next;		
			}
			break;
		case KeyCode.KEY_CODE_SRC_UP:
		case KeyCode.KEY_CODE_CH_FLD_UP:		
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_TUNE_TRACK_UP:
			if (bPanalkey) {
				wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
				wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
				wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			}else {
				wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			}
			break;
		case KeyCode.KEY_CODE_ENTER:
			wheelKeyInfo.mstrKeyCode = DDef.Enter;
			break;
		case KeyCode.KEY_CODE_HANGUP:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_MODE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_PICKUP:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_PWR:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case KeyCode.KEY_CODE_SPEECH:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_SRC1:
			if (wheelKeyInfo.mKeyStatus == 0x02) {
				wheelKeyInfo.mKeyStatus = CanKey.KEY_COMPLEX;
				wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			}else {
				wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			}
			break;
		case KeyCode.KEY_CODE_SRC_LEFT:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_SRC_RIGHT:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_VOL_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_VOL_DOWN_1:
			if (bPanalkey) {
				wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
				wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
				wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			}else {
				wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			}
			break;
		case KeyCode.KEY_CODE_VOL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_UP_1:
			if (bPanalkey) {
				wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
				wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
				wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			}else {
				wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			}
			break;
		case KeyCode.KEY_CODE_OTHER:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
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
	public boolean IsSuportMeInfo() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Message getVolInfo(int iVol) {
		// TODO Auto-generated method stub
		byte[] bydata = new byte[2];
		bydata[0] = 0x07;
		bydata[1] = (byte) iVol;

		return CanTxRxStub.getTxMessage(DATA_TYPE_DSP_REQUEST,
				DATA_TYPE_DSP_REQUEST, bydata, this);
	}
}
