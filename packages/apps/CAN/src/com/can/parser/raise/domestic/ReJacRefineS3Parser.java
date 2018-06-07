package com.can.parser.raise.domestic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.can.parser.DDef;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.DDef.TpmsWarn;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;

/**
 * ClassName:ReJacRefineS3Parser
 * 
 * @function:江淮瑞风S3
 * @author Kim
 * @Date:  2016-7-23下午2:12:13
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReJacRefineS3Parser extends CanProxy{

	private Handler mObjHandler;
	private ReJacRefineS3Protocol mObjProtocol;

	private TPMSInfo mObjTpmsInfo;
	private BaseInfo mObjBaseInfo;
	private TpmsWarn mObjTpmsWarnInfo;
	private WheelKeyInfo mObjWheelKeyInfo;
	
	public ReJacRefineS3Parser() {
		// TODO Auto-generated constructor stub
		mObjTpmsInfo = new TPMSInfo();
		mObjBaseInfo = new BaseInfo();
		mObjTpmsWarnInfo = new TpmsWarn();
		mObjWheelKeyInfo = new WheelKeyInfo();
	}

	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		// TODO Auto-generated method stub
		super.start(null, context, name, protocol);

		this.mObjHandler = handler;
		this.mObjProtocol = (ReJacRefineS3Protocol) protocol;
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		// TODO Auto-generated method stub
		super.RegisterProxy(ReJacRefineS3Protocol.DATA_TYPE_TPMS_INFO,
				ReJacRefineS3Protocol.DATA_TYPE_TPMS_INFO);
		super.RegisterProxy(ReJacRefineS3Protocol.DATA_TYPE_BASE_INFO,
				ReJacRefineS3Protocol.DATA_TYPE_BASE_INFO);
		super.RegisterProxy(ReJacRefineS3Protocol.DATA_TYPE_WHEEL_KEY_INFO,
				ReJacRefineS3Protocol.DATA_TYPE_WHEEL_KEY_INFO);
		super.RegisterProxy(ReJacRefineS3Protocol.DATA_TYPE_TPMS_WARN,
				ReJacRefineS3Protocol.DATA_TYPE_TPMS_WARN);
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

			if (iCmdID == mObjProtocol.GetBaseInfoCmdId()) {

				mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
				objUImsg.what = DDef.BASE_CMD_ID;
				objUImsg.obj = mObjBaseInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetWheelKeyInfoCmdId()) {

				mObjWheelKeyInfo = mObjProtocol.parseWheelKeyInfo(packet,
						mObjWheelKeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelKeyInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetTpmsInfoCmdId()) {

				mObjTpmsInfo = mObjProtocol.parseTpmsInfo(packet,
						mObjTpmsInfo);
				objUImsg.what = DDef.TPMS_CMD_ID;
				objUImsg.obj = mObjTpmsInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetTpmsWarnCmdId()) {

				mObjTpmsWarnInfo = mObjProtocol.parseTpmsWarn(packet,
						mObjTpmsWarnInfo);
				objUImsg.what = DDef.SYSINFO_CMD_ID;
				objUImsg.obj = mObjTpmsWarnInfo;
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
		objUImsg.obj = mObjProtocol;
		mObjHandler.sendMessage(objUImsg);
	}

	@Override
	public Object getData(int iwhat, int iVal) {
		// TODO Auto-generated method stub
		Object obj = null;
		switch (iwhat) {
		case DDef.TPMS_CMD_ID:
			obj = mObjTpmsInfo;
			break;
		default:
			break;
		}
		return obj;
	}
}
