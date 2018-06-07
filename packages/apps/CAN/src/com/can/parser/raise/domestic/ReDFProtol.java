package com.can.parser.raise.domestic;

import com.can.assist.CanKey;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.PhoneState;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;
import com.can.ui.CanPopWind;

public class ReDFProtol extends ReProtocol {

	public static final byte DATA_TYPE_PANEL_KEY_INFO	= 0x20;
	public static final byte DATA_TYPE_WHEEL_KEY_INFO 	= 0x21;
	public static final byte DATA_TYPE_AIR_INFO			= 0x23;
	public static final byte DATA_TYPE_RADAR_INFO 		= 0x24;
	public static final byte DATA_TYPE_BASE_INFO 		= 0x28;
	public static final byte DATA_TYPE_WHEEL_INFO 		= 0x30;
	public static final byte DATA_TYPE_OUT_TEMP_INFP	= 0x36;

	public class KeyCode {
		public static final byte KEY_CODE_NULL 		= 0x00;
		public static final byte KEY_CODE_VOL_UP 	= 0x01; // vol+
		public static final byte KEY_CODE_VOL_DOWN 	= 0x02; // vol+
		public static final byte KEY_CODE_MUTE 		= 0x06; // mute
		public static final byte KEY_CODE_SOURCE	= 0x07; // mode
		public static final byte KEY_CODE_CANCEL	= 0x08;	
		public static final byte KEY_CODE_PICK_UP	= 0x09;
		public static final byte KEY_CODE_HUNG		= 0x0A;
		public static final byte KEY_CODE_UP 		= 0x0B; 
		public static final byte KEY_CODE_DOWN 		= 0x0C; 
		
		public static final byte KEY_CODE_SCAN		= 0x20;
		public static final byte KEY_CODE_BAND		= 0x21;
		public static final byte KEY_CODE_AST		= 0x22;
		public static final byte KEY_CODE_SEEK_UP	= 0x23;
		public static final byte KEY_CODE_SEEK_DN	= 0x24;
		public static final byte KEY_CODE_VOL_UP_X	= 0x2B;
		public static final byte KEY_CODE_VOL_DN_X	= 0x2C;
		public static final byte KEY_CODE_POWER		= 0x2D;
		public static final byte KEY_CODE_EQ		= 0x2F;
		public static final byte KEY_CODE_SET		= 0x30;
		public static final byte KEY_CODE_LINK		= 0x32;
		public static final byte KEY_CODE_MEDIA		= 0x33;
		public static final byte KEY_CODE_BACK		= 0x34;
		public static final byte KEY_CODE_HOME		= 0x35;
	}
	
	
	@Override
	public byte[] connect() {
		return super.connect();
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		if (packet[2] == 0x02) {
			radarInfo.mBackLeftDis = (byte) ((packet[3]&0xFF) == 0xFF ? 0x00 : ((packet[3] & 0xFF) * 10/256 + 1));
			radarInfo.mBackLeftCenterDis = radarInfo.mBackLeftDis;
			radarInfo.mBackRightCenterDis = (byte) ((packet[4]&0xFF) == 0xFF ? 0x00 : ((packet[4] & 0xFF) * 10/256 + 1));
			radarInfo.mBackRightDis = radarInfo.mBackRightCenterDis;
		} else if (packet[2] == 0x03) {
			radarInfo.mBackLeftDis = (byte) (packet[3] + 1);
			radarInfo.mBackLeftCenterDis = (byte) (packet[4] + 1);
			radarInfo.mBackRightCenterDis = radarInfo.mBackLeftCenterDis;
			radarInfo.mBackRightDis = (byte) (packet[5] + 1);
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
//		baseInfo.mFrontBoxDoor = (byte) (data0 >> 2 & 0x01);
		return baseInfo;
	}

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		int cursor = 3;
		byte data0 = packet[cursor++];
		airInfo.mAiron = (byte) (data0 >> 7 & 0x01);
		airInfo.mAcState = (byte) (data0 >> 6 & 0x01);
		airInfo.mCircleState = (byte) (data0 >> 5 & 0x01);
		airInfo.mAutoLight1 = (byte) (data0 >> 3 & 0x01);
		airInfo.mRearLight = (byte) (data0 >> 1 & 0x01);
		
		byte data1 = packet[cursor++];
		airInfo.mWindMode = data1;
		if (airInfo.mWindMode == 0x01 || airInfo.mWindMode == 0x02) {
			airInfo.mParallelWind = 0x01;
		} else {
			airInfo.mParallelWind = 0x00;
		}
		
		if (airInfo.mWindMode == 0x02 || airInfo.mWindMode == 0x03 || airInfo.mWindMode == 0x04) {
			airInfo.mDowmWind = 0x01;
		} else {
			airInfo.mDowmWind = 0x00;
		}
		
		if (airInfo.mWindMode == 0x04 || airInfo.mWindMode == 0x05) {
			airInfo.mUpwardWind = 0x01;
		} else {
			airInfo.mUpwardWind = 0x00;
		}
		
		byte data2 = packet[cursor++];
		airInfo.mWindRate = data2;
		airInfo.mMaxWindlv = 0x08;
		
		byte data3 = packet[cursor++];
		airInfo.mLeftTemp = data3 + 16;
		airInfo.mRightTemp = data3 + 16;
		airInfo.mMaxTemp = 32;
		airInfo.mMinTemp = 16;
		return airInfo;
	}

