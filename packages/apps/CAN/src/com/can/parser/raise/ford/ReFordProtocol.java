package com.can.parser.raise.ford;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.os.Message;

import com.can.assist.CanKey;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CanAudio;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.PhoneState;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.SyncMediaTime;
import com.can.parser.DDef.SyncMenu;
import com.can.parser.DDef.SyncOption;
import com.can.parser.DDef.SyncState;
import com.can.parser.DDef.SyncTalkTime;
import com.can.parser.DDef.Sync_listInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;
import com.can.ui.CanPopWind;

/**
 * ClassName:ReFordProtocol
 * 
 * @function:睿志城协议
 * @author Kim
 * @Date: 2016-7-2下午2:33:21
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReFordProtocol extends ReProtocol {
	/**
	 * 协议数据类型定义 福特全兼容协议 ver1.0
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x21; // 空调信息
	public static final byte DATA_TYPE_CAR_INFO = (byte) 0x29; // 车辆信息
	public static final byte DATA_TYPE_BACK_RADAR_INFO = (byte) 0x22; // 后雷达信息
	public static final byte DATA_TYPE_FRONT_RADAR_INFO = (byte) 0x23; // 前雷达信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x24; // 基本信息
	public static final byte DATA_TYPE_PARKASSIST_INFO = (byte) 0x25; // 辅助系统信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x30; // 版本信息
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x20; // 方向盘按键信息
	public static final byte DATA_TYPE_BACK_LIGHT_INFO = (byte) 0x14; // 背光调节
	public static final byte DATA_TYPE_CAR_TIME_INFO = (byte) 0x26; // 车身时间
	public static final byte DATA_TYPE_CAR_LANG_SET = (byte) 0x27; // 车身语言
	public static final byte DATA_TYPE_CAR_ACTIVE_PARK = (byte) 0x28; // 自动泊车
	public static final byte DATA_TYPE_CAR_WARN_INFO = (byte) 0x2A; // 车辆警告信息

	public static final byte DATA_TYPE_SYNC_STR_UP = (byte) 0x70;
	public static final byte DATA_TYPE_SYNC_STR_DOWN = (byte) 0x71;
	public static final byte DATA_TYPE_SYNC_STR_SHORT = (byte) 0x72;
	public static final byte DATA_TYPE_SYNC_STATE = (byte) 0x78;
	public static final byte DATA_TYPE_SYNC_ADUIO = (byte) 0x79;
	public static final byte DATA_TYPE_SYNC_BACK_SRC1 = (byte) 0x7A;
	public static final byte DATA_TYPE_SYNC_BACK_SRC2 = (byte) 0x7B;
	public static final byte DATA_TYPE_SYNC_VER = (byte) 0x40;
	public static final byte DATA_TYPE_SYNC_MENU_INFO = (byte) 0x50;
	public static final byte DATA_TYPE_SYNC_MENU_OPTION = (byte) 0x51;
	public static final byte DATA_TYPE_SYNC_MEDIA_INFO = (byte) 0x52;
	public static final byte DATA_TYPE_SYNC_TALK_INFO = (byte) 0x53;
	public static final byte DATA_TYPE_PANEL_KEY_INFO = (byte) 0x01;

	// Host -> Slave
	public static final byte DATA_TYPE_CTRL_CMD = (byte) 0xC6;
	public static final byte DATA_TYPE_REQUEST = (byte) 0x90;
	public static final byte DATA_TYPE_SOURCE = (byte) 0xC0;

	public class KeyCode {
		public static final byte KEY_CODE_VOL_UP = (byte) 0x01;
		public static final byte KEY_CODE_VOL_DOWN = (byte) 0x02;
		public static final byte KEY_CODE_MENU_NEXT = (byte) 0x03;
		public static final byte KEY_CODE_MENU_LAST = (byte) 0x04;
		public static final byte KEY_CODE_TEL = (byte) 0x05;
		public static final byte KEY_CODE_MUTE = (byte) 0x06;
		public static final byte KEY_CODE_SRC = (byte) 0x07;
		public static final byte KEY_CODE_MENU_UP = (byte) 0x0e;
		public static final byte KEY_CODE_MENU_DN = (byte) 0x0f;
		public static final byte KEY_CODE_MENU_LEFT = (byte) 0x10;
		public static final byte KEY_CODE_MENU_RIGHT = (byte) 0x11;
		public static final byte KEY_CODE_MENU_OK = (byte) 0x12;
		public static final byte KEY_CODE_NUM_0 = (byte) 0x20;
		public static final byte KEY_CODE_NUM_1 = (byte) 0x21;
		public static final byte KEY_CODE_NUM_2 = (byte) 0x22;
		public static final byte KEY_CODE_NUM_3 = (byte) 0x23;
		public static final byte KEY_CODE_NUM_4 = (byte) 0x24;
		public static final byte KEY_CODE_NUM_5 = (byte) 0x25;
		public static final byte KEY_CODE_NUM_6 = (byte) 0x26;
		public static final byte KEY_CODE_NUM_7 = (byte) 0x27;
		public static final byte KEY_CODE_NUM_8 = (byte) 0x28;
		public static final byte KEY_CODE_NUM_9 = (byte) 0x29;
		public static final byte KEY_CODE_NUM_a = (byte) 0x2A;
		public static final byte KEY_CODE_NUM_b = (byte) 0x2B;

		public static final byte KEY_CODE_STRIUS = (byte) 0x33;
		public static final byte KEY_CODE_RADIO = (byte) 0x34;
		public static final byte KEY_CODE_CD = (byte) 0x35;
		public static final byte KEY_CODE_AUX = (byte) 0x36;
		public static final byte KEY_CODE_MENU = (byte) 0x37;
		public static final byte KEY_CODE_SOUND = (byte) 0x38;
		public static final byte KEY_CODE_PHONE = (byte) 0x39;
		public static final byte KEY_CODE_CLOCK = (byte) 0x3D;
		public static final byte KEY_CODE_POWER = (byte) 0x3F;
		public static final byte KEY_CODE_OK = (byte) 0x48;
		public static final byte KEY_CODE_LEFT = (byte) 0x49;
		public static final byte KEY_CODE_RIGHT = (byte) 0x4A;
		public static final byte KEY_CODE_UP = (byte) 0x4B;
		public static final byte KEY_CODE_DN = (byte) 0x4C;
		public static final byte KEY_CODE_TELUP = (byte) 0x52;
		public static final byte KEY_CODE_HUNG = (byte) 0x53;
		public static final byte KEY_CODE_EJECT = (byte) 0x54;
		public static final byte KEY_CODE_TA = (byte) 0x56;
		public static final byte KEY_CODE_INFO = (byte) 0x57;
		public static final byte KEY_CODE_DSP = (byte) 0x59;
		public static final byte KEY_CODE_PMUTE = (byte) 0x5A;
		public static final byte KEY_CODE_DISPLAY = (byte) 0x5B;
		public static final byte KEY_CODE_K1 = (byte) 0x5C;
		public static final byte KEY_CODE_K2 = (byte) 0x5D;
		public static final byte KEY_CODE_K3 = (byte) 0x5E;
		public static final byte KEY_CODE_K4 = (byte) 0x5F;
		public static final int KEY_CODE_VOLADD = 0xF0;
		public static final int KEY_CODE_VOLDEL = 0xF1;
	}

	// 车辆设置
	public static final byte CAR_SET = (byte) 0xA3;
	public static final byte TRACTIONCTRL = (byte) 0x01;
	public static final byte TrunLightonce = (byte) 0x03;
	public static final byte Msgtoneon = (byte) 0x05;
	public static final byte Warntoneon = (byte) 0x07;
	public static final byte ToneType = (byte) 0x09;
	public static final byte Mileunit = (byte) 0x0E;
	public static final byte Autobright = (byte) 0x10;
	public static final byte Rainsensor = (byte) 0xA5;
	public static final byte Interiorlight = (byte) 0xA6;
	public static final byte Parklockctrl = (byte) 0xA7;
	public static final byte NOT_SEND = (byte) 0x00;

	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_AIR_INFO & 0xFF;
	}

	@Override
	public int GetCarInfoCmdId() {
		// TODO Auto-generated method stub
		return (DATA_TYPE_CAR_INFO & 0xFF);
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return (DATA_TYPE_BACK_RADAR_INFO & 0xFF);
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return (DATA_TYPE_FRONT_RADAR_INFO & 0xFF);
	}

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return (DATA_TYPE_BASE_INFO & 0xFF);
	}

	@Override
	public int GetBackLightInfoCmdId() {
		// TODO Auto-generated method stub
		return (DATA_TYPE_BACK_LIGHT_INFO & 0xFF);
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	public int GetWarnInfoCmdId() {
		return DATA_TYPE_CAR_WARN_INFO & 0xFF;
	}

	public int GetSyncAudioCmdId() {
		return DATA_TYPE_SYNC_ADUIO & 0xFF;
	}

	public int GetSyncStateCmdId() {
		return DATA_TYPE_SYNC_STATE & 0xFF;
	}

	public int GetSyncMenuCmdId() {
		return DATA_TYPE_SYNC_MENU_INFO & 0xFF;
	}

	public int GetSyncOptionCmdId() {
		return DATA_TYPE_SYNC_MENU_OPTION & 0xFF;
	}

	public int GetSyncMediaTimeCmdId() {
		return DATA_TYPE_SYNC_MEDIA_INFO & 0xFF;
	}

	public int GetSyncTalkTimeCmdId() {
		return DATA_TYPE_SYNC_TALK_INFO & 0xFF;
	}

	@Override
	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo) {
		// TODO Auto-generated method stub
		return super.parseCarInfo(packet, carInfo);
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		// TODO Auto-generated method stub
		int cursor = 0;
		byte DataType = (byte) packet[cursor + 1];
		cursor += 3; // 跳过 2E、Data Type、Length字段

		if (DataType == DATA_TYPE_BACK_RADAR_INFO) {

			radarInfo.mBackLeftDis = (byte) (DataConvert
					.byteToInt(packet[cursor]) / 0x03 + (((DataConvert
					.byteToInt(packet[cursor]) % 0x03) == 0) ? 1 : 0));
			radarInfo.mBackLeftDis = radarInfo.mBackLeftDis > 10 ? 10
					: radarInfo.mBackLeftDis;

			radarInfo.mBackLeftCenterDis = (byte) (DataConvert
					.byteToInt(packet[cursor + 1]) / 0x03 + (((DataConvert
					.byteToInt(packet[cursor + 1]) % 0x03) == 0) ? 1 : 0));
			radarInfo.mBackLeftCenterDis = radarInfo.mBackLeftCenterDis > 10 ? 10
					: radarInfo.mBackLeftCenterDis;

			radarInfo.mBackRightCenterDis = (byte) (DataConvert
					.byteToInt(packet[cursor + 2]) / 0x03 + (((DataConvert
					.byteToInt(packet[cursor + 2]) % 0x03) == 0) ? 1 : 0));
			radarInfo.mBackRightCenterDis = radarInfo.mBackRightCenterDis > 10 ? 10
					: radarInfo.mBackRightCenterDis;

			radarInfo.mBackRightDis = (byte) (DataConvert
					.byteToInt(packet[cursor + 3]) / 0x03 + (((DataConvert
					.byteToInt(packet[cursor + 3]) % 0x03) == 0) ? 1 : 0));
			radarInfo.mBackRightDis = radarInfo.mBackRightDis > 10 ? 10
					: radarInfo.mBackRightDis;
		} else if (DataType == DATA_TYPE_FRONT_RADAR_INFO) {

			radarInfo.mFrontLeftDis = (byte) (DataConvert
					.byteToInt(packet[cursor]) / 0x03 + (((DataConvert
					.byteToInt(packet[cursor]) % 0x03) == 0) ? 1 : 0));
			radarInfo.mFrontLeftDis = radarInfo.mFrontLeftDis > 10 ? 10
					: radarInfo.mFrontLeftDis;

			radarInfo.mFrontLeftCenterDis = (byte) (DataConvert
					.byteToInt(packet[cursor + 1]) / 0x03 + (((DataConvert
					.byteToInt(packet[cursor + 1]) % 0x03) == 0) ? 1 : 0));
			radarInfo.mFrontLeftCenterDis = radarInfo.mFrontLeftCenterDis > 10 ? 10
					: radarInfo.mFrontLeftCenterDis;

			radarInfo.mFrontRightCenterDis = (byte) (DataConvert
					.byteToInt(packet[cursor + 2]) / 0x03 + (((DataConvert
					.byteToInt(packet[cursor + 2]) % 0x03) == 0) ? 1 : 0));
			radarInfo.mFrontRightCenterDis = radarInfo.mFrontRightCenterDis > 10 ? 10
					: radarInfo.mFrontRightCenterDis;

			radarInfo.mFrontRightDis = (byte) (DataConvert
					.byteToInt(packet[cursor + 3]) / 0x03 + (((DataConvert
					.byteToInt(packet[cursor + 3]) % 0x03) == 0) ? 1 : 0));
			radarInfo.mFrontRightDis = radarInfo.mFrontRightDis > 10 ? 10
					: radarInfo.mFrontRightDis;
		}

		radarInfo.mbyRightShowType = 0;

		return radarInfo;
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

		airInfo.mAutoLight1 = (byte) ((airData0 >> 3) & 0x1);
		airInfo.mDaulLight = (byte) ((airData0 >> 2) & 0x1);
		airInfo.mMaxForntLight = (byte) ((airData0 >> 1) & 0x1);

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

		airInfo.mTempUnit = (byte) ((packet[cursor + 2] >> 6) & 0x1);

		byte airData2 = (byte) packet[cursor];

		if (airInfo.mTempUnit == 0) {
			airInfo.mMinTemp = (float) 15.5;
			airInfo.mMaxTemp = (float) 29.5;
			airInfo.mLeftTemp = (float) (airData2 / 2.0);
		} else {
			airInfo.mMinTemp = (float) 59.9;
			airInfo.mMaxTemp = (float) 85.1;
			airInfo.mLeftTemp = (float) ((airData2 / 2.0) * 1.8 + 32);
		}

		// Data 3
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		airInfo.mRightTemp = (float) (18 + (airData3 - 1) * 0.5);

		if (airInfo.mTempUnit == 0) {
			airInfo.mRightTemp = (float) (airData3 / 2.0);
		} else {
			airInfo.mRightTemp = (float) ((airData3 / 2.0) * 1.8 + 32);
		}

		// Data 4
		cursor += 1;
		byte airData4 = (byte) packet[cursor];
		airInfo.mAcMax = (byte) ((airData4 >> 2) & 0x1);

		cursor += 1;
		airInfo.mOutTempEnable = true;
		byte airData5 = (byte) packet[cursor];
		if (airInfo.mTempUnit == 0) {
			if (DataConvert.GetBit(airData5, 7) == 1) {
				airInfo.mOutTemp = -(0xFF - (airData5 & 0xFF));
			} else {
				airInfo.mOutTemp = airData5 & 0xFF;
			}
		} else {
			airInfo.mOutTemp = (float) (airData5 * 1.8 + 32);
		}

		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!equals(mbyAirInfo, packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}

	private boolean equals(byte[] bylastInfo, byte[] byNewInfo) {

		int iIndex = 0;
		boolean bequals = true;

		for (byte b : byNewInfo) {
			if (iIndex == 8) {
			} else {

				if ((b != bylastInfo[iIndex]) && bequals) {
					bequals = false;
				}
			}
			iIndex++;
		}

		return bequals;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		byte baseData1 = (byte) packet[cursor];

		baseInfo.mTailBoxDoor = (byte) ((baseData1 >> 3) & 0x1);
		baseInfo.mRightBackDoor = (byte) ((baseData1 >> 5) & 0x1);
		baseInfo.mLeftBackDoor = (byte) ((baseData1 >> 4) & 0x1);
		baseInfo.mRightFrontDoor = (byte) ((baseData1 >> 7) & 0x1);
		baseInfo.mLeftFrontDoor = (byte) ((baseData1 >> 6) & 0x1);

		cursor += 1;
		byte baseData0 = (byte) packet[cursor];
		baseInfo.mLightState = (byte) ((baseData0 >> 2) & 0x1);
		baseInfo.mPStopBlockState = (byte) ((baseData0 >> 3) & 0x1);
		baseInfo.mBackCarState = (byte) ((baseData0 >> 0) & 0x1);

		if (packet.length > 6) {
			cursor += 1;
			byte bydatas = packet[cursor];
			baseInfo.mbyTractionCtrl = (byte) DataConvert.GetBit(bydatas, 0);
			baseInfo.mbyTrunLightOnce = (byte) DataConvert.GetBit(bydatas, 1);
			baseInfo.mbyMsgToneOn = (byte) DataConvert.GetBit(bydatas, 2);
			baseInfo.mbyWarnToneOn = (byte) DataConvert.GetBit(bydatas, 3);
			baseInfo.mbyToneType = (byte) ((bydatas & 0x70) >> 4);
			baseInfo.mbyMileUnit = (byte) DataConvert.GetBit(bydatas, 7);

			cursor += 1;
			byte bydatas1 = packet[cursor];
			baseInfo.mbyPlan = (byte) (bydatas1 & 0x07);
			baseInfo.mbySpeed = (byte) ((bydatas1 & 0x38) >> 3);
			baseInfo.mbyAotuBright = (byte) DataConvert.GetBit(bydatas1, 7);

			cursor += 1;
			byte bydata2 = packet[cursor];
			baseInfo.mbyEngineHotPer = (byte) (bydata2 & 0x7F);
			baseInfo.mbyShowEngineHot = (byte) DataConvert.GetBit(bydata2, 7);

			cursor += 1;
			byte bydata3 = packet[cursor];
			baseInfo.mbyRainSensor = (byte) DataConvert.GetBit(bydata3, 0);
			baseInfo.mbyInteriorlight = (byte) DataConvert.GetBit(bydata3, 1);
		}

		return baseInfo;
	}

	@Override
	public VerInfo parseVersionInfo(byte[] packet) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		VerInfo version = new VerInfo();
		byte[] ver = new byte[16];
		System.arraycopy(packet, cursor, ver, 0, 16);
		version.mVersion = DataConvert.BytesToStr(ver);
		return version;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor] & 0xFF;
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

	public CanAudio parseSyncAudio(byte[] packet, CanAudio canAudio) {
		int cursor = 3;
		canAudio.mbyAudioState = packet[cursor];

		switch (canAudio.mbyAudioState) {
		case 0:
			canAudio.mbshow = false;
			break;
		case 1:
			canAudio.mbshow = true;
			canAudio.mbyMode = 2;
			break;
		case 2:
			canAudio.mbshow = false;
			break;
		case 3:
			canAudio.mbyMode = 1;
			canAudio.mbshow = true;
			break;
		case 4:
			canAudio.mbyMode = 2;
			canAudio.mbshow = true;
			break;
		case 5:
			canAudio.mbshow = false;
			break;
		}

		return canAudio;
	}

	public SyncState parseSyncState(byte[] packet, SyncState state) {
		int cursor = 3;

		state.mbySyncMode = packet[cursor];

		state.mbyPresentDev = (byte) DataConvert.GetBit(packet[cursor + 1], 0);
		state.mbyConnBt = (byte) DataConvert.GetBit(packet[cursor + 1], 1);
		state.mbyShowMsg = (byte) DataConvert.GetBit(packet[cursor + 1], 3);
		state.mbySpeechOn = (byte) DataConvert.GetBit(packet[cursor + 1], 4);
		state.mbyTalking = (byte) DataConvert.GetBit(packet[cursor + 1], 5);
		state.mbyEnableKey = (byte) DataConvert.GetBit(packet[cursor + 1], 6);

		state.mbySignal = (byte) (0x0f & packet[cursor + 2]); // 低四位信号
		state.mbyPower = (byte) ((0xf0 & packet[cursor + 2]) >> 4); // 高四位电量

		return state;
	}

	public HashMap<Integer, DDef.Sync_listInfo> mSyncHashMap = new HashMap<Integer, DDef.Sync_listInfo>();

	public SyncMenu parseSyncMenu(byte[] packet, SyncMenu syncMenu) {
		int cursor = 3;

		syncMenu.mbyMenuType = packet[cursor];
		syncMenu.mbySelOption = packet[cursor + 1];
		syncMenu.mbyMenuPer = packet[cursor + 2];
		syncMenu.mbyMsgType = packet[cursor + 3];
		syncMenu.mbyMsgSelOption = packet[cursor + 4];
		syncMenu.mbyMenuIcon = packet[cursor + 5];
		syncMenu.mbyMenuBar = packet[cursor + 6];
		syncMenu.mHashMap = new HashMap<Integer, DDef.Sync_listInfo>();
		syncMenu.mHashMap.putAll(mSyncHashMap);
		mSyncHashMap.clear();

		return syncMenu;
	}

	private int CheckIconId(int iId) {
		int iIndex = 0;
		boolean bfind = false;
		for (int iter : DDef.tbArIcon) {
			if (iter == iId) {
				bfind = true;
				break;
			}

			iIndex++;
		}

		if (!bfind) {
			iIndex = 0;
		}

		return iIndex;
	}

	public SyncOption parseSyncOption(byte[] packet, SyncOption syncOption) {
		int cursor = 3;

		// 状态选项
		syncOption.mbyRowNum = packet[cursor];

		if (syncOption.mbyRowNum <= 10) {
			// 文本
			syncOption.mbyLineText = (byte) (0x0f & packet[cursor + 1]);
		} else if (syncOption.mbyRowNum <= 18) {
			// 按键
			syncOption.mbyKeyState = (byte) (0x0f & packet[cursor + 1]);
		}

		if (syncOption.mbyRowNum >= 1 && syncOption.mbyRowNum <= 5) {
			syncOption.mbyRowNum -= 1;
			syncOption.mbyTextType = 0;
		} else if (syncOption.mbyRowNum >= 6 && syncOption.mbyRowNum <= 10) {
			syncOption.mbyRowNum -= 6;
			syncOption.mbyTextType = 1;
		} else if (syncOption.mbyRowNum >= 11 && syncOption.mbyRowNum <= 14) {
			syncOption.mbyRowNum -= 11;
			syncOption.mbyTextType = 2;
		} else if (syncOption.mbyRowNum >= 15 && syncOption.mbyRowNum <= 18) {
			syncOption.mbyRowNum -= 15;
			syncOption.mbyTextType = 3;
		}

		syncOption.mbyEnable = (byte) DataConvert.GetBit(packet[cursor + 1], 4);
		syncOption.mbyLeftIcon = packet[cursor + 2];
		syncOption.mbyRightIcon = packet[cursor + 3];

		byte[] bydatas = new byte[packet.length - 8];
		System.arraycopy(packet, cursor + 4, bydatas, 0, packet.length - 8);

		try {
			syncOption.strText = new String(bydatas, "unicode").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (syncOption.mbyTextType == 2 || syncOption.mbyTextType == 3) {
			if (syncOption.mbyLeftIcon == 0x12) {
				syncOption.mbyLeftIcon = 1;
			} else if (syncOption.mbyLeftIcon == 0xcf) {
				syncOption.mbyLeftIcon = 2;
			} else if (syncOption.mbyLeftIcon == 0xd0) {
				syncOption.mbyLeftIcon = 3;
			} else {
				syncOption.mbyLeftIcon = 4;
			}
		}

		if (syncOption.mbyTextType == 0 || syncOption.mbyTextType == 1) {
			Sync_listInfo objListInfo = new Sync_listInfo();
			objListInfo.ilefticon = CheckIconId(syncOption.mbyLeftIcon & 0xFF);

			if (syncOption.mbyRightIcon == 0x15) {
				objListInfo.irighticon = 1;
			} else if (syncOption.mbyRightIcon == 0x76) {
				objListInfo.irighticon = 2;
			} else {
				objListInfo.irighticon = 0;
			}

			objListInfo.strText = syncOption.strText;
			mSyncHashMap.put((int) syncOption.mbyRowNum, objListInfo);
		}

		return syncOption;
	}

	public SyncMediaTime parseMediaTime(byte[] packet,
			SyncMediaTime syncMediaTime) {
		int cursor = 3;

		syncMediaTime.mbyHuor = packet[cursor];
		syncMediaTime.mbyMinute = packet[cursor + 1];
		syncMediaTime.mbySecond = packet[cursor + 2];

		return syncMediaTime;
	}

	public SyncTalkTime parseTalkTime(byte[] packet, SyncTalkTime syncTalkTime) {
		int cursor = 3;

		syncTalkTime.mbyHuor = 0;
		syncTalkTime.mbyMinute = packet[cursor + 1];
		syncTalkTime.mbySecond = packet[cursor + 2];

		return syncTalkTime;
	}

	private String mstrKeyCode = null;

	private WheelKeyInfo TranslateKey(WheelKeyInfo keyInfo) {
		int iPhoneState = 0;
		keyInfo.mbyKeyRepeat = 0;
		
		switch (keyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_UP:
			keyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DOWN:
			keyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_MENU_NEXT:
			iPhoneState = CanPopWind.mObjGdSharedData.getMediaInfo()
					.getPhonestate();
			if (iPhoneState == PhoneState.INCOMING
					|| iPhoneState == PhoneState.OUTGOING
					|| iPhoneState == PhoneState.SPEAKING) {
				keyInfo.mstrKeyCode = DDef.Phone_hang;
			} else {
				keyInfo.mstrKeyCode = DDef.Media_next;
			}
			break;
		case KeyCode.KEY_CODE_MENU_LAST:
			iPhoneState = CanPopWind.mObjGdSharedData.getMediaInfo()
					.getPhonestate();
			if (iPhoneState == PhoneState.INCOMING) {
				keyInfo.mstrKeyCode = DDef.Phone_dial;
			} else {
				keyInfo.mstrKeyCode = DDef.Media_pre;
			}
			break;
		case KeyCode.KEY_CODE_TEL:
			keyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_PMUTE:
		case KeyCode.KEY_CODE_MUTE:
			keyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_SRC:
			keyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_MENU_UP:
			keyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_MENU_DN:
			keyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_MENU_LEFT:
			keyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_MENU_RIGHT:
			keyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_MENU_OK:
			keyInfo.mstrKeyCode = DDef.Media_pause;
			break;
		case KeyCode.KEY_CODE_NUM_0:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num0;
			break;
		case KeyCode.KEY_CODE_NUM_1:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num1;
			break;
		case KeyCode.KEY_CODE_NUM_2:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num2;
			break;
		case KeyCode.KEY_CODE_NUM_3:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num3;
			break;
		case KeyCode.KEY_CODE_NUM_4:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num4;
			break;
		case KeyCode.KEY_CODE_NUM_5:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num5;
			break;
		case KeyCode.KEY_CODE_NUM_6:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num6;
			break;
		case KeyCode.KEY_CODE_NUM_7:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num7;
			break;
		case KeyCode.KEY_CODE_NUM_8:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num8;
			break;
		case KeyCode.KEY_CODE_NUM_9:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Num9;
			break;
		case KeyCode.KEY_CODE_NUM_a:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Numa;
			break;
		case KeyCode.KEY_CODE_NUM_b:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Numb;
			break;
		case KeyCode.KEY_CODE_STRIUS:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Speech;
			break;
		case KeyCode.KEY_CODE_RADIO:
			keyInfo.mstrKeyCode = DDef.Src_radio;
			break;
		case KeyCode.KEY_CODE_CD:
			keyInfo.mstrKeyCode = DDef.Src_dvd;
			break;
		case KeyCode.KEY_CODE_AUX:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Src_aux;
			break;
		case KeyCode.KEY_CODE_MENU:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case KeyCode.KEY_CODE_INFO:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Src_info;
			break;
		case KeyCode.KEY_CODE_K1:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.K1;
			break;
		case KeyCode.KEY_CODE_K2:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.K2;
			break;
		case KeyCode.KEY_CODE_K3:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.K3;
			break;
		case KeyCode.KEY_CODE_K4:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.K4;
			break;
		case KeyCode.KEY_CODE_OK:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Enter;
			break;
		case KeyCode.KEY_CODE_LEFT:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Left;
			break;
		case KeyCode.KEY_CODE_RIGHT:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Right;
			break;
		case KeyCode.KEY_CODE_UP:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Up;
			break;
		case KeyCode.KEY_CODE_DN:
			keyInfo.mbyKeyRepeat = CanKey.KEY_REPEAT;
			keyInfo.mstrKeyCode = DDef.Down;
			break;
		case KeyCode.KEY_CODE_SOUND:
			keyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case KeyCode.KEY_CODE_PHONE:
			keyInfo.mstrKeyCode = DDef.Src_bt;
			break;
		case KeyCode.KEY_CODE_CLOCK:
			keyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case KeyCode.KEY_CODE_POWER:
			keyInfo.mstrKeyCode = DDef.Power;
			break;
		case KeyCode.KEY_CODE_TELUP:
			keyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_HUNG:
			keyInfo.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_EJECT:
			keyInfo.mstrKeyCode = DDef.Eject;
			break;
		case KeyCode.KEY_CODE_TA:
			keyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case KeyCode.KEY_CODE_DSP:
			keyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case KeyCode.KEY_CODE_DISPLAY:
			keyInfo.mstrKeyCode = DDef.Blacklight;
			break;
		case KeyCode.KEY_CODE_VOLADD:
			keyInfo.mKnobSteps = keyInfo.mKeyStatus;
			keyInfo.mKeyStatus = CanKey.KEY_KNOB;
			keyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOLDEL:
			keyInfo.mKnobSteps = keyInfo.mKeyStatus;
			keyInfo.mKeyStatus = CanKey.KEY_KNOB;
			keyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		}

		if (keyInfo.mKeyCode != 0) {
			mstrKeyCode = keyInfo.mstrKeyCode;
		}
		if (keyInfo.mKeyCode == 0) {
			keyInfo.mstrKeyCode = mstrKeyCode;
			mstrKeyCode = null;
		}

		return keyInfo;
	}
	
	@Override
	public Message getCanKeyAttr(String strKeyCode) {
		// TODO Auto-generated method stub
		byte bycmd = 0x00;
		
		if (strKeyCode.equals(DDef.Num0)) {
			bycmd = 0x0D;
		}else if (strKeyCode.equals(DDef.Num1)) {
			bycmd = 0x0E;
		}else if (strKeyCode.equals(DDef.Num2)) {
			bycmd = 0x0F;
		}else if (strKeyCode.equals(DDef.Num3)) {
			bycmd = 0x10;
		}else if (strKeyCode.equals(DDef.Num4)) {
			bycmd = 0x11;
		}else if (strKeyCode.equals(DDef.Num5)) {
			bycmd = 0x12;
		}else if (strKeyCode.equals(DDef.Num6)) {
			bycmd = 0x13;
		}else if (strKeyCode.equals(DDef.Num7)) {
			bycmd = 0x14;
		}else if (strKeyCode.equals(DDef.Num8)) {
			bycmd = 0x15;
		}else if (strKeyCode.equals(DDef.Num9)) {
			bycmd = 0x16;
		}else if (strKeyCode.equals(DDef.Numa)) {
			bycmd = 0x17;
		}else if (strKeyCode.equals(DDef.Numb)) {
			bycmd = 0x18;
		}else if (strKeyCode.equals(DDef.Speech)) {
			bycmd = 0x01;
		}else if (strKeyCode.equals(DDef.Src_aux)) {
			bycmd = 0x1B;
		}else if (strKeyCode.equals(DDef.Src_home)) {
			bycmd = 0x02;
		}else if (strKeyCode.equals(DDef.Src_info)) {
			bycmd = 0x06;
		}else if (strKeyCode.equals(DDef.K1)) {
			bycmd = 0x1C;			
		}else if (strKeyCode.equals(DDef.K2)) {
			bycmd = 0x1D;
		}else if (strKeyCode.equals(DDef.K3)) {
			bycmd = 0x1E;
		}else if (strKeyCode.equals(DDef.K4)) {
			bycmd = 0x1F;
		}else if (strKeyCode.equals(DDef.Enter)) {
			bycmd = 0x0C;
		}else if (strKeyCode.equals(DDef.Left)) {
			bycmd = 0x19;		
		}else if (strKeyCode.equals(DDef.Right)) {
			bycmd = 0x1A;			
		}else if (strKeyCode.equals(DDef.Up)) {
			bycmd = 0x0A;				
		}else if (strKeyCode.equals(DDef.Down)) {
			bycmd = 0x0B;			
		}
		
		return sendcmd(bycmd);
	}

	@Override
	public Message sendcmd(byte bycmd) {
		// TODO Auto-generated method stub
		byte[] bydata = new byte[2];
		bydata[0] = (byte) 0xA1;
		bydata[1] = bycmd;

		return CanTxRxStub.getTxMessage(DATA_TYPE_CTRL_CMD, DATA_TYPE_CTRL_CMD,
				bydata, this);
	}

	@Override
	public Message senddata(byte bycmd, byte byval) {
		// TODO Auto-generated method stub
		return super.senddata(bycmd, byval);
	}
}
