package com.adnroid.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.file.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PopuwindowAdapter extends BaseAdapter{
	 private Context context;
	 private List<ViewPopHolder> items;
	 private LayoutInflater mInflater;
	 private Drawable mIcon_folder;
	 
	public PopuwindowAdapter(Context context, File[] files) {
		// TODO Auto-generated constructor stub
		this.context=context;
		mInflater = LayoutInflater.from(context);
		items = new ArrayList<ViewPopHolder>();
		mIcon_folder = context.getResources().getDrawable(R.drawable.folder);
		inItems(files);
		this.context=context;
	}
	
	private void inItems(File[] files) {
		items.clear();
		if (files != null) {
			//ArrayList<File> _files = new ArrayList<File>();
			ArrayList<File> _folders = new ArrayList<File>();
			for (File file : files) {
				if (file.isDirectory()) {
					_folders.add(file);
				} else {
					//files.add(file);
				}
			}
			//_folders.addAll(_files);
			for (File f : _folders) {
				doItem(f);
			}
		}
	}
	
	private void doItem(File f) {
		ViewPopHolder vh = new ViewPopHolder();
		vh.path = f.getPath();
		vh.label = f.getName();
		vh.isDirectory = f.isDirectory();
		Drawable icon = mIcon_folder;
		vh.icon = icon;
		items.add(vh);
	}

	public static class ViewPopHolder {
		CharSequence label;
		Drawable icon;
		String path;
		boolean isDirectory;
		String type;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup par) {
		ViewPopHolder holder = items.get(position);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.grid_item1, par, false);
			}
			final TextView textView = (TextView) convertView.findViewById(R.id.name);
			//Bitmap bm = drawableToBitmap(holder.icon);
			//bm = Bitmap.createScaledBitmap(bm, 60, 60, true);
			
			textView.setCompoundDrawablesWithIntrinsicBounds(null, holder.icon, null, null);
			textView.setText(holder.label);
		
		return convertView;
	}
	private static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	//刷新
	public void refurbish(File[] files) {
		inItems(files);
	}

}
