package com.can.parser.raise.nissan;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import android.os.Message;
import com.can.assist.CanKey;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReNissanProtocol
 * 
 * @function:睿志城日产协议类
 * @author Kim
 * @Date:  2016-6-4 上午10:19:37
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReNissanProtocol extends ReProtocol{
	
	// Slave -> Host
    public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x20; // 方向盘按键信息
    public static final byte DATA_TYPE_BASE_INFO = (byte) 0x24; // 基本信息
    public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x30; // 版本信息
    public static final byte DATA_TYPE_MEDIA_SRC_INFO = (byte) 0x40; // 媒体源信息
    public static final byte DATA_TYPE_INCOMING_INFO = (byte) 0x50;

    // Host -> Slave
    public static final byte DATA_TYPE_START_END = (byte) 0x81; // 每次 ACC
                                                                // 上电重新建立连接
    public static final byte DATA_TYPE_SOURCE_REQUEST = (byte) 0xC0; // Source
    public static final byte DATA_TYPE_MEDIA_ONE = (byte) 0x70; // 媒体字符串信息第一行
    public static final byte DATA_TYPE_MEDIA_TWO = (byte) 0x71; // 媒体字符串信息第二行
    public static final byte DATA_TYPE_VOLUME_REQUEST = (byte) 0xC4; // 音量显示控制
    public static final byte DATA_TYPE_TELEPHONE_REQUEST = (byte) 0xC5; // 电话状态
    public static final byte DATA_TYPE_TIME_INFO = (byte) 0xC8; // 时间信息
    public static final byte DATA_TYPE_SETLANG = (byte) 0xC6; // 设置语言
    public static final byte DATA_TYPE_AVM = (byte) 0xC7; // AVM 全景式监控影像系统

    // 源定义
    public class Source {
        public static final byte OFF = (byte) 0x00; // 显示音响关
        public static final byte TUNER = (byte) 0x01; // Tuner
        public static final byte DISC = (byte) 0x02; // DISC
        public static final byte TV = (byte) 0x03; // TV
        public static final byte NAVI = (byte) 0x04; // NAVI
        public static final byte PHONE = (byte) 0x05; // Phone
        public static final byte IPOD = (byte) 0x06; // IPOD
        public static final byte AUX = (byte) 0x07; // Aux
        public static final byte USB = (byte) 0x08; // USB
        public static final byte SD = (byte) 0x09; // SD
        public static final byte DVB_T = (byte) 0x0A; // DVB-T
        public static final byte PHONE_A2DP = (byte) 0x0b; // Phone A2DP
        public static final byte OTHER = (byte) 0x0c; // 0x0c
        public static final byte CDC = (byte) 0x0D; // CDC
        public static final byte CD = (byte) 0x10; // CD
        public static final byte DVD = (byte) 0x11; // DVD
    }

    // 媒体类型
    public class MediaType {

        public static final byte OFF = (byte) 0x00; // 仅显示媒体源
        public static final byte TUNER = (byte) 0x01; // Tuner
        public static final byte SAM = (byte) 0x10; // Simple Audio Media
        public static final byte EAM = (byte) 0x11; // Enhanced Audio Media
        public static final byte IPOD = (byte) 0x12; // IPod
        public static final byte SAM2 = (byte) 0x13; // Simple Audio Media 2
        public static final byte FBV = (byte) 0x20; // File Based Video
        public static final byte DVDV = (byte) 0x21; // DVD Video
        public static final byte OV = (byte) 0x22; // Other Video
        public static final byte AUX = (byte) 0x30; // Aux other
        public static final byte PHONE = (byte) 0x40; // Phone
        public static final byte CUSTOM = (byte) 0xFF; // 显示用户自定义字符(需车型支持)
    }
    
    // 媒体源信息 (can发过来的)
    public class MediaSource{
    	public static final byte None = 0x00;    	// 无媒体
    	public static final byte TUNERAM = 0x01; 	// AM
    	public static final byte TUNERFM1 = 0x02;	// Fm1
    	public static final byte TUNERFM2 = 0x03;	// Fm2
    	public static final byte CD       = 0x04;	// CD
    	public static final byte USB1     = 0x05;	// Usb1
    	public static final byte USB2     = 0x06;	// Usb2
    	public static final byte BTDSP    = 0x07;	// 蓝牙音响
    	public static final byte AUX	  = 0x08;	// Aux
    }
    
    // 来电处理
    public class INCOMING{
    	public static final byte DIAL = 0x01;
    	public static final byte HUNG = 0x02;
    }

    // 收音机波段
    public class TunerBound {
        public static final byte TUNER_FM = (byte) 0x00;
        public static final byte TUNER_FM1 = (byte) 0x01;
        public static final byte TUNER_FM2 = (byte) 0x02;
        public static final byte TUNER_FM3 = (byte) 0x03;
        public static final byte TUNER_AM = (byte) 0x10;
        public static final byte TUNER_AM1 = (byte) 0x11;
        public static final byte TUNER_AM2 = (byte) 0x12;
        public static final byte TUNER_AM3 = (byte) 0x13;
    }

    // 电话状态
    public class PhoneState {
        public static final byte POHNE_NOT_ACTIVE = (byte) 0x00;
        public static final byte PHONE_RINGING = (byte) 0x01; // (Incoming CALL)
        public static final byte PHONE_CONNECTED = (byte) 0x02;
    }

    // 按键类型
    public class KeyCode {
        public static final byte KEY_CODE_VOL_UP = (byte) 0x01;
        public static final byte KEY_CODE_VOL_DOWN = (byte) 0x02;
        public static final byte KEY_CODE_MENU_UP = (byte) 0x03;
        public static final byte KEY_CODE_MENU_DOWN = (byte) 0x04;
        public static final byte KEY_CODE_SRC = (byte) 0x07;
        public static final byte KEY_CODE_PICKUP = (byte) 0x09; // 接电话
        public static final byte KEY_CODE_HANGUP = (byte) 0x0A; // 挂电话
        public static final byte KEY_CODE_RETURN = (byte) 0x15;
        public static final byte KEY_CODE_ENTER = (byte) 0x16;
        public static final byte KEY_CODE_ON_OFF = (byte) 0x87; // 天籁公爵后座控制按键，原车功能为控制主机POWER
        public static final byte KEY_CODE_ON_NOON = (byte) 0x00; // 无按键（弹起）
    }

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_BASE_INFO & 0xFF);
	}

	@Override
	public int GetVersionInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_VERSION_INFO & 0xFF);
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
        return (int) (DATA_TYPE_WHEEL_KEY_INFO & 0xFF);
	}

	@Override
	public int GetMediaSourceCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_MEDIA_SRC_INFO & 0xFF);
	}

	@Override
	public int GetPhoneActionCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_INCOMING_INFO & 0xFF);
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
        int cursor = 3; // 跳过 2E、Data Type、Length字段

        // Data0
        byte baseData0 = (byte) packet[cursor];
        baseInfo.mBackCarState = (byte) ((baseData0 >> 0) & 0x1);
        return baseInfo;
	}

	@Override
	public VerInfo parseVersionInfo(byte[] packet) {
		// TODO Auto-generated method stub
        int cursor = 3; // 跳过 2E、Data Type、Length字段

        VerInfo version = new VerInfo();
        byte[] ver = new byte[8];
        System.arraycopy(packet, cursor, ver, 0, 8);
        version.mVersion = DataConvert.BytesToStr(ver);
        return version;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        wheelInfo.mKeyCode = packet[cursor];
        wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
        
        if (wheelInfo.mKeyCode == KeyCode.KEY_CODE_SRC &&
        		wheelInfo.mKeyStatus == 0x02) {
        	
			wheelInfo.mKeyStatus = CanKey.KEY_SERIES; 
		}else if (wheelInfo.mKeyCode == KeyCode.KEY_CODE_ENTER &&
				wheelInfo.mKeyStatus == 0x02) {
			
			wheelInfo.mKeyStatus = CanKey.KEY_SERIES;
		}
        
        return TranslateKey(wheelInfo);
	}

	public WheelKeyInfo parseMediaSource(byte[] packet, WheelKeyInfo wheelKeyInfo){
		int icursor = 3;
		
		wheelKeyInfo.mKeyCode = packet[icursor];
		wheelKeyInfo.mKeyStatus = 0x01;
		return TranslateSource(wheelKeyInfo);
	}
	
	public WheelKeyInfo parsePhoneState(byte[] packet, WheelKeyInfo wheelKeyInfo){
		int icursor = 3;
		
		wheelKeyInfo.mKeyCode = packet[icursor];
		wheelKeyInfo.mKeyStatus = 0x01;
		return TranslatePhoneState(wheelKeyInfo);
	}
	
	private WheelKeyInfo TranslateSource(WheelKeyInfo info){

		switch (info.mKeyCode) {
		case MediaSource.TUNERAM:
			info.mstrKeyCode = DDef.Radio_am;
			break;
		case MediaSource.TUNERFM1:
		case MediaSource.TUNERFM2:
			info.mstrKeyCode = DDef.Radio_fm;			
			break;
		case MediaSource.CD:
			info.mstrKeyCode = DDef.Src_dvd;
			break;
		case MediaSource.USB1:
		case MediaSource.USB2:
			info.mstrKeyCode = DDef.Src_usb;
			break;
		case MediaSource.BTDSP:
			info.mstrKeyCode = DDef.Src_bt;
			break;
		case MediaSource.AUX:
			info.mstrKeyCode = DDef.Src_aux;
			break;
		default:
			break;
		}
		
		return info;
	}
	
	private WheelKeyInfo TranslatePhoneState(WheelKeyInfo info){

		switch (info.mKeyCode) {
		case INCOMING.DIAL:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case INCOMING.HUNG:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		default:
			break;
		}
		
		return info;
	}
	
	private String mstrKeyCode = null;
	public WheelKeyInfo TranslateKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub
		switch (info.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_UP:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_VOL_DOWN:
			info.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_MENU_UP:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_MENU_DOWN:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_SRC:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_PICKUP:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_HANGUP:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_RETURN:
			info.mstrKeyCode = DDef.Back;
			break;
		case KeyCode.KEY_CODE_ENTER:
			info.mstrKeyCode = DDef.Enter;
			break;
		case KeyCode.KEY_CODE_ON_OFF:
			info.mstrKeyCode = DDef.Power;
			break;
		default:
			break;
		}
		
		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrKeyCode;
			mstrKeyCode = null;
		}else {
			mstrKeyCode = info.mstrKeyCode;
		}
		
		return info;
	}

	@Override
	public byte TranslateSource(int iSource) {
		// TODO Auto-generated method stub
		byte bySource = Source.OFF;
		
		switch (iSource) {
		case DDef.SOURCE_DEF.Src_radio:
			bySource = Source.TUNER;
			break;
		case DDef.SOURCE_DEF.Src_music:
			bySource = Source.USB;
			break;
		case DDef.SOURCE_DEF.Src_video:
			bySource = Source.USB;
			break;
		case DDef.SOURCE_DEF.Src_bluetooth:
			bySource = Source.PHONE;
			break;
		case DDef.SOURCE_DEF.Src_dvd:
			bySource = Source.DISC;
			break;
		case DDef.SOURCE_DEF.Src_dvr:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_dtv:
			bySource = Source.TV;
			break;
		case DDef.SOURCE_DEF.Src_atv:
			bySource = Source.TV;	
			break;
		case DDef.SOURCE_DEF.Src_avin:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_ipod:
			bySource = Source.IPOD;
			break;
		case DDef.SOURCE_DEF.Src_phonelink:
			bySource = Source.OTHER;
			break;
		case DDef.SOURCE_DEF.Src_backcar:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_bt_phone:
			bySource = Source.PHONE;
			break;
		case DDef.SOURCE_DEF.Src_photo:
			bySource = Source.USB;
			break;
		default:
			bySource = Source.OTHER;
			break;
		}
		
		return bySource;
	}
	
	private byte getMediaType(byte bySource){
		
		byte byMeType = MediaType.OFF;
		
		switch (bySource) {
		case Source.TUNER:
			byMeType = MediaType.TUNER;
			break;
		case Source.AUX:
			byMeType = MediaType.AUX;
			break;
		case Source.DISC:
			byMeType = MediaType.EAM;
			break;
		case Source.IPOD:
			byMeType = MediaType.IPOD;
			break;
		case Source.OTHER:
			byMeType = MediaType.AUX;
			break;
		case Source.PHONE:
			byMeType = MediaType.PHONE;
			break;
		case Source.PHONE_A2DP:
			byMeType = MediaType.EAM;
			break;
		case Source.TV:
			byMeType = MediaType.AUX;
			break;
		case Source.USB:
			byMeType = MediaType.EAM;
			break;
		default:
			byMeType = MediaType.AUX;
			break;
		}
		
		return byMeType;
	}
	
	public byte TranslateBand(byte byBand){
		byte bybandEx = TunerBound.TUNER_FM;
		
		switch (byBand) {
		case 0x00:
			bybandEx = TunerBound.TUNER_FM1;
			break;
		case 0x01:
			bybandEx = TunerBound.TUNER_FM2;
			break;
		case 0x02:
			bybandEx = TunerBound.TUNER_FM3;
			break;
		case 0x03:
			bybandEx = TunerBound.TUNER_AM1;
			break;
		case 0x04:
			bybandEx = TunerBound.TUNER_AM2;
			break;
		default:
			break;
		}
		
		return bybandEx;
	}
	
	@Override
	public Message getBtInfo(int iType, int iValue, MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		
		byte[] bydata = null;
		
		if (iType == 0x01) {
			switch (mediaInfo.getPhonestate()) {
			case DDef.PHONE_STATE_INCOMING:
			case DDef.PHONE_STATE_OUTGOING:
			case DDef.PHONE_STATE_SPEAKING:
				String strPhoneNum = mediaInfo.getPhoneNumber();
				if (strPhoneNum != null) {
					if (strPhoneNum.isEmpty()) {
						bydata = new byte[8];
						bydata[0] = 0x01;
						bydata[1] = 0x01;
					}else {
						bydata = new byte[strPhoneNum.length()+2];
						
						bydata[0] = 0x01;
						bydata[1] = 0x01;
						
		                for (int i = 2; i < strPhoneNum.length()+2; i++) {
		                    bydata[i] = (byte) strPhoneNum.charAt(i-2);
		                }
					}
				}
				break;
			case DDef.PHONE_NONE:
			case DDef.PHONE_STATE_IDLE:
				bydata = new byte[8];
				bydata[0] = 0x00;
				bydata[1] = 0x01;
				break;
			default:
				break;
			}
		}
		
		return CanTxRxStub.getTxMessage(DATA_TYPE_TELEPHONE_REQUEST, DATA_TYPE_TELEPHONE_REQUEST, bydata, this);
	}

	public byte[] getPhoneInfo(MediaInfo mediaInfo){
		byte[] bydata = null;
		
		switch (mediaInfo.getPhonestate()) {
		case DDef.PHONE_STATE_INCOMING:
		case DDef.PHONE_STATE_OUTGOING:
		case DDef.PHONE_STATE_SPEAKING:
			String strPhoneNum = mediaInfo.getPhoneNumber();
			if (strPhoneNum != null) {
				if (strPhoneNum.isEmpty()) {
					bydata = new byte[8];
					bydata[0] = 0x01;
					bydata[1] = 0x01;
				}else {
					bydata = new byte[strPhoneNum.length()+2];
					
					bydata[0] = 0x01;
					bydata[1] = 0x01;
					
	                for (int i = 2; i < strPhoneNum.length()+2; i++) {
	                    bydata[i] = (byte) strPhoneNum.charAt(i-2);
	                }
				}
			}
			break;
		case DDef.PHONE_NONE:
		case DDef.PHONE_STATE_IDLE:
			bydata = new byte[8];
			bydata[0] = 0x00;
			bydata[1] = 0x01;
			break;
		default:
			bydata = new byte[8];
			break;
		}
		
		return bydata;
	}
	
	private Message getID3Info(MediaInfo mediaInfo, int type) {
		
		byte byCmdId = 0;
		String strVal = "";
		byte[] byData = null;

		switch (type) {
		case DDef.ID3_TITLE:
			byCmdId = DATA_TYPE_MEDIA_ONE;
			strVal = mediaInfo.getID3Title();
			break;
		case DDef.ID3_AUTHOR:
			byCmdId = DATA_TYPE_MEDIA_TWO;
			strVal = mediaInfo.getID3Author();
			break;
		}
		
		if (strVal != null) {
			
			if (strVal.isEmpty()) {
				byData = new byte[1];
				byData[0] = 0x01;
			}else {
				
				byte[] datas = null;
				
				try {
					datas = strVal.getBytes("unicode");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (datas != null && datas.length > 0) {
					byData = new byte[datas.length+1];
					byData[0] = 0x11;
					System.arraycopy(datas, 2, byData, 1, datas.length - 2);
				} 
			}
		} else {
			byData = new byte[1];
			byData[0] = 0x01;
		}
		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}
	
	public byte[] MediaPlayInfo(MediaInfo mediaInfo){
    	
    	byte[] bydata = null;
    	byte bySource = TranslateSource(mediaInfo.getSource());
    	byte byMediaType = getMediaType(bySource);
    	
    	switch (byMediaType) {
		case MediaType.SAM:
			break;
		case MediaType.EAM:
			bydata = new byte[8];
			bydata[0] = bySource;
			bydata[1] = byMediaType;
            byte[] byCurTrack = DataConvert.Hi2Lo2Byte(mediaInfo.getCurTrack());
            bydata[2] = byCurTrack[0];
            bydata[3] = byCurTrack[1];
            byte[] iTotalTrack = DataConvert.Hi2Lo2Byte(mediaInfo.getTotalTrack());
            bydata[4] = iTotalTrack[0];
            bydata[5] = iTotalTrack[1];
            int icurTime = mediaInfo.getPlayTime();
            bydata[6] = (byte) ((icurTime/60)%60);
            bydata[7] = (byte) (icurTime%60);
			break;
		case MediaType.TUNER:
	        bydata = new byte[8];
	        bydata[0] = bySource;
	        bydata[1] = byMediaType;
	        
	        bydata[2] = TranslateBand(mediaInfo.getBand());             
	        int iFreq = mediaInfo.getMainFreq();
	        byte[] itmpFreq = DataConvert.Hi2Lo2Byte(iFreq);
	        bydata[3] = itmpFreq[0];                                
	        bydata[4] = itmpFreq[1];                                
	        bydata[5] = mediaInfo.getFreqIndex();
	        bydata[6] = 0x00;
	        bydata[7] = 0x00;
			break;
		case MediaType.IPOD:
			break;
		case MediaType.FBV:
			break;
		case MediaType.DVDV:
			break;
		case MediaType.OV:
			break;
		case MediaType.AUX:
			bydata = new byte[8];
			bydata[0] = bySource;
			bydata[1] = byMediaType;
			break;
		case MediaType.PHONE:
			bydata = getPhoneInfo(mediaInfo);
			break;
		default:
			bydata = new byte[8];
			break;
		}
    	
    	return bydata;
    }
	
	private Message getMediaData(MediaInfo mediaInfo){
		
		byte   byCmdId;
    	byte[] byData = null;
		int iSource = mediaInfo.getSource();
		
		if (iSource == DDef.SOURCE_DEF.Src_bt_phone ||
				iSource == DDef.SOURCE_DEF.Src_bluetooth) {
			
			byCmdId = DATA_TYPE_TELEPHONE_REQUEST;

		}else {
			
			byCmdId = DATA_TYPE_SOURCE_REQUEST;
		}
		
		byData  = MediaPlayInfo(mediaInfo);
    	
		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		ArrayList<Message> aListMsg = new ArrayList<Message>();
		
		aListMsg.add(getMediaData(mediaInfo));
		aListMsg.add(getID3Info(mediaInfo, DDef.ID3_TITLE));
		aListMsg.add(getID3Info(mediaInfo, DDef.ID3_AUTHOR));	
		aListMsg.add(CanTxRxStub.getTxMessage(DATA_TYPE_TIME_INFO, DATA_TYPE_TIME_INFO, getTime(mediaInfo), this));
	
		return aListMsg;
	}
	
	@Override
	public Message getVolInfo(int iVol) {
		// TODO Auto-generated method stub
		return CanTxRxStub.getTxMessage(DATA_TYPE_VOLUME_REQUEST, DATA_TYPE_VOLUME_REQUEST, getVol(iVol), this);
	}

	@Override
	public boolean IsSuportMeInfo() {
		// TODO Auto-generated method stub
		return true;
	}

	private byte[] getTime(MediaInfo mediaInfo){
		byte[] byData = new byte[3];
		
		TimeInfo timeInfo = mediaInfo.getTimeInfo();
		
		byData[0] = timeInfo.byMinute;
		byData[1] = timeInfo.byHour;
		byData[2] = timeInfo.by24Mode;
		
		return byData;
	}

	private byte[] getVol(int iVol){
		byte[] byData = {0};
		
		byte byVol = (byte)iVol;
		
		byData[0] = (byte) ((byVol > 0) ? (byData[0] & 0x00) : (byData[0] | 0x80));
		byData[0] = (byte) (byData[0] | ((0x7F & byData[0]) | byVol));
		
		return byData;
	}

	@Override
	public Message getCanKeyAttr(String strKeyCode) {
		// TODO Auto-generated method stub
		byte[] byData;
		Message msg = null;
		
		if (strKeyCode.equals(DDef.Src_mode)) {
			byData = new byte[1];
			byData[0] = 0x01;
			msg = CanTxRxStub.getTxMessage(DATA_TYPE_AVM, DATA_TYPE_AVM, byData, this);
		}else if (strKeyCode.equals(DDef.Enter)) {
			byData = new byte[2];
			byData[0] = 0x02;
			byData[1] = 0x02;
			msg = CanTxRxStub.getTxMessage(DATA_TYPE_SETLANG, DATA_TYPE_SETLANG, byData, this);			
		}
		
		return msg;
	}
}
