package com.can.parser.raise.jeep;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CDState;
import com.can.parser.DDef.CDTxInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CompassInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.PowerAmplifier;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;

/**
 * ClassName:ReJeepParser
 * 
 * @function:Jeep自由光解析器
 * @author Kim
 * @Date:  2016-7-14上午9:33:38
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReJeepParser extends CanProxy{
	
	private Handler    	   mObjHandler;
	private ReJeepProtocol mObjProtocol;
	
	private AirInfo   	  mObjAirInfo;
	private CarInfo   	  mObjCarInfo;
	private RadarInfo 	  mObjRadarInfo;
	private BaseInfo  	  mObjBaseInfo;
	private WheelInfo 	  mObjWheelInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	
	private PowerAmplifier mObjDspInfo;
	private CompassInfo    mObjCompassInfo;
	private CDState        mObjCdState;
	private CDTxInfo       mObjCdTxInfo;
	
	public ReJeepParser() {
		// TODO Auto-generated constructor stub
		mObjCarInfo       = new CarInfo();
		mObjRadarInfo     = new RadarInfo();
		mObjAirInfo       = new AirInfo();
		mObjBaseInfo      = new BaseInfo();
		mObjWheelInfo     = new WheelInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
		
		mObjDspInfo       = new PowerAmplifier();
		mObjCompassInfo   = new CompassInfo();
		mObjCdState       = new CDState();
		mObjCdTxInfo      = new CDTxInfo();
	}
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		// TODO Auto-generated method stub
		super.start(null, context, name, protocol);
		
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReJeepProtocol) protocol;
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		// TODO Auto-generated method stub
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_AIR_INFO, ReJeepProtocol.DATA_TYPE_AIR_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_BASE_INFO, ReJeepProtocol.DATA_TYPE_BASE_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_WHEEL_KEY_INFO, ReJeepProtocol.DATA_TYPE_WHEEL_KEY_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_BACK_RADAR_INFO, ReJeepProtocol.DATA_TYPE_BACK_RADAR_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_FRONT_RADAR_INFO, ReJeepProtocol.DATA_TYPE_FRONT_RADAR_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_WHEEL_INFO, ReJeepProtocol.DATA_TYPE_WHEEL_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_VERSION_INFO, ReJeepProtocol.DATA_TYPE_VERSION_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_BACK_LIGHT_INFO, ReJeepProtocol.DATA_TYPE_BACK_LIGHT_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_CAR_INFO, ReJeepProtocol.DATA_TYPE_CAR_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_PANEL_KEY_INFO, ReJeepProtocol.DATA_TYPE_PANEL_KEY_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_DSP_INFO, ReJeepProtocol.DATA_TYPE_DSP_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_COMPASS_INFO, ReJeepProtocol.DATA_TYPE_COMPASS_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_CD_STATE_INFO, ReJeepProtocol.DATA_TYPE_CD_STATE_INFO);
		super.RegisterProxy(ReJeepProtocol.DATA_TYPE_CD_TX_INFO, ReJeepProtocol.DATA_TYPE_CD_TX_INFO);
	}
	
	@Override
	public void deInit() {
		// TODO Auto-generated method stub
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
				
				mObjAirInfo = mObjProtocol.parseAirInfo(packet, mObjAirInfo);

				objUImsg.what = DDef.AIR_CMD_ID;
				objUImsg.obj = mObjAirInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetCarInfoCmdId()) {
				
				mObjCarInfo = mObjProtocol.parseCarInfo(packet, mObjCarInfo);
				objUImsg.what = DDef.CARINFO_CMD_ID;
				objUImsg.obj = mObjCarInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetBackRadarInfoCmdId()
					|| iCmdID == mObjProtocol.GetFrontRadarInfoCmdId()) {

				mObjRadarInfo = mObjProtocol.parseRadarInfo(packet, mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetBaseInfoCmdId()) {

				mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
				objUImsg.what = DDef.BASE_CMD_ID;
				objUImsg.obj = mObjBaseInfo;
				mObjHandler.sendMessage(objUImsg);

			}  else if (iCmdID == mObjProtocol.GetWheelInfoCmdId()) {

				mObjWheelInfo = mObjProtocol.parseWheelInfo(packet, mObjWheelInfo);
				objUImsg.what = DDef.WHEEL_CMD_ID;
				objUImsg.obj = mObjWheelInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetWheelKeyInfoCmdId()) {

				mObjWheelKeyInfo = mObjProtocol.parseWheelKeyInfo(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetPanelKeyCmdId()) {
				
				mObjWheelKeyInfo = mObjProtocol.parsePanelKeyInfo(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mObjHandler.sendMessage(objUImsg);
			}else if (iCmdID == mObjProtocol.GetBackLightInfoCmdId()) {
				
				mObjBackLightInfo = mObjProtocol.parseBackLightInfo(packet,
						mObjBackLightInfo);
				objUImsg.what = DDef.LIGHT_CMD_ID;
				objUImsg.obj = mObjBackLightInfo;
				mObjHandler.sendMessage(objUImsg);
			}else if (iCmdID == mObjProtocol.GetDspInfoCmdId()) {
				
				mObjDspInfo = mObjProtocol.parseDspInfo(packet,
						mObjDspInfo);
				objUImsg.what = DDef.DSP_CMD_ID;
				objUImsg.obj = mObjDspInfo;
				mObjHandler.sendMessage(objUImsg);
			}else if (iCmdID == mObjProtocol.GetCompassCmdId()) {
				
				mObjCompassInfo = mObjProtocol.parseCompassInfo(packet,
						mObjCompassInfo);
				objUImsg.what = DDef.COMPASS_CMD_ID;
				objUImsg.obj = mObjCompassInfo;
				mObjHandler.sendMessage(objUImsg);
			}else if (iCmdID == mObjProtocol.GetCDStateCmdId()) {
				
				mObjCdState = mObjProtocol.parseCDStateInfo(packet,
						mObjCdState);
				objUImsg.what = DDef.CD_STATE_ID;
				objUImsg.obj = mObjCdState;
				mObjHandler.sendMessage(objUImsg);
			}else if (iCmdID == mObjProtocol.GetCDTxCmdId()) {
				
				mObjCdTxInfo = mObjProtocol.parseCdTxInfo(packet,
						mObjCdTxInfo);
				objUImsg.what = DDef.CD_TX_INFO_ID;
				objUImsg.obj = mObjCdTxInfo;
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
		case DDef.DSP_CMD_ID:
			obj = mObjDspInfo;
			break;

		default:
			break;
		}
		return obj;
	}

}
