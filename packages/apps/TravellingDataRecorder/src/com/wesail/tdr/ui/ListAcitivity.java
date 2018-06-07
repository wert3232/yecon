package com.wesail.tdr.ui;


import org.json.JSONException;
import org.json.JSONObject;

import com.wesail.tdr.R;

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
//no use,this is a demo
public class ListAcitivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private static Context mContext;
	private ListView listView;
	private int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_main_menu);
		initView();
		
	}
	
	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
//		listView = (ListView) findViewById(R.id.about_list);
//		listView.setAdapter(new AboutAdapter(this));
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
	private  class AboutAdapter extends BaseAdapter{
		Resources res;
		String[] aboutList;
		LayoutInflater inflater;
		public AboutAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			res = getResources();
//			aboutList = res.getStringArray(R.array.about_list);
		}
		@Override
		public int getCount() {
			return aboutList.length;
		}

		@Override
		public Object getItem(int position) {
			return aboutList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.list_item,parent,false);
			try {
				JSONObject jsonObject = new JSONObject(aboutList[position]);
				TextView tx1 = (TextView) view.findViewById(R.id.tx_1);
				TextView tx2 = (TextView) view.findViewById(R.id.tx_2);
				tx1.setText(jsonObject.getString("name"));
				tx2.setText(jsonObject.getString("content")); 
				return view;
			} catch (JSONException e) {
                e.printStackTrace();
            }
			return view;
		}
	}
}