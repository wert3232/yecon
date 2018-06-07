package com.can.parser.raise.gm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.can.parser.DDef;
import com.can.parser.DDef.GmCarSet;
import com.can.parser.Protocol;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CanAudio;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.GmOnStar;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;

public class ReGmParser extends CanProxy{
	private Handler    	   mObjHandler;
	private ReGmProtocol   mObjProtocol;
	
	private BaseInfo  	  mObjBaseInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	private AirInfo		  mObjAirInfo;
	private RadarInfo	  mObjRadarInfo;
	private WheelInfo	  mObjWheelInfo;
	private GmCarSet	  mObjCarSet;
	private GmOnStar	  mObjOnStar;
	private CanAudio	  mObjCanAudio;
	
	public ReGmParser() {
		mObjBaseInfo      = new BaseInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
		mObjAirInfo 	  = new AirInfo();
		mObjRadarInfo	  = new RadarInfo();
		mObjWheelInfo	  = new WheelInfo();
		mObjCarSet		  = new GmCarSet();
		mObjOnStar		  = new GmOnStar();
		mObjCanAudio	  = new CanAudio();
	}	
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		super.start(null, context, name, protocol);
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReGmProtocol)protocol;
	}
	
	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		if (eCmdType == E_CMD_TYPE.eCmd_Type_PopWind || eCmdType == E_CMD_TYPE.eCmd_Type_BackCar) {

			super.RegisterProxy(ReGmProtocol.DATA_TYPE_WHEEL_KEY_INFO, ReGmProtocol.DATA_TYPE_WHEEL_KEY_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_PANEL_INFO, ReGmProtocol.DATA_TYPE_PANEL_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_AIR_INFO, ReGmProtocol.DATA_TYPE_AIR_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_BACK_LIGHT_INFO, ReGmProtocol.DATA_TYPE_BACK_LIGHT_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_AIR_SET_INFO, ReGmProtocol.DATA_TYPE_AIR_SET_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_CAR_SET_INFO, ReGmProtocol.DATA_TYPE_CAR_SET_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_RADAR_ON_OFF, ReGmProtocol.DATA_TYPE_RADAR_ON_OFF);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_ON_STAR_PHONE_INFO, ReGmProtocol.DATA_TYPE_ON_STAR_PHONE_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_ON_STAR_STATE_INFO, ReGmProtocol.DATA_TYPE_ON_STAR_STATE_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_CAR_SET2_INFO, ReGmProtocol.DATA_TYPE_CAR_SET2_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_CAR_SPEED_INFO, ReGmProtocol.DATA_TYPE_CAR_SPEED_INFO);
			
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_LAN_INFO, ReGmProtocol.DATA_TYPE_LAN_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_WARNING_VOL_INFO, ReGmProtocol.DATA_TYPE_WARNING_VOL_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_REAR_RADAR_INFO, ReGmProtocol.DATA_TYPE_REAR_RADAR_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_FRONT_RADAR_INFO, ReGmProtocol.DATA_TYPE_FRONT_RADAR_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_BASE_INFO, ReGmProtocol.DATA_TYPE_BASE_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_WHEEL_INFO, ReGmProtocol.DATA_TYPE_WHEEL_INFO);
			super.RegisterProxy(ReGmProtocol.DATA_TYPE_VERSION_INFO, ReGmProtocol.DATA_TYPE_VERSION_INFO);
		}
	}

	public void deInit(){
		
		super.deInit();
	}
	
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what)
		{
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
				mObjAirInfo = mObjProtocol.parseAirInfo(packet, mObjAirInfo);
				objUImsg.what = DDef.AIR_CMD_ID;
				objUImsg.obj = mObjAirInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetBackRadarInfoCmdId() ||
					   iCmdID == mObjProtocol.GetFrontRadarInfoCmdId() ) {
					
				mObjRadarInfo = mObjProtocol.parseRadarInfo(packet, mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
				mObjHandler.sendMessage(objUImsg);
			} else	if (iCmdID == mObjProtocol.GetBaseInfoCmdId()) {

				Log.i(TAG, "Recevice BaseInfo Message!");
				mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
				objUImsg.what = DDef.BASE_CMD_ID;
				objUImsg.obj = mObjBaseInfo;
				mObjHandler.sendMessage(objUImsg);
				
			} else if (iCmdID == mObjProtocol.GetWheelKeyInfoCmdId() ||
					   iCmdID == ReGmProtocol.DATA_TYPE_PANEL_INFO ) {
				Log.i(TAG, "Recevice can Key Message!");

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
			} else if (iCmdID == mObjProtocol.GetWheelInfoCmdId()) {
				mObjWheelInfo = mObjProtocol.parseWheelInfo(packet, mObjWheelInfo);
				objUImsg.what = DDef.WHEEL_CMD_ID;
				objUImsg.obj = mObjWheelInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == ReGmProtocol.DATA_TYPE_RADAR_ON_OFF ||
					   iCmdID == ReGmProtocol.DATA_TYPE_AIR_SET_INFO ||
					   iCmdID == ReGmProtocol.DATA_TYPE_CAR_SET_INFO ||
					   iCmdID == ReGmProtocol.DATA_TYPE_CAR_SET2_INFO ||
					   iCmdID == ReGmProtocol.DATA_TYPE_WARNING_VOL_INFO ||
					   iCmdID == ReGmProtocol.DATA_TYPE_CAR_SPEED_INFO ||
					   iCmdID == ReGmProtocol.DATA_TYPE_LAN_INFO ) {
				mObjCarSet = mObjProtocol.parseGmCarSet(packet, mObjCarSet);
				objUImsg.what = DDef.CAR_SET_CMD_ID;
				objUImsg.obj = mObjCarSet;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == ReGmProtocol.DATA_TYPE_ON_STAR_STATE_INFO) {
				mObjCanAudio = mObjProtocol.parseCanAudio(packet, mObjCanAudio);
				objUImsg.what = DDef.CAN_AUDIO_INFO;
				objUImsg.obj = mObjCanAudio;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == ReGmProtocol.DATA_TYPE_ON_STAR_PHONE_INFO) {
				mObjOnStar = mObjProtocol.parseOnStar(packet, mObjOnStar);
				objUImsg.what = DDef.ON_STAR_ID;
				objUImsg.obj = mObjOnStar;
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
		Message objUImsg = mObjHandler.obtainMessage();
		objUImsg.what = DDef.FINISH_BIND;
		objUImsg.obj  = mObjProtocol; 
		mObjHandler.sendMessage(objUImsg);
	}

	@Override
	public Object getData(int iwhat, int iVal) {
		Object object = null;
		switch (iwhat) {
		case DDef.AIR_CMD_ID:
			object = mObjAirInfo;
			break;
		case DDef.CAR_SET_CMD_ID:
			object = mObjCarSet;
			break;
		case DDef.ON_STAR_ID:
			object = mObjOnStar;
			break;
		case DDef.CAN_AUDIO_INFO:
			object = mObjCanAudio;
			break;
		default:
			break;
		}
		return object;
	}

}
