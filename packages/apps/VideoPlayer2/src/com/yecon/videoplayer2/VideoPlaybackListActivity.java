
package com.yecon.videoplayer2;

import static com.yecon.videoplayer2.DebugUtil.*;
import static android.constant.YeconConstants.*;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import com.autochips.storage.EnvironmentATC;
import com.yecon.common.SourceManager;
import com.yecon.videoplayer2.FileListManager.ListLoadingNotify;
import com.yecon.videoplayer2.FileListManager.MediaPlugCallback;
import com.yecon.videoplayer2.FileScanner.FileType;
import com.yecon.videoplayer2.ForeBackGroundHandler.ForeBackGroundLisenter;
import com.yecon.videoplayer2.VideoPlaybackMainActivity.MediaType;
import com.yecon.videoplayer2.VideoPlaybackMainActivity.PlayStatus;

public class VideoPlaybackListActivity extends Activity implements OnClickListener, OnTouchListener {
    private static final String TAG = "VideoPlaybackListActivity";
    public static final int RETURN_TIME_OUT = 8000;
    //private VideoPlaybackAllListFragment mAllListFragment;
    private final int MEDIA_DEVICE_COUNT = 8;
    private FoldersFragment foldersFragment[] = new FoldersFragment[MEDIA_DEVICE_COUNT];
    private FragmentManager mFragmentManager;
    private static Object sourceTocken;
    private VideoPlayerApp videoPlayerApp;
    private View btnPlaying;
    private View []btnTabs = new View[MEDIA_DEVICE_COUNT];
    private View vLayoutTabs, vLayoutError, vLayoutScan;
    private FileScanner fileScanner = null;
	private EnvironmentATC envATC;
	private FileListManager fileListManager;
	public static void setVideoPlaybackMainActivityInstance(VideoPlaybackMainActivity instance){
		videoInstance = instance;
	}
	public static VideoPlaybackMainActivity getVideoPlaybackMainActivityInstance(){
		return videoInstance;
	}
	private static VideoPlaybackMainActivity videoInstance = null;
	
	public FileListManager getFileListManager(){
		return fileListManager;
	}
    public static class VideoInfo {
        String filePath;
        String mimeType;
        Boolean isBitmapFake;
        String title;
        String duration;
        String size;
        String modified;
        MediaType storage;
        long id;
        boolean isPlaying;
        VideoInfo(){
        	
        }
        VideoInfo(String filePath, String title, int duration){
        	this.filePath = filePath;
        	this.title = title;
        	this.duration = Integer.toString(duration);
        }
    }

