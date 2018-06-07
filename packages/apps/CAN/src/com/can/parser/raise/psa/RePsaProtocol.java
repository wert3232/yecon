package com.can.parser.raise.psa;



import java.util.ArrayList;

import android.os.Message;


import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.PsaCarState;
import com.can.parser.DDef.PsaCruSpeed;
import com.can.parser.DDef.PsaDiagInfo;
import com.can.parser.DDef.PsaFuncInfo;
import com.can.parser.DDef.PsaMemSpeed;
import com.can.parser.DDef.PsaTripComputer;
import com.can.parser.DDef.PsaWarnInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

public class RePsaProtocol extends ReProtocol {

	final byte mHeadFlag = (byte) 0xFD;

	final byte mHeadCursor = (byte) 0;
	final byte mDataLenCursor = (byte) 1;
	final byte mCmdIDCursor = (byte) 2;
	final byte mDataCursor = (byte) 3;
	// Slave -> Host
	
	public static final byte DATA_TYPE_BACK_LIGHT_INFO = 0x01; 	// 背光调节
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = 0x02; 	// 方向盘按键信息
	public static final byte DATA_TYPE_AIR_INFO = 0x21; 		// 空调信息
	public static final byte DATA_TYPE_WHEEL_INFO = 0x29; 		// 方向盘转角
	public static final byte DATA_TYPE_RADAR_INFO = 0x30; 		// 全雷达信息
	public static final byte DATA_TYPE_REVER_RADIO_INFO = 0x32;	// 倒车雷达
	public static final byte DATA_TYPE_OBD_PAGE0_INFO = 0x33;	
	public static final byte DATA_TYPE_OBD_PAGE1_INFO = 0x34;
	public static final byte DATA_TYPE_OBD_PAGE2_INFO = 0x35;
	public static final byte DATA_TYPE_OUT_TEMP_INFO = 0x36;
	public static final byte DATA_TYPE_WARN_RECORD_INFO = 0x37;
	public static final byte DATA_TYPE_CAR_STATE_INFO = 0x38;
	public static final byte DATA_TYPE_CAR_FUNC_INFO = 0x39;
	public static final byte DATA_TYPE_DIAGNOSIS_INFO = 0x3A;
	public static final byte DATA_TYPE_REMEM_SPEED_INFO = 0x3B;
	public static final byte DATA_TYPE_SPEED_INFO = 0x3D;
	public static final byte DATA_TYPE_CAR_INFO = 0x41; 		// 车辆信息
	
	// Host -> Slave
	public static final byte DATA_TYPE_CAR_STATE_REQUEST = (byte) 0x80; 
	public static final byte DATA_TYPE_CAR_TRIP_REQUEST = (byte) 0x82;
	public static final byte DATA_TYPE_AIR_SET_REQUEST = (byte) 0x8A;
	public static final byte DATA_TYPE_REMEM_SPEED_REQUEST = (byte) 0x88;
	public static final byte DATA_TYPE_SPEED_REQUEST = (byte) 0x89;
	public static final byte DATA_TYPE_TIME_REQUEST = (byte) 0xA6;
	public static final byte DATA_TYPE_GET_REQUEST = (byte) 0x8F;