	public OutTemputerInfo parseOutTempInfo(byte[] packet, OutTemputerInfo outtemp) {
		
		byte data0 = packet[3];
		if (data0 >> 7 == 0x00) {
			outtemp.mOutCTemp = data0 & 0x7F;
		} else {
			outtemp.mOutCTemp = - (data0 & 0x7F);
		}
		outtemp.mbEnable = true;
		return outtemp;
	}
	
	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		wheelInfo.mEps = -(DataConvert.byte2Short(packet, 3)*45/5600);
		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor] & 0xFF;
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
		if (packet[1] == DATA_TYPE_PANEL_KEY_INFO) {
			int iPhoneState = CanPopWind.mObjGdSharedData.getMediaInfo().getPhonestate();
			if (wheelInfo.mKeyCode == KeyCode.KEY_CODE_DOWN &&
					(iPhoneState == PhoneState.INCOMING || 
					iPhoneState == PhoneState.OUTGOING || 
					iPhoneState == PhoneState.SPEAKING)) {
				wheelInfo.mstrKeyCode = DDef.Phone_hang;
			} else if (wheelInfo.mKeyCode == KeyCode.KEY_CODE_UP &&
					iPhoneState == PhoneState.INCOMING) {
				wheelInfo.mstrKeyCode = DDef.Phone_dial;
			}
		}
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
		case KeyCode.KEY_CODE_MEDIA:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_PICK_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_HUNG:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_CANCEL:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case KeyCode.KEY_CODE_UP:
		case KeyCode.KEY_CODE_SEEK_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_DOWN:
		case KeyCode.KEY_CODE_SEEK_DN:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_SCAN:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_as;
			break;
		case KeyCode.KEY_CODE_BAND:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_fm;
			break;
		case KeyCode.KEY_CODE_AST:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_as;
			break;
		case KeyCode.KEY_CODE_VOL_UP_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DN_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_POWER:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case KeyCode.KEY_CODE_EQ:
			wheelKeyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case KeyCode.KEY_CODE_SET:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case KeyCode.KEY_CODE_LINK:
			break;
		case KeyCode.KEY_CODE_BACK:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case KeyCode.KEY_CODE_HOME:
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
	public int GetFrontRadarInfoCmdId() {
		return DATA_TYPE_RADAR_INFO;
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		return DATA_TYPE_RADAR_INFO;
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

	@Override
	public int GetAirInfoCmdId() {
		return DATA_TYPE_AIR_INFO;
	}
	
	public int GetOutTempInfoCmdId() {
		return DATA_TYPE_OUT_TEMP_INFP;
	}
}
