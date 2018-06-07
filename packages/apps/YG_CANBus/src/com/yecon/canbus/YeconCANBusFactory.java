/**
 * @Title: CANBusParseFactory.java
 * @Package com.yecon.canbus
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午2:00:55
 * @version V1.0
 */
package com.yecon.canbus;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yecon.canbus.info.CANBusConstants;
import com.yecon.canbus.parse.CANBusParse;
import com.yecon.canbus.raise.ford.FordCIActivity;
import com.yecon.canbus.raise.ford.RaiseFordParse;
import com.yecon.canbus.raise.gm.GMCIActivity;
import com.yecon.canbus.raise.gm.RaiseGMParse;
import com.yecon.canbus.raise.honda.HondaCIActivity;
import com.yecon.canbus.raise.honda.HondaCamryCIActivity;
import com.yecon.canbus.raise.honda.RaiseHondaCamryParse;
import com.yecon.canbus.raise.honda.RaiseHondaParse;
import com.yecon.canbus.raise.hyundai.HyundaiCIActivity;
import com.yecon.canbus.raise.hyundai.RaiseHyundaiParse;
import com.yecon.canbus.raise.mazda.MazdaCIActivity;
import com.yecon.canbus.raise.mazda.RaiseGC7Parse;
import com.yecon.canbus.raise.mazda.RaiseMazdaParse;
import com.yecon.canbus.raise.peugeot.PeugeotCIActivity;
import com.yecon.canbus.raise.peugeot.RaisePeugeotParse;
import com.yecon.canbus.raise.vw.RaiseVWGolf7Parse;
import com.yecon.canbus.raise.vw.RaiseVWParse;
import com.yecon.canbus.raise.vw.VWCIActivity;

/**
 * @ClassName: YeconCANBusFactory
 * @Description: 创建CANBUS解析器
 * @author hzGuo
 * @date 2016年4月15日 下午2:00:55
 *
 */
public class YeconCANBusFactory {
	public static CANBusParse CreateCanbusParse(int iCanbusType) {
		CANBusParse parse = null;
		switch (iCanbusType) {
		case CANBusConstants.CANBUS_TYPE_FORD:
			parse = new RaiseFordParse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_HONDA:
			parse = new RaiseHondaParse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_HONDA_CAMRY:
			parse = new RaiseHondaCamryParse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_GM:
			parse = new RaiseGMParse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_VW:
			parse = new RaiseVWParse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_VW_GOLF7:
			parse = new RaiseVWGolf7Parse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_PEUGEOT:
			parse = new RaisePeugeotParse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_MAZDA:
			parse = new RaiseMazdaParse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_HYUNDAI:
			parse = new RaiseHyundaiParse();
			break;
			
		case CANBusConstants.CANBUS_TYPE_GC7:
			parse = new RaiseGC7Parse();
			break;

		default:
			Log.e("YeconCANBusFactory", "Invalide Canbus type " + iCanbusType);
			break;
		}
		return parse;
	}
	
	public static void CreateCanbusUI(Context content, int iCanbusType)
	{
		Intent intent = null;
		switch (iCanbusType) {
		case CANBusConstants.CANBUS_TYPE_FORD:
			intent = new Intent(content, FordCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_HONDA:
			intent = new Intent(content, HondaCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_HONDA_CAMRY:
			intent = new Intent(content, HondaCamryCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_GM:
			intent = new Intent(content, GMCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_VW:
			intent = new Intent(content, VWCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_VW_GOLF7:
			intent = new Intent(content, VWCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_PEUGEOT:
			intent = new Intent(content, PeugeotCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_MAZDA:
			intent = new Intent(content, MazdaCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_HYUNDAI:
			intent = new Intent(content, HyundaiCIActivity.class);
			break;
			
		case CANBusConstants.CANBUS_TYPE_GC7:
			intent = new Intent(content, MazdaCIActivity.class);
			break;

		default:
			Log.e("YeconCANBusFactory", "Invalide Canbus type " + iCanbusType);
			break;
		}
		if (intent != null) {
			content.startActivity(intent);
		}
	}
}
