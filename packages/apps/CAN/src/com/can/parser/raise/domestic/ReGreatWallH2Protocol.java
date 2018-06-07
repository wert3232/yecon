package com.can.parser.raise.domestic;

import java.util.ArrayList;
import android.os.Message;

import com.can.assist.CanKey;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReGreatWallH2Protocol
 * 
 * @function:长城哈弗H2协议
 * @author Kim
 * @Date: 2016-7-23上午11:32:50
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReGreatWallH2Protocol extends ReProtocol {

	/**
	 * 协议数据类型定义 哈弗H2 ver1.0
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x23; // 空调信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x28; // 基本信息
	public static final byte DATA_TYPE_RADAR_INFO = (byte) 0x26; // 方向盘转角
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x7F; // 版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x30; // 方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x21; // 方向盘按键信息
	public static final byte DATA_TYPE_PANEL_KEY_INFO = (byte) 0x22; // 方向盘按键信息

	// Host -> Slave
	public static final byte DATA_TYPE_TIME_SET = (byte) 0x83; // 时间设置

	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_AIR_INFO & 0xFF;
	}

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BASE_INFO & 0xFF;
	}

	public int GetRadarInfoCmdId() {
		return DATA_TYPE_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetWheelInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_INFO & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	public int GetPanelKeyInfoCmdId() {
		return DATA_TYPE_PANEL_KEY_INFO & 0xFF;
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
        airInfo.mAutoLight2 = (byte)(( airData0 >> 3) & 0x1);       
        airInfo.mDaulLight = (byte)(( airData0 >> 2) & 0x1);       
        airInfo.mFWDefogger = (byte)(( airData0 >> 1) & 0x1);       
        airInfo.mRearLight = (byte)(( airData0 >> 0) & 0x1);

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
        airInfo.mUpwardWind = 0;
        airInfo.mParallelWind = 0;
        airInfo.mDowmWind = 0;
		airInfo.mFWDefogger = 0;
		
		if (airData1 == 0x01) {
			airInfo.mUpwardWind = 1;
		}else if (airData1 == 0x02) {
			airInfo.mUpwardWind = 1;
			airInfo.mDowmWind = 1;
		}else if (airData1 == 0x03) {
			airInfo.mDowmWind = 1;
		}else if (airData1 == 0x04) {
			airInfo.mFWDefogger = 1;
			airInfo.mDowmWind = 1;
		}else if (airData1 == 0x05) {
			airInfo.mFWDefogger = 1;
		}else if (airData1 == 0x06) {
			airInfo.mFWDefogger = 1;
			airInfo.mUpwardWind = 1;
		}else if (airData1 == 0x07) {
			airInfo.mFWDefogger = 1;
			airInfo.mUpwardWind = 1;
			airInfo.mDowmWind = 1;
		}
		
		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];
		airInfo.mDisplay = 1;
        airInfo.mMaxWindlv = 8;
		airInfo.mWindRate = airData2;
		
		// Data 3
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		airInfo.mbshowLTempLv = true;
		airInfo.mbyLeftTemplv = airData3;

		// Data 4
		cursor += 1;
		byte airData4 = (byte) packet[cursor];
		airInfo.mbshowRTempLv  = true;
		airInfo.mbyRightTemplv = airData4;
		
		// Data 5
		cursor += 1;
		int iairData5 = packet[cursor] & 0xFF;
		
		if (iairData5 != 0xff) {
			airInfo.mOutTempEnable = true;
			airInfo.mOutTemp = (float) (iairData5*0.5-40);
		}
		
		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!equals(mbyAirInfo, packet, 8)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}
	
	private boolean equals(byte[] bylastInfo, byte[] byNewInfo, int icursor) {

		int iIndex = 0;
		boolean bequals = true;

		for (byte b : byNewInfo) {
			if (iIndex == icursor) {
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

		// Data0
		byte baseData0 = (byte) packet[cursor];

		baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 3);
		baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 5);
		baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 4);
		baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 7);
		baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 6);

		return baseInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		// TODO Auto-generated method stub
		return super.parseRadarInfo(packet, radarInfo);
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte[] bydata = new byte[2];
		bydata[0] = packet[cursor+1];
		bydata[1] = packet[cursor];
		
		wheelInfo.mEps = DataConvert.byte2Short(bydata, 0);
		wheelInfo.mEps = (wheelInfo.mEps * 45 / 5500);

		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslateKey(wheelInfo);
	}

	public WheelKeyInfo parsePanelKeyInfo(byte[] packet, WheelKeyInfo KeyInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		KeyInfo.mKeyCode = packet[cursor];
		KeyInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslatePanelKey(KeyInfo);
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
		case 0x0b:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x0c:
			info.mstrKeyCode = DDef.Media_next;
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
		default:
			break;
		}

		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrkeycode;
			mstrkeycode = null;
		} else {
			mstrkeycode = info.mstrKeyCode;
		}

		return info;
	}

	public String mstrPanelkeycode = null;

	public WheelKeyInfo TranslatePanelKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub

		switch (info.mKeyCode) {
		case 0x01:
			info.mstrKeyCode = DDef.Power;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Src_radio;
			break;
		case 0x09:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x21:
			info.mstrKeyCode = DDef.Volume_add;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x22:
			info.mstrKeyCode = DDef.Volume_del;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x29:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x30:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x31:
			info.mstrKeyCode = DDef.Src_mode;
			break;			
		case 0x32:
			info.mstrKeyCode = DDef.Src_home;
			break;			
		case 0x33:
			info.mstrKeyCode = DDef.Phone_dial;
			break;			
		case 0x34:
			info.mstrKeyCode = DDef.Phone_hang;
			break;			
		case 0x35:
			info.mstrKeyCode = DDef.Src_set;
			break;
		case 0x36:
			info.mstrKeyCode = DDef.Src_navi;
			break;
		default:
			break;
		}

		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrPanelkeycode;
			mstrPanelkeycode = null;
		} else {
			mstrPanelkeycode = info.mstrKeyCode;
		}

		return info;
	}

	@Override
	public boolean IsSuportMeInfo() {
		// TODO Auto-generated method stub
		return true;
	}

	private byte[] getTime(MediaInfo mediaInfo) {
		byte[] byData = new byte[3];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();
		byData[0] = 0x03;
		byData[1] = timeInfo.byHour;
		byData[2] = timeInfo.byMinute;

		return byData;
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		ArrayList<Message> aListMsg = new ArrayList<Message>();

		aListMsg.add(CanTxRxStub.getTxMessage(DATA_TYPE_TIME_SET,
				DATA_TYPE_TIME_SET, getTime(mediaInfo), this));

		return aListMsg;
	}
}