	public class KeyCode {
		public static final byte KEY_CODE_MENU = 0x02;
		public static final byte KEY_CODE_UP = 0x03; 
		public static final byte KEY_CODE_DOWN = 0x04; 
		public static final byte KEY_CODE_OK = 0x07;
		public static final byte KEY_CODE_ESC = 0x08;
		public static final byte KEY_CODE_MODE = 0x10; 
		public static final byte KEY_CODE_SOURCE = 0x11;
		public static final byte KEY_CODE_SEEK_ADD = 0x12;
		public static final byte KEY_CODE_SEEK_DEL = 0x13; 
		public static final byte KEY_CODE_VOL_ADD = 0x14; 
		public static final byte KEY_CODE_VOL_DEL = 0x15; 
		public static final byte KEY_CODE_MUTE = 0x16;
		public static final byte KEY_CODE_MENU_UP = 0x17; 	//向上选择收音频道/曲目列表
		public static final byte KEY_CODE_MENU_DN = 0x18;
		public static final byte KEY_CODE_PAGE_SW = 0x20; 	//切换行车电脑信息页显示
		public static final byte KEY_CODE_MENU_EX = 0x21;		//菜单模式
		public static final byte KEY_CODE_MEM_SPEED = 0x22;	//弹出记忆速度界面
		public static final byte KEY_CODE_BT = 0x23;
		public static final byte KEY_CODE_PUSH_TO_TALK = 0x29;
		public static final byte KEY_CODE_PICK_UP = 0x30;
		public static final byte KEY_CODE_HUNG = 0x31;
		public static final byte KEY_CODE_NAVI = 0x32;
		public static final byte KEY_CODE_RADIO = 0x33;
		public static final byte KEY_CODE_SETUP = 0x34;

		public static final byte KEY_CODE_ADDR = 0x35; 
		public static final byte KEY_CODE_MEDIA = 0x36; 
		public static final byte KEY_CODE_TRAF = 0x37; 
		public static final byte KEY_CODE_UP_EX = 0x38;
		public static final byte KEY_CODE_DN_EX = 0x39; 
		public static final byte KEY_CODE_LEFT = 0x40; 
		public static final byte KEY_CODE_RIGHT = 0x41; 
		public static final byte KEY_CODE_SCROLL_UP = 0x42; 
		public static final byte KEY_CODE_SCROLL_DN = 0x43;
		public static final byte KEY_CODE_NUM1 = 0x44;
		public static final byte KEY_CODE_NUM2 = 0x45;
		public static final byte KEY_CODE_NUM3 = 0x46;
		public static final byte KEY_CODE_NUM4 = 0x47;
		public static final byte KEY_CODE_NUM5 = 0x48;
		public static final byte KEY_CODE_NUM6 = 0x49;
		public static final byte KEY_CODE_SRC = 0x4A;
		public static final byte KEY_CODE_BAND = 0x50;
		public static final byte KEY_CODE_LSIT = 0x51;
		public static final byte KEY_CODE_SOUND = 0x52;
		public static final byte KEY_CODE_TA_INFO = 0x53;
		public static final byte KEY_CODE_DARK	= 0x54;
		public static final byte KEY_CODE_EJECT = 0x55; 
		public static final byte KEY_CODE_NEXT = 0x56; 
		public static final byte KEY_CODE_PREV = 0x57; 
		public static final byte KEY_CODE_GO_UP = 0x58; 
		public static final byte KEY_CODE_GO_DN = 0x59;
		public static final byte KEY_CODE_CHECK = 0x60;		//DVD 收到此按键发送诊断请求 0x87
		public static final int KEY_CODE_POWER = 0x80;
	}

	@Override
	public byte[] getPacket(int cmdID, int cmdSubID, byte[] datas) {
		// 3 是 head + datatype + len, 1 是 checksum
		byte[] packet = new byte[3 + 2 + datas.length];

		// 填充包头
		packet[0] = mHeadFlag;

		// 填充length datas + 2 check + 1datatype + 1 self
		packet[1] = (byte) (4 + datas.length & 0xFF);

		// 填充datatype
		packet[2] = (byte) (cmdID & 0xFF);

		// 填充 data section
		System.arraycopy(datas, 0, packet, mDataCursor, datas.length);

		// 计算 checksum，填充 checksum section

		byte[] tmp = DataConvert.Hi2Lo2Byte(caculateCheckSum(packet, 0,
				packet.length));

		packet[packet.length - 2] = tmp[1];

		packet[packet.length - 1] = tmp[0];

		return packet;
	}

	@Override
	public boolean isPacketHeader(byte[] buffer, int cursor) {
		return (buffer[mHeadCursor + cursor] == mHeadFlag);
	}

