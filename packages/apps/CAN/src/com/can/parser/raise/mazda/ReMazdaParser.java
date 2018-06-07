package com.can.parser.raise.mazda;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.can.parser.DDef.AirInfo;
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
 * ClassName:ReMazdaParser
 * 
 * @function:睿志城马自达系列
 * @author Kim
 * @Date:  2016-7-16上午9:13:50
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReMazdaParser extends CanProxy{

	private Handler    	    mObjHandler;
	private ReMazdaProtocol mObjProtocol;
	
	private AirInfo   	   mObjAirInfo;
	private RadarInfo 	   mObjRadarInfo;
	private WheelInfo 	   mObjWheelInfo;
	private WheelKeyInfo   mObjWheelKeyInfo;
	private PowerAmplifier mObjDspInfo;
	
	public ReMazdaParser() {
		// TODO Auto-generated constructor stub
		mObjRadarInfo     = new RadarInfo();
		mObjAirInfo       = new AirInfo();
		mObjWheelInfo     = new WheelInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjDspInfo       = new PowerAmplifier();
	}
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		// TODO Auto-generated method stub
		super.start(null, context, name, protocol);
		
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReMazdaProtocol) protocol;
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		// TODO Auto-generated method stub
		super.RegisterProxy(ReMazdaProtocol.DATA_TYPE_AIR_INFO1, ReMazdaProtocol.DATA_TYPE_AIR_INFO1);
		super.RegisterProxy(ReMazdaProtocol.DATA_TYPE_AIR_INFO2, ReMazdaProtocol.DATA_TYPE_AIR_INFO2);
		super.RegisterProxy(ReMazdaProtocol.DATA_TYPE_PANEL_KEY_INFO, ReMazdaProtocol.DATA_TYPE_PANEL_KEY_INFO);
		super.RegisterProxy(ReMazdaProtocol.DATA_TYPE_WHEEL_KEY_INFO, ReMazdaProtocol.DATA_TYPE_WHEEL_KEY_INFO);
		super.RegisterProxy(ReMazdaProtocol.DATA_TYPE_RADAR_INFO, ReMazdaProtocol.DATA_TYPE_RADAR_INFO);
		super.RegisterProxy(ReMazdaProtocol.DATA_TYPE_WHEEL_INFO, ReMazdaProtocol.DATA_TYPE_WHEEL_INFO);
		super.RegisterProxy(ReMazdaProtocol.DATA_TYPE_DSP_INFO, ReMazdaProtocol.DATA_TYPE_DSP_INFO);
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

			int iCmdID = mObjProtocol.ParseRegisterCmdId(packet, 0);
			Message objUImsg = mObjHandler.obtainMessage();

			if (iCmdID == mObjProtocol.GetAirInfoCmdId()) {
				
				mObjAirInfo = mObjProtocol.parseAirInfo(packet, mObjAirInfo);

				objUImsg.what = DDef.AIR_CMD_ID;
				objUImsg.obj = mObjAirInfo;
				mObjHandler.sendMessage(objUImsg);

			} else if (iCmdID == mObjProtocol.GetAirInfo1CmdId()) {
				
				mObjAirInfo = mObjProtocol.parseAirInfo1(packet, mObjAirInfo);
 
				objUImsg.what = DDef.AIR_CMD_ID;
				objUImsg.obj = mObjAirInfo;
				mObjHandler.sendMessage(objUImsg);
				
			} else if (iCmdID == mObjProtocol.GetBackRadarInfoCmdId()) {

				mObjRadarInfo = mObjProtocol.parseRadarInfo(packet, mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
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

			} else if (iCmdID == mObjProtocol.GetPanelKeyInfoCmdId()) {
				
				mObjWheelKeyInfo = mObjProtocol.parsePanelKeyInfo(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetDspInfoCmdId()) {

				mObjDspInfo = mObjProtocol.parseDspInfo(packet,
						mObjDspInfo);
				objUImsg.what = DDef.DSP_CMD_ID;
				objUImsg.obj = mObjDspInfo;
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
		return null;
	}
}
