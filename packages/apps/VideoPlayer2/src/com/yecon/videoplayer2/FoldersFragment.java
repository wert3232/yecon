package com.yecon.videoplayer2;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.yecon.videoplayer2.FileListManager.DEVICE;
import com.yecon.videoplayer2.FileListManager.FileInfo;
import com.yecon.videoplayer2.FileScanner.FileType;
import com.yecon.videoplayer2.FileScanner.LoadCallback;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FoldersFragment extends Fragment implements OnItemClickListener {
	
	private boolean DEBUG = true;
	private FileScanner fileScanner = null;
	private final String TAG = "FoldersFragment";
	private Handler uiHandler;
	private ListView lvList;
	private View layoutError, layoutScan;
	private ArrayList<FileInfo>lFolders=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>files=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>lFiles=new ArrayList<FileInfo>();
	private FolderListAdapter folderListAdapter;
	private TrackListAdapter trackListAdapter;
	private VideoPlaybackListActivity activity;
	private FindPosTask<FileInfo, String> findPosTask;
	private FileListManager.DEVICE device;
		
	private final int MSG_FOLDERS_CHANGED = 1;
	//private final int MSG_FILES_CHANGED = 2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.folders_fragment, container, false);
		fileScanner = new FileScanner(getActivity(), FileType.VIDEO);
		lvList = (ListView) view.findViewById(R.id.lv_all);
		layoutError = view.findViewById(R.id.layout_error);
		layoutScan = view.findViewById(R.id.layout_scan);
		lvList.setOnItemClickListener(this);
		lvList.setOnTouchListener( new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(activity!=null){
					activity.returnLater();
				}
				return false;
			}
			
		});
		folderListAdapter = new FolderListAdapter(getActivity(), this);
		trackListAdapter = new TrackListAdapter(getActivity());
		initData();
		return view;
	}
		
	public FoldersFragment(DEVICE device) {
		super();
		this.device = device;
	}
		
	private void resetLanguage(){
		((TextView)layoutError.findViewById(R.id.tv_sd_error)).setText(R.string.no_sd_error);
		((TextView)layoutScan.findViewById(R.id.tv_scanning)).setText(R.string.file_scanning);
    }
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
    	if(newConfig.userSetLocale){
    		resetLanguage();
        }
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onAttach(Activity act) {
		// TODO Auto-generated method stub
		super.onAttach(act);
		this.activity = (VideoPlaybackListActivity) act;
		uiHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == MSG_FOLDERS_CHANGED){
					lFolders = activity.getFileListManager().getAllFiles(device.getValue());
					lvList.setAdapter(folderListAdapter);
					folderListAdapter.notifyDataSetChanged();
					
					if(lFolders.size()==0){
						if(activity.getFileListManager().isScanning(device.getValue())){
							lvList.setVisibility(View.GONE);
	                        layoutError.setVisibility(View.GONE);
	                        layoutScan.setVisibility(View.VISIBLE);
						}
						else{
							lvList.setVisibility(View.GONE);
	                        layoutError.setVisibility(View.VISIBLE);
	                        layoutScan.setVisibility(View.GONE);
						}						
					}
					else{
						lvList.setVisibility(View.VISIBLE);
                        layoutError.setVisibility(View.GONE);
                        layoutScan.setVisibility(View.GONE);
                        updateCurPos();
					}
				}
				super.handleMessage(msg);
			}
			
		};
	}

	private void initData() {
		// TODO Auto-generated method stub
		updateListView();
	}
	
