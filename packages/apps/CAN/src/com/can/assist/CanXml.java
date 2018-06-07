package com.can.assist;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.can.activity.R;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.Protocol;
import com.can.services.CanProxy;
import com.can.ui.CanFrament;
import com.yecon.metazone.YeconMetazone;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.util.Log;

/**
 * ClassName:CanXml
 * 
 * @function:解析车型Xml数据集合 根据车型数据创建功能对象
 * @author Kim
 * @Date: 2016-5-30 上午11:56:14
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanXml {

	protected final static String TAG = "CanXml";

	/**
	 * 
	 * ClassName:CarType_Info
	 * 
	 * @function:车型数据属性
	 * @author Kim
	 * @Date: 2016-6-3 下午3:05:43
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	static public class CarType_Info {
		public String strBoxName = null;
		public int iBoxId = 0;
		public int iBoxBand = 0;
		public String strSeriesName = "";
		public int iSeriesId = 0;
		public String strTypeName = "";
		public int iTypeId = 0;
		public String strCfgName = "";
		public int iCfgId = 0;
		public int iNeedIcon = 0;
		public int iAudioPort = 0;
		public String strProClass = "";
		public String strParserClass = "";
		public String strUIClass = "";
		public String strAudioClass = "";
		public String strPopClass = "";
	}

	/**
	 * 
	 * ClassName:CAN_DESCRIBE
	 * 
	 * @function:车型数据结构
	 * @author Kim
	 * @Date: 2016-6-3 下午3:06:21
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	static public class CAN_DESCRIBE {
		public int iBoxID = 0;
		public int iSeriesID = 0;
		public int iCarTypeID = 0;
		public int iConfigID = 0;
	}
	
	public static enum E_Update_Type{
		eUpdate_Type_CanBox,
		eUpdate_Type_CanSeries,
		eUpdate_Type_CanType,
		eUpdate_Type_CanCfg,
		eUpdate_Type_None,
	}

	public static ArrayList<CarType_Info> mArrayList = null;

	public CanXml() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @Title: parseCanxml   
	 * @Description: TODO 
	 * @param context      
	 * @return: void
	 */
	public static void parseCanxml(Context context){
		pullXmlData(context.getResources().getXml(R.xml.cartype));
	}
	
	/**
	 * 
	 * <p>
	 * Title: create
	 * </p>
	 * <p>
	 * Description: 动态创建协议解析器
	 * </p>
	 * 
	 * @param handler
	 * @param context
	 * @param strName
	 * @param eCmdType
	 * @return
	 */
	public static CanProxy create(Handler handler, Context context,
			String strName, E_CMD_TYPE eCmdType) {

		CanProxy ObjCanProxy = null;

		Object ObjectProxy = null;
		Object objectProl = null;
		Class<?> ObjClParser = null;
		Class<?> objClProtocol = null;
		Constructor<?> objCrParser = null;
		Constructor<?> objCrProtocol = null;

		XmlResourceParser xmlParser = context.getResources().getXml(
				R.xml.cartype);

		if (pullXmlData(xmlParser)) {

			CarType_Info carTypeInfo = getAttr(getCanDescribe());

			if (carTypeInfo == null) {
				Log.e(TAG, "carTypeInfo is empty!");
			} else if (carTypeInfo.strParserClass.isEmpty()) {
				Log.i(TAG, "strParserClass is empty!");
			}else if (carTypeInfo.strProClass.isEmpty()) {
				Log.i(TAG, "strProClass is empty!");
			}else {
				try {
					objClProtocol = Class.forName(carTypeInfo.strProClass
							.toString());
					objCrProtocol = objClProtocol.getDeclaredConstructor();
					objectProl = objCrProtocol.newInstance();

					ObjClParser = Class.forName(carTypeInfo.strParserClass
							.toString());
					objCrParser = ObjClParser.getConstructor();
					ObjectProxy = objCrParser.newInstance();

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (ObjectProxy != null) {

				ObjCanProxy = (CanProxy) ObjectProxy;

				ObjCanProxy.Init(eCmdType);
				ObjCanProxy.start(handler, context, strName,
						(Protocol) objectProl);
			}
		}

		return ObjCanProxy;
	}

	/**
	 * 
	 * <p>
	 * Title: create
	 * </p>
	 * <p>
	 * Description: 动态创建Page
	 * </p>
	 * 
	 * @param context
	 * @return
	 */
	public static CanFrament create(Context context) {
		CanFrament objFragment = null;

		Object objectPage = null;

		XmlResourceParser xmlParser = context.getResources().getXml(
				R.xml.cartype);

		if (pullXmlData(xmlParser)) {

			CarType_Info carTypeInfo = getAttr(getCanDescribe());

			if (carTypeInfo == null) {
				Log.e(TAG, "carTypeInfo is empty!");
			} else if (carTypeInfo.strUIClass.isEmpty()) {
				Log.i(TAG, "strUIClass is empty!");
			}else {
				try {
					Class<?> objClass = Class.forName(carTypeInfo.strUIClass
							.toString());
					Constructor<?> objConstructor = objClass
							.getDeclaredConstructor();
					objectPage = objConstructor.newInstance();

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (objectPage != null) {
					objFragment = (CanFrament) objectPage;
				}
			}
		}

		return objFragment;
	}
	/**
	 * 
	* Title: getAudio
	* Description: 获取CanAudio的Frament
	* @param  context
	* @return CanFrament
	* 
	 */
	public static CanFrament getAudio(Context context){
		
		CanFrament objFragment = null;
		Object objectPage = null;

		XmlResourceParser xmlParser = context.getResources().getXml(
				R.xml.cartype);

		if (pullXmlData(xmlParser)) {

			CarType_Info carTypeInfo = getAttr(getCanDescribe());

			if (carTypeInfo == null) {
				Log.e(TAG, "carTypeInfo is empty!");
			} else if (carTypeInfo.strAudioClass.isEmpty()) {
				Log.i(TAG, "strAudioClass is empty!");
			}else {
				try {
					Class<?> objClass = Class.forName(carTypeInfo.strAudioClass
							.toString());
					Constructor<?> objConstructor = objClass
							.getDeclaredConstructor();
					objectPage = objConstructor.newInstance();

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (objectPage != null) {
					objFragment = (CanFrament) objectPage;
				}
			}
		}

		return objFragment;
	}
	
	public static CanFrament getPopFrament(Context context){
		
		CanFrament objFragment = null;
		Object objectPage = null;

		XmlResourceParser xmlParser = context.getResources().getXml(
				R.xml.cartype);

		if (pullXmlData(xmlParser)) {

			CarType_Info carTypeInfo = getAttr(getCanDescribe());

			if (carTypeInfo == null) {
				Log.e(TAG, "carTypeInfo is empty!");
			} else if (carTypeInfo.strPopClass.isEmpty()) {
				Log.i(TAG, "strPopClass is empty!");
			}else {
				try {
					Class<?> objClass = Class.forName(carTypeInfo.strPopClass
							.toString());
					Constructor<?> objConstructor = objClass
							.getDeclaredConstructor();
					objectPage = objConstructor.newInstance();

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (objectPage != null) {
					objFragment = (CanFrament) objectPage;
				}
			}
		}

		return objFragment;
	}
	/**
	 * 
	 * @Title: getAudioProt   
	 * @Description: TODO 
	 * @return      
	 * @return: int
	 */
	public static int getAudioProt(Context context){
		
		CarType_Info carTypeInfo = getAttr(getCanDescribe());
		
		if (carTypeInfo == null) {
			pullXmlData(context.getResources().getXml(R.xml.cartype));
			carTypeInfo = getAttr(getCanDescribe());
		}
		
		return carTypeInfo.iAudioPort;
	}
	/**
	 * 
	* <p>Title: getInfo</p>
	* <p>Description: 获取车型属性信息</p>
	* @param context
	* @return
	 */
	public static CarType_Info getInfo(Context context) {
		CarType_Info carTypeInfo = null;
		XmlResourceParser xmlParser = context.getResources().getXml(
				R.xml.cartype);

		if (pullXmlData(xmlParser)) {

			carTypeInfo = getAttr(getCanDescribe());
		}

		return carTypeInfo;
	}
	/**
	 * 
	* <p>Title: getCanboxlist</p>
	* <p>Description: </p>
	* @return
	 */
	public static ArrayList<String> getCanboxlist(){
		
		ArrayList<String> arrayList = new ArrayList<String>();
		
		if (mArrayList.isEmpty()) {
			
			Log.e(TAG, "CarTypeInfo Arraylist is empty!");
		}else {
			Iterator<CarType_Info> iter = mArrayList.iterator();
			while (iter.hasNext()) {

				CarType_Info carTypeInfo = iter.next();

				if (arrayList.isEmpty()) {
					
					arrayList.add(carTypeInfo.strBoxName);
				}
				else {
					
					boolean bIsequals = false;
					
					for (String strBoxName : arrayList) {
						bIsequals = strBoxName.equals(carTypeInfo.strBoxName);
					}
					
					if (bIsequals == false) {
						
						arrayList.add(carTypeInfo.strBoxName);
					}
				}
			}
		}
		
		return arrayList;
	}
	/**
	 * 
	* <p>Title: getCanSeries</p>
	* <p>Description: </p>
	* @param strCanbox
	* @return
	 */
	public static ArrayList<String> getCanSeries(String strCanbox){
		
		ArrayList<String> arrayList = new ArrayList<String>();
		
		if (mArrayList.isEmpty()) {
			
			Log.e(TAG, "CarTypeInfo Arraylist is empty!");
		}else {
			Iterator<CarType_Info> iter = mArrayList.iterator();
			while (iter.hasNext()) {

				boolean bIsequals = false;
				CarType_Info carTypeInfo = iter.next();

				bIsequals = carTypeInfo.strBoxName.equals(strCanbox);
				
				if (bIsequals) {
					
					if (arrayList.isEmpty()) {
						arrayList.add(carTypeInfo.strSeriesName);
					}
					else {
						
						boolean bIsequalSeries = false;
						
						for (String strName : arrayList) {
							bIsequalSeries = strName.equals(carTypeInfo.strSeriesName);
						}
						
						if (bIsequalSeries == false) {
							arrayList.add(carTypeInfo.strSeriesName);
						}
					}
				}
			}
		}
		
		return arrayList;
	}
	/**
	 * 
	* <p>Title: getCanType</p>
	* <p>Description: </p>
	* @param strBox
	* @param strSeries
	* @return
	 */
	public static ArrayList<String> getCanType(String strBox, String strSeries){
		ArrayList<String> arrayList = new ArrayList<String>();
		
		if (mArrayList.isEmpty()) {
			
			Log.e(TAG, "CarTypeInfo Arraylist is empty!");
		}else {
			Iterator<CarType_Info> iter = mArrayList.iterator();
			while (iter.hasNext()) {

				CarType_Info carTypeInfo = iter.next();
				
				boolean bIsequalbox = false;
				boolean bIsequalSeries = carTypeInfo.strSeriesName.equals(strSeries);
				
				bIsequalbox = carTypeInfo.strBoxName.equals(strBox);
				
				if (bIsequalbox && bIsequalSeries) {
					
					if (arrayList.isEmpty()) {
						arrayList.add(carTypeInfo.strTypeName);
					}
					else {
						
						boolean bIsequalType = false;
						
						for (String strName : arrayList) {
							bIsequalType = strName.equals(carTypeInfo.strTypeName);
						}
						
						if (bIsequalType == false) {
							arrayList.add(carTypeInfo.strTypeName);
						}
					}
				}
			}
		}
		
		return arrayList;
	}
	/**
	 * 
	* <p>Title: getCanCfg</p>
	* <p>Description: </p>
	* @return
	 */
	public static ArrayList<String> getCanCfg(){
		ArrayList<String> arrayList = new ArrayList<String>();
		
		if (mArrayList.isEmpty()) {
			
			Log.e(TAG, "CarTypeInfo Arraylist is empty!");
		}else {
			Iterator<CarType_Info> iter = mArrayList.iterator();
			while (iter.hasNext()) {

				CarType_Info carTypeInfo = iter.next();

				if (arrayList.isEmpty()) {
					arrayList.add(carTypeInfo.strCfgName);
				}
				else {
					
					boolean bIsequals = false;
					
					for (String strCfgName : arrayList) {
						bIsequals = strCfgName.equals(carTypeInfo.strCfgName);
					}
					
					if (bIsequals == false) {
						arrayList.add(carTypeInfo.strCfgName);
					}
				}
			}
		}
		
		return arrayList;
	}
	
	/**
	 * 
	 * <p>
	 * Title: getCanDescribe
	 * </p>
	 * <p>
	 * Description: 获取车型信息
	 * </p>
	 * 
	 * @return
	 */
	public static CAN_DESCRIBE getCanDescribe() {
		
		CAN_DESCRIBE objCandDescribe = new CAN_DESCRIBE();
		objCandDescribe.iBoxID     = YeconMetazone.GetCanBox();
		objCandDescribe.iSeriesID  = YeconMetazone.GetCarLine();
		objCandDescribe.iCarTypeID = YeconMetazone.GetCarType();
		objCandDescribe.iConfigID  = YeconMetazone.GetCarCfg();
		
		return objCandDescribe;
	}
	/**
	 * 
	* <p>Title: setCanDescribe</p>
	* <p>Description: </p>
	* @param carTypeInfo
	* @return
	 */
	public static boolean setCanDescribe(CarType_Info carTypeInfo){
		boolean bRet = true;
		
		if (carTypeInfo != null) {
			
			YeconMetazone.SetCanBox(carTypeInfo.iBoxId);
			YeconMetazone.SetCarLine(carTypeInfo.iSeriesId);
			YeconMetazone.SetCarType(carTypeInfo.iTypeId);
			YeconMetazone.SetCarCfg(carTypeInfo.iCfgId);
			YeconMetazone.SetCanBoxUartBaudrate(carTypeInfo.iBoxBand);
			
		}else {
			bRet = false;
		}
		
		return bRet;
	}

	/**
	 * 
	 * <p>
	 * Title: getAttr
	 * </p>
	 * <p>
	 * Description: 根据车型信息获取其属性
	 * </p>
	 * 
	 * @param canDescribe
	 * @return
	 */
	public static CarType_Info getAttr(CAN_DESCRIBE canDescribe) {

		CarType_Info ObjcarTypeInfo = null;

		if (mArrayList.isEmpty()) {
			Log.e(TAG, "CarTypeInfo Arraylist is empty!");
		} else {

			Iterator<CarType_Info> iter = mArrayList.iterator();
			while (iter.hasNext()) {

				CarType_Info carTypeInfo = iter.next();

				if (carTypeInfo.iBoxId == canDescribe.iBoxID
						&& carTypeInfo.iSeriesId == canDescribe.iSeriesID
						&& carTypeInfo.iTypeId == canDescribe.iCarTypeID) {
					ObjcarTypeInfo = carTypeInfo;
				}
			}
		}

		return ObjcarTypeInfo;
	}
	/**
	 * 
	* <p>Title: getAttr</p>
	* <p>Description: </p>
	* @param eType
	* @param strVal1
	* @param strVal2
	* @param strVal3
	* @return
	 */
	public static CarType_Info getAttr(E_Update_Type eType, String strVal1, String strVal2, String strVal3){
		CarType_Info objcarTypeInfo = new CarType_Info();
		
		if (mArrayList.isEmpty()) {
			Log.e(TAG, "CarTypeInfo Arraylist is empty!");
		} else {

			Iterator<CarType_Info> iter = mArrayList.iterator();
			while (iter.hasNext()) {

				CarType_Info carTypeInfo = iter.next();

				if (eType == E_Update_Type.eUpdate_Type_CanBox) {
					if (strVal1 != null) {
						if (carTypeInfo.strBoxName.equals(strVal1)){
							objcarTypeInfo = carTypeInfo;
							break;
						}
					}
				}else if (eType == E_Update_Type.eUpdate_Type_CanSeries) {
					if ((strVal1 != null) && (strVal2 != null)) {
						if (carTypeInfo.strBoxName.equals(strVal1) && carTypeInfo.strSeriesName.equals(strVal2)) {
							objcarTypeInfo = carTypeInfo;
							break;
						}
					}
				}else if (eType == E_Update_Type.eUpdate_Type_CanType) {
					if ((strVal1 != null) && (strVal2 != null) && (strVal3 != null)) {
						if (carTypeInfo.strBoxName.equals(strVal1) && carTypeInfo.strSeriesName.equals(strVal2) && carTypeInfo.strTypeName.equals(strVal3)) {
							objcarTypeInfo = carTypeInfo;
							break;
						}
					}
				}
			}
		}
		
		return objcarTypeInfo;
	}
	
	/**
	 * 
	* <p>Title: getAttr</p>
	* <p>Description: </p>
	* @param strVal1
	* @param strVal2
	* @param strVal3
	* @param strVal4
	* @return
	 */
	public static CarType_Info getAttr(String strVal1, String strVal2, String strVal3, String strVal4){
		CarType_Info objCarTypeInfo = null;
		
		if (mArrayList.isEmpty()) {
			Log.e(TAG, "CarTypeInfo Arraylist is empty!");
		} else {

			Iterator<CarType_Info> iter = mArrayList.iterator();
			while (iter.hasNext()) {

				CarType_Info carTypeInfo = iter.next();

				if (carTypeInfo.strBoxName.equals(strVal1) &&
					carTypeInfo.strSeriesName.equals(strVal2)	&&
					carTypeInfo.strTypeName.equals(strVal3) &&
					carTypeInfo.strCfgName.equals(strVal4)) {
					objCarTypeInfo = carTypeInfo;
				}
			}
		}

		return objCarTypeInfo;
	}

	/**
	 * 
	 * <p>
	 * Title: pullXmlData
	 * </p>
	 * <p>
	 * Description: 解析CarType.xml add到list
	 * </p>
	 * 
	 * @param xml
	 * @return
	 */
	private static boolean pullXmlData(XmlResourceParser xml) {
		boolean bRet = false;
		CarType_Info ObjCarTypeInfo = null;

		try {

			int iEventType = xml.getEventType();
			while (iEventType != XmlPullParser.END_DOCUMENT) {

				switch (iEventType) {
				case XmlPullParser.START_DOCUMENT:
					mArrayList = new ArrayList<CanXml.CarType_Info>();
					break;
				case XmlPullParser.START_TAG:
					if ("Field".equals(xml.getName())) {
						ObjCarTypeInfo = new CarType_Info();
					}

					if (ObjCarTypeInfo != null) {
						if ("Item".equals(xml.getName())) {

							String strAttr = xml.getAttributeValue(0);

							if (strAttr.equals("box")) {
								ObjCarTypeInfo.strBoxName = xml.nextText();
							} else if (strAttr.equals("BoxId")) {
								ObjCarTypeInfo.iBoxId = Integer.parseInt(xml
										.nextText());
							} else if (strAttr.equals("Baud")) {
								ObjCarTypeInfo.iBoxBand = Integer.parseInt(xml
										.nextText());
							} else if (strAttr.equals("Series")) {
								ObjCarTypeInfo.strSeriesName = xml.nextText();
							} else if (strAttr.equals("SeriesId")) {
								ObjCarTypeInfo.iSeriesId = Integer.parseInt(xml
										.nextText());
							} else if (strAttr.equals("Type")) {
								ObjCarTypeInfo.strTypeName = xml.nextText();
							} else if (strAttr.equals("TypeId")) {
								ObjCarTypeInfo.iTypeId = Integer.parseInt(xml
										.nextText());
							} else if (strAttr.equals("Cfg")) {
								ObjCarTypeInfo.strCfgName = xml.nextText();
							} else if (strAttr.equals("CfgId")) {
								ObjCarTypeInfo.iCfgId = Integer.parseInt(xml
										.nextText());
							} else if (strAttr.equals("NeedIcon")) {
								ObjCarTypeInfo.iNeedIcon = Integer.parseInt(xml
										.nextText());
							} else if (strAttr.equals("ProlClass")) {
								ObjCarTypeInfo.strProClass = xml.nextText();
							} else if (strAttr.equals("UIClass")) {
								ObjCarTypeInfo.strUIClass = xml.nextText();
							} else if (strAttr.equals("PserClass")) {
								ObjCarTypeInfo.strParserClass = xml.nextText();
							} else if (strAttr.equals("AudClass")) {
								ObjCarTypeInfo.strAudioClass = xml.nextText();
							} else if (strAttr.equals("PopClass")) {
								ObjCarTypeInfo.strPopClass = xml.nextText();
							} else if (strAttr.equals("AudioPort")) {
								ObjCarTypeInfo.iAudioPort = Integer.parseInt(xml
										.nextText());
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Field".equals(xml.getName())) {
						mArrayList.add(ObjCarTypeInfo);
						ObjCarTypeInfo = null;
						bRet = true;
					}
					break;
				default:
					break;
				}

				iEventType = xml.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			xml.close();
		}

		return bRet;
	}
}
