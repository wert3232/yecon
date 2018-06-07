package com.can.services;

import java.util.ArrayList;
import java.util.Iterator;
import com.can.assist.CanXml;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.parser.DDef.E_CMD_TYPE;
import com.can.ui.CanPopWind;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * ClassName:CanUIService
 * 
 * @function:UI服务 处理数据 解决后台数据刷新问题
 * @author Kim
 * @Date: 2016-7-1 上午9:54:54
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public abstract class CanUIService extends Service {

	protected final String TAG = this.getClass().getName();

	public CanProxy mObjCanProxy = null;
	public Protocol mObjProtocol = null;
	private ArrayList<UIUserInfo> mArrayMessengers = new ArrayList<UIUserInfo>();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mObjCanProxy = CanXml.create(mObjHandler, getApplicationContext(),
				CanPopWind.class.getName(), E_CMD_TYPE.eCmd_Type_PopWind);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mObjCanProxy.deInit();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mCanUiService.getBinder();
	}
	/**
	 * 
	 * @Title: doMessage   
	 * @Description: TODO 
	 * @param msg      
	 * @return: void
	 */
	public abstract void doMessage(Message msg);

	/**
	 * 主handler
	 */
	public Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			doMessage(msg);
			
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
				break;
			}
			
			send2UIUser(msg);
		}
	};

	/**
	 * 负责与注册的user交互
	 */
	public Handler mObjUserHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case CanTxRxStub.MSG_CAN_REG_USER:
				registerUser(msg);
				break;
			case CanTxRxStub.MSG_CAN_UREG_USER:
				unregisterUser(msg);
				break;
			case CanTxRxStub.MSG_CAN_TX:
				send(msg);
				break;
			case CanTxRxStub.MSG_CAN_GET_DATA:
				getdata(msg);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	Messenger mCanUiService = new Messenger(mObjUserHandler);

	public class UIUserInfo {
		public String mName = null;
		public Messenger mMessenger = null;
	}
	/**
	 * 
	 * @Title: registerUser   
	 * @Description: TODO 
	 * @param msg      
	 * @return: void
	 */
	private void registerUser(Message msg) {
		Log.i(TAG, "++registerUser++");
		if (msg.replyTo == null) {
			Log.e(TAG,
					"--registerUser: "
							+ " "
							+ String.valueOf(msg.getData().getString(
									CanTxRxStub.REGISTER_USER)) + " error!");
			return;
		}

		UIUserInfo tmp = new UIUserInfo();

		tmp.mMessenger = msg.replyTo;
		Bundle bundle = msg.getData();
		tmp.mName = String.valueOf(bundle.getString(CanTxRxStub.REGISTER_USER));

		int i = 0;
		// 找是否有注册过用户
		for (UIUserInfo userInfo : mArrayMessengers) {
			if (userInfo.mMessenger != null
					&& userInfo.mMessenger.equals(tmp.mMessenger)) {
				i++;
				// 重复注册
				break;
			}
		}

		if (i <= 0) {
			mArrayMessengers.add(tmp);

			Log.i(TAG, "--registerUser: " + " " + tmp.mName
					+ " registered successfully");
		} else {
			// 重复注册 不加入list
			Log.i(TAG, "--registerUser: " + " " + tmp.mName
					+ " had been registered");
		}
		Log.i(TAG, "--registerUser--");
		
		setProtocol(msg.replyTo);
	}
	/**
	 * 
	 * @Title: unregisterUser   
	 * @Description: TODO 
	 * @param msg      
	 * @return: void
	 */
	private void unregisterUser(Message msg) {
		int i = 0;
		Bundle bundle = msg.getData();
		String name = String.valueOf(bundle
				.getString(CanTxRxStub.UREGISTER_USER));

		Log.i(TAG, "++deregisterUser: " + " " + name);

		Iterator<UIUserInfo> iter = mArrayMessengers.iterator();
		while (iter.hasNext()) {

			UIUserInfo userInfo = iter.next();
			if (userInfo.mMessenger.equals(msg.replyTo)
					&& userInfo.mName.equals(name)) {
				iter.remove();

				Log.i(TAG, "deregisterUser: " + userInfo.mName);
				i++;
			}
		}

		if (i <= 0) {
			Log.w(TAG, "--deregisterUser: failed, no user");
		} else {
			Log.i(TAG, "--deregisterUser: OK");
		}
	}
	/**
	 * 
	 * @Title: send2UIUser   
	 * @Description: TODO 
	 * @param msg      
	 * @return: void
	 */
	public void send2UIUser(Message msg) {

		for (UIUserInfo userInfo : mArrayMessengers) {
			try {
				if (userInfo.mMessenger != null) {

					userInfo.mMessenger.send(Message.obtain(msg));
				}
			} catch (Exception e) {
				mArrayMessengers.remove(userInfo);
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @Title: send   
	 * @Description: TODO 
	 * @param msg      
	 * @return: void
	 */
	private void send(Message msg) {
		if (mObjCanProxy != null) {
			mObjCanProxy.sendMsg2Can(CanTxRxStub.getTxMessage(msg));
		}
	}
	/**
	 * 
	 * @Title: getdata   
	 * @Description: TODO 
	 * @param msg      
	 * @return: void
	 */
	private void getdata(Message msg) {

		if (msg.replyTo != null && mObjCanProxy != null) {

			try {
				msg.replyTo.send(CanTxRxStub.getdataMessage(msg.arg1,
						mObjCanProxy.getData(msg.arg1, msg.arg2)));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @Title: setProtocol   
	 * @Description: TODO 
	 * @param messenger      
	 * @return: void
	 */
    private void setProtocol(Messenger messenger){
        try {

            if (messenger != null) {
                Message msg = CanTxRxStub.getProtocol(mObjProtocol);
                messenger.send(msg);
            }
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
