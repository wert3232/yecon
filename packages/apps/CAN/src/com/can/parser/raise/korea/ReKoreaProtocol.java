package com.can.parser.raise.korea;

import java.util.ArrayList;
import android.os.Message;
import com.can.assist.CanKey;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReKoreaProtocol
 * 
 * @function:睿志城韩系协议类
 * @author Kim
 * @Date: 2016-6-4 上午10:30:27
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReKoreaProtocol extends ReProtocol {

	final byte mHeadFlag = (byte) 0xFD;

	final byte mHeadCursor = (byte) 0;
	final byte mDataLenCursor = (byte) 1;
	final byte mCmdIDCursor = (byte) 2;
	final byte mDataCursor = (byte) 3;
	// Slave -> Host
	public static final byte DATA_TYPE_OUT_TEMPUTER_INFO = 0x01;
	public static final byte DATA_TYPE_AIR_INFO = 0x03; // 空调信息
	public static final byte DATA_TYPE_CAR_INFO = 0x41; // 车辆信息
	public static final byte DATA_TYPE_RADAR_INFO = 0x04; // 后雷达信息
	public static final byte DATA_TYPE_BASE_INFO = 0x05; // 车门信息
	public static final byte DATA_TYPE_PARKASSIST_INFO = 0x25; // 辅助系统信息
	public static final byte DATA_TYPE_VERSION_INFO = 0x30; // 版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = 0x26; // 方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = 0x02; // 方向盘按键信息
	public static final byte DATA_TYPE_BACK_LIGHT_INFO = 0x07; // 背光调节
	public static final byte DATA_TYPE_POWERONOFF_INFO = (byte) 0xff;

	// Host -> Slave
	public static final byte DATA_TYPE_CAR_INFO_REQUEST = (byte) 0x90; // 请求控制信息
	public static final byte DATA_TYPE_POWERONOFF_REQUEST = (byte) 0x04;
	public static final byte DATA_TYPE_TIMER_REQUEST = (byte) 0x06;
	public static final byte DATA_TYPE_FADE_BAL_REQUEST = (byte) 0x07;
	public static final byte DATA_TYPE_EQ_REQUEST = (byte) 0x08;
	public static final byte DATA_TYPE_SOURCE_REQUEST = (byte) 0x09; // Source
	public static final byte DATA_TYPE_ICON_REQUEST = (byte) 0xC1; // ICON
	public static final byte DATA_TYPE_RADIO_REQUEST = (byte) 0xC2; // 收音信息
	public static final byte DATA_TYPE_MEDIA_PLAYER_REQUEST = (byte) 0xC3; // 媒体播放信息
	public static final byte DATA_TYPE_VOLUME_REQUEST = (byte) 0x05; // 音量显示控制
	public static final byte DATA_TYPE_TELEPHONE_REQUEST = (byte) 0xC5; // 电话状态
	public static final byte DATA_TYPE_RADAR_SOUND_REQUEST = (byte) 0xC6; // Radar声音控制
	public static final byte DATA_TYPE_PA_REQUEST = (byte) 0xA0; // 功放控制指令
	public static final byte DATA_TYPE_MEDIA_BOX_REQUEST = (byte) 0xA1; // 媒体盒控制指令
	public static final byte DATA_TYPE_NAVI_REQUEST = (byte) 0xA2; // 行车电脑导航信息显示
	public static final byte DATA_TYPE_BT_REQUEST = (byte) 0xA3; // 蓝牙控制指令

	// 源定义
	public class Source {
		public static final byte ERROR_MODE = 0x00;
		public static final byte DISC_CD_DVD = 0x01;
		public static final byte RADIO = 0x02;
		public static final byte AV1 = 0x03;
		public static final byte AV2 = 0x04;
		public static final byte TV = 0x05;
		public static final byte NAVI = 0x06;
		public static final byte TEL_MODE = 0x07;
		public static final byte IPOD_MODE = 0x08;
		public static final byte AM_MODE = 0x09;
		public static final byte WBAND_MODE = 0x0A;
		public static final byte BT_MODE = 0x0b;
		public static final byte SD_MODE = 0x0c;
		public static final byte USB_MODE = 0x0D;

		public static final byte PHOTO_MODE = 0x0E;
		public static final byte SET_VOL_MODE = 0x0F;
		public static final byte BT_A2DP_MODE = 0x11;
		public static final byte AUX_MODE = 0x12;
		public static final byte CD_MODE = 0x13;
		public static final byte MP3_CD_MODE = 0x14;
		public static final byte USB_VIDEO_MODE = 0x15;

		public static final byte USB_AUDIO_MODE = 0x16;
		public static final byte SXM_MODE = 0x17;
		public static final byte DMB1_MODE = 0x18;
		public static final byte DMB2_MODE = 0x19;
		public static final byte DMB_MODE = 0x20;

		public static final byte CLOSE_MODE = (byte) 0x80;
		public static final byte APPLICATION_MODE = (byte) 0x81;
		public static final byte FILM_CLOSE_MODE = (byte) 0x82;
		public static final byte MYMUSIC_MODE = (byte) 0x83;
		public static final byte PANDORA_MODE = (byte) 0x84;
	}

	// 收音机波段
	public class TunerBound {
		public static final byte TUNER_FM1 = 0x00;
		public static final byte TUNER_FM2 = 0x01;
		public static final byte TUNER_FM3 = 0x02;
		public static final byte TUNER_AM = 0x03;
		public static final byte TUNER_AM1 = 0x04;
		public static final byte TUNER_LW = 0x05;
		public static final byte TUNER_WB = 0x06;
	}

	// 电话状态
	public class PhoneState {
		public static final byte POHNE_ENTER_BTUI = 0x00;
		public static final byte PHONE_INCOMING = 0x01;
		public static final byte PHONE_SPEAKING = 0x02;
		public static final byte PHONE_OUTGOING = 0x03;
		public static final byte PHONE_CONNECT = 0x04;
		public static final byte PHONE_DISCONNECT = 0x05;
		public static final byte PHONE_TERMINATE = 0x06;
	}

	// 按键类型
	public class KeyCode {
		public static final byte KEY_CODE_NULL = 0x00;
		public static final byte KEY_CODE_MUTE = 0x10; // mute
		public static final byte KEY_CODE_SOURCE = 0x11; // mode
		public static final byte KEY_CODE_SEEK_UP = 0x12; // seek+
		public static final byte KEY_CODE_SEEK_DOWN = 0x13; // seek-
		public static final byte KEY_CODE_VOL_UP = 0x14; // vol+
		public static final byte KEY_CODE_VOL_DOWN = 0x15; // vol-
		public static final byte KEY_CODE_PHONE_ANSWER = 0x16; // 接电话
		public static final byte KEY_CODE_PHONE_HANGUP = 0x17; // 挂电话
		public static final byte KEY_CODE_POWER = 0x18; // power
		public static final byte KEY_CODE_SEL_VOL_UP = 0x19; // 旋钮vol+
		public static final byte KEY_CODE_SEL_VOL_DOWN = 0x1a; // 旋钮vol-
		public static final byte KEY_CODE_RADIO = 0x1b; // fm/am (KX5顶配Media)

		public static final byte KEY_CODE_MEDIA = 0x1c; // media (KX5顶配map)
		public static final byte KEY_CODE_PHONE = 0x1d; // phone
		public static final byte KEY_CODE_DISP = 0x1e; // disp (KX5顶配UVO)
		public static final byte KEY_CODE_SEEK_UP_BUTTON = 0x1f; // 面板seek+
		public static final byte KEY_CODE_SEEK_DOWN_BUTTON = 0x20; // 面板seek-
		public static final byte KEY_CODE_MAP = 0x21; // map
		public static final byte KEY_CODE_DEST = 0x22; // dest
		public static final byte KEY_CODE_ROUTE = 0x23; // route

		public static final byte KEY_CODE_SETUP = 0x24; // setup
		public static final byte KEY_CODE_SEL = 0x25; // sel/enter 旋钮
		public static final byte KEY_CODE_TUNE_UP = 0x26; // Tune+ 旋钮
		public static final byte KEY_CODE_TURE_DOWN = 0x27; // Tune- 旋钮
		public static final byte KEY_CODE_UVO = 0x28; // UVO
		public static final byte KEY_CODE_HOME = 0x29; // home
		public static final int KEY_CODE_KNOB_VOLADD = 0x85;
		public static final int KEY_CODE_KNOB_VOLDEL = 0x84;
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
	public int getCheckSum(byte[] buffer, int cursor, int packetLength) {
		// 包的最后个字节
		byte[] checkSum1 = new byte[1];
		byte[] checkSum2 = new byte[1];
		checkSum1[0] = buffer[cursor + packetLength - 2];
		checkSum2[0] = buffer[cursor + packetLength - 1];
		int checkSumTotal1 = DataConvert.byte2Int(checkSum1, 0);
		int checkSumTotal2 = DataConvert.byte2Int(checkSum2, 0);
		int checkSumTotal = checkSumTotal2 + (checkSumTotal1 * 256);
		// Log.d(TAG, "getCheckSum  checkSumTotal"+checkSumTotal);
		return checkSumTotal;
	}

	@Override
	public int caculateCheckSum(byte[] buffer, int cursor, int packetLength) {
		// 一个有效包的开始 Header(占一字节)+ DataType + byLength + Data1 + Data2 + …
		// + DataN + Checksum
		// 计算当前包数据长度 byLength 是从 Data1 到 DataN 的所有数据的字节数之和
		// 计算checksum 从DataType（包含）开始到DataN（包含）
		// CheckSum = SUM(DataType + length + Data0 + ... + DataN)
		int iCheckSum = 0;
		int iDataEnd = packetLength - 3;
		int i = 0;

		iDataEnd += cursor;

		byte[] buffer1 = new byte[1];

		for (i = mDataLenCursor + cursor; i <= iDataEnd; i++) {
			buffer1[0] = buffer[i];
			iCheckSum += DataConvert.byte2Int(buffer1, 0);
		}
		// iCheckSum ^= 0xFF;
		// Log.d(TAG, "iCheckSum = "+iCheckSum);
		return (int) iCheckSum;
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
		return DATA_TYPE_AIR_INFO & 0xFF;
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
		return DATA_TYPE_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		return DATA_TYPE_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetBaseInfoCmdId() {
		return DATA_TYPE_BASE_INFO & 0xFF;
	}

	@Override
	public int GetParkAssistCmdId() {
		return DATA_TYPE_PARKASSIST_INFO & 0xFF;
	}

	@Override
	public int GetVersionInfoCmdId() {
		return DATA_TYPE_VERSION_INFO & 0xFF;
	}

	@Override
	public int GetWheelInfoCmdId() {
		return DATA_TYPE_WHEEL_INFO & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	@Override
	public int GetBackLightInfoCmdId() {
		return DATA_TYPE_BACK_LIGHT_INFO & 0xFF;
	}
	
	public int GetOutTempInfoCmdId() {
		return DATA_TYPE_OUT_TEMPUTER_INFO & 0xFF;
	}

	private String mstrKeyCode = null;
	public WheelKeyInfo TranslateKey(WheelKeyInfo wheelKeyInfo) {

		switch (wheelKeyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_SOURCE:
		case KeyCode.KEY_CODE_MEDIA:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_SEEK_UP:
		case KeyCode.KEY_CODE_SEEK_UP_BUTTON:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_SEEK_DOWN:
		case KeyCode.KEY_CODE_SEEK_DOWN_BUTTON:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_VOL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_PHONE_ANSWER:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_PHONE_HANGUP:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_POWER:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case KeyCode.KEY_CODE_RADIO:
			wheelKeyInfo.mstrKeyCode = DDef.Src_radio;
			break;
		case KeyCode.KEY_CODE_PHONE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_bt;
			break;
		case KeyCode.KEY_CODE_UVO:
		case KeyCode.KEY_CODE_DISP:
			wheelKeyInfo.mstrKeyCode = DDef.Blacklight;
			break;
		case KeyCode.KEY_CODE_MAP:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case KeyCode.KEY_CODE_DEST:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case KeyCode.KEY_CODE_ROUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case KeyCode.KEY_CODE_SETUP:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case KeyCode.KEY_CODE_SEL:
			wheelKeyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case KeyCode.KEY_CODE_TUNE_UP:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_TURE_DOWN:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_HOME:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case KeyCode.KEY_CODE_SEL_VOL_DOWN:
		case KeyCode.KEY_CODE_KNOB_VOLADD:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_SEL_VOL_UP:
		case KeyCode.KEY_CODE_KNOB_VOLDEL:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
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
	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo) {
		return null;
	}

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		int cursor = 3; // 跳过2E、Data Type、Length字段
		// Log.d(TAG, "parseAirInfo   ");
		// Data 0
		byte airData0 = (byte) packet[cursor];
		if (airData0 >= 62 && airData0 <= 90) {
			airInfo.mTempUnit = 0x01;
			airInfo.mLeftTemp = (float) airData0;
		} else {
			airInfo.mTempUnit = 0x00;
			airInfo.mLeftTemp = (float) (17 + airData0 * 0.5);
		}

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		if (airInfo.mTempUnit == 0x01) {
			airInfo.mRightTemp = (float) airData1;
		} else {
			airInfo.mRightTemp = (float) (17 + airData1 * 0.5);
		}
		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];
		airInfo.mWindRate = airData2;
		airInfo.mAiron = (byte) (airData2 == 0x00 ? 0x00 : 0x01);

		// Data 3
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		airInfo.mCircleState = (byte) ((airData3 >> 7) & 0x01);
		airInfo.mUpwardWind = (byte) ((airData3 >> 6) & 0x1);// qian chu shang
		airInfo.mRearLight = (byte) ((airData3 >> 5) & 0x1);// hou chu shang
		airInfo.mDowmWind = (byte) ((airData3) >> 4 & 0x1);
		airInfo.mParallelWind = (byte) ((airData3 >> 3) & 0x1);
		airInfo.mAutoLight1 = (byte) ((airData3 >> 2) & 0x1);
		airInfo.mDaulLight = (byte) ((airData3 >> 1) & 0x1);
		airInfo.mAcState = (byte) ((airData3 >> 0) & 0x1);

		if (airInfo.mTempUnit == 0x00) {
			airInfo.mMinTemp = 17;
			airInfo.mMaxTemp = 32;
		} else {
			airInfo.mMinTemp = 62;
			airInfo.mMaxTemp = 90;
		}

		airInfo.mMaxWindlv = 8;

		// //后座温度
		// cursor += 1;
		// byte airData4 = (byte) packet[cursor];
		// airInfo.mRightTemp = (float) (17 + airData4 * 0.5);
		//
		// //后座风速
		// cursor += 1;
		// byte airData5 = (byte) packet[cursor];
		// airInfo.mWindRate = airData5;
		return airInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte RadarData0 = (byte) packet[cursor];
		radarInfo.mFrontRightDis = (byte) ((RadarData0 & 0x03) == 00 ? 00 : (4-(RadarData0 & 0x03)));
		radarInfo.mFrontRightCenterDis = (byte) ((RadarData0 >>2 & 0x03) == 00 ? 00 : (4-(RadarData0 >> 2 & 0x03)));
		radarInfo.mFrontLeftCenterDis = (byte) ((RadarData0 >>4 & 0x03) == 00 ? 00 : (4-(RadarData0 >> 6 & 0x03)));
		radarInfo.mFrontLeftDis = (byte) ((RadarData0 >>6 & 0x03) == 00 ? 00 : (4-(RadarData0 >> 6 & 0x03)));

		byte RadarData1 = (byte) packet[cursor + 1];
		radarInfo.mBackRightDis =(byte) ((RadarData1 & 0x03) == 00 ? 00 : (4-(RadarData1 & 0x03)));
		radarInfo.mBackRightCenterDis = (byte) ((RadarData1 >>2 & 0x03) == 00 ? 00 : (4-(RadarData1 >> 2 & 0x03)));
		radarInfo.mBackLeftCenterDis = (byte) ((RadarData1 >>4 & 0x03) == 00 ? 00 : (4-(RadarData1 >> 4 & 0x03)));
		radarInfo.mBackLeftDis = (byte) ((RadarData1 >>6 & 0x03) == 00 ? 00 : (4-(RadarData1 >> 6 & 0x03)));

		radarInfo.mbyRightShowType = 0;

		return radarInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		// Data0
		byte baseData0 = (byte) packet[cursor];
		baseInfo.mFrontBoxDoor = (byte) ((baseData0 >> 5) & 0x1);
		baseInfo.mTailBoxDoor = (byte) ((baseData0 >> 4) & 0x1);
		baseInfo.mRightBackDoor = (byte) ((baseData0 >> 3) & 0x01);
		baseInfo.mLeftBackDoor = (byte) ((baseData0 >> 2) & 0x1);
		baseInfo.mRightFrontDoor = (byte) ((baseData0 >> 1) & 0x1);
		baseInfo.mLeftFrontDoor = (byte) ((baseData0 >> 0) & 0x1);

		return baseInfo;
	}

	@Override
	public ParkAssistInfo parseParkAssistantInfo(byte[] packet) {
		// TODO Auto-generated method stub
		return null;
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
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mEps = DataConvert.byte2Short(packet, cursor);
		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor] & 0xFF;
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
		return TranslateKey(wheelInfo);
	}

	@Override
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		backLightInfo.mLight = packet[cursor] & 0xFF;
		return backLightInfo;
	}

	public OutTemputerInfo parseOutTemputerInfo(byte[] packet,
			OutTemputerInfo info) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		if (DataConvert.byteToInt(packet[cursor]) == 0xFF) {
			info.mbEnable = false;
		} else {
			info.mbEnable = true;
			boolean bNegative = (DataConvert.GetBit(packet[cursor], 7) == 0x01)? true : false;
			if (bNegative) {
				info.mOutCTemp = -(float)(DataConvert.byteToInt(packet[cursor]) & 0x7F);
			}else{
				info.mOutCTemp = (float)(DataConvert.byteToInt(packet[cursor]) & 0x7F);
			}
		}
		
		if (DataConvert.byteToInt(packet[cursor + 1]) == 0xFF) {
			info.mbFEndble = false;
		}else {
			info.mbFEndble = true;
			info.mOutFTemp = DataConvert.byteToInt(packet[cursor+1]);
		}

		return info;
	}

	@Override
	public byte TranslateSource(int iSource) {
		byte bySource = 0x00;

		switch (iSource) {
		case DDef.SOURCE_DEF.Src_radio:
			bySource = Source.RADIO;
			break;
		case DDef.SOURCE_DEF.Src_music:
			bySource = Source.MYMUSIC_MODE;
			break;
		case DDef.SOURCE_DEF.Src_video:
			bySource = Source.MYMUSIC_MODE;
			break;
		case DDef.SOURCE_DEF.Src_bluetooth:
		case DDef.SOURCE_DEF.Src_bt_phone:
			bySource = Source.BT_MODE;
			break;
		case DDef.SOURCE_DEF.Src_dvd:
			bySource = Source.DISC_CD_DVD;
			break;
		// case DDef.SOURCE_DEF.Src_dvr:
		// bySource = Source.AUX_MODE;
		// break;
		case DDef.SOURCE_DEF.Src_dtv:
			bySource = Source.TV;
			break;
		case DDef.SOURCE_DEF.Src_atv:
			bySource = Source.TV;
			break;
		case DDef.SOURCE_DEF.Src_avin:
			bySource = Source.AV1;
			break;
		case DDef.SOURCE_DEF.Src_ipod:
			bySource = Source.IPOD_MODE;
			break;
		// case DDef.SOURCE_DEF.Src_phonelink:
		// break;
		// case DDef.SOURCE_DEF.Src_backcar:
		// break;
		case DDef.SOURCE_DEF.Src_photo:
			bySource = Source.PHOTO_MODE;
			break;
		default:
			bySource = Source.ERROR_MODE;
			break;
		}

		return bySource;
	}

	@Override
	public Message getVolInfo(int iVol) {
		// TODO Auto-generated method stub
		return CanTxRxStub.getTxMessage(
				ReKoreaProtocol.DATA_TYPE_VOLUME_REQUEST,
				ReKoreaProtocol.DATA_TYPE_VOLUME_REQUEST, getVol(iVol), this);
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		ArrayList<Message> aListMsg = new ArrayList<Message>();

		aListMsg.add(CanTxRxStub.getTxMessage(
				ReKoreaProtocol.DATA_TYPE_SOURCE_REQUEST,
				ReKoreaProtocol.DATA_TYPE_SOURCE_REQUEST,
				SourceInfo(mediaInfo), this));

		aListMsg.add(getTimeData(mediaInfo));
		aListMsg.add(getMediaData(mediaInfo));

		return aListMsg;
	}

	private Message getTimeData(MediaInfo mediaInfo) {
		byte byCmdId = ReKoreaProtocol.DATA_TYPE_TIMER_REQUEST;
		byte[] byData = new byte[3];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();
		byData[0] = timeInfo.byMinute;
		byData[1] = timeInfo.byHour;
		byData[2] = timeInfo.by24Mode;

		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}

	private Message getMediaData(MediaInfo mediaInfo) {

		byte byCmdId = ReKoreaProtocol.DATA_TYPE_SOURCE_REQUEST;
		byte[] byData = null;

		byData = MediaPlayInfo(mediaInfo);

		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}

	private byte[] getVol(int iVol) {
		byte[] byData = { 0 };

		byte byVol = (byte) iVol;

		byData[0] = (byte) ((byVol > 0) ? (byData[0] & 0x00)
				: (byData[0] | 0x80));
		byData[0] = (byte) (byData[0] | ((0x7F & byData[0]) | byVol));

		return byData;
	}

	/**
	 * 
	 * <p>
	 * Title: SourceInfo
	 * </p>
	 * <p>
	 * Description: 源信息
	 * </p>
	 * 
	 * @param mediaInfo
	 * @return
	 */
	public byte[] SourceInfo(MediaInfo mediaInfo) {

		byte[] bydata = new byte[1];

		bydata[0] = TranslateSource(mediaInfo.getSource());

		return bydata;
	}

	public byte TranslateBand(byte byBand) {
		byte bybandEx = TunerBound.TUNER_FM1;

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
			bybandEx = TunerBound.TUNER_AM;
			break;
		default:
			break;
		}

		return bybandEx;
	}

	public byte[] RadioInfo(MediaInfo mediainfo) {

		byte[] bydata = new byte[4];
		bydata[0] = Source.RADIO;
		bydata[1] = TranslateBand(mediainfo.getBand());
		int iFreq = mediainfo.getMainFreq();
		bydata[2] = (byte) (iFreq / 100);// itmpFreq[0];
		bydata[3] = (byte) (iFreq % 100);// itmpFreq[1];
		return bydata;
	}

	public byte[] MediaPlayInfo(MediaInfo mediaInfo) {

		byte[] bydata = null;
		byte bySource = TranslateSource(mediaInfo.getSource());
		switch (bySource) {
		case Source.MYMUSIC_MODE:
			bydata = new byte[6];
			byte[] byCurTrack = DataConvert
					.Hi2Lo2Byte(mediaInfo.getCurTrack());
			bydata[0] = bySource;
			bydata[1] = byCurTrack[1];
			bydata[2] = byCurTrack[0];
			int icurTime = mediaInfo.getPlayTime();
			bydata[3] = (byte) (icurTime / 3600);
			bydata[4] = (byte) ((icurTime / 60) % 60);
			bydata[5] = (byte) (icurTime % 60);
			break;
		case Source.IPOD_MODE:
			break;
		case Source.AV1:
			break;
		case Source.DISC_CD_DVD:
			break;
		case Source.RADIO:
			bydata = RadioInfo(mediaInfo);
			break;
		default:
			break;
		}

		return bydata;
	}

	@Override
	public boolean IsSuportMeInfo() {
		return true;
	}

	@Override
	public Message getBtInfo(int iType, int iValue, MediaInfo mediaInfo) {
		byte byCmdId = ReKoreaProtocol.DATA_TYPE_SOURCE_REQUEST;
		byte[] bydata = new byte[2];

		if (iType == 0x00) {
			bydata[0] = Source.BT_MODE;
			if (iValue == 0x01) {
				bydata[1] = PhoneState.PHONE_CONNECT;
			} else {
				bydata[1] = PhoneState.PHONE_DISCONNECT;
			}
		} else if (iType == 0x01) {
			bydata[0] = Source.TEL_MODE;
			switch (iValue) {
			case DDef.PHONE_STATE_INCOMING:
				bydata[1] = PhoneState.PHONE_INCOMING;
				break;
			case DDef.PHONE_STATE_OUTGOING:
				bydata[1] = PhoneState.PHONE_OUTGOING;
				break;
			case DDef.PHONE_STATE_SPEAKING:
				bydata[1] = PhoneState.PHONE_SPEAKING;
				break;
			case DDef.PHONE_STATE_IDLE:
				bydata[1] = PhoneState.PHONE_TERMINATE;
				break;
			default:
				break;
			}
		}
		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, bydata, this);
	}
}
