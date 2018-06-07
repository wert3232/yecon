package com.autochips.bluetooth;

import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtPlayerManage;
import com.autochips.bluetooth.util.L;
import com.yecon.common.SourceManager;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;
import android.bluetooth.BluetoothProfile;

public class PhoneLinkReceiver extends BroadcastReceiver{
	
	public static final String BROADCAST_CONNECTION_BREAK = 
	"net.easyconn.connection.break";
	public static final String BROADCAST_BT_CHECKSTATUS = 
	"net.easyconn.bt.checkstatus";
	public static final String BROADCAST_BT_CONNECTED = "net.easyconn.bt.connected";
	public static final String BROADCAST_BT_NOT_CONNECTED = 
	"net.easyconn.bt.notconnected";
	public static final String BROADCAST_BT_CONNECT = "net.easyconn.bt.connect";
	public static final String BROADCAST_BT_A2DP_ACQUIRE = 
	"net.easyconn.a2dp.acquire";
	public static final String BROADCAST_BT_A2DP_RELEASE = 
	"net.easyconn.a2dp.release";
	public static final String BROADCAST_APP_QUIT = "net.easyconn.app.quit";
	public static final String BROADCAST_BT_OPENED = "net.easyconn.bt.opened";
	private String TAG = "PhoneLinkReceiver";
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.i(TAG , arg1.getAction());
		if(arg1.getAction().equals(SourceManager.ACTION_SOURCE_CHANGED_NOTIFY)){
			int srcNo = arg1.getIntExtra(SourceManager.EXTRA_SOURCE_NO, -1);
			if(srcNo>=0 && srcNo<SourceManager.SRC_NO.max){
				if(SourceManager.SRC_NO.backcar!=srcNo 
						&&SourceManager.SRC_NO.bt_phone!=srcNo ){
					if(srcNo!=SourceManager.SRC_NO.bluetooth){
							Bluetooth.getInstance().musicrevoke();	
							if (Bluetooth.getInstance().isA2DPPlaying()) {
								L.e("PL receiver BluetoothAvrcpCtPlayerManage.CMD_PAUSE");
								Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
								MainBluetoothActivity.playNextTime = true;								
							}		
					}
				}
			}
		}
		else if(arg1.getAction().equals(BROADCAST_BT_CHECKSTATUS)){
			//int status  = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.ID_A2DP);
			//if(BluetoothAdapter.STATE_CONNECTED ==status
			//		|| BluetoothAdapter.STATE_CONNECTING ==status) {
			if(BluetoothReceiver.mbA2DPConnected){
				Intent intent = new Intent(BROADCAST_BT_CONNECTED);
				arg0.sendBroadcast(intent);
			}
			else{
				if(LocalBluetoothManager.getInstance(arg0).getBluetoothState() != BluetoothAdapter.STATE_ON){
	                LocalBluetoothManager.getInstance(arg0).setBluetoothEnabled(true);
	            }
				Intent intent = new Intent(BROADCAST_BT_OPENED);
				String name = BluetoothAdapter.getDefaultAdapter().getName();
				String mac = BluetoothAdapter.getDefaultAdapter().getAddress();
				String pin = SystemProperties.get(Bluetooth.PERSYS_BT_PAIR, "0000");
				intent.putExtra("name",  name);
				intent.putExtra("pin",  pin);
				intent.putExtra("mac",  mac);
				arg0.sendBroadcast(intent);
			}
		}
		else if(arg1.getAction().equals(BROADCAST_BT_A2DP_ACQUIRE)
				|| arg1.getAction().equals(SourceManager.ACTION_PHONE_LINK_RESUME)){
			String flag = arg1.getStringExtra("flag");
			if(null!=flag){
				//it's miracast or iplay.
				if(flag.equals("iplay")){
					//it's iplay , not using a2dp
				}			
			}
			else{
				//usb phone link need a2dp
				SystemProperties.set("persist.sys.need_a2dp", ""+1);
				BluetoothAvrcpCtPlayerManage avrcp = BluetoothAvrcpCtPlayerManage.getInstance(arg0);
				if(avrcp!=null){
					avrcp.notifyResume();	
				}
			}
		}
		else if(arg1.getAction().equals(BROADCAST_BT_A2DP_RELEASE)
				|| arg1.getAction().equals(SourceManager.ACTION_PHONE_LINK_PAUSE)){
			SystemProperties.set("persist.sys.need_a2dp", ""+0);
			String flag = arg1.getStringExtra("flag");
			if(null!=flag){
				//it's miracast or iplay.
				if(flag.equals("iplay")){
					//it's iplay , not using a2dp
				}				
			}
			else{				
				BluetoothAvrcpCtPlayerManage avrcp = BluetoothAvrcpCtPlayerManage.getInstance(arg0);
				if(avrcp!=null){
					avrcp.notifyRevoke();	
				}
			}
		}
		else if(arg1.getAction().equals(BROADCAST_APP_QUIT)){
			
		}
	}
	
	//status: 0: not connected, 1:connected
	public static void notifyA2dpStatus(Context context, int status){
		Intent intent;
		if(1==status){
			intent = new Intent(BROADCAST_BT_CONNECTED);
			context.sendBroadcast(intent);
		}
		else if(0==status){
			intent = new Intent(BROADCAST_BT_NOT_CONNECTED);
			context.sendBroadcast(intent);
		}
	}

}
