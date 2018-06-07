package com.can.parser.raise.honda;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.can.parser.DDef;
import com.can.parser.DDef.CarSetting;
import com.can.parser.DDef.CompassInfo;
import com.can.parser.DDef.FuelMilInfo;
import com.can.parser.Protocol;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

public class ReHondaParser extends CanProxy{
	private Handler    	   mObjHandler;
	private ReHondaProtocol mObjProtocol;
	
	private AirInfo   	  mObjAirInfo;
	private CarSetting	  mObjCarSetting;
	private RadarInfo 	  mObjRadarInfo;
	private BaseInfo  	  mObjBaseInfo;
	private WheelInfo 	  mObjWheelInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	private FuelMilInfo	  mObjFuelMilInfo;
	private CompassInfo	  mObjCompassInfo;
	
	private byte 		  mDoorData = 0;
	
	public ReHondaParser() {
		mObjCarSetting    = new CarSetting();
		mObjRadarInfo     = new RadarInfo();
		mObjAirInfo       = new AirInfo();
		mObjBaseInfo      = new BaseInfo();
		mObjWheelInfo     = new WheelInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
		mObjFuelMilInfo	  = new FuelMilInfo();
		mObjCompassInfo	  = new CompassInfo();
	}	
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		super.start(null, context, name, protocol);
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReHondaProtocol)protocol;
	}
	
	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		if (eCmdType == E_CMD_TYPE.eCmd_Type_PopWind || eCmdType == E_CMD_TYPE.eCmd_Type_BackCar) {
			
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_AIR_INFO, ReHondaProtocol.DATA_TYPE_AIR_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_BASE_INFO, ReHondaProtocol.DATA_TYPE_BASE_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_WHEEL_KEY_INFO, ReHondaProtocol.DATA_TYPE_WHEEL_KEY_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_BACK_RADAR_INFO, ReHondaProtocol.DATA_TYPE_BACK_RADAR_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_FRONT_RADAR_INFO, ReHondaProtocol.DATA_TYPE_FRONT_RADAR_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_WHEEL_INFO, ReHondaProtocol.DATA_TYPE_WHEEL_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_PARKASSIST_INFO, ReHondaProtocol.DATA_TYPE_PARKASSIST_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_BACK_LIGHT_INFO, ReHondaProtocol.DATA_TYPE_BACK_LIGHT_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_CAR_INFO, ReHondaProtocol.DATA_TYPE_CAR_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_FUEL_MIL_INFO, ReHondaProtocol.DATA_TYPE_FUEL_MIL_INFO);
			super.RegisterProxy(ReHondaProtocol.DATA_TYPE_COMPASS_INFO, ReHondaProtocol.DATA_TYPE_COMPASS_INFO);
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
				
				Log.i(TAG, "Recevice Air Message %s!" + DataConvert.Bytes2Str(packet));
				mObjAirInfo = mObjProtocol.parseAirInfo(packet, mObjAirInfo);
				objUImsg.what = DDef.AIR_CMD_ID;
				objUImsg.obj = mObjAirInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == ReHondaProtocol.DATA_TYPE_CAR_INFO) {
				
				Log.i(TAG, "Recevice Car Message!");
				mObjCarSetting = mObjProtocol.parseCarSettingInfo(packet, mObjCarSetting);
				objUImsg.what = DDef.CAR_SET_CMD_ID;
				objUImsg.obj = mObjCarSetting;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetBackRadarInfoCmdId()
					|| iCmdID == mObjProtocol.GetFrontRadarInfoCmdId()) {

				Log.i(TAG, "Recevice RadarInfo Message!");
				mObjRadarInfo = mObjProtocol.parseRadarInfo(packet, mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetBaseInfoCmdId()) {

				Log.i(TAG, "Recevice BaseInfo Message!");
				if (mDoorData != packet[3]) {
					mDoorData = packet[3];
					mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
					objUImsg.what = DDef.BASE_CMD_ID;
					objUImsg.obj = mObjBaseInfo;
					mObjHandler.sendMessage(objUImsg);
				}
			} else if (iCmdID == mObjProtocol.GetParkAssistCmdId()) {
				Log.i(TAG, "Recevice ParkAssist Message!");
				ParkAssistInfo parkAssistInfo = mObjProtocol
						.parseParkAssistantInfo(packet);
				objUImsg.what = DDef.PARK_CMD_ID;
				objUImsg.obj = parkAssistInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetWheelInfoCmdId()) {
				Log.i(TAG, "Recevice can Wheel Message!");
				mObjWheelInfo = mObjProtocol.parseWheelInfo(packet, mObjWheelInfo);
				objUImsg.what = 6;
				objUImsg.obj = mObjWheelInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetWheelKeyInfoCmdId()) {
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
			} else if (iCmdID == ReHondaProtocol.DATA_TYPE_FUEL_MIL_INFO) {
				Log.i(TAG, "Recevice fuel info Message!++ mCHAverFuelUnit:" + mObjFuelMilInfo.mCHAverFuelUnit);
				mObjFuelMilInfo = mObjProtocol.parseFuelMilInfo(packet, mObjFuelMilInfo);
				Log.i(TAG, "Recevice fuel info Message!-- mCHAverFuelUnit:" + mObjFuelMilInfo.mCHAverFuelUnit);
				objUImsg.what = DDef.FUELMIL_CMD_ID;
				objUImsg.obj = mObjFuelMilInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == ReHondaProtocol.DATA_TYPE_COMPASS_INFO) {
				Log.i(TAG, "Recevice compass info Message!");
				mObjCompassInfo = mObjProtocol.parseCompassInfo(packet, mObjCompassInfo);
				objUImsg.what = DDef.COMPASS_CMD_ID;
				objUImsg.obj = mObjCompassInfo;
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
		// TODO Auto-generated method stub
		Object object = null;
		switch (iwhat) {
		case DDef.FUELMIL_CMD_ID:
			object = mObjFuelMilInfo;
			break;
		case DDef.COMPASS_CMD_ID:
			object = mObjCompassInfo;
			break;
		case DDef.CAR_SET_CMD_ID:
			object = mObjCarSetting;
			break;
		case DDef.AIR_CMD_ID:
			object = mObjAirInfo;
			break;
		default:
			break;
		}
		return object;
	}

}
