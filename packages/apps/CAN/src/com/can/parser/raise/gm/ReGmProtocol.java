package com.can.parser.raise.gm;

import com.can.assist.CanKey;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CanAudio;
import com.can.parser.DDef.CarGmType;
import com.can.parser.DDef.GmCarSet;
import com.can.parser.DDef.GmOnStar;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.PhoneState;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;
import com.can.ui.CanPopWind;
import com.yecon.metazone.YeconMetazone;

public class ReGmProtocol extends ReProtocol{
	private int mCarType = 0;

    // Slave -> Host
    public static final byte DATA_TYPE_WHEEL_KEY_INFO = 0x01;
    public static final byte DATA_TYPE_PANEL_INFO = 0x02;
    public static final byte DATA_TYPE_AIR_INFO = 0x03;
    public static final byte DATA_TYPE_BACK_LIGHT_INFO = 0x04;
    public static final byte DATA_TYPE_AIR_SET_INFO = 0x05;
    public static final byte DATA_TYPE_CAR_SET_INFO = 0x06;
    public static final byte DATA_TYPE_RADAR_ON_OFF = 0x07;
    public static final byte DATA_TYPE_ON_STAR_PHONE_INFO = 0x08;
    public static final byte DATA_TYPE_ON_STAR_STATE_INFO = 0x09;
    public static final byte DATA_TYPE_CAR_SET2_INFO = 0x0A;
    public static final byte DATA_TYPE_CAR_SPEED_INFO = 0x0B;
    public static final byte DATA_TYPE_LAN_INFO = 0x0C;
    public static final byte DATA_TYPE_WARNING_VOL_INFO = 0x0D;
    public static final byte DATA_TYPE_REAR_RADAR_INFO = 0x22;
    public static final byte DATA_TYPE_FRONT_RADAR_INFO = 0x23;
    public static final byte DATA_TYPE_BASE_INFO = 0x24;
    public static final byte DATA_TYPE_WHEEL_INFO = 0x26;
    public static final byte DATA_TYPE_VERSION_INFO = 0x30;
    
    // Host -> Slave
    public static final byte DATA_TYPE_AIR_SET_REQUEST = (byte) 0x82;
    public static final byte DATA_TYPE_CAR_SET_REQUEST = (byte) 0x83;
    public static final byte DATA_TYPE_RADIO_ON_OFF_REQUEST = (byte) 0x84;
    public static final byte DATA_TYPE_ON_START_STATE_REQUEST = (byte) 0x85;
    public static final byte DATA_TYPE_ON_START_PHONE_REUQEST = (byte) 0x86;
    public static final byte DATA_TYPE_LAN_REQUEST = (byte) 0x87;
    public static final byte DATA_TYPE_WARNING_VOL_REQUEST = (byte) 0x88;
    public static final byte DATA_TYPE_MEDIA_REQUEST = (byte) 0x90;

    
    public static final byte ON_STAR_SYS_CLOSE = 0x00;
    public static final byte ON_STAR_SYS_OPEN = 0x01;
    public static final byte ON_STAR_INCOMING = 0x02;
    public static final byte ON_STAR_OUTGOING = 0x03;
    public static final byte ON_STAR_SPEAKING = 0x04;

    // 按键类型
    public class KeyCode {
        public static final byte KEY_CODE_NO_KEY = 0x00; 	// 无按键
        public static final byte KEY_CODE_VOL_UP = 0x01; 	// vol+
        public static final byte KEY_CODE_VOL_DOWN = 0x02;	// vol-
        public static final byte KEY_CODE_MENU_UP = 0x03;	
        public static final byte KEY_CODE_MENU_DOWN = 0x04; 
        public static final byte KEY_CODE_SRC = 0x05; 		// 
        public static final byte KEY_CODE_SPEECH = 0x06; 	// SPEECH
        public static final byte KEY_CODE_MUTE = 0x07;
    }
    