	@Override
	public int parseDataLength(byte[] buffer, int cursor) {
		int dataLength = (int) (buffer[mDataLenCursor + cursor] & 0xFF);
		return dataLength;
	}

	@Override
	public int ParseRegisterCmdId(byte[] buffer, int cursor) {
		return (int) (buffer[cursor + mCmdIDCursor] & 0xFF);
	}

	@Override
	public int parseRegisterSubId(byte[] buffer, int cursor) {
		return (int) (buffer[cursor + mCmdIDCursor] & 0xFF);
	}

	@Override
	public int getCmdIDCursor() {
		return mCmdIDCursor;
	}

	@Override
	public int getCmdSubIDCursor() {
		return mCmdIDCursor;
	}

	@Override
	public int caculateCheckSum(byte[] buffer, int cursor, int packetLength) {
		// 一个有效包的开始 Header(占一字节)+ DataType + byLength + Data1 + Data2 + …
		// + DataN + Checksum
		// 计算当前包数据长度 byLength 是从 Data1 到 DataN 的所有数据的字节数之和
		// 计算checksum 从DataType（包含）开始到DataN（包含）
		// CheckSum = SUM(DataType + length + Data0 + ... + DataN)
		int iCheckSum = 0;
		int iDataEnd = packetLength - 2;
		int i = 0;

		iDataEnd += cursor;

		for (i = mDataLenCursor + cursor; i <= iDataEnd; i++) {
			iCheckSum += (buffer[i] & 0xFF);
		}
		return iCheckSum & 0xFF;
	}

	@Override
	public int parsePacketLength(byte[] buffer, int cursor) {
		int packetLength = parseDataLength(buffer, cursor);

		// 1 是 Head code :0xFD
		packetLength += 1;

		return packetLength;
	}

	@Override
	public byte[] connect() {
		byte[] byData = { (byte) 0x01 };
		byte[] byConCmd = getPacket(0x04, 0x00, byData);
		return byConCmd;
	}

	@Override
	public int GetAirInfoCmdId() {
		return DATA_TYPE_AIR_INFO;
	}

	@Override
	public int GetCarInfoCmdId() {
		return 0;
	}

	@Override
	public int GetCarInfoRequestCmdId() {
		return 0;
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		return DATA_TYPE_RADAR_INFO;
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		return DATA_TYPE_REVER_RADIO_INFO;
	}

	@Override
	public int GetWheelInfoCmdId() {
		return DATA_TYPE_WHEEL_INFO;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		return DATA_TYPE_WHEEL_KEY_INFO;
	}

	@Override
	public int GetBackLightInfoCmdId() {
		return DATA_TYPE_BACK_LIGHT_INFO;
	}
	
	public int GetOutTempInfoCmdId() {
		return DATA_TYPE_OUT_TEMP_INFO;
	}

