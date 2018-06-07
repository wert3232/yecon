package com.can.parser.raise.mazda;

import java.util.ArrayList;

import android.os.Message;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.PowerAmplifier;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReMazdaProtocol
 * 
 * @function:睿志城马自达系列
 * @author Kim
 * @Date: 2016-7-16上午9:14:21
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReMazdaProtocol extends ReProtocol {

	/**
	 * 协议数据类型定义 马自达协议V1.12
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO1 = (byte) 0x13; // 空调信息1
	public static final byte DATA_TYPE_AIR_INFO2 = (byte) 0x10; // 空调信息2
	public static final byte DATA_TYPE_RADAR_INFO = (byte) 0x32; // 雷达信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x29; // 方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x20; // 方向盘按键信息
	public static final byte DATA_TYPE_PANEL_KEY_INFO = (byte) 0x12; // 方向盘按键信息
	public static final byte DATA_TYPE_DSP_INFO = (byte) 0x31; // 原车功放信息
	public static final byte DATA_TYPE_VER_INFO = (byte) 0x7F; // 版本信息

	// Host -> Slave
	public static final byte DATA_TYPE_DSP_SET = (byte) 0x84; // 原车功放信息
	public static final byte DATA_TYPE_SET_CLOCK = (byte) 0x06; // 原车功放信息
	// 协议格式
	final byte mHeadFlag = (byte) 0xFD;
	final byte mHeadCursor = (byte) 0;
	final byte mDataLenCursor = (byte) 1;
	final byte mCmdIDCursor = (byte) 2;
	final byte mDataCursor = (byte) 3;

	@Override
	public byte[] getPacket(int cmdID, int cmdSubID, byte[] datas) {
		// TODO Auto-generated method stub
		// 3 是 head + datatype + len, 1 是 checksum
		byte[] packet = new byte[3 + 1 + datas.length];

		// 填充包头
		packet[0] = mHeadFlag;

		// 填充length
		packet[1] = (byte) (3 + datas.length & 0xFF);

		// 填充datatype
		packet[2] = (byte) (cmdID & 0xFF);

		// 填充 data section
		System.arraycopy(datas, 0, packet, mDataCursor, datas.length);

		// 计算 checksum，填充 checksum section
		packet[packet.length - 1] = (byte) (caculateCheckSum(packet, 0,
				packet.length) & 0xFF);

		return packet;
	}

	@Override
	public int caculateCheckSum(byte[] buffer, int cursor, int packetLength) {
		// TODO Auto-generated method stub
		int iCheckSum = 0;
		int iDataEnd = packetLength - 2;
		int i = 0;

		iDataEnd += cursor;

		for (i = 1; i <= iDataEnd; i++) {
			iCheckSum += buffer[i];
		}

		return iCheckSum & 0xFF;
	}

	@Override
	public int getCheckSum(byte[] buffer, int cursor, int packetLength) {
		// TODO Auto-generated method stub
		return super.getCheckSum(buffer, cursor, packetLength);
	}

	@Override
	public boolean isPacketHeader(byte[] buffer, int cursor) {
		return (buffer[mHeadCursor + cursor] == mHeadFlag);
	}

	@Override
	public int parseDataLength(byte[] buffer, int cursor) {
		int dataLength = (int) (buffer[mDataLenCursor + cursor] & 0xFF);
		return dataLength;
	}

	@Override
	public int ParseRegisterCmdId(byte[] buffer, int cursor) {
		return (int) (buffer[cursor + mCmdIDCursor] & 0xFF);
	}

	@Override
	public int parseRegisterSubId(byte[] buffer, int cursor) {
		return (int) (buffer[cursor + mCmdIDCursor] & 0xFF);
	}

	@Override
	public int parsePacketLength(byte[] buffer, int cursor) {
		int packetLength = parseDataLength(buffer, cursor);

		// 1 是 Head code :0xFD
		packetLength += 1;

		return packetLength;
	}

	@Override
	public int getCmdIDCursor() {
		return mCmdIDCursor;
	}

	@Override
	public int getCmdSubIDCursor() {
		return mCmdIDCursor;
	}

	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_AIR_INFO1 & 0xFF;
	}

	public int GetAirInfo1CmdId() {
		return DATA_TYPE_AIR_INFO2 & 0xFF;
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	public int GetPanelKeyInfoCmdId() {
		return DATA_TYPE_PANEL_KEY_INFO & 0xFF;
	}

	public int GetWheelInfoCmdId() {
		return DATA_TYPE_WHEEL_INFO & 0xFF;
	}

	public int GetDspInfoCmdId() {
		return DATA_TYPE_DSP_INFO & 0xFF;
	}

	private byte[] mbyAirInfo = null;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3;

		// Data 0
		byte airData0 = (byte) packet[cursor];
		byte airData3 = (byte) packet[cursor + 3];
		byte airData6 = (byte) packet[cursor + 6];

		airInfo.mMinTemp = (float) 0;
		airInfo.mMaxTemp = (float) 0xFF;
		if ((airData0 & 0xFF) == 0 || (airData0 & 0xFF) == 0xFF) {
			
			airInfo.mbshowLTemp = true;
			airInfo.mLeftTemp = (float) (airData0 & 0xFF);
		} else if ((airData0 & 0xFF) != 0xFE) {
			airInfo.mbshowLTemp = true;
			
			if ((airData0 >= 1) && (airData0 <=7)) {
				airInfo.mbshowLTempLv = true;
				airInfo.mbyLeftTemplv = airData0;
			}else {
				airInfo.mbshowLTempLv = false;
				if ((airData3 & 0x0f) != 0) {
					airInfo.mLeftTemp = (float) ((airData0 & 0xFF) + 0.5);
				} else {
					airInfo.mLeftTemp = (float) (airData0 & 0xFF);
				}
			}

		}else {
			airInfo.mbshowLTemp = false;
		}

		if ((airData6 & 0xFF) == 0 || (airData6 & 0xFF) == 0xFF) {

			airInfo.mbshowRTemp = true;
			airInfo.mRightTemp  = (float) (airData6 & 0xFF);
		} else if ((airData6 & 0xFF) != 0xFE) {

			airInfo.mbshowRTemp = true;
			
			if ((airData6 >= 1) && (airData6 <=7)) {
				airInfo.mbshowRTempLv = true;
				airInfo.mRightTemp = airData6;
			}else {
				airInfo.mbshowRTempLv = false;
				if ((airData3 & 0xf0) != 0) {
					airInfo.mRightTemp = (float) ((airData6 & 0xFF) + 0.5);
				} else {
					airInfo.mRightTemp = (float) (airData6 & 0xFF);
				}
			}

		}else {
			airInfo.mbshowRTemp = false;
		}
		
		byte bydata5 = packet[cursor + 5];

		// 环境温度
		airInfo.mOutTempEnable = true;
		if (DataConvert.GetBit(bydata5, 7) == 1) {
			airInfo.mOutTemp = -((bydata5-0x80) & 0xFF);
		} else {
			airInfo.mOutTemp = (bydata5 & 0xFF);
		}

		// Ac
		byte airData1 = (byte) packet[cursor + 1];
		byte airData4 = (byte) packet[cursor + 4];
		byte airData2 = (byte) packet[cursor + 2];

		if (DataConvert.GetBit(airData4, 0) == 1) {
			airInfo.mAcState = (byte) DataConvert.GetBit(airData1, 0);
		} else {
			airInfo.mAcState = 0;
		}
		// ECO
		if (DataConvert.GetBit(airData4, 1) == 1) {
			airInfo.mEco = (byte) DataConvert.GetBit(airData1, 1);
		} else {
			airInfo.mEco = 0;
		}
		// AuTo
		if (DataConvert.GetBit(airData4, 2) == 1) {
			airInfo.mAutoLight1 = (byte) DataConvert.GetBit(airData1, 2);
		} else {
			airInfo.mAutoLight1 = 0;
		}
		// 正面吹风
		if (DataConvert.GetBit(airData4, 3) == 1) {
			airInfo.mParallelWind = (byte) DataConvert.GetBit(airData1, 3);
		} else {
			airInfo.mParallelWind = 0;
		}
		// 下吹风
		if (DataConvert.GetBit(airData4, 4) == 1) {
			airInfo.mDowmWind = (byte) DataConvert.GetBit(airData1, 4);
		} else {
			airInfo.mDowmWind = 0;
		}
		// 后窗除雾
		if (DataConvert.GetBit(airData4, 5) == 1) {
			airInfo.mRearLight = (byte) DataConvert.GetBit(airData1, 5);
		} else {
			airInfo.mRearLight = 0;
		}
		// 前窗除雾
		if (DataConvert.GetBit(airData4, 6) == 1) {
			airInfo.mFWDefogger = (byte) DataConvert.GetBit(airData1, 6);
		} else {
			airInfo.mFWDefogger = 0;
		}
		// 内外循环
		if (DataConvert.GetBit(airData4, 7) == 1) {
			airInfo.mCircleState = (byte) ((DataConvert.GetBit(airData1, 7) == 1) ? 0 : 1);
		} else {
			airInfo.mCircleState = 0;
		}
		// Dual
		if (DataConvert.GetBit(airData2, 5) == 1) // 功能开关打开才有状态，否则直接灭掉
		{
			airInfo.mDaulLight = (byte) DataConvert.GetBit(airData2, 4);
		} else {
			airInfo.mDaulLight = 0;
		}

		// 风速
		airInfo.mWindRate = (byte) (0x0F & airData2);
		if (airInfo.mWindRate == 0) {
			// 空调指示灯信息
			airInfo.mAiron = 0;
		} else {
			airInfo.mAiron = 1;
		}

		airInfo.mDisplay = 1;

		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!equals(mbyAirInfo,packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;
	
		return airInfo;
	}
	
	private boolean equals(byte[] bylastInfo, byte[] byNewInfo){
		
		int iIndex = 0;
		boolean bequals = true;
		
		if (byNewInfo[2] == 0x13) {
			
			for (byte b : byNewInfo) {
				if (iIndex == 8) {
				}else {
					
					if ((b != bylastInfo[iIndex]) && bequals) {
						bequals = false;
					}
				}
				iIndex++;
			}
		}else {
		
			for (byte b : byNewInfo) {
				if (iIndex == 2) {
				}else {
					
					if ((b != bylastInfo[iIndex]) && bequals) {
						bequals = false;
					}
				}
				iIndex++;
			}
		}
		
		return bequals;
	}

	public AirInfo parseAirInfo1(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3;

		// Data 0
		byte airData0 = (byte) packet[cursor];
		byte airData3 = (byte) packet[cursor + 3];
		byte airData2 = (byte) packet[cursor + 2];

		if (DataConvert.GetBit(airData2, 7) == 1) {
			airInfo.mbshowRTemp = false;
			airInfo.mbshowLTemp = false;
		} else {
			airInfo.mMinTemp = (float) 0;
			airInfo.mMaxTemp = (float) 0xFF;
			
			if ((airData0 & 0xFF) == 0 || (airData0 & 0xFF) == 0xFF) {
				airInfo.mbshowRTemp = true;
				airInfo.mbshowLTemp = true;
				airInfo.mLeftTemp = (float) (airData0 & 0xFF);
				airInfo.mRightTemp = (float) (airData0 & 0xFF);
			} else if ((airData0 & 0xFF) != 0xFE) {
				airInfo.mbshowRTemp = true;
				airInfo.mbshowLTemp = true;
				
				if ((airData0 >= 1) && (airData0 <=7)) {
					airInfo.mbshowLTempLv = true;
					airInfo.mbshowRTempLv = true;
					airInfo.mLeftTemp  = airData0;
					airInfo.mRightTemp = airData0;
				}else {
					airInfo.mbshowLTempLv = false;
					airInfo.mbshowRTempLv = false;
					if ((airData3 & 0x0f) != 0) {
						airInfo.mLeftTemp = (float) ((airData0 & 0xFF) + 0.5);
						airInfo.mRightTemp = (float) ((airData0 & 0xFF) + 0.5);
					} else {
						airInfo.mLeftTemp = (float) (airData0 & 0xFF);
						airInfo.mRightTemp = (float) (airData0 & 0xFF);
					}
				}

			} else {
				airInfo.mbshowRTemp = false;
				airInfo.mbshowLTemp = false;
			}
		}
		
		if (DataConvert.GetBit(airData2, 7) == 1) {
			airInfo.mOutTempEnable = true;
			airInfo.mOutTemp = (packet[cursor] & 0xFF);
		}else {
			airInfo.mOutTempEnable = false;
		}

		// Ac
		byte airData1 = (byte) packet[cursor + 1];
		byte airData4 = (byte) packet[cursor + 4];

		if (DataConvert.GetBit(airData4, 0) == 1) {
			airInfo.mAcState = (byte) DataConvert.GetBit(airData1, 0);
		} else {
			airInfo.mAcState = 0;
		}
		// ECO
		if (DataConvert.GetBit(airData4, 1) == 1) {
			airInfo.mEco = (byte) DataConvert.GetBit(airData1, 1);
		} else {
			airInfo.mEco = 0;
		}
		// AuTo
		if (DataConvert.GetBit(airData4, 2) == 1) {
			airInfo.mAutoLight1 = (byte) DataConvert.GetBit(airData1, 2);
		} else {
			airInfo.mAutoLight1 = 0;
		}
		// 正面吹风
		if (DataConvert.GetBit(airData4, 3) == 1) {
			airInfo.mParallelWind = (byte) DataConvert.GetBit(airData1, 3);
		} else {
			airInfo.mParallelWind = 0;
		}
		// 下吹风
		if (DataConvert.GetBit(airData4, 4) == 1) {
			airInfo.mDowmWind = (byte) DataConvert.GetBit(airData1, 4);
		} else {
			airInfo.mDowmWind = 0;
		}
		// 后窗除雾
		if (DataConvert.GetBit(airData4, 5) == 1) {
			airInfo.mRearLight = (byte) DataConvert.GetBit(airData1, 5);
		} else {
			airInfo.mRearLight = 0;
		}
		// 前窗除雾
		if (DataConvert.GetBit(airData4, 6) == 1) {
			airInfo.mFWDefogger = (byte) DataConvert.GetBit(airData1, 6);
		} else {
			airInfo.mFWDefogger = 0;
		}
		// 内外循环
		if (DataConvert.GetBit(airData4, 7) == 1) {
			airInfo.mCircleState = (byte) ((DataConvert.GetBit(airData1, 7) == 1) ? 0 : 1);
		} else {
			airInfo.mCircleState = 0;
		}

		// 风速
		airInfo.mWindRate = (byte) (0x0F & airData2);
		if (airInfo.mWindRate == 0) {
			// 空调指示灯信息
			airInfo.mAiron = 0;
		} else {
			airInfo.mAiron = 1;
		}

		airInfo.mDisplay = 1;

		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!equals(mbyAirInfo,packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		// TODO Auto-generated method stub
		int cursor = 3;

		radarInfo.mBackLeftCenterDis = packet[cursor + 1];
		radarInfo.mBackRightCenterDis = packet[cursor + 2];
		radarInfo.mBackRightDis = packet[cursor + 3];

		if (packet[cursor] == 1) // 距离
		{
			int ilDis = packet[cursor + 2] & 0xFF;
			if (ilDis == 0xFE) {
				radarInfo.mBackLeftDis = 0;
			} else {
				radarInfo.mBackLeftDis = (byte) (ilDis * 10 / 254);
				if (radarInfo.mBackLeftDis == 0) {
					radarInfo.mBackLeftDis = 1;
				}
			}

			int iLMDis = packet[cursor + 3] & 0xFF;
			if (iLMDis == 0xFE) {
				radarInfo.mBackLeftCenterDis = 0;
			} else {
				radarInfo.mBackLeftCenterDis = (byte) (iLMDis * 13 / 254);
				if (radarInfo.mBackLeftCenterDis == 0) {
					radarInfo.mBackLeftCenterDis = 1;
				}
			}

			int iRMDis = packet[cursor + 4] & 0xFF;
			if (iRMDis == 0xFE) {
				radarInfo.mBackRightCenterDis = 0;
			} else {
				radarInfo.mBackRightCenterDis = (byte) (iRMDis * 13 / 254);
				if (radarInfo.mBackRightCenterDis == 0) {
					radarInfo.mBackRightCenterDis = 1;
				}
			}

			int iRDis = packet[cursor + 5] & 0xFF;
			if (iRDis == 0xFE) {
				radarInfo.mBackRightDis = 0;
			} else {
				radarInfo.mBackRightDis = (byte) (iRDis * 10 / 254);
				if (radarInfo.mBackRightDis == 0) {
					radarInfo.mBackRightDis = 1;
				}
			}

		} else {

			int iCount = packet[cursor + 1] & 0xFF;

			if ((packet[cursor + 2] & 0xFF) == (iCount - 1)) {
				radarInfo.mBackLeftDis = 0;
			} else if (iCount != 0) {
				radarInfo.mBackLeftDis = (byte) (packet[cursor + 2] * 10 / iCount);
				if (radarInfo.mBackLeftDis == 0) {
					radarInfo.mBackLeftDis = 1;
				}
			}

			if ((packet[cursor + 3] & 0xFF) == (iCount - 1)) {
				radarInfo.mBackLeftCenterDis = 0;
			} else if (iCount != 0) {
				radarInfo.mBackLeftCenterDis = (byte) (packet[cursor + 3] * 13 / iCount);
				if (radarInfo.mBackLeftCenterDis == 0) {
					radarInfo.mBackLeftCenterDis = 1;
				}
			}

			if ((packet[cursor + 4] & 0xFF) == (iCount - 1)) {
				radarInfo.mBackRightCenterDis = 0;
			} else if (iCount != 0) {
				radarInfo.mBackRightCenterDis = (byte) (packet[cursor + 4] * 13 / iCount);
				if (radarInfo.mBackRightCenterDis == 0) {
					radarInfo.mBackRightCenterDis = 1;
				}
			}

			if ((packet[cursor + 5] & 0xFF) == (iCount - 1)) {
				radarInfo.mBackRightDis = 0;
			} else if (iCount != 0) {
				radarInfo.mBackRightDis = (byte) (packet[cursor + 5] * 10 / iCount);
				if (radarInfo.mBackRightDis == 0) {
					radarInfo.mBackRightDis = 1;
				}
			}
		}

		return radarInfo;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3;

		wheelInfo.mEps = DataConvert.byte2Short(packet, cursor);

		if ((wheelInfo.mEps <= 0x3470) && (wheelInfo.mEps > 0x1E40)) {
			// 左
			wheelInfo.mEps = -(wheelInfo.mEps * 45 / 5680);
		} else if (wheelInfo.mEps == 0x1E40) {
			// 中
			wheelInfo.mEps = 0;
		} else if ((wheelInfo.mEps >= 0x0810) && (wheelInfo.mEps < 0x1E40)) {
			// 右
			wheelInfo.mEps = (wheelInfo.mEps * 45 / 5680);
		}

		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslateKey(wheelInfo, false);
	}

	public WheelKeyInfo parsePanelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = (packet[cursor] == 0) ? 0 : 1;

		return TranslateKey(wheelInfo, true);
	}

	public PowerAmplifier parseDspInfo(byte[] packet, PowerAmplifier dspInfo) {
		return dspInfo;
	}

	public String mstrKeyCode = null;

	public WheelKeyInfo TranslateKey(WheelKeyInfo info, boolean bPanelkey) {
		// TODO Auto-generated method stub
		switch (info.mKeyCode) {
		case 0x11:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x12:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x13:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x14:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case 0x15:
			info.mstrKeyCode = DDef.Volume_del;
			break; 
		case 0x16:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x30:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case 0x31:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		default:
			break;
		}

		if (bPanelkey) {
			if (info.mKeyCode != 0) {
				mstrKeyCode = info.mstrKeyCode;
			}
			if (info.mKeyCode == 0) {
				info.mstrKeyCode = mstrKeyCode;
				mstrKeyCode = null;
			}
		}

		return info;
	}

	@Override
	public boolean IsSuportMeInfo() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		ArrayList<Message> aListMsg = new ArrayList<Message>();
		
		aListMsg.add(getMediaData(mediaInfo));

		return aListMsg;
	}
	
	private Message getMediaData(MediaInfo mediaInfo){
		
		byte   byCmdId = 0x07;
    	byte[] byData = null;
		int iSource = mediaInfo.getSource();
		Message msg = null;
		
		if (iSource == DDef.SOURCE_DEF.Src_radio) {
    		
    		byCmdId = 0x07;
			byData  = RadioInfo(mediaInfo);
			
			msg = CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
		}
    	
		return msg;
	}
	
	public byte[] RadioInfo(MediaInfo mediainfo){

        byte byBand = mediainfo.getBand();
        StringBuffer strFreq = new StringBuffer();
        int iMFreq = mediainfo.getMainFreq();
         
        if (byBand > 2) {
			//Am
        	if (byBand == 3) {
            	strFreq.append("AM1");
            	strFreq.append(iMFreq);
            	strFreq.append("KHZ");
			}else {
            	strFreq.append("AM2");
            	strFreq.append(iMFreq);
            	strFreq.append("KHZ");
			}

		}else {
			if (byBand == 0) {
            	strFreq.append("FM1");
            	strFreq.append(iMFreq/100);
            	strFreq.append("MHZ");
			}else if (byBand == 1) {
            	strFreq.append("FM2");
            	strFreq.append(iMFreq/100);
            	strFreq.append("KHZ");
			}else {
            	strFreq.append("FM3");
            	strFreq.append(iMFreq/100);
            	strFreq.append("MHZ");
			}
		}
        
        return strFreq.toString().getBytes();
    }
}
