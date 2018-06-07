package com.can.parser.raise.domestic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.can.parser.DDef;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.Protocol;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;

public class ReSouAstParser extends CanProxy{
	private Handler    	  mObjHandler;
	private ReSouAstProtol	  mObjProtocol;
	
	private BaseInfo  	  mObjBaseInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private WheelInfo	  mObjWheelInfo;
	private RadarInfo	  mObjRadarInfo;
	private TPMSInfo	  mObjTpmsInfo;
	
	private byte 		  mDoorData = 0;
	public ReSouAstParser() {
		mObjBaseInfo      = new BaseInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjWheelInfo	  = new WheelInfo();
		mObjRadarInfo	  = new RadarInfo();
		mObjTpmsInfo	  = new TPMSInfo();
	}	
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		super.start(null, context, name, protocol);
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReSouAstProtol)protocol;
	}
	
	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		if (eCmdType == E_CMD_TYPE.eCmd_Type_PopWind || eCmdType == E_CMD_TYPE.eCmd_Type_BackCar) {
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_BASE_INFO, ReSouAstProtol.DATA_TYPE_BASE_INFO);
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_WHEEL_INFO, ReSouAstProtol.DATA_TYPE_WHEEL_INFO);
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_WHEEL_KEY_INFO, ReSouAstProtol.DATA_TYPE_WHEEL_KEY_INFO);
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_REAR_RADAR_INFO, ReSouAstProtol.DATA_TYPE_REAR_RADAR_INFO);
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_PANEL_KEY_INFO, ReSouAstProtol.DATA_TYPE_PANEL_KEY_INFO);
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_FRONT_RADAR_INFO, ReSouAstProtol.DATA_TYPE_FRONT_RADAR_INFO);
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_TPMS_INFO, ReSouAstProtol.DATA_TYPE_TPMS_INFO);
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_FUEL_INFO, ReSouAstProtol.DATA_TYPE_FUEL_INFO);
			super.RegisterProxy(ReSouAstProtol.DATA_TYPE_CAR_SET_INFO, ReSouAstProtol.DATA_TYPE_CAR_SET_INFO);
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
			if (iCmdID == mObjProtocol.GetBaseInfoCmdId()) {
				if (mDoorData != packet[3]) {
					mDoorData = packet[3];
					mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
					objUImsg.what = DDef.BASE_CMD_ID;
					objUImsg.obj = mObjBaseInfo;
					mObjHandler.sendMessage(objUImsg);
				}
			} else if (iCmdID == mObjProtocol.GetWheelKeyInfoCmdId() ||
						iCmdID == ReSouAstProtol.DATA_TYPE_PANEL_KEY_INFO) {
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
			} else if (iCmdID == mObjProtocol.GetBackRadarInfoCmdId()
					|| iCmdID == mObjProtocol.GetFrontRadarInfoCmdId()) {

				Log.i(TAG, "Recevice RadarInfo Message!");
				mObjRadarInfo = mObjProtocol.parseRadarInfo(packet, mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == ReSouAstProtol.DATA_TYPE_CAR_SET_INFO) {
				
			} else if (iCmdID == ReSouAstProtol.DATA_TYPE_TPMS_INFO) {
				mObjTpmsInfo = mObjProtocol.parseTpmsInfo(packet, mObjTpmsInfo);
				objUImsg.what = DDef.TPMS_CMD_ID;
				objUImsg.obj = mObjTpmsInfo;
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
			break;
		case DDef.TPMS_CMD_ID:
			object = mObjTpmsInfo;
			break;
		default:
			break;
		}
		return object;
	}
}
