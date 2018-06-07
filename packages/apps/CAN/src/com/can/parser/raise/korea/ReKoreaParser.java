package com.can.parser.raise.korea;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReKoreaParser
 * 
 * @function:睿志城韩系解析
 * @author Kim
 * @Date:  2016-6-4 上午10:30:08
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReKoreaParser extends CanProxy{

	private Handler 	  mHandler;
	private ReKoreaProtocol mProtocol;
	private AirInfo   	  mObjAirInfo;
	private RadarInfo 	  mObjRadarInfo;
	private BaseInfo  	  mObjBaseInfo;
	private WheelInfo 	  mObjWheelInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	private OutTemputerInfo mObjOutTemputerInfo;
	
	public ReKoreaParser() {
		mObjAirInfo = new AirInfo();
		mObjRadarInfo = new RadarInfo();
		mObjBaseInfo = new BaseInfo();
		mObjWheelInfo = new WheelInfo();
		mObjWheelKeyInfo = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
		mObjOutTemputerInfo = new OutTemputerInfo();
	}
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		super.start(handler, context, name, protocol);
		mHandler = handler;
		mProtocol = (ReKoreaProtocol)protocol;
	}


	@Override
	public void deInit() {
		super.deInit();
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		if (eCmdType == E_CMD_TYPE.eCmd_Type_PopWind || eCmdType == E_CMD_TYPE.eCmd_Type_BackCar) {
			super.RegisterProxy(ReKoreaProtocol.DATA_TYPE_AIR_INFO, ReKoreaProtocol.DATA_TYPE_AIR_INFO);
			super.RegisterProxy(ReKoreaProtocol.DATA_TYPE_BACK_LIGHT_INFO, ReKoreaProtocol.DATA_TYPE_BACK_LIGHT_INFO);
			super.RegisterProxy(ReKoreaProtocol.DATA_TYPE_PARKASSIST_INFO, ReKoreaProtocol.DATA_TYPE_PARKASSIST_INFO);
			super.RegisterProxy(ReKoreaProtocol.DATA_TYPE_WHEEL_INFO, ReKoreaProtocol.DATA_TYPE_WHEEL_INFO);
			super.RegisterProxy(ReKoreaProtocol.DATA_TYPE_WHEEL_KEY_INFO, ReKoreaProtocol.DATA_TYPE_WHEEL_KEY_INFO);
			super.RegisterProxy(ReKoreaProtocol.DATA_TYPE_RADAR_INFO, ReKoreaProtocol.DATA_TYPE_RADAR_INFO);
			super.RegisterProxy(ReKoreaProtocol.DATA_TYPE_BASE_INFO, ReKoreaProtocol.DATA_TYPE_BASE_INFO);
			super.RegisterProxy(ReKoreaProtocol.DATA_TYPE_OUT_TEMPUTER_INFO, ReKoreaProtocol.DATA_TYPE_OUT_TEMPUTER_INFO);
		} else if (eCmdType == E_CMD_TYPE.eCmd_Type_Dsp) {
		}
	}

	@Override
	public void Finish() {
		Message objUImsg = mHandler.obtainMessage();
		objUImsg.what = DDef.FINISH_BIND;
		objUImsg.obj  = mProtocol; 
		mHandler.sendMessage(objUImsg);
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case CanTxRxStub.MSG_CAN_RX:
			byte[] packet = CanTxRxStub.getRxPacket(msg);

//			if (packet != null) {
//				// 回复Ack
//				byte[] ack = { (byte) 0xFF };
//				sendMsg2Can(CanTxRxStub.getTxMessage(0, 0, ack, null));
//			}
			int iCmdID = mProtocol.ParseRegisterCmdId(packet, 0);
			Message objUImsg = mHandler.obtainMessage();

			if (iCmdID == mProtocol.GetAirInfoCmdId()) {
				
				Log.i(TAG, "Recevice Air Message %s!" + DataConvert.Bytes2Str(packet));
				mObjAirInfo = mProtocol.parseAirInfo(packet, mObjAirInfo);

				objUImsg.what = DDef.AIR_CMD_ID;
				objUImsg.obj = mObjAirInfo;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == mProtocol.GetBackRadarInfoCmdId()
					|| iCmdID == mProtocol.GetFrontRadarInfoCmdId()) {

				mObjRadarInfo = mProtocol.parseRadarInfo(packet, mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
				mHandler.sendMessage(objUImsg);

			} else if (iCmdID == mProtocol.GetBaseInfoCmdId()) {
				mObjBaseInfo = mProtocol.parseBaseInfo(packet, mObjBaseInfo);
				objUImsg.what = DDef.BASE_CMD_ID;
				objUImsg.obj = mObjBaseInfo;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == mProtocol.GetParkAssistCmdId()) {
				ParkAssistInfo parkAssistInfo = mProtocol
						.parseParkAssistantInfo(packet);
				objUImsg.what = DDef.PARK_CMD_ID;
				objUImsg.obj = parkAssistInfo;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == mProtocol.GetWheelInfoCmdId()) {
				Log.i(TAG, "Recevice can Wheel Message!");
				mObjWheelInfo = mProtocol.parseWheelInfo(packet, mObjWheelInfo);
				objUImsg.what = 6;
				objUImsg.obj = mObjWheelInfo;
				mHandler.sendMessage(objUImsg);

			} else if (iCmdID == mProtocol.GetWheelKeyInfoCmdId()) {
				Log.i(TAG, "Recevice can Key Message!");

				mObjWheelKeyInfo = mProtocol.parseWheelKeyInfo(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mHandler.sendMessage(objUImsg);

			} else if (iCmdID == mProtocol.GetBackLightInfoCmdId()) {
				mObjBackLightInfo = mProtocol.parseBackLightInfo(packet,
						mObjBackLightInfo);
				objUImsg.what = DDef.LIGHT_CMD_ID;
				objUImsg.obj = mObjBackLightInfo;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == mProtocol.GetOutTempInfoCmdId()) {
				
				mObjOutTemputerInfo = mProtocol.parseOutTemputerInfo(packet, mObjOutTemputerInfo);
				objUImsg.what = DDef.OUT_TEMP_ID;
				objUImsg.obj = mObjOutTemputerInfo;
				mHandler.sendMessage(objUImsg);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public Object getData(int iwhat, int iVal) {
		// TODO Auto-generated method stub
		return null;
	}
}
