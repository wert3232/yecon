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

public class RecorderInformationAcitivity extends Activity implements OnClickListener{
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
		setContentView(R.layout.activity_recorder_information);
		initView();
		
		IntentFilter intent = new IntentFilter();
        intent.addAction(ActionConstants.INFO_RECORDER);
        registerReceiver(mReceiver, intent);
	}

	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		nameList = (ListView) findViewById(R.id.name_list);
		contentList = (ListView) findViewById(R.id.content_list);
		
		nameList.setAdapter(nameAdapter);
		contentList.setAdapter(contentAdapter);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = new Intent(ActionConstants.SEND_CMD);
		intent.putExtra("cmd", new byte[]{(byte)0x07});
		sendOrderedBroadcast(intent,null);
		
		intent.putExtra("cmd", new byte[]{(byte)0x03});
		sendOrderedBroadcast(intent,null);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	private void initData() {
		mContext = this;
		nameAdapter = new InfromationAdapter(this, R.array.recorder_information_list);
		
		List<String> contents = new ArrayList<String>();	
		contents.add("authentication_code");
		contents.add("certified_product_model");
		contents.add("first_time_to_install");
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
	private String authentication_code;
	private String certified_product_model;
	private String first_time_to_install;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ActionConstants.INFO_RECORDER.equalsIgnoreCase(action)){
				abortBroadcast();
				if(intent.getStringExtra("authentication_code") != null){	
					authentication_code = intent.getStringExtra("authentication_code");
				}
				if(intent.getStringExtra("certified_product_model") != null){	
					certified_product_model = intent.getStringExtra("certified_product_model");
				}
				if(intent.getStringExtra("first_time_to_install") != null){	
					first_time_to_install = intent.getStringExtra("first_time_to_install");
				}
				
				List<String> contents = new ArrayList<String>();	
				contents.add(authentication_code);
				contents.add(certified_product_model);
				contents.add(first_time_to_install);
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