	private String mstrKeyCode = null;
	public WheelKeyInfo TranslateKey(WheelKeyInfo wheelKeyInfo) {
		wheelKeyInfo.bInternal = false;
		switch (wheelKeyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_MENU:
		case KeyCode.KEY_CODE_MENU_EX:
		case KeyCode.KEY_CODE_LSIT:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case KeyCode.KEY_CODE_UP:
		case KeyCode.KEY_CODE_SEEK_DEL:
		case KeyCode.KEY_CODE_MENU_UP:
		case KeyCode.KEY_CODE_UP_EX:
		case KeyCode.KEY_CODE_LEFT:
		case KeyCode.KEY_CODE_SCROLL_UP:
		case KeyCode.KEY_CODE_PREV:
		case KeyCode.KEY_CODE_GO_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_DOWN:
		case KeyCode.KEY_CODE_SEEK_ADD:
		case KeyCode.KEY_CODE_MENU_DN:
		case KeyCode.KEY_CODE_DN_EX:
		case KeyCode.KEY_CODE_RIGHT:
		case KeyCode.KEY_CODE_SCROLL_DN:
		case KeyCode.KEY_CODE_NEXT:
		case KeyCode.KEY_CODE_GO_DN:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_OK:
			wheelKeyInfo.mstrKeyCode = DDef.Enter;
			break;
		case KeyCode.KEY_CODE_ESC:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case KeyCode.KEY_CODE_MODE:
		case KeyCode.KEY_CODE_SOURCE:
		case KeyCode.KEY_CODE_MEDIA:
		case KeyCode.KEY_CODE_SRC:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_VOL_ADD:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DEL:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_PAGE_SW:
			wheelKeyInfo.mstrKeyCode = DDef.PAGE_SW;
			wheelKeyInfo.bInternal = true;
			break;
		case KeyCode.KEY_CODE_MEM_SPEED:
			wheelKeyInfo.mstrKeyCode = DDef.MEM_SPEED;
			wheelKeyInfo.bInternal = true;
			break;
		case KeyCode.KEY_CODE_BT:
		case KeyCode.KEY_CODE_PUSH_TO_TALK:
		case KeyCode.KEY_CODE_PICK_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_HUNG:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_NAVI:
		case KeyCode.KEY_CODE_ADDR:
		case KeyCode.KEY_CODE_TRAF:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case KeyCode.KEY_CODE_RADIO:
		case KeyCode.KEY_CODE_BAND:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_fm;
			break;
		case KeyCode.KEY_CODE_SETUP:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case KeyCode.KEY_CODE_SOUND:
			wheelKeyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case KeyCode.KEY_CODE_TA_INFO:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case KeyCode.KEY_CODE_DARK:
			wheelKeyInfo.mstrKeyCode = DDef.Blacklight;
			break;
		case KeyCode.KEY_CODE_EJECT:
			wheelKeyInfo.mstrKeyCode = DDef.Eject;
			break;
		case KeyCode.KEY_CODE_POWER:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case KeyCode.KEY_CODE_CHECK:
			break;
		}

		if (wheelKeyInfo.mKeyCode != 0) {
			mstrKeyCode = wheelKeyInfo.mstrKeyCode;
			wheelKeyInfo.mKeyStatus = 0x01;
		}
		if (wheelKeyInfo.mKeyCode == 0) {
			wheelKeyInfo.mstrKeyCode = mstrKeyCode;
			wheelKeyInfo.mKeyStatus = 0x00;
			mstrKeyCode = null;
		}
		return wheelKeyInfo;
	}

