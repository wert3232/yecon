package com.wesail.tdr.ui;


import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.L;
import com.wesail.tdr.R;
import com.wesail.tdr.constant.ActionConstants;
import com.wesail.tdr.service.TDRService;
import com.wesail.tdr.ui.view.ConnectAlertDialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenuAcitivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private static Context mContext;
	private ConnectAlertDialog dialog;
	private ActivityManager activityManager;
	private Activity activity;
	private boolean isConnect = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_main_menu);
		initView();
		
		IntentFilter intent = new IntentFilter();
        intent.addAction(ActionConstants.TDR_CONNECT);
        intent.addAction(ActionConstants.TDR_DISCONNECT);
        registerReceiver(mReceiver, intent);
        L.e("TDR OPEN ");
        if(isServiceRunning(this, TDRService.class.getName())){
        	Intent cmdIntent = new Intent(ActionConstants.SEND_CMD);
        	cmdIntent.putExtra("cmd", new byte[]{(byte)0x02});
        	sendOrderedBroadcast(cmdIntent,null);
        }else {	
        	startService(new Intent(mContext, TDRService.class));
        }
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		try {	
			if(!isConnect){
				stopService(new Intent(mContext, TDRService.class));
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
		super.onDestroy();
	}
	
	private void initView() {
		dialog = new ConnectAlertDialog(this,R.style.dialog);
		dialog.setContent(getResources().getString(R.string.msg_disconnect));
		dialog.setCallBack(callback);
		
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.bad_driving_record).setOnClickListener(this);
		findViewById(R.id.record_query).setOnClickListener(this);
		findViewById(R.id.mileage).setOnClickListener(this);
		findViewById(R.id.vehicle_file).setOnClickListener(this);
		findViewById(R.id.recorder_information).setOnClickListener(this);
		findViewById(R.id.driver_information).setOnClickListener(this);
		
	}


	private void initData() {
		mFragmentManager = getFragmentManager();
		mContext = this;
	}
	
	public boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        if(activityManager == null){	
        	activityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        }
	   List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
       if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bad_driving_record:
			startActivity(new Intent(this, BadDriveRecrodAcitivity.class));
			break;
		case R.id.record_query:
//			startActivity(new Intent(this, DriverInfromationAcitivity.class));
			startActivity(new Intent(this, RecordQueryAcitivity.class));
			break;
		case R.id.mileage:
			startActivity(new Intent(this, MileageAcitivity.class));
			break;
		case R.id.vehicle_file:
			startActivity(new Intent(this, VehicleFileAcitivity.class));
			break;
		case R.id.recorder_information:
			startActivity(new Intent(this, RecorderInformationAcitivity.class));
			break;
		case R.id.driver_information:
			startActivity(new Intent(this, DriverInfromationAcitivity.class));
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			L.e("MainMenuAcitivity onReceive action:" + action);
			if(ActionConstants.TDR_CONNECT.equalsIgnoreCase(action)){
				abortBroadcast();
				
				findViewById(R.id.bad_driving_record).setEnabled(true);
				findViewById(R.id.record_query).setEnabled(true);
				findViewById(R.id.mileage).setEnabled(true);
				findViewById(R.id.vehicle_file).setEnabled(true);
				findViewById(R.id.recorder_information).setEnabled(true);
				findViewById(R.id.driver_information).setEnabled(true);
				isConnect = true;
			}else if(ActionConstants.TDR_DISCONNECT.equalsIgnoreCase(action)){
				abortBroadcast();
				isConnect = false;
				dialog.show();
			}
		}
	};
	
	private ConnectAlertDialog.Callback callback = new ConnectAlertDialog.Callback() {
		
		@Override
		public void onCallOK(Dialog dialog, TextView ok, TextView no) {
			if(isServiceRunning(mContext, TDRService.class.getName())){
				stopService(new Intent(mContext, TDRService.class));
	        }
			finish();
		}
		
		@Override
		public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {

		}
	};
}