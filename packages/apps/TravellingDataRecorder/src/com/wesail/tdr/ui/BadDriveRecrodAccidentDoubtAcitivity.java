package com.wesail.tdr.ui;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.L;
import com.wesail.tdr.R;
import com.wesail.tdr.constant.ActionConstants;
import com.wesail.tdr.entity.AccidentDoubt;
import com.wesail.tdr.entity.AccidentDoubtDetil;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
public class BadDriveRecrodAccidentDoubtAcitivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private static Context mContext;
	private ListView listView;
	private int count = 0;
	private Activity self = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_bad_drive_record_accident_doubt);
		initView();
		
		IntentFilter intent1 = new IntentFilter();
        intent1.addAction(ActionConstants.LIST_DATA_ACCIDENT_DOUBT);
        registerReceiver(mReceiver, intent1);
        
        Intent intent2 = new Intent(ActionConstants.SEND_CMD);
        intent2.putExtra("cmd", new byte[]{(byte)0x10});
		sendOrderedBroadcast(intent2,null);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.list);
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
	private  class AccidentDoubtAdapter extends BaseAdapter{
		Resources res;
		LayoutInflater inflater;
		ArrayList<AccidentDoubt> list;
		public AccidentDoubtAdapter(Context context,ArrayList<AccidentDoubt> list) {
			inflater = LayoutInflater.from(context);
			res = getResources();
			this.list = list;
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.list_item_accident_doubt,parent,false);
			
			TextView tx1 = (TextView) view.findViewById(R.id.tx_1);
			TextView tx2 = (TextView) view.findViewById(R.id.tx_2);
			TextView tx3 = (TextView) view.findViewById(R.id.tx_3);
			tx3.setOnClickListener(new DetilOnClickListener(position));
			tx1.setText((position + 1) + "");
			tx2.setText(list.get(position).getTime());
			return view;
		}

		private class DetilOnClickListener implements OnClickListener{
			private int position;
			public DetilOnClickListener(int position) {
				this.position = position;
			}
			@Override
			public void onClick(View v) {
				if(list != null){
					list.get(position);
					Intent intent = new Intent(self,BadDriveRecrodAccidentDoubtDetilAcitivity.class);
					intent.putExtra("item", list.get(position));
					startActivity(intent);
				}
				else{
					L.e(this.getClass() + " list has error");
				}
			}
			
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e("overtime onReceive");
			String action = intent.getAction();
			if(ActionConstants.LIST_DATA_ACCIDENT_DOUBT.equalsIgnoreCase(action)){
				abortBroadcast();
				ArrayList<AccidentDoubt> list = intent.getParcelableArrayListExtra("list");
				if(list != null){	
					for (AccidentDoubt accidentDoubt : list) {
						L.e(accidentDoubt.getTime());
						for (AccidentDoubtDetil detil : accidentDoubt.getList()) {
							L.i(detil.getParkingBeforeSeconds() + " " +
									detil.getBrake() + " " +
									detil.getTurnLeft() + " " +
									detil.getTurnRight() + " " +
									detil.getFarLight() + " " +
									detil.getDippedLight() + " " +
									detil.getAlarm() + " " +
									detil.getOpenTheDoor() + " "
									);
						}
					}
					L.e(this.getClass() + " list size:" + list.size());
					listView.setAdapter(new AccidentDoubtAdapter(self, list));
				}else{
					L.e(this.getClass() + " receiver list is null!!!");
				}
				findViewById(R.id.loading).setVisibility(View.GONE);
			}
		}
	};
}