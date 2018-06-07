package com.can.parser.raise.psa;

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
import com.can.parser.DDef.PsaCarState;
import com.can.parser.DDef.PsaCruSpeed;
import com.can.parser.DDef.PsaDiagInfo;
import com.can.parser.DDef.PsaFuncInfo;
import com.can.parser.DDef.PsaMemSpeed;
import com.can.parser.DDef.PsaTripComputer;
import com.can.parser.DDef.PsaWarnInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

public class RePsaParser extends CanProxy{
	private byte  mDoorData = -1;
	private Handler 	  mHandler;
	private RePsaProtocol mProtocol;
	private AirInfo   	  mObjAirInfo;
	private RadarInfo 	  mObjRadarInfo;
	private BaseInfo  	  mObjBaseInfo;
	private WheelInfo 	  mObjWheelInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	private OutTemputerInfo mObjOutTemputerInfo;
	private PsaTripComputer mObjTripComputer;
	private PsaCarState		mObjCarState;
	private PsaWarnInfo		mObjWarnInfo;
	private PsaDiagInfo		mObjDiagInfo;
	private PsaFuncInfo		mObjFuncInfo;
	private PsaCruSpeed		mObjCruSpeed;
	private PsaMemSpeed		mObjMemSpeed;
	
	public RePsaParser() {
		mObjAirInfo = new AirInfo();
		mObjRadarInfo = new RadarInfo();
		mObjBaseInfo = new BaseInfo();
		mObjWheelInfo = new WheelInfo();
		mObjWheelKeyInfo = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
		mObjOutTemputerInfo = new OutTemputerInfo();
		mObjTripComputer = new PsaTripComputer();
		mObjCarState = new PsaCarState();
		mObjWarnInfo = new PsaWarnInfo();
		mObjDiagInfo = new PsaDiagInfo();
		mObjFuncInfo = new PsaFuncInfo();
		mObjCruSpeed = new PsaCruSpeed();
		mObjMemSpeed = new PsaMemSpeed();
	}
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		super.start(handler, context, name, protocol);
		mHandler = handler;
		mProtocol = (RePsaProtocol)protocol;
	}


	@Override
	public void deInit() {
		super.deInit();
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		if (eCmdType == E_CMD_TYPE.eCmd_Type_PopWind || eCmdType == E_CMD_TYPE.eCmd_Type_BackCar) {
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_AIR_INFO, RePsaProtocol.DATA_TYPE_AIR_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_BACK_LIGHT_INFO, RePsaProtocol.DATA_TYPE_BACK_LIGHT_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_WHEEL_INFO, RePsaProtocol.DATA_TYPE_WHEEL_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_WHEEL_KEY_INFO, RePsaProtocol.DATA_TYPE_WHEEL_KEY_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_RADAR_INFO, RePsaProtocol.DATA_TYPE_RADAR_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_REVER_RADIO_INFO, RePsaProtocol.DATA_TYPE_REVER_RADIO_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_OBD_PAGE0_INFO, RePsaProtocol.DATA_TYPE_OBD_PAGE0_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_OBD_PAGE1_INFO, RePsaProtocol.DATA_TYPE_OBD_PAGE1_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_OBD_PAGE2_INFO, RePsaProtocol.DATA_TYPE_OBD_PAGE2_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_OUT_TEMP_INFO, RePsaProtocol.DATA_TYPE_OUT_TEMP_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_WARN_RECORD_INFO, RePsaProtocol.DATA_TYPE_WARN_RECORD_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_CAR_STATE_INFO, RePsaProtocol.DATA_TYPE_CAR_STATE_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_CAR_FUNC_INFO, RePsaProtocol.DATA_TYPE_CAR_FUNC_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_DIAGNOSIS_INFO, RePsaProtocol.DATA_TYPE_DIAGNOSIS_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_REMEM_SPEED_INFO, RePsaProtocol.DATA_TYPE_REMEM_SPEED_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_SPEED_INFO, RePsaProtocol.DATA_TYPE_SPEED_INFO);
			super.RegisterProxy(RePsaProtocol.DATA_TYPE_CAR_INFO, RePsaProtocol.DATA_TYPE_CAR_INFO);
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
				
				mObjOutTemputerInfo = mProtocol.parseOutTempInfo(packet, mObjOutTemputerInfo);
				objUImsg.what = DDef.OUT_TEMP_ID;
				objUImsg.obj = mObjOutTemputerInfo;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == RePsaProtocol.DATA_TYPE_OBD_PAGE0_INFO ||
						iCmdID == RePsaProtocol.DATA_TYPE_OBD_PAGE1_INFO ||
						iCmdID == RePsaProtocol.DATA_TYPE_OBD_PAGE2_INFO) {
				mObjTripComputer = mProtocol.parsePsaTripComputer(packet, mObjTripComputer);
				objUImsg.what = DDef.TRIP_INFO_MINOIL;
				objUImsg.obj = mObjTripComputer;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == RePsaProtocol.DATA_TYPE_CAR_STATE_INFO) {
				if (mDoorData != packet[3]) {
					mDoorData = packet[3];
					mObjBaseInfo = mProtocol.parseBaseInfo(packet, mObjBaseInfo);
					objUImsg.what = DDef.BASE_CMD_ID;
					objUImsg.obj = mObjBaseInfo;
					mHandler.sendMessage(objUImsg);
				}
				Message msg1 = mHandler.obtainMessage();
				mObjCarState = mProtocol.parsepPsaCarState(packet, mObjCarState);
				msg1.what = DDef.CAR_SET_CMD_ID;
				msg1.obj = mObjCarState;
				mHandler.sendMessage(msg1);
			} else if (iCmdID == RePsaProtocol.DATA_TYPE_WARN_RECORD_INFO) {
				mObjWarnInfo = mProtocol.parsePsaWarnInfo(packet, mObjWarnInfo);
				objUImsg.what = DDef.PSA_WARN_INFO;
				objUImsg.obj = mObjWarnInfo;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == RePsaProtocol.DATA_TYPE_CAR_FUNC_INFO) {
				mObjFuncInfo = mProtocol.parsePsaFuncInfo(packet, mObjFuncInfo);
				objUImsg.what = DDef.PSA_FUNC_INFO;
				objUImsg.obj = mObjFuncInfo;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == RePsaProtocol.DATA_TYPE_DIAGNOSIS_INFO) {
				mObjDiagInfo = mProtocol.parsePsaDiagInfo(packet, mObjDiagInfo);
				objUImsg.what = DDef.PSA_DIOG_INFO;
				objUImsg.obj = mObjDiagInfo;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == RePsaProtocol.DATA_TYPE_REMEM_SPEED_INFO) {
				mObjMemSpeed = mProtocol.parsePsaMemSpeed(packet, mObjMemSpeed);
				objUImsg.what = DDef.PSA_MEM_SPEED;
				objUImsg.obj = mObjMemSpeed;
				mHandler.sendMessage(objUImsg);
			} else if (iCmdID == RePsaProtocol.DATA_TYPE_SPEED_INFO) {
				mObjCruSpeed = mProtocol.parsePsaCruSpeed(packet, mObjCruSpeed);
				objUImsg.what = DDef.PSA_CRU_SPEED;
				objUImsg.obj = mObjCruSpeed;
				mHandler.sendMessage(objUImsg);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public Object getData(int iwhat, int iVal) {
		Object object = null;
		switch (iwhat) {
		case DDef.AIR_CMD_ID:
			object = mObjAirInfo;
			break;
		case DDef.CAR_SET_CMD_ID:
			object = mObjCarState;
			break;
		case DDef.PSA_WARN_INFO:
			object = mObjWarnInfo;
			break;
		case DDef.PSA_DIOG_INFO:
			object = mObjDiagInfo;
			break;
		case DDef.PSA_FUNC_INFO:
			object = mObjFuncInfo;
			break;
		case DDef.PSA_MEM_SPEED:
			object = mObjMemSpeed;
			break;
		case DDef.PSA_CRU_SPEED:
			object = mObjCruSpeed;
			break;
		default:
			break;
		}
		return object;
	}
}
