package com.can.parser.raise.domestic;



import java.util.ArrayList;

import android.os.Message;

import com.can.assist.CanKey;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.MGCarSet;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;

public class ReMGProtol extends ReProtocol {
	//Slave - Host
	public static final byte DATA_TYPE_WHEEL_KEY_INFO	= 0x20;
	public static final byte DATA_TYPE_AIR_SET_INFO		= 0x21;
	public static final byte DATA_TYPE_REAR_RADAR_INFO	= 0x22;
	public static final byte DATA_TYPE_BASE_INFO 		= 0x24;
	public static final byte DATA_TYPE_OUT_TEMP_INFO  	= 0x27;
	public static final byte DATA_TYPE_WHEEL_INFO 		= 0x29;
	public static final byte DATA_TYPE_CAR_SET_INFO		= 0x40;

	//Host - Slave
	public static final byte DATA_TYPE_AIR_SET_REQUEST = (byte) 0x8A;
	public static final byte DATA_TYPE_CAR_SET_REQUEST		= (byte) 0xC6;
	public static final byte DATA_TYPE_ALL_IAMGE_REQUEST	= (byte) 0x84;
	public static final byte DATA_TYPE_ALL_IAMGE_SET_REQUEST = (byte) 0xC7;
	public static final byte DATA_TYPE_TIME_SET_REQUEST 	= (byte) 0xA6;
	
	public class KeyCode {
		public static final byte KEY_CODE_NULL 		= 0x00;
		public static final byte KEY_CODE_VOL_ADD	= 0x01;
		public static final byte KEY_CODE_VOL_DEL	= 0x02;
		public static final byte KEY_CODE_NEXT		= 0x04;
		public static final byte KEY_CODE_PREV		= 0x03;
		public static final byte KEY_CODE_HUNG	 	= 0x06;
		public static final byte KEY_CODE_SOURCE	= 0x07;
		public static final byte KEY_CODE_SPEECH	= 0x08;
		public static final byte KEY_CODE_BT		= 0x09;
		public static final byte KEY_CODE_PICKUP	= 0x10;
		public static final byte KEY_CODE_HOME		= 0x11;
		public static final byte KEY_CODE_BACK		= 0x12;
		public static final byte KEY_CODE_HOME1		= 0x13;
		public static final byte KEY_CODE_MUTE		= 0x16;
		public static final byte KEY_CODE_NAVI		= 0x32;
		public static final byte KEY_CODE_POWER		= (byte) 0x80;
		public static final byte KEY_CODE_VOL_ADD_X	= (byte) 0x81;
		public static final byte KEY_CODE_VOL_DEL_X	= (byte) 0x82;
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
//		baseInfo.mFrontBoxDoor = (byte) (data0 >> 2 & 0x01);
		return baseInfo;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		int iEps = -((((packet[3] & 0xFF) << 8) + (packet[4] & 0xFF)) - 0x7F8D);
		wheelInfo.mEps = iEps*45/0x1E96;
		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		wheelInfo.mKeyCode = packet[cursor] & 0xFF;
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
		if (packet[2] == 0x01) {
			wheelInfo.mKeyStatus = wheelInfo.mKeyCode == 0x00 ? 0x00 : 0x01;
		} 
		return TranslateKey(wheelInfo);
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
		case KeyCode.KEY_CODE_SPEECH:
			wheelKeyInfo.mstrKeyCode = DDef.Speech;
			break;
		case KeyCode.KEY_CODE_BT:
			wheelKeyInfo.mstrKeyCode = DDef.Src_bt;
			break;
		case KeyCode.KEY_CODE_HOME:
		case KeyCode.KEY_CODE_HOME1:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case KeyCode.KEY_CODE_BACK:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case KeyCode.KEY_CODE_NAVI:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case KeyCode.KEY_CODE_POWER:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case KeyCode.KEY_CODE_VOL_ADD_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DEL_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
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
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        backLightInfo.mLight = packet[cursor] * 255/0x08;
		return backLightInfo;
	}
	
	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		int cursor = 3;
        radarInfo.mBackLeftDis = packet[cursor++];
        radarInfo.mBackLeftCenterDis = packet[cursor++];
        radarInfo.mBackRightCenterDis = packet[cursor++];
        radarInfo.mBackRightDis = packet[cursor++];
        return radarInfo;
	}
	
	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		 //Data 0 
		int cursor = 3;
		byte data0 = packet[cursor++];
		airInfo.mAiron = (byte) (data0 >> 7 & 0x01);
		airInfo.mAcState = (byte) (data0 >> 6 & 0x01);
		airInfo.mCircleState = (byte) (data0 >> 5 & 0x01);
		airInfo.mAutoLight1 = (byte) (data0 >> 4 & 0x01);
        
        byte data1 = packet[cursor++];
        airInfo.mWindMode = (byte) (data1 >> 4 & 0x0F);
        if (airInfo.mWindMode == 0x00 || airInfo.mWindMode == 0x01) {
			airInfo.mParallelWind = 0x01;
		} else {
			airInfo.mParallelWind = 0x00;
		}
        
        if (airInfo.mWindMode == 0x01 || airInfo.mWindMode == 0x02 || airInfo.mWindMode == 0x03) {
			airInfo.mDowmWind = 0x01;
		} else {
			airInfo.mDowmWind = 0x00;
		}
        
        if (airInfo.mWindMode == 0x03 || airInfo.mWindMode == 0x04) {
			airInfo.mUpwardWind = 0x01;
		} else {
			airInfo.mUpwardWind = 0x00;
		}
        
//        airInfo.mAutoLight1 = (byte) (airInfo.mWindMode == 0x0F ? 0x01 : 0x00);
        airInfo.mWindRate = (byte) (data1 & 0x0F);
        
        byte data2 = packet[cursor++];
		airInfo.mLeftTemp = 16 + data2;
        airInfo.mRightTemp = airInfo.mLeftTemp;
        airInfo.mMaxTemp = 16 + 0x0F;
        airInfo.mMinTemp = 16 + 0x00;
        
        byte data3 = packet[cursor++]; 
        airInfo.mFWDefogger = (byte) (( data3 >> 7) & 0x1);
        airInfo.mRearLight = (byte)(( data3 >> 6) & 0x1);
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
	public int GetBackRadarInfoCmdId() {
		return DATA_TYPE_REAR_RADAR_INFO;
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
		byte byCmdId = ReMGProtol.DATA_TYPE_TIME_SET_REQUEST;
		byte[] byData = new byte[5];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();
		byte by24Mode = (byte) (timeInfo.by24Mode == 0x01 ? 0x00 : 0x01);
		byData[0] = (byte) (timeInfo.iYear - 2000);
		byData[1] = timeInfo.byMonth;
		byData[2] = timeInfo.byDay;
		byData[3] = (byte) (timeInfo.byHour| (by24Mode << 7));
		byData[4] = timeInfo.byMinute ;

		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
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
	};
}
