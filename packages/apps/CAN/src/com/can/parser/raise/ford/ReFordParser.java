package com.can.parser.raise.ford;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CanAudio;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.SyncMediaTime;
import com.can.parser.DDef.SyncMenu;
import com.can.parser.DDef.SyncOption;
import com.can.parser.DDef.SyncState;
import com.can.parser.DDef.SyncTalkTime;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;
import com.can.tool.CanInfo;
import com.can.tool.DataConvert;


/**
 * ClassName:ReFordParser
 * 
 * @function:睿志城福特协议解析器
 * @author Kim
 * @Date:  2016-7-2下午2:28:09
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReFordParser extends CanProxy{

	private Handler 	   mObjHandler  = null;
	private ReFordProtocol mObjProtocol = null;
	
	private AirInfo   	  mObjAirInfo;
	private CarInfo   	  mObjCarInfo;
	private RadarInfo 	  mObjRadarInfo;
	private BaseInfo  	  mObjBaseInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	
	private CanAudio      mObjCanAudio;
	private SyncState	  mObjSyncState;
	private SyncMenu	  mObjSyncMenu;
	private SyncOption    mObjSyncOption;
	private SyncMediaTime mObjSyncMediaTime;
	private SyncTalkTime  mObjSyncTalkTime;
	
	public ReFordParser() {
		// TODO Auto-generated constructor stub
		mObjCarInfo       = new CarInfo();
		mObjRadarInfo     = new RadarInfo();
		mObjAirInfo       = new AirInfo();
		mObjBaseInfo      = new BaseInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
		mObjCanAudio     = new CanAudio();
		mObjSyncState    = new SyncState();
		mObjSyncMenu     = new SyncMenu();
		mObjSyncOption   = new SyncOption();
		mObjSyncMediaTime= new SyncMediaTime();
		mObjSyncTalkTime = new SyncTalkTime();
	}
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		// TODO Auto-generated method stub
		super.start(null, context, name, protocol);
		
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReFordProtocol)protocol;
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		// TODO Auto-generated method stub
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_AIR_INFO, ReFordProtocol.DATA_TYPE_AIR_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_BASE_INFO, ReFordProtocol.DATA_TYPE_BASE_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_WHEEL_KEY_INFO, ReFordProtocol.DATA_TYPE_WHEEL_KEY_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_BACK_RADAR_INFO, ReFordProtocol.DATA_TYPE_BACK_RADAR_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_FRONT_RADAR_INFO, ReFordProtocol.DATA_TYPE_FRONT_RADAR_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_PARKASSIST_INFO, ReFordProtocol.DATA_TYPE_PARKASSIST_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_VERSION_INFO, ReFordProtocol.DATA_TYPE_VERSION_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_BACK_LIGHT_INFO, ReFordProtocol.DATA_TYPE_BACK_LIGHT_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_CAR_INFO, ReFordProtocol.DATA_TYPE_CAR_INFO);
		
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_SYNC_ADUIO, ReFordProtocol.DATA_TYPE_SYNC_ADUIO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_SYNC_STATE, ReFordProtocol.DATA_TYPE_SYNC_STATE);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_SYNC_MENU_INFO, ReFordProtocol.DATA_TYPE_SYNC_MENU_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_SYNC_MENU_OPTION, ReFordProtocol.DATA_TYPE_SYNC_MENU_OPTION);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_SYNC_MEDIA_INFO, ReFordProtocol.DATA_TYPE_SYNC_MEDIA_INFO);
		super.RegisterProxy(ReFordProtocol.DATA_TYPE_SYNC_TALK_INFO, ReFordProtocol.DATA_TYPE_SYNC_TALK_INFO);
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case CanTxRxStub.MSG_CAN_RX:
			byte[] packet = CanTxRxStub.getRxPacket(msg);

			if (packet != null) {
				// 回复Ack
				byte[] ack = { (byte) 0xFF };
				sendMsg2Can(CanTxRxStub.getTxMessage(0, 0, ack, null));
			}

			int iCmdID = mObjProtocol.ParseRegisterCmdId(packet, 0);
			Message objUImsg = mObjHandler.obtainMessage();
			
			if (iCmdID == mObjProtocol.GetAirInfoCmdId()) {
				
				CanInfo.i(TAG, "Recevice Air Message %s!" + DataConvert.Bytes2Str(packet));
				mObjAirInfo = mObjProtocol.parseAirInfo(packet, mObjAirInfo);

				objUImsg.what = DDef.AIR_CMD_ID;
				objUImsg.obj = mObjAirInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetCarInfoCmdId()) {
				
				CanInfo.i(TAG, "Recevice Car Message!");
				mObjCarInfo = mObjProtocol.parseCarInfo(packet, mObjCarInfo);
				objUImsg.what = DDef.CARINFO_CMD_ID;
				objUImsg.obj = mObjCarInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetBackRadarInfoCmdId()
					|| iCmdID == mObjProtocol.GetFrontRadarInfoCmdId()) {

				CanInfo.i(TAG, "Recevice RadarInfo Message!");
				mObjRadarInfo = mObjProtocol.parseRadarInfo(packet, mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetBaseInfoCmdId()) {

				CanInfo.i(TAG, "Recevice BaseInfo Message!");
				mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
				objUImsg.what = DDef.BASE_CMD_ID;
				objUImsg.obj = mObjBaseInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetParkAssistCmdId()) {
				CanInfo.i(TAG, "Recevice ParkAssist Message!");
				ParkAssistInfo parkAssistInfo = mObjProtocol
						.parseParkAssistantInfo(packet);
				objUImsg.what = DDef.PARK_CMD_ID;
				objUImsg.obj = parkAssistInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetWheelKeyInfoCmdId()) {
				CanInfo.i(TAG, "Recevice can Key Message!");

				mObjWheelKeyInfo = mObjProtocol.parseWheelKeyInfo(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetBackLightInfoCmdId()) {
				
				mObjBackLightInfo = mObjProtocol.parseBackLightInfo(packet,
						mObjBackLightInfo);
				objUImsg.what = DDef.LIGHT_CMD_ID;
				objUImsg.obj = mObjBackLightInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetVersionInfoCmdId()) {

				CanInfo.i(TAG, "Recevice Version Message!");
			} else if (iCmdID == mObjProtocol.GetSyncAudioCmdId()) {
				
				mObjCanAudio = mObjProtocol.parseSyncAudio(packet, mObjCanAudio);
				
				objUImsg.what = DDef.CAN_AUDIO_INFO;
				objUImsg.obj = mObjCanAudio;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetSyncStateCmdId()) {
				
				mObjSyncState = mObjProtocol.parseSyncState(packet, mObjSyncState);
				
				objUImsg.what = DDef.SYNC_STATE;
				objUImsg.obj = mObjSyncState;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetSyncMenuCmdId()) {
				
				mObjSyncMenu = mObjProtocol.parseSyncMenu(packet, mObjSyncMenu);
				
				objUImsg.what = DDef.SYNC_MENU;
				objUImsg.obj = mObjSyncMenu;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetSyncOptionCmdId()) {
				
				mObjSyncOption = mObjProtocol.parseSyncOption(packet, mObjSyncOption);
				
				objUImsg.what = DDef.SYNC_OPTION;
				objUImsg.obj = mObjSyncOption;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetSyncMediaTimeCmdId()) {
				
				mObjSyncMediaTime = mObjProtocol.parseMediaTime(packet, mObjSyncMediaTime);
				
				objUImsg.what = DDef.SYNC_MEDTIME;
				objUImsg.obj = mObjSyncMediaTime;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetSyncTalkTimeCmdId()) {
				
				mObjSyncTalkTime = mObjProtocol.parseTalkTime(packet, mObjSyncTalkTime);
				
				objUImsg.what = DDef.SYNC_TALKTIME;
				objUImsg.obj = mObjSyncTalkTime;
				mObjHandler.sendMessage(objUImsg);
			}
			break;

		default:
			super.handleMessage(msg);	
			break;
		}
	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub
		Message objUImsg = mObjHandler.obtainMessage();
		objUImsg.what = DDef.FINISH_BIND;
		objUImsg.obj  = mObjProtocol; 
		mObjHandler.sendMessage(objUImsg);
	}

	@Override
	public Object getData(int iwhat, int iVal) {
		// TODO Auto-generated method stub
		Object obj = null;
		switch (iwhat) {
		case DDef.BASE_CMD_ID:
			obj = mObjBaseInfo;
			break;
		default:
			break;
		}
		
		return obj;
	}
}
