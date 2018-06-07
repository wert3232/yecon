package com.can.parser.raise.domestic;

import com.can.parser.DDef;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

public class ReBaojunProtol extends ReProtocol {

	public static final byte DATA_TYPE_WHEEL_KEY_INFO 	= 0x21;
	public static final byte DATA_TYPE_REAR_RADAR_INFO 	= 0x26;
	public static final byte DATA_TYPE_FRONT_RADAR_INFO = 0x27;
	public static final byte DATA_TYPE_BASE_INFO 		= 0x28;
	public static final byte DATA_TYPE_WHEEL_INFO 		= 0x30;

	public class KeyCode {
		public static final byte KEY_CODE_NULL 		= 0x00;
		public static final byte KEY_CODE_VOL_UP 	= 0x01; // vol+
		public static final byte KEY_CODE_VOL_DOWN 	= 0x02; // vol+
		public static final byte KEY_CODE_MUTE 		= 0x06; // mute
		public static final byte KEY_CODE_SOURCE	= 0x07; // mode
		public static final byte KEY_CODE_PICK_UP	= 0x09;
		public static final byte KEY_CODE_HUNG		= 0x0A;
		public static final byte KEY_CODE_UP 		= 0x0B; 
		public static final byte KEY_CODE_DOWN 		= 0x0C; 
	}
	
	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		int cursor = 3;
		byte RadarData0 = (byte) packet[cursor++];
		byte RadarData1 = (byte) packet[cursor++];
		byte RadarData2 = (byte) packet[cursor++];
		byte RadarData3 = (byte) packet[cursor++];
		if (packet[1] == DATA_TYPE_FRONT_RADAR_INFO) {
			radarInfo.mFrontLeftDis = RadarData0 ;
			radarInfo.mFrontLeftCenterDis = RadarData1;
			radarInfo.mFrontRightCenterDis = RadarData2;
			radarInfo.mFrontRightDis = RadarData3;
			
		} else if (packet[1] == DATA_TYPE_REAR_RADAR_INFO) {
			radarInfo.mBackRightDis = RadarData3;
			radarInfo.mBackRightCenterDis = RadarData2;
			radarInfo.mBackLeftCenterDis = RadarData1;
			radarInfo.mBackLeftDis = RadarData0;
		}
		return radarInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		byte data0 = packet[3];
		baseInfo.mRightFrontDoor = (byte) (data0 >> 7 & 0x01);
		baseInfo.mLeftFrontDoor = (byte) (data0 >> 6 & 0x01);
		baseInfo.mRightBackDoor = (byte) (data0 >> 5 & 0x01);
		baseInfo.mLeftBackDoor = (byte) (data0 >> 4 & 0x01);
		baseInfo.mTailBoxDoor = (byte) (data0 >> 3 & 0x01);
		baseInfo.mFrontBoxDoor = (byte) (data0 >> 2 & 0x01);
		return baseInfo;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		wheelInfo.mEps = DataConvert.byte2Short(packet, 3)*45/9232;
		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor] & 0xFF;
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
		return TranslateKey(wheelInfo);
	}

	private String mstrKeyCode = null;
	private WheelKeyInfo TranslateKey(WheelKeyInfo wheelKeyInfo) {
		switch (wheelKeyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_SOURCE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_PICK_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_HUNG:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
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
	public int GetFrontRadarInfoCmdId() {
		return DATA_TYPE_FRONT_RADAR_INFO;
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		return DATA_TYPE_REAR_RADAR_INFO;
	}

	@Override
	public int GetBaseInfoCmdId() {
		return DATA_TYPE_BASE_INFO;
	}

	@Override
	public int GetWheelInfoCmdId() {
		return DATA_TYPE_WHEEL_INFO;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		return DATA_TYPE_WHEEL_KEY_INFO;
	}
}
