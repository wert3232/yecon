package com.wesail.tdr.ui;


import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.L;
import com.wesail.tdr.R;
import com.wesail.tdr.entity.DravelSpeed;
import com.wesail.tdr.entity.DravelSpeedDetil;

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
public class RecordQueryDravelSpeedDetilAcitivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private static Context mContext;
	private ListView listView;
	private TextView detilInfoText;
	private int count = 0;
	private DravelSpeed DriverSpeed;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_record_query_dravel_speed_detil);
		initView();
		
	}
	
	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.list);
		detilInfoText = (TextView) findViewById(R.id.detil_info);
		DriverSpeed = getIntent().getParcelableExtra("item");
		detilInfoText.setText(DriverSpeed.getTime());
		if(DriverSpeed != null){
			DriverSpeedDetilAdapter adapter = new DriverSpeedDetilAdapter(this, DriverSpeed.getList());
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
	private  class DriverSpeedDetilAdapter extends BaseAdapter{
		private Resources res;
		private LayoutInflater inflater;
		private ArrayList<DravelSpeedDetil> driverSpeedDetils;
		public DriverSpeedDetilAdapter(Context context,ArrayList<DravelSpeedDetil> doubtDetils) {
			inflater = LayoutInflater.from(context);
			res = getResources();
			this.driverSpeedDetils = doubtDetils;
		}
		@Override
		public int getCount() {
			return driverSpeedDetils.size();
		}

		@Override
		public Object getItem(int position) {
			return driverSpeedDetils.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.list_item_dravel_speed_details,parent,false);
			DravelSpeedDetil detil = driverSpeedDetils.get(position);
			TextView tx1 = (TextView)view.findViewById(R.id.tx_1);
			TextView tx2= (TextView)view.findViewById(R.id.tx_2);
			tx1.setText(detil.getTime());
			tx2.setText(detil.getSpeed() + "");		
			return view;
		}
	}
}