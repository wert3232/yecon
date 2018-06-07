package com.yecon.OperateIntro;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	public ImageAdapter(Context c) {
		mContext = c;
	}

	public int getCount() {
		return Util.mImageIds.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		Util.index = position % Util.mImageIds.length;
		// Log.i("lzy", "getItemId..................Tag.save_index.........." +
		// Util.index);
		Util.setTitle(mContext);
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageview = new ImageView(mContext);
		int pos = position % (Util.mImageIds.length);
		imageview.setImageResource(Util.mImageIds[pos]);
		imageview.setLayoutParams(new Gallery.LayoutParams(Util.imgWidth, Util.imgHeight));
		imageview.setScaleType(ImageView.ScaleType.FIT_XY);
		return imageview;
	}

}
