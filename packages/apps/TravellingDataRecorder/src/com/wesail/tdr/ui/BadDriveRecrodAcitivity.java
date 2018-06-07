package com.wesail.tdr.ui;


import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.R;
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

public class BadDriveRecrodAcitivity extends Activity implements OnClickListener{
	private static Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_bad_drive_record);
		initView();
        Intent intent = new Intent(ActionConstants.SEND_CMD);
        intent.putExtra("cmd", new byte[]{(byte)0x02});
		sendOrderedBroadcast(intent,null);
		
	}
	
	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(com.wesail.tdr.R.id.overtime_driving_record).setOnClickListener(this);
		findViewById(com.wesail.tdr.R.id.overspeed_records).setOnClickListener(this);
		findViewById(com.wesail.tdr.R.id.accident_doubt_record).setOnClickListener(this);
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
		case R.id.overtime_driving_record:
			startActivity(new Intent(this, BadDriveRecrodOvertimeDrivingAcitivity.class));
			break;
		case R.id.overspeed_records:
			startActivity(new Intent(this, BadDriveRecrodOverspeedAcitivity.class));
			break;
		case R.id.accident_doubt_record:
			startActivity(new Intent(this, BadDriveRecrodAccidentDoubtAcitivity.class));
			break;
		default:
			break;
		}
	}
}