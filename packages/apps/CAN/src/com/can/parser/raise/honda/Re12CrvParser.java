package com.can.parser.raise.honda;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.can.parser.DDef;
import com.can.parser.DDef.CompassInfo;
import com.can.parser.DDef.UsbIpodInfo;
import com.can.parser.Protocol;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;

public class Re12CrvParser extends CanProxy{
	private Handler    	   mObjHandler;
	private Re12CrvProtocol mObjProtocol;
	
	private BaseInfo  	  mObjBaseInfo;
	private WheelKeyInfo  mObjWheelKeyInfo;
	private BackLightInfo mObjBackLightInfo;
	private CompassInfo	  mObjCompassInfo;
	private UsbIpodInfo	  mObjUsbIpodInfo;
	
	public Re12CrvParser() {
		mObjBaseInfo      = new BaseInfo();
		mObjWheelKeyInfo  = new WheelKeyInfo();
		mObjBackLightInfo = new BackLightInfo();
		mObjCompassInfo	  = new CompassInfo();
		mObjUsbIpodInfo   = new UsbIpodInfo();
	}	
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		super.start(null, context, name, protocol);
		this.mObjHandler  = handler;
		this.mObjProtocol = (Re12CrvProtocol)protocol;
	}
	
	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		if (eCmdType == E_CMD_TYPE.eCmd_Type_PopWind || eCmdType == E_CMD_TYPE.eCmd_Type_BackCar) {
			
			super.RegisterProxy(Re12CrvProtocol.DATA_TYPE_BASE_INFO, Re12CrvProtocol.DATA_TYPE_BASE_INFO);
			super.RegisterProxy(Re12CrvProtocol.DATA_TYPE_WHEEL_KEY_INFO, Re12CrvProtocol.DATA_TYPE_WHEEL_KEY_INFO);
			super.RegisterProxy(Re12CrvProtocol.DATA_TYPE_BACK_LIGHT_INFO, Re12CrvProtocol.DATA_TYPE_BACK_LIGHT_INFO);
			super.RegisterProxy(Re12CrvProtocol.DATA_TYPE_BACK_LIGHT_INFO, Re12CrvProtocol.DATA_TYPE_BACK_LIGHT_INFO);
			super.RegisterProxy(Re12CrvProtocol.DATA_TYPE_COMPASS_INFO, Re12CrvProtocol.DATA_TYPE_COMPASS_INFO);
			super.RegisterProxy(Re12CrvProtocol.DATA_TYPE_USB_IPOD_INFO, Re12CrvProtocol.DATA_TYPE_USB_IPOD_INFO);
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

				Log.i(TAG, "Recevice BaseInfo Message!");
				mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
				objUImsg.what = DDef.BASE_CMD_ID;
				objUImsg.obj = mObjBaseInfo;
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
			} else if (iCmdID == Re12CrvProtocol.DATA_TYPE_COMPASS_INFO) {
				Log.i(TAG, "Recevice compass info Message!");
				mObjCompassInfo = mObjProtocol.parseCompassInfo(packet, mObjCompassInfo);
				objUImsg.what = DDef.COMPASS_CMD_ID;
				objUImsg.obj = mObjCompassInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == Re12CrvProtocol.DATA_TYPE_USB_IPOD_INFO) {
				Log.i(TAG, "Recevice compass info Message!");
				mObjUsbIpodInfo = mObjProtocol.parseUsbIpodInfo(packet, mObjUsbIpodInfo);
				objUImsg.what = DDef.USB_IPOD_INFO;
				objUImsg.obj = mObjUsbIpodInfo;
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
		case DDef.COMPASS_CMD_ID:
			object = mObjCompassInfo;
			break;
		default:
			break;
		}
		return object;
	}

}
