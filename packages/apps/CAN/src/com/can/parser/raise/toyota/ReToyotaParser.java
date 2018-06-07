package com.can.parser.raise.toyota;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CurOilInfo;
import com.can.parser.DDef.IntantOilInfo;
import com.can.parser.DDef.OilVatteryInfo;
import com.can.parser.DDef.PowerAmplifier;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.SetInfo;
import com.can.parser.DDef.SystemInfo;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.DDef.VehicleSettings;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.services.CanTxRxStub;
import com.can.tool.CanInfo;

/**
 * ClassName:ReToyotaParser
 * 
 * @function:丰田解析
 * @author Kim
 * @Date: 2016-6-4 上午10:27:13
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReToyotaParser extends CanProxy {

	private Handler mObjHandler;
	private ReToyotaProtocol mObjProtocol;

	private AirInfo mObjAirInfo;
	private CarInfo mObjCarInfo;
	private RadarInfo mObjRadarInfo;
	private BaseInfo mObjBaseInfo;
	private TPMSInfo mObjTpmsInfo;
	private WheelInfo mObjWheelInfo;
	private WheelKeyInfo mObjWheelkeyInfo;
	private VehicleSettings mObjVehicleSet;
	private OilVatteryInfo mObjOilVatteryInfo;
	private PowerAmplifier mObjPowerAmp;
	private SystemInfo mObjsystemInfo;
	private IntantOilInfo mObjIntantOilInfo;
	private CurOilInfo mObjCurOilInfo;
	private SetInfo mObjSetInfo;

	public ReToyotaParser() {
		// TODO Auto-generated constructor stub
		mObjAirInfo = new AirInfo();
		mObjCarInfo = new CarInfo();
		mObjRadarInfo = new RadarInfo();
		mObjBaseInfo = new BaseInfo();
		mObjTpmsInfo = new TPMSInfo();
		mObjWheelInfo = new WheelInfo();
		mObjWheelkeyInfo = new WheelKeyInfo();
		mObjVehicleSet = new VehicleSettings();
		mObjOilVatteryInfo = new OilVatteryInfo();
		mObjPowerAmp = new PowerAmplifier();
		mObjsystemInfo = new SystemInfo();
		mObjIntantOilInfo = new IntantOilInfo();
		mObjCurOilInfo = new CurOilInfo();
		mObjSetInfo = new SetInfo();
	}

	@Override
	public void Init(E_CMD_TYPE eCmdType) {
		// TODO Auto-generated method stub
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_WHEEL_KEY_INFO,
				ReToyotaProtocol.DATA_TYPE_WHEEL_KEY_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_AIR_INFO,
				ReToyotaProtocol.DATA_TYPE_AIR_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_WHEEL_INFO,
				ReToyotaProtocol.DATA_TYPE_WHEEL_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_VERSION_INFO,
				ReToyotaProtocol.DATA_TYPE_VERSION_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_BACK_RADAR_INFO,
				ReToyotaProtocol.DATA_TYPE_BACK_RADAR_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_FRONT_RADAR_INFO,
				ReToyotaProtocol.DATA_TYPE_FRONT_RADAR_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_BASE_INFO,
				ReToyotaProtocol.DATA_TYPE_BASE_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_VEHICLE_SETTINGS,
				ReToyotaProtocol.DATA_TYPE_VEHICLE_SETTINGS);

		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_CAR_INFO,
				ReToyotaProtocol.DATA_TYPE_CAR_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_SETTINGS_INFO,
				ReToyotaProtocol.DATA_TYPE_SETTINGS_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_BACK_RADAR_INFO,
				ReToyotaProtocol.DATA_TYPE_BACK_RADAR_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_FRONT_RADAR_INFO,
				ReToyotaProtocol.DATA_TYPE_FRONT_RADAR_INFO);
		super.RegisterProxy(
				ReToyotaProtocol.DATA_TYPE_TRIP_INFORMATION_MIN_OIL,
				ReToyotaProtocol.DATA_TYPE_TRIP_INFORMATION_MIN_OIL);
		super.RegisterProxy(
				ReToyotaProtocol.DATA_TYPE_TRIP_INFORMATION_INSTANT_OIL,
				ReToyotaProtocol.DATA_TYPE_TRIP_INFORMATION_INSTANT_OIL);

		super.RegisterProxy(
				ReToyotaProtocol.DATA_TYPE_TRIP_INFORMATION_15MIN_OIL,
				ReToyotaProtocol.DATA_TYPE_TRIP_INFORMATION_15MIN_OIL);
		super.RegisterProxy(
				ReToyotaProtocol.DATA_TYPE_TRIP_INFORMATION_HISTORY_OIL,
				ReToyotaProtocol.DATA_TYPE_TRIP_INFORMATION_HISTORY_OIL);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_TPMS_INFO,
				ReToyotaProtocol.DATA_TYPE_TPMS_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_OILELE_FALG,
				ReToyotaProtocol.DATA_TYPE_OILELE_FALG);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_FUNC_INFO,
				ReToyotaProtocol.DATA_TYPE_FUNC_INFO);
		super.RegisterProxy(ReToyotaProtocol.DATA_TYPE_PANAL_KEY,
				ReToyotaProtocol.DATA_TYPE_PANAL_KEY);
	}

	@Override
	public void start(Handler handler, Context context, String name,
			Protocol protocol) {
		// TODO Auto-generated method stub
		super.start(null, context, name, protocol);

		this.mObjHandler = handler;
		this.mObjProtocol = (ReToyotaProtocol) protocol;
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

				mObjWheelkeyInfo = mObjProtocol.parseWheelKeyInfo(packet,
						mObjWheelkeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelkeyInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetAirInfoCmdId()) {
				CanInfo.i(TAG, "Recevice Air Message %s!");
				mObjAirInfo = mObjProtocol.parseAirInfo(packet, mObjAirInfo);

				objUImsg.what = DDef.AIR_CMD_ID;
				objUImsg.obj = mObjAirInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetBaseInfoCmdId()) {
				CanInfo.i(TAG, "Recevice BaseInfo Message!");
				mObjBaseInfo = mObjProtocol.parseBaseInfo(packet, mObjBaseInfo);
				objUImsg.what = DDef.BASE_CMD_ID;
				objUImsg.obj = mObjBaseInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetWheelInfoCmdId()) {
				CanInfo.i(TAG, "Recevice WheelInfo Message!");
				mObjWheelInfo = mObjProtocol.parseWheelInfo(packet,
						mObjWheelInfo);
				objUImsg.what = DDef.WHEEL_CMD_ID;
				objUImsg.obj = mObjWheelInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetTripInfoMinOilCmdId()) {
				CanInfo.i(TAG, "Recevice TripInfoMinOil Message!");
				mObjCarInfo = mObjProtocol.parseTripInfoMinOil(packet,
						mObjCarInfo);
				objUImsg.what = DDef.TRIP_INFO_MINOIL;
				objUImsg.obj = mObjCarInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetTripInfoInstantOilCmdId()) {
				CanInfo.i(TAG, "Recevice TripInfoInstantOil Message!");
				mObjIntantOilInfo = mObjProtocol.parseTripInfoInstantOil(
						packet, mObjIntantOilInfo);
				objUImsg.what = DDef.INTANTOIL_CMD_ID;
				objUImsg.obj = mObjIntantOilInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetTripInfoHistoryOilCmdId()) {
				CanInfo.i(TAG, "Recevice TripInfoHistoryOil Message!");
				mObjCarInfo = mObjProtocol.parseTripInfoHistoryOil(packet,
						mObjCarInfo);
				objUImsg.what = DDef.TRIP_INFO_HISOIL;
				objUImsg.obj = mObjCarInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetTPMSCmdId()) {
				CanInfo.i(TAG, "Recevice TPMSInfo Message!");
				mObjTpmsInfo = mObjProtocol.parseTPMSInfo(packet, mObjTpmsInfo);
				objUImsg.what = DDef.TPMS_CMD_ID;
				objUImsg.obj = mObjTpmsInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetTripInfo15MinOilCmdId()) {
				CanInfo.i(TAG, "Recevice TripInfo15MinOil Message!");
				mObjCurOilInfo = mObjProtocol.parseTripInformation15MinOil(
						packet, mObjCurOilInfo);
				objUImsg.what = DDef.CUROIL_CMD_ID;
				objUImsg.obj = mObjCurOilInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetPowerAmplifierCmdId()) {
				CanInfo.i(TAG, "Recevice PowerAmplifier Message!");
				mObjPowerAmp = mObjProtocol.parsePowerAmplifier(packet,
						mObjPowerAmp);
				objUImsg.what = DDef.DSP_CMD_ID;
				objUImsg.obj = mObjPowerAmp;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetSystemInfoCmdId()) {
				CanInfo.i(TAG, "Recevice SystemInfo Message!");
				mObjsystemInfo = mObjProtocol.parseSystemInfo(packet,
						mObjsystemInfo);
				objUImsg.what = DDef.SYSINFO_CMD_ID;
				objUImsg.obj = mObjsystemInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetOilEleInfoCmdId()) {
				CanInfo.i(TAG, "Recevice OilEleInfo Message!");
				mObjOilVatteryInfo = mObjProtocol.parseOilVatteryInfo(packet,
						mObjOilVatteryInfo);
				objUImsg.what = DDef.SYSINFO_CMD_ID;
				objUImsg.obj = mObjOilVatteryInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetBackRadarInfoCmdId()) {
				CanInfo.i(TAG, "Recevice BackRadarInfo Message!");
				mObjRadarInfo = mObjProtocol.parseRadarInfo(packet,
						mObjRadarInfo);
				objUImsg.what = DDef.RADAR_CMD_ID;
				objUImsg.obj = mObjRadarInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetVehicleSettingsCmdId()) {
				CanInfo.i(TAG, "Recevice VehicleSettings Message!");
				mObjVehicleSet = mObjProtocol.parseVehicleSettingsInfo(packet,
						mObjVehicleSet);
				objUImsg.what = DDef.CAR_SET_CMD_ID;
				objUImsg.obj = mObjVehicleSet;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetCarInfoCmdId()) {
				CanInfo.i(TAG, "Recevice CarInfo Message!");
				mObjCarInfo = mObjProtocol.parseCarInfo(packet, mObjCarInfo);
				objUImsg.what = DDef.CARINFO_CMD_ID;
				objUImsg.obj = mObjCarInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetSettingInfo()) {
				CanInfo.i(TAG, "Recevice setinfo Message!");
				mObjSetInfo = mObjProtocol.parseSetInfo(packet, mObjSetInfo);
				objUImsg.what = DDef.SET_INFO_CMD_ID;
				objUImsg.obj = mObjSetInfo;
				mObjHandler.sendMessage(objUImsg);
			} else if (iCmdID == mObjProtocol.GetPanelkeyId()) {
				CanInfo.i(TAG, "Recevice Panel Key Message!");

				mObjWheelkeyInfo = mObjProtocol.parsePanelkey(packet,
						mObjWheelkeyInfo);
				objUImsg.what = DDef.CANKEY_CMD_ID;
				objUImsg.obj = mObjWheelkeyInfo;
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
		case DDef.CAR_SET_CMD_ID:
			obj = mObjVehicleSet;
			break;
		case DDef.TRIP_INFO_MINOIL:
			obj = mObjCarInfo;
			break;
		case DDef.INTANTOIL_CMD_ID:
			obj = mObjIntantOilInfo;
			break;
		case DDef.CUROIL_CMD_ID:
			obj = mObjCurOilInfo;
			break;
		case DDef.TRIP_INFO_HISOIL:
			obj = mObjCarInfo;
			break;
		case DDef.TPMS_CMD_ID:
			obj = mObjTpmsInfo;
			break;
		case DDef.SET_INFO_CMD_ID:
			obj = mObjSetInfo;
			break;
		case DDef.RADAR_CMD_ID:
			obj = mObjRadarInfo;
			break;
		case DDef.SYSINFO_CMD_ID:
			obj = mObjOilVatteryInfo;
			break;
		case DDef.DSP_CMD_ID:
			obj = mObjPowerAmp;
			break;
		default:
			break;
		}
		return obj;
	}
}
