package com.autochips.miracast;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SppManager {
	private final  String SPP_HEAD_FLAG = "YgSpp";
	private final String TAG = "SppManager";
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_ERROR = 5;
    
 // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private boolean exited = false;
    
	Context context;
	Handler uiHandler;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSppService mChatService = null;
    private StringBuffer outStrBuf = new StringBuffer();
    private Handler mainHandler;
    //private byte[] outBuf ;
    
	SppManager(Context context, Handler uiHandler){
		this.context = context;
		this.uiHandler = uiHandler;
		this.mainHandler = new Handler(context.getMainLooper()){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
	            case MESSAGE_STATE_CHANGE:
	                Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                switch (msg.arg1) {
	                case BluetoothSppService.STATE_CONNECTED:

	                    break;
	                case BluetoothSppService.STATE_CONNECTING:

	                    break;
	                case BluetoothSppService.STATE_LISTEN:
	                case BluetoothSppService.STATE_NONE:

	                    break;
	                }
	                break;
	            case MESSAGE_WRITE:

	                break;
	            case MESSAGE_READ:

	                break;
	            case MESSAGE_DEVICE_NAME:

	                break;
	            case MESSAGE_ERROR:
	            	//listen again.
	            	Log.i(TAG, "MESSAGE_ERROR: " + msg.getData().getString(TOAST, ""));
	            	if(!exited)
	            		mainHandler.postDelayed(runnableStartSppServer,1000);
	                break;
	            }
			}
			
		};
	}
	
	private Runnable runnableStartSppServer = new Runnable(){
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!exited)
				start();
		}
	};
	
	public void init(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mChatService = new BluetoothSppService(context, mainHandler);
        start();
	}
	
	public void release(){
        stop();
	}
	
	public void start(){
		if(mChatService!=null){
			 mChatService.start();
		}
		exited = false;
	}
	
	public void stop(){
		if(mChatService!=null){
        	mChatService.stop();
        }
		exited = true;
		mainHandler.removeCallbacksAndMessages(null);
	}
	
	public enum CMDNO{
		CMD_HOME,
		CMD_BACK;
	};
	
	public void sendCmd(CMDNO cmdNo){
		if(mChatService!=null){
			outStrBuf.setLength(0);
			outStrBuf.append( SPP_HEAD_FLAG + ":cmd:"+cmdNo.ordinal());
			mChatService.write(outStrBuf.toString().getBytes());
		}
	}
	
	public int getSppState(){
		if(mChatService!=null){
			return mChatService.getState();
		}
		return BluetoothSppService.STATE_NONE;
	}
	
	
}
