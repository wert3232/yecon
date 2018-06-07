package com.can.parser.raise.domestic;

import com.can.assist.CanKey;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;

/**
 * ClassName:ReGreatWallC30Protocol
 * 
 * @function:长城C30
 * @author Kim
 * @Date: 2016-7-23上午11:38:05
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReGreatWallC30Protocol extends ReProtocol{
	/**
	 * 协议数据类型定义 长安锐翔V7 ver1.01
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x23; // 空调信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x7F; // 版本信息
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x21; // 方向盘按键信息

	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_AIR_INFO & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	private byte[] mbyAirInfo = null;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
	 	int cursor = 3; //跳过2E、Data Type、Length字段
        
        //Data 0 
        byte airData0 = (byte)packet[cursor];  
        airInfo.mAcState = (byte)(( airData0 >> 6) & 0x1);       
        airInfo.mCircleState = (byte)(( airData0 >> 5) & 0x1);           
        airInfo.mRearLight = (byte)(( airData0 >> 1) & 0x1);
        
        //Data 1
        cursor += 1;
        byte airData1 = (byte)packet[cursor]; 
        airInfo.mUpwardWind = 0;
        airInfo.mParallelWind = 0;
        airInfo.mDowmWind = 0;
		airInfo.mFWDefogger = 0;
		
		if (airData1 == 0x01) {
			airInfo.mParallelWind = 1;
		}else if (airData1 == 0x02) {
			airInfo.mParallelWind = 1;
			airInfo.mDowmWind = 1;
		}else if (airData1 == 0x03) {
			airInfo.mDowmWind = 1;
		}else if (airData1 == 0x04) {
			airInfo.mFWDefogger = 1;
			airInfo.mDowmWind = 1;
		}else if (airData1 == 0x05) {
			airInfo.mFWDefogger = 1;
		}
		
        //Data 2
        cursor += 1;
        byte airData2 = (byte)packet[cursor];
        airInfo.mDisplay = 1;
        airInfo.mMaxWindlv = 8;
        airInfo.mWindRate = airData2;
        airInfo.mAiron = (byte) ((airData2 == 1) ? 0 : 1);  
        

        //Data 3 
        cursor += 1;
        byte airData3 = (byte)packet[cursor];
		airInfo.mbshowLTempLv = true;
		airInfo.mbshowRTempLv = true;
		airInfo.mbyLeftTemplv = airData3;
		airInfo.mbyRightTemplv = airData3;
        
        if (mbyAirInfo != null) {
			
        	airInfo.bAirUIShow = (!mbyAirInfo.equals(packet) && 
        			(airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true : false;
		}
        
        mbyAirInfo = packet;
        
        return airInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslateKey(wheelInfo);
	}
	
	public String mstrkeycode = null;
	
	public WheelKeyInfo TranslateKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub

		switch (info.mKeyCode) {
		case 0x01:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case 0x02:
			info.mstrKeyCode = DDef.Volume_del;
			break;
		case 0x06:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x0c:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x0b:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x09:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case 0x0a:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		case 0x20:
			info.mstrKeyCode = DDef.Radio_as;
			break;
		case 0x21:
			info.mstrKeyCode = DDef.Src_radio;
			break;
		case 0x22:
			info.mstrKeyCode = DDef.Src_navi;
			break;
		case 0x23:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x24:
			info.mstrKeyCode = DDef.Media_next;			
			break;
		case 0x25:
			info.mstrKeyCode = DDef.Media_pause;		
			break;
		case 0x26:
			info.mstrKeyCode = DDef.Media_pause;
			break;
		case 0x27:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		case 0x28:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case 0x29:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x2A:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x2B:
			info.mstrKeyCode = DDef.Volume_add;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x2C:
			info.mstrKeyCode = DDef.Volume_del;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x2D:
			info.mstrKeyCode = DDef.Power;
			break;
		case 0x2E:
			info.mstrKeyCode = DDef.Eject;
			break;
		case 0x2F:
			info.mstrKeyCode = DDef.Src_eq;
			break;
		case 0x30:
			info.mstrKeyCode = DDef.Src_set;
			break;
		default:
			break;
		}
		
		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrkeycode;
			mstrkeycode = null;
		}else {
			mstrkeycode = info.mstrKeyCode;
		}
		return info;
	}
}
