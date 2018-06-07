package com.wesail.tdr.ui;


import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.L;
import com.wesail.tdr.R;
import com.wesail.tdr.entity.AccidentDoubt;
import com.wesail.tdr.entity.AccidentDoubtDetil;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
public class BadDriveRecrodAccidentDoubtDetilAcitivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private static Context mContext;
	private ListView listView;
	private TextView detilInfoText;
	private int count = 0;
	private AccidentDoubt accidentDoubt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_bad_drive_record_accident_doubt_detil);
		initView();
		
	}
	
	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.list);
		detilInfoText = (TextView) findViewById(R.id.detil_info);
		accidentDoubt = getIntent().getParcelableExtra("item");
		detilInfoText.setText(accidentDoubt.getTime());
		if(accidentDoubt != null){
			AccidentDoubtDetilAdapter adapter = new AccidentDoubtDetilAdapter(this, accidentDoubt.getList());
			listView.setAdapter(adapter);
		}
	}


	private void initData() {
		mFragmentManager = getFragmentManager();
		mContext = this;
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
	private  class AccidentDoubtDetilAdapter extends BaseAdapter{
		private Resources res;
		private LayoutInflater inflater;
		private ArrayList<AccidentDoubtDetil> doubtDetils;
		public AccidentDoubtDetilAdapter(Context context,ArrayList<AccidentDoubtDetil> doubtDetils) {
			inflater = LayoutInflater.from(context);
			res = getResources();
			this.doubtDetils = doubtDetils;
		}
		@Override
		public int getCount() {
			return doubtDetils.size();
		}

		@Override
		public Object getItem(int position) {
			return doubtDetils.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.list_item_accident_doubt_detail,parent,false);
			AccidentDoubtDetil detil = doubtDetils.get(position);
			TextView parkingBeforeSecondsText = (TextView)view.findViewById(R.id.parking_before_seconds);
			TextView speedText= (TextView)view.findViewById(R.id.speed);
			TextView brakeText = (TextView)view.findViewById(R.id.brake);
			TextView turnLeftText= (TextView)view.findViewById(R.id.turn_left);
			TextView turnRightText = (TextView)view.findViewById(R.id.turn_right);
			TextView farLightText = (TextView)view.findViewById(R.id.far_light);
			TextView dippedLightText  = (TextView)view.findViewById(R.id.dipped_light);
			TextView alarmText  = (TextView)view.findViewById(R.id.alarm);
			TextView openTheDoorText = (TextView)view.findViewById(R.id.open_the_door);
			
//			parkingBeforeSecondsText.setText(detil.getParkingBeforeSeconds() + "");
			String second = String.format("%.1f", position * 0.2);
			parkingBeforeSecondsText.setText(second);
			speedText.setText(Math.abs(detil.getSpeed()) + "km/h");
			brakeText.setText(detil.getBrake() == 1 ? getResources().getString(R.string.open) : getResources().getString(R.string.close));
			turnLeftText.setText(detil.getTurnLeft() == 1 ? getResources().getString(R.string.open) : getResources().getString(R.string.close));
			turnRightText.setText(detil.getTurnRight() == 1 ? getResources().getString(R.string.open) : getResources().getString(R.string.close));
			farLightText.setText(detil.getFarLight() == 1 ? getResources().getString(R.string.open) : getResources().getString(R.string.close));
			dippedLightText.setText(detil.getDippedLight() == 1 ? getResources().getString(R.string.open) : getResources().getString(R.string.close));
			alarmText.setText(detil.getAlarm() == 1 ? getResources().getString(R.string.open) : getResources().getString(R.string.close));
			openTheDoorText.setText(detil.getOpenTheDoor() == 1 ? getResources().getString(R.string.open) : getResources().getString(R.string.close));
			
			return view;
		}
	}
}