package com.can.parser.raise.domestic;


import com.can.assist.CanXml;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarDomestic;
import com.can.parser.DDef.PhoneState;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;
import com.can.ui.CanPopWind;

public class ReBQProtol extends ReProtocol {
	
	private int mCarType = 0x09;

	public static final byte DATA_TYPE_BACK_LIGHT_INFO  = 0x14;
	public static final byte DATA_TYPE_WHEEL_KEY_INFO 	= 0x20;
	public static final byte DATA_TYPE_BASE_INFO 		= 0x24;
	public static final byte DATA_TYPE_WHEEL_INFO 		= 0x29;

	public class KeyCode {
		public static final byte KEY_CODE_NULL 		= 0x00;
		public static final byte KEY_CODE_VOL_UP 	= 0x01; // vol+
		public static final byte KEY_CODE_VOL_DOWN 	= 0x02; // vol+
		public static final byte KEY_CODE_NEXT		= 0x03;
		public static final byte KEY_CODE_PREV		= 0x04;
		public static final byte KEY_CODE_PICKUP	= 0x05;	// D60 为接挂电话,X55 为接电话
		public static final byte KEY_CODE_MUTE		= 0x06; // X55 有电话时为挂断，无电话时静音
		public static final byte KEY_CODE_SOURCE	= 0x07; // mode
		public static final byte KEY_CODE_HUNG		= 0x08;
	}
	
	
	@Override
	public byte[] connect() {
		mCarType = CanXml.getCanDescribe().iCarTypeID;
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
		int iEps = DataConvert.byte2Short(packet, 3);
		if (mCarType == CarDomestic.BQD60) {
			if (iEps > 0x1F00) {
				iEps = -(iEps - 0x1F00);
			} else if (iEps < 0x1F00) {
				iEps = 0x1F00 - iEps;
			} else {
				iEps = 0x00;
			}
		} else if (mCarType == CarDomestic.BQD60) {
			if (iEps > 0x1E80) {
				iEps = -(iEps - 0x1E80);
			} else if (iEps < 0x1E80) {
				iEps = 0x1E80 - iEps;
			} else {
				iEps = 0x00;
			}
		}

		wheelInfo.mEps = iEps*45/0x13FF;
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
		case KeyCode.KEY_CODE_SOURCE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_PICKUP:
			{
				wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
				int iPhoneState = CanPopWind.mObjGdSharedData.getMediaInfo().getPhonestate();
				if(mCarType == CarDomestic.BQD60){
					if ((iPhoneState == PhoneState.INCOMING || 
							iPhoneState == PhoneState.OUTGOING || 
							iPhoneState == PhoneState.SPEAKING)) {
						wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
					}
				}
			}
			break;
		case KeyCode.KEY_CODE_MUTE:
			{
				wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
				int iPhoneState = CanPopWind.mObjGdSharedData.getMediaInfo().getPhonestate();
				if(mCarType == CarDomestic.BQX55){
					if ((iPhoneState == PhoneState.INCOMING || 
							iPhoneState == PhoneState.OUTGOING || 
							iPhoneState == PhoneState.SPEAKING)) {
						wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
					}
				}
			}
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

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		int cursor = 3; //跳过2E、Data Type、Length字段
        
        //Data 0 
        airInfo.mWindRate = packet[cursor++];
        
        byte data1 = packet[cursor++];
        airInfo.mCircleState = (byte)(( data1 >> 7) & 0x1);   
        airInfo.mFWDefogger = (byte) (( data1 >> 6) & 0x1);
        airInfo.mRearLight = (byte)(( data1 >> 5) & 0x1);
        airInfo.mDowmWind = (byte)((data1) >> 4 & 0x1);
        airInfo.mParallelWind = (byte)((data1 >> 3) & 0x1);
        airInfo.mAutoLight1 = (byte)((data1 >> 2) & 0x1);    
          
        
        byte data2 = packet[cursor++];
        airInfo.mAcMax = (byte) ((data2 >> 7) & 0x01);
        airInfo.mDaulLight = (byte)((data2 >> 6) & 0x01);       
        airInfo.mWindRate = (byte) (data2 & 0x0F);
        //Data 1
        byte data3 = packet[cursor++]; 
        airInfo.mMaxTemp = 0x39;
        airInfo.mMinTemp = 0x01;
        airInfo.mLeftTemp = (float) ((data3 - 0x01)*0.5 + 18);
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
}
