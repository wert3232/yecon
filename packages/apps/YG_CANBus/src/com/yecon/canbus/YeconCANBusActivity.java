package com.yecon.canbus;

import com.yecon.canbus.parse.YeconCANBus;
import com.yecon.canbus.raise.ford.FordACActivity;
import com.yecon.canbus.raise.honda.HondaACActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class YeconCANBusActivity extends Activity implements OnClickListener {

	private static final String ACTION_CANBUS_SERVICE = "yecon.intent.action.CANBUS_SERVICE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yecon_canbus);
		findViewById(R.id.btn_startService).setOnClickListener(this);
		findViewById(R.id.canbus_type_ford).setOnClickListener(this);
		findViewById(R.id.canbus_type_honda).setOnClickListener(this);
		
		YeconCANBusFactory.CreateCanbusUI(this, YeconCANBus.ReadCanbusType(this));
		finish();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (arg0.getId()) {
		case R.id.btn_startService:
			intent = new Intent(ACTION_CANBUS_SERVICE);
			startService(intent);
			break;
			
		case R.id.canbus_type_ford:
			intent = new Intent(getApplicationContext(), FordACActivity.class);
			startActivity(intent);
			break;

		case R.id.canbus_type_honda:
			intent = new Intent(getApplicationContext(), HondaACActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
