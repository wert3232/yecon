package com.can.services;

import java.util.ArrayList;
import java.util.Iterator;
import com.can.parser.Protocol;
import com.can.tool.CanInfo;
import com.can.tool.DataConvert;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.mcu.McuBaseInfo;
import android.mcu.McuExternalConstant;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * ClassName:CanService
 * 
 * @function:Can服务端
 * @author Kim
 * @Date:  2016-5-27 上午11:26:34
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanService extends Service implements McuListener{
	protected final String TAG = this.getClass().getName();  
	
	public Protocol mObjProtocol;
	private McuManager mObjMcuMananger;
	public ArrayList<UserInfo> mArrayUserInfos = new ArrayList<UserInfo>();
	

    //定义接收数据缓冲最大长度
    private final int miMaxLength = 4096;
    //数据缓冲区
    private byte[] mBuffer = new byte[miMaxLength];
    //每次收到实际长度
    private int miAvailable = 0;
    //当前已经收到包的总长度
    private int mpRemainingLength[] = new int[1];
    //最小包长度
    private int mpMinPacketLength[] = new int[1];
    //分析buf有效数据起点
    private int mpCursor[] = new int[1];
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
        mpCursor[0] = 0;
        mpRemainingLength[0] = 0;
        mpMinPacketLength[0] = 5;
		
		mObjMcuMananger = (McuManager)getSystemService(Context.MCU_SERVICE);
		
		try {
			mObjMcuMananger.RPC_RequestMcuInfoChangedListener(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		try {
			mObjMcuMananger.RPC_RemoveMcuInfoChangedListener(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case CanTxRxStub.MSG_CAN_RX:
				send2User(msg);
				break;
			case CanTxRxStub.MSG_CAN_TX:
				send2Prot(msg);
				break;
			case CanTxRxStub.MSG_CAN_REG_USER:
				registerUser(msg);
				break;
			case CanTxRxStub.MSG_CAN_UREG_USER:
				deregisterUser(msg);
				break;
			case CanTxRxStub.MSG_CAN_SET_PROTOCOL:
				setProtocol(msg);
				break;
			default:
				Log.e(TAG, "donot know msg what?");
				break;
			}
		}
	};
	
	public Messenger mServiceMessenger = new Messenger(mHandler);

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mServiceMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	/**
	 * 
	 * ClassName:UserInfo
	 * 
	 * @function:用户代理注册信息
	 * @author Kim
	 * @Date:  2016-5-27 下午3:26:35
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
    static public class UserInfo {
        public int iCmdId = 0;
        public int iSubId = 0;
        public String mName = null;
        public Messenger mMessenger = null;
    }
	
	/**
	 * 
	* <p>Title: registerUser</p>
	* <p>Description: 注册用户</p>
	* @param msg
	 */
	protected void registerUser(Message msg){
		Log.i(TAG, "++registerUser++");
		if (msg.replyTo == null) {
            Log.e(TAG, "--registerUser: " + "iCmdId: " + msg.arg1 + " iSubId: " + msg.arg2 + " "
                    + String.valueOf(msg.getData().getString(CanTxRxStub.REGISTER_USER)) + " error!");
            return;
        }

        UserInfo tmp = new UserInfo();
        tmp.iCmdId = msg.arg1;
        tmp.iSubId = msg.arg2;
        tmp.mMessenger = msg.replyTo;
        Bundle bundle = msg.getData();
        tmp.mName = String.valueOf(bundle.getString(CanTxRxStub.REGISTER_USER));

        int i = 0;
        // 找是否有注册过用户
        for (UserInfo userInfo : mArrayUserInfos) {
            if (userInfo.iCmdId == tmp.iCmdId && userInfo.iSubId == tmp.iSubId
                    && userInfo.mMessenger != null && userInfo.mMessenger.equals(tmp.mMessenger)) {
                i++;
                // 重复注册
                break;
            }
        }

        if (i <= 0) {
        	mArrayUserInfos.add(tmp);
        	
            Log.i(TAG, "--registerUser: " + "iCmdId: " + tmp.iCmdId + " iSubId: " + tmp.iSubId + " "
                    + tmp.mName + " registered successfully");
        } else {
            // 重复注册 不加入list
            Log.i(TAG, "--registerUser: " + "iCmdId: " + tmp.iCmdId + " iSubId: " + tmp.iSubId + " "
                    + tmp.mName + " had been registered");
        }
		Log.i(TAG, "--registerUser--");		
	}
	
	/**
	 * 
	* <p>Title: deregisterUser</p>
	* <p>Description: 注销用户</p>
	* @param msg
	 */
	protected void deregisterUser(Message msg){
		
        int i = 0;
        Bundle bundle = msg.getData();
        String name = String.valueOf(bundle.getString(CanTxRxStub.UREGISTER_USER));

        Log.i(TAG, "++deregisterUser: " + msg.arg1 + " " + msg.arg2 + " " + name);

        Iterator<UserInfo> iter = mArrayUserInfos.iterator();
        while (iter.hasNext()) {
        	
            UserInfo userInfo = iter.next();
            if (userInfo.iCmdId == msg.arg1 && userInfo.iSubId == msg.arg2 &&
                    userInfo.mMessenger.equals(msg.replyTo) && userInfo.mName.equals(name)) {
                iter.remove();

                Log.i(TAG, "deregisterUser: "
                        + userInfo.iCmdId + " " + userInfo.iSubId + " " + userInfo.mName);
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
	 * onMcuInfoChanged Rx数据
	 */
	@Override
	public void onMcuInfoChanged(McuBaseInfo arg0, int arg1) {
		// TODO Auto-generated method stub
		
		if (arg0 != null){
			
			if (arg1 == McuExternalConstant.MCU_CANBUS_INFO_TYPE) {
				//是can数据
			    if (mObjProtocol != null){
			    	
			    	try {
			    		 
			    		miAvailable = arg0.getOriginalInfo().getMcuData().length;
			    		System.arraycopy(arg0.getOriginalInfo().getMcuData(), 0, mBuffer, mpRemainingLength[0], miAvailable);
			    		
			    		CanInfo.Rx(arg0.getOriginalInfo().getMcuData());
			    		
			    		if (mObjProtocol.IsAck(mBuffer)) {
			    			mpRemainingLength[0] = 0;
			    			CanInfo.i(TAG, "now Recevice IsAck!");
						} else {
							
							if (miAvailable > 0) {
		                        //读到数据
		                        mpRemainingLength[0] += miAvailable;
							}
		        			else{
		        				
		        				Log.e(TAG, "onMcuInfoChanged:getCANBusData empty!");
		        				return;
		        			}
		        			
		        			//分析数据 直到数据不足一包为止ֹ
		        			while (mpRemainingLength[0] >= mpMinPacketLength[0]){
		        				//给协议分析
		        				if (mObjProtocol.parsePack(mBuffer, mpCursor, mpRemainingLength, mpMinPacketLength)){
		        					
		        					if (mHandler != null) {
		        						
		                                byte[] packet = new byte[mpMinPacketLength[0]];
		                                System.arraycopy(mBuffer, mpCursor[0] - mpMinPacketLength[0], packet, 0, mpMinPacketLength[0]);
		                                mHandler.sendMessage(CanTxRxStub.getRxMessage(packet, mObjProtocol));
									}
		        					
		        					//恢复最小包长 
		                            mpMinPacketLength[0] = mObjProtocol.getMinPacketLength();
		        				}
		        				else{
		        					//非整包数据 等收到一整包数据再做分析 
		        					break;
		        				}
		        			}
		        			
		        			//残留字节移动到缓存区首
		                    if (mpRemainingLength[0] > 0 && mpCursor[0] > 0) {
		                        System.arraycopy(mBuffer, mpCursor[0], mBuffer, 0, mpRemainingLength[0]);
		                    }

		                    mpCursor[0] = 0;
						}
			    		
			    	}catch (IndexOutOfBoundsException e) {
	                    e.printStackTrace();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                } finally {
	                    // mBuffer游标恢复0
	                    mpCursor[0] = 0;
	                }
			    }
			    else{
			    	Log.e(TAG, "onMcuInfoChanged: mObjProtocol == null!");
			    }
			}
		}
	}
	
	/**
	 * 
	* <p>Title: send2Prot</p>
	* <p>Description: Tx 发给Can的接口</p>
	* @param msg
	* @return
	 */
	protected boolean send2Prot(Message msg){
		boolean bRet = true;
		Bundle bundle = msg.getData();
		byte[] packet = bundle.getByteArray(CanTxRxStub.BUND_CAN_TX);
		
		CanInfo.Tx(packet);
		
		try {
			mObjMcuMananger.RPC_SendCANInfo(packet, packet.length);
		} catch (Exception e) {
			// TODO: handle exception
			bRet = false;
			e.printStackTrace();
		}
		
		return bRet;
	}
	
	/**
	 * 
	* <p>Title: send2User</p>
	* <p>Description: 分发给用户代理</p>
	* @param msg
	* @return
	 */
	protected boolean send2User(Message msg){
		boolean bRet = true;
        
        for (UserInfo userInfo : mArrayUserInfos) {
            try {
                if (userInfo.iCmdId == msg.arg1 && userInfo.iSubId == msg.arg2 && userInfo.mMessenger != null) {
                    
                	userInfo.mMessenger.send(Message.obtain(msg));
                }
            } catch (Exception e) {
            	mArrayUserInfos.remove(userInfo);
                e.printStackTrace();
            }
        }
        
		return bRet;
	}
	/**
	 * 
	* <p>Title: setProtocol</p>
	* <p>Description: 设置协议类型 并且连接Can</p>
	* @param msg
	 */
	private void setProtocol(Message msg){
		
		if (mObjProtocol == null){
			mObjProtocol = (Protocol)msg.obj;
			byte[] byData = mObjProtocol.connect();
			
			Log.i(TAG, "Connect CAN %s!" + DataConvert.Bytes2Str(byData));
			try {
				mObjMcuMananger.RPC_SendCANInfo(byData, byData.length);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}
