package com.wesail.tdr.ui;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.R;
import com.wesail.tdr.constant.ActionConstants;
import com.wesail.tdr.ui.adapter.InfromationAdapter;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;

public class VehicleFileAcitivity extends Activity implements OnClickListener{
	private static Context mContext;
	private ListView nameList;
	private ListView contentList;
	private InfromationAdapter nameAdapter;
	private InfromationAdapter contentAdapter;
	private Activity self = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_vehicle_file);
		initView();
		
		IntentFilter intent = new IntentFilter();
        intent.addAction(ActionConstants.INFO_VEHICLE_FILE);
        registerReceiver(mReceiver, intent);
	}
	
	@Override
	protected void onResume() {
		Intent intent = new Intent(ActionConstants.SEND_CMD);
		intent.putExtra("cmd", new byte[]{(byte)0x5});
		sendOrderedBroadcast(intent,null);
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		nameList = (ListView) findViewById(R.id.name_list);
		contentList = (ListView) findViewById(R.id.content_list);
		
		
		nameList.setAdapter(nameAdapter);
		contentList.setAdapter(contentAdapter);
	}


	private void initData() {
		mContext = this;
		nameAdapter = new InfromationAdapter(this, R.array.vehicle_file_list);
		
		List<String> contents = new ArrayList<String>();	
		contents.add("vehicle_identification_number");
		contents.add("vehicle_number");
		contents.add("number_plate_classification");
		contentAdapter = new InfromationAdapter(this, contents);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
		default:
			break;
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ActionConstants.INFO_VEHICLE_FILE.equalsIgnoreCase(action)){
				abortBroadcast();
				String vehicle_identification_number = intent.getStringExtra("vehicle_identification_number");
				String vehicle_number = intent.getStringExtra("vehicle_number");
				String number_plate_classification = intent.getStringExtra("number_plate_classification");
				
				List<String> contents = new ArrayList<String>();	
				contents.add(vehicle_identification_number);
				contents.add(vehicle_number);
				contents.add(number_plate_classification);
				contentAdapter = new InfromationAdapter(self, contents);
				contentList.setAdapter(contentAdapter);
				
				int adaptiveTextWidth = contentAdapter.getAdaptiveTextWidth();
				if(adaptiveTextWidth != -1){
					contentList.getLayoutParams().width = adaptiveTextWidth + 10;
				}
			}
		}
	};
}