    public class GmPanelCode {
    	public static final byte KEY_CODE_POWER 	= 0x01;
    	public static final byte KEY_CODE_PREV 		= 0x02;
    	public static final byte KEY_CODE_NEXT 		= 0x03;
    	public static final byte KEY_CODE_COFING 	= 0x04;
    	public static final byte KEY_CODE_TONE		= 0x05;
    	public static final byte KEY_CODE_BACK 		= 0x06;
    	public static final byte KEY_CODE_RADIO 	= 0x07;
    	public static final byte KEY_CODE_DISC 		= 0x08;
    	public static final byte KEY_CODE_MUTE		= 0x09;
    	public static final byte KEY_CODE_NUM1 		= 0x0A;
    	public static final byte KEY_CODE_NUM2 		= 0x0B;
    	public static final byte KEY_CODE_NUM3 		= 0x0C;
    	public static final byte KEY_CODE_NUM4 		= 0x0D;
    	public static final byte KEY_CODE_NUM5 		= 0x0E;
    	public static final byte KEY_CODE_NUM6 		= 0x0F;
    	public static final byte KEY_CODE_LOAD 		= 0x10;
    	public static final byte KEY_CODE_EJECT 	= 0x11;
    	public static final byte KEY_CODE_INFO 		= 0x12;
    	public static final byte KEY_CODE_TIME 		= 0x13;
    	public static final byte KEY_CODE_FAV123	= 0x14;
    	public static final byte KEY_CODE_AS		= 0x15;
    	public static final byte KEY_CODE_ENTER		= 0x16;
    	public static final byte KEY_CODE_VOL_UP_X  = 0x17;
    	public static final byte KEY_CODE_VOL_DN_X  = 0x18;
    	public static final byte KEY_CODE_SEL_UP_X  = 0x19;
    	public static final byte KEY_CODE_SEL_DN_X  = 0x1A;
    	public static final byte KEY_CODE_PLAY  	= 0x1B;
    	public static final byte KEY_CODE_UP 		= 0x1C;
    	public static final byte KEY_CODE_DOWN  	= 0x1D;
    	public static final byte KEY_CODE_TUNE_ADD  = 0x34;
    	public static final byte KEY_CODE_TUNE_DEL  = 0x35;
    	public static final byte KEY_CODE_AUX  		= 0x40;
    	public static final byte KEY_CODE_HOME  	= 0x50;
    	public static final byte KEY_CODE_SRC  		= 0x51;
    	public static final byte KEY_CODE_MENU		= 0x53;
    	public static final byte KEY_CODE_MEDIA		= 0x54;
    }
    
    public class EncorePanelCode {
    	public static final byte KEY_CODE_NUM2 		= 0x01;
    	public static final byte KEY_CODE_MUTE 		= 0x02;
    	public static final byte KEY_CODE_BACK 		= 0x03;
    	public static final byte KEY_CODE_FAV123 	= 0x04;
    	public static final byte KEY_CODE_RADIO		= 0x05;
    	public static final byte KEY_CODE_PREV		= 0x06;
    	public static final byte KEY_CODE_POWER	 	= 0x07;
    	public static final byte KEY_CODE_ENTER		= 0x08;
    	public static final byte KEY_CODE_INFO		= 0x09;
    	public static final byte KEY_CODE_NUM3 		= 0x0A;
    	public static final byte KEY_CODE_NUM4 		= 0x0B;
    	public static final byte KEY_CODE_NUM5 		= 0x0C;
    	public static final byte KEY_CODE_NUM6 		= 0x0D;

    	public static final byte KEY_CODE_AS12 		= 0x10;
    	public static final byte KEY_CODE_EJECT 	= 0x11;
    	public static final byte KEY_CODE_CONFIG	= 0x12;
    	public static final byte KEY_CODE_TIME 		= 0x13;
    	public static final byte KEY_CODE_TONE		= 0x14;
    	public static final byte KEY_CODE_NUM1		= 0x15;
    	public static final byte KEY_CODE_NEXT		= 0x16;
    	public static final byte KEY_CODE_VOL_UP  	= 0x17;
    	public static final byte KEY_CODE_VOL_DN 	= 0x18;
    	public static final byte KEY_CODE_SEL_UP	= 0x19;
    	public static final byte KEY_CODE_SEL_DN	= 0x1A;
    	public static final byte KEY_CODE_PLAY  	= 0x1B;
    	public static final byte KEY_CODE_DISC		= 0x36;
    	public static final byte KEY_CODE_AUX  		= 0x40;
    }

