package com.yecon.carsetting.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yecon.carsetting.R;
import com.yecon.carsetting.bean.AppInfo;

public class AppListAdpter extends BaseAdapter {
	private Context mContext;
	private List<AppInfo> mAppInfoList;

	// public static ArrayList<onCheckBoxListener> ehList = new
	// ArrayList<onCheckBoxListener>();

	public AppListAdpter(Context ct, List<AppInfo> datas) {
		this.mContext = ct;
		this.mAppInfoList = datas;
	}

	public void updateListView(List<AppInfo> list) {
		this.mAppInfoList = list;
		notifyDataSetChanged();
	}

	public void remove(AppInfo mAppList) {
		this.mAppInfoList.remove(mAppList);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return mAppInfoList.size();
	}

	@Override
	public AppInfo getItem(int position) {
		return mAppInfoList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final int index = position;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.navigation_all_list_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.app_title = (TextView) convertView.findViewById(R.id.app_info_title);
			viewHolder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon_imageView);
			viewHolder.app_ck = (CheckBox) convertView.findViewById(R.id.app_info_ck);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final AppInfo mAppInfo = mAppInfoList.get(position);
		final String appName = mAppInfo.getAppName();
		final Drawable appIcon = mAppInfo.getAppIcon();

		final String PkgName = mAppInfo.getPackageName(); // Jade add;

		MarginLayoutParams margin9 = new MarginLayoutParams(viewHolder.app_icon.getLayoutParams());
		margin9.setMargins(20, 10, 20, 10);
		RelativeLayout.LayoutParams layoutParams9 = new RelativeLayout.LayoutParams(margin9);
		layoutParams9.height = 60;
		layoutParams9.width = 60;
		viewHolder.app_icon.setLayoutParams(layoutParams9);
		viewHolder.app_icon.setImageDrawable(appIcon);
		viewHolder.app_title.setText(appName);
		// viewHolder.app_title.setText(appName+": "+PkgName); //Jade test
		// package name;

		viewHolder.app_ck.setChecked(mAppInfo.isCheck());
		viewHolder.app_ck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				// for(onCheckBoxListener mBox:ehList){
				if (mListener != null)
					mListener.onCheckedChanged(cb.isChecked(), mAppInfo);
				// }
				// mChecked.set(p, cb.isChecked());
			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView app_title;
		ImageView app_icon;
		CheckBox app_ck;

	}

	public interface onCheckBoxListener {
		public void onCheckedChanged(boolean isCheck, AppInfo mAppInfo);
	}

	public static onCheckBoxListener mListener;

	public static void setCheckBoxListener(onCheckBoxListener mL) {
		mListener = mL;
	}

}
