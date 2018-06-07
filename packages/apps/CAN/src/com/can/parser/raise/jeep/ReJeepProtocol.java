package com.can.parser.raise.jeep;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.os.Message;

import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CDState;
import com.can.parser.DDef.CDTxInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CompassInfo;
import com.can.parser.DDef.PowerAmplifier;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.parser.raise.nissan.ReNissanProtocol.Source;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReJeepProtocol
 * 
 * @function:Jeep协议类
 * @author Kim
 * @Date: 2016-7-14上午9:34:41
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReJeepProtocol extends ReProtocol {

	/**
	 * 睿志城Jeep自由光V1.03.002
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x05; // 空调信息
	public static final byte DATA_TYPE_CAR_INFO = (byte) 0x07; // 车辆信息
	public static final byte DATA_TYPE_BACK_RADAR_INFO = (byte) 0x22; // 后雷达信息
	public static final byte DATA_TYPE_FRONT_RADAR_INFO = (byte) 0x23; // 前雷达信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x0A; // 基本信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x30; // 版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x09; // 方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x01; // 方向盘按键信息
	public static final byte DATA_TYPE_BACK_LIGHT_INFO = (byte) 0x02; // 背光调节
	public static final byte DATA_TYPE_PANEL_KEY_INFO = (byte) 0x04; // 面板按键
	public static final byte DATA_TYPE_COMPASS_INFO = (byte) 0x0B; // 指南针信息
	public static final byte DATA_TYPE_CD_STATE_INFO = (byte) 0x10; // CD状态信息
	public static final byte DATA_TYPE_CD_TX_INFO = (byte) 0x11; // CD文本信息
	public static final byte DATA_TYPE_DSP_INFO = (byte) 0x31; // Dsp

	// Host -> Slave
	public static final byte DATA_TYPE_DSP_SET = (byte) 0x93; // Dsp set
	public static final byte DATA_TYPE_SOURCE_REQUEST = (byte) 0x90; // Source
	public static final byte DATA_TYPE_AIR_SET = (byte) 0x95; // AIR set
	public static final byte DATA_TYPE_CAR_SET = (byte) 0x97; // CAR set
	public static final byte DATA_TYPE_CD_CTRL = (byte) 0xA0; // CD Ctrl
	public static final byte DATA_TYPE_REQUEST_INFO = (byte) 0xF1; // CD Ctrl
	public static final byte DATA_TYPE_TIME_SET = (byte) 0xC6; // CD Ctrl

	// 车辆设置
	public static final byte parksense = 0x01;
	public static final byte tiltrmirror = 0x04;
	public static final byte fparksensevol = 0x02;
	public static final byte affectdyncline = 0x05;
	public static final byte rainfallwiper = 0x06;
	public static final byte rparksensevol = 0x03;
	public static final byte rampauxiliary = 0x07;
	public static final byte blindspotwarn = 0x0E;
	public static final byte videoparkfixline = 0x08;
	public static final byte rradarautoparkaux = 0x09;
	public static final byte allowautobrakeservice = 0x0A;
	public static final byte autoparkbrake = 0x0B;
	public static final byte lanedeparturecal = 0x0D;

	public static final byte fcollisionwarnbrake = 0x0F;
	public static final byte fcollisionwarn = 0x10;
	public static final byte antiglareautobeam = 0x16;
	public static final byte cardoorwarn = 0x28;
	public static final byte autostartdrivingseat = 0x54;
	public static final byte nearlightupauto = 0x12;
	public static final byte lanedeparturewarning = 0x0C;
	public static final byte buzzerswitch = 0x60;

	public static final byte headlightoffdelay = 0x11;
	public static final byte wiperautostartlight = 0x13;
	public static final byte daytimerunlight = 0x14;
	public static final byte lightflash = 0x15;

	public static final byte autodoorlock = 0x20;
	public static final byte getoffautounlock = 0x22;
	public static final byte driveautopadlock = 0x21;
	public static final byte lockcarvoice = 0x23;
	public static final byte firsttimunlock = 0x24;
	public static final byte nokeytoenter = 0x25;
	public static final byte intellkeyperset = 0x26;
	public static final byte electricdooralarm = 0x27;

	public static final byte seatconvpassinout = 0x31;
	public static final byte enginepowershutdown = 0x32;

	public static final byte onoroffadjustsuspen = 0x41;
	public static final byte showsuspensioninfo = 0x42;
	public static final byte tirejack = 0x43;
	public static final byte modeoftransport = 0x44;
	public static final byte wheelalignmentmodel = 0x45;

	public static final byte rearviewadjustlens = 0x51;
	public static final byte unitset = 0x52;
	public static final byte langset = 0x53;
	public static final byte Reset = 0x55;
	public static boolean mbCdInfo = false;
	
	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_AIR_INFO & 0xFF;
	}

	@Override
	public int GetCarInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_CAR_INFO & 0xFF;
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BACK_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_FRONT_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BASE_INFO & 0xFF;
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

	public int GetPanelKeyCmdId() {
		return DATA_TYPE_PANEL_KEY_INFO & 0xFF;
	}

	@Override
	public int GetBackLightInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BACK_LIGHT_INFO & 0xFF;
	}

	public int GetCompassCmdId() {
		return DATA_TYPE_COMPASS_INFO & 0xFF;
	}

	public int GetCDStateCmdId() {
		return DATA_TYPE_CD_STATE_INFO & 0xFF;
	}

	public int GetCDTxCmdId() {
		return DATA_TYPE_CD_TX_INFO & 0xFF;
	}

	public int GetDspInfoCmdId() {
		return DATA_TYPE_DSP_INFO & 0xFF;
	}

	@Override
	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过2E、Data Type、Length字段

		byte bydata0 = packet[cursor];
		// Parksense雷达泊车
		carInfo.mbyParksenseRadar = (byte) ((0xC0 & bydata0) >> 6);
		// 前Parksense音量
		carInfo.mbyFrontParksenseVol = (byte) ((0x30 & bydata0) >> 4);
		// 后Parksense音量
		carInfo.mbyAfterParksenseVol = (byte) ((0x0C & bydata0) >> 2);
		// 倒车时倾斜后视镜
		carInfo.mbyBackcarTiltMirror = (byte) DataConvert.GetBit(bydata0, 1);
		// Parkview影响泊车动态引导线
		carInfo.mbyParksenseDynTrack = (byte) DataConvert.GetBit(bydata0, 0);

		cursor += 1;
		byte bydata1 = packet[cursor];
		// 雨量感应式雨刷
		carInfo.mbyRainsensingwipers = (byte) DataConvert.GetBit(bydata1, 7);
		// 坡道起步辅助
		carInfo.mbyRampstartassist = (byte) DataConvert.GetBit(bydata1, 6);
		// 忙点警报
		carInfo.mbyBusywarning = (byte) ((0x30 & bydata1) >> 4);

		cursor += 1;
		byte bydata2 = packet[cursor];
		// 大灯关闭延时
		carInfo.mbyHeadlightoffdelay = (byte) ((0xC0 & bydata2) >> 6);
		// 启动雨刷时自动启动大灯
		carInfo.mbySautowheadlightwiper = (byte) DataConvert.GetBit(bydata2, 3);
		// 日间行车灯
		carInfo.mbyDaytimeRunninglight = (byte) DataConvert.GetBit(bydata2, 2);
		// 锁车转向灯闪烁
		carInfo.mbyLockcarturnlightflash = (byte) DataConvert
				.GetBit(bydata2, 1);

		cursor += 1;
		byte bydata3 = packet[cursor];
		// 自动车门锁定
		carInfo.mbyAutomaticdoorlock = (byte) DataConvert.GetBit(bydata3, 7);
		// 下车时自动解锁
		carInfo.mbyAutounlockgetoff = (byte) DataConvert.GetBit(bydata3, 6);
		// 行车自动落锁
		carInfo.mbyDrivingautolatch = (byte) DataConvert.GetBit(bydata3, 5);
		// 锁车时发出提示音
		carInfo.mbyLockcarprompttone = (byte) DataConvert.GetBit(bydata3, 4);
		// 首次按车钥匙解锁
		carInfo.mbyForfirstunlockcarkeys = (byte) DataConvert
				.GetBit(bydata3, 3);
		// 无钥匙进入
		carInfo.mbyNokeyentry = (byte) DataConvert.GetBit(bydata3, 2);
		// 智能钥匙个性化设置
		carInfo.mbySmartkeypersonal = (byte) DataConvert.GetBit(bydata3, 1);
		// 电动尾门报警
		carInfo.mbyElectrictaildooralarm = (byte) DataConvert
				.GetBit(bydata3, 0);

		cursor += 1;
		byte bydata4 = packet[cursor];
		// 座椅便利进出
		carInfo.mbySeatconvenientinout = (byte) DataConvert.GetBit(bydata4, 7);
		// 发动机电源关闭延迟
		carInfo.mbyEnginepoweroffdelay = (byte) ((0x60 & bydata4) >> 5);

		cursor += 1;
		byte bydata5 = packet[cursor];
		// 上下车自动调节悬架
		carInfo.mbyUpdownautosuspension = (byte) DataConvert.GetBit(bydata5, 7);
		// 显示悬架信息
		carInfo.mbyDispsuspensioninfo = (byte) DataConvert.GetBit(bydata5, 6);
		// 轮胎千斤顶
		carInfo.mbyTirejack = (byte) DataConvert.GetBit(bydata5, 5);
		// 运输模式
		carInfo.mbyTransportmode = (byte) DataConvert.GetBit(bydata5, 4);
		// 轮胎校准模式
		carInfo.mbyTirecalibrationmodel = (byte) DataConvert.GetBit(bydata5, 3);

		cursor += 1;
		byte bydata6 = packet[cursor];
		// 后视镜调光镜
		carInfo.mbyMirrorlightmirror = (byte) DataConvert.GetBit(bydata6, 7);
		// 单位设置
		carInfo.mbyUnitSetting = (byte) DataConvert.GetBit(bydata6, 6);
		// 语言设置
		carInfo.mbylangSetting = (byte) DataConvert.GetBit(bydata6, 5);

		cursor += 1;
		byte bydata7 = packet[cursor];
		// Parkview影响泊车固定引导线
		carInfo.mbyParksensestaTrack = (byte) DataConvert.GetBit(bydata7, 7);
		// 后雷达泊车自动辅助
		carInfo.mbyRearRadarparking = (byte) DataConvert.GetBit(bydata7, 6);
		// 允许自动制动服务
		carInfo.mbyAllowautobrakeser = (byte) DataConvert.GetBit(bydata7, 5);
		// 自动泊车制动
		carInfo.mbyAutomaticparkbrake = (byte) DataConvert.GetBit(bydata7, 4);
		// 车道偏离校正力度
		carInfo.mbyLanedevcorrection = (byte) (0x07 & bydata7);

		cursor += 1;
		byte bydata8 = packet[cursor];
		// 前方碰撞警报自动制动
		carInfo.mbyAutobrakfrontcwarn = (byte) DataConvert.GetBit(bydata8, 6);
		// 前方碰撞警告
		carInfo.mbyForwardcollisionwarn = (byte) DataConvert.GetBit(bydata8, 5);
		// 自动防炫远光灯
		carInfo.mbyAutoantidazzlelight = (byte) DataConvert.GetBit(bydata8, 4);
		// 车门报警
		carInfo.mbyDooralarm = (byte) DataConvert.GetBit(bydata8, 2);
		// 车辆启动时自动启动主驾驶座椅加热
		carInfo.mbyAutodriverseatvehicle = (byte) (0x03 & bydata8);

		cursor += 1;
		byte bydata9 = packet[cursor];
		// 靠近时大灯自动亮起
		carInfo.mbyAutolightuphdlightclose = (byte) ((0x60 & bydata9) >> 5);
		// 车道偏离警告
		carInfo.mbyLaneDepartureWarn = (byte) ((0x18 & bydata9) >> 3);
		// 蜂鸣器开关
		carInfo.mbyBuzzerswitchonoff = (byte) DataConvert.GetBit(bydata9, 0);

		return carInfo;
	}

	private byte[] mbyAirInfo;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过2E、Data Type、Length字段

		// Data 0
		byte airData0 = (byte) packet[cursor];
		airInfo.mAiron = (byte) ((airData0 >> 7) & 0x1);
		airInfo.mAcState = (byte) ((airData0 >> 6) & 0x1);
		airInfo.mAcMax = (byte) ((airData0 >> 5) & 0x1);
		airInfo.mAutoLight2 = (byte) ((airData0 >> 4) & 0x1);
		airInfo.mCircleState = (byte) ((airData0 >> 2) & 0x3);
		airInfo.mRearLight = (byte) ((airData0 >> 1) & 0x1);
		airInfo.mFWDefogger = (byte) ((airData0 >> 0) & 0x1);

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		airInfo.mSync = (byte) ((airData1 >> 6) & 0x3);
		airInfo.mWindRate = (byte) ((airData1) & 0x0F);

		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];
		airInfo.mAutoWind = (byte) ((airData2 >> 7) & 0x1);
		airInfo.mUpwardWind = (byte) ((airData2 >> 6) & 0x1);
		airInfo.mParallelWind = (byte) ((airData2 >> 4) & 0x1);
		airInfo.mDowmWind = (byte) ((airData2) >> 5 & 0x1);
		airInfo.mDisplay = 1;

		// Data 3
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		airInfo.mLeftTemp = (float) airData3;
		airInfo.mMinTemp = (float) 0;
		airInfo.mMaxTemp = (float) 0x7F;

		// Data 4
		cursor += 1;
		byte airData4 = (byte) packet[cursor];
		airInfo.mRightTemp = (float) airData4;

		// Data 5
		cursor += 1;
		byte airData5 = (byte) packet[cursor];
		airInfo.mLeftHotSeatTemp = (byte) ((byte) ((airData5 >> 6) & 0x3)+1);
		airInfo.mRightHotSeatTemp = (byte) ((byte) ((airData5 >> 2) & 0x3)+1);

		cursor +=2;
		airInfo.mOutTempEnable = true;
		byte airData7 = (byte) packet[cursor];
		if (airInfo.mTempUnit == 0) {
			airInfo.mOutTemp = airData7 - 40;
		} else {
			airInfo.mOutTemp = (float) (airData7 * 1.8 + 32);
		}
		
		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!equals(mbyAirInfo, packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}
	
	private boolean equals(byte[] bylastInfo, byte[] byNewInfo) {

		int iIndex = 0;
		boolean bequals = true;

		for (byte b : byNewInfo) {
			if (iIndex == 10) {
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
		baseInfo.mLightState = (byte) ((baseData0 >> 2) & 0x1);
		baseInfo.mPStopBlockState = (byte) ((baseData0 >> 1) & 0x1);
		baseInfo.mBackCarState = (byte) ((baseData0 >> 0) & 0x1);

		// Data1
		byte baseData1 = (byte) packet[cursor + 1];

		baseInfo.mTailBoxDoor = (byte) ((baseData1 >> 3) & 0x1);
		baseInfo.mRightBackDoor = (byte) ((baseData1 >> 4) & 0x1);
		baseInfo.mLeftBackDoor = (byte) ((baseData1 >> 5) & 0x1);
		baseInfo.mRightFrontDoor = (byte) ((baseData1 >> 6) & 0x1);
		baseInfo.mLeftFrontDoor = (byte) ((baseData1 >> 7) & 0x1);

		return baseInfo;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mEps = DataConvert.byte2Short(packet, cursor);
		wheelInfo.mEps = -(wheelInfo.mEps * 45 / 539);

		return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = TranslateKey((byte) (packet[cursor] & 0xFF));
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
		return wheelInfo;
	}

	@Override
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		backLightInfo.mLight = DataConvert.byteToInt((byte) packet[cursor]);
		float flightlv = (float) ((float) (backLightInfo.mLight) * 1.27);
		backLightInfo.mLight = (int) flightlv;

		return backLightInfo;
	}

	@Override
	public CompassInfo parseCompassInfo(byte[] packet, CompassInfo compassInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		compassInfo.mbyCompassDir = packet[cursor];
		compassInfo.mbyCompassState = (byte) ((packet[cursor + 1] >> 6) & 0x3);
		compassInfo.Compassarea = (byte) (packet[cursor + 1] & 0x3F);

		return compassInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		// TODO Auto-generated method stub
		int cursor = 0;
		byte DataType = (byte) packet[cursor + 1];
		cursor += 3; // 跳过 2E、Data Type、Length字段

		if (DataType == DATA_TYPE_BACK_RADAR_INFO) {

			radarInfo.mBackLeftDis = packet[cursor];
			radarInfo.mBackLeftCenterDis = packet[cursor + 1];
			radarInfo.mBackRightCenterDis = packet[cursor + 2];
			radarInfo.mBackRightDis = packet[cursor + 3];
		} else if (DataType == DATA_TYPE_FRONT_RADAR_INFO) {
			radarInfo.mFrontLeftDis = packet[cursor];
			radarInfo.mFrontLeftCenterDis = packet[cursor + 1];
			radarInfo.mFrontRightCenterDis = packet[cursor + 2];
			radarInfo.mFrontRightDis = packet[cursor + 3];
		}

		radarInfo.mbyRightShowType = 0;

		return radarInfo;
	}

	public WheelKeyInfo parsePanelKeyInfo(byte[] packet, WheelKeyInfo keyInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		keyInfo.mKeyCode = TranslateKey((byte) (packet[cursor] & 0xFF));
		keyInfo.mKeyStatus = (byte) packet[cursor + 1];
		return keyInfo;
	}

	public CDState parseCDStateInfo(byte[] packet, CDState state) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte bydata0 = packet[cursor];
		state.mbyCDPlayMode = (byte) ((bydata0 >> 4) & 0xF);
		state.mbyCDStatus = (byte) (bydata0 & 0xF);

		byte[] bydata = new byte[2];
		bydata[0] = packet[cursor + 4];
		bydata[1] = packet[cursor + 3];
		state.miCDCurTrack = DataConvert.byte2Short(bydata, 0);
		bydata[0] = packet[cursor + 2];
		bydata[1] = packet[cursor + 1];
		state.miCDTotalTrack = DataConvert.byte2Short(bydata, 0);
		bydata[0] = packet[cursor + 6];
		bydata[1] = packet[cursor + 5];
		state.miCDCurTrackTimeNum = DataConvert.byte2Short(bydata, 0);
		state.mbyCDPlayHour = packet[cursor + 7];
		state.mbyCDPlayMin = packet[cursor + 8];
		state.mbyCDPlaySec = packet[cursor + 9];

		return state;
	}

	public CDTxInfo parseCdTxInfo(byte[] packet, CDTxInfo info) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		info.miType = packet[cursor] & 0xFF;
		byte[] bydata = new byte[2];
		bydata[0] = packet[cursor + 2];
		bydata[1] = packet[cursor + 1];
		info.miTrackNum = DataConvert.byte2Short(bydata, 0);

		byte[] bydatas = new byte[packet.length - 7];
		System.arraycopy(packet, cursor + 3, bydatas, 0, packet.length - 7);
		try {
			info.mstrText = new String(bydatas, "unicode").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info;
	}

	public PowerAmplifier parseDspInfo(byte[] packet, PowerAmplifier dspInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		dspInfo.mMute = (byte) DataConvert.GetBit(packet[cursor], 7);
		dspInfo.mCurVol = packet[cursor] & 0x7F;

		dspInfo.mFAD = packet[cursor + 1]; // FAD指示
		dspInfo.mBAL = packet[cursor + 2]; // BAL指示
		dspInfo.mBASS = packet[cursor + 3]; // BASS指示
		dspInfo.mTRE = packet[cursor + 5]; // TRE指示
		dspInfo.mMID = packet[cursor + 4]; // MID指示
		dspInfo.mASL = (byte) ((packet[cursor + 6] >> 1) & 0x2); // ASL指示
		dspInfo.mVolByASL = (byte) DataConvert.GetBit(packet[cursor + 6], 0); // 车辆音量跟随ASL
		dspInfo.mDspDev = (byte) DataConvert.GetBit(packet[cursor + 7], 7);

		return dspInfo;
	}

	private String mstrKeyCode = null;

	public WheelKeyInfo TranslateKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub
		switch (info.mKeyCode) {
		case 0x01:
			info.mstrKeyCode = DDef.Blacklight;
			break;
		case 0x02:
			info.mstrKeyCode = DDef.Back;
			break;
		case 0x03:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x04:
		case 0x20:
			info.mstrKeyCode = DDef.Enter;
			break;
		case 0x05:
		case 0x12:
		case 0x14:
			info.mstrKeyCode = DDef.Volume_del;
			break;
		case 0x06:
		case 0x11:
		case 0x13:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case 0x07:
		case 0x1E:
		case 0x1D:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x08:
		case 0x1F:
		case 0x1C:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x18:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case 0x19:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		case 0x17:
			info.mstrKeyCode = DDef.Speech;
			break;
		case 0x16:
			info.mstrKeyCode = DDef.Radio_as;
			break;
		case 0x1A:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		default:
			break;
		}

		if (info.mKeyCode != 0) {
			mstrKeyCode = info.mstrKeyCode;
		}
		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrKeyCode;
			mstrKeyCode = null;
		}

		return info;
	}

	@Override
	public boolean IsSuportMeInfo() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public byte TranslateSource(int iSource) {
		// TODO Auto-generated method stub
		byte bySource = Source.OFF;

		switch (iSource) {
		case DDef.SOURCE_DEF.Src_radio:
			bySource = 0x01;
			break;
		case DDef.SOURCE_DEF.Src_music:
			bySource = 0x04;
			break;
		case DDef.SOURCE_DEF.Src_video:
			bySource = 0x04;
			break;
		case DDef.SOURCE_DEF.Src_bluetooth:
			bySource = 0x05;
			break;
		case DDef.SOURCE_DEF.Src_dvd:
			bySource = 0x03;
			break;
		case DDef.SOURCE_DEF.Src_phonelink:
		case DDef.SOURCE_DEF.Src_backcar:
		case DDef.SOURCE_DEF.Src_bt_phone:
		case DDef.SOURCE_DEF.Src_photo:
		case DDef.SOURCE_DEF.Src_ipod:
		case DDef.SOURCE_DEF.Src_dvr:
		case DDef.SOURCE_DEF.Src_dtv:
		case DDef.SOURCE_DEF.Src_atv:
		case DDef.SOURCE_DEF.Src_avin:
			bySource = 0x07;
			break;
		default:
			break;
		}

		return bySource;
	}

	public byte[] MediaPlayInfo(MediaInfo mediaInfo) {

		byte[] bydata = null;
		String strVal = "";
		byte bySource = TranslateSource(mediaInfo.getSource());
		StringBuffer strInfo = new StringBuffer();
		bySource = mbCdInfo ? 0x07 : bySource;

		switch (bySource) {
		case 0x01:
			int iBand = mediaInfo.getBand();
			if (iBand >= 0 && iBand <= 2) {
				strInfo.append(1);
				strInfo.append("FM");
				strInfo.append(" ");
				strInfo.append("CH");
				strInfo.append(mediaInfo.getFreqIndex());
				strInfo.append(" ");
				strInfo.append(((float)mediaInfo.getMainFreq())/100);
				strInfo.append("MHZ");
			}else {
				strInfo.append(2);
				strInfo.append("AM");
				strInfo.append(" ");
				strInfo.append("CH");
				strInfo.append(mediaInfo.getFreqIndex());
				strInfo.append(" ");
				strInfo.append(mediaInfo.getMainFreq());
				strInfo.append("KHZ");
			}
			
			strVal = strInfo.toString();
			
			try {
				bydata = strVal.getBytes("unicode");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 0x03:
			strInfo.append(3);
			strInfo.append("DISC");
			strInfo.append(" ");
			strInfo.append(mediaInfo.getCurTrack());
			strInfo.append("/");
			strInfo.append(mediaInfo.getTotalTrack());
			
			strVal = strInfo.toString();
			
			try {
				bydata = strVal.getBytes("unicode");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 0x04:
			strInfo.append(4);
			strInfo.append("USB");
			strInfo.append(" ");
			strInfo.append(mediaInfo.getCurTrack());
			strInfo.append("/");
			strInfo.append(mediaInfo.getTotalTrack());
			
			strVal = strInfo.toString();
			
			try {
				bydata = strVal.getBytes("unicode");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 0x05:
			strInfo.append(5);
			strInfo.append("A2DP");
			
			strVal = strInfo.toString();
			
			try {
				bydata = strVal.getBytes("unicode");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 0x06:
			strInfo.append(6);
			strInfo.append("SD");
			strInfo.append(" ");
			strInfo.append(mediaInfo.getCurTrack());
			strInfo.append("/");
			strInfo.append(mediaInfo.getTotalTrack());
			
			strVal = strInfo.toString();
			
			try {
				bydata = strVal.getBytes("unicode");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 0x07:
			strInfo.append(7);
			strInfo.append("AUX");
			
			strVal = strInfo.toString();
			
			try {
				bydata = strVal.getBytes("unicode");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

		return bydata;
	}

	@Override
	public Message getVolInfo(int iVol) {
		// TODO Auto-generated method stub
		byte[] bydata = new byte[2];
		bydata[0] = 0x07;
		bydata[1] = (byte) iVol;

		return CanTxRxStub.getTxMessage(DATA_TYPE_DSP_SET, DATA_TYPE_DSP_SET,
				bydata, this);
	}

	private byte[] getTime(MediaInfo mediaInfo) {
		byte[] byData = new byte[4];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();

		byData[0] = 0x50;
		byData[1] = (byte) ((timeInfo.by24Mode == 0) ? (byData[1] | 0x80)
				: (byData[1] & 0x00));
		byData[1] = (byte) (byData[1] | ((0x7E & byData[1]) | timeInfo.byHour));
		byData[2] = timeInfo.byMinute;
		byData[3] = timeInfo.bySecond;

		return byData;
	}

	private Message getMediaData(MediaInfo mediaInfo) {

		byte[] byData = MediaPlayInfo(mediaInfo);

		return CanTxRxStub.getTxMessage(DATA_TYPE_SOURCE_REQUEST,
				DATA_TYPE_SOURCE_REQUEST, byData, this);
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		ArrayList<Message> aListMsg = new ArrayList<Message>();

		aListMsg.add(getMediaData(mediaInfo));
		aListMsg.add(CanTxRxStub.getTxMessage(DATA_TYPE_TIME_SET,
				DATA_TYPE_TIME_SET, getTime(mediaInfo), this));

		return aListMsg;
	}
}