//	@Override
//	public void onHiddenChanged(boolean hidden) {
//		// TODO Auto-generated method stub
//		super.onHiddenChanged(hidden);
//		if (!hidden) {
//			updateListView();
//		} else {
//			
//		}
//	}
	
	public void updateListView(){
		uiHandler.sendEmptyMessage(MSG_FOLDERS_CHANGED);
	}
	
	private int fileCountLastTime = 0;
	//mode : 0: foler, 1:files
	private void updateListViewData(int mode, boolean updateNow){
		int itemCount = 0;
		if(mode==0){
			uiHandler.post(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub					
					lvList.setAdapter(folderListAdapter);
					folderListAdapter.notifyDataSetChanged();
					updateCurPos();
				}
				
			});	
		}
		else if(1==mode){
			itemCount = files.size();
			if(itemCount>(fileCountLastTime + 50) || updateNow){
				fileCountLastTime = itemCount;
				Log.i(TAG, "total files count: " + itemCount);
				uiHandler.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub					
						lFiles.clear();
						lFiles.addAll(files);
						lvList.setAdapter(trackListAdapter);
						trackListAdapter.notifyDataSetChanged();
						updateCurPos();
					}
					
				});	
				
			}
		}
		
	}
	
	private void cancelUpdateCurPos(){
		if(findPosTask!=null){
        	findPosTask.cancel(true);
        	findPosTask=null;
        }
	}
	public void updateCurPos(){
		ListAdapter adapter = lvList.getAdapter();
		 List<FileInfo>list;
		if(adapter!=null){
			if(adapter instanceof FolderListAdapter){
				list = lFolders;
			}
			else{
				list = lFiles;
			}
		}
		else{
			return;
		}
		cancelUpdateCurPos();
		String playingPath = activity.getCurPath();
		if(playingPath==null || playingPath.length()==0){
			return;
		}
		FindPosTask.Input<FileInfo, String>input = new FindPosTask.Input<FileInfo, String>();
        input.setList(list);
        input.setBean(playingPath);
               
        findPosTask = new FindPosTask<FileInfo, String>(new FindPosTask.CompareCallback<FileInfo, String>() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean equal(FileInfo t1, String bean) {
				// TODO Auto-generated method stub
				if(t1!=null && bean!=null){
					if(t1.getMode() == 0){
						//folder
						try{
							return (bean.substring(0, bean.lastIndexOf("/")).equals(t1.getPath()));
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
					else{
						//files.
						return (bean.equals(t1.getPath()));
					}
				}
				return false;
			}

			@Override
			public void onFinished(int pos) {
				// TODO Auto-generated method stub
				ListAdapter adapter = lvList.getAdapter();
				if(adapter!=null){
					if(adapter instanceof FolderListAdapter){
						for(FileInfo file:lFolders){
							if(file.isPlaying()){
								file.setPlaying(false);
							}
						}
						if(pos>=0 && pos<lFolders.size()){
							lFolders.get(pos).setPlaying(true);		
							final int index = pos;
							uiHandler.postDelayed(new Runnable(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									 if(index<lvList.getFirstVisiblePosition()){
										 lvList.setSelection(index);
				        			 }
				        			 else if(index>lvList.getLastVisiblePosition()){
				        				 lvList.setSelection(index-(lvList.getLastVisiblePosition()-lvList.getFirstVisiblePosition()));
				        			 }
								}
								
							}, 10);
							
						}	
						folderListAdapter.notifyDataSetChanged();
					}
					else if(adapter instanceof TrackListAdapter){
						for(FileInfo file:lFiles){
							if(file.isPlaying()){
								file.setPlaying(false);
							}
						}
						if(pos>=0 && pos<lFiles.size()){
							lFiles.get(pos).setPlaying(true);							
							final int index = pos;
							uiHandler.postDelayed(new Runnable(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									 if(index<lvList.getFirstVisiblePosition()){
										 lvList.setSelection(index);
				        			 }
				        			 else if(index>lvList.getLastVisiblePosition()){
				        				 lvList.setSelection(index-(lvList.getLastVisiblePosition()-lvList.getFirstVisiblePosition()));
				        			 }
								}
								
							}, 10);
						}	
						trackListAdapter.notifyDataSetChanged();
					}
				}
				
			}
			
		});
        findPosTask.execute(input);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		cancelUpdateCurPos();
		super.onDestroyView();
	}
	
	private static class ViewHolder {
        TextView icon;
        TextView line1;
        TextView line2;
        ImageView play_indicator;
    }
	
	private String removeDevPathHead(String path){
		String ret = path;
		try{
			if(path!=null){
				if(path.startsWith("/mnt/")){
					ret =  path.substring("/mnt/".length());
				}
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	private String getPathName(String path){
		String ret = path;
		try{
			if(path!=null){
				ret = path.substring(path.lastIndexOf("/")+1);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	 private class FolderListAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;
	        private Context mContext;

	        private FoldersFragment mFragment;

	        private int mLastFirstVisible = 0;

	        public FolderListAdapter(Context context, FoldersFragment currentFragment) {
	            mInflater = LayoutInflater.from(context);
	            mContext = context;
	            mFragment = currentFragment;
	        }

	        public int getLastFirstVisible() {
	            return mLastFirstVisible;
	        }

	        public void setLastFirstVisible(int lastFirstVisible) {
	            this.mLastFirstVisible = lastFirstVisible;
	        }

	        @Override
	        public int getCount() {
	            return lFolders.size();
	        }

	        @Override
	        public Object getItem(int position) {
	            return lFolders.get(position);
	        }

	        @Override
	        public long getItemId(int position) {
	            return position;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            ViewHolder holder;
	            if (convertView == null) {
	                holder = new ViewHolder();
	                convertView = mInflater.inflate(R.layout.track_list_item_group, null);
	                holder.line1 = (TextView) convertView.findViewById(R.id.line1);
	                holder.line2 = (TextView) convertView.findViewById(R.id.line2);
	                holder.play_indicator = (ImageView) convertView.findViewById(R.id.play_indicator);
	                holder.icon = (TextView) convertView.findViewById(R.id.icon);
	                holder.icon.setBackgroundResource(R.drawable.ic_play_folder);

	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }

	            FileInfo folderInfo = mFragment.lFolders.get(position);

	            holder.line1.setText(getPathName(folderInfo.getPath()));
	            holder.line2.setText(removeDevPathHead(folderInfo.getPath()));

	            if (folderInfo.isPlaying()) {
	                holder.play_indicator.setVisibility(View.VISIBLE);
	            } else {
	                holder.play_indicator.setVisibility(View.INVISIBLE);
	            }

	            return convertView;
	        }

	    }

	 private class TrackListAdapter extends BaseAdapter {
	        private Context mContext;
	        private LayoutInflater mInflater;

	        public class ViewHolder {
	            TextView indexText;
	            TextView titleText;
	            TextView durationText;
	            TextView sizeText;
	            TextView modifiedText;
	            ImageView playIndicator;
	        }

	        public TrackListAdapter(Context context) {
	            this.mContext = context;
	            this.mInflater = (LayoutInflater) mContext
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        }

	        @Override
	        public int getCount() {
	            return lFiles.size();
	        }

	        @Override
	        public Object getItem(int p) {
	            return lFiles.get(p);
	        }

	        @Override
	        public long getItemId(int p) {
	            return p;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            ViewHolder holder;
	            if (convertView == null) {
	                holder = new ViewHolder();
	                convertView = mInflater.inflate(R.layout.list_item_layout, null);
	                holder.indexText = (TextView) convertView.findViewById(R.id.tv_index);
	                holder.titleText = (TextView) convertView.findViewById(R.id.tv_title);
	                holder.durationText = (TextView) convertView.findViewById(R.id.tv_duration);
	                holder.durationText.setVisibility(View.GONE);
	                holder.modifiedText = (TextView) convertView.findViewById(R.id.tv_modify);
	                holder.sizeText = (TextView) convertView.findViewById(R.id.tv_size);
	                holder.playIndicator = (ImageView) convertView.findViewById(R.id.play_indicator);
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }

	            // display the infomation of widget.
	            FileInfo info = lFiles.get(position);
	            if (null == info) {
	                return convertView;
	            }

	            holder.titleText.setText(info.getName());

	            /*
	             * if (info.duration != null) { String time = convertDurationToTime(info.duration);
	             * holder.durationText.setText(time); } else { holder.durationText.setText("00:00:00");
	             * }
	             */

	            long videosize = 100 * Long.valueOf(lFiles.get(position).getSize()) / (1024 * 1024);
	            float ft_size = videosize / (float) 100.0;
	            holder.sizeText.setText(ft_size + "M");
//	            if (info.modified != null) {
//	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//	                String st = sdf.format(new Date(Long.valueOf(info.modified)));
//	                holder.modifiedText.setText(st);
//	            }

	            holder.indexText.setText((position + 1) + ".");

	            //if (VideoPlayerApp.GetInstance().getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_NONE
	            //        && info.isPlaying) {
	            if(info.isPlaying()){
	                // convertView.setBackgroundResource(R.drawable.list_item_down);
	                holder.playIndicator.setVisibility(View.VISIBLE);
	            } else {
	                // convertView.setBackgroundResource(R.drawable.list_item_normal);
	                holder.playIndicator.setVisibility(View.INVISIBLE);
	            }

	            return convertView;
	        }
	    }
	 
	 public long getFileSizes(File f){//取得文件大小
	       long s=0;
	       if (f.exists()) {
	           FileInputStream fis = null;
	           try {
					fis = new FileInputStream(f);
					s= fis.available();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	          
	       }
	       return s;
	    }
	 
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		ListAdapter adapter = lvList.getAdapter();
		if(adapter instanceof FolderListAdapter){
			if(arg2>=0 && arg2<lFolders.size()){
				FileInfo folder = lFolders.get(arg2);				
				fileScanner.scanFolder(folder.getPath(), new LoadCallback(){

					@Override
					public void onLoad(String path, int fileType) {
						// TODO Auto-generated method stub
						File f = new File(path);
						files.add(new FileInfo(path, f.getName(), getFileSizes(f)));
						updateListViewData(1, false);
					}

					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						updateListViewData(1, true);
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						files.clear();
					}
					
				});
			}			
		}
		else if(adapter instanceof TrackListAdapter){
			Intent intent = new Intent(getActivity(), VideoPlaybackMainActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	        intent.putExtra(VideoPlayerContext.OPEN_LIST, "list");
	        if (VideoPlayerApp.GetInstance().getVideoPlayerContext().openFileList(lFiles, arg2, true)
	        		|| VideoPlaybackListActivity.getVideoPlaybackMainActivityInstance()==null) {
	            intent.putExtra(VideoPlayerContext.OPEN_POS, arg2);
	        }
	        startActivity(intent);
		}
	}

	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		boolean ret = false;
		ListAdapter adapter = lvList.getAdapter();
		if(adapter!=null && adapter instanceof TrackListAdapter){
			ret = true;
			updateListViewData(0, true);
		}		
		return ret;
	}	
	
}
