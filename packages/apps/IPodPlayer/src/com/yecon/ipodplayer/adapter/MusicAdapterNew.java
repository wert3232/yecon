package com.yecon.ipodplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import com.yecon.ipodplayer.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;

public class MusicAdapterNew extends BaseAdapter {
	private LayoutInflater mInflater;// 锟斤拷锟斤拷锟揭拷锟斤拷锟斤拷锟斤拷锟斤拷锟矫恳伙拷锟絠tem
										// view锟斤拷锟斤拷锟�锟斤拷锟斤拷

	private Context mContext = null;
	private List<MusicInfo> mMusicItem = new ArrayList<MusicInfo>();
	private List<String> items; // for text
	private int selectedPos = 0; //
	SparseBooleanArray selected;
	public MusicAdapterNew(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		selected = new SparseBooleanArray();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tNum = (TextView) convertView
					.findViewById(R.id.list_num);
			viewHolder.tName = (TextView) convertView
					.findViewById(R.id.list_name);

			
			viewHolder.tNum.setTextColor(mContext.getResources().getColor(R.color.white));
			viewHolder.tName.setTextColor(mContext.getResources().getColor(R.color.white));
			//by lzy modify
			if(selected.get(position)){
				
				convertView.setBackgroundResource(R.drawable.list_item_down);
				//convertView.setBackgroundColor(mContext.getResources().getColor(R.color.tranparent));
			}
			else
			{
				//convertView.setBackgroundColor(mContext.getResources().getColor(R.color.tranparent));
				convertView.setBackgroundResource(R.drawable.list_item_normal);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tNum.setText((position + 1) + "");
		viewHolder.tName.setText(mMusicItem.get(position).sName);
		// viewHolder.tName.setText( );
		return convertView;
	}

	static class ViewHolder {

		TextView tNum;
		TextView tName;
	}

	public int getCount() {

		return mMusicItem.size();
	}

	public void clear() {
		mMusicItem.clear();
	}

	public Object getItem(int position) {

		return mMusicItem.get(position);
	}

	public long getItemId(int position) {

		return position;
	}

	// public Object getItem(String string) {
	// int i;
	// for (i = 0; i < items.size(); i++) {
	// if (items.get(i).equals(string))
	// break;
	// }
	// return VHs.get(i);
	// }

	public void setSelectedPos(int index) {
		selectedPos = index;
	}

	public void setSelectedPos(String string) {
		int i;
		Log.e("INDEX", "-----------index-------------");
		for (i = 0; i < items.size(); i++) {
			if (items.get(i).equals(string))
				break;
		}
		Log.e("INDEX", "-----------index:" + i);
		selectedPos = i;
	}

	// public Object getCurItem() {
	// if (VHs.size() > selectedPos)
	// curItem = VHs.get(selectedPos);
	// return curItem;
	// }

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public void addItem(MusicInfo mInfo) {
		mMusicItem.add(mInfo);
	}

	public void setItem(List<MusicInfo> mList) {
		mMusicItem = mList;
	}

	public MusicInfo getMusicInfo() {
		return new MusicInfo();
	}

	public class MusicInfo {
		String sNum;
		String sName;

		public String getsNum() {
			return sNum;
		}

		public void setsNum(String sNum) {
			this.sNum = sNum;
		}

		public String getsName() {
			return sName;
		}

		public void setsName(String sName) {
			this.sName = sName;
		}
	}

}
