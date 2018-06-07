package com.can.parser.raise.nissan;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;
import com.can.tool.CanInfo;

/**
 * ClassName:ReNissanParser
 * 
 * @function:睿志城日产协议解析
 * @author Kim
 * @Date:  2016-6-4 上午10:19:07
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReNissanParser extends CanProxy{

	private Handler mObjHandler;
	private ReNissanProtocol mObjProtocol;
	
	private WheelKeyInfo  mObjWheelKeyInfo;
	
	public ReNissanParser() {
		// TODO Auto-generated constructor stub
		mObjWheelKeyInfo  = new WheelKeyInfo();
	}
	
	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		// TODO Auto-generated method stub
		super.start(null, context, name, protocol);
		
		this.mObjHandler  = handler;
		this.mObjProtocol = (ReNissanProtocol)protocol;
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		// TODO Auto-generated method stub
		super.RegisterProxy(ReNissanProtocol.DATA_TYPE_WHEEL_KEY_INFO, ReNissanProtocol.DATA_TYPE_WHEEL_KEY_INFO);
		super.RegisterProxy(ReNissanProtocol.DATA_TYPE_VERSION_INFO, ReNissanProtocol.DATA_TYPE_VERSION_INFO);
		super.RegisterProxy(ReNissanProtocol.DATA_TYPE_MEDIA_SRC_INFO, ReNissanProtocol.DATA_TYPE_MEDIA_SRC_INFO);
		super.RegisterProxy(ReNissanProtocol.DATA_TYPE_INCOMING_INFO, ReNissanProtocol.DATA_TYPE_INCOMING_INFO);
	}

	@Override
	public void deInit() {
		// TODO Auto-generated method stub
		super.deInit();
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case CanTxRxStub.MSG_CAN_RX:
			byte[] packet = CanTxRxStub.getRxPacket(msg);

			if (packet != null) {
				// 回复Ack
				byte[] ack = { (byte) 0xFF };
				sendMsg2Can(CanTxRxStub.getTxMessage(0, 0, ack, null));
			}
			
			int iCmdID = mObjProtocol.ParseRegisterCmdId(packet, 0);
			Message objUImsg = mObjHandler.obtainMessage();
			
			if (iCmdID == mObjProtocol.GetWheelKeyInfoCmdId()) {
				CanInfo.i(TAG, "Recevice can Key Message!");

				mObjWheelKeyInfo = mObjProtocol.parseWheelKeyInfo(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mObjHandler.sendMessage(objUImsg);
			}else if (iCmdID == mObjProtocol.GetMediaSourceCmdId()) {
				CanInfo.i(TAG, "Recevice can Key Message!");

				mObjWheelKeyInfo = mObjProtocol.parseMediaSource(packet, 
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mObjHandler.sendMessage(objUImsg);
			}else if (iCmdID == mObjProtocol.GetPhoneActionCmdId()) {
				CanInfo.i(TAG, "Recevice can Key Message!");

				mObjWheelKeyInfo = mObjProtocol.parsePhoneState(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
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
