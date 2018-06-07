package com.can.assist;

import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.can.activity.R;
import com.can.parser.DDef;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * ClassName:CanKey
 * 
 * @function:Can协议按键处理
 * @author Kim
 * @Date: 2016-5-30 下午2:50:18
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanKey {

	private OnCanKeyListener mCanKeyListener;
	private CANKEY_INFO mObjCanKeyInfo = null;
	private static long mIntervalTime = 0;
	private static long mInitTcikCount = 0;
	private static long mlClickNum = 0;
	private static long mDelayMillis = 100;
	public static final byte KEY_UP = 0x00;
	public static final byte KEY_DOWN = 0x01;
	public static final byte KEY_LONG = 0x02;
	public static final byte KEY_SERIES = 0x03;
	public static final byte KEY_COMPLEX = 0x04;
	public static final byte KEY_KNOB = 0x05;
	public static final byte KEY_REPEAT = 0x06;
	public static final int KEY_CAN = 0x321;
	protected final String TAG = this.getClass().getName();
	private HashMap<String, Integer> mkeyMap = new HashMap<String, Integer>();

	private enum E_CANKEY_ACTION {
		eCanKey_Action_Invalid, eCanKey_Action_valid, eCanKey_Action_Complex, eCanKey_Action_Send, eCankey_Action_Knob, eCankey_Action_Repeat
	}

	public CanKey(Context context) {
		// TODO Auto-generated constructor stub
		parserkeyxml(context.getResources().getXml(R.xml.key));
	}

	private Handler mObjhHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case KEY_CAN:
				parser(msg);
				break;
			}
		}
	};

	private class CANKEY_INFO {
		public String strKeyCode = "";
		public int iKeyState = 0x00;
		public int iKnobStep = 0x00;
		public byte byRepeat = 0x00;
		public boolean bInternal = false;
	}

	public void sendCankey(String strKeyCode, int iState, int iSteps, byte byRepeat, boolean bInternal) {

		CANKEY_INFO objCanInfo = new CANKEY_INFO();

		objCanInfo.strKeyCode = strKeyCode;
		objCanInfo.iKeyState = iState;
		objCanInfo.iKnobStep = iSteps;
		objCanInfo.byRepeat  = byRepeat;
		objCanInfo.bInternal = bInternal;

		Message msg = Message.obtain(null, KEY_CAN);
		msg.obj = objCanInfo;

		mObjhHandler.sendMessage(msg);
	}

	private void parser(Message msg) {

		CANKEY_INFO objCanKeyInfo = (CANKEY_INFO) msg.obj;
		E_CANKEY_ACTION eAction = getIsAction(objCanKeyInfo);

		if ((objCanKeyInfo.byRepeat == KEY_REPEAT)
				&& eAction == E_CANKEY_ACTION.eCanKey_Action_valid) {
			eAction = E_CANKEY_ACTION.eCankey_Action_Repeat;
		}

		if (eAction == E_CANKEY_ACTION.eCanKey_Action_valid
				|| eAction == E_CANKEY_ACTION.eCanKey_Action_Complex) {

			if (objCanKeyInfo.bInternal && mCanKeyListener != null) {
				mCanKeyListener.ondo(objCanKeyInfo.strKeyCode, true);
			} else {
				sendKey(objCanKeyInfo.strKeyCode);
			}
		} else if (eAction == E_CANKEY_ACTION.eCanKey_Action_Send
				|| eAction == E_CANKEY_ACTION.eCankey_Action_Repeat) {

			if (mCanKeyListener != null) {
				mCanKeyListener.ondo(objCanKeyInfo.strKeyCode, false);
			}
		} else if (eAction == E_CANKEY_ACTION.eCankey_Action_Knob) {
			mObjhHandler.post(runnable);
		}
	}

	private boolean IsComplexKey(String strKeycode) {
		boolean bRet = false;

		if (strKeycode != null) {
			if (strKeycode.equals(DDef.Volume_add)
					|| strKeycode.equals(DDef.Volume_del)
					|| strKeycode.equals(DDef.Src_home)
					|| strKeycode.equals(DDef.Src_mode)
					|| strKeycode.equals(DDef.Media_next)
					|| strKeycode.equals(DDef.Media_pre)
					|| strKeycode.equals(DDef.Volume_mute)) {
				bRet = true;
			}
		}

		return bRet;
	}

	private E_CANKEY_ACTION getIsAction(CANKEY_INFO cankeyInfo) {

		E_CANKEY_ACTION eAction = E_CANKEY_ACTION.eCanKey_Action_Invalid;

		if (IsComplexKey(cankeyInfo.strKeyCode)) {
			switch (cankeyInfo.iKeyState) {
			case KEY_DOWN:
				mObjCanKeyInfo = cankeyInfo;
				Log.i(TAG, "+++++++++++++++++++++++CanKey Down:"
						+ cankeyInfo.strKeyCode);

				if (0 == mIntervalTime) {

					// 按下开始计数
					mIntervalTime = System.currentTimeMillis();
				}
				break;
			case KEY_UP:
				if (mObjCanKeyInfo != null) {

					mInitTcikCount = 0;
					mObjCanKeyInfo = null;
					eAction = E_CANKEY_ACTION.eCanKey_Action_valid;
					Log.i(TAG, "+++++++++++++++++++++++CanKey Up:"
							+ cankeyInfo.strKeyCode);
				} else {
					Log.e(TAG, "The last time no down press！");
				}
				
				mlClickNum = 0;
				mIntervalTime = 0;
				break;
			case KEY_LONG:
				if (System.currentTimeMillis() - mIntervalTime >= 1000) {
					// 按下1s不做长按处理
					if (mInitTcikCount == 0) {
						// 初始计数
						mInitTcikCount = System.currentTimeMillis();
						eAction = E_CANKEY_ACTION.eCanKey_Action_valid;
					} else {
						if (System.currentTimeMillis() - mInitTcikCount >= 50) {
							// 长按50ms处理一次
							eAction = E_CANKEY_ACTION.eCanKey_Action_valid;
						}
					}
				}

				if (eAction == E_CANKEY_ACTION.eCanKey_Action_valid) {
					Log.i(TAG, "+++++++++++++++++++++++CanKey Long:"
							+ cankeyInfo.strKeyCode);
				}
				break;
			case KEY_SERIES:
				if (mlClickNum++ > 3) {
					mlClickNum = 0;
					mObjCanKeyInfo = null;
					eAction = E_CANKEY_ACTION.eCanKey_Action_Send;
				}
				break;
			case KEY_COMPLEX:
				if (mlClickNum++ > 3) {
					mlClickNum = 0;
					mObjCanKeyInfo = null;
					eAction = E_CANKEY_ACTION.eCanKey_Action_Complex;
				}
				break;
			case KEY_KNOB:
				if (cankeyInfo.iKnobStep > 0) {
					mObjCanKeyInfo = cankeyInfo;
					eAction = E_CANKEY_ACTION.eCankey_Action_Knob;
				}
				break;
			}
		}else {
			eAction = (cankeyInfo.iKeyState == 0x01) ? E_CANKEY_ACTION.eCanKey_Action_valid : E_CANKEY_ACTION.eCanKey_Action_Invalid;
		}
		
		return eAction;
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (mObjCanKeyInfo != null) {
				if (mObjCanKeyInfo.iKnobStep > 0) {
					--mObjCanKeyInfo.iKnobStep;
					sendKey(mObjCanKeyInfo.strKeyCode);
				}
			}

			mObjhHandler.postDelayed(runnable, mDelayMillis);
		}
	};

	/**
	 * 发送键值
	 * 
	 * @param iKeyCode
	 */
	public void sendKey(final String strKeyCode) {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					Log.i(TAG, "+++++++++++++++++++++++CanKey TransKey:"
							+ strKeyCode);
					int iKeyCode = TranslateKey(strKeyCode);
					Instrumentation instrKey = new Instrumentation();
					instrKey.sendKeyDownUpSync(iKeyCode);

				} catch (Exception e) {
					// TODO: handle exception
				}

				super.run();
			}

		}.start();
	}

	/**
	 * 
	 * @Title: parserkeyxml
	 * @Description: TODO
	 * @param xml
	 * @return
	 * @return: boolean
	 */
	private boolean parserkeyxml(XmlResourceParser xml) {
		boolean bRet = false;
		String strName = "";
		Integer iKeyCode = 0;

		try {

			int iEventType = xml.getEventType();
			while (iEventType != XmlPullParser.END_DOCUMENT) {

				switch (iEventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("Item".equals(xml.getName())) {
						strName = xml.getAttributeValue(0);
						iKeyCode = Integer.parseInt(xml.getAttributeValue(1));
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Item".equals(xml.getName())) {
						mkeyMap.put(strName, iKeyCode);
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

	/**
	 * 
	 * @Title: TranslateKey
	 * @Description: TODO
	 * @param strName
	 * @return
	 * @return: int
	 */
	public int TranslateKey(String strName) {
		return mkeyMap.get(strName);
	}

	/**
	 * 
	 * <p>
	 * Title: setOnCanKeyListener
	 * </p>
	 * <p>
	 * Description: 设置侦听回调
	 * </p>
	 * 
	 * @param onListener
	 */
	public void setOnCanKeyListener(OnCanKeyListener onListener) {
		mCanKeyListener = onListener;
	}

	/**
	 * 
	 * ClassName:OnCanKeyListener
	 * 
	 * @function:按键回调
	 * @author Kim
	 * @Date: 2016-6-21 上午11:46:15
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public interface OnCanKeyListener {
		/**
		 * 
		 * <p>
		 * Title: ondo
		 * </p>
		 * <p>
		 * Description:
		 * </p>
		 * 
		 * @param iKeyCode
		 */
		void ondo(String strKeyCode, boolean bInternal);
	}
}