	@Override
	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo) {
		return null;
	}

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		int cursor = 3; // 跳过2E、Data Type、Length字段
		// Data 0
		byte airData0 = (byte) packet[cursor++];
		airInfo.mAiron = (byte) (airData0 >> 7 & 0x01);
		airInfo.mAcState = (byte) (airData0 >> 6 & 0x01);
		airInfo.mCircleState = (byte) (airData0 >> 5 & 0x01);
		airInfo.mAutoLight1 = (byte) (airData0 >> 3 & 0x01);
		airInfo.mDaulLight = (byte) (airData0 >> 2 & 0x01);
		airInfo.mRearLight = (byte) (airData0 & 0x01);
		
		byte airData1 = (byte) packet[cursor++];
		airInfo.mUpwardWind = (byte) (airData1 >> 7 & 0x01);
		airInfo.mParallelWind = (byte) (airData1 >> 6 & 0x01);
		airInfo.mDowmWind = (byte) (airData1 >> 5 & 0x01);
		
		airInfo.mAutoLight1 = (byte) ((airData1 >> 5 & 0x07) == 0x00 ? 0x01 : 0x00);
		airInfo.mWindRate = (byte) (airData1 & 0x0F);
		airInfo.mMaxWindlv = 0x09;
		
		airInfo.mLeftTemp = (float) ((packet[cursor++] & 0xFF) * 0.5);
		airInfo.mRightTemp = (float) ((packet[cursor++] & 0xFF)* 0.5);
		airInfo.mMinTemp = 0x00;
		airInfo.mMaxTemp = (float) (0xFF * 0.5);
		
		byte airData4 = (byte) packet[cursor++];
		airInfo.mFWDefogger = (byte) (airData4 >> 7 & 0x01);
		airInfo.mAcMax = (byte) (airData4 >> 3 & 0x01);
		airInfo.mTempUnit = (byte) (airData4 & 0x01);
		
		return airInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		if (packet[2] == DATA_TYPE_RADAR_INFO) {
			
			radarInfo.mFrontLeftDis = getRadarValue(packet[cursor++]);
			radarInfo.mFrontRightCenterDis = getRadarValue(packet[cursor++]);
			radarInfo.mFrontLeftCenterDis = radarInfo.mFrontRightCenterDis;
			radarInfo.mFrontRightDis = getRadarValue(packet[cursor++]);
			
			radarInfo.mBackLeftDis = getRadarValue(packet[cursor++]);
			radarInfo.mBackRightCenterDis = getRadarValue(packet[cursor++]);
			radarInfo.mBackLeftCenterDis = radarInfo.mBackRightCenterDis;
			radarInfo.mBackRightDis = getRadarValue(packet[cursor]);
			
		} else if (packet[2] == DATA_TYPE_REVER_RADIO_INFO) {
			
			radarInfo.mRadarSwtich = (byte) (packet[cursor] == 0x03 ? 0x00 : 0x01);
			radarInfo.mRadarShowSwitch = (byte) (packet[cursor++] == 0x02 ? 0x01 : 0x00);
			radarInfo.mbyRightShowType = 0;
			
			radarInfo.mBackLeftDis = getRadarValue(packet[cursor++]);
			radarInfo.mBackRightCenterDis = getRadarValue(packet[cursor++]);
			radarInfo.mBackLeftCenterDis = radarInfo.mBackRightCenterDis;
			radarInfo.mBackRightDis = getRadarValue(packet[cursor++]);
			
			radarInfo.mFrontLeftDis = getRadarValue(packet[cursor++]);
			radarInfo.mFrontRightCenterDis = getRadarValue(packet[cursor++]);
			radarInfo.mFrontLeftCenterDis = radarInfo.mFrontRightCenterDis;
			radarInfo.mFrontRightDis = getRadarValue(packet[cursor]);
		}
		return radarInfo;
	}

	private byte getRadarValue(byte byCmd) {
		return (byte) (byCmd == 0x05 ? 0x00 : byCmd+ 1);
	}
	
	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// Data0
		byte baseData0 = (byte) packet[3];
		baseInfo.mLeftFrontDoor = (byte) ((baseData0 >> 7) & 0x1);
		baseInfo.mRightFrontDoor = (byte) ((baseData0 >> 6) & 0x1);
		baseInfo.mLeftBackDoor = (byte) ((baseData0 >> 5) & 0x1);
		baseInfo.mRightBackDoor = (byte) ((baseData0 >> 4) & 0x01);
