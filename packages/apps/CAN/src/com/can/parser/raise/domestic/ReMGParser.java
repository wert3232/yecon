package com.can.parser.raise.domestic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.can.parser.DDef;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.MGCarSet;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.Protocol;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;

public class ReMGParser extends CanProxy{
	private Handler    	  mObjHandler;
	private ReMGProtol	  mObjProtocol;
	
	private BaseInfo  	  mObjBaseInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private WheelInfo	  mObjWheelInfo;
	private BackLightInfo mObjBackLightInfo;
	private RadarInfo	  mObjRadarInfo;
	private AirInfo		  mObjAirInfo;
	private MGCarSet	  mObjCarSet;
	private OutTemputerInfo mObjTempInfo;
	
	private byte 		  mDoorData = 0;
	public ReMGParser() {
		mObjBaseInfo      = new BaseInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjWheelInfo	  = new WheelInfo();
		mObjBackLightInfo = new BackLightInfo();
		mObjRadarInfo	  = new RadarInfo();
		mObjAirInfo		  = new AirInfo();
		mObjCarSet		  = new MGCarSet();
		mObjTempInfo	  = new OutTemputerInfo();
	}	
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		super.start(null, context, name, protocol);
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReMGProtol)protocol;
	}
	
	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		if (eCmdType == E_CMD_TYPE.eCmd_Type_PopWind || eCmdType == E_CMD_TYPE.eCmd_Type_BackCar) {
			super.RegisterProxy(ReMGProtol.DATA_TYPE_BASE_INFO, ReMGProtol.DATA_TYPE_BASE_INFO);
			super.RegisterProxy(ReMGProtol.DATA_TYPE_WHEEL_INFO, ReMGProtol.DATA_TYPE_WHEEL_INFO);
			super.RegisterProxy(ReMGProtol.DATA_TYPE_WHEEL_KEY_INFO, ReMGProtol.DATA_TYPE_WHEEL_KEY_INFO);
			super.RegisterProxy(ReMGProtol.DATA_TYPE_REAR_RADAR_INFO, ReMGProtol.DATA_TYPE_REAR_RADAR_INFO);
			super.RegisterProxy(ReMGProtol.DATA_TYPE_AIR_SET_INFO, ReMGProtol.DATA_TYPE_AIR_SET_INFO);
			super.RegisterProxy(ReMGProtol.DATA_TYPE_CAR_SET_INFO, ReMGProtol.DATA_TYPE_CAR_SET_INFO);
			super.RegisterProxy(ReMGProtol.DATA_TYPE_OUT_TEMP_INFO, ReMGProtol.DATA_TYPE_OUT_TEMP_INFO);
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
			} else	if (iCmdID == mObjProtocol.GetBaseInfoCmdId()) {
				if (mDoorData != packet[3]) {
					mDoorData = packet[3];
					mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
					objUImsg.what = DDef.BASE_CMD_ID;
					objUImsg.obj = mObjBaseInfo;
					mObjHandler.sendMessage(objUImsg);
				}
			} else if (iCmdID == mObjProtocol.GetWheelKeyInfoCmdId()) {
				Log.i(TAG, "Recevice can Key Message!");
				mObjWheelKeyInfo = mObjProtocol.parseWheelKeyInfo(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mObjHandler.sendMessage(objUImsg);

			}  else if (iCmdID == mObjProtocol.GetWheelInfoCmdId()) {
				mObjWheelInfo = mObjProtocol.parseWheelInfo(packet, mObjWheelInfo);
				objUImsg.what = DDef.WHEEL_CMD_ID;
				objUImsg.obj = mObjWheelInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetBackLightInfoCmdId()) {
				mObjBackLightInfo = mObjProtocol.parseBackLightInfo(packet,
						mObjBackLightInfo);
				objUImsg.what = DDef.LIGHT_CMD_ID;
				objUImsg.obj = mObjBackLightInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetBackRadarInfoCmdId()
					|| iCmdID == mObjProtocol.GetFrontRadarInfoCmdId()) {

				Log.i(TAG, "Recevice RadarInfo Message!");
				mObjRadarInfo = mObjProtocol.parseRadarInfo(packet, mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == ReMGProtol.DATA_TYPE_CAR_SET_INFO) {
				mObjCarSet = mObjProtocol.parseCarSet(packet, mObjCarSet);
				objUImsg.what = DDef.CAR_SET_CMD_ID;
				objUImsg.obj = mObjCarSet;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == ReMGProtol.DATA_TYPE_OUT_TEMP_INFO) {
				mObjTempInfo = mObjProtocol.parseTemputerInfo(packet, mObjTempInfo);
				objUImsg.what = DDef.OUT_TEMP_ID;
				objUImsg.obj = mObjTempInfo;
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
		case DDef.CAR_SET_CMD_ID:
			object = mObjCarSet;
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
