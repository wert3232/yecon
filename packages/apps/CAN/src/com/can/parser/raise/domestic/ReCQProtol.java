package com.can.parser.raise.domestic;



import java.util.ArrayList;

import android.os.Message;

import com.can.assist.CanKey;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CQCarSet;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;

public class ReCQProtol extends ReProtocol {
	//Slave - Host
	public static final byte DATA_TYPE_AIR_SET_INFO		= 0x10;
	public static final byte DATA_TYPE_WHEEL_KEY_INFO	= 0x12;
	public static final byte DATA_TYPE_BACK_LIGHT_INFO  = 0x14;
	public static final byte DATA_TYPE_BASE_INFO 		= 0x24;
	public static final byte DATA_TYPE_WHEEL_INFO 		= 0x31;
	public static final byte DATA_TYPE_REAR_RADAR_INFO	= 0x32;
	public static final byte DATA_TYPE_FRONT_RADAR_INFO	= 0x33;
	public static final byte DATA_TYPE_PANORAMIC_IMAGE	= 0x40;
	public static final byte DATA_TYPE_PANEL_INFO		= 0x50;
	public static final byte DATA_TYPE_LINK_SOS_INFO	= 0x51;
	public static final byte DATA_TYPE_CAR_SET_INFO		= 0x52;

	//Host - Slave
	public static final byte DATA_TYPE_REAR_VIEW_SW_REQUEST = (byte) 0x82;
	public static final byte DATA_TYPE_CAR_SET_REQUEST		= (byte) 0x83;
	public static final byte DATA_TYPE_ALL_IAMGE_REQUEST	= (byte) 0x84;
	public static final byte DATA_TYPE_ALL_IAMGE_SET_REQUEST = (byte) 0xC7;
	public static final byte DATA_TYPE_TIME_SET_REQUEST 	= (byte) 0xC8;
	
	public class KeyCode {
		public static final byte KEY_CODE_NULL 		= 0x00;
		public static final byte KEY_CODE_SOURCE	= 0x11;
		public static final byte KEY_CODE_NEXT		= 0x12;
		public static final byte KEY_CODE_PREV		= 0x13;
		public static final byte KEY_CODE_VOL_UP	= 0x14;
		public static final byte KEY_CODE_VOL_DOWN	= 0x15;
		public static final byte KEY_CODE_MUTE		= 0x16;
		public static final byte KEY_CODE_PICKUP	= 0x30;
		public static final byte KEY_CODE_HUNG		= 0x31;
	}
	
	public class PanelCode {
		public static final byte KEY_CODE_NULL 		= 0x00;
		public static final byte KEY_CODE_POWER		= 0x01;
		public static final byte KEY_CODE_EJECT		= 0x02;
		public static final byte KEY_CODE_MENU		= 0x03;
		public static final byte KEY_CODE_MODE		= 0x04;
		public static final byte KEY_CODE_BAND		= 0x05;  // AM/FM
		public static final byte KEY_CODE_SEARCH_UP	= 0x06;
		public static final byte KEY_CODE_SEARCH_DN	= 0x07;
		public static final byte KEY_CODE_SET		= 0x08;
		public static final byte KEY_CODE_MUTE		= 0x09;
		public static final byte KEY_CODE_NAVI		= 0x0A;
		public static final byte KEY_CODE_SEL		= 0x0B;
		public static final byte KEY_CODE_VOL_ADD	= 0x10;	// VOL右旋
		public static final byte KEY_CODE_VOL_DEL	= 0x11; // VOL左旋
		public static final byte KEY_CODE_NEXT		= 0x12; // Tune右旋
		public static final byte KEY_CODE_PREV		= 0x13; // Tune左旋
		public static final byte KEY_CODE_NUM1		= 0x21;
		public static final byte KEY_CODE_NUM2		= 0x22;
		public static final byte KEY_CODE_NUM3		= 0x23;
		public static final byte KEY_CODE_NUM4		= 0x24;
		public static final byte KEY_CODE_NUM5		= 0x25;
		public static final byte KEY_CODE_NUM6		= 0x26;
		public static final byte KEY_CODE_APS		= 0x27;
		public static final byte KEY_CODE_SCAN		= 0x28;
	}
	
	@Override
	public byte[] connect() {
//		mCarType = CanXml.getCanDescribe().iCarTypeID;
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
		int iEps = (((packet[3] & 0xFF) << 8) + (packet[4] & 0xFF)) - 0x1E00;
		wheelInfo.mEps = iEps*45/0x1300;
		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		wheelInfo.mKeyCode = packet[cursor] & 0xFF;
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
		if (packet[1] == 0x50) {
			return  TranslatePanelKey(wheelInfo);
		} else	if (packet[2] == 0x01) {
			wheelInfo.mKeyStatus = wheelInfo.mKeyCode == 0x00 ? 0x00 : 0x01;
		} 
		return TranslateKey(wheelInfo);
	}

