package com.can.parser.raise.dz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CarService;
import com.can.parser.DDef.CarTimeInfo;
import com.can.parser.DDef.ControlEnable;
import com.can.parser.DDef.ConvConsumers;
import com.can.parser.DDef.DrivingData;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;
import com.can.tool.CanInfo;
import com.can.tool.DataConvert;

/**
 * ClassName:ReMT7Parser
 * 
 * @function:睿志城大众MT7平台协议解析器
 * @author Kim
 * @Date:  2016-5-26 上午11:54:49
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReMQB7Parser extends CanProxy{
	
	private Handler 	   mObjHandler;
	private ReMQB7Protocol mObjProtocol;
	
	private AirInfo   	  mObjAirInfo;
	private CarInfo   	  mObjCarInfo;
	private RadarInfo 	  mObjRadarInfo;
	private BaseInfo  	  mObjBaseInfo;
	private WheelInfo 	  mObjWheelInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	
	private CarTimeInfo  	mObjCarTimeInfo;
	private OutTemputerInfo mObjEnviTemp;
	private ControlEnable   mObjCtrlEnable;
	private DrivingData		mObjDrivingData;
	private ConvConsumers   mObjConvConsumers;
	private CarService		mObjCarService;
	private TPMSInfo		mObjTpmsInfo;

	public ReMQB7Parser() {
		// TODO Auto-generated constructor stub
		mObjCarInfo       = new CarInfo();
		mObjRadarInfo     = new RadarInfo();
		mObjAirInfo       = new AirInfo();
		mObjBaseInfo      = new BaseInfo();
		mObjWheelInfo     = new WheelInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
		
		mObjCarTimeInfo   = new CarTimeInfo();
		mObjEnviTemp      = new OutTemputerInfo();
		mObjCtrlEnable    = new ControlEnable();
		mObjDrivingData   = new DrivingData();
		mObjConvConsumers = new ConvConsumers();
		mObjCarService    = new CarService();
		mObjTpmsInfo      = new TPMSInfo();
	}
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		// TODO Auto-generated method stub
		super.start(null, context, name, protocol);
		
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReMQB7Protocol)protocol;
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		// TODO Auto-generated method stub
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_AIR_INFO, ReMQB7Protocol.DATA_TYPE_AIR_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_BASE_INFO, ReMQB7Protocol.DATA_TYPE_BASE_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_WHEEL_KEY_INFO, ReMQB7Protocol.DATA_TYPE_WHEEL_KEY_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_BACK_RADAR_INFO, ReMQB7Protocol.DATA_TYPE_BACK_RADAR_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_FRONT_RADAR_INFO, ReMQB7Protocol.DATA_TYPE_FRONT_RADAR_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_WHEEL_INFO, ReMQB7Protocol.DATA_TYPE_WHEEL_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_PARKASSIST_INFO, ReMQB7Protocol.DATA_TYPE_PARKASSIST_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_VERSION_INFO, ReMQB7Protocol.DATA_TYPE_VERSION_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_BACK_LIGHT_INFO, ReMQB7Protocol.DATA_TYPE_BACK_LIGHT_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_CAR_INFO, ReMQB7Protocol.DATA_TYPE_CAR_INFO);
		
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_CAR_TIME_INFO, ReMQB7Protocol.DATA_TYPE_CAR_TIME_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_ENVIRONMENT_TEMP_INFO, ReMQB7Protocol.DATA_TYPE_ENVIRONMENT_TEMP_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_CAR_STATE_INFO, ReMQB7Protocol.DATA_TYPE_CAR_STATE_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_CONTROL_MAKE, ReMQB7Protocol.DATA_TYPE_CAR_STATE_INFO);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_DRIVING_DATA, ReMQB7Protocol.DATA_TYPE_DRIVING_DATA);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_CONV_CONSUMERS, ReMQB7Protocol.DATA_TYPE_CONV_CONSUMERS);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_SERVICES, ReMQB7Protocol.DATA_TYPE_SERVICES);
		super.RegisterProxy(ReMQB7Protocol.DATA_TYPE_TPMS, ReMQB7Protocol.DATA_TYPE_TPMS);
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
				objUImsg.what = 6;
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
			} else if (iCmdID == mObjProtocol.GetCarTimeInfoCmdId()) {
				
				mObjCarTimeInfo = mObjProtocol.parseCarTimeInfo(packet, mObjCarTimeInfo);
				objUImsg.what = DDef.CAR_TIME_INFO;
				objUImsg.obj = mObjCarTimeInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetCarEnviTempCmdId()) {
				
				mObjEnviTemp = mObjProtocol.parseOutTempInfo(packet, mObjEnviTemp);
				objUImsg.what = DDef.OUT_TEMP_ID;
				objUImsg.obj = mObjEnviTemp;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetControlEnableCmdId()) {
				
				mObjCtrlEnable = mObjProtocol.parseControlEnable(packet, mObjCtrlEnable);
				objUImsg.what = DDef.CONTROLENABLE_INFO;
				objUImsg.obj = mObjCtrlEnable;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetDrivingDataCmdId()) {
				
				mObjDrivingData = mObjProtocol.parseDrivingData(packet, mObjDrivingData);
				objUImsg.what = DDef.DRIVING_DATA;
				objUImsg.obj = mObjDrivingData;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetConvConsumersCmdId()) {
				
				mObjConvConsumers = mObjProtocol.parseConvConsumers(packet, mObjConvConsumers);
				objUImsg.what = DDef.CONV_CONSUMERS;
				objUImsg.obj = mObjConvConsumers;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetCarServiceCmdId()) {
				
				mObjCarService = mObjProtocol.parseCarService(packet, mObjCarService);
				objUImsg.what = DDef.CAR_SERVICE;
				objUImsg.obj = mObjCarService;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetTpmsInfoCmdId()) {
				
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
		case DDef.DRIVING_DATA:
			mObjDrivingData.mbyPageId = (byte) iVal;
			obj = mObjDrivingData;
			break;
		}
		
		return obj;
	}
}