    public class GL8PanelCode {
    	public static final byte KEY_CODE_PREV 		= 0x01;
    	public static final byte KEY_CODE_NUM5 		= 0x02;		//收音台号5
    	public static final byte KEY_CODE_NUM4 		= 0x03;		//收音台号4
    	public static final byte KEY_CODE_NUM3	 	= 0x04;		//收音台号3
    	public static final byte KEY_CODE_POWER		= 0x05;
    	public static final byte KEY_CODE_INFO		= 0x06;
    	public static final byte KEY_CODE_FAV	 	= 0x07;
    	public static final byte KEY_CODE_NUM1		= 0x08;		//收音台号1
    	public static final byte KEY_CODE_NUM2		= 0x09;		//收音台号2
    	public static final byte KEY_CODE_NEXT 		= 0x0A;
    	public static final byte KEY_CODE_ENTER		= 0x0B;
    	public static final byte KEY_CODE_BACK 		= 0x0C;
    	public static final byte KEY_CODE_TONE 		= 0x0D;
    	public static final byte KEY_CODE_EJECT		= 0x0E;
    	
    	public static final byte KEY_CODE_CONFIG 	= 0x11;
    	public static final byte KEY_CODE_MEDIA		= 0x12;
    	public static final byte KEY_CODE_PLAY 		= 0x13;
    	public static final byte KEY_CODE_BAND		= 0x14;
    	public static final byte KEY_CODE_MUTE		= 0x15;
    	public static final byte KEY_CODE_NUM6		= 0x16;		//收音台号6
    	public static final byte KEY_CODE_VOL_UP  	= 0x17;
    	public static final byte KEY_CODE_VOL_DN 	= 0x18;
    	public static final byte KEY_CODE_SEL_UP	= 0x19;
    	public static final byte KEY_CODE_SEL_DN	= 0x1A;
    	public static final byte KEY_CODE_TUNE_ADD  = 0x34;
    	public static final byte KEY_CODE_TUNE_DEL  = 0x35;
    }
    // 按键状态
    public class KeyStatus {
    	public static final byte KEY_STATUS_UP 			= 0x00;	 // 抬起
        public static final byte KEY_STATUS_DOWN        = 0x01;  // 按下
        public static final byte KEY_STATUS_LONGPRESS   = 0x02;  // 长按
    } 

	@Override
	public byte[] connect() {
		mCarType = YeconMetazone.GetCarType();
		return super.connect();
	}
	
	@Override
	public int GetBaseInfoCmdId() {
		return (int) (DATA_TYPE_BASE_INFO & 0xFF);
	}

	@Override
	public int GetVersionInfoCmdId() {
		return (int) (DATA_TYPE_VERSION_INFO & 0xFF);
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		return (int) (DATA_TYPE_WHEEL_KEY_INFO & 0xFF);
	}

	@Override
	public int GetBackLightInfoCmdId() {
		return (int) (DATA_TYPE_BACK_LIGHT_INFO & 0xFF);
	}

