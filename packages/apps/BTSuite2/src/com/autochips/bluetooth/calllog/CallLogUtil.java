package com.autochips.bluetooth.calllog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.vcard.VCardEntry;
import com.autochips.bluetooth.Bluetooth;
import com.autochips.bluetooth.BluetoothConstants;
import com.autochips.bluetooth.util.L;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.pbap.BluetoothPbapCard;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.bluetooth.client.pbap.BluetoothPbapClient.ConnectionState;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class CallLogUtil {
	private  static CallLogUtil instance = null;
	public static final String ACITON_CALL_LOG = "com.bluetooth.result.log_call";
	private BluetoothPbapClient client;
	private BluetoothServiceHandler mHandler;
	private Context mContext;
	private BluetoothDevice bluetoothDevice;
	private int logType = -1;
	private PhoneLogTask logTask;
	public static synchronized CallLogUtil  getInstance(Context context){
        if (instance == null){
            instance = new CallLogUtil(context);
        }
        return instance;
    }
	private  CallLogUtil(Context context) {
		this.mContext = context;
		logTask = new PhoneLogTask();
	}
	private int getLogType(){
		return logType;
	}
	public void setBluetoothDevice(BluetoothDevice device){
		if(bluetoothDevice == null || client == null){ //如果没有初始化过
			bluetoothDevice = device;
		}else if(!bluetoothDevice.getAddress().equals(device.getAddress())){ //如果是新的蓝牙设备
			this.close();
			bluetoothDevice = device;
		}else if(bluetoothDevice.getAddress().equals(device.getAddress())){ //l老的
			return;
		}
		else{ 
			this.close();
		}
		mHandler = new BluetoothServiceHandler(mContext);
		client = new BluetoothPbapClient(bluetoothDevice, mHandler);
		logTask.setClient(client);
	}
	
	public void pullMiss(BluetoothDevice device){
		setBluetoothDevice(device);
		pullMiss();
	}
	public void pullMiss(){
		logType = BluetoothConstants.CALL_TYPE_MISS;
		if(client.getState() == ConnectionState.DISCONNECTED || client.getState() == ConnectionState.DISCONNECTING){
		}
//		client.pullPhoneBook(BluetoothPbapClient.MCH_PATH);
		logTask.pullNew(BluetoothPbapClient.MCH_PATH);
	}
	
	public void pullCombined(BluetoothDevice device){
		setBluetoothDevice(device);
		pullCombined();
	}
	public void pullCombined(){
		logType = BluetoothConstants.CALL_TYPE_COMBINED;
		if(client.getState() == ConnectionState.DISCONNECTED || client.getState() == ConnectionState.DISCONNECTING){
		}
//		client.pullPhoneBook(BluetoothPbapClient.CCH_PATH);
		logTask.pullNew(BluetoothPbapClient.CCH_PATH);
	}
	
	public void pullOutGoing(BluetoothDevice device){
		setBluetoothDevice(device);
		pullOutGoing();
	}
	public void pullOutGoing(){
		logType = BluetoothConstants.CALL_TYPE_OUTGOING;
		if(client.getState() == ConnectionState.DISCONNECTED || client.getState() == ConnectionState.DISCONNECTING){
//			client.connect();
		}
//		client.pullPhoneBook(BluetoothPbapClient.OCH_PATH);
		logTask.pullNew(BluetoothPbapClient.OCH_PATH);
	}
	
	public void pullInCome(BluetoothDevice device){
		setBluetoothDevice(device);
		pullInCome();
	}
	public void pullInCome(){
		logType = BluetoothConstants.CALL_TYPE_INCOME;
		if(client.getState() == ConnectionState.DISCONNECTED || client.getState() == ConnectionState.DISCONNECTING){
//			client.connect();
		}
//		client.pullPhoneBook(BluetoothPbapClient.ICH_PATH);
		logTask.pullNew(BluetoothPbapClient.ICH_PATH);
	}
	
	public void close(){
		mHandler = null;
		client.disconnect();
	}
	public class BluetoothServiceHandler extends Handler {
		private Context context;
		public BluetoothServiceHandler(Context context) {
			this.context = context;
		}
        @Override
        public void handleMessage(Message msg) {
//            L.d("callLog",msg);
        	int result = msg.what;
            switch (result) {
                case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE: {
                	ArrayList<String> results =  new ArrayList<String>();
                    L.e("callLog","EVENT_PULL_PHONE_BOOK_DONE : " + msg.obj.getClass().getName());
                    List<VCardEntry> list = (List<VCardEntry>) msg.obj;
                    for(VCardEntry vCardEntry : list){   	
//                    	L.e("callLog","list item class :" + vCardEntry.getClass() + "   " + vCardEntry);
                    	results.add(BluetoothPbapCard.jsonifyVcardEntry(vCardEntry));
//                    	L.e("callLog","json :" + BluetoothPbapCard.jsonifyVcardEntry(vCardEntry));
                    }
                    Intent intent = new Intent(ACITON_CALL_LOG);
                    intent.putStringArrayListExtra("list", results);
//                    intent.putExtra("logType", logType);
					intent.putExtra("logType",logTask.getLoadingLogType());
                    context.sendOrderedBroadcast(intent, null);
//                    L.e("callLog","sendOrderedBroadcast ACITON_CALL_LOG");
					logTask.complete();
                }
                break;
                case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_ERROR: {
                    L.e("callLog","EVENT_PULL_PHONE_BOOK_ERROR");
                    logTask.error();
                }
                break;
                case BluetoothPbapClient.EVENT_PULL_VCARD_LISTING_ERROR: {
                    L.e("callLog","EVENT_PULL_VCARD_LISTING_ERROR");
					logTask.error();
                }
                break;
                case BluetoothPbapClient.EVENT_PULL_VCARD_ENTRY_ERROR: {
                    L.e("callLog","EVENT_PULL_VCARD_ENTRY_ERROR");
                }
                break;
                case BluetoothPbapClient.EVENT_SESSION_CONNECTED: {
                    L.e("callLog","EVENT_SESSION_CONNECTED");
                }
                break;
                case BluetoothPbapClient.EVENT_SESSION_DISCONNECTED: {
                    L.e("callLog","EVENT_SESSION_DISCONNECTED");
					logTask.error();
                }
                break;
                case BluetoothPbapClient.EVENT_SESSION_AUTH_REQUESTED: {
                    L.e("callLog","EVENT_SESSION_AUTH_REQUESTED");
                }
                break;
                case BluetoothPbapClient.EVENT_SESSION_AUTH_TIMEOUT: {
                    L.e("callLog","EVENT_SESSION_AUTH_TIMEOUT");
					logTask.error();
                }
                break;
                default: {
                	L.e("callLog", result + "");
					logTask.error();
                }
            }
            client.disconnect();
        }
    }
    private class PhoneLogTask{
		private final String FREE = "";
		private final int LOADING = 1;
		private final int WAITING = 2;
		private Map<Integer,String> map = new HashMap<Integer,String>();
		private BluetoothPbapClient client;
		PhoneLogTask(){
			map.put(LOADING,FREE); // loading -> ""
			map.put(WAITING,FREE);//  waiting -> ""
		}
		public void setClient(BluetoothPbapClient client){
			this.client = client;
		}
		public PhoneLogTask pullNew(String logOrder){
			map.put(WAITING,logOrder);
			L.w("callLog","logOrder:" + logOrder);
			if(isLoadingFree()){
				next();
				L.w("callLog","logOrder: next()");
			}

			L.w("callLog","map.get(LOADING):" + map.get(LOADING) + "  map.get(WAITING):" + map.get(WAITING));
			return this;
		}
		public	PhoneLogTask complete(){
			map.put(LOADING,FREE); // loading -> ""
			return this;
		}
		public PhoneLogTask error(){
			map.put(LOADING,FREE); // loading -> ""
			map.put(WAITING,FREE);//  waiting -> ""
			return this;
		}
		public PhoneLogTask consume(){
			next();
			return this;
		}
		private PhoneLogTask next(){
			if(isLoadingFree() && !isWaitingFree()){ //当获取通话记录空闲时,并且有等待的通话记录获取任务时  load ""  waiting v
				String waitingOrder = map.get(WAITING);
				client.pullPhoneBook(waitingOrder);
				L.w("callLog","waitingOrder:" + waitingOrder);
				map.put(LOADING,waitingOrder);  // loading   "" ->  v
				map.put(WAITING,FREE);		//  waiting   v  -> ""
			}
			return this;
		}
		public int getLoadingLogType(){
			String loadOrder = map.get(LOADING);
			if(BluetoothPbapClient.CCH_PATH.equals(loadOrder)){
				return BluetoothConstants.CALL_TYPE_COMBINED;
			}else if(BluetoothPbapClient.ICH_PATH.equals(loadOrder)){
				return BluetoothConstants.CALL_TYPE_INCOME;
			}else if(BluetoothPbapClient.MCH_PATH.equals(loadOrder)){
				return BluetoothConstants.CALL_TYPE_MISS;
			}else if(BluetoothPbapClient.OCH_PATH.equals(loadOrder)){
				return BluetoothConstants.CALL_TYPE_OUTGOING;
			}
			return -1;
		}
		private boolean isLoadingFree(){
			return FREE.equals(map.get(LOADING));
		}
		private boolean isWaitingFree(){
			return FREE.equals(map.get(WAITING));
		}

	}
}
