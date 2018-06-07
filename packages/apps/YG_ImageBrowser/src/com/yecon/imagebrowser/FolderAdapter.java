package com.yecon.imagebrowser;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yecon.imagebrowser.R;

public class FolderAdapter extends BaseAdapter{

	private ArrayList<FolderItem> mList;
	private LayoutInflater mInflater;
	
	public FolderAdapter(Context context, ArrayList<FolderItem> list) {
		mInflater = LayoutInflater.from(context);
		this.mList=list;
	}

	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public FolderItem getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).folder_id;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.folder_item, parent, false);
			holder.avator=(ImageView)convertView.findViewById(R.id.album_image);
			holder.name=(TextView)convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final FolderItem folder = getItem(position);
		
		holder.avator.setVisibility(View.VISIBLE);
		holder.name.setText(String.format("%d", folder.amount));
		int endIndex = folder.folder.lastIndexOf(File.separator);
		String folderName = folder.folder.substring(endIndex + 1);
		holder.content.setText(folderName);
		if(folder.files!=null&&folder.files.size()>0){
			ImageLoader.getInstance().displayImage(folder.files.get(0), holder.avator);
		}
		return convertView;
	}
	
	private static class ViewHolder {
		public TextView name;
		public ImageView avator;
		TextView content;
	}
}
