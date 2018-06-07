package com.wesail.tdr.ui;


import java.util.ArrayList;







import com.wesail.tdr.L;
import com.wesail.tdr.R;
import com.wesail.tdr.constant.ActionConstants;
import com.wesail.tdr.entity.DravelSpeed;
import com.wesail.tdr.entity.DravelSpeedDetil;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
public class RecordQueryDravelSpeedAcitivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private static Context mContext;
	private ListView listView;
	private View loading;
	private int count = 0;
	private Activity self = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_record_query_dravel_speed);
		initView();
		
		IntentFilter intent1 = new IntentFilter();
        intent1.addAction(ActionConstants.LIST_DATA_DRAVEL_SPEED);
        registerReceiver(mReceiver, intent1);
        
        Intent intent2 = new Intent(ActionConstants.SEND_CMD);
        intent2.putExtra("cmd", new byte[]{(byte)0x08});
		sendOrderedBroadcast(intent2,null);
		loading.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		Intent intent2 = new Intent(ActionConstants.SEND_CMD);
        intent2.putExtra("cmd", new byte[]{(byte)0x87});
		sendOrderedBroadcast(intent2,null);
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	
	private void initView() {
		findViewById(com.wesail.tdr.R.id.back).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.list);
		loading = findViewById(R.id.loading);
		Animator animator = AnimatorInflater.loadAnimator(this, R.anim.loading);
		animator.setTarget(loading);
		animator.start();
		/*Animation animation = AnimationUtils.loadAnimation(this, R.anim.loading2);
		loading.startAnimation(animation);*/
		
		
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
	private  class DriverSpeedAdapter extends BaseAdapter{
		private Resources res;
		private LayoutInflater inflater;
		private ArrayList<DravelSpeed> list;
		public DriverSpeedAdapter(Context context,ArrayList<DravelSpeed> list) {
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
			View view = inflater.inflate(R.layout.list_item_dravel_speed,parent,false);
			TextView tx1 = (TextView) view.findViewById(R.id.tx_1);
			TextView tx2 = (TextView) view.findViewById(R.id.tx_2);
			tx2.setOnClickListener(new DetilOnClickListener(position));
			tx1.setText(list.get(position).getTime());
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
					Intent intent = new Intent(self,RecordQueryDravelSpeedDetilAcitivity.class);
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
			L.e("dravel speed onReceive");
			String action = intent.getAction();
			if(ActionConstants.LIST_DATA_DRAVEL_SPEED.equalsIgnoreCase(action)){
				abortBroadcast();
				ArrayList<DravelSpeed> list = intent.getParcelableArrayListExtra("list");
				if(list != null){	
					/*for (DravelSpeed driverSpeed : list) {
						L.e(driverSpeed.getTime());
						for (DravelSpeedDetil detil : driverSpeed.getList()) {
							L.i(detil.getTime() + " " +
									detil.getSpeed() + " "
									);
						}
					}*/
					L.e(this.getClass() + " list size:" + list.size());
					listView.setAdapter(new DriverSpeedAdapter(self, list));
					loading.setVisibility(View.GONE);
				}else{
					L.e(this.getClass() + " receiver list is null!!!");
				}
			}
		}
	};
}