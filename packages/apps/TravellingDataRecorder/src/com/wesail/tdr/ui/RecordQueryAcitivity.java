package com.wesail.tdr.ui;


import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.R;
import com.wesail.tdr.TDRActivity;
import com.wesail.tdr.constant.ActionConstants;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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

public class RecordQueryAcitivity extends Activity implements OnClickListener{
	private static Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_record_query);
		initView();
        Intent intent = new Intent(ActionConstants.SEND_CMD);
        intent.putExtra("cmd", new byte[]{(byte)0x02});
		sendOrderedBroadcast(intent,null);
		
	}
	
	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(com.wesail.tdr.R.id.travel_speed).setOnClickListener(this);
		findViewById(com.wesail.tdr.R.id.power_down_power_down_record).setOnClickListener(this);
	}


	private void initData() {
		mContext = this;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.travel_speed:
			startActivity(new Intent(this, RecordQueryDravelSpeedAcitivity.class));
			break;
		case R.id.power_down_power_down_record:
			startActivity(new Intent(this, RecordQueryPowerAcitivity.class));
			break;
		default:
			break;
		}
	}
}