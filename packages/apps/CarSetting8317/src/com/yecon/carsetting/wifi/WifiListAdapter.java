package com.yecon.carsetting.wifi;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yecon.carsetting.R;


public class WifiListAdapter extends ArrayAdapter<WifiItem> {

	private List<WifiItem> listWifi;
	private Context context;
	private int resourceId;
	
	public WifiListAdapter(Context context, int resource, List<WifiItem> objects) {
		super(context, resource, objects);
		
		this.listWifi = objects;
		this.resourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		WifiItem wifiItem = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.wifiName = (TextView) view.findViewById(R.id.list_wifi_name);
			viewHolder.describes = (TextView) view.findViewById(R.id.list_wifi_describes);
			viewHolder.connectStatus = (TextView) view.findViewById(R.id.list_wifi_connect_status);
			viewHolder.wifiLevel = (ImageView) view.findViewById(R.id.list_wifi_icon);

			view.setTag(viewHolder);
			
		}else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.wifiName.setText(wifiItem.getWifiName());
		viewHolder.describes.setText(wifiItem.getDescribes());
		viewHolder.connectStatus.setText(wifiItem.getConnected());
		//viewHolder.connectStatus.setText("22222");
		
		viewHolder.wifiLevel.setImageResource(wifiItem.getImageLevelID()); //把id读出后，再写�?

		return view;
	}
	
	class ViewHolder {
		TextView wifiName;
		TextView describes;
		TextView connectStatus;
		ImageView wifiLevel;
		ImageView wifiLock;
	}
	
	
}