	private String mstrKeyCode = null;
	public WheelKeyInfo TranslateKey(WheelKeyInfo wheelKeyInfo) {
		
		switch (wheelKeyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_SOURCE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_PICKUP:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_HUNG:
			wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_PREV:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_NEXT:
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

	private String mstrPanelCode = null;
	public WheelKeyInfo TranslatePanelKey(WheelKeyInfo wheelKeyInfo) {
		
		switch (wheelKeyInfo.mKeyCode) {
		case PanelCode.KEY_CODE_POWER:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case PanelCode.KEY_CODE_EJECT:
			wheelKeyInfo.mstrKeyCode = DDef.Eject;
			break;
		case PanelCode.KEY_CODE_MENU:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case PanelCode.KEY_CODE_MODE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case PanelCode.KEY_CODE_BAND:
			wheelKeyInfo.mstrKeyCode = DDef.Src_radio;
			break;
		case PanelCode.KEY_CODE_SEARCH_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case PanelCode.KEY_CODE_SEARCH_DN:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case PanelCode.KEY_CODE_SET:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case PanelCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case PanelCode.KEY_CODE_NAVI:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case PanelCode.KEY_CODE_SEL:
			wheelKeyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case PanelCode.KEY_CODE_VOL_ADD:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case PanelCode.KEY_CODE_VOL_DEL:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case PanelCode.KEY_CODE_NEXT:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case PanelCode.KEY_CODE_PREV:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case PanelCode.KEY_CODE_NUM1:
			wheelKeyInfo.mstrKeyCode = DDef.Num1;
			break;
		case PanelCode.KEY_CODE_NUM2:
			wheelKeyInfo.mstrKeyCode = DDef.Num2;
			break;
		case PanelCode.KEY_CODE_NUM3:
			wheelKeyInfo.mstrKeyCode = DDef.Num3;
			break;
		case PanelCode.KEY_CODE_NUM4:
			wheelKeyInfo.mstrKeyCode = DDef.Num4;
			break;
		case PanelCode.KEY_CODE_NUM5:
			wheelKeyInfo.mstrKeyCode = DDef.Num5;
			break;
		case PanelCode.KEY_CODE_NUM6:
			wheelKeyInfo.mstrKeyCode = DDef.Num6;
			break;
		case PanelCode.KEY_CODE_APS:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_as;
			break;
		case PanelCode.KEY_CODE_SCAN:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_as;
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
        byte DataType = (byte) packet[1];
        byte byMode = packet[cursor++];
        byte byMax = packet[cursor++];
    	if (DataType == DATA_TYPE_REAR_RADAR_INFO) {
            radarInfo.mBackLeftDis = getRadarValue(byMode, packet[cursor++], byMax);
            radarInfo.mBackLeftCenterDis = getRadarValue(byMode, packet[cursor++], byMax);
            radarInfo.mBackRightCenterDis = getRadarValue(byMode, packet[cursor++], byMax);
            radarInfo.mBackRightDis = getRadarValue(byMode, packet[cursor++], byMax);
        } else if (DataType == DATA_TYPE_FRONT_RADAR_INFO) {
            radarInfo.mFrontLeftDis = getRadarValue(byMode, packet[cursor++], byMax);
            radarInfo.mFrontLeftCenterDis = getRadarValue(byMode, packet[cursor++], byMax);
            radarInfo.mFrontRightCenterDis = getRadarValue(byMode, packet[cursor++], byMax);
            radarInfo.mFrontRightDis = getRadarValue(byMode, packet[cursor++], byMax);
        }
        return radarInfo;
	}
	
	private byte getRadarValue(byte byMode, byte byData, byte byMax) {
		byte byValue = 0x00;
		if (byMode == 0x00) {
			byValue = (byte)(byData == byMax ? 0x00 : byData + 1);
		} else {
			if (byData >= 0x19 && byData <= 0x7F) {
				if (byData == 0x19) {
					byValue = 0x01;
				} else {
					byValue = (byte) ((byData - 0x19)*3/0x66 + 2);
				}
			} 
		}
		return byValue;
	}
	
	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		 //Data 0 
		int cursor = 3;
        airInfo.mWindRate = packet[cursor++];
        
        byte data1 = packet[cursor++];
        airInfo.mCircleState = (byte)(( data1 >> 7) & 0x1);   
        airInfo.mFWDefogger = (byte) (( data1 >> 6) & 0x1);
        airInfo.mRearLight = (byte)(( data1 >> 5) & 0x1);
        airInfo.mDowmWind = (byte)((data1) >> 4 & 0x1);
        airInfo.mParallelWind = (byte)((data1 >> 3) & 0x1);
        airInfo.mAutoLight1 = (byte)((data1 >> 2) & 0x1);    
        airInfo.mAcState = (byte) (data1 & 0x01);
        
        byte data2 = packet[cursor++];
        airInfo.mAcMax = (byte) ((data2 >> 7) & 0x01);
        airInfo.mDaulLight = (byte)((data2 >> 6) & 0x01);       
        airInfo.mWindRate = (byte) (data2 & 0x0F);
        //Data 1
        byte data3 = packet[cursor++]; 
        airInfo.mMaxTemp = 32;
        airInfo.mMinTemp = 18;
        airInfo.mLeftTemp = (float) ((data3 - 0x01)*0.25 + 18);
        
        byte data4 = packet[cursor++]; 
        airInfo.mRightTemp = (float) ((data4 - 0x01)*0.25 + 18);
        return airInfo;
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
	public int GetBackLightInfoCmdId() {
		return DATA_TYPE_BACK_LIGHT_INFO;
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		return DATA_TYPE_REAR_RADAR_INFO;
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		return DATA_TYPE_FRONT_RADAR_INFO;
	}

	@Override
	public int GetAirInfoCmdId() {
		return DATA_TYPE_AIR_SET_INFO;
	}

	@Override
	public boolean IsSuportMeInfo() {
		return true;
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		ArrayList<Message> aListMsg = new ArrayList<Message>();
		aListMsg.add(getTimeInfo(mediaInfo));
		return aListMsg;
	}
	
	private Message getTimeInfo(MediaInfo mediaInfo) {
		byte byCmdId = ReCQProtol.DATA_TYPE_TIME_SET_REQUEST;
		byte[] byData = new byte[4];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();
		byData[0] = timeInfo.byMinute;
		byData[1] = timeInfo.byHour;
		byData[2] = timeInfo.by24Mode;
		byData[3] = timeInfo.byAmPm;

		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}
	
	public CQCarSet parseCarSet(byte[] packet, CQCarSet carSet) {
		switch (packet[3]) {
		case 0x01:
			carSet.mLan = packet[4];
			break;
		case 0x02:
			carSet.mCompressor = packet[4];
			break;
		case 0x03:
			carSet.mCycleCtrl = packet[4];
			break;
		case 0x04:
			carSet.mCozy = packet[4];
			break;
		case 0x05:
			carSet.mLeftAutoHeat = packet[4];
			break;
		case 0x06:
			carSet.mRightAutoHeat = packet[4];
			break;
		case 0x07:
			carSet.mOverSpeed = packet[4];
			break;
		case 0x08:
			carSet.mAlramVol = packet[4];
			break;
		case 0x09:
			carSet.mPowerTime = packet[4];
			break;
		case 0x0A:
			carSet.mStartTime = packet[4];
			break;
		case 0x0B:
			carSet.mSteerMode = packet[4];
			break;
		case 0x0C:
			carSet.mRemoteUnlock = packet[4];
			break;
		case 0x0D:
			carSet.mLockBySpeed = packet[4];
			break;
		case 0x0E:
			carSet.mAutoUnlock = packet[4];
			break;
		case 0x0F:
			carSet.mRemoteWind = packet[4];
			break;
		case 0x10:
			carSet.mFrontWiper = packet[4];
			break;
		case 0x11:
			carSet.mRearWiper = packet[4];
			break;
		case 0x12:
			carSet.mFollowToHome = packet[4];
			break;
		case 0x13:
			carSet.mFogLights = packet[4];
			break;
		case 0x14:
			carSet.mDayLights = packet[4];
			break;
		case 0x15:
			carSet.mLightSensitivity = packet[4];
			break;
		case 0x16:
			carSet.mLeftHeatLv = packet[4];
			break;
		case 0x17:
			carSet.mRightHeatLv = packet[4];
			break;
		case 0x18:
			carSet.mAnionMode = packet[4];
			break;
		case 0x19:
			carSet.mWelcomeFunc = packet[4];
			break;
		case 0x1A:
			carSet.mAutoKey = packet[4];
			break;
		case 0x1B:
			carSet.mRearviewMirror = packet[4];
			break;
		default:
			break;
		}
		return carSet;
	}
}
