package com.can.services;

import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.tool.CanInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

/**
 * ClassName:CanTxRxStub
 * 
 * @function:Rx Tx 数据与Message之间的转换
 * @author Kim
 * @Date: 2016-5-26 下午4:10:00
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanTxRxStub {

	public static final int MSG_CAN_TX = 1;
	public static final int MSG_CAN_RX = 2;
	public static final int MSG_CAN_REG_USER = 3;
	public static final int MSG_CAN_UREG_USER = 4;
	public static final int MSG_CAN_SET_PROTOCOL = 5;
	public static final int MSG_CAN_GET_DATA = 6;

	public static final String BUND_CAN_TX = "Msg_Can_Tx";
	public static final String BUND_CAN_RX = "Msg_Can_Rx";
	public static final String REGISTER_USER = "Msg_Can_Reg_User";
	public static final String UREGISTER_USER = "Msg_Can_Reg_User";
	
	public static final String CAN_MODE = "Can_Mode";
	public static final String CAN_PHONE_MODE = "Can_Phone_Mode";
	public static final String CAN_SOURCE_MODE = "Can_Source_Mode";

	public static final String ACTION_CAN_SERVICE = "yecon.intent.CanService";
	public static final String ACTION_CAN_UI_SERVICE = "yecon.intent.CanUIService";

	/**
	 * 
	 * <p>
	 * Title: getRxMessage
	 * </p>
	 * <p>
	 * Description: 将Rx数据打包2Message
	 * </p>
	 * 
	 * @param packet
	 * @param objprotocol
	 * @return
	 */
	public static Message getRxMessage(byte[] packet, Protocol objprotocol) {

		Bundle bundle = new Bundle();
		bundle.putByteArray(BUND_CAN_RX, packet);

		CanInfo.Rx(packet);

		Message msg = Message.obtain(null, MSG_CAN_RX, 0, 0);
		msg.arg1 = objprotocol.ParseRegisterCmdId(packet, 0);
		msg.arg2 = objprotocol.parseRegisterSubId(packet, 0);
		msg.setData(bundle);

		return msg;
	};

	/**
	 * 
	 * <p>
	 * Title: getRxPacket
	 * </p>
	 * <p>
	 * Description: 将msg 解包2 byte[]
	 * </p>
	 * 
	 * @param msg
	 * @return
	 */
	public static byte[] getRxPacket(Message msg) {

		Bundle bundle = msg.getData();
		byte[] packet = bundle.getByteArray(BUND_CAN_RX);

		return packet;
	};

	/**
	 * 
	 * <p>
	 * Title: getTxMessage
	 * </p>
	 * <p>
	 * Description: 将Tx数据打包2Msg
	 * </p>
	 * 
	 * @param cmdID
	 * @param subCmdID
	 * @param datas
	 * @param protocol
	 * @return
	 */
	public static Message getTxMessage(int cmdID, int subCmdID, byte[] datas,
			Protocol protocol) {

		Bundle bundle = new Bundle();

		if (datas == null) {
			return null;
		}

		if (protocol != null) {

			byte[] pakcet = protocol.getPacket(cmdID, subCmdID, datas);

			bundle.putByteArray(BUND_CAN_TX, pakcet);

		} else {

			bundle.putByteArray(BUND_CAN_TX, datas);
		}

		Message msg = Message.obtain(null, MSG_CAN_TX, 0, 0);
		msg.setData(bundle);

		return msg;
	}
	/**
	 * 
	* <p>Title: getTxMessage</p>
	* <p>Description: </p>
	* @param message
	* @return
	 */
	public static Message getTxMessage(Message message){
		
		Message msg = Message.obtain(null, MSG_CAN_TX);
		msg.setData(message.getData());
		
		return msg;
	}
	/**
	 * 
	 * <p>
	 * Title: getTxPacket
	 * </p>
	 * <p>
	 * Description: 将Msg解包2 byte[]
	 * </p>
	 * 
	 * @param msg
	 * @return
	 */
	public static byte[] getTxPacket(Message msg) {
		Bundle bundle = msg.getData();
		byte[] packet = bundle.getByteArray(BUND_CAN_TX);

		return packet;
	}

	/**
	 * 
	 * <p>
	 * Title: getRegisterMessage
	 * </p>
	 * <p>
	 * Description: 将注册信息打包2 Msg
	 * </p>
	 * 
	 * @param cmdID
	 * @param subCmdID
	 * @param name
	 * @param replyTo
	 * @return
	 */
	public static Message getRegisterMessage(int cmdID, int subCmdID,
			String name, Messenger replyTo) {

		Message msg = Message.obtain(null, MSG_CAN_REG_USER, cmdID, subCmdID);
		msg.replyTo = replyTo;

		Bundle bundle = new Bundle();
		bundle.putString(REGISTER_USER, name);
		msg.setData(bundle);

		return msg;
	}

	/**
	 * 
	 * <p>
	 * Title: getRegisterMessage
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param name
	 * @param replyTo
	 * @return
	 */
	public static Message getRegisterMessage(String name, Messenger replyTo) {

		Message msg = Message.obtain(null, MSG_CAN_REG_USER, 0, 0);
		msg.replyTo = replyTo;

		Bundle bundle = new Bundle();
		bundle.putString(REGISTER_USER, name);
		msg.setData(bundle);

		return msg;
	}

	/**
	 * 
	 * <p>
	 * Title: getDeregisterMessage
	 * </p>
	 * <p>
	 * Description: 将注销信息打包成msg
	 * </p>
	 * 
	 * @param cmdID
	 * @param subCmdID
	 * @param name
	 * @param replyTo
	 * @return
	 */
	public static Message getDeregisterMessage(int cmdID, int subCmdID,
			String name, Messenger replyTo) {

		Message msg = Message.obtain(null, MSG_CAN_UREG_USER, cmdID, subCmdID);
		msg.replyTo = replyTo;

		Bundle bundle = new Bundle();
		bundle.putString(UREGISTER_USER, name);
		msg.setData(bundle);

		return msg;
	}
	/**
	 * 
	* <p>Title: getDeregisterMessage</p>
	* <p>Description: </p>
	* @param name
	* @param replyTo
	* @return
	 */
	public static Message getDeregisterMessage(String name, Messenger replyTo) {

		Message msg = Message.obtain(null, MSG_CAN_UREG_USER, 0, 0);
		msg.replyTo = replyTo;

		Bundle bundle = new Bundle();
		bundle.putString(UREGISTER_USER, name);
		msg.setData(bundle);

		return msg;
	}

	/**
	 * 
	 * <p>
	 * Title: getProtocolMessage
	 * </p>
	 * <p>
	 * Description: 将协议对象打包成Msg
	 * </p>
	 * 
	 * @param protocol
	 * @return
	 */
	public static Message getProtocolMessage(Protocol protocol) {

		Message msg = Message.obtain(null, MSG_CAN_SET_PROTOCOL);
		msg.obj = protocol;

		return msg;
	}
	/**
	 * 
	* <p>Title: getProtocol</p>
	* <p>Description: </p>
	* @param protocol
	* @return
	 */
	public static Message getProtocol(Protocol protocol) {

		Message msg = Message.obtain(null, DDef.FINISH_BIND);
		msg.obj = protocol;

		return msg;
	}
	/**
	 * 
	* <p>Title: getdataMessage</p>
	* <p>Description: </p>
	* @param messenger
	* @return
	 */
	public static Message getdataMessage(int type, Messenger messenger){
		
		Message msg = Message.obtain(null, MSG_CAN_GET_DATA);
		msg.arg1    = type;
		msg.replyTo = messenger;
		
		return msg;
	}
	/**
	 * 
	* <p>Title: getdataMessage</p>
	* <p>Description: </p>
	* @param type
	* @param iVal
	* @param messenger
	* @return
	 */
	public static Message getdataMessage(int type, int iVal, Messenger messenger){
		
		Message msg = Message.obtain(null, MSG_CAN_GET_DATA);
		msg.arg1    = type;
		msg.arg2    = iVal;
		msg.replyTo = messenger;
		
		return msg;
	}
	/**
	 * 
	* <p>Title: getdataMessage</p>
	* <p>Description: </p>
	* @param type
	* @param obj
	* @return
	 */
	public static Message getdataMessage(int type, Object obj){
		
		Message msg = Message.obtain(null, type);
		msg.obj = obj;
		
		return msg;
	}
}
