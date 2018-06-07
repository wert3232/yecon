package com.can.parser.raise.domestic;

import com.can.parser.DDef;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

/**
 * ClassName:ReGeelyEc7Protocol
 * 
 * @function:吉利Ec7协议
 * @author Kim
 * @Date: 2016-7-23下午2:15:50
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReGeelyEc7Protocol extends ReProtocol {
	/**
	 * 协议数据类型定义 吉利Ec7协议 ver1.0
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x24; // 基本信息
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x20; // 方向盘按键信息

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BASE_INFO & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		// Data0
		byte baseData0 = (byte) packet[cursor];

		baseInfo.mFrontBoxDoor = (byte) DataConvert.GetBit(baseData0, 2);
		baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 3);
		baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 5);
		baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 4);
		baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 7);
		baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 6);

		return baseInfo;
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
		case 0x03:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x04:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Src_mode;
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