//		baseInfo.mFrontBoxDoor = (byte) ((baseData0 >> 5) & 0x1);
		baseInfo.mTailBoxDoor = (byte) ((baseData0 >> 3) & 0x1);
		
		return baseInfo;
	}

	@Override
	public ParkAssistInfo parseParkAssistantInfo(byte[] packet) {
		return null;
	}

	public PsaCarState parsepPsaCarState(byte[] packet, PsaCarState carState) {
		byte data = packet[4];
		carState.mRearWiper = (byte) (data >> 7 & 0x01);
		carState.mDriverAssist3008 = (byte) (data >> 6 & 0x01);
		carState.mDoorAutoLock = (byte) (data >> 4 & 0x01);
		carState.mDriverAssistOther = (byte) (data >> 3 & 0x01);
		carState.mCentDoorLockSts = (byte) (data >> 2 & 0x01);
		carState.mTripEcoPages = (byte) (data & 0x03);
		
		data = packet[5];
		carState.mLightStuats = (byte) (data >> 7 & 0x01);
		carState.mLightDelaySts = (byte) (data >> 5 & 0x03);
		carState.mAutoRunILL = (byte) (data & 0x01);
		
		data = packet[6];
		carState.mLightAtmosphere = (byte) (data >> 5 & 0x07);
		carState.mRadarStop = (byte) (data >> 3 & 0x01);
		carState.mBackCarSts = (byte) (data >> 2 & 0x01);
		carState.mIsPGear = (byte) (data >> 1 & 0x01);
		carState.mSmallLight = (byte) (data & 0x01);
		
		data = packet[7];
		carState.mLightGoHome = (byte) (data >> 6 & 0x03);
		carState.mLightHost = (byte) (data >> 4 & 0x03);
		carState.mEqSet = (byte) (data >> 1 & 0x03);
		carState.mFuelSet = (byte) (data & 0x01);
		
		data = packet[8];
		carState.mBlindAreaProbe = (byte) (data >> 7 & 0x01);
		carState.mEnginesstopfcdis = (byte) (data >> 6 & 0x01);
		carState.mGuestfunction = (byte) (data >> 5 & 0x01);
		carState.mDoorOpenOptions = (byte) (data >> 4 & 0x01);
		carState.mLauguageSet = (byte) (data & 0x07);
		return carState;
	}
	
	public OutTemputerInfo parseOutTempInfo(byte[] packet, OutTemputerInfo outTempInfo) {
		byte data0 = packet[3];
		if ((data0 >> 7) == 0x00) {
			outTempInfo.mOutCTemp = data0 & 0x7F;
		} else {
			outTempInfo.mOutCTemp = -(data0 & 0x7F);
		}
		return outTempInfo;
	}
	
	@Override
	public VerInfo parseVersionInfo(byte[] packet) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		VerInfo version = new VerInfo();
		byte[] ver = new byte[16];
		System.arraycopy(packet, cursor, ver, 0, 16);
		version.mVersion = DataConvert.BytesToStr(ver);
		return version;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		wheelInfo.mEps = -(DataConvert.byte2Short(packet, 3) * 45 /0x1800);
		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor] & 0xFF;
		return TranslateKey(wheelInfo);
	}

	@Override
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
		backLightInfo.mLight = (packet[5] & 0xFF)*255/0x0F;
		return backLightInfo;
	}

	public PsaWarnInfo parsePsaWarnInfo(byte[] packet, PsaWarnInfo warnInfo){
		warnInfo.mWarnInfo = null;
		warnInfo.mWarnInfoTotal = packet[3] & 0xFF;
		if (warnInfo.mWarnInfoTotal >0) {
			warnInfo.mWarnInfo = new int[warnInfo.mWarnInfoTotal];
			for (int i = 0; i < warnInfo.mWarnInfoTotal ; i++) {
				warnInfo.mWarnInfo[i] = packet[4+i] & 0xFF;
			}
		}
		return warnInfo;
	}
	
	public PsaFuncInfo parsePsaFuncInfo(byte[] packet, PsaFuncInfo funcInfo) {
		funcInfo.mFuncInfo = null;
		funcInfo.mFuncInfoTotal = packet[3] & 0xFF;
		if (funcInfo.mFuncInfoTotal >0) {
			funcInfo.mFuncInfo = new int[funcInfo.mFuncInfoTotal];
			for (int i = 0; i < funcInfo.mFuncInfoTotal ; i++) {
				funcInfo.mFuncInfo[i] = packet[4+i] & 0xFF;
			}
		}
		return funcInfo;
	}
	
	public PsaCruSpeed parsePsaCruSpeed(byte[] packet, PsaCruSpeed curSpeed) {
		return curSpeed;
	}
	
	public PsaMemSpeed parsePsaMemSpeed(byte[] packet, PsaMemSpeed memSpeed) {
		int cursor = 3;
		byte data0 = packet[cursor++];
		memSpeed.mMemory = (byte) (data0 >> 7 & 0x01);
		for (int i = 0; i < 5; i++) {
			memSpeed.mSpeedsSel[i] = (byte) DataConvert.GetBit(data0, (6 - i));
			memSpeed.mSpeeds[i] = packet[cursor++] & 0xFF;
		}
		return memSpeed;
	}
	
	public PsaDiagInfo parsePsaDiagInfo(byte[] packet, PsaDiagInfo diagInfo) {
		diagInfo.mDiagInfo = null;
		diagInfo.mDiagInfoTotal = packet[3] & 0xFF;
		if (diagInfo.mDiagInfoTotal >0) {
			diagInfo.mDiagInfo = new int[diagInfo.mDiagInfoTotal];
			for (int i = 0; i < diagInfo.mDiagInfoTotal ; i++) {
				diagInfo.mDiagInfo[i] = packet[4+i] & 0xFF;
			}
		}
		return diagInfo;
	}
	
	public PsaTripComputer parsePsaTripComputer(byte[] packet, PsaTripComputer tripComputer) {
		int cursor = 3;
		if (packet[2] == DATA_TYPE_OBD_PAGE0_INFO) {
			
			tripComputer.mFuelConsumption = byteToInt(packet[cursor++], packet[cursor++])/10;
			tripComputer.mResidualOilMileage = byteToInt(packet[cursor++], packet[cursor++]);
			tripComputer.mObjectiveTomileage = byteToInt(packet[cursor++], packet[cursor++]);
			tripComputer.mTimingH = packet[cursor++];
			tripComputer.mTimingM = packet[cursor++];
			tripComputer.mTimingS = packet[cursor];
			
		} else if (packet[2] == DATA_TYPE_OBD_PAGE1_INFO) {
			
			tripComputer.mFuelAverage1 = byteToInt(packet[cursor++], packet[cursor++])/10;
			tripComputer.mSpeedAverage1 = byteToInt(packet[cursor++], packet[cursor++]);
			tripComputer.mAccumulatMileage1 = byteToInt(packet[cursor++], packet[cursor++]);
			
		} else if (packet[2] == DATA_TYPE_OBD_PAGE2_INFO) {
			
			tripComputer.mFuelAverage2 = byteToInt(packet[cursor++], packet[cursor++])/10;
			tripComputer.mSpeedAverage2 = byteToInt(packet[cursor++], packet[cursor++]);
			tripComputer.mAccumulatMileage2 = byteToInt(packet[cursor++], packet[cursor++]);
			
		}
		return tripComputer;
	}
	
	private int byteToInt(byte byCmd1, byte byCmd2) {
		int iValue = 0;
		if (byCmd1 == -1 && byCmd2 == -1) {
			iValue = -1;
		} else {
			iValue = ((byCmd1 & 0xFF) << 8) + (byCmd2 & 0xFF);
		}
		return iValue;
	}
	
	@Override
	public boolean IsSuportMeInfo() {
		return true;
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		ArrayList<Message> aListMsg = new ArrayList<Message>();
		
		aListMsg.add(getTimeData(mediaInfo));
		return aListMsg;
	}
	
	private Message getTimeData(MediaInfo mediaInfo) {
		byte byCmdId = RePsaProtocol.DATA_TYPE_TIME_REQUEST;
		byte[] byData = new byte[5];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();
		byData[0] = (byte) (timeInfo.iYear - 2000);
		byData[1] = timeInfo.byMonth;
		byData[2] = timeInfo.byDay;
		byData[3] = timeInfo.byHour;
		byData[4] = timeInfo.byMinute;

		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}

	@Override
	public Message getMuteRadarData(boolean isMute) {
		return null;
	}

	@Override
	public Message getInquiryTrackData() {
		return null;
	}

	@Override
	public Message getParkData(boolean bPark) {
		return null;
	}

	@Override
	public int GetMediaSourceCmdId() {
		return 0;
	}

	@Override
	public int GetPhoneActionCmdId() {
		return 0;
	}
}
