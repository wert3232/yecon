package com.can.parser.raise.domestic;



import com.can.assist.CanKey;
import com.can.parser.DDef;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.MGCarSet;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;

public class ReSouAstProtol extends ReProtocol {
	//Slave - Host
	public static final byte DATA_TYPE_WHEEL_KEY_INFO	= 0x20;
	public static final byte DATA_TYPE_PANEL_KEY_INFO	= 0x21;
	public static final byte DATA_TYPE_REAR_RADAR_INFO	= 0x22;
	public static final byte DATA_TYPE_FRONT_RADAR_INFO = 0x23;
	public static final byte DATA_TYPE_BASE_INFO 		= 0x24;
	public static final byte DATA_TYPE_TPMS_INFO		= 0x25;
	public static final byte DATA_TYPE_WHEEL_INFO 		= 0x29;
	public static final byte DATA_TYPE_CAR_SET_INFO		= 0x32;
	public static final byte DATA_TYPE_FUEL_INFO		= 0x33;
	//Host - Slave
	public static final byte DATA_TYPE_CAR_SET_REQUEST		= (byte) 0x82;
	
	public class KeyCode {
		public static final byte KEY_CODE_NULL 		= 0x00;
		public static final byte KEY_CODE_VOL_ADD	= 0x01;
		public static final byte KEY_CODE_VOL_DEL	= 0x02;
		public static final byte KEY_CODE_PREV		= 0x03;
		public static final byte KEY_CODE_NEXT		= 0x04;
		public static final byte KEY_CODE_SOURCE	= 0x07;
		public static final byte KEY_CODE_SPEECH	= 0x08;
		public static final byte KEY_CODE_BT		= 0x09;
	}
	
	public class PanelCode {
		public static final byte KEY_CODE_NULL 		= 0x00;
		public static final byte KEY_CODE_MENU		= 0x01;
		public static final byte KEY_CODE_NAVI		= 0x02;
		public static final byte KEY_CODE_MEDIA		= 0x03;
		public static final byte KEY_CODE_MAP		= 0x04;
		public static final byte KEY_CODE_BACK		= 0x05;
		public static final byte KEY_CODE_INFO		= 0x06;
		public static final byte KEY_CODE_OK		= 0x07;
		public static final byte KEY_CODE_VOL_DEL_X = 0x08;
		public static final byte KEY_CODE_VOL_ADD_X = 0x09;
	}
	
	@Override
	public byte[] connect() {
		return super.connect();
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
		int iEps = ((packet[3] & 0x7F) << 8) + (packet[4] & 0xFF);
		if ((packet[3] >>7 & 0x01) == 0x01) {
			iEps = -((~(iEps-1)) & 0x7FFF);
		} 
		wheelInfo.mEps = iEps*45/550;
		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		wheelInfo.mKeyCode = packet[cursor] & 0xFF;
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
		if (packet[1] == 0x21) {
			TranslatePanelKey(wheelInfo);
		} else {
			TranslateKey(wheelInfo);
		}
		return wheelInfo;
	}

