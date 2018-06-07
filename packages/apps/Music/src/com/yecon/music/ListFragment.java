
package com.yecon.music;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yecon.music.MusicUtils;

import static com.yecon.music.MusicUtils.Defs.*;
import static com.yecon.music.util.DebugUtil.*;

@SuppressLint("HandlerLeak")
public class ListFragment extends Fragment implements OnItemClickListener,
        OnTouchListener {
    private static final String TRACK_LIST_SHARE_FILE = "track_list_share";

    @SuppressWarnings("unused")
    private static int mLastListPosCourse = -1;
    @SuppressWarnings("unused")
    private static int mLastListPosFine = -1;

    private String mAlbumId;
    private String mArtistId;
    private long mSelectedId;

    private boolean mUseLastListPos = false;

    private RelativeLayout mLayoutError;
    private View vScanning;
    private TextView tvScanning;

    private ListView mTrackList;
    private TrackAdapter mTrackAdapter;

    private Activity mActivity;

    private ArrayList<MusicFileInfo> mPlayList = new ArrayList<MusicFileInfo>();

    private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	loadPlayList();
            //mTrackList.invalidateViews();            
            autoExit();
        }
    };

    private BroadcastReceiver mScanListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                //mReScanHandler.sendEmptyMessage(0);
            }
        }
    };

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        printLog("TrackListFragment - onCreateView - start");
        
        View view = initUI(inflater, container);
        
        initData();

        initRecevier();

        autoExit();

        printLog("TrackListFragment - onCreateView - end", false);

        return view;
    }

    class LoadPlayListTask extends AsyncTask<loadPlayListParameters,  Integer, ArrayList<MusicFileInfo>>{
    	
		@Override
		protected ArrayList<MusicFileInfo> doInBackground(loadPlayListParameters... arg0) {
			// TODO Auto-generated method stub
			loadPlayListParameters input = arg0[0];
			long []list = input.playList;
			long audioId = input.audioId;
			if(list==null || list.length==0){
				return null;
			}			
			String[] cols = new String[] {
	                MediaStore.Files.FileColumns._ID,
	                MediaStore.Files.FileColumns.FOLDER_NAME,
	                MediaStore.Files.FileColumns.PARENT,
	                MediaStore.Files.FileColumns.TITLE,
	                MediaStore.Audio.Media.ARTIST,
	        };        
	        Uri uri = MediaStore.Files.getContentUri("external");
	        String sortOrder = MediaStore.Audio.Media.TITLE_KEY;
	        ArrayList<MusicFileInfo> fileInfoArray = new ArrayList<MusicFileInfo>();

	        StringBuilder where = new StringBuilder();
	        final int ONCE = 500;
	        int steps = list.length/ONCE;
	        int left = list.length%ONCE;
	        int n = 0;
	        Cursor cursor;
	        
	        
	        for(int i = 0;i<steps;i++){
	        	
	        	where.setLength(0);
	            where.append(MediaStore.Files.FileColumns.TITLE);
	            where.append(" != '' AND (");
	            
	        	for(int j = 0;j<ONCE; j++){
		        	 where.append(MediaStore.Files.FileColumns._ID);
		             where.append("=");
		             where.append(list[n]);
		             if (j < (ONCE-1)) {
		                 where.append(" OR ");
		             }
		             n++;
	        	}
	        	
	        	where.append(") AND ");
	            where.append(MediaStore.Audio.Media.IS_MUSIC);
	            where.append("=1");

	            cursor = mActivity.getContentResolver().query(uri, cols, where.toString(), null, sortOrder);
	            if (cursor == null) {
	                continue;
	            }

	            printLog("count: " + cursor.getCount(), false);

	            int index = 0;

	            cursor.moveToFirst();

	            int fileIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
	            int folderIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.FOLDER_NAME);
	            int parentIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT);
	            int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
	            int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

	            while (!cursor.isAfterLast()) {
	                long fileId = cursor.getLong(fileIdx);
	                String folder = cursor.getString(folderIdx);
	                MusicFileInfo fileInfo = new MusicFileInfo();
	                fileInfo.setId(fileId);
	                fileInfo.setParent(cursor.getInt(parentIdx));
	                fileInfo.setDirName(folder);
	                fileInfo.setDirPath(folder);
	                fileInfo.setFilename(cursor.getString(titleIdx));
	                fileInfo.setArtistName(cursor.getString(artistIdx));
	                if (fileId == audioId) {
	                    fileInfo.setIsPlaying(true);
	                } else {
	                    fileInfo.setIsPlaying(false);
	                }
	                fileInfoArray.add(fileInfo);
	                cursor.moveToNext();
	            }
	            cursor.close();
	        }
	        
	        if(left>0){
	        	
	        	where.setLength(0);
	            where.append(MediaStore.Files.FileColumns.TITLE);
	            where.append(" != '' AND (");
	            
	        	 for (int j = 0; j < left; j++) {        	
	                 where.append(MediaStore.Files.FileColumns._ID);
	                 where.append("=");
	                 where.append(list[n]);
	                 if (j  < (left - 1)) {
	                     where.append(" OR ");
	                 }
	                 n++;
	             }
	        	 
	        	 where.append(") AND ");
	             where.append(MediaStore.Audio.Media.IS_MUSIC);
	             where.append("=1");

	             cursor = mActivity.getContentResolver().query(uri, cols, where.toString(), null, sortOrder);
	             if (cursor == null) {
	            	 return fileInfoArray;
	             }

	             printLog("count: " + cursor.getCount(), false);

	             int index = 0;

	             cursor.moveToFirst();

	             int fileIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
	             int folderIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.FOLDER_NAME);
	             int parentIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT);
	             int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
	             int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

	             while (!cursor.isAfterLast()) {
	                 long fileId = cursor.getLong(fileIdx);
	                 String folder = cursor.getString(folderIdx);
	                 MusicFileInfo fileInfo = new MusicFileInfo();
	                 fileInfo.setId(fileId);
	                 fileInfo.setParent(cursor.getInt(parentIdx));
	                 fileInfo.setDirName(folder);
	                 fileInfo.setDirPath(folder);
	                 fileInfo.setFilename(cursor.getString(titleIdx));
	                 fileInfo.setArtistName(cursor.getString(artistIdx));
	                 if (fileId == audioId) {
	                     fileInfo.setIsPlaying(true);
	                 } else {
	                     fileInfo.setIsPlaying(false);
	                 }
	                 fileInfoArray.add(fileInfo);
	                 cursor.moveToNext();
	             }
	             cursor.close();
	             
	        }
	       
	        printLog("getPlayList - end", false);
			return fileInfoArray;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if(mPlayList.size()==0){
				vScanning.setVisibility(View.VISIBLE);
				mLayoutError.setVisibility(View.GONE);
			}			
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<MusicFileInfo> result) {
			// TODO Auto-generated method stub
			vScanning.setVisibility(View.GONE);
			if(mTrackAdapter!=null){
				mPlayList.clear();
				if(result!=null && result.size()>0){
					mPlayList.addAll(result);
				}
				mTrackAdapter.notifyDataSetChanged();				
				updateUi();
			}			
			super.onPostExecute(result);
		}

		
	};
	
	class loadPlayListParameters{
		long []playList;
		long audioId;
	}
    private AsyncTask<loadPlayListParameters,  Integer, ArrayList<MusicFileInfo>> taskLoadPlayList=null;
    private void loadPlayList(){
    	try {
    		loadPlayListParameters param = new loadPlayListParameters();
    		param.playList = ((MusicTrackActivity) mActivity).getService().getQueue();
    		param.audioId = ((MusicTrackActivity) mActivity).getService().getAudioId();
            if(param.playList!=null && param.playList.length>0){
            	if(taskLoadPlayList!=null){
            		taskLoadPlayList.cancel(true);
            	}
            	taskLoadPlayList = new LoadPlayListTask();
            	taskLoadPlayList.execute(param);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void initData() {
    	loadPlayList();

        SharedPreferences prefs = mActivity.getSharedPreferences(TRACK_LIST_SHARE_FILE,
                Context.MODE_PRIVATE);
        if (prefs != null) {
            mSelectedId = prefs.getLong("selectedtrack", 0);
            mAlbumId = prefs.getString("album", "");
            mArtistId = prefs.getString("artist", "");

            printLog("TrackListFragment - onCreateView - mSelectedId: " + mSelectedId
                    + " - mAlbumId: " + mAlbumId + " - mArtistId: " + mArtistId, false);
        }
    }

    private View initUI(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.yecon_list_fragment_layout, container, false);

        mLayoutError = (RelativeLayout) view.findViewById(R.id.layout_error);
        vScanning = view.findViewById(R.id.layout_scan);
        tvScanning = (TextView)view.findViewById(R.id.tvScanning);
        tvScanning.setVisibility(view.GONE);

        mTrackList = (ListView) view.findViewById(R.id.lv_tracklist);
        mTrackList.setCacheColorHint(0);
        mTrackList.setOnItemClickListener(this);
        mTrackList.setTextFilterEnabled(true);
        mTrackList.setOnTouchListener(this);

        mTrackAdapter = new TrackAdapter(mActivity);
        if (mTrackAdapter != null) {
            mTrackList.setAdapter(mTrackAdapter);
        }

        return view;
    }

    private void initRecevier() {
//        IntentFilter f = new IntentFilter();
//        f.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
//        f.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
//        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
//        f.addDataScheme("file");
//        mActivity.registerReceiver(mScanListener, f);
        
        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.QUEUE_CHANGED);
        mActivity.registerReceiver(mTrackListListener, f);
        
    }

    @Override
    public void onResume() {
        super.onResume();       
    }

    @Override
    public void onPause() {
        super.onPause();        
    }
    
    
    @Override
	public void onStop() {
		// TODO Auto-generated method stub    	
		super.onStop();
	}

	@Override
    public void onDestroyView() {
        super.onDestroyView();
        printLog("TrackListFragment - onDestroyView ");
        
        mActivity.unregisterReceiver(mTrackListListener);
    	uiHandler.removeCallbacksAndMessages(null);
    	if(taskLoadPlayList!=null){
    		taskLoadPlayList.cancel(true);
    		taskLoadPlayList=null;
    	}    	
    	
        SharedPreferences prefs = mActivity.getSharedPreferences(TRACK_LIST_SHARE_FILE,
                Context.MODE_PRIVATE);
        if (prefs != null) {
            Editor editor = prefs.edit();
            editor.putLong("selectedtrack", mSelectedId);
            editor.putString("artist", mArtistId);
            editor.putString("album", mAlbumId);
            editor.commit();

            printLog("TrackListFragment - onDestroyView - mSelectedId: " + mSelectedId
                    + " - mAlbumId: " + mAlbumId + " - mArtistId: " + mArtistId, false);
        }

        if (mTrackList != null) {
            if (mUseLastListPos) {
                mLastListPosCourse = mTrackList.getFirstVisiblePosition();
                View cv = mTrackList.getChildAt(0);
                if (cv != null) {
                    mLastListPosFine = cv.getTop();
                }
            }
        }

        //mActivity.unregisterReceiver(mScanListener);

        mTrackAdapter = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mPlayList.size()==0) {
            return;
        }

        int size = mPlayList.size();
        long[] playList = new long[size];
        int index = 0;
        for (MusicFileInfo fileInfo : mPlayList) {
            playList[index++] = fileInfo.getId();
        }

        MusicUtils.playAll(mActivity, playList, position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case SCAN_DONE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    mActivity.finish();
                } else {
                    // getTrackCursor(mAdapter.getQueryHandler(), null, true);
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        autoExit();

        return false;
    }

    private boolean updateUi() {
        if (!checkStorageExist() || mPlayList.size()==0) {
            mLayoutError.setVisibility(View.VISIBLE);
            //uiHandler.sendEmptyMessageDelayed(0, 1000);
            return true;
        } else {
            mLayoutError.setVisibility(View.GONE);
            uiHandler.postDelayed(new Runnable(){
            	final int pos = getPlayingPosition();
				@Override
				public void run() {
					// TODO Auto-generated method stub
					 if(pos<mTrackList.getFirstVisiblePosition()){
        				 mTrackList.setSelection(pos);
        			 }
        			 else if(pos>mTrackList.getLastVisiblePosition()){
        				 mTrackList.setSelection(pos-(mTrackList.getLastVisiblePosition()-mTrackList.getFirstVisiblePosition()));
        			 }
				}
            	
            }, 20);
            return false;
        }
    }

    private int getPlayingPosition() {
        int position = 0;
        long currentId = MusicUtils.getCurrentAudioId();
        for (MusicFileInfo info : mPlayList) {
            if (currentId == info.getId()) {
                break;
            }
            position++;
        }
        return position;
    }

    private void autoExit() {
        ((MusicTrackActivity) mActivity).autoExit();
    }

    private boolean checkStorageExist() {
        return ((MusicTrackActivity) mActivity).checkStorageExist();
    }

    private static class ViewHolder {
        TextView icon;
        TextView line1;
        TextView line2;
        ImageView play_indicator;
    }

    private class TrackAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public TrackAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
           return mPlayList.size();
        }

        @Override
        public Object getItem(int position) {
            if(position>=0 && position<mPlayList.size()){
            	return mPlayList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mPlayList.size()==0) {
                return null;
            }

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.track_list_item_group, null);
                holder.line1 = (TextView) convertView.findViewById(R.id.line1);
                holder.line2 = (TextView) convertView.findViewById(R.id.line2);
                holder.play_indicator = (ImageView) convertView.findViewById(R.id.play_indicator);
                holder.icon = (TextView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.icon.setText((position + 1) + ". ");

            MusicFileInfo fileInfo = mPlayList.get(position);
            holder.line1.setText(fileInfo.getFilename());
            holder.line2.setText(fileInfo.getArtistName());

            long fileId = fileInfo.getId();
            try {
                if (fileId == MusicUtils.sService.getAudioId()) {
                    holder.play_indicator.setVisibility(View.VISIBLE);
                } else {
                    holder.play_indicator.setVisibility(View.INVISIBLE);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

}