    private Handler uiHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 88){
				PlayStatus playStatus = VideoPlayerApp.GetInstance().getVideoPlayerContext().getPlayStatus();
				if(playStatus!=PlayStatus.STATUS_STOP && playStatus!=PlayStatus.STATUS_NONE){
					//it's playing , return to playing
					Intent intent = new Intent(VideoPlaybackListActivity.this, VideoPlaybackMainActivity.class);
			        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			        startActivity(intent);
				}
			}
		}
		
	};
	
	private void exitApp(){
		if (videoInstance != null) {
			videoInstance.finish();
			videoInstance = null;
		}
		finish();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "list onCreate");
        super.onCreate(savedInstanceState);
        videoPlayerApp = VideoPlayerApp.GetInstance();
        videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_NONE);
        videoPlayerApp.getVideoPlayerContext().clearPlayList();
        fileScanner = new FileScanner(this, FileType.VIDEO);
		envATC = new EnvironmentATC(this);

        sourceTocken = SourceManager.registerSourceListener(getApplicationContext(),
                SourceManager.SRC_NO.video);
        SourceManager.setAudioFocusNotify(VideoPlaybackListActivity.getSourceTocken(),
                new SourceManager.AudioFocusNotify() {

                    @Override
                    public void onAudioFocusChange(int action) {
                        switch (action) {
                            case AudioManager.AUDIOFOCUS_LOSS:
                            	Log.i(TAG, "AUDIOFOCUS_LOSS");
                            	exitApp();
                                break;

                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            	if(videoInstance != null){
                            		VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
                            		PlayStatus playStatus =playerContext.getPlayStatus();
                            		if(playStatus == PlayStatus.STATUS_PLAY 
                            				|| playStatus == PlayStatus.STATUS_FF
                            				|| playStatus == PlayStatus.STATUS_RW){
                            			videoInstance.doPlayPause(false);
                            			playerContext.setPausedByAudioFocus(true);
                            		}
                            	}
                                break;

                            case AudioManager.AUDIOFOCUS_GAIN:
                            	VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
                            	if(playerContext.isPausedByAudioFocus()){
                            		playerContext.setPausedByAudioFocus(false);
                        			if(videoInstance != null){
                        				videoInstance.doPlayPause(true);
                                	}
                            	}                            	
                                break;
                        }
                    }
                });

        initUI();
        
        initData();
        
        resume2Play();
        
        Log.d("Playlist", "create");
    }
    
    public static void resume2PlayByAudioFocus(){
    	VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
    	if(playerContext.isPausedByAudioFocus()){
    		playerContext.setPausedByAudioFocus(false);
			if(videoInstance != null){
				videoInstance.doPlayPause(true);
        	}
    	}       
    }
    
    private void resetLanguage(){
    	((TextView)btnTabs[0]).setText(R.string.str_tab_sd1);
    	((TextView)btnTabs[1]).setText(R.string.str_tab_sd2);
    	((TextView)btnTabs[2]).setText(R.string.str_tab_usb1);
    	((TextView)btnTabs[3]).setText(R.string.str_tab_usb2);
    	((TextView)btnTabs[4]).setText(R.string.str_tab_usb3);
    	((TextView)btnTabs[5]).setText(R.string.str_tab_usb4);
    	((TextView)btnTabs[6]).setText(R.string.str_tab_usb5);
    	((TextView)btnTabs[7]).setText(R.string.str_tab_internal);
    }
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
    	if(newConfig.userSetLocale){
    		resetLanguage();
        }
		super.onConfigurationChanged(newConfig);
	}
    
	private void showLoading(){
    	
    }

    private void showListView(){
    	
    }
    
    private void resume2Play() {
		// TODO Auto-generated method stub
    	if(!videoPlayerApp.getVideoPlayerContext().resume2Play(this)){
    		
    	}
	}
    

	@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        printLog("VideoPlaybackListActivity - onNewIntent");
        this.setIntent(intent);
    }
	
	private void checkFolderFragmentObject(){
		if(foldersFragment[0]==null)
			foldersFragment[0] = new FoldersFragment(FileListManager.DEVICE.sd1);	
		if(foldersFragment[1]==null)
			foldersFragment[1] = new FoldersFragment(FileListManager.DEVICE.sd2);
		if(foldersFragment[2]==null)
			foldersFragment[2] = new FoldersFragment(FileListManager.DEVICE.usb1);
		if(foldersFragment[3]==null)
			foldersFragment[3] = new FoldersFragment(FileListManager.DEVICE.usb2);
		if(foldersFragment[4]==null)
			foldersFragment[4] = new FoldersFragment(FileListManager.DEVICE.usb3);
		if(foldersFragment[5]==null)
			foldersFragment[5] = new FoldersFragment(FileListManager.DEVICE.usb4);
		if(foldersFragment[6]==null)
			foldersFragment[6] = new FoldersFragment(FileListManager.DEVICE.usb5);
		if(foldersFragment[7]==null)
			foldersFragment[7] = new FoldersFragment(FileListManager.DEVICE.internal);
	}
    private void initData() {    	
        mFragmentManager = getFragmentManager();
        //mAllListFragment = new VideoPlaybackAllListFragment();
        checkFolderFragmentObject();

        IntentFilter filter = new IntentFilter();
        filter.addAction(VideoPlayerContext.ACTION_PLAY_FILE_CHANGED);
        filter.addAction(ACTION_QB_POWEROFF);
        filter.addAction(ACTION_QB_PREPOWEROFF);
        registerReceiver(mPlayStatusReceiver, filter);
        
        fileListManager = new FileListManager(this, FileScanner.FileType.VIDEO, new ListLoadingNotify(){

			@Override
			public void onListChanged() {
				// TODO Auto-generated method stub
				uiHandler.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						updateTabsVisiblity();
						for(int i=0;i<foldersFragment.length && i<btnTabs.length;i++){
		            		if(btnTabs[i].getVisibility()==View.VISIBLE
		            				&& foldersFragment[i].isAdded()){
		            			foldersFragment[i].updateListView();
		                	}
		            	} 						
					}
					
				});
				
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				uiHandler.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//vLayoutTabs.setVisibility(View.GONE);
				    	//vLayoutScan.setVisibility(View.VISIBLE);
				    	//vLayoutError.setVisibility(View.GONE);
						updateTabsVisiblity();
				    	for(int i=0;i<foldersFragment.length && i<btnTabs.length;i++){
		            		if(btnTabs[i].getVisibility()==View.VISIBLE
		            				&& foldersFragment[i].isAdded()){
		            			foldersFragment[i].updateListView();
		                	}
		            	} 	
					}
					
				});
				
			}

			@Override
			public void onDeviceIsEmpty() {
				// TODO Auto-generated method stub
				uiHandler.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						 SourceManager.hotPlugSource(VideoPlaybackListActivity.this, SourceManager.SRC_NO.video, "", false);
						 exitApp();
					}
					
				});
			}
			
		});
		fileListManager.init();
		fileListManager.setMediaPlugCallback(new MediaPlugCallback(){

			@Override
			public void onMediaPlug(String devPath, boolean insert) {
				// TODO Auto-generated method stub
				if(!insert){
					VideoPlayerApp.GetInstance().getVideoPlayerContext().removePlayListByDevPath(devPath);
				}
			}
			
		});
		fileListManager.reloadAllDevicesFiles();
		updateTabsVisiblity();
    }

    private void initUI() {
        setContentView(R.layout.videoplayback_list_activity);
//        btnPlaying = findViewById(R.id.btn_tab_list);
//        if(btnPlaying!=null){
//        	btnPlaying.setOnClickListener(this);
//        }
//        btnFolders = findViewById(R.id.btn_tab_folder);
//        if(btnFolders!=null){
//        	btnFolders.setOnClickListener(this);
//        }
        vLayoutTabs = findViewById(R.id.layout_tabs);
        vLayoutTabs.setVisibility(View.VISIBLE);
        vLayoutError = findViewById(R.id.layout_error);
        vLayoutError.setVisibility(View.GONE);
        vLayoutScan = findViewById(R.id.layout_scan);
        vLayoutScan.setVisibility(View.GONE);
        btnTabs[0]=findViewById(R.id.btn_tab_sd1);
        btnTabs[1]=findViewById(R.id.btn_tab_sd2);
        btnTabs[2]=findViewById(R.id.btn_tab_usb1);
        btnTabs[3]=findViewById(R.id.btn_tab_usb2);
        btnTabs[4]=findViewById(R.id.btn_tab_usb3);
        btnTabs[5]=findViewById(R.id.btn_tab_usb4);
        btnTabs[6]=findViewById(R.id.btn_tab_usb5);
        btnTabs[7]=findViewById(R.id.btn_tab_internal);
        for(View v:btnTabs){
        	v.setOnClickListener(this);
        	v.setOnTouchListener(this);
	   	 }
    }
    
    private void updateTabsVisiblity(){
    	int i=0;
    	 ArrayList<String> devices =  fileListManager.getAvailibleDevices();
    	 for(View v:btnTabs){
    		 v.setVisibility(View.GONE);
    	 }
    	 int index = 0;
    	 int count = 0;
    	 for(String devPath:devices){
    		 index = fileListManager.getDevIndexByPath(devPath);
    		 if(index>=0){
    			 btnTabs[index].setVisibility(View.VISIBLE);
    			 count++;
    		 }
    	 }
    	 
    	 for(i=0;i<btnTabs.length;i++){
	   		 if(btnTabs[i].getVisibility()==View.VISIBLE && btnTabs[i].isSelected()){
	   			 break;
	   		 }
	   	 }
    	 if(i>=btnTabs.length){
    		 for(View v:btnTabs){
        		 if(v.getVisibility() == View.VISIBLE){
        			 onClick(v);
        			 break;
        		 }
        	 }
    	 }
    	 
    	 if(count>0){
    		 if(count>1){
    			 vLayoutTabs.setVisibility(View.VISIBLE);
    		 }    		 
    		 else{
    			 vLayoutTabs.setVisibility(View.GONE);
    		 }
	    	 vLayoutScan.setVisibility(View.GONE);
	    	 vLayoutError.setVisibility(View.GONE);
    	 }
    	 else{
    		 vLayoutTabs.setVisibility(View.GONE);
        	 vLayoutScan.setVisibility(View.GONE);
        	 vLayoutError.setVisibility(View.VISIBLE);
    	 }    	 
    }
    
    private void updateTabsSelected(View selectedView){
	   	 for(View v:btnTabs){
	   		 if(v.getId()!=selectedView.getId()){
	   			 v.setSelected(false);
	   		 }
	   		 else{
	   			v.setSelected(true);
	   		 }
	   	 }
   }

    @Override
    protected void onResume() {
        super.onResume();        
        SourceManager.acquireSource(sourceTocken);
        resume2PlayByAudioFocus();
        returnLater();
    }
    
    public void returnLater(){
		uiHandler.removeMessages(88);
		uiHandler.sendEmptyMessageDelayed(88, RETURN_TIME_OUT);
	}
    public void cancelReturnLater(){
		uiHandler.removeMessages(88);
	}
    
    @Override
    protected void onDestroy() {    	
    	uiHandler.removeCallbacksAndMessages(null);    	
    	unregisterReceiver(mPlayStatusReceiver);
    	fileListManager.release();
        super.onDestroy();
        SourceManager.unregisterSourceListener(sourceTocken);
		sourceTocken = null;
    }

    public static Object getSourceTocken() {
        return sourceTocken;
    }
    
    public void onBackPressed() {
//    	PlayStatus playStatus = VideoPlayerApp.GetInstance().getVideoPlayerContext().getPlayStatus();
//		if(playStatus==PlayStatus.STATUS_STOP || playStatus==PlayStatus.STATUS_NONE){
//			//not playing ,exit now,.
//			if (VideoPlaybackMainActivity.videoInstance != null) {
//				VideoPlaybackMainActivity.videoInstance.finish();
//				VideoPlaybackMainActivity.videoInstance = null;
//			}
//			finish();
//		}
//		else{
//			//it's playing , return to playing
//			Intent intent = new Intent(this, VideoPlaybackMainActivity.class);
//	        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//	        startActivity(intent);
//		}
    	for(int i=0;i<foldersFragment.length;i++){
    		if(foldersFragment[i].isVisible()){
        		if(foldersFragment[i].onBackPressed()){
        			return;
        		}
        	}
    	}    	
    	
    	exitApp();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
//		case R.id.btn_tab_list:{
//				btnFolders.setSelected(false);
//				btnPlaying.setSelected(true);
//				FragmentTransaction ft = mFragmentManager.beginTransaction();
//		        ft.replace(R.id.layout_content, mAllListFragment);
//		        ft.commit();
//			}			
//			break;
//		case R.id.btn_tab_folder:{
//				btnFolders.setSelected(true);
//				btnPlaying.setSelected(false);
//				FragmentTransaction ft = mFragmentManager.beginTransaction();
//		        ft.replace(R.id.layout_content, foldersFragment);
//		        ft.commit();
//			}			
//			break;
		case R.id.btn_tab_sd1:{
			updateTabsSelected(btnTabs[0]);
			switch2Fragment(foldersFragment[0]);
		}			
		break;
		case R.id.btn_tab_sd2:{
			updateTabsSelected(btnTabs[1]);
			switch2Fragment(foldersFragment[1]);
		}			
		break;
		case R.id.btn_tab_usb1:{
			updateTabsSelected(btnTabs[2]);
			switch2Fragment(foldersFragment[2]);
		}			
		break;
		case R.id.btn_tab_usb2:{
			updateTabsSelected(btnTabs[3]);
			switch2Fragment(foldersFragment[3]);
		}			
		break;
		case R.id.btn_tab_usb3:{
			updateTabsSelected(btnTabs[4]);
			switch2Fragment(foldersFragment[4]);
		}			
		break;
		case R.id.btn_tab_usb4:{
			updateTabsSelected(btnTabs[5]);
			switch2Fragment(foldersFragment[5]);
		}			
		break;
		case R.id.btn_tab_usb5:{
			updateTabsSelected(btnTabs[6]);
			switch2Fragment(foldersFragment[6]);
		}		
		break;
		case R.id.btn_tab_internal:{
			updateTabsSelected(btnTabs[7]);
			switch2Fragment(foldersFragment[7]);
		}	
		break;
		}
		
		
	}
	
	private Fragment mCurrFragment=null;
	private void switch2Fragment(Fragment to){  
        if(mCurrFragment!=to){  
        	try{
	            FragmentTransaction transaction=mFragmentManager.beginTransaction();  
	            if(!to.isAdded()){  
	            	if(mCurrFragment!=null){
	            		 transaction.hide(mCurrFragment);
	            	}
	                transaction.add(R.id.layout_content, to).commitAllowingStateLoss();  
	            }else{  
	            	if(mCurrFragment!=null){
	            		 transaction.hide(mCurrFragment);
	            	}
	                transaction.show(to).commitAllowingStateLoss();  
	            }  
        	}
            catch(Exception e){
            	e.printStackTrace();
    			finish();
    		}
        }  
        mCurrFragment=to;  
    } 
	
	private BroadcastReceiver mPlayStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Uri uri = intent.getData();
            String action = intent.getAction();
            // String path = uri.getPath();
            if (action.equals(VideoPlayerContext.ACTION_PLAY_FILE_CHANGED)) {
            	for(int i=0;i<foldersFragment.length;i++){
            		if(foldersFragment[i].isVisible()){
            			foldersFragment[i].updateCurPos();
                	}
            	}    	
            } else if (action.equals(ACTION_QB_POWEROFF) || action.equals(ACTION_QB_PREPOWEROFF)) {
            	exitApp();
            }
        }
    };
    
    public String getCurPath(){
    	String curPath = VideoPlayerApp.GetInstance().getVideoPlayerContext().getCurrentTrackPath();
        if(curPath==null){
        	//It's not playing, get the track played last time.
        	PlayListData.ListItem listItem = VideoPlayerApp.GetInstance().getVideoPlayerContext().getPlaying();
        	if(listItem!=null){
        		curPath = listItem.getPath();
        	}
        }
        return curPath;
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		returnLater();
		return false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		cancelReturnLater();
		super.onStop();
	}    
    
}
