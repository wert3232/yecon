package com.can.parser.raise.dz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;
import com.can.tool.CanInfo;
import com.can.tool.DataConvert;

/**
 * ClassName:ReMQB6Parser
 * 
 * @function:睿志城大众MQB6平台协议解析器
 * @author Kim
 * @Date:  2016-5-26 上午11:54:05
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReMQB6Parser extends CanProxy{

	private Handler    	   mObjHandler;
	private ReMQB6Protocol mObjProtocol;
	
	private AirInfo   	  mObjAirInfo;
	private CarInfo   	  mObjCarInfo;
	private RadarInfo 	  mObjRadarInfo;
	private BaseInfo  	  mObjBaseInfo;
	private WheelInfo 	  mObjWheelInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	
	public ReMQB6Parser() {
		// TODO Auto-generated constructor stub
		mObjCarInfo       = new CarInfo();
		mObjRadarInfo     = new RadarInfo();
		mObjAirInfo       = new AirInfo();
		mObjBaseInfo      = new BaseInfo();
		mObjWheelInfo     = new WheelInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
	}	
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		// TODO Auto-generated method stub
		super.start(null, context, name, protocol);
		
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReMQB6Protocol)protocol;
	}

	/**
	 * 注册代理
	 */
	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		// TODO Auto-generated method stub
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_AIR_INFO, ReMQB6Protocol.DATA_TYPE_AIR_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_BASE_INFO, ReMQB6Protocol.DATA_TYPE_BASE_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_WHEEL_KEY_INFO, ReMQB6Protocol.DATA_TYPE_WHEEL_KEY_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_BACK_RADAR_INFO, ReMQB6Protocol.DATA_TYPE_BACK_RADAR_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_FRONT_RADAR_INFO, ReMQB6Protocol.DATA_TYPE_FRONT_RADAR_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_WHEEL_INFO, ReMQB6Protocol.DATA_TYPE_WHEEL_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_PARKASSIST_INFO, ReMQB6Protocol.DATA_TYPE_PARKASSIST_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_VERSION_INFO, ReMQB6Protocol.DATA_TYPE_VERSION_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_BACK_LIGHT_INFO, ReMQB6Protocol.DATA_TYPE_BACK_LIGHT_INFO);
		super.RegisterProxy(ReMQB6Protocol.DATA_TYPE_CAR_INFO, ReMQB6Protocol.DATA_TYPE_CAR_INFO);
	}

	public void deInit(){
		
		super.deInit();
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
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
			} else if (iCmdID == mObjProtocol.GetWheelInfoCmdId()) {
				CanInfo.i(TAG, "Recevice can Wheel Message!");
				mObjWheelInfo = mObjProtocol.parseWheelInfo(packet, mObjWheelInfo);
				objUImsg.what = DDef.WHEEL_CMD_ID;
				objUImsg.obj = mObjWheelInfo;
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
		case DDef.CARINFO_CMD_ID:
			obj = mObjCarInfo;	
			break;
		default:
			break;
		}
		
		return obj;
	}
}
