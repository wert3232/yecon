package com.autochips.dvr;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Context mContext;
	private FileSearch mFileSearch;

	public final class ViewHolder {
		public TextView id;
		public TextView name;
		public ImageButton lockBtn;
		public ImageButton delBtn;
	}
	
	public FileListAdapter(Context context, FileSearch search) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mFileSearch = search;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFileSearch.mFileArrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		if (convertView == null) {
			holder=new ViewHolder();  
			convertView = mInflater.inflate(R.layout.listview_item, null);
			holder.id = (TextView)convertView.findViewById(R.id.video_id);
			holder.name = (TextView)convertView.findViewById(R.id.video_name);
			holder.lockBtn = (ImageButton)convertView.findViewById(R.id.lock_btn);
			holder.delBtn = (ImageButton)convertView.findViewById(R.id.del_btn);
			convertView.setTag(holder);				
		}else {				
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.id.setText((position+1)+".");
		holder.name.setText(tools.getOnlyFile(mFileSearch.mFileArrayList.get(position)));
		
		holder.id.setTextColor(mContext.getResources().getColor(R.color.white));
		holder.name.setTextColor(mContext.getResources().getColor(R.color.white));
		
		holder.lockBtn.setTag(position);
		holder.lockBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		holder.lockBtn.setFocusable(false);
		holder.delBtn.setTag(position);
		holder.delBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setMessage(mContext.getResources().getString(R.string.sure_delete));
				builder.setTitle(mContext.getResources().getString(R.string.del_hint));
				builder.setPositiveButton(mContext.getResources().getString(R.string.del_ok), new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Message msg=new Message();
						msg.what = MsgDefine.MSG_DELETE_FILE;
						msg.arg1=position;
						FileListActivity.getHandler().sendMessage(msg);
					}
				});
				builder.setNegativeButton(mContext.getResources().getString(R.string.del_cancel), new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});

				builder.create().show();
			}
		});
		holder.delBtn.setFocusable(false);
		
		if (position == FileListActivity.mCurListViewPos) {
			convertView.setBackgroundResource(R.drawable.list_item_down); 
		}else {
			convertView.setBackgroundResource(R.drawable.list_item_normal);
		}

		
		return convertView;
	}

}
