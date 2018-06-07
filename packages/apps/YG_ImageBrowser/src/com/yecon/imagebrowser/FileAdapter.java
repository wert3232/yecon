package com.yecon.imagebrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yecon.imagebrowser.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FileAdapter extends BaseAdapter {
	private String[] files;

	private LayoutInflater mLayoutInflater;

	public FileAdapter(Context context, String[] files) {
		this.files = files;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return files == null ? 0 : files.length;
	}

	@Override
	public String getItem(int position) {
		return files[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyGridViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyGridViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.file_item, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.album_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyGridViewHolder) convertView.getTag();
		}
		String url = getItem(position);

		ImageLoader.getInstance().displayImage(url, viewHolder.imageView);
		return convertView;
	}

	private static class MyGridViewHolder {
		ImageView imageView;
	}
}