    private String mstrKeyCode = null;
    private String mstrPanelCode = null;
	private WheelKeyInfo TranslateKey(WheelKeyInfo wheelKeyInfo) {

		int iPhoneState = CanPopWind.mObjGdSharedData.getMediaInfo().getPhonestate();
		switch (wheelKeyInfo.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_VOL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_SPEECH:
			if (iPhoneState == PhoneState.INCOMING) {
				wheelKeyInfo.mstrKeyCode = DDef.Phone_dial;
			} else {
				wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			}
			break;
		case KeyCode.KEY_CODE_MENU_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_MENU_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_SRC:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_MUTE:
			if (iPhoneState == PhoneState.INCOMING || 
				iPhoneState == PhoneState.OUTGOING || 
				iPhoneState == PhoneState.SPEAKING) {
				wheelKeyInfo.mstrKeyCode = DDef.Phone_hang;
			} else {
				wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			}
			break;
		default:
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

	private WheelKeyInfo translateGmPanel(WheelKeyInfo wheelKeyInfo) {

		switch (wheelKeyInfo.mKeyCode) {
		case GmPanelCode.KEY_CODE_POWER:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case GmPanelCode.KEY_CODE_PREV:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case GmPanelCode.KEY_CODE_NEXT:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case GmPanelCode.KEY_CODE_COFING:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case GmPanelCode.KEY_CODE_TONE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case GmPanelCode.KEY_CODE_BACK:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case GmPanelCode.KEY_CODE_RADIO:
			wheelKeyInfo.mstrKeyCode = DDef.Src_radio;
			break;
		case GmPanelCode.KEY_CODE_DISC:
			wheelKeyInfo.mstrKeyCode = DDef.Src_dvd;
			break;
		case GmPanelCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case GmPanelCode.KEY_CODE_NUM1:
			wheelKeyInfo.mstrKeyCode = DDef.Num1;
			break;
		case GmPanelCode.KEY_CODE_NUM2:
			wheelKeyInfo.mstrKeyCode = DDef.Num2;
			break;
		case GmPanelCode.KEY_CODE_NUM3:
			wheelKeyInfo.mstrKeyCode = DDef.Num3;
			break;
		case GmPanelCode.KEY_CODE_NUM4:
			wheelKeyInfo.mstrKeyCode = DDef.Num4;
			break;
		case GmPanelCode.KEY_CODE_NUM5:
			wheelKeyInfo.mstrKeyCode = DDef.Num5;
			break;
		case GmPanelCode.KEY_CODE_NUM6:
			wheelKeyInfo.mstrKeyCode = DDef.Num6;
			break;
		case GmPanelCode.KEY_CODE_LOAD:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case GmPanelCode.KEY_CODE_EJECT:
			wheelKeyInfo.mstrKeyCode = DDef.Eject;
			break;
		case GmPanelCode.KEY_CODE_INFO:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case GmPanelCode.KEY_CODE_TIME:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case GmPanelCode.KEY_CODE_FAV123:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case GmPanelCode.KEY_CODE_AS:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_as;
			break;
		case GmPanelCode.KEY_CODE_ENTER:
			wheelKeyInfo.mstrKeyCode = DDef.Enter;
			break;
		case GmPanelCode.KEY_CODE_VOL_UP_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case GmPanelCode.KEY_CODE_VOL_DN_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case GmPanelCode.KEY_CODE_SEL_UP_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case GmPanelCode.KEY_CODE_SEL_DN_X:
			wheelKeyInfo.mKnobSteps = wheelKeyInfo.mKeyStatus;
			wheelKeyInfo.mKeyStatus = CanKey.KEY_KNOB;
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case GmPanelCode.KEY_CODE_PLAY:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pause;
			break;
		case GmPanelCode.KEY_CODE_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case GmPanelCode.KEY_CODE_DOWN:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case GmPanelCode.KEY_CODE_TUNE_ADD:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case GmPanelCode.KEY_CODE_TUNE_DEL:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case GmPanelCode.KEY_CODE_AUX:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case GmPanelCode.KEY_CODE_HOME:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case GmPanelCode.KEY_CODE_SRC:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case GmPanelCode.KEY_CODE_MENU:
			wheelKeyInfo.mstrKeyCode = DDef.Src_home;
			break;
		case GmPanelCode.KEY_CODE_MEDIA:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		default:
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

	private WheelKeyInfo translateEncorePanel(WheelKeyInfo wheelKeyInfo) {

		switch (wheelKeyInfo.mKeyCode) {
		case EncorePanelCode.KEY_CODE_NUM2:
			break;
		case EncorePanelCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case EncorePanelCode.KEY_CODE_BACK:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case EncorePanelCode.KEY_CODE_FAV123:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;		
			break;
		case EncorePanelCode.KEY_CODE_RADIO:
			wheelKeyInfo.mstrKeyCode = DDef.Src_radio;
			break;
		case EncorePanelCode.KEY_CODE_PREV:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case EncorePanelCode.KEY_CODE_POWER:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case EncorePanelCode.KEY_CODE_ENTER:
			wheelKeyInfo.mstrKeyCode = DDef.Enter;
			break;
		case EncorePanelCode.KEY_CODE_INFO:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case EncorePanelCode.KEY_CODE_NUM3:
			wheelKeyInfo.mstrKeyCode = DDef.Num3;
			break;
		case EncorePanelCode.KEY_CODE_NUM4:
			wheelKeyInfo.mstrKeyCode = DDef.Num4;
			break;
		case EncorePanelCode.KEY_CODE_NUM5:
			wheelKeyInfo.mstrKeyCode = DDef.Num5;
			break;
		case EncorePanelCode.KEY_CODE_NUM6:
			wheelKeyInfo.mstrKeyCode = DDef.Num6;
			break;
		case EncorePanelCode.KEY_CODE_AS12:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_as;
			break;
		case EncorePanelCode.KEY_CODE_EJECT:
			wheelKeyInfo.mstrKeyCode = DDef.Eject;
			break;
		case EncorePanelCode.KEY_CODE_CONFIG:
		case EncorePanelCode.KEY_CODE_TIME:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case EncorePanelCode.KEY_CODE_TONE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case EncorePanelCode.KEY_CODE_NUM1:
			wheelKeyInfo.mstrKeyCode = DDef.Num1;
			break;
		case EncorePanelCode.KEY_CODE_NEXT:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case EncorePanelCode.KEY_CODE_VOL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case EncorePanelCode.KEY_CODE_VOL_DN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case EncorePanelCode.KEY_CODE_SEL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case EncorePanelCode.KEY_CODE_SEL_DN:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case EncorePanelCode.KEY_CODE_PLAY:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pause;
			break;
		case EncorePanelCode.KEY_CODE_DISC:
			wheelKeyInfo.mstrKeyCode = DDef.Src_dvd;
			break;
		case EncorePanelCode.KEY_CODE_AUX:
			wheelKeyInfo.mstrKeyCode = DDef.Src_aux;
			break;
		default:
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
	
	private WheelKeyInfo translateGL8Panel(WheelKeyInfo wheelKeyInfo) {

		switch (wheelKeyInfo.mKeyCode) {
		case GL8PanelCode.KEY_CODE_PREV:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pre;
			break;
		case GL8PanelCode.KEY_CODE_NUM5:
			wheelKeyInfo.mstrKeyCode = DDef.Num5;
			break;
		case GL8PanelCode.KEY_CODE_NUM4:
			wheelKeyInfo.mstrKeyCode = DDef.Num4;
			break;
		case GL8PanelCode.KEY_CODE_NUM3:
			wheelKeyInfo.mstrKeyCode = DDef.Num3;
			break;
		case GL8PanelCode.KEY_CODE_POWER:
			wheelKeyInfo.mstrKeyCode = DDef.Power;
			break;
		case GL8PanelCode.KEY_CODE_INFO:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case GL8PanelCode.KEY_CODE_FAV:
			wheelKeyInfo.mstrKeyCode = DDef.Src_navi;
			break;
		case GL8PanelCode.KEY_CODE_NUM1:
			wheelKeyInfo.mstrKeyCode = DDef.Num1;
			break;
		case GL8PanelCode.KEY_CODE_NEXT:
			wheelKeyInfo.mstrKeyCode = DDef.Media_next;
			break;
		case GL8PanelCode.KEY_CODE_ENTER:
			wheelKeyInfo.mstrKeyCode = DDef.Enter;
			break;
		case GL8PanelCode.KEY_CODE_BACK:
			wheelKeyInfo.mstrKeyCode = DDef.Back;
			break;
		case GL8PanelCode.KEY_CODE_TONE:
			wheelKeyInfo.mstrKeyCode = DDef.Src_eq;
			break;
		case GL8PanelCode.KEY_CODE_EJECT:
			wheelKeyInfo.mstrKeyCode = DDef.Eject;
			break;
		case GL8PanelCode.KEY_CODE_CONFIG:
			wheelKeyInfo.mstrKeyCode = DDef.Src_set;
			break;
		case GL8PanelCode.KEY_CODE_MEDIA:
			wheelKeyInfo.mstrKeyCode = DDef.Src_mode;
			break;
		case GL8PanelCode.KEY_CODE_PLAY:
			wheelKeyInfo.mstrKeyCode = DDef.Media_pause;
			break;
		case GL8PanelCode.KEY_CODE_BAND:
			wheelKeyInfo.mstrKeyCode = DDef.Radio_fm;
			break;
		case GL8PanelCode.KEY_CODE_MUTE:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_mute;
			break;
		case GL8PanelCode.KEY_CODE_NUM6:
			wheelKeyInfo.mstrKeyCode = DDef.Num6;
			break;
		case GL8PanelCode.KEY_CODE_VOL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case GL8PanelCode.KEY_CODE_VOL_DN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case GL8PanelCode.KEY_CODE_SEL_UP:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case GL8PanelCode.KEY_CODE_SEL_DN:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		case GL8PanelCode.KEY_CODE_TUNE_ADD:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_add;
			break;
		case GL8PanelCode.KEY_CODE_TUNE_DEL:
			wheelKeyInfo.mstrKeyCode = DDef.Volume_del;
			break;
		default:
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
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

        // Data0
        byte baseData0 = (byte) packet[cursor];
        baseInfo.mRightFrontDoor = (byte) ((baseData0 >> 7) & 0x1);
        baseInfo.mLeftFrontDoor = (byte) ((baseData0 >> 6) & 0x1);
        baseInfo.mRightBackDoor = (byte) ((baseData0 >> 5) & 0x1);
        baseInfo.mLeftBackDoor = (byte) ((baseData0 >> 4) & 0x1);
        baseInfo.mTailBoxDoor = (byte) ((baseData0 >> 3) & 0x1);
        
        // Data1
        byte baseData1 = (byte) packet[cursor + 1];
        baseInfo.mFrontBoxDoor = (byte) ((baseData1 >> 7) & 0x1);
        return baseInfo;
	}

	@Override
	public ParkAssistInfo parseParkAssistantInfo(byte[] packet) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        ParkAssistInfo parkAssistInfo = new ParkAssistInfo();

        // Data0
        byte baseData0 = (byte) packet[cursor];
        parkAssistInfo.mParkSystemState = (byte) ((baseData0 >> 1) & 0x1);
        parkAssistInfo.mRadarSoundState = (byte) ((baseData0 >> 0) & 0x1);

        return parkAssistInfo;
	}

	@Override
	public VerInfo parseVersionInfo(byte[] packet) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        int length = packet[2];
        VerInfo version = new VerInfo();
        byte[] ver = new byte[length];
        System.arraycopy(packet, cursor, ver, 0, length);
        version.mVersion = DataConvert.BytesToStr(ver);
        return version;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelKeyInfo) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段

        if (packet[1] == DATA_TYPE_WHEEL_KEY_INFO) {
        	wheelKeyInfo.mKeyCode = packet[cursor];
        	wheelKeyInfo.mKeyStatus = packet[cursor + 1];
            TranslateKey(wheelKeyInfo);
            
		} else if (packet[1] == DATA_TYPE_PANEL_INFO) {
			if (mCarType == CarGmType.GM) {
				wheelKeyInfo.mKeyCode = (packet[cursor]);
				wheelKeyInfo.mKeyStatus = packet[cursor + 1];
				translateGmPanel(wheelKeyInfo);
				
			} else if (mCarType == CarGmType.ENCORE) {
				wheelKeyInfo.mKeyCode = packet[cursor];
				wheelKeyInfo.mKeyStatus = packet[cursor + 1];
				translateEncorePanel(wheelKeyInfo);
				
			} else if (mCarType == CarGmType.GL8) {
				wheelKeyInfo.mKeyCode = packet[cursor];
				wheelKeyInfo.mKeyStatus = packet[cursor + 1];
				translateGL8Panel(wheelKeyInfo);
				
			}
		}
        return wheelKeyInfo;
	}

	@Override
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        backLightInfo.mLight = (packet[cursor] & 0x8F)*255/18;
		return backLightInfo;
	}

	@Override
	public boolean IsSuportMeInfo() {
		return false;
	}

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		int cursor = 3;
		
		byte data0 = packet[cursor];
		cursor++;
		byte data1 = packet[cursor];
		airInfo.mAiron = (byte) (data0 >> 7 & 0x01);
		airInfo.mAcState = (byte) (data0 >> 6 & 0x01);
		airInfo.mCircleState = (byte) (data0 >> 5 & 0x01);
		airInfo.mRearLight = (byte) (data0 >> 4 & 0x01);
		airInfo.mAqsInCircle = (byte) (data0 >> 3 & 0x01);
		
		//airInfo.mWindRate = (byte) (data0 & 0x07);
		byte byLowWind = (byte) (data0 & 0x07);
		byte byHighWind = (byte) (0x10 & data1);

		if (byHighWind == 0x10) {
			airInfo.mWindRate = (byte) (0x08 | byLowWind);
		} else {
			airInfo.mWindRate = (byte) (0x07 & byLowWind);
		}
		airInfo.mMaxWindlv = 0x08;
		
		airInfo.mDaulLight = (byte) ((data1 >> 5) & 0x01);
		airInfo.mWindMode = (byte) (data1 & 0x0F);
		airInfo.mAutoLight1 = (byte) (airInfo.mWindMode == 1 ? 0x01 : 0x00);
		if (airInfo.mWindMode == 3 || airInfo.mWindMode == 4 || 
			airInfo.mWindMode == 8 || airInfo.mWindMode == 9) {
			airInfo.mDowmWind = 0x01;
		} else {
			airInfo.mDowmWind = 0x00;
		}
		
		if (airInfo.mWindMode == 3 || airInfo.mWindMode == 4 || 
			airInfo.mWindMode == 8 || airInfo.mWindMode == 9) {
			airInfo.mDowmWind = 0x01;
		} else {
			airInfo.mDowmWind = 0x00;
		}
		
		if (airInfo.mWindMode == 2 || airInfo.mWindMode == 6 || 
			airInfo.mWindMode == 7 || airInfo.mWindMode == 8 || airInfo.mWindMode == 9) {
			airInfo.mUpwardWind = 0x01;
		} else {
			airInfo.mUpwardWind = 0x00;
		}
		
		if (airInfo.mWindMode == 4 || airInfo.mWindMode == 5 || 
			airInfo.mWindMode == 6 || airInfo.mWindMode == 9) {
			airInfo.mParallelWind = 0x01;
		} else {
			airInfo.mParallelWind = 0x00;
		}
		
		airInfo.mMinTemp = 14;
		airInfo.mMaxTemp = 32;
		cursor++;
		airInfo.mLeftTemp = tranlateTemp(packet[cursor]);
		cursor++;
		airInfo.mRightTemp = tranlateTemp(packet[cursor]);
		
		cursor++;
		byte data4 = packet[cursor];
		airInfo.mLeftHotSeatTemp = (byte) (data4 >> 4 & 0x0F);
		airInfo.mRightHotSeatTemp = (byte) (data4 & 0x0F);
		
		cursor++;
//		byte data5 = packet[cursor];
//		float fOutTemp = data5;
 		
		return airInfo;
	}

	private float tranlateTemp(byte byData) {
		float fTemp = 0;
		switch (byData) {
		case 0x1D:
			fTemp = 16;
			break;
		case 0x1F:
			fTemp = (float) 16.5;
			break;
		case 0x20:
			fTemp = 15;
			break;
		case 0x21:
			fTemp = (float) 15.5;
			break;
		case 0x22:
			fTemp = 31;
			break;
		case 0x00:
			fTemp = 0;
			break;
		case 0x1E:
			fTemp = 32;
			break;
		case (byte) 0xFF:
			fTemp = -1;
			break;
		default:
			fTemp = (float) (16.5 + byData*0.5);
			break;
		}
		return fTemp;
	}
	
	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		int cursor = 0;
        byte iCmd = (byte) packet[cursor + 1];
        cursor += 3; // 跳过 2E、Data Type、Length字段
        if (iCmd == DATA_TYPE_REAR_RADAR_INFO) {
            radarInfo.mBackLeftDis = packet[cursor];
            radarInfo.mBackLeftCenterDis = packet[cursor + 1];
            radarInfo.mBackRightCenterDis = packet[cursor + 2];
            radarInfo.mBackRightDis = packet[cursor + 3];
        } else if (iCmd == DATA_TYPE_FRONT_RADAR_INFO) {
            radarInfo.mFrontLeftDis = packet[cursor];
            radarInfo.mFrontLeftCenterDis = packet[cursor + 1];
            radarInfo.mFrontRightCenterDis = packet[cursor + 2];
            radarInfo.mFrontRightDis = packet[cursor + 3];
        }
		return radarInfo;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		wheelInfo.mEps = -(DataConvert.byte2Short(packet, 3)*45/7800);
		return wheelInfo;
	}

	public GmCarSet parseGmCarSet(byte[] packet, GmCarSet gmCarSet) {
		int cursor = 3;
		if (packet[1] == DATA_TYPE_RADAR_ON_OFF) {
			gmCarSet.mRadar = (byte) (packet[cursor] & 0x01);
			
		} else	if (packet[1] == DATA_TYPE_AIR_SET_INFO) {
			byte data0 = packet[cursor];

			gmCarSet.mAutoWindSet = (byte) (data0 >> 6 & 0x03);
			gmCarSet.mAirQualitySensor = (byte) (data0 >> 4 & 0x03);
			gmCarSet.mPartTemp = (byte) (data0 >> 2 & 0x03);
			gmCarSet.mAutoDefogR = (byte) (data0 >> 1 & 0x01);
			gmCarSet.mAutoDefogF = (byte) (data0 & 0x01);
			
			byte data1 = packet[cursor + 1];
			gmCarSet.mRemoteSeatHeat = (byte) (data1 >> 3 & 0x01);
			gmCarSet.mStartMode = (byte) (data1 & 0x03); 
			
		} else if (packet[1] == DATA_TYPE_CAR_SET_INFO) {
			byte data0 = packet[cursor];
			gmCarSet.mLookForLights = (byte) (data0 >> 7 & 0x01);
			gmCarSet.mHeadlampDelaylock = (byte) (data0 >> 5 & 0x03);
			gmCarSet.mPreventDoorlock = (byte) (data0 >> 4 & 0x01);
			gmCarSet.mAutolockByStart = (byte) (data0 >> 3 & 0x01);
			gmCarSet.mAUtoUnlockByPark = (byte) (data0 >> 1 & 0x03);
			gmCarSet.mDelayLock = (byte) (data0 & 0x01);
			
			cursor++;
			byte data1 = packet[cursor];
			gmCarSet.mRemoteUnlockLight = (byte) (data1 >> 7 & 0x01);
			gmCarSet.mRemotelockLight = (byte) (data1 >> 5 & 0x03);
			gmCarSet.mRemoteUnlock = (byte) (data1 >> 4 & 0x01);
			gmCarSet.mBackWiper = (byte) (data1 >> 3 & 0x01);
			gmCarSet.mRemoteThenLock = (byte) (data1 >> 2 & 0x01);
			gmCarSet.mRemoteStart = (byte) (data1 >> 1 & 0x01);
			gmCarSet.mCarBodyCtrl = (byte) (data1 & 0x01);
			
		} else if (packet[1] == DATA_TYPE_CAR_SET2_INFO) {
			byte data0 = packet[cursor];
			gmCarSet.mNearCarUnlock = (byte) (data0 >> 7 &0x01);
			gmCarSet.mForgetKey = (byte) (data0 >> 6 & 0x01);
			gmCarSet.mPresonByDriver = (byte) (data0 >> 5 & 0x01);
			gmCarSet.mAutoRelockDoor = (byte) (data0 >> 4 & 0x01);
			gmCarSet.mFlankWarn  = (byte) (data0 >> 2 & 0x01);
			gmCarSet.mAutolockByAway = (byte) (data0 & 0x03);
			
			cursor++;
			byte data1 = packet[cursor];
			gmCarSet.mAutoCollision = (byte) (data1 >> 6 & 0x03);
			gmCarSet.mCarState = (byte) (data1 >> 5 & 0x01);
			gmCarSet.mAutoWiper = (byte) (data1 >> 4 & 0x01);
			gmCarSet.mRemoteWind = (byte) (data1 >> 3 & 0x01);
			
		} else if (packet[1] == DATA_TYPE_WARNING_VOL_INFO) {
			gmCarSet.mWarnVol = packet[cursor];
		} else if (packet[1] == DATA_TYPE_CAR_SPEED_INFO) {
			gmCarSet.mSpeed = DataConvert.byte2Short(packet, cursor)/16;
		} else if (packet[1] == DATA_TYPE_LAN_INFO) {
			gmCarSet.mLan = packet[cursor];
		}
		
		return gmCarSet;
	}
	
	public CanAudio parseCanAudio(byte[] packet, CanAudio canAudio) {
		int cursor = 3;
		
		canAudio.mbyMode = 2;
		canAudio.mbyAudioState = packet[cursor];
		
		switch (canAudio.mbyAudioState) {
		case ON_STAR_SYS_CLOSE:
			canAudio.mbshow = false;
			break;
		case ON_STAR_SYS_OPEN:
		case ON_STAR_INCOMING:
		case ON_STAR_OUTGOING:
		case ON_STAR_SPEAKING:
			canAudio.mbshow = true;
			break;
		}
		return canAudio;
	}
	
	public GmOnStar parseOnStar(byte[] packet, GmOnStar gmOnStar) {
		int cursor = 3;
		String strNum = "";
		for (int i = cursor; i < cursor + 10; i++) {
			if ((packet[i] & 0xF0) == 0xF0) {
				break;
			} else if ((packet[i] & 0x0F) == 0x0F) {
				strNum += intToStringNum(packet[i] >> 4 & 0x0F);
				break;
			} else {
				strNum += intToStringNum(packet[i] >> 4 & 0x0F);
				strNum += intToStringNum(packet[i] & 0x0F);
			}
		}
		gmOnStar.mStrNum = strNum;
		return gmOnStar;
	}
	
	private String intToStringNum(int iValue) {
		String str = "";
		if ( iValue == 0x0A) {
			str += "*";
		} else if (iValue == 0x0B) {
			str += "#";
		} else {
			str += iValue;
		}
		return str;
	}
	
	@Override
	public int GetAirInfoCmdId() {
		return DATA_TYPE_AIR_INFO;
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
	public int GetWheelInfoCmdId() {
		return DATA_TYPE_WHEEL_INFO;
	}
}