	private String mstrKeyCode = null;
	public WheelKeyInfo TranslateKey(WheelKeyInfo wheelKeyInfo) {
		switch (wheelKeyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_ADD:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DEL:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_PREV:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_NEXT:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_SOURCE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_SPEECH:
			wheelKeyInfo.mstrKeyCode = DDef.Speech;
			break;
		case KeyCode.KEY_CODE_BT:
			wheelKeyInfo.mstrKeyCode = DDef.Src_bt;
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
	
	private String mstrPanelCode = null;
	public WheelKeyInfo TranslatePanelKey(WheelKeyInfo wheelKeyInfo) {

		switch (wheelKeyInfo.mKeyCode) {
		case PanelCode.KEY_CODE_MENU:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case PanelCode.KEY_CODE_NAVI:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case PanelCode.KEY_CODE_MEDIA:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case PanelCode.KEY_CODE_MAP:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case PanelCode.KEY_CODE_BACK:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case PanelCode.KEY_CODE_INFO:
			wheelKeyInfo.mstrKeyCode = DDef.Src_info;
			break;
		case PanelCode.KEY_CODE_OK:
			wheelKeyInfo.mstrKeyCode = DDef.Enter;
			break;
		case PanelCode.KEY_CODE_VOL_ADD_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case PanelCode.KEY_CODE_VOL_DEL_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		}
		
		if (wheelKeyInfo.mKeyCode != 0) {
			mstrPanelCode = wheelKeyInfo.mstrKeyCode;
		}
		if (wheelKeyInfo.mKeyCode == 0) {
			wheelKeyInfo.mstrKeyCode = mstrPanelCode;
			mstrPanelCode = null;
		}
		return wheelKeyInfo;
	}

	@Override
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        backLightInfo.mLight = packet[cursor] * 255/0x08;
		return backLightInfo;
	}
	
	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		int cursor = 3;
		if (packet[1] == DATA_TYPE_REAR_RADAR_INFO) {
	        radarInfo.mBackLeftDis = packet[cursor++];
	        radarInfo.mBackLeftCenterDis = packet[cursor++];
	        radarInfo.mBackRightCenterDis = packet[cursor++];
	        radarInfo.mBackRightDis = packet[cursor++];
		} else if (packet[1] == DATA_TYPE_FRONT_RADAR_INFO) {
	        radarInfo.mFrontLeftDis = packet[cursor++];
	        radarInfo.mFrontLeftCenterDis = packet[cursor++];
	        radarInfo.mFrontRightCenterDis = packet[cursor++];
	        radarInfo.mFrontRightDis = packet[cursor++];
		}
		return radarInfo;
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
	public int GetBackRadarInfoCmdId() {
		return DATA_TYPE_REAR_RADAR_INFO;
	}

	
	@Override
	public int GetFrontRadarInfoCmdId() {
		return DATA_TYPE_FRONT_RADAR_INFO;
	}

	public MGCarSet parseCarSet(byte[] packet, MGCarSet carSet) {
		int cursor = 3;
		byte data0 = packet[cursor++];
		carSet.mDrivingLatch = (byte) (data0 >> 7 & 0x01);
		carSet.mUnlock = (byte) (data0 >> 6 & 0x01);
		carSet.mUnlockMode = (byte) (data0 >> 5 & 0x01);
		carSet.mNearUnlock = (byte) (data0 >> 4 & 0x01);
		
		byte data1 = packet[cursor++];
		carSet.mReversLights = (byte) (data1 >> 7 & 0x01);
		carSet.mNearLights = (byte) (data1 >> 6 & 0x01);
		carSet.mFogLights = (byte) (data1 >> 5 & 0x01);
		carSet.mTime = (byte) (data1 & 0x0F);
		
		byte data2 = packet[cursor++];
		carSet.mCarReversLights = (byte) (data2 >> 7 & 0x01);
		carSet.mCarNearLights = (byte) (data2 >> 6 & 0x01);
		carSet.mCarFogLights = (byte) (data2 >> 5 & 0x01);
		carSet.mCarTime = (byte) (data2 & 0x0F);
		return carSet;
	}
	
	public OutTemputerInfo parseTemputerInfo(byte[] packet, OutTemputerInfo tempInfo) {
		tempInfo.mbEnable = true;
		if ((packet[3] >> 7 & 0x01) == 0x01) {
			tempInfo.mOutCTemp = -(packet[3] & 0x7F);
		} else {
			tempInfo.mOutCTemp = packet[3] & 0x7F;
		}
		return tempInfo;
	}
	
	public TPMSInfo parseTpmsInfo(byte[] packet, TPMSInfo tpmsInfo) {
		int cursor = 3;
		tpmsInfo.mFLTirePressure = ((packet[cursor++] & 0xFF) << 8) + (packet[cursor++] & 0xFF);
		tpmsInfo.mFRTirePressure = ((packet[cursor++] & 0xFF) << 8) + (packet[cursor++] & 0xFF);
		tpmsInfo.mBLTirePressure = ((packet[cursor++] & 0xFF) << 8) + (packet[cursor++] & 0xFF);
		tpmsInfo.mBRTirePressure = ((packet[cursor++] & 0xFF) << 8) + (packet[cursor++] & 0xFF);
		return tpmsInfo;
	}
}
