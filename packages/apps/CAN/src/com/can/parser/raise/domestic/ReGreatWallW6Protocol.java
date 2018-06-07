package com.can.parser.raise.domestic;

import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

/**
 * ClassName:ReGreatWallW6Protocol
 * 
 * @function:长城风骏6协议
 * @author Kim
 * @Date:  2016-7-23上午11:31:18
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReGreatWallW6Protocol extends ReProtocol{

	/**
	 * 协议数据类型定义 风骏6 ver1.0
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x23; // 空调信息
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
		int cursor = 3; // 跳过2E、Data Type、Length字段

		// Data 0
		byte airData0 = (byte) packet[cursor];
		airInfo.mAiron = (byte) ((airData0 >> 7) & 0x1);
		airInfo.mAcState = (byte) ((airData0 >> 6) & 0x1);
        airInfo.mAutoLight2 = (byte)(( airData0 >> 3) & 0x1);       

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
        airInfo.mMaxWindlv = 7;
		airInfo.mWindRate = airData2;
		
		// Data 3
		cursor += 1;
		int airData3 = packet[cursor] & 0xFF;
		
		if (airData3 == 0x7F) {
			airInfo.mbshowLTemp = false;
		}else {
			airInfo.mbshowLTemp = true;
			if (airData3 == 0x00 || airData3 == 0x1F) {
				if (airData3 == 0x1F) {
					airInfo.mLeftTemp = 0xFF;
				}else {
					airInfo.mLeftTemp = 0x00;
				}
			}else {
				airInfo.mLeftTemp = airData3 + 17;
			}
		}

		airInfo.mMinTemp = 0x00;
		airInfo.mMaxTemp = 0xFF;

		// Data 4
		cursor += 1;
		int airData4 = packet[cursor] & 0xFF;
		
		if (airData4 == 0x7F) {
			airInfo.mbshowRTemp = false;
		}else {
			airInfo.mbshowRTemp = true;
			if (airData4 == 0x00 || airData4 == 0x1F) {
				if (airData4 == 0x1F) {
					airInfo.mRightTemp = 0xFF;
				}else {
					airInfo.mRightTemp = 0x00;
				}
			}else {
				airInfo.mRightTemp = airData4 + 17;
			}
		}
		
		// Data 5
		cursor += 1;
		byte airData5 = packet[cursor];
		
		airInfo.mOutTempEnable = true;
		if (DataConvert.GetBit(airData5 & 0xFF, 7) == 1) {
			airInfo.mOutTemp = -(airData5 & 0x7F);
		}else {
			airInfo.mOutTemp = (airData5 & 0x7F);
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
		case 0x0d:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x0e:
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
}
