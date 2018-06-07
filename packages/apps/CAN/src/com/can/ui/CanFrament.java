package com.can.ui;

import com.autochips.inputsource.AVIN;
import com.autochips.inputsource.InputSource;
import com.can.activity.R;
import com.can.assist.CanXml;
import com.can.parser.Protocol;
import com.can.services.CanTxRxStub;
import com.yecon.common.SourceManager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;

/**
 * ClassName:CanFrament
 * 
 * @function:Frament 管理/数据交互
 * @author Kim
 * @Date: 2016-7-1 下午1:54:41
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanFrament extends Fragment {
	protected final String TAG = this.getClass().getName();

	public String mStrName = "";
	private AVIN mObjATCAudio = null;
	private Object mObjSRCTocken = null;
	public Handler mObjHandler = null;
	public Messenger mUIMessenger = null;
	public Messenger mServiceMessenger = null;

	/**
	 * 
	 * @Title: Init
	 * @Description: 初始化绑定UI服务
	 * @param handler
	 * @param strName
	 * @return: void
	 */
	public void Init(Handler handler, String strName) {

		this.openAudio(strName);

		mStrName = strName;
		mObjHandler = handler;
		mUIMessenger = new Messenger(handler);
		this.doBindService(CanTxRxStub.ACTION_CAN_UI_SERVICE);
	}

	/**
	 * 
	 * @Title: DeInit
	 * @Description: 解绑
	 * @return: void
	 */
	public void DeInit() {
		this.unBindService();
		this.closeAudio();
		mObjHandler.removeCallbacksAndMessages(null);
	}

	/**
	 * 
	 * @Title: showPage
	 * @Description: 显示Page
	 * @param frament
	 * @return: void
	 */
	public void showPage(CanFrament frament) {
		if (frament != null && frament != this) {
			FragmentManager Objmanager = getActivity().getFragmentManager();
			FragmentTransaction ObjTransaction = Objmanager.beginTransaction();
			ObjTransaction.hide(this);
			if (frament.isAdded()) {
				ObjTransaction.show(frament);
			} else {
				ObjTransaction.add(R.id.fragment_container, frament, null);
			}
			ObjTransaction.addToBackStack(null);
			ObjTransaction.commit();
		}
	}

	public void showPopPage(CanFrament frament) {
		if (frament != null && frament != this) {
			FragmentManager Objmanager = getActivity().getFragmentManager();
			FragmentTransaction ObjTransaction = Objmanager.beginTransaction();
			ObjTransaction.hide(this);
			if (frament.isAdded()) {
				ObjTransaction.show(frament);
			} else {
				ObjTransaction.add(R.id.fragment_pop_container, frament, null);
			}
			ObjTransaction.commit();
		}
	}
	
	/**
	 * 
	 * @Title: sendMsg
	 * @Description: 发送数据
	 * @param byCmdId
	 * @param bydata
	 * @param protocol
	 * @return: void
	 */
	public void sendMsg(byte byCmdId, byte[] bydata, Protocol protocol) {

		if (mServiceMessenger != null) {
			try {
				mServiceMessenger.send(CanTxRxStub.getTxMessage(byCmdId,
						byCmdId, bydata, protocol));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @Title: sendMsg   
	 * @Description: TODO 
	 * @param msg      
	 * @return: void
	 */
	public void sendMsg(Message msg){
		if (mServiceMessenger != null) {
			try {
				mServiceMessenger.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @Title: getData
	 * @Description: 请求数据
	 * @param type
	 * @return: void
	 */
	public void getData(int type) {
		if (mServiceMessenger != null) {

			try {
				mServiceMessenger.send(CanTxRxStub.getdataMessage(type,
						mUIMessenger));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Title: getdata
	 * 
	 * @param type
	 * @param iVal
	 */
	public void getData(int type, int iVal) {
		if (mServiceMessenger != null) {

			try {
				mServiceMessenger.send(CanTxRxStub.getdataMessage(type, iVal,
						mUIMessenger));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mServiceMessenger = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mServiceMessenger = new Messenger(service);

			register();
		}
	};

	private void doBindService(String action) {
		Log.i(TAG, "++doBindUIService:" + this.getClass().getName());

		Intent intent = new Intent();
		intent.setAction(action);
		getActivity()
				.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		Log.i(TAG, "--doBindUIService");
	}

	private void unBindService() {
		deregister();
		getActivity().unbindService(mConnection);
	}

	/**
	 * 
	 * @Title: register
	 * @Description: 注册UI用户
	 * @return: boolean
	 */
	private boolean register() {
		boolean bRet = false;

		try {

			if (mServiceMessenger != null) {
				Message msg = CanTxRxStub.getRegisterMessage(mStrName,
						mUIMessenger);
				mServiceMessenger.send(msg);
				bRet = true;
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * 
	 * @Title: deregister
	 * @Description: 注销UI用户
	 * @return: boolean
	 */
	private boolean deregister() {
		boolean bRet = false;

		try {

			if (mServiceMessenger != null) {
				Message msg = CanTxRxStub.getDeregisterMessage(mStrName,
						mUIMessenger);
				mServiceMessenger.send(msg);
				bRet = true;
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * 
	 * @Title: openAudio
	 * @Description: 打开音频
	 * @return: void
	 */
	public void openAudio(String string) {

		if (string.equals(CanTxRxStub.CAN_PHONE_MODE)
				|| string.equals(CanTxRxStub.CAN_SOURCE_MODE)) {

			mObjSRCTocken = SourceManager.registerSourceListener(getActivity(),
					SourceManager.SRC_NO.CanAudio);
			SourceManager.setAudioFocusNotify(mObjSRCTocken,
					new SourceManager.AudioFocusNotify() {

						@Override
						public void onAudioFocusChange(int arg0) {
							// TODO Auto-generated method stub
							switch (arg0) {
							case AudioManager.AUDIOFOCUS_LOSS:
								getActivity().finish();
								break;
							case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
							case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
								if (mObjATCAudio != null) {
									mObjATCAudio.stop();
								}
								break;
							case AudioManager.AUDIOFOCUS_GAIN:
								if (mObjATCAudio != null) {
									mObjATCAudio.play();
								}
								break;
							default:
								break;
							}
						}
					});

			if (mObjATCAudio == null) {
				mObjATCAudio = new AVIN();

				if (string.equals(CanTxRxStub.CAN_SOURCE_MODE)) {
					SourceManager.acquireSource(mObjSRCTocken);
				} else {
					SourceManager.acquireSource(mObjSRCTocken,
							AudioManager.STREAM_VOICE_CALL,
							AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
				}

				mObjATCAudio.setDestination(InputSource.DEST_TYPE_FRONT);
				mObjATCAudio.setSource(InputSource.SOURCE_TYPE_AVIN,
						AVIN.PORT_NONE, CanXml.getAudioProt(getActivity()),
						AVIN.PRIORITY_IN_CBM_LEVEL_DEFAULT);
				mObjATCAudio.play();
			}
		}
	}

	/**
	 * 
	 * @Title: closeAudio
	 * @Description: 关闭音频
	 * @return: void
	 */
	public void closeAudio() {

		if (mStrName.equals(CanTxRxStub.CAN_PHONE_MODE)
				|| mStrName.equals(CanTxRxStub.CAN_SOURCE_MODE)) {
			if (mObjATCAudio != null) {
				mObjATCAudio.stop();
				mObjATCAudio.release();
				mObjATCAudio = null;
			}

			SourceManager.unregisterSourceListener(mObjSRCTocken);
		}
	}
	/**
	 * 
	 * @Title: showAudioPage   
	 * @Description: 显示带音频的page       
	 * @return: void
	 */
	public void showAudioPage() {

		Intent intent = new Intent();
		intent.putExtra(CanTxRxStub.CAN_MODE, CanTxRxStub.CAN_SOURCE_MODE);
		intent.setClass(getActivity(), CanSource.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	public static void FullScreen(Activity activity, boolean bFull) {
		try {
			if (activity == null)
				return;

			WindowManager.LayoutParams lp = activity.getWindow()
					.getAttributes();
			if (bFull) {
				lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
				activity.getWindow().setAttributes(lp);
				activity.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			} else {
				lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
				activity.getWindow().setAttributes(lp);
				activity.getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			}
		} catch (Exception e) {
			
		}
	}
}
