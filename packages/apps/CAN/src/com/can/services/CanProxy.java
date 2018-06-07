package com.can.services;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.can.parser.DDef.E_CMD_TYPE;
import com.can.parser.Protocol;

/**
 * ClassName:CanProxy
 * 
 * @function:Can用户代理 客户端
 * @author Kim
 * @Date:  2016-5-26 上午11:56:30
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public abstract class CanProxy extends Handler{
	
	public String	 mStrUserName      = "";
	public Context   mProxyContext     = null;
	public Protocol  mProxyProtocol    = null;
	public Messenger mProxyMessenger   = null;
	public Messenger mServiceMessenger = null;
	
	protected final String TAG = this.getClass().getName(); 
	public ArrayList<RegProxy_Info> mArrayRegProxyInfo = new ArrayList<RegProxy_Info>();
	
	public CanProxy() {
		// TODO Auto-generated constructor stub
	}	
	
	/**
	 * 
	* <p>Title: start</p>
	* <p>Description: 绑定服务</p>
	* @param action
	 */
	public void start(Handler handler, Context context, String name, Protocol protocol){
		
		mStrUserName    = name;
		mProxyContext   = context;
		mProxyProtocol  = protocol;
		mProxyMessenger = new Messenger(this);
		this.doBindService(CanTxRxStub.ACTION_CAN_SERVICE);
	}
	
	/**
	 * 
	* <p>Title: registerProxy</p>
	* <p>Description: 初始化注册用户</p>
	 */
	public void RegisterProxy(int iCmdId, int iSubId){
		
		RegProxy_Info regProxyInfo = new RegProxy_Info();
		regProxyInfo.iCmdId = iCmdId;
		regProxyInfo.iSubId = iSubId;
		
		mArrayRegProxyInfo.add(regProxyInfo);
	}
	
	/**
	 * 
	* <p>Title: Init</p>
	* <p>Description: 初始化</p>
	 */
	public abstract void Init(E_CMD_TYPE eCmdType);
	/**
	 * 
	* <p>Title: Finish</p>
	* <p>Description: 完成绑定服务</p>
	 */
	public abstract void Finish();
	/**
	 * 
	* <p>Title: stop</p>
	* <p>Description: 解绑服务</p>
	 */
	public void deInit(){
		
		this.unBindService();
	}
	/**
	 * 
	* <p>Title: getData</p>
	* <p>Description: 获取数据接口</p>
	* @param iwhat
	* @return
	 */
	public abstract Object getData(int iwhat, int iVal);
	/**
	 * 
	* <p>Title: sendMsg2Can</p>
	* <p>Description: 发送数据给Can</p>
	* @param msg
	* @return
	 */
    public boolean sendMsg2Can(Message msg){
        boolean bRet = false;

        if (mServiceMessenger == null) {
        	
        	Log.e(TAG, "sendMsg2Can:mServiceMessenger is null");
		}else if (msg == null) {
			
			Log.i(TAG, "sendMsg2Can:msg is fiter!");
		}else {
            try {
            	
                msg.replyTo = this.mProxyMessenger;
                mServiceMessenger.send(msg);
                bRet = true;
                
            } catch (RemoteException e) {
            	
                e.printStackTrace();
            } catch (Exception e) {
            	
                e.printStackTrace();
            }
		}

        return bRet;
    }
	
    private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			for (RegProxy_Info regProxyInfo : mArrayRegProxyInfo){
				deregisterProxy(regProxyInfo.iCmdId, regProxyInfo.iSubId);
			}
			
			mServiceMessenger = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mServiceMessenger = new Messenger(service);
			
			setProtocol();
			
			for (RegProxy_Info regProxyInfo : mArrayRegProxyInfo){
				registerProxy(regProxyInfo.iCmdId, regProxyInfo.iSubId);
			}
			
			Finish();
		}
	};
	
	private void doBindService(String action) {
        Log.i(TAG, "++doBindService:" + this.getClass().getName());

        Intent intent = new Intent();
        intent.setAction(action);
        mProxyContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Log.i(TAG, "--doBindService");
    }
    
    private void unBindService(){
    	
    	mProxyContext.unbindService(mConnection);
    }
    
    /**
     * 
    * <p>Title: registerProxy</p>
    * <p>Description: 注册代理</p>
    * @param iCmdId
    * @param iSubId
    * @return
     */
    private boolean registerProxy(int iCmdId, int iSubId) {
        boolean bRet = false;

        try {
        	
            if (mServiceMessenger != null) {
                Message msg = CanTxRxStub.getRegisterMessage(iCmdId, iSubId, mStrUserName, mProxyMessenger);
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
    * <p>Title: deregisterProxy</p>
    * <p>Description: 注销代理</p>
    * @param iCmdId
    * @param iSubId
    * @return
     */
    private boolean deregisterProxy(int iCmdId, int iSubId) {
        boolean bRet = false;

        try {

            if (mServiceMessenger != null) {
                Message msg = CanTxRxStub.getDeregisterMessage(iCmdId, iSubId, mStrUserName, mProxyMessenger);
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
    * <p>Title: setProtocol</p>
    * <p>Description: 设置协议类</p>
     */
    private void setProtocol(){
        try {

            if (mServiceMessenger != null) {
                Message msg = CanTxRxStub.getProtocolMessage(mProxyProtocol);
                mServiceMessenger.send(msg);
            }
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * ClassName:RegProxy_Info
     * 
     * @function:缓存注册信息
     * @author Kim
     * @Date:  2016-5-31 下午4:19:11
     * @Copyright: Copyright (c) 2016
     * @version 1.0
     */
    public class RegProxy_Info{
    	int iCmdId = 0;
    	int iSubId = 0;
    }
}
