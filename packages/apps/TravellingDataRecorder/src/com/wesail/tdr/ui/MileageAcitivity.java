package com.wesail.tdr.ui;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.L;
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

public class MileageAcitivity extends Activity implements OnClickListener{
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
		setContentView(R.layout.activity_driver_information);
		initView();
		
		IntentFilter intent = new IntentFilter();
        intent.addAction(ActionConstants.INFO_MILEAGE);
        registerReceiver(mReceiver, intent);
	}
	
	@Override
	protected void onResume() {
		Intent intent = new Intent(ActionConstants.SEND_CMD);
		intent.putExtra("cmd", new byte[]{(byte)0x3});
		sendOrderedBroadcast(intent,null);
		super.onResume();
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
		nameAdapter = new InfromationAdapter(this, R.array.mileage_list);
		
		List<String> contents = new ArrayList<String>();	
		contents.add("initial_mileage");
		contents.add("accumulated_miles");
		contentAdapter = new InfromationAdapter(this, contents);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
			if(ActionConstants.INFO_MILEAGE.equalsIgnoreCase(action)){
				abortBroadcast();
				float initial_mileage = intent.getFloatExtra("initial_mileage",0);
				float accumulated_miles = intent.getFloatExtra("accumulated_miles",0);
				List<String> contents = new ArrayList<String>();	
				contents.add(initial_mileage + " km");
				contents.add(accumulated_miles + " km");
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