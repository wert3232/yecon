package com.wesail.tdr.ui;


import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.L;
import com.wesail.tdr.R;
import com.wesail.tdr.constant.ActionConstants;

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
public class BadDriveRecrodOverspeedAcitivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private static Context mContext;
	private ListView listView;
	private int count = 0;
	private Activity self = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_bad_drive_record_overspeed);
		initView();
		
		IntentFilter intent = new IntentFilter();
        intent.addAction(ActionConstants.LIST_DATA_OVER_SPEED);
        registerReceiver(mReceiver, intent);
	}
	
	@Override
	protected void onResume() {
		Intent intent = new Intent(ActionConstants.SEND_CMD);
		intent.putExtra("cmd", new byte[]{(byte)0xb5});
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
	private  class OverSpeedAdapter extends BaseAdapter{
		private Resources res;
		private LayoutInflater inflater;
		private String[] items;
		public OverSpeedAdapter(Context context,String[] items) {
			inflater = LayoutInflater.from(context);
			res = getResources();
			this.items = items;
		}
		@Override
		public int getCount() {
			if(items == null){
				return 0;
			}else if(items.length > 0){
				return items.length;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(items != null){
				return items[position];
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.list_item_overspeed,parent,false);
			try {
				JSONObject jsonObject = new JSONObject(items[position]);
				String  time = jsonObject.getString("time");
				String  overtime = jsonObject.getString("overtime");
				String  overspeed_average_speed = jsonObject.getString("overspeed_average_speed");
				TextView tx1 = (TextView) view.findViewById(R.id.tx_1);
				TextView tx2 = (TextView) view.findViewById(R.id.tx_2);
				TextView tx3 = (TextView) view.findViewById(R.id.tx_3);
				tx1.setText(time);
				tx2.setText(overtime);
				tx3.setText(overspeed_average_speed);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
			return view;
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e("overtime onReceive");
			String action = intent.getAction();
			if(ActionConstants.LIST_DATA_OVER_SPEED.equalsIgnoreCase(action)){
				abortBroadcast();
				String[] items = intent.getStringArrayExtra("items");
				for(String str : items){
					L.i("get item :" + str );
				}
				listView.setAdapter(new OverSpeedAdapter(self, items));
				findViewById(R.id.loading).setVisibility(View.GONE);
			}
		}
	};